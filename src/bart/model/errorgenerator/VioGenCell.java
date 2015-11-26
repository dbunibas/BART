package bart.model.errorgenerator;

import bart.BartConstants;
import speedy.model.database.Cell;
import bart.utility.BartUtility;
import java.util.ArrayList;
import java.util.List;

public class VioGenCell {

    private VioGenQuery vioGenQuery;
    private Cell vioGenCell;
    private List<ViolationContext> violationContexts = new ArrayList<ViolationContext>();

    public VioGenCell(VioGenQuery vioGenQuery, Cell vioGenCell) {
        this.vioGenQuery = vioGenQuery;
        this.vioGenCell = vioGenCell;
    }

    public VioGenQuery getVioGenQuery() {
        return vioGenQuery;
    }

    public Cell getCell() {
        return vioGenCell;
    }

    public List<ViolationContext> getViolationContexts() {
        return violationContexts;
    }

    public void addViolationContext(ViolationContext violationContext) {
        this.violationContexts.add(violationContext);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VioGenCell ").append(vioGenCell).append("\n");
        sb.append(BartUtility.printCollection(violationContexts, BartConstants.INDENT)).append("\n");
        return sb.toString();
    }

}
