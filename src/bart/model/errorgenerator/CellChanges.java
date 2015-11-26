package bart.model.errorgenerator;

import bart.BartConstants;
import speedy.model.database.Cell;
import speedy.model.database.IValue;
import bart.utility.BartUtility;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CellChanges {

    private final static Logger logger = LoggerFactory.getLogger(CellChanges.class);
//    private final Set<ICellChange> changes = new HashSet<ICellChange>();
    private final Map<String, ICellChange> changes = new HashMap<String, ICellChange>();
    private final Map<Cell, VioGenCell> changedCells = new HashMap<Cell, VioGenCell>();
    private final Map<Cell, IValue> newValues = new HashMap<Cell, IValue>();
    private final Set<Cell> violationContextCells = new HashSet<Cell>();

    public Set<ICellChange> getChanges() {
        return new HashSet<ICellChange>(changes.values());
    }

    public void addChange(ICellChange cellChange) {
        if (logger.isDebugEnabled()) logger.debug("Adding change: " + cellChange + " - " + cellChange.hashCode() + "\n" + BartUtility.printCollection(changes.values()));
        this.changes.put(cellChange.toString(), cellChange);
        if (cellChange.getType().equals(BartConstants.VIOGEN_CHANGE)) {
            this.changedCells.put(cellChange.getCell(), ((VioGenQueryCellChange) cellChange).getVioGenCell());
        }
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
        for (ICellChange cellChange : changes.values()) {
//            sb.append(cellChange.toString()).append(" [").append(cellChange.getVioGenQuery().getDependency().getId()).append("]\n");
            sb.append(cellChange.toString()).append("\n");
        }
        return sb.toString();
    }

}
