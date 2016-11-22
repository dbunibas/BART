package bart.model.errorgenerator;

import bart.BartConstants;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import java.util.ArrayList;
import java.util.List;

public class VioGenQueryCellChange extends AbstractCellChange {

    private VioGenCell vioGenCell;
    private ViolationContext context;
    private VioGenQuery vioGenQuery;

    private List<ViolationContext> violationContexts = new ArrayList<ViolationContext>();
    private double repairability;

    public VioGenQueryCellChange(VioGenCell cell, ViolationContext context, VioGenQuery vioGenQuery) {
        this.vioGenCell = cell;
        this.context = context;
        this.vioGenQuery = vioGenQuery;
    }

    public VioGenCell getVioGenCell() {
        return vioGenCell;
    }

    public Cell getCell() {
        return vioGenCell.getCell();
    }

    public VioGenQuery getVioGenQuery() {
        return vioGenQuery;
    }

    public ViolationContext getContext() {
        return context;
    }

    public void setContext(ViolationContext context) {
        this.context = context;
    }

    public List<ViolationContext> getViolationContexts() {
        return violationContexts;
    }

    public void setViolationContexts(List<ViolationContext> violationContexts) {
        this.violationContexts = violationContexts;
    }

    public double getRepairability() {
        return repairability;
    }

    public void setRepairability(double repairability) {
        this.repairability = repairability;
    }

    public String getType() {
        return BartConstants.VIOGEN_CHANGE;
    }

//    @Override
//    public int hashCode() {
//        int hash = 7;
//        hash = 17 * hash + (this.getCell() != null ? this.getCell().hashCode() : 0);
//        hash = 17 * hash + (this.getNewValue() != null ? this.getNewValue().hashCode() : 0);
//        hash = 17 * hash + (this.context != null ? this.context.hashCode() : 0);
//        hash = 17 * hash + (this.vioGenQuery != null ? this.vioGenQuery.toShortString().hashCode() : 0);
//        return hash;
//    }
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) return false;
//        if (getClass() != obj.getClass()) return false;
//        final VioGenQueryCellChange other = (VioGenQueryCellChange) obj;
//        if (this.getCell() != other.getCell() && (this.getCell() == null || !this.getCell().equals(other.getCell()))) return false;
//        if (this.getNewValue() != other.getNewValue() && (this.getNewValue() == null || !this.getNewValue().equals(other.getNewValue()))) return false;
//        if (this.context != other.context && (this.context == null || !this.context.equals(other.context))) return false;
//        if (this.vioGenQuery != other.vioGenQuery && (this.vioGenQuery == null || !this.vioGenQuery.toShortString().equals(other.vioGenQuery.toShortString()))) return false;
//        return true;
//    }
    @Override
    public String toShortString() {
        return this.getCell().getTupleOID() + ":" + this.getCell().getAttributeRef().toStringNoAlias() + "." + (this.getNewValue() == null ? "null" : this.getNewValue().toString());
    }

    @Override
    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString()).append("\n");
        sb.append("Context: ").append(context == null ? "" : context.toString()).append("\n");
        sb.append("VioGenQuery ").append(vioGenQuery.toShortString()).append("\n");
        sb.append(super.toLongString());
        return sb.toString();
    }

}
