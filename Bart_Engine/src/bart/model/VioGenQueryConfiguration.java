package bart.model;

public class VioGenQueryConfiguration implements Cloneable {

//    private int sampleSize = 10;
    private double percentage;
    private double sizeFactorForStandardQueries = 1.5;
    private double sizeFactorForSymmetricQueries = 0.5;
    private double sizeFactorForInequalityQueries = 1;
    private double sizeFactorForSingleTupleQueries = 1;
    private double probabilityFactorForStandardQueries = 1;
    private double probabilityFactorForSymmetricQueries = 1;
    private double probabilityFactorForInequalityQueries = 1;
    private double probabilityFactorForSingleTupleQueries = 1;
    private double windowSizeFactorForStandardQueries = 3;
    private double windowSizeFactorForSymmetricQueries = 3;
    private double windowSizeFactorForInequalityQueries = 3;
    private double windowSizeFactorForSingleTupleQueries = 1;
    private double offsetFactorForStandardQueries = 0.3;
    private double offsetFactorForSymmetricQueries = 0.3;
    private double offsetFactorForInequalityQueries = 0.3;
    private double offsetFactorForSingleTupleQueries = 0.3;
    private boolean useLimitInStandardQueries = true;
    private boolean useLimitInSymmetricQueries = false;
    private boolean useLimitInInequalityQueries = true;
    private boolean useLimitInSingleTupleQueries = true;
    private boolean useOffsetInStandardQueries = true;
    private boolean useOffsetInSymmetricQueries = true;
    private boolean useOffsetInInequalityQueries = true;
    private boolean useOffsetInSingleTupleQueries = false;
    private String queryExecutor;

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

//    public int getSampleSize() {
//        return sampleSize;
//    }
//
//    public void setSampleSize(int sampleSize) {
//        this.sampleSize = sampleSize;
//    }
    public double getSizeFactorForStandardQueries() {
        return sizeFactorForStandardQueries;
    }

    public void setSizeFactorForStandardQueries(double sizeFactorForStandardQueries) {
        this.sizeFactorForStandardQueries = sizeFactorForStandardQueries;
    }

    public double getSizeFactorForSymmetricQueries() {
        return sizeFactorForSymmetricQueries;
    }

    public void setSizeFactorForSymmetricQueries(double sizeFactorForSymmetricQueries) {
        this.sizeFactorForSymmetricQueries = sizeFactorForSymmetricQueries;
    }

    public double getSizeFactorForInequalityQueries() {
        return sizeFactorForInequalityQueries;
    }

    public void setSizeFactorForInequalityQueries(double sizeFactorForInequalityQueries) {
        this.sizeFactorForInequalityQueries = sizeFactorForInequalityQueries;
    }

    public double getSizeFactorForSingleTupleQueries() {
        return sizeFactorForSingleTupleQueries;
    }

    public void setSizeFactorForSingleTupleQueries(double sizeFactorForSingleTupleQueries) {
        this.sizeFactorForSingleTupleQueries = sizeFactorForSingleTupleQueries;
    }

    public double getWindowSizeFactorForStandardQueries() {
        return windowSizeFactorForStandardQueries;
    }

    public void setWindowSizeFactorForStandardQueries(double windowSizeFactorForStandardQueries) {
        this.windowSizeFactorForStandardQueries = windowSizeFactorForStandardQueries;
    }

    public double getWindowSizeFactorForSymmetricQueries() {
        return windowSizeFactorForSymmetricQueries;
    }

    public void setWindowSizeFactorForSymmetricQueries(double windowSizeFactorForSymmetricQueries) {
        this.windowSizeFactorForSymmetricQueries = windowSizeFactorForSymmetricQueries;
    }

    public double getWindowSizeFactorForInequalityQueries() {
        return windowSizeFactorForInequalityQueries;
    }

    public void setWindowSizeFactorForInequalityQueries(double windowSizeFactorForInequalityQueries) {
        this.windowSizeFactorForInequalityQueries = windowSizeFactorForInequalityQueries;
    }

