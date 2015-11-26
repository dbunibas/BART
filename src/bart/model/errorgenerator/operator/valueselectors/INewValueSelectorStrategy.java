package bart.model.errorgenerator.operator.valueselectors;

import bart.model.EGTask;
import speedy.model.database.Cell;
import speedy.model.database.IValue;
import bart.model.errorgenerator.ICellChange;

public interface INewValueSelectorStrategy {

    public IValue generateNewValuesForContext(Cell originalCell, ICellChange cellChange, EGTask task);
}
