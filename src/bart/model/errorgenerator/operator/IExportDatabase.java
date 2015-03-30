package bart.model.errorgenerator.operator;

import bart.model.database.IDatabase;
import bart.model.errorgenerator.CellChanges;

public interface IExportDatabase {

    public void export(IDatabase database, CellChanges cellChanges, String path);

    public void export(IDatabase database, String path);

}
