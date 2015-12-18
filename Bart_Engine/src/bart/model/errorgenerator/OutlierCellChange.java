package bart.model.errorgenerator;

import bart.BartConstants;
import speedy.model.database.Cell;

public class OutlierCellChange extends AbstractCellChange {

    private Cell cell;
    private OutlierRange range;
    private boolean detectable = false;
//    private OutlierRange newRange;

    public OutlierCellChange(Cell cell) {
        this.cell = cell;
    }

    public Cell getCell() {
        return cell;
    }

    public void setRange(double min, double max) {
        this.range = new OutlierRange(min, max);
    }

    public boolean isDetectable() {
        return detectable;
    }

    public void setDetectable(boolean detectable) {
        this.detectable = detectable;
    }

//    public void setNewRange(double min, double max) {
//        this.newRange = new OutlierRange(min, max);
//    }
    
    public boolean isDetectable(double q1, double q3) {
        Number numericalValue = (Number) super.getNewValue().getPrimitiveValue();
        double changed = numericalValue.doubleValue();
        double iqr = q3 - q1;
        return isOutlier(q1, q3, iqr, changed);
    }

    private boolean isOutlier(double q1, double q3, double iqr, double value) {
        /* Tukey’s Outlier Filter */
        if (value < (q1 - 1.5 * iqr) || value > (q3 + 1.5 * iqr)) {
            return true;
        }
        return false;
    }

    @Override
    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString()).append("\n");
        sb.append(super.toLongString());
        sb.append("Original range for outliers: ").append(range).append("\n");
//        sb.append("New range outlier: ").append(newRange).append("\n");
        return sb.toString();
    }

    public String getType() {
        return BartConstants.OUTLIER_CHANGE;
    }

    private class OutlierRange {

        private double min;
        private double max;

        public OutlierRange(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("value < ").append(min).append(" or value > ").append(max);
            return sb.toString();
        }
    }

}
