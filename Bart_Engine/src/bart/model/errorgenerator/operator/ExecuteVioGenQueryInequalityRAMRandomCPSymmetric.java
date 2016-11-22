package bart.model.errorgenerator.operator;

import bart.BartConstants;
import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.NumberOfChanges;
import bart.model.VioGenQueryConfiguration;
import bart.model.algebra.operators.BuildAlgebraTree;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Limit;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.database.operators.IRunQuery;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaWithAdornments;
import bart.model.dependency.PositiveFormula;
import bart.model.errorgenerator.CellChanges;
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

public class ExecuteVioGenQueryInequalityRAMRandomCPSymmetric implements IVioGenQueryExecutor, IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(ExecuteVioGenQueryInequalityRAMRandomCPSymmetric.class);

    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();
    private GenerateChangesAndContexts changesGenerator = new GenerateChangesAndContexts();
    private ISampleStrategy sampleStrategy;
    private IRunQuery queryRunner;
    private INewValueSelectorStrategy valueSelector;

    private void checkConditions(VioGenQuery vioGenQuery, EGTask task) {
        if (!vioGenQuery.getFormula().isSymmetric()) {
            throw new IllegalArgumentException("VioGenQuery is not symmetric.\n" + vioGenQuery);
        }
//        if (DependencyUtility.hasVariableEqualities(vioGenQuery.getFormula())) {
//            throw new IllegalArgumentException("VioGenQuery with equalities is not supported.\n" + vioGenQuery);
//        }
        if (task.getConfiguration().isGenerateAllChanges()) {
            throw new IllegalArgumentException("ExecuteVioGenQueryInequalitySymmetricMainMemoryRandom requires a random execution. Please set generateAllChanges to false.");
        }
    }

    @Override
    public int execute(VioGenQuery vioGenQuery, CellChanges allCellChanges, EGTask task) {
        if (task.getConfiguration().isPrintLog()) System.out.println("--- VioGen Query: " + vioGenQuery.toShortString());
        intitializeOperators(task);
        checkConditions(vioGenQuery, task);
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
        Set<TuplePair> discardedTuples = new HashSet<TuplePair>();
        NumberOfChanges numberOfChanges = new NumberOfChanges();
        Set<Tuple> usedTuples = new HashSet<Tuple>();
        findVioGenCells(vioGenQuery, allCellChanges, numberOfChanges, sampleSize, discardedTuples, usedTuples, start, task);
        if (!ExecuteVioGenQueryUtility.checkIfFinished(numberOfChanges.getChanges(), sampleSize)) {
            if (logger.isInfoEnabled()) logger.info("After first iteration there are " + (sampleSize - numberOfChanges.getChanges()) + " remaining changes to perform!");
            executeDiscardedPairs(vioGenQuery, allCellChanges, discardedTuples, usedTuples, numberOfChanges, sampleSize, start, task);
        }
        if (task.getConfiguration().isPrintLog()) PrintUtility.printSuccess("Executed changes: " + numberOfChanges.getChanges());
        return numberOfChanges.getChanges();
    }

    private void findVioGenCells(VioGenQuery vioGenQuery, CellChanges allCellChanges, NumberOfChanges numberOfChanges, int sampleSize, Set<TuplePair> discardedTuples, Set<Tuple> usedTuples, long start, EGTask task) {
        int offset = computeOffset(vioGenQuery, task);
        Set<FormulaVariable> inequalityVariables = getInequalityVariables(vioGenQuery.getFormula().getFormulaWithAdornments());
        if (logger.isInfoEnabled()) logger.info("Inequality Variables: " + inequalityVariables);
        IAlgebraOperator operator = getQuery(vioGenQuery, offset, task);
        List<Tuple> extractedTuples = ExecuteVioGenQueryUtility.materializeTuples(operator, queryRunner, task);
        for (int i = 0; i < extractedTuples.size() - 1; i++) {
            if (BartUtility.isTimeout(start, task)) {
                logger.warn("Timeout for vioGenQuery " + vioGenQuery);
                discardedTuples.clear();
                return;
            }
            Tuple firstTuple = extractedTuples.get(i);
            if (usedTuples.contains(firstTuple)) {
                continue;
            }
            for (int j = i + 1; j < extractedTuples.size(); j++) {
                if (BartUtility.isTimeout(start, task)) {
                    logger.warn("Timeout for vioGenQuery " + vioGenQuery);
                    discardedTuples.clear();
                    return;
                }
                Tuple secondTuple = extractedTuples.get(j);
                if (usedTuples.contains(firstTuple) || usedTuples.contains(secondTuple)) {
                    continue;
                }
                TuplePair tuplePair = new TuplePair(firstTuple, secondTuple);
                if (discardedTuples.size() < offset) {
                    discardedTuples.add(tuplePair);
                    continue;
                }
                if (!BartUtility.pickRandom(vioGenQuery.getConfiguration().getProbabilityFactorForInequalityQueries())) {
                    discardedTuples.add(tuplePair);
                    continue;
                }
                boolean verified = AlgebraUtility.verifyComparisonsOnTuplePair(tuplePair.getFirstTuple(), tuplePair.getSecondTuple(), vioGenQuery.getFormula(), task);
                if (!verified) {
                    continue;
                }
                changesGenerator.handleTuplePair(tuplePair.getFirstTuple(), tuplePair.getSecondTuple(), vioGenQuery, allCellChanges, numberOfChanges, usedTuples, valueSelector, task);
                if (ExecuteVioGenQueryUtility.checkIfFinished(numberOfChanges.getChanges(), sampleSize)) {
                    return;
                }
            }
        }
    }

    private int computeOffset(VioGenQuery vioGenQuery, EGTask task) {
        if (!vioGenQuery.getConfiguration().isUseOffsetInInequalityQueries()) {
            return 0;
        }
        int sampleSize = ExecuteVioGenQueryUtility.computeSampleSize(vioGenQuery, task);
        Set<TableAlias> tableInFormula = DependencyUtility.extractTableAliasInFormula(vioGenQuery.getFormula());
        SampleParameters sampleParameters = sampleStrategy.computeParameters(vioGenQuery.getQuery(), tableInFormula, BartConstants.INEQUALITY_QUERY_TYPE, sampleSize, vioGenQuery.getConfiguration(), task);
        int offset = sampleParameters.getOffset();
        if (logger.isInfoEnabled()) logger.info("Offset: " + offset);
        return offset;
    }

    private IAlgebraOperator getQuery(VioGenQuery vioGenQuery, int offset, EGTask task) {
        FormulaWithAdornments formulaWithAdornment = vioGenQuery.getFormula().getFormulaWithAdornments();
        PositiveFormula symmetricFormula = formulaWithAdornment.getFormula().getPositiveFormula();
        if (logger.isInfoEnabled()) logger.info("Symmetric Formula\n\t" + symmetricFormula);
        IAlgebraOperator operator = treeBuilder.buildTreeForPremise(symmetricFormula, task);
        VioGenQueryConfiguration queryConfiguration = vioGenQuery.getConfiguration();
        if (queryConfiguration.isUseLimitInInequalityQueries()) {
            int sampleSize = ExecuteVioGenQueryUtility.computeSampleSize(vioGenQuery, task);
            Set<TableAlias> tableInFormula = DependencyUtility.extractTableAliasInFormula(vioGenQuery.getFormula());
            SampleParameters sampleParameters = sampleStrategy.computeParameters(operator, tableInFormula, BartConstants.INEQUALITY_QUERY_TYPE, sampleSize, queryConfiguration, task);
            int limitValue = sampleParameters.getLimit() + offset;
            Limit limit = new Limit(limitValue);
            limit.addChild(operator);
            operator = limit;
        }
        if (logger.isInfoEnabled()) logger.info("Operator:\n" + operator);
        return operator;
    }

    private Set<FormulaVariable> getInequalityVariables(FormulaWithAdornments formulaWithAdornment) {
        Set<FormulaVariable> result = new HashSet<FormulaVariable>();
        for (FormulaVariable formulaVariable : formulaWithAdornment.getAdornments().keySet()) {
            if (formulaWithAdornment.getAdornments().get(formulaVariable).equals(BartConstants.NOT_EQUAL)) {
                result.add(formulaVariable);
            }
        }
        return result;
    }

    private void executeDiscardedPairs(VioGenQuery vioGenQuery, CellChanges allCellChanges, Set<TuplePair> discardedTuples, Set<Tuple> usedTuples, NumberOfChanges numberOfChanges, int sampleSize, long start, EGTask task) {
        Iterator<TuplePair> it = discardedTuples.iterator();
        while (it.hasNext()) {
            if (BartUtility.isTimeout(start, task)) {
                logger.warn("Timeout for vioGenQuery " + vioGenQuery);
                return;
            }
            TuplePair tuplePair = it.next();
            if (usedTuples.contains(tuplePair.getFirstTuple()) || usedTuples.contains(tuplePair.getSecondTuple())) {
                continue;
            }
            boolean verified = AlgebraUtility.verifyComparisonsOnTuplePair(tuplePair.getFirstTuple(), tuplePair.getSecondTuple(), vioGenQuery.getFormula(), task);
            if (!verified) {
                continue;
            }
            changesGenerator.handleTuplePair(tuplePair.getFirstTuple(), tuplePair.getSecondTuple(), vioGenQuery, allCellChanges, numberOfChanges, usedTuples, valueSelector, task);
            if (ExecuteVioGenQueryUtility.checkIfFinished(numberOfChanges.getChanges(), sampleSize)) {
                return;
            }
        }
    }

    public void intitializeOperators(EGTask task) {
        queryRunner = OperatorFactory.getInstance().getQueryRunner(task);
        valueSelector = OperatorFactory.getInstance().getValueSelector(task);
        String strategy = task.getConfiguration().getSampleStrategyForInequalityQueries();
        sampleStrategy = OperatorFactory.getInstance().getSampleStrategy(strategy, task);
    }

}
