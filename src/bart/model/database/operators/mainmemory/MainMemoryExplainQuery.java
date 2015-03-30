package bart.model.database.operators.mainmemory;

import bart.model.EGTask;
import bart.model.algebra.IAlgebraOperator;
import bart.model.database.operators.IExplainQuery;
import bart.model.database.operators.IRunQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainMemoryExplainQuery implements IExplainQuery {

    private static Logger logger = LoggerFactory.getLogger(MainMemoryExplainQuery.class);
    private IRunQuery queryRunner = new MainMemoryRunQuery();

    public long explain(IAlgebraOperator query, EGTask task) {
        return queryRunner.getSize(query, task.getSource(), task.getTarget()).getSize();
    }

}
