package bart.model.algebra.operators;

import bart.model.database.CellRef;
import bart.model.database.IDatabase;
import bart.model.database.IValue;

public interface IUpdateCell {

    void execute(CellRef cellRef, IValue value, IDatabase database);

}
