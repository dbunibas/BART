package bart.model.detection;


public class RepairabilityStats {

    private double mean;
    private double confidenceInterval;

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getConfidenceInterval() {
        return confidenceInterval;
    }

    public void setConfidenceInterval(double confidenceInterval) {
        this.confidenceInterval = confidenceInterval;
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Mean: ").append(mean);
        sb.append(" Confidence interval: ");
        sb.append(confidenceInterval);
        sb.append(" [");
        sb.append(mean - confidenceInterval);
        sb.append(" - ");
        sb.append(mean + confidenceInterval);
        sb.append("]");
        return sb.toString();
    }
    
}