    public double getWindowSizeFactorForSingleTupleQueries() {
        return windowSizeFactorForSingleTupleQueries;
    }

    public void setWindowSizeFactorForSingleTupleQueries(double windowSizeFactorForSingleTupleQueries) {
        this.windowSizeFactorForSingleTupleQueries = windowSizeFactorForSingleTupleQueries;
    }

    public double getOffsetFactorForStandardQueries() {
        return offsetFactorForStandardQueries;
    }

    public void setOffsetFactorForStandardQueries(double offsetFactorForStandardQueries) {
        this.offsetFactorForStandardQueries = offsetFactorForStandardQueries;
    }

    public double getOffsetFactorForSymmetricQueries() {
        return offsetFactorForSymmetricQueries;
    }

    public void setOffsetFactorForSymmetricQueries(double offsetFactorForSymmetricQueries) {
        this.offsetFactorForSymmetricQueries = offsetFactorForSymmetricQueries;
    }

    public double getOffsetFactorForInequalityQueries() {
        return offsetFactorForInequalityQueries;
    }

    public void setOffsetFactorForInequalityQueries(double offsetFactorForInequalityQueries) {
        this.offsetFactorForInequalityQueries = offsetFactorForInequalityQueries;
    }

    public double getOffsetFactorForSingleTupleQueries() {
        return offsetFactorForSingleTupleQueries;
    }

    public void setOffsetFactorForSingleTupleQueries(double offsetFactorForSingleTupleQueries) {
        this.offsetFactorForSingleTupleQueries = offsetFactorForSingleTupleQueries;
    }

    public boolean isUseLimitInStandardQueries() {
        return useLimitInStandardQueries;
    }

    public void setUseLimitInStandardQueries(boolean useLimitInStandardQueries) {
        this.useLimitInStandardQueries = useLimitInStandardQueries;
    }

    public boolean isUseLimitInSymmetricQueries() {
        return useLimitInSymmetricQueries;
    }

    public void setUseLimitInSymmetricQueries(boolean useLimitInSymmetricQueries) {
        this.useLimitInSymmetricQueries = useLimitInSymmetricQueries;
    }

    public boolean isUseLimitInInequalityQueries() {
        return useLimitInInequalityQueries;
    }

    public void setUseLimitInInequalityQueries(boolean useLimitInInequalityQueries) {
        this.useLimitInInequalityQueries = useLimitInInequalityQueries;
    }

    public boolean isUseLimitInSingleTupleQueries() {
        return useLimitInSingleTupleQueries;
    }

    public void setUseLimitInSingleTupleQueries(boolean useLimitInSingleTupleQueries) {
        this.useLimitInSingleTupleQueries = useLimitInSingleTupleQueries;
    }

    public boolean isUseOffsetInStandardQueries() {
        return useOffsetInStandardQueries;
    }

    public void setUseOffsetInStandardQueries(boolean useOffsetInStandardQueries) {
        this.useOffsetInStandardQueries = useOffsetInStandardQueries;
    }

    public boolean isUseOffsetInSymmetricQueries() {
        return useOffsetInSymmetricQueries;
    }

    public void setUseOffsetInSymmetricQueries(boolean useOffsetInSymmetricQueries) {
        this.useOffsetInSymmetricQueries = useOffsetInSymmetricQueries;
    }

    public boolean isUseOffsetInInequalityQueries() {
        return useOffsetInInequalityQueries;
    }

    public void setUseOffsetInInequalityQueries(boolean useOffsetInInequalityQueries) {
        this.useOffsetInInequalityQueries = useOffsetInInequalityQueries;
    }

    public boolean isUseOffsetInSingleTupleQueries() {
        return useOffsetInSingleTupleQueries;
    }

    public void setUseOffsetInSingleTupleQueries(boolean useOffsetInSingleTupleQueries) {
        this.useOffsetInSingleTupleQueries = useOffsetInSingleTupleQueries;
    }

    public VioGenQueryConfiguration clone() {
        try {
            return (VioGenQueryConfiguration) super.clone();
        } catch (CloneNotSupportedException ex) {
        }
        return null;
    }

