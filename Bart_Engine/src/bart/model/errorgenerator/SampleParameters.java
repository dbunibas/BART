package bart.model.errorgenerator;

public class SampleParameters {

    private Double probability;
    private Integer limit;
    private Integer offset;

    public SampleParameters(Double probability, Integer limit, Integer offset) {
        this.probability = probability;
        this.limit = limit;
        this.offset = offset;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "SampleParameters (" + "probability=" + probability + ", limit=" + limit + ", offset=" + offset + ')';
    }

}
