package bart.model.errorgenerator.operator;

import bart.BartConstants;
import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.NumberOfChanges;
import speedy.model.database.AttributeRef;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.database.operators.IRunQuery;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaWithAdornments;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.EquivalenceClass;
import bart.model.errorgenerator.EquivalenceClassQuery;
import bart.model.errorgenerator.ISampleStrategy;
import bart.model.errorgenerator.SampleParameters;
import bart.model.errorgenerator.VioGenQuery;
import bart.model.errorgenerator.operator.valueselectors.INewValueSelectorStrategy;
import bart.utility.AlgebraUtility;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.utility.PrintUtility;

public class ExecuteVioGenQuerySymmetricMainMemoryRandom implements IVioGenQueryExecutor, IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(ExecuteVioGenQuerySymmetricMainMemoryRandom.class);

    private ExtractEquivalenceClasses equivalenceClassExtractor = new ExtractEquivalenceClasses();
    private ISampleStrategy sampleStrategy;
    private GenerateChangesAndContexts changesGenerator = new GenerateChangesAndContexts();
    private GenerateAlgebraTreeForSymmetricQuery symmetricQueryBuilder = new GenerateAlgebraTreeForSymmetricQuery();
    private IRunQuery queryRunner;
    private INewValueSelectorStrategy valueSelector;

    private void checkConditions(VioGenQuery vioGenQuery, EGTask task) throws IllegalArgumentException {
        if (!vioGenQuery.getFormula().isSymmetric()) {
            throw new IllegalArgumentException("VioGenQuery is not symmetric.\n" + vioGenQuery);
        }
        if (!vioGenQuery.getFormula().hasEqualityAdornments()) {
            throw new IllegalArgumentException("VioGenQuery with no equalities is not supported.\n" + vioGenQuery);
        }
        if (task.getConfiguration().isGenerateAllChanges()) {
            throw new IllegalArgumentException("ExecuteVioGenQuerySymmetricRandom requires a random execution. Please set generateAllChanges to false.");
        }
    }

    @Override
    public int execute(VioGenQuery vioGenQuery, CellChanges allCellChanges, EGTask task) {
        if (task.getConfiguration().isPrintLog()) System.out.println("--- VioGen Query: " + vioGenQuery.toShortString());
        intitializeOperators(task);
        checkConditions(vioGenQuery, task);
        symmetricQueryBuilder.initializeQuery(vioGenQuery.getFormula().getFormulaWithAdornments(), task);
        long start = new Date().getTime();
        int sampleSize = ExecuteVioGenQueryUtility.computeSampleSize(vioGenQuery, task);
        if (sampleSize == 0) {
            if (task.getConfiguration().isPrintLog()) System.out.println("No changes required");
            return 0;
        }
        if (!task.getConfiguration().isGenerateAllChanges() && task.getConfiguration().isPrintLog()) {
            PrintUtility.printInformation("Error percentage: " + vioGenQuery.getConfiguration().getPercentage());
            PrintUtility.printInformation(sampleSize + " changes required");
        }
        List<EquivalenceClassQuery> equivalenceClassQueries = vioGenQuery.getFormula().getFormulaWithAdornments().getEquivalenceClassQueries();
        Set<DiscardedTuplePair> discardedTuples = new HashSet<DiscardedTuplePair>();
        ExecuteVioGenQueryUtility.executeEquivalenceClassQuery(equivalenceClassQueries, queryRunner, task.getSource(), task.getTarget());
        int offset = computeOffset(vioGenQuery, task);
        NumberOfChanges numberOfChanges = new NumberOfChanges();
        while (true) {
            if (BartUtility.isTimeout(start, task)) {
                break;
            }
            List<EquivalenceClass> equivalenceClasses = equivalenceClassExtractor.getNextCommonEquivalenceClasses(equivalenceClassQueries);
            if (equivalenceClasses.isEmpty()) {
                break;
            }
            findVioGenCellsForEquivalenceClasses(vioGenQuery, equivalenceClasses, discardedTuples, allCellChanges, numberOfChanges, sampleSize, offset, start, task);
            if (ExecuteVioGenQueryUtility.checkIfFinished(numberOfChanges.getChanges(), sampleSize)) {
                break;
            }
        }
        ExecuteVioGenQueryUtility.closeIterators(equivalenceClassQueries);
        if (!ExecuteVioGenQueryUtility.checkIfFinished(numberOfChanges.getChanges(), sampleSize)) {
            if (logger.isInfoEnabled()) logger.info("After first iteration there are " + (sampleSize - numberOfChanges.getChanges()) + " remaining changes to perform!");
            executeDiscardedPairs(vioGenQuery, allCellChanges, numberOfChanges, discardedTuples, sampleSize, start, task);
        }
        if (task.getConfiguration().isPrintLog()) PrintUtility.printSuccess("Executed changes: " + numberOfChanges.getChanges());
        return numberOfChanges.getChanges();
    }

    private void findVioGenCellsForEquivalenceClasses(VioGenQuery vioGenQuery, List<EquivalenceClass> equivalenceClasses, Set<DiscardedTuplePair> discardedTuples,
            CellChanges allCellChanges, NumberOfChanges numberOfChanges, int sampleSize, int tuplePairsToDiscard, long start, EGTask task) {
        EquivalenceClass firstEquivalenceClass = equivalenceClasses.get(0);
        FormulaWithAdornments formulaWithAdornment = vioGenQuery.getFormula().getFormulaWithAdornments();
        List<AttributeRef> equalityAttributes = firstEquivalenceClass.getEqualityAttributes();
        Set<FormulaVariable> inequalityVariables = ExecuteVioGenQueryUtility.getInequalityVariables(formulaWithAdornment);
        EquivalenceClass equivalenceClassForIntersection;
        if (equivalenceClasses.size() == 1) {
            equivalenceClassForIntersection = firstEquivalenceClass;
        } else {
            equivalenceClassForIntersection = new EquivalenceClass(equalityAttributes);
            equivalenceClassForIntersection.addAllTuple(ExecuteVioGenQueryUtility.intersectTuples(equivalenceClasses));
            if (logger.isDebugEnabled()) logger.debug("tuples after intersection: " + equivalenceClassForIntersection.getTuples().size());
        }
        if (logger.isDebugEnabled()) logger.debug("Building context for equivalence class...");
        Set<Tuple> usedTuples = new HashSet<Tuple>();
        for (int i = 0; i < equivalenceClassForIntersection.getTuples().size() - 1; i++) {
            if (BartUtility.isTimeout(start, task)) {
                logger.warn("Timeout for vioGenQuery " + vioGenQuery);
                discardedTuples.clear();
                return;
            }
            Tuple firstTuple = equivalenceClassForIntersection.getTuples().get(i);
            if (usedTuples.contains(firstTuple)) {
                continue;
            }
            for (int j = i + 1; j < equivalenceClassForIntersection.getTuples().size(); j++) {
                if (BartUtility.isTimeout(start, task)) {
                    logger.warn("Timeout for vioGenQuery " + vioGenQuery);
                    discardedTuples.clear();
                    return;
                }
                Tuple secondTuple = equivalenceClassForIntersection.getTuples().get(j);
                if (usedTuples.contains(secondTuple)) {
                    continue;
                }
                TuplePair tuplePair = new TuplePair(firstTuple, secondTuple);
//                boolean verified = (inequalityVariables.size() <= 1) || ExecuteVioGenQueryUtility.verifyInequalitiesOnTuplePair(inequalityVariables, tuplePair, equivalenceClassForIntersection);
                boolean verified = (inequalityVariables.size() <= 1) || AlgebraUtility.verifyComparisonsOnTuplePair(firstTuple, secondTuple, vioGenQuery.getFormula(), task);
                if (!verified) {
                    continue;
                }
                if (discardedTuples.size() < tuplePairsToDiscard) {
                    discardedTuples.add(new DiscardedTuplePair(tuplePair, equivalenceClassForIntersection));
                    continue;
                }
                if (!BartUtility.pickRandom(vioGenQuery.getConfiguration().getProbabilityFactorForSymmetricQueries())) {
                    discardedTuples.add(new DiscardedTuplePair(tuplePair, equivalenceClassForIntersection));
                    continue;
                }
                changesGenerator.handleTuplePair(tuplePair.getFirstTuple(), tuplePair.getSecondTuple(), vioGenQuery, allCellChanges, numberOfChanges, usedTuples, valueSelector, task);
                if (ExecuteVioGenQueryUtility.checkIfFinished(numberOfChanges.getChanges(), sampleSize)) {
                    return;
                }
            }
        }
    }

    private void executeDiscardedPairs(VioGenQuery vioGenQuery, CellChanges allCellChanges, NumberOfChanges numberOfChanges, Set<DiscardedTuplePair> discardedTuples, int sampleSize, long start, EGTask task) {
        Iterator<DiscardedTuplePair> it = discardedTuples.iterator();
        Set<Tuple> usedTuples = new HashSet<Tuple>();
        while (it.hasNext()) {
            if (BartUtility.isTimeout(start, task)) {
                logger.warn("Timeout for vioGenQuery " + vioGenQuery);
                break;
            }
            DiscardedTuplePair discardedTuplePair = it.next();
            TuplePair tuplePair = discardedTuplePair.getTuplePair();
            if (usedTuples.contains(tuplePair.getFirstTuple()) || usedTuples.contains(tuplePair.getSecondTuple())) {
                continue;
            }
            changesGenerator.handleTuplePair(tuplePair.getFirstTuple(), tuplePair.getSecondTuple(), vioGenQuery, allCellChanges, numberOfChanges, usedTuples, valueSelector, task);
            if (ExecuteVioGenQueryUtility.checkIfFinished(numberOfChanges.getChanges(), sampleSize)) {
                return;
            }
        }
    }

    /////////////////////////////
    private int computeOffset(VioGenQuery vioGenQuery, EGTask task) {
        if (!vioGenQuery.getConfiguration().isUseOffsetInSymmetricQueries()) {
            return 0;
        }
        int sampleSize = ExecuteVioGenQueryUtility.computeSampleSize(vioGenQuery, task);
        Set<TableAlias> tableInFormula = DependencyUtility.extractTableAliasInFormula(vioGenQuery.getFormula());
        SampleParameters sampleParameters = sampleStrategy.computeParameters(vioGenQuery.getQuery(), tableInFormula, BartConstants.SYMMETRIC_QUERY_TYPE, sampleSize, vioGenQuery.getConfiguration(), task);
        int offset = sampleParameters.getOffset();
        if (logger.isInfoEnabled()) logger.info("Offset: " + offset);
        return offset;
    }

    public void intitializeOperators(EGTask task) {
        queryRunner = OperatorFactory.getInstance().getQueryRunner(task);
        valueSelector = OperatorFactory.getInstance().getValueSelector(task);
        String strategy = task.getConfiguration().getSampleStrategyForSymmetricQueries();
        sampleStrategy = OperatorFactory.getInstance().getSampleStrategy(strategy, task);
    }

}

class DiscardedTuplePair {

    private TuplePair tuplePair;
    private EquivalenceClass equivalenceClass;

    public DiscardedTuplePair(TuplePair tuplePair, EquivalenceClass equivalenceClass) {
        this.tuplePair = tuplePair;
        this.equivalenceClass = equivalenceClass;
    }

    public TuplePair getTuplePair() {
        return tuplePair;
    }

    public EquivalenceClass getEquivalenceClass() {
        return equivalenceClass;
    }

}
