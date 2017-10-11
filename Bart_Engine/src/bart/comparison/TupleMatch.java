package bart.comparison;

import speedy.model.database.TupleWithTable;

public class TupleMatch {

    private TupleWithTable leftTuple;
    private TupleWithTable rightTuple;
    private ValueMappings valueMappings;
    private Double scoreEstimate;

    public TupleMatch(TupleWithTable leftTuple, TupleWithTable rightTuple, ValueMapping leftValueMapping) {
        this.leftTuple = leftTuple;
        this.rightTuple = rightTuple;
        this.valueMappings = new ValueMappings(leftValueMapping);
    }

    public TupleMatch(TupleWithTable leftTuple, TupleWithTable rightTuple, ValueMappings valueMappings, double scoreEstimate) {
        this.leftTuple = leftTuple;
        this.rightTuple = rightTuple;
        this.valueMappings = valueMappings;
        this.scoreEstimate = scoreEstimate;
    }

    public TupleWithTable getLeftTuple() {
        return leftTuple;
    }

    public TupleWithTable getRightTuple() {
        return rightTuple;
    }

    public ValueMapping getLeftToRightValueMapping() {
        return valueMappings.getLeftToRightValueMapping();
    }

    public ValueMapping getRightToLeftValueMapping() {
        return valueMappings.getRightToLeftValueMapping();
    }

    public ValueMappings getValueMappings() {
        return valueMappings;
    }

    public void setValueMappings(ValueMappings valueMappings) {
        this.valueMappings = valueMappings;
    }

    public Double getScoreEstimate() {
        return scoreEstimate;
    }

    @Override
    public String toString() {
        return "Match: " + (scoreEstimate != null ? " (score estimate: " + scoreEstimate + ") " : "") + "" + leftTuple.toString() + " <-> " + rightTuple.toString()
                + (this.getLeftToRightValueMapping() != null && !this.getLeftToRightValueMapping().isEmpty() ? "\nLeft to right value mapping:" + this.getLeftToRightValueMapping() : "")
                + (this.getRightToLeftValueMapping() != null && !this.getRightToLeftValueMapping().isEmpty() ? "\nRight to left value mapping:" + this.getRightToLeftValueMapping() : "");
    }
}