    public double getProbabilityFactorForStandardQueries() {
        return probabilityFactorForStandardQueries;
    }

    public void setProbabilityFactorForStandardQueries(double probabilityFactorForStandardQueries) {
        this.probabilityFactorForStandardQueries = probabilityFactorForStandardQueries;
    }

    public double getProbabilityFactorForSymmetricQueries() {
        return probabilityFactorForSymmetricQueries;
    }

    public void setProbabilityFactorForSymmetricQueries(double probabilityFactorForSymmetricQueries) {
        this.probabilityFactorForSymmetricQueries = probabilityFactorForSymmetricQueries;
    }

    public double getProbabilityFactorForInequalityQueries() {
        return probabilityFactorForInequalityQueries;
    }

    public void setProbabilityFactorForInequalityQueries(double probabilityFactorForInequalityQueries) {
        this.probabilityFactorForInequalityQueries = probabilityFactorForInequalityQueries;
    }

    public double getProbabilityFactorForSingleTupleQueries() {
        return probabilityFactorForSingleTupleQueries;
    }

    public void setProbabilityFactorForSingleTupleQueries(double probabilityFactorForSingleTupleQueries) {
        this.probabilityFactorForSingleTupleQueries = probabilityFactorForSingleTupleQueries;
    }

    public String getQueryExecutor() {
        return queryExecutor;
    }

    public void setQueryExecutor(String queryExecutor) {
        this.queryExecutor = queryExecutor;
    }
    
    

    @Override
    public String toString() {
        return "\t percentage=" + percentage
                + "\n\t sizeFactorForStandardQueries=" + sizeFactorForStandardQueries
                + "\n\t sizeFactorForSymmetricQueries=" + sizeFactorForSymmetricQueries
                + "\n\t sizeFactorForInequalityQueries=" + sizeFactorForInequalityQueries
                + "\n\t sizeFactorForSingleTupleQueries=" + sizeFactorForSingleTupleQueries
                + "\n\t probabilityFactorForStandardQueries=" + probabilityFactorForStandardQueries
                + "\n\t probabilityFactorForSymmetricQueries=" + probabilityFactorForSymmetricQueries
                + "\n\t probabilityFactorForInequalityQueries=" + probabilityFactorForInequalityQueries
                + "\n\t probabilityFactorForSingleTupleQueries=" + probabilityFactorForSingleTupleQueries
                + "\n\t windowSizeFactorForStandardQueries=" + windowSizeFactorForStandardQueries
                + "\n\t windowSizeFactorForSymmetricQueries=" + windowSizeFactorForSymmetricQueries
                + "\n\t windowSizeFactorForInequalityQueries=" + windowSizeFactorForInequalityQueries
                + "\n\t windowSizeFactorForSingleTupleQueries=" + windowSizeFactorForSingleTupleQueries
                + "\n\t useLimitInStandardQueries=" + useLimitInStandardQueries
                + "\n\t useLimitInSymmetricQueries=" + useLimitInSymmetricQueries
                + "\n\t useLimitInInequalityQueries=" + useLimitInInequalityQueries
                + "\n\t useLimitInSingleTupleQueries=" + useLimitInSingleTupleQueries
                + "\n\t useOffsetInStandardQueries=" + useOffsetInStandardQueries
                + "\n\t useOffsetInSymmetricQueries=" + useOffsetInSymmetricQueries
                + "\n\t useOffsetInInequalityQueries=" + useOffsetInInequalityQueries
                + "\n\t useOffsetInSingleTupleQueries=" + useOffsetInSingleTupleQueries
                + "\n\t offsetFactorForStandardQueries=" + offsetFactorForStandardQueries
                + "\n\t offsetFactorForSymmetricQueries=" + offsetFactorForSymmetricQueries
                + "\n\t offsetFactorForInequalityQueries=" + offsetFactorForInequalityQueries
                + "\n\t offsetFactorForSingleTupleQueries=" + offsetFactorForSingleTupleQueries
                + "\n\t queryExecutor=" + queryExecutor;
    }

}
