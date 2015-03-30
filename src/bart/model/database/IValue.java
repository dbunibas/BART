package bart.model.database;

import java.io.Serializable;

public interface IValue extends Serializable{
    
    public String getType();
    public Object getPrimitiveValue();
    @Override
    public String toString();

}
