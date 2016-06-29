package bart.model.errorgenerator.operator;

import bart.BartConstants;
import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.EGTaskConfiguration;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.RandomCellChange;
import bart.model.errorgenerator.ValueConstraint;
import bart.model.errorgenerator.operator.valueselectors.INewValueSelectorStrategy;
import bart.utility.BartUtility;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteRandomErrors implements IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(ExecuteRandomErrors.class);
    private INewValueSelectorStrategy valueSelector;

    public CellChanges execute(EGTask task, CellChanges detectableChanges) {
        intitializeOperators(task);
        CellChanges cellChanges = new CellChanges();
        if (!task.getConfiguration().isRandomErrors()) return null;
        if (task.getConfiguration().isDebug()) System.out.println("Start to dirty RANDOM");
        EGTaskConfiguration configuration = task.getConfiguration();
        Set<String> tablesForRandomErrors = configuration.getTablesForRandomErrors();
        for (String tableName : tablesForRandomErrors) {
            dirtyTable(tableName, task, cellChanges, detectableChanges);
        }
        if (task.getConfiguration().isPrintLog()) System.out.println("Random CellChanges generated: " + cellChanges.getChanges().size());
        return cellChanges;
    }

    private void dirtyTable(String tableName, EGTask task, CellChanges cellChanges, CellChanges detectableChanges) {
        if (task.getConfiguration().isDebug()) System.out.println("Start to add random errors into table: " + tableName);
        ITable table = task.getTarget().getTable(tableName);
        if (task.getConfiguration().isPrintLog()) System.out.println("Adding random errors into table: " + tableName + " - size: " + table.getSize());
        Set<String> attributesForRandomErrors = task.getConfiguration().getAttributesForRandomErrors(tableName);
        if (task.getConfiguration().isDebug()) System.out.println("Attributes to dirty: " + attributesForRandomErrors);
        checkAttributes(table, attributesForRandomErrors);
        double percentage = task.getConfiguration().getPercentageForRandomErrors(tableName);
        if (task.getConfiguration().isDebug()) System.out.println("Percentage to dirty for attributes: " + percentage);
        double percentageValue = percentage / 100.0;
        ITupleIterator it = table.getTupleIterator();
        while (it.hasNext()) {
            Tuple tuple = it.next();
            for (Cell cell : tuple.getCells()) {
                if (!attributesForRandomErrors.contains(cell.getAttribute())) {
                    continue;
                }
                if (task.getConfiguration().isAvoidInteractions() && detectableChanges.isViolationContextCell(cell)) {
                    continue;
                }
                if (BartUtility.pickRandom(percentageValue)) {
                    RandomCellChange cellChange = buildCellChange(cell, task);
                    cellChanges.addChange(cellChange);
                }
            }

        }
        it.close();
        if (task.getConfiguration().isDebug()) System.out.println("Random errors generated");
    }

    private RandomCellChange buildCellChange(Cell cell, EGTask task) {
        RandomCellChange cellChange = new RandomCellChange(cell);
        AttributeRef attributeRef = cell.getAttributeRef();
        Attribute attribute = BartUtility.getAttribute(attributeRef, task);
        String type = attribute.getType();
        ValueConstraint starValueConstraint = new ValueConstraint(new ConstantValue(BartConstants.STAR_VALUE), type);
        cellChange.addWhiteListValue(starValueConstraint);
        cellChange.addBlackListValue(new ValueConstraint(cell.getValue(), type));
        IValue newValue = valueSelector.generateNewValuesForContext(cell, cellChange, task);
        cellChange.setNewValue(newValue);
        if (task.getConfiguration().isDebug()) System.out.println("Changing Cell: " + cell + " *** From value: " + cell.getValue() + " to: " + newValue);
        return cellChange;
    }

    public void intitializeOperators(EGTask task) {
        valueSelector = OperatorFactory.getInstance().getValueSelector(task);
    }

    private void checkAttributes(ITable table, Set<String> attributesForRandomErrors) {
        for (String attributesForRandomError : attributesForRandomErrors) {
            Attribute attribute = table.getAttribute(attributesForRandomError); //Exception if it not exists
            if (logger.isDebugEnabled()) logger.debug("Attribute: " + attribute);
        }
    }

}
