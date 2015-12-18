package bart.model.errorgenerator;

import speedy.model.database.IValue;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractCellChange implements ICellChange {

    private IValue newValue;
    private Set<ValueConstraint> whiteList = new HashSet<ValueConstraint>();
    private ValueConstraint whiteListIntersection;
    private Set<ValueConstraint> blackList = new HashSet<ValueConstraint>();
    private Set<String> violatedDependencies = new HashSet<String>();

    @Override
    public IValue getNewValue() {
        return newValue;
    }

    @Override
    public void setNewValue(IValue newValue) {
        this.newValue = newValue;
    }

    @Override
    public Set<ValueConstraint> getWhiteList() {
        return whiteList;
    }

    @Override
    public void addWhiteListValue(ValueConstraint value) {
        this.whiteList.add(value);
    }

    @Override
    public Set<ValueConstraint> getBlackList() {
        return blackList;
    }

    @Override
    public void addBlackListValue(ValueConstraint value) {
        this.blackList.add(value);
    }

    @Override
    public void setBlackList(Set<ValueConstraint> blackList) {
        this.blackList = blackList;
    }

    @Override
    public ValueConstraint getWhiteListIntersection() {
        return whiteListIntersection;
    }

    @Override
    public void setWhiteListIntersection(ValueConstraint whiteListIntersection) {
        this.whiteListIntersection = whiteListIntersection;
    }

    @Override
    public Set<String> getViolatedDependencies() {
        return violatedDependencies;
    }

    @Override
    public void addViolatedDependency(String dependencyId) {
        this.violatedDependencies.add(dependencyId);
    }

    @Override
    public String toString() {
        return getCell() + " := " + getNewValue() + "-" + hashCode();
    }

    public String toShortString() {
        return getCell() + " := " + getNewValue();
    }

    @Override
    public int hashCode() {
        return this.toShortString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final AbstractCellChange other = (AbstractCellChange) obj;
        return this.toShortString().equals(other.toShortString());
    }

    @Override
    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Type: ").append(this.getType()).append("\n");
        sb.append(whiteListIntersection == null ? "" : "\t White List Intersection:" + whiteListIntersection + "\n");
        sb.append(whiteList.isEmpty() ? "" : "\t White List values:" + whiteList + "\n");
        sb.append(blackList.isEmpty() ? "" : "\t Black List values:" + blackList + "\n");
        return sb.toString();
    }

}
