package bart.model.detection.operator;

import bart.model.database.AttributeRef;
import bart.model.database.Cell;
import bart.model.database.IValue;
import bart.model.errorgenerator.CellChange;
import bart.model.errorgenerator.ViolationContext;
import bart.utility.BartUtility;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EstimateRepairabilityForCellChange {

    private static Logger logger = LoggerFactory.getLogger(EstimateRepairabilityForCellChange.class);

    public double computeRepairability(CellChange cellChange, ViolationContext context) {
        List<ViolationContext> contexts = new ArrayList<ViolationContext>();
        contexts.add(context);
        return computeRepairability(cellChange, contexts);
    }

    public double computeRepairability(CellChange cellChange, List<ViolationContext> contexts) {
        if (logger.isDebugEnabled()) logger.debug("Computing reparability for change " + cellChange + " with contexts " + BartUtility.printCollection(contexts));
        if (contexts.isEmpty()) {
            if (logger.isInfoEnabled()) logger.info("Non detectable change " + cellChange);
            return 0.0;
        }
        double maxRepairability = 0.0;
        for (ViolationContext context : contexts) {
            IValue originalValue = cellChange.getCell().getValue();
            if (hasSourceCellsWithValue(context, originalValue)) {
                maxRepairability = 1.0;
                break;
            }
            List<Cell> cellsForAttribute = findCellsForAttribute(context, cellChange.getCell().getAttributeRef());
            if (logger.isDebugEnabled()) logger.debug("Candidate cells: " + cellsForAttribute);
            List<Cell> cellsWithOriginalValue = findCellsWithValue(cellsForAttribute, originalValue);
            if (logger.isDebugEnabled()) logger.debug("Cells with original value: " + cellsWithOriginalValue);
            double repairability = cellsWithOriginalValue.size() / (double) cellsForAttribute.size();
            if (maxRepairability < repairability) {
                maxRepairability = repairability;
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Repairability: " + maxRepairability);
        return maxRepairability;
    }

    private boolean hasSourceCellsWithValue(ViolationContext violationContext, IValue value) {
        for (Cell cell : violationContext.getCells()) {
            if (cell.getAttributeRef().isSource() && cell.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private List<Cell> findCellsForAttribute(ViolationContext violationContext, AttributeRef attributeRef) {
        List<Cell> cellsForAttribute = new ArrayList<Cell>();
        for (Cell cell : violationContext.getCells()) {
            if (cell.getAttributeRef().equalsModuloAlias(attributeRef)) {
                cellsForAttribute.add(cell);
            }
        }
        return cellsForAttribute;
    }

    private List<Cell> findCellsWithValue(List<Cell> cellsForAttribute, IValue value) {
        List<Cell> result = new ArrayList<Cell>();
        for (Cell cell : cellsForAttribute) {
            if (cell.getValue().equals(value)) {
                result.add(cell);
            }
        }
        return result;
    }
}
