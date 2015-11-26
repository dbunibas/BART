package bart.model.detection.operator;

import bart.BartConstants;
import bart.exceptions.ErrorGeneratorException;
import bart.model.EGTask;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import bart.model.dependency.Dependency;
import bart.model.detection.Violations;
import bart.model.errorgenerator.VioGenQueryCellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ICellChange;
import bart.model.errorgenerator.OutlierCellChange;
import bart.model.errorgenerator.ViolationContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CheckDetectableCellChanges {

    public Set<ICellChange> findNonDetectableChanges(CellChanges changes, Violations violations, EGTask task) {
        return findChangesWithDetectabilityK(changes, violations, 0, task);
    }

    public Set<ICellChange> findChangesDetectableOnce(CellChanges changes, Violations violations, EGTask task) {
        return findChangesWithDetectabilityK(changes, violations, 1, task);
    }

    public Set<ICellChange> findChangesWithDetectabilityK(CellChanges changes, Violations violations, int k, EGTask task) {
//        if (!task.getConfiguration().isAvoidInteractions()) {
//            throw new IllegalArgumentException("Unable to check detectable changes with avoidInteractions = false");
//        }
        checkDetectability(changes, violations, task);
        Set<ICellChange> result = new HashSet<ICellChange>();
        for (ICellChange change : changes.getChanges()) {
            if (change.getViolatedDependencies().size() == k) {
                if (!change.getType().equals(BartConstants.OUTLIER_CHANGE)) {
                    result.add(change);
                } else {
                    OutlierCellChange outlierCellChange = (OutlierCellChange) change;
                    if (!outlierCellChange.isDetectable()) result.add(change);
                }
            }
        }
        return result;
    }

    private void checkDetectability(CellChanges changes, Violations violations, EGTask task) throws ErrorGeneratorException {
        Map<Dependency, Set<CellRef>> violatedCells = initializeViolatedCells(violations);
        for (ICellChange cellChange : changes.getChanges()) {
            for (Dependency dependency : violatedCells.keySet()) {
                Set<CellRef> cellsForDependency = violatedCells.get(dependency);
                if (cellsForDependency.contains(getCellRefNoAlias(cellChange.getCell()))) {
                    cellChange.addViolatedDependency(dependency.getId());
                }
            }
        }
    }

    private Map<Dependency, Set<CellRef>> initializeViolatedCells(Violations violations) {
        Map<Dependency, Set<CellRef>> result = new HashMap<Dependency, Set<CellRef>>();
        for (Dependency dependency : violations.getViolations().keySet()) {
            Set<CellRef> cellsForDependency = new HashSet<CellRef>();
            result.put(dependency, cellsForDependency);
            for (ViolationContext context : violations.getViolations().get(dependency)) {
                for (Cell cell : context.getCells()) {
                    cellsForDependency.add(getCellRefNoAlias(cell));
                }
            }
        }
        return result;
    }

    private CellRef getCellRefNoAlias(Cell cell) {
        AttributeRef attributeRefNoAlias = new AttributeRef(cell.getAttributeRef().getTableName(), cell.getAttribute());
        return new CellRef(cell.getTupleOID(), attributeRefNoAlias);
    }

    public void checkIfDoubleModification(CellChanges changes) {
        Set<Cell> modifiedCells = new HashSet<Cell>();
        for (ICellChange change : changes.getChanges()) {
            Cell modifiedCell = change.getCell();
            if (modifiedCells.contains(modifiedCell)) {
                if (change.getType().equals(BartConstants.VIOGEN_CHANGE)) {
                    throw new ErrorGeneratorException("The same cell has been changed twice " + modifiedCell + " - " + ((VioGenQueryCellChange) change).getVioGenQuery().toShortString());
                } else {
                    throw new ErrorGeneratorException("The same cell has been changed twice " + modifiedCell + " - Type: " + change.getType()); // TODO getType
                }
            }
            modifiedCells.add(modifiedCell);
        }
    }
}
