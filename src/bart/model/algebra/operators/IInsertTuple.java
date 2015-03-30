package bart.model.algebra.operators;

import bart.model.EGTask;
import bart.model.database.ITable;
import bart.model.database.Tuple;

public interface IInsertTuple {

    void execute(ITable table, Tuple tuple, EGTask task);

}
