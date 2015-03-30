package bart.model.algebra.operators;

import bart.model.algebra.IAlgebraOperator;
import bart.model.database.IDatabase;

public interface IDelete {

    boolean execute(String tableName, IAlgebraOperator sourceQuery, IDatabase source, IDatabase target);

}
