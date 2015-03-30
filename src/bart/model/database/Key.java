package bart.model.database;

import java.util.ArrayList;
import java.util.List;

public class Key {
    
    private List<AttributeRef> attributes;
    private boolean primaryKey;
    
    public Key(List<AttributeRef> attributes) {
        this(attributes, false);
    }
    
    public Key(AttributeRef attribute) {
        this(attribute, false);
    }

    public Key(List<AttributeRef> attributes, boolean primaryKey) {
        if (attributes.size() < 1) {
            throw new IllegalArgumentException("Key constraints cannot be empty: " + attributes);
        }
        this.attributes = attributes;
        this.primaryKey = primaryKey;
    }
    
    public Key(AttributeRef attribute, boolean primaryKey) {
        this.attributes = new ArrayList<AttributeRef>();
        this.attributes.add(attribute);
        this.primaryKey = primaryKey;
    }

    public List<AttributeRef> getAttributes() {
        return this.attributes;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public String toString() {
        String result = "";
        if (this.primaryKey) {
            result += "Primary key: ";
        } else {
            result += "Key: ";
        }
        result += this.attributes;        
        return result;
    }    

}
