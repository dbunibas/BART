package bart.model.database;

import java.util.List;

public interface IDatabase extends Cloneable {

    public String getName();
    public List<String> getTableNames();
    public List<Key> getKeys();
    public List<Key> getKeys(String table);
    public List<ForeignKey> getForeignKeys();
    public List<ForeignKey> getForeignKeys(String table);
    
    public ITable getTable(String name);
    public void addTable(ITable table);
    public ITable getFirstTable();
    
    public long getSize();
    
    public IDatabase clone();
    
    public String printSchema();
    public String printInstances();
    public String printInstances(boolean sort);
    
}
