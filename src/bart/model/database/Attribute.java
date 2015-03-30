package bart.model.database;

public class Attribute {

    private String tableName;
    private String name;
    private String type;

    public Attribute(String tableName, String name, String type) {
        this.tableName = tableName;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getTableName() {
        return tableName;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Attribute{" + "tableName=" + tableName + ", name=" + name + ", type=" + type + '}';
    }

    
}
