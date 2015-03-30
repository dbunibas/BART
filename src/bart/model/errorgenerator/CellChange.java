package bart.model.errorgenerator;

import bart.model.database.Cell;
import bart.model.database.IValue;
import java.util.HashSet;
import java.util.Set;

public class CellChange {

    private VioGenCell vioGenCell;
    private IValue newValue;
    private ViolationContext generatingContext;
    private VioGenQuery vioGenQuery;
    private Set<ValueConstraint> whiteList = new HashSet<ValueConstraint>();
    private ValueConstraint whiteListIntersection;
    private Set<ValueConstraint> blackList = new HashSet<ValueConstraint>();
    private Set<String> violatedDependencies = new HashSet<String>();
    private double repairabilityAPosteriori;
    private double repairabilityAPriori;

    public CellChange(VioGenCell cell, ViolationContext context, VioGenQuery vioGenQuery) {
        this.vioGenCell = cell;
        this.generatingContext = context;
        this.vioGenQuery = vioGenQuery;
    }

    public void setNewValue(IValue newValue) {
        this.newValue = newValue;
    }

    public VioGenCell getVioGenCell() {
        return vioGenCell;
    }

    public Cell getCell() {
        return vioGenCell.getCell();
    }

    public IValue getNewValue() {
        return newValue;
    }

    public VioGenQuery getVioGenQuery() {
        return vioGenQuery;
    }

    public ViolationContext getGeneratingContext() {
        return generatingContext;
    }

    public void setGeneratingContext(ViolationContext generatingContext) {
        this.generatingContext = generatingContext;
    }

    public Set<ValueConstraint> getWhiteList() {
        return whiteList;
    }

    public void addWhiteListValue(ValueConstraint value) {
        this.whiteList.add(value);
    }

    public Set<ValueConstraint> getBlackList() {
        return blackList;
    }

    public void addBlackListValue(ValueConstraint value) {
        this.blackList.add(value);
    }

    public void setBlackList(Set<ValueConstraint> blackList) {
        this.blackList = blackList;
    }

    public ValueConstraint getWhiteListIntersection() {
        return whiteListIntersection;
    }

    public void setWhiteListIntersection(ValueConstraint whiteListIntersection) {
        this.whiteListIntersection = whiteListIntersection;
    }

    public Set<String> getViolatedDependencies() {
        return violatedDependencies;
    }

    public void addViolatedDependency(String dependencyId) {
        this.violatedDependencies.add(dependencyId);
    }

    public double getRepairabilityAPosteriori() {
        return repairabilityAPosteriori;
    }

    public void setRepairabilityAPosteriori(double repairabilityAPosteriori) {
        this.repairabilityAPosteriori = repairabilityAPosteriori;
    }

    public double getRepairabilityAPriori() {
        return repairabilityAPriori;
    }

    public void setRepairabilityAPriori(double repairabilityAPriori) {
        this.repairabilityAPriori = repairabilityAPriori;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.getCell() != null ? this.getCell().hashCode() : 0);
        hash = 17 * hash + (this.newValue != null ? this.newValue.hashCode() : 0);
        hash = 17 * hash + (this.generatingContext != null ? this.generatingContext.hashCode() : 0);
        hash = 17 * hash + (this.vioGenQuery != null ? this.vioGenQuery.toShortString().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final CellChange other = (CellChange) obj;
        if (this.getCell() != other.getCell() && (this.getCell() == null || !this.getCell().equals(other.getCell()))) return false;
        if (this.newValue != other.newValue && (this.newValue == null || !this.newValue.equals(other.newValue))) return false;
        if (this.generatingContext != other.generatingContext && (this.generatingContext == null || !this.generatingContext.equals(other.generatingContext))) return false;
        if (this.vioGenQuery != other.vioGenQuery && (this.vioGenQuery == null || !this.vioGenQuery.toShortString().equals(other.vioGenQuery.toShortString()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return getCell() + " := " + newValue;
    }

    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString()).append("\n");
        sb.append("Context: ").append(generatingContext.toString()).append("\n");
        sb.append("VioGenQuery ").append(vioGenQuery.toShortString()).append("\n");
        sb.append(whiteListIntersection == null ? "" : "\t White List Intersection:" + whiteListIntersection + "\n");
        sb.append(whiteList.isEmpty() ? "" : "\t White List values:" + whiteList + "\n");
        sb.append(blackList.isEmpty() ? "" : "\t Black List values:" + blackList + "\n");
        return sb.toString();
    }

}
