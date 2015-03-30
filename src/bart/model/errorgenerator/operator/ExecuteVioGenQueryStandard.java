package bart.model.errorgenerator.operator;

import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.algebra.IAlgebraOperator;
import bart.model.algebra.operators.BuildAlgebraTree;
import bart.model.algebra.operators.ITupleIterator;
import bart.model.database.Tuple;
import bart.model.database.operators.IRunQuery;
import bart.model.detection.operator.EstimateRepairabilityAPriori;
import bart.model.errorgenerator.CellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.VioGenQuery;
import bart.model.errorgenerator.operator.valueselectors.INewValueSelectorStrategy;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteVioGenQueryStandard implements IVioGenQueryExecutor, IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(ExecuteVioGenQueryStandard.class);

    private GenerateChangesAndContexts changesGenerator = new GenerateChangesAndContexts();
    private EstimateRepairabilityAPriori aPrioriRepairabilityEstimator = new EstimateRepairabilityAPriori();
    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();
    private IRunQuery queryRunner;
    private INewValueSelectorStrategy valueSelector;

    public void execute(VioGenQuery vioGenQuery, CellChanges allCellChanges, EGTask task) {
        if (!task.getConfiguration().isGenerateAllChanges()) {
            throw new IllegalArgumentException("Stardard operator can be used only to generate all changes");
        }
        if (task.getConfiguration().isPrintLog()) System.out.println("--- VioGen Query: " + vioGenQuery.toShortString());
        intitializeOperators(task);
        if (DependencyUtility.hasOnlyVariableInequalities(vioGenQuery)) {
            logger.warn("Executing a vioGenQuery without equalities is slow.");
        }
        IAlgebraOperator operator = treeBuilder.buildTreeForPremise(vioGenQuery.getFormula(), task);
        vioGenQuery.setQuery(operator);
        if (logger.isDebugEnabled()) logger.debug("Operator\n" + operator.toString());
        IAlgebraOperator query = vioGenQuery.getQuery();
        if (logger.isDebugEnabled()) logger.debug("Executing " + query);
        if (task.getConfiguration().isPrintLog()) System.out.println("Executing " + vioGenQuery);
        if (task.getConfiguration().isDebug()) System.out.println("Query:\n" + query);
        ITupleIterator tupleIterator = queryRunner.run(query, task.getSource(), task.getTarget());
        if (logger.isDebugEnabled()) logger.debug(BartUtility.printIterator(tupleIterator));
        while (tupleIterator.hasNext()) {
            Tuple tuple = tupleIterator.next();
            generateChangeForTuple(vioGenQuery, tuple, allCellChanges, task);
        }
        tupleIterator.close();
    }

    private void generateChangeForTuple(VioGenQuery vioGenQuery, Tuple tuple, CellChanges allCellChanges, EGTask task) {
        List<CellChange> changesForTuple = changesGenerator.generateChangesForStandardTuple(vioGenQuery, tuple, allCellChanges, valueSelector, task);
        if (task.getConfiguration().isEstimateAPrioriRepairability()) {
            aPrioriRepairabilityEstimator.estimateRepairabilityFromGeneratingContext(changesForTuple, vioGenQuery);
        }
        changesGenerator.filterChanges(changesForTuple, task);
        changesGenerator.addChanges(changesForTuple, allCellChanges);
    }

    public void intitializeOperators(EGTask task) {
        queryRunner = OperatorFactory.getInstance().getQueryRunner(task);
        valueSelector = OperatorFactory.getInstance().getValueSelector(task);
    }

}
