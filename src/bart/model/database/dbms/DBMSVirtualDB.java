package bart.model.database.dbms;


import bart.model.database.ForeignKey;
import bart.model.database.IDatabase;
import bart.model.database.ITable;
import bart.model.database.Key;
import bart.BartConstants;
import bart.persistence.relational.AccessConfiguration;
import java.util.ArrayList;
import java.util.List;

public class DBMSVirtualDB implements IDatabase {

    private DBMSDB originalDB;
    private DBMSDB deltaDB;
    private String suffix;
    private AccessConfiguration accessConfiguration;
//    private List<DBMSVirtualTable> tables = new ArrayList<DBMSVirtualTable>();
    private List<ITable> tables = new ArrayList<ITable>();

    public DBMSVirtualDB(DBMSDB originalDB, DBMSDB deltaDB, String suffix, AccessConfiguration accessConfiguration) {
        this.originalDB = originalDB;
        this.deltaDB = deltaDB;
        this.suffix = suffix;
        this.accessConfiguration = accessConfiguration;
        loadTables();
    }

    private void loadTables() {
        for (String tableName : originalDB.getTableNames()) {
            ITable originalTable = originalDB.getTable(tableName);
            tables.add(new DBMSVirtualTable(originalTable, accessConfiguration, suffix));
        }
    }

    public void addTable(ITable table) {
//        tables.add((DBMSVirtualTable) table);
        tables.add(table);
    }

    public String getName() {
        return "Virtual" + originalDB.getName();
    }

    public List<String> getTableNames() {
        List<String> result = new ArrayList<String>();
        for (ITable virtualTable : tables) {
            result.add(virtualTable.getName());
        }
        return result;
//        return originalDB.getTableNames();
    }

    public List<Key> getKeys() {
        return originalDB.getKeys();
    }

    public List<Key> getKeys(String table) {
        return originalDB.getKeys(table);
    }

    public List<ForeignKey> getForeignKeys() {
        return originalDB.getForeignKeys();
    }

    public List<ForeignKey> getForeignKeys(String table) {
        return originalDB.getForeignKeys(table);
    }

    public ITable getTable(String name) {
//        return new DBMSVirtualTable(name, accessConfiguration, stepId);
        for (ITable table : tables) {
            if (table.getName().equalsIgnoreCase(name)) {
                return table;
            }
        }
        return deltaDB.getTable(name);
//        throw new IllegalArgumentException("Unable to find table " + name + " in database " + getName());
    }

    public ITable getFirstTable() {
        return getTable(getTableNames().get(0));
    }

    public AccessConfiguration getAccessConfiguration() {
        return accessConfiguration;
    }

    public String getInitDBScript() {
        return null;
    }

    public void setInitDBScript(String initDBScript) {
    }

    public long getSize() {
        return this.originalDB.getSize();
    }

    public IDatabase clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String printSchema() {
        StringBuilder result = new StringBuilder();
        result.append("Schema: ").append(getName()).append(" {\n");
        for (String tableName : getTableNames()) {
            DBMSVirtualTable table = (DBMSVirtualTable) getTable(tableName);
            result.append(table.printSchema(BartConstants.INDENT));
//            table.closeConnection();
        }
        if (!getKeys().isEmpty()) {
            result.append(BartConstants.INDENT).append("--------------- Keys: ---------------\n");
            for (Key key : getKeys()) {
                result.append(BartConstants.INDENT).append(key).append("\n");
            }
        }
        if (!getForeignKeys().isEmpty()) {
            result.append(BartConstants.INDENT).append("----------- Foreign Keys: -----------\n");
            for (ForeignKey foreignKey : getForeignKeys()) {
                result.append(BartConstants.INDENT).append(foreignKey).append("\n");
            }
        }
        result.append("}\n");
        return result.toString();
    }

    public String printInstances() {
        return printInstances(false);
    }

    public String printInstances(boolean sort) {
        StringBuilder result = new StringBuilder();
        for (String tableName : getTableNames()) {
            DBMSVirtualTable table = (DBMSVirtualTable) getTable(tableName);
            if (sort) {
                result.append(table.toStringWithSort(BartConstants.INDENT));
            } else {
                result.append(table.toString(BartConstants.INDENT));
            }
//            table.closeConnection();
        }
        return result.toString();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
//        result.append(printSchema());
        result.append(printInstances());
        return result.toString();
    }
}
