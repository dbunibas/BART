package bart.model.database;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class EmptyDB implements IDatabase {

    public String getName() {
        return "EMPTY";
    }

    public List<String> getTableNames() {
        return Collections.EMPTY_LIST;
    }

    public List<Key> getKeys() {
        return Collections.EMPTY_LIST;
    }

    public List<Key> getKeys(String table) {
        return Collections.EMPTY_LIST;
    }

    public List<ForeignKey> getForeignKeys() {
        return Collections.EMPTY_LIST;
    }

    public List<ForeignKey> getForeignKeys(String table) {
        return Collections.EMPTY_LIST;
    }

    public ITable getTable(String name) {
        return null;
    }

    public ITable getFirstTable() {
        return null;
    }

    public String printSchema() {
        StringBuilder result = new StringBuilder();
        result.append("DB: ").append(getName()).append(" {");
        result.append(" }\n");
        return result.toString();
    }

    public String printInstances() {
        return "";
    }

    public String printInstances(boolean sort) {
        return "";
    }

    public String toString() {
        return printSchema();
    }

    public IDatabase clone() {
        return this;
    }

    public long getSize() {
        return 0;
    }

    public void addTable(ITable table) {
    }
}
