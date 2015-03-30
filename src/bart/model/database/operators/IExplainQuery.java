package bart.model.database.operators;

import bart.model.EGTask;
import bart.model.algebra.IAlgebraOperator;
import bart.model.database.IDatabase;
import bart.model.database.ITable;
import bart.model.database.Tuple;

public interface IExplainQuery {

    long explain(IAlgebraOperator query, EGTask task);

}
