package bart.model.errorgenerator.operator.valueselectors;

import bart.model.EGTask;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import bart.model.errorgenerator.ICellChange;
import bart.model.errorgenerator.ValueConstraint;
import bart.model.errorgenerator.operator.IntersectValueConstraints;
import bart.persistence.Types;
import bart.utility.BartUtility;
import java.util.Set;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutlierValueSelector implements INewValueSelectorStrategy {

    private static Logger logger = LoggerFactory.getLogger(OutlierValueSelector.class);
    private IntersectValueConstraints valueIntersector = new IntersectValueConstraints();

    public IValue generateNewValuesForContext(Cell originalCell, ICellChange change, EGTask task) {
        if (logger.isDebugEnabled()) logger.debug("Generating new value for cell " + originalCell.toString());
        ValueConstraint whiteValueConstraint = valueIntersector.intersect(change.getWhiteList());
        if (whiteValueConstraint == null) {
            if (logger.isDebugEnabled()) logger.debug("Discarding context with incompatible white values...\n" + change);
            return null;
        }
        change.setWhiteListIntersection(whiteValueConstraint);
        if (logger.isInfoEnabled()) logger.info("Change: " + change.toLongString());
        if (change.getBlackList().contains(change.getWhiteListIntersection())) {
            if (logger.isDebugEnabled()) logger.debug("Discarding context with empty candidate values...\n" + change);
            return null;
        }
        if (whiteValueConstraint.isStarConstraint() || !whiteValueConstraint.isNumeric()) {
            if (logger.isDebugEnabled()) logger.debug("Discarding context with incompatible type values...\n" + change);
            return null;
        } else {
            if (logger.isDebugEnabled()) logger.debug("Generating new value for blacklist " + change.getBlackList());
            AttributeRef attributeRef = originalCell.getAttributeRef();
            Attribute attribute = BartUtility.getAttribute(attributeRef, task);
            String type = attribute.getType();
            return generateNumericalValue(whiteValueConstraint, change.getBlackList(), type);
        }
    }

    private IValue generateNumericalValue(ValueConstraint interval, Set<ValueConstraint> blackList, String type) {
        assert (interval.isNumeric());
        double rangeMin = Double.parseDouble(interval.getStart().toString());
        double range = interval.getRange();
        if (range == 0.0) {
            return getConstantValueOnType(rangeMin, type);
        }
        double newValue = getRandomInRange(rangeMin, rangeMin + range);
        while (interval.contains(getConstantValueOnType(newValue, type))) {
            if (!contains(newValue, blackList)) {
                return getConstantValueOnType(newValue, type);
            }
            newValue = getRandomInRange(rangeMin, rangeMin + range);
        }
        return getConstantValueOnType(newValue, type);
    }

    private boolean contains(double newValue, Set<ValueConstraint> blackList) {
        for (ValueConstraint valueConstraint : blackList) {
            if (valueConstraint.contains(new ConstantValue(newValue))) {
                return true;
            }
        }
        return false;
    }

    private double getRandomInRange(double min, double max) {
        RandomDataGenerator generator = new RandomDataGenerator();
        return generator.nextUniform(min, max);
    }

    private ConstantValue getConstantValueOnType(Object value, String type) {
        Object valueByType = null;
        Number numericalValue = (Number) value;
        if (type.equalsIgnoreCase(Types.INTEGER)) {
            valueByType = numericalValue.intValue();
        }
        if (type.equalsIgnoreCase(Types.DOUBLE)) {
            valueByType = numericalValue.doubleValue();
        }
        if (type.equalsIgnoreCase(Types.LONG)) {
            valueByType = numericalValue.longValue();
        }
        if (valueByType == null) {
            valueByType = numericalValue.doubleValue();
        }
        return new ConstantValue(valueByType);
    }

}
