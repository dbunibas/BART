package bart.comparison;

import java.text.DecimalFormat;

public class TableSimilarity {

    private final static DecimalFormat df = new DecimalFormat("#0.00");
    private double similarity;
    private double precision;
    private double recall;

    public TableSimilarity(double similarity, double precision, double recall) {
        this.similarity = similarity;
        this.precision = precision;
        this.recall = recall;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public double getFMeasure() {
        if (precision == 0.0 && recall == 0.0) {
            return 0.0;
        }
        return (2 * precision * recall) / (precision + recall);
    }

    @Override
    public String toString() {
        return "Similarity: " + df.format(similarity) + " Pr: " + df.format(precision) + " Rc: " + df.format(recall) + " FM: " + df.format(getFMeasure());
    }

}
