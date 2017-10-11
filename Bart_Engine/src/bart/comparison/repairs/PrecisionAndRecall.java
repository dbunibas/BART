package bart.comparison.repairs;

public class PrecisionAndRecall implements Comparable<PrecisionAndRecall> {

    private double precision;
    private double recall;
    private double fMeasure;
    private String expectedInstance;
    private String generatedInstance;

    public PrecisionAndRecall(double precision, double recall, double measure) {
        this.precision = precision;
        this.recall = recall;
        this.fMeasure = measure;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getfMeasure() {
        return fMeasure;
    }

    public String getExpectedInstance() {
        return expectedInstance;
    }

    public void setExpectedInstance(String expectedInstance) {
        this.expectedInstance = expectedInstance;
    }

    public String getGeneratedInstance() {
        return generatedInstance;
    }

    public void setGeneratedInstance(String generatedInstance) {
        this.generatedInstance = generatedInstance;
    }

    @Override
    public String toString() {
        return precision + "\t" + recall + "\t" + fMeasure;
    }

    public int compareTo(PrecisionAndRecall o) {
        if (this.fMeasure < o.fMeasure) {
            return 1;
        }
        if (this.fMeasure > o.fMeasure) {
            return -1;
        }
        return 0;
    }
}
