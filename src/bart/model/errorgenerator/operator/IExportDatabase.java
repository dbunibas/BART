package bart.model.errorgenerator.operator;

import speedy.model.database.IDatabase;
import bart.model.errorgenerator.CellChanges;

public interface IExportDatabase {

    public void export(IDatabase database, CellChanges cellChanges, String path);

    public void export(IDatabase database, String path);

}
