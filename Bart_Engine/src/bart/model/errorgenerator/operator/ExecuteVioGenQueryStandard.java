package bart.model.errorgenerator.operator;

import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.NumberOfChanges;
import bart.model.algebra.operators.BuildAlgebraTree;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Tuple;
import speedy.model.database.operators.IRunQuery;
import bart.model.errorgenerator.VioGenQueryCellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.VioGenQuery;
import bart.model.errorgenerator.operator.valueselectors.INewValueSelectorStrategy;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteVioGenQueryStandard implements IVioGenQueryExecutor, IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(ExecuteVioGenQueryStandard.class);

    private GenerateChangesAndContexts changesGenerator = new GenerateChangesAndContexts();
    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();
    private IRunQuery queryRunner;
    private INewValueSelectorStrategy valueSelector;

    @Override
    public int execute(VioGenQuery vioGenQuery, CellChanges allCellChanges, EGTask task) {
        if (!task.getConfiguration().isGenerateAllChanges()) {
            throw new IllegalArgumentException("Stardard operator can be used only to generate all changes");
        }
        if (task.getConfiguration().isPrintLog()) System.out.println("--- VioGen Query: " + vioGenQuery.toShortString());
        intitializeOperators(task);
        if (DependencyUtility.hasOnlyVariableInequalities(vioGenQuery)) {
            logger.warn("Executing a vioGenQuery without equalities is slow.");
        }
        long start = new Date().getTime();
        IAlgebraOperator operator = treeBuilder.buildTreeForPremise(vioGenQuery.getFormula(), task);
        vioGenQuery.setQuery(operator);
        if (logger.isDebugEnabled()) logger.debug("Operator\n" + operator.toString());
        IAlgebraOperator query = vioGenQuery.getQuery();
        if (logger.isDebugEnabled()) logger.debug("Executing " + query);
        if (task.getConfiguration().isPrintLog()) System.out.println("Executing " + vioGenQuery);
        if (task.getConfiguration().isDebug()) System.out.println("Query:\n" + query);
        NumberOfChanges numberOfChanges = new NumberOfChanges();
        ITupleIterator tupleIterator = queryRunner.run(query, task.getSource(), task.getTarget());
        if (logger.isDebugEnabled()) logger.debug(BartUtility.printIterator(tupleIterator));
        while (tupleIterator.hasNext()) {
            if (BartUtility.isTimeout(start, task)) {
                logger.warn("Timeout for vioGenQuery " + vioGenQuery);
                break;
            }
            Tuple tuple = tupleIterator.next();
            List<VioGenQueryCellChange> changesForTuple = changesGenerator.generateChangesForStandardTuple(vioGenQuery, tuple, allCellChanges, valueSelector, task);
            if (!changesForTuple.isEmpty()) {
                changesGenerator.addChanges(changesForTuple, allCellChanges, numberOfChanges);
            }
        }
        tupleIterator.close();
        return numberOfChanges.getChanges();
    }

    public void intitializeOperators(EGTask task) {
        queryRunner = OperatorFactory.getInstance().getQueryRunner(task);
        valueSelector = OperatorFactory.getInstance().getValueSelector(task);
    }

}
