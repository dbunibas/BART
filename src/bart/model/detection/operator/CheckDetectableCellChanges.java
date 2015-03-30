package bart.model.detection.operator;

import bart.exceptions.ErrorGeneratorException;
import bart.model.EGTask;
import bart.model.database.AttributeRef;
import bart.model.database.Cell;
import bart.model.database.CellRef;
import bart.model.dependency.Dependency;
import bart.model.detection.Violations;
import bart.model.errorgenerator.CellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ViolationContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CheckDetectableCellChanges {

    public Set<CellChange> findNonDetectableChanges(CellChanges changes, Violations violations, EGTask task) {
        return findChangesWithDetectabilityK(changes, violations, 0, task);
    }

    public Set<CellChange> findChangesDetectableOnce(CellChanges changes, Violations violations, EGTask task) {
        return findChangesWithDetectabilityK(changes, violations, 1, task);
    }

    public Set<CellChange> findChangesWithDetectabilityK(CellChanges changes, Violations violations, int k, EGTask task) {
        if(!task.getConfiguration().isAvoidInteractions()){
            throw new IllegalArgumentException("Unable to check detectable changes with avoidInteractions = false");
        }
        checkDetectability(changes, violations, task);
        Set<CellChange> result = new HashSet<CellChange>();
        for (CellChange change : changes.getChanges()) {
            if (change.getViolatedDependencies().size() == k) {
                result.add(change);
            }
        }
        return result;
    }

    private void checkDetectability(CellChanges changes, Violations violations, EGTask task) throws ErrorGeneratorException {
        Map<Dependency, Set<CellRef>> violatedCells = initializeViolatedCells(violations);
        for (CellChange cellChange : changes.getChanges()) {
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
        for (CellChange change : changes.getChanges()) {
            Cell modifiedCell = change.getCell();
            if (modifiedCells.contains(modifiedCell)) {
                throw new ErrorGeneratorException("The same cell has been changed twice " + modifiedCell + " - " + change.getVioGenQuery().toShortString());
            }
            modifiedCells.add(modifiedCell);
        }
    }
}
