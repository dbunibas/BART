package bart.model.database.operators.mainmemory;

import bart.model.EGTask;
import bart.model.database.IDatabase;
import bart.model.database.operators.IDatabaseManager;

public class MainMemoryDatabaseManager implements IDatabaseManager {

    public IDatabase cloneTarget(EGTask task, String suffix) {
        return task.getTarget().clone();
    }

    public void restoreTarget(IDatabase original, EGTask task, String suffix) {
        task.setTarget(original.clone());
    }

    public void removeClone(EGTask task, String suffix) {
    }

    public void analyzeDatabase(EGTask task) {
    }

    public void removeTable(String tableName, IDatabase deltaDB) {
    }
}
