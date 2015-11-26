package bart.model.errorgenerator;

import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import bart.utility.BartUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import speedy.utility.comparator.StringComparator;

public class ViolationContext {

    private Set<Cell> contextCells = new HashSet<Cell>();
    private Set<CellRef> contextCellRefs = new HashSet<CellRef>();
    private String dependencyId;

    public ViolationContext() {
    }

    public String getDependencyId() {
        return dependencyId;
    }

    public void setDependencyId(String dependencyId) {
        this.dependencyId = dependencyId;
    }

    public Set<Cell> getCells() {
        return contextCells;
    }

    public void addAllCells(Set<Cell> cells) {
        for (Cell cell : cells) {
            addCell(cell);
        }
    }
    
    public boolean containsCellRef(CellRef cellRef){
        return contextCellRefs.contains(cellRef);
    }

    public void addCell(Cell cell) {
        this.contextCells.add(cell);
        this.contextCellRefs.add(BartUtility.getCellRefNoAlias(cell));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.contextCells != null ? this.contextCells.hashCode() : 0);
        hash = 61 * hash + (this.dependencyId != null ? this.dependencyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final ViolationContext other = (ViolationContext) obj;
        if (this.contextCells != other.contextCells && (this.contextCells == null || !this.contextCells.equals(other.contextCells))) return false;
        if ((this.dependencyId == null) ? (other.dependencyId != null) : !this.dependencyId.equals(other.dependencyId)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        List<Cell> orderedCells = new ArrayList<Cell>(contextCells);
        Collections.sort(orderedCells, new StringComparator());
        sb.append(orderedCells);
        return sb.toString();
    }

    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VioContext ").append(contextCells).append(" (").append(dependencyId).append(")\n");
        return sb.toString();
    }

}
