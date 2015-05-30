package bart.model.errorgenerator.operator.valueselectors;

import bart.model.EGTask;
import bart.model.database.Cell;
import bart.model.database.IValue;
import bart.model.errorgenerator.ICellChange;

public interface INewValueSelectorStrategy {

    public IValue generateNewValuesForContext(Cell originalCell, ICellChange cellChange, EGTask task);
}
