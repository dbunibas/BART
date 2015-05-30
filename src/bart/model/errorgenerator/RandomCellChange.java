package bart.model.errorgenerator;

import bart.BartConstants;
import bart.model.database.Cell;

public class RandomCellChange extends AbstractCellChange {

    private Cell cell;

    public RandomCellChange(Cell cell) {
        this.cell = cell;
    }

    public Cell getCell() {
        return cell;
    }

    @Override
    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString()).append("\n");
        sb.append(super.toLongString());
        return sb.toString();
    }

    public String getType() {
        return BartConstants.RANDOM_CHANGE;
    }

}
