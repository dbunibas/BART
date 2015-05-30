package bart.model.errorgenerator.operator;

import bart.BartConstants;
import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.database.AttributeRef;
import bart.model.database.TableAlias;
import bart.model.database.Tuple;
import bart.model.database.operators.IRunQuery;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void execute(VioGenQuery vioGenQuery, CellChanges allCellChanges, EGTask task) {
        if (task.getConfiguration().isPrintLog()) System.out.println("--- VioGen Query: " + vioGenQuery.toShortString());
        intitializeOperators(task);
        checkConditions(vioGenQuery, task);
        symmetricQueryBuilder.initializeQuery(vioGenQuery.getFormula().getFormulaWithAdornments(), task);
        int sampleSize = ExecuteVioGenQueryUtility.computeSampleSize(vioGenQuery, task);
        if (sampleSize == 0) {
            if (task.getConfiguration().isPrintLog()) System.out.println("No changes required");
            return;
        }
        if (!task.getConfiguration().isGenerateAllChanges() && task.getConfiguration().isPrintLog()) {
            System.out.println("Error percentage: " + vioGenQuery.getConfiguration().getPercentage());
            System.out.println(sampleSize + " changes required");
        }
        List<EquivalenceClassQuery> equivalenceClassQueries = vioGenQuery.getFormula().getFormulaWithAdornments().getEquivalenceClassQueries();
        Set<DiscardedTuplePair> discardedTuples = new HashSet<DiscardedTuplePair>();
        ExecuteVioGenQueryUtility.executeEquivalenceClassQuery(equivalenceClassQueries, queryRunner, task.getSource(), task.getTarget());
        int offset = computeOffset(vioGenQuery, task);
        int initialChanges = allCellChanges.getChanges().size();
        while (true) {
            List<EquivalenceClass> equivalenceClasses = equivalenceClassExtractor.getNextCommonEquivalenceClasses(equivalenceClassQueries);
            if (equivalenceClasses.isEmpty()) {
                break;
            }
            findVioGenCellsForEquivalenceClasses(vioGenQuery, equivalenceClasses, discardedTuples, allCellChanges, sampleSize, offset, initialChanges, task);
            if (ExecuteVioGenQueryUtility.checkIfFinished(allCellChanges, initialChanges, sampleSize)) {
                break;
            }
        }
        ExecuteVioGenQueryUtility.closeIterators(equivalenceClassQueries);
        if (!ExecuteVioGenQueryUtility.checkIfFinished(allCellChanges, initialChanges, sampleSize)) {
            if (logger.isInfoEnabled()) logger.info("After first iteration there are " + (sampleSize - ((allCellChanges.getChanges().size()) - initialChanges)) + " remaining changes to perform!");
            executeDiscardedPairs(vioGenQuery, allCellChanges, discardedTuples, initialChanges, sampleSize, task);
        }
        int executedChanges = (allCellChanges.getChanges().size()) - initialChanges;
        if (task.getConfiguration().isPrintLog()) System.out.println("Executed changes: " + executedChanges);
    }

    private void findVioGenCellsForEquivalenceClasses(VioGenQuery vioGenQuery, List<EquivalenceClass> equivalenceClasses, Set<DiscardedTuplePair> discardedTuples,
            CellChanges allCellChanges, int sampleSize, int tuplePairsToDiscard, int initialChanges, EGTask task) {
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
            Tuple firstTuple = equivalenceClassForIntersection.getTuples().get(i);
            if (usedTuples.contains(firstTuple)) {
                continue;
            }
            for (int j = i + 1; j < equivalenceClassForIntersection.getTuples().size(); j++) {
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
                changesGenerator.handleTuplePair(tuplePair.getFirstTuple(), tuplePair.getSecondTuple(), vioGenQuery, allCellChanges, usedTuples, valueSelector, task);
                if (ExecuteVioGenQueryUtility.checkIfFinished(allCellChanges, initialChanges, sampleSize)) {
                    return;
                }
            }
        }
    }

    private void executeDiscardedPairs(VioGenQuery vioGenQuery, CellChanges allCellChanges, Set<DiscardedTuplePair> discardedTuples, int initialChanges, int sampleSize, EGTask task) {
        Iterator<DiscardedTuplePair> it = discardedTuples.iterator();
        Set<Tuple> usedTuples = new HashSet<Tuple>();
        while (it.hasNext()) {
            DiscardedTuplePair discardedTuplePair = it.next();
            TuplePair tuplePair = discardedTuplePair.getTuplePair();
            EquivalenceClass equivalenceClass = discardedTuplePair.getEquivalenceClass();
            if (usedTuples.contains(tuplePair.getFirstTuple()) || usedTuples.contains(tuplePair.getSecondTuple())) {
                continue;
            }
            changesGenerator.handleTuplePair(tuplePair.getFirstTuple(), tuplePair.getSecondTuple(), vioGenQuery, allCellChanges, usedTuples, valueSelector, task);
            if (ExecuteVioGenQueryUtility.checkIfFinished(allCellChanges, initialChanges, sampleSize)) {
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
