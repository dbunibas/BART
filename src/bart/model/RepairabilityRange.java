package bart.model;

public class RepairabilityRange {

    private Double minValue = null;
    private Double maxValue = null;

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public String toString() {
        return "min value: " + minValue + " - max value: " + maxValue;
    }
}
