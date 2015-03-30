package bart.model.database.mainmemory.datasource;

import java.io.Serializable;

public class OID implements Serializable{
    
    private Object value;
    private String skolemString;
    
    public OID(Object value) {
        this.value = value;
    }
    
    public Object getValue() {
        return this.value;               
    }

    public String getSkolemString() {
        return skolemString;
    }

    public void setSkolemString(String skolemString) {
        this.skolemString = skolemString;
    }
    
    @Override
    public String toString() {
        return this.value.toString();
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OID)) {
            return false;
        }
        OID otherOid = (OID)o;
        return this.value.equals(otherOid.value);
    }
}
