package bart.model.errorgenerator.operator.deltadb;

import bart.model.EGTask;
import bart.model.database.IDatabase;


public interface IBuildDeltaDB {

    IDatabase generate(IDatabase database, EGTask task, String rootName);

}
