package bart.model.database;

import java.io.Serializable;

public class TupleOID implements Serializable{

    private Object value;
    
    public TupleOID(Object value) {
        this.value = value;
    }
    
    public Object getValue() {
        return this.value;               
    }
    
    @Override
    public String toString() {
        return this.value.toString();
    }
    
    @Override
    public int hashCode() {
        return this.value.toString().hashCode();
    }
    
    public Long getNumericalValue() {
        return Long.parseLong(value.toString());
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TupleOID)) {
            return false;
        }
        TupleOID otherOid = (TupleOID)o;
        return this.toString().equals(otherOid.toString());
    }
}
