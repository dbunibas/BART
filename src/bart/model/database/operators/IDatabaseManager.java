package bart.model.database.operators;

import bart.model.EGTask;
import bart.model.database.IDatabase;

public interface IDatabaseManager {

    public IDatabase cloneTarget(EGTask task, String suffix);

    public void restoreTarget(IDatabase original, EGTask task, String suffix);

    public void removeClone(EGTask task, String suffix);

    public void analyzeDatabase(EGTask task);

    public void removeTable(String tableName, IDatabase deltaDB);

}
