package bart.model.errorgenerator.operator;

import bart.BartConstants;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.algebra.IAlgebraOperator;
import bart.model.algebra.operators.BuildAlgebraTree;
import bart.model.algebra.operators.ITupleIterator;
import bart.model.database.TableAlias;
import bart.model.database.Tuple;
import bart.model.database.operators.IRunQuery;
import bart.model.errorgenerator.VioGenQueryCellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ISampleStrategy;
import bart.model.errorgenerator.SampleParameters;
import bart.model.errorgenerator.VioGenQuery;
import bart.model.errorgenerator.operator.valueselectors.INewValueSelectorStrategy;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteVioGenQueryStandardMainMemoryRandom implements IVioGenQueryExecutor {

    private static Logger logger = LoggerFactory.getLogger(ExecuteVioGenQueryStandardMainMemoryRandom.class);

    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();
    private GenerateChangesAndContexts changesGenerator = new GenerateChangesAndContexts();
    private IRunQuery queryRunner;
    private ISampleStrategy sampleStrategy;
    private INewValueSelectorStrategy valueSelector;

    public void execute(VioGenQuery vioGenQuery, CellChanges allCellChanges, EGTask task) {
        if (task.getConfiguration().isPrintLog()) System.out.println("--- VioGen Query: " + vioGenQuery.toShortString());
        intitializeOperators(task);
        if (task.getConfiguration().isGenerateAllChanges()) {
            throw new IllegalArgumentException("ExecuteVioGenQueryStandardMainMemoryRandom requires a random execution. Please set generateAllChanges to false.");
        }
        long start = new Date().getTime();
        Set<Tuple> discardedTuples = new HashSet<Tuple>();
        int offset = computeOffset(vioGenQuery, task);
        int sampleSize = ExecuteVioGenQueryUtility.computeSampleSize(vioGenQuery, task);
        if (sampleSize == 0) {
            if (task.getConfiguration().isPrintLog()) System.out.println("No changes required");
            return;
        }
        if (!task.getConfiguration().isGenerateAllChanges() && task.getConfiguration().isPrintLog()) {
            System.out.println("Error percentage: " + vioGenQuery.getConfiguration().getPercentage());
            System.out.println(sampleSize + " changes required");
        }
        int initialChanges = allCellChanges.getChanges().size();
        generateChanges(vioGenQuery, allCellChanges, sampleSize, offset, discardedTuples, start, task);
        if (!ExecuteVioGenQueryUtility.checkIfFinished(allCellChanges, initialChanges, sampleSize)) {
            if (logger.isInfoEnabled()) logger.info("After first iteration there are " + (sampleSize - ((allCellChanges.getChanges().size()) - initialChanges)) + " remaining changes to perform!");
            if (logger.isInfoEnabled()) logger.info("Discarded tuples: " + discardedTuples.size());
            executeDiscardedPairs(vioGenQuery, allCellChanges, discardedTuples, initialChanges, sampleSize, start, task);
        }
        int executedChanges = (allCellChanges.getChanges().size()) - initialChanges;
        if (task.getConfiguration().isPrintLog()) System.out.println("Executed changes: " + executedChanges);
    }

    private void generateChanges(VioGenQuery vioGenQuery, CellChanges allCellChanges, int sampleSize, int offset, Set<Tuple> discardedTuples, long start, EGTask task) {
        if (DependencyUtility.hasOnlyVariableInequalities(vioGenQuery)) {
            logger.warn("Executing a vioGenQuery without equalities is slow. Please use ExecuteVioGenQueryForInequalities operator");
        }
        IAlgebraOperator operator = treeBuilder.buildTreeForPremise(vioGenQuery.getFormula(), task);
        vioGenQuery.setQuery(operator);
        if (logger.isDebugEnabled()) logger.debug("Operator\n" + operator.toString());
        IAlgebraOperator query = vioGenQuery.getQuery();
        if (task.getConfiguration().isDebug()) System.out.println("Query:\n" + query);
        ITupleIterator tupleIterator = queryRunner.run(query, task.getSource(), task.getTarget());
        if (logger.isDebugEnabled()) logger.debug(BartUtility.printIterator(tupleIterator));
        int initialChanges = allCellChanges.getChanges().size();
        while (tupleIterator.hasNext()) {
            if (BartUtility.isTimeout(start, task)) {
                logger.warn("Timeout for vioGenQuery " + vioGenQuery);
                discardedTuples.clear();
                break;
            }
            Tuple tuple = tupleIterator.next();
            if (discardedTuples.size() < offset) {
                discardedTuples.add(tuple);
                continue;
            }
            if (!BartUtility.pickRandom(vioGenQuery.getConfiguration().getProbabilityFactorForStandardQueries())) {
                discardedTuples.add(tuple);
                continue;
            }
            generateChangeForTuple(vioGenQuery, tuple, allCellChanges, task);
            if (ExecuteVioGenQueryUtility.checkIfFinished(allCellChanges, initialChanges, sampleSize)) {
                break;
            }
        }
        tupleIterator.close();
    }

    private void executeDiscardedPairs(VioGenQuery vioGenQuery, CellChanges allCellChanges, Set<Tuple> discardedTuples, int initialChanges, int sampleSize, long start, EGTask task) {
        Iterator<Tuple> it = discardedTuples.iterator();
        while (it.hasNext()) {
            if (BartUtility.isTimeout(start, task)) {
                logger.warn("Timeout for vioGenQuery " + vioGenQuery);
                break;
            }
            Tuple discardedTuple = it.next();
            generateChangeForTuple(vioGenQuery, discardedTuple, allCellChanges, task);
            if (ExecuteVioGenQueryUtility.checkIfFinished(allCellChanges, initialChanges, sampleSize)) {
                return;
            }
        }
    }

    private void generateChangeForTuple(VioGenQuery vioGenQuery, Tuple tuple, CellChanges allCellChanges, EGTask task) {
        List<VioGenQueryCellChange> changesForTuple = changesGenerator.generateChangesForStandardTuple(vioGenQuery, tuple, allCellChanges, valueSelector, task);
        if (!changesForTuple.isEmpty()) {
            changesGenerator.addChanges(changesForTuple, allCellChanges);
        }
    }

    private int computeOffset(VioGenQuery vioGenQuery, EGTask task) {
        if (!vioGenQuery.getConfiguration().isUseOffsetInStandardQueries()) {
            return 0;
        }
        int sampleSize = ExecuteVioGenQueryUtility.computeSampleSize(vioGenQuery, task);
        Set<TableAlias> tableInFormula = DependencyUtility.extractTableAliasInFormula(vioGenQuery.getFormula());
        SampleParameters sampleParameters = sampleStrategy.computeParameters(vioGenQuery.getQuery(), tableInFormula, BartConstants.STANDARD_QUERY_TYPE, sampleSize, vioGenQuery.getConfiguration(), task);
        int offset = sampleParameters.getOffset();
        if (logger.isInfoEnabled()) logger.info("Offset: " + offset);
        return offset;
    }

    public void intitializeOperators(EGTask task) {
        queryRunner = OperatorFactory.getInstance().getQueryRunner(task);
        valueSelector = OperatorFactory.getInstance().getValueSelector(task);
        String strategy = task.getConfiguration().getSampleStrategyForStandardQueries();
        sampleStrategy = OperatorFactory.getInstance().getSampleStrategy(strategy, task);
    }

}
