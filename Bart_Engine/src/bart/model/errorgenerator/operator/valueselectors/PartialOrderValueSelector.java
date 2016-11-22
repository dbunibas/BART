package bart.model.errorgenerator.operator.valueselectors;

import bart.model.EGTask;
import bart.model.errorgenerator.ICellChange;
import bart.model.errorgenerator.OrderingAttribute;
import bart.persistence.Types;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;
import speedy.model.database.dbms.DBMSDB;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import speedy.utility.DBMSUtility;
import speedy.utility.SpeedyUtility;

public class PartialOrderValueSelector implements INewValueSelectorStrategy {

    private static final Logger logger = LoggerFactory.getLogger(PartialOrderValueSelector.class);
    private Map<OrderingAttribute, IValue> cacheMax = new HashMap<OrderingAttribute, IValue>();
    private Map<OrderingAttribute, IValue> cacheMin = new HashMap<OrderingAttribute, IValue>();

    public PartialOrderValueSelector(EGTask task) {
        Map<String, OrderingAttribute> vioGenOrderingAttributes = task.getConfiguration().getVioGenOrderingAttributes();
        Set<String> dependencies = vioGenOrderingAttributes.keySet();
        if (!(task.getTarget() instanceof DBMSDB)) {
            logger.error("PartialOrderValueSelector works only form DBMSDB");
            throw new IllegalArgumentException("Not implemented yes");
        }
        AccessConfiguration accessConfiguration = ((DBMSDB) task.getTarget()).getAccessConfiguration();
        for (String dependencyId : dependencies) {
            OrderingAttribute orderingAttribute = vioGenOrderingAttributes.get(dependencyId);
            if (logger.isDebugEnabled()) logger.debug("Ordering attribute: " + orderingAttribute);
            if (orderingAttribute.isAsc()) {
                // popolate min
                IValue valueMin = getValue(orderingAttribute.getAttribute(), orderingAttribute.getTable(), accessConfiguration, "MIN");
                if (logger.isDebugEnabled()) logger.debug("ValueMin: " + valueMin);
                IValue generated = generateValue(valueMin, true);
                cacheMin.put(orderingAttribute, generated);
            } else {
                // popolate max
                IValue valueMax = getValue(orderingAttribute.getAttribute(), orderingAttribute.getTable(), accessConfiguration, "MAX");
                IValue generated = generateValue(valueMax, false);
                cacheMax.put(orderingAttribute, generated);
            }
        }
    }

    public IValue generateNewValuesForContext(Cell originalCell, ICellChange cellChange, EGTask task) {
        AttributeRef attributeRef = originalCell.getAttributeRef();
        Map<String, OrderingAttribute> vioGenOrderingAttributes = task.getConfiguration().getVioGenOrderingAttributes();
        OrderingAttribute oa = findOrderingAttribute(vioGenOrderingAttributes, attributeRef);
        if (oa != null) {
            if (oa.isAsc()) {
                return cacheMin.get(oa);
            } else {
                return cacheMax.get(oa);
            }
        }
        return null;
    }

    private IValue getValue(String attributeName, String tableName, AccessConfiguration accessConfiguration, String operation) {
        String query = "";
        try {
            Connection connection = QueryManager.getConnection(accessConfiguration);
            query = "SELECT " + operation + "(\"" + attributeName + "\") FROM " + DBMSUtility.getSchemaNameAndDot(accessConfiguration) + "\"" + tableName + "\"";
            if (logger.isDebugEnabled()) logger.debug("Query: " + query);
            ResultSet resultSet = QueryManager.executeQuery(query, connection, accessConfiguration);
            resultSet.next();
            Tuple tuple = DBMSUtility.createTuple(resultSet, tableName);
            if (logger.isDebugEnabled()) logger.debug("Tuple : " + tuple);
            IValue value = tuple.getCells().get(1).getValue();
            resultSet.close();
            connection.close();
            return value;
        } catch (SQLException e) {
            logger.error("Unable to execute query: " + query + ". " + e.getLocalizedMessage());
        }
        return null;
    }

    private IValue generateValue(IValue value, boolean min) {
        if (SpeedyUtility.isNumeric(value.getType())) {
            return generateNumericalValue(value, min);
        }
        if (SpeedyUtility.isDate(value.getType())) {
            return generateDateValue(value, min);
        }
        return generateStringValue(value, min);
    }

    private IValue generateNumericalValue(IValue value, boolean min) {
        if (logger.isDebugEnabled()) logger.debug("Generate Numerical Value");
        if (value.getType().equals(Types.DOUBLE)) {
            Double newValue = (Double) value.getPrimitiveValue();
            double doubleValue = newValue.doubleValue();
            if (min) {
                doubleValue--;
            } else {
                doubleValue++;
            }
            return new ConstantValue(doubleValue);
        }
        if (value.getType().equals(Types.INTEGER)) {
            Integer newValue = (Integer) value.getPrimitiveValue();
            int intValue = newValue.intValue();
            if (min) {
                intValue--;
            } else {
                intValue++;
            }
            return new ConstantValue(intValue);
        }
        if (value.getType().equals(Types.LONG)) {
            Long newValue = (Long) value.getPrimitiveValue();
            long longValue = newValue.longValue();
            if (min) {
                longValue--;
            } else {
                longValue++;
            }
            return new ConstantValue(longValue);
        }
        throw new IllegalArgumentException("Type not implemented. Value type: " + value.getType());
    }

    private IValue generateStringValue(IValue value, boolean min) {
        if (logger.isDebugEnabled()) logger.debug("Generate String Value");
        String primitiveValue = (String) value.getPrimitiveValue();
        if (min) {
//            String sValue = Character.MIN_VALUE + primitiveValue;
            String sValue = " " + primitiveValue;
            return new ConstantValue(sValue);
        } else {
//            String sValue = Character.MAX_VALUE + primitiveValue;
            String sValue = "z" + primitiveValue;
            return new ConstantValue(sValue);
        }
    }

    private IValue generateDateValue(IValue value, boolean min) {
        if (logger.isDebugEnabled()) logger.debug("Generate Date Value");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private OrderingAttribute findOrderingAttribute(Map<String, OrderingAttribute> vioGenOrderingAttributes, AttributeRef attributeRef) {
        for (String key : vioGenOrderingAttributes.keySet()) {
            OrderingAttribute oa = vioGenOrderingAttributes.get(key);
            if (oa.match(attributeRef)) return oa;
        }
        return null;
    }

}
