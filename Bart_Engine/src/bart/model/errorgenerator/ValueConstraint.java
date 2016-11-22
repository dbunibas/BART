package bart.model.errorgenerator;

import bart.BartConstants;
import speedy.model.database.IValue;
import bart.utility.BartUtility;
import speedy.utility.SpeedyUtility;

public class ValueConstraint {

    private IValue start;
    private IValue end;
    private String type;
    private boolean inclusiveLeft;
    private boolean inclusiveRight;

    public ValueConstraint(IValue start, IValue end, String type) {
        this.start = start;
        this.end = end;
        this.type = type;
    }

    public ValueConstraint(IValue start, String type) {
        this.start = start;
        this.type = type;
        if (SpeedyUtility.isNumeric(type)) {
            this.end = start;
        }
    }

    public IValue getStart() {
        return start;
    }

    public IValue getEnd() {
        return end;
    }

    public String getType() {
        return type;
    }

    public boolean isInclusiveLeft() {
        return inclusiveLeft;
    }

    public void setInclusiveLeft(boolean inclusiveLeft) {
        this.inclusiveLeft = inclusiveLeft;
    }

    public boolean isInclusiveRight() {
        return inclusiveRight;
    }

    public void setInclusiveRight(boolean inclusiveRight) {
        this.inclusiveRight = inclusiveRight;
    }

    public boolean leftCompatible(IValue value) {
        if (!isNumeric()) {
            return value.equals(start);
        }
        double doubleValue = Double.parseDouble(value.toString());
        double startValue = Double.parseDouble(start.toString());
        if (inclusiveLeft) {
            return startValue <= doubleValue;
        } else {
            return startValue < doubleValue;
        }
    }

    public boolean rightCompatible(IValue value) {
        if (!isNumeric()) {
            return value.equals(end);
        }
        double doubleValue = Double.parseDouble(value.toString());
        double endValue = Double.parseDouble(end.toString());
        if (inclusiveRight) {
            return doubleValue <= endValue;
        } else {
            return doubleValue < endValue;
        }
    }

    public boolean contains(IValue value) {
        return leftCompatible(value) && rightCompatible(value);
    }

    public boolean isStarConstraint() {
        return BartConstants.STAR_VALUE.equals(this.start.toString());
    }

    public boolean isNumeric() {
        return SpeedyUtility.isNumeric(type);
    }

    public double getRange() {
        if (!isNumeric()) {
            return 0;
        }
        double starValue = Double.parseDouble(start.toString());
        double endValue = Double.parseDouble(end.toString());
        return endValue - starValue;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        return toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        if (end == null) {
            return this.start.toString();
        }
        return "["
                + (this.start == BartConstants.NEGATIVE_INFINITY ? "-INF" : this.start.toString()) + ", "
                + (this.end == BartConstants.POSITIVE_INFINITY ? "INF" : this.end.toString()) + "]";
    }

}
