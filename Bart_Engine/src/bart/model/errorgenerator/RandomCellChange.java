package bart.model.errorgenerator;

import bart.BartConstants;
import speedy.model.database.Cell;

public class RandomCellChange extends AbstractCellChange {

    private Cell cell;
    private boolean export = true;

    public RandomCellChange(Cell cell) {
        this.cell = cell;
    }

    public Cell getCell() {
        return cell;
    }

    public String getType() {
        return BartConstants.RANDOM_CHANGE;
    }

    public boolean isExport() {
        return export;
    }

    public void setExport(boolean export) {
        this.export = export;
    }

    @Override
    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString()).append("\n");
        sb.append(super.toLongString());
        return sb.toString();
    }

}
