package bart.model.errorgenerator.operator.valueselectors;

import bart.model.EGTask;
import bart.model.database.IValue;
import bart.model.errorgenerator.CellChange;
import bart.model.errorgenerator.VioGenCell;

public interface INewValueSelectorStrategy {

    public IValue generateNewValuesForContext(VioGenCell vioGenCell, CellChange cellChange, EGTask task);
}
