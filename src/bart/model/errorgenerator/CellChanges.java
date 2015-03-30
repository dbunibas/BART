package bart.model.errorgenerator;

import bart.model.database.Cell;
import bart.model.database.IValue;
import bart.utility.BartUtility;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CellChanges {

    private static Logger logger = LoggerFactory.getLogger(CellChanges.class);
    private Set<CellChange> changes = new HashSet<CellChange>();
    private Map<Cell, VioGenCell> changedCells = new HashMap<Cell, VioGenCell>();
    private Map<Cell, IValue> newValues = new HashMap<Cell, IValue>();
    private Set<Cell> violationContextCells = new HashSet<Cell>();

    public Set<CellChange> getChanges() {
        return changes;
    }

    public void addChange(CellChange cellChange) {
        this.changes.add(cellChange);
        this.changedCells.put(cellChange.getCell(), cellChange.getVioGenCell());
        this.newValues.put(cellChange.getCell(), cellChange.getNewValue());
    }

    public void addCellInViolationContext(Cell cell) {
        this.violationContextCells.add(cell);
    }

    public void addAllCellsInViolationContext(Set<Cell> cells) {
        this.violationContextCells.addAll(cells);
    }

    public boolean isViolationContextCell(Cell cell) {
        if (logger.isDebugEnabled()) logger.debug("Checking if cell " + cell + " is in violation context\n\t" + BartUtility.printCollection(violationContextCells, "\t"));
        if (logger.isDebugEnabled()) logger.debug("# Result: " + violationContextCells.contains(cell));
        return violationContextCells.contains(cell);
    }

    public boolean cellHasBeenChanged(Cell cell) {
        return changedCells.keySet().contains(cell);
    }

    public IValue getNewValue(Cell cell) {
        return newValues.get(cell);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CellChanges:\n");
        for (CellChange cellChange : changes) {
            sb.append(cellChange.toString()).append(" [").append(cellChange.getVioGenQuery().getDependency().getId()).append("]\n");
        }
        return sb.toString();
    }

}
