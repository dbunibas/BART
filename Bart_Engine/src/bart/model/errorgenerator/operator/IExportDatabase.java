package bart.model.errorgenerator.operator;

import speedy.model.database.IDatabase;
import bart.model.errorgenerator.CellChanges;

public interface IExportDatabase {

    public void export(IDatabase database, String prefix, CellChanges cellChanges, String path, String taskPath);

    public void export(IDatabase database, String prefix, String path, String taskPath);

}
