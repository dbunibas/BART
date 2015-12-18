package bart.model.errorgenerator.operator;

import bart.BartConstants;
import bart.exceptions.ErrorGeneratorException;
import speedy.model.database.ConstantValue;
import bart.model.errorgenerator.ValueConstraint;
import java.util.Set;

public class IntersectValueConstraints {

    public ValueConstraint intersect(Set<ValueConstraint> values) {
        assert (!values.isEmpty()) : "Unable to intersect an empty list";
        if (isStarValue(values)) {
            return values.iterator().next();
        }
        if (areNumerical(values)) {
            ValueConstraint numericalIntersection = intersectNumericalConstraints(values);
            return numericalIntersection;
        } else {
            if (values.size() > 1) {
                return null;
            } else {
                return values.iterator().next();
            }
        }
    }

    private static boolean areNumerical(Set<ValueConstraint> values) {
        int numericalConstraints = countNumericalConstraints(values);
        if (numericalConstraints == 0) {
            return false;
        } else if (numericalConstraints == values.size()) {
            return true;
        } else {
            throw new ErrorGeneratorException("Constraints of different types: " + values);
        }
    }

    private static int countNumericalConstraints(Set<ValueConstraint> values) {
        int count = 0;
        for (ValueConstraint value : values) {
            if (value.isNumeric()) {
                count++;
            }
        }
        return count;
    }

    private static ValueConstraint intersectNumericalConstraints(Set<ValueConstraint> values) {
        double end = Double.MAX_VALUE;
        double start = -Double.MAX_VALUE;
        for (ValueConstraint valueConstraint : values) {
            end = Math.min(end, Double.parseDouble(valueConstraint.getEnd().toString()));
            start = Math.max(start, Double.parseDouble(valueConstraint.getStart().toString()));
            if (end < start) {
                return null;
            }
        }
        String type = BartConstants.NUMERIC;
        if (areSameType(values)) {
            type = values.iterator().next().getType();
        }
        return new ValueConstraint(new ConstantValue(start), new ConstantValue(end), type);
    }

    private static boolean areSameType(Set<ValueConstraint> values) {
        String type = null;
        for (ValueConstraint value : values) {
            String currentType = value.getType();
            if (type == null) {
                type = currentType;
            } else if (!type.equals(currentType)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isStarValue(Set<ValueConstraint> values) {
        return values.size() == 1 && values.iterator().next().isStarConstraint();
    }

}
