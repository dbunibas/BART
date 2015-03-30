package bart.model.errorgenerator.operator.valueselectors;

import bart.BartConstants;
import bart.model.EGTask;
import bart.model.database.ConstantValue;
import bart.model.database.IValue;
import bart.model.errorgenerator.CellChange;
import bart.model.errorgenerator.ValueConstraint;
import bart.model.errorgenerator.VioGenCell;
import bart.model.errorgenerator.operator.IntersectValueConstraints;
import bart.persistence.Types;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicValueSelector implements INewValueSelectorStrategy {

    private static Logger logger = LoggerFactory.getLogger(BasicValueSelector.class);
    private IntersectValueConstraints valueIntersector = new IntersectValueConstraints();
//    private double doubleErrorRange = 99999;
    private int doubleErrorRange = 100;

    public IValue generateNewValuesForContext(VioGenCell vioGenCell, CellChange change, EGTask task) {
        if (logger.isDebugEnabled()) logger.debug("Generating new value for vioGenCell " + vioGenCell.toString());
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
        if (whiteValueConstraint.isStarConstraint()) {
            if (logger.isDebugEnabled()) logger.debug("Generating new value for star value");
            return generateNewValueForStar(whiteValueConstraint, vioGenCell);
        } else if (!whiteValueConstraint.isNumeric()) {
            if (logger.isDebugEnabled()) logger.debug("Generating new value for whitelist " + change.getWhiteList());
            return new ConstantValue(whiteValueConstraint.toString());
        } else {
            if (logger.isDebugEnabled()) logger.debug("Generating new value for blacklist " + change.getBlackList());
            return generateNumericalValue(whiteValueConstraint, change.getBlackList());
        }
    }

    private IValue generateNewValueForStar(ValueConstraint valueConstraint, VioGenCell vioGenCell) {
        if (!valueConstraint.isNumeric()) {
            return new ConstantValue(vioGenCell.getCell().getValue() + "-*");
        }
        String oldValue = vioGenCell.getCell().getValue().toString();
        double oldDouble = Double.parseDouble(oldValue);
        int error = new Random().nextInt(doubleErrorRange);
        double newValue = oldDouble + error;
        if (valueConstraint.getType().equals(Types.INTEGER)) {
            return new ConstantValue((int) newValue);
        }
        return new ConstantValue(newValue);
    }

    private IValue generateNumericalValue(ValueConstraint interval, Set<ValueConstraint> blackList) {
        assert (interval.isNumeric());
        double rangeMin = Double.parseDouble(interval.getStart().toString());
        double range = interval.getRange();
        if (range == 0.0) {
            return new ConstantValue(rangeMin);
        }
        double step = (range > 1 ? 1 : range / 10);
        double newValue = rangeMin + step;
        while (interval.contains(new ConstantValue(newValue))) {
            if (!contains(newValue, blackList)) {
                return new ConstantValue(newValue);
            }
            newValue = newValue + step;
        }
        return new ConstantValue(newValue);
    }

    private boolean contains(double newValue, Set<ValueConstraint> blackList) {
        for (ValueConstraint valueConstraint : blackList) {
            if (valueConstraint.contains(new ConstantValue(newValue))) {
                return true;
            }
        }
        return false;
    }
}
