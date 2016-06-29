package bart.model.errorgenerator.operator;

import bart.IInitializableOperator;
import bart.model.EGTask;
import bart.model.dependency.Dependency;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ICellChange;
import bart.model.errorgenerator.OrderingAttribute;
import bart.model.errorgenerator.RandomCellChange;
import bart.model.errorgenerator.operator.valueselectors.INewValueSelectorStrategy;
import bart.model.errorgenerator.operator.valueselectors.PartialOrderValueSelector;
import bart.utility.BartDBMSUtility;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;

public class ExecutePartialOrderErrors implements IInitializableOperator {
    
    private INewValueSelectorStrategy valueSelector;
    private static final Logger logger = LoggerFactory.getLogger(ExecutePartialOrderErrors.class);
    
    public void intitializeOperators(EGTask task) {
        valueSelector = new PartialOrderValueSelector(task);
    }
    
    public CellChanges execute(EGTask task, CellChanges generatedChanges, Dependency dc) {
        intitializeOperators(task);
        if (logger.isDebugEnabled()) logger.debug("Executing PartialOrderErrors");
        Map<String, OrderingAttribute> vioGenOrderingAttributes = task.getConfiguration().getVioGenOrderingAttributes();
        OrderingAttribute orderingAttribute = vioGenOrderingAttributes.get(dc.getId());
        if (logger.isDebugEnabled()) logger.debug("Dependency: " + dc.toString());
        if (logger.isDebugEnabled()) logger.debug("OrderingAttribute: " + orderingAttribute);
        Set<ICellChange> changes = generatedChanges.getChanges();
        CellChanges cellChanges = new CellChanges();
        if (logger.isDebugEnabled()) logger.debug("Changes SIZE: " + changes.size());
        for (ICellChange change : changes) {
//            if (change.getViolatedDependencies().contains(dc.getId())) {
                TupleOID tupleOID = change.getCell().getTupleOID();
                if (logger.isDebugEnabled()) logger.debug("Change: "+ change.toLongString());
                if (logger.isDebugEnabled()) logger.debug("Cell: "+ change.getCell().toStringWithOIDAndAlias());
                String tableName = change.getCell().getAttributeRef().getTableName();
                Tuple tuple = BartDBMSUtility.getTuple(task.getTarget(), tupleOID.getNumericalValue(), tableName);
                if (logger.isDebugEnabled()) logger.debug("Tuple: "+ tuple);
                AttributeRef attributeRef = new AttributeRef(tableName, orderingAttribute.getAttribute());
                Cell cell = tuple.getCell(attributeRef);
                cell.setTupleOid(tupleOID);
                RandomCellChange cellChange = new RandomCellChange(cell);
                cellChange.setNewValue(valueSelector.generateNewValuesForContext(cell, cellChange, task));
                cellChange.setExport(false);
                if (logger.isDebugEnabled()) logger.debug("Change value: " + cell.getValue() + " to " + cellChange.getNewValue());
                cellChanges.addChange(cellChange);
//            }
        }
        return cellChanges;
    }
    
}
