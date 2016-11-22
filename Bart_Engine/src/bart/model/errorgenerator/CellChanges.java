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
import speedy.utility.SpeedyUtility;

public class CellChanges {

    private final static Logger logger = LoggerFactory.getLogger(CellChanges.class);
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
//            this.changedCells.put(cellChange.getCell(), ((VioGenQueryCellChange) cellChange).getVioGenCell());
            this.changedCells.put(SpeedyUtility.unAlias(cellChange.getCell()), ((VioGenQueryCellChange) cellChange).getVioGenCell());
        }
//        this.newValues.put(cellChange.getCell(), cellChange.getNewValue());
        this.newValues.put(SpeedyUtility.unAlias(cellChange.getCell()), cellChange.getNewValue());
    }

    public void addCellInViolationContext(Cell cell) {
//        this.violationContextCells.add(cell);
        this.violationContextCells.add(SpeedyUtility.unAlias(cell));
    }

    public void addAllCellsInViolationContext(Set<Cell> cells) {
        for (Cell cell : cells) {
            addCellInViolationContext(cell);
        }
    }

    public boolean isViolationContextCell(Cell cell) {
        cell = SpeedyUtility.unAlias(cell);
        if (logger.isDebugEnabled()) logger.debug("Checking if cell " + cell + " is in violation context\n\t" + BartUtility.printCollection(violationContextCells, "\t"));
        if (logger.isDebugEnabled()) logger.debug("# Result: " + violationContextCells.contains(cell));
        return violationContextCells.contains(cell);
    }

    public boolean cellHasBeenChanged(Cell cell) {
//        return changedCells.keySet().contains(cell);
        return changedCells.keySet().contains(SpeedyUtility.unAlias(cell));
    }

    public IValue getNewValue(Cell cell) {
//        return newValues.get(cell);
        return newValues.get(SpeedyUtility.unAlias(cell));
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
