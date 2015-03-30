package bart.model.database.operators;

import bart.model.algebra.IAlgebraOperator;
import bart.model.algebra.operators.ITupleIterator;
import bart.model.database.IDatabase;
import bart.model.database.ResultInfo;

public interface IRunQuery {

    ITupleIterator run(IAlgebraOperator query, IDatabase source, IDatabase target);

    ResultInfo getSize(IAlgebraOperator query, IDatabase source, IDatabase target);

}
