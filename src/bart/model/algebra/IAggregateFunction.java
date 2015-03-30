package bart.model.algebra;

import bart.model.database.AttributeRef;
import bart.model.database.IValue;
import bart.model.database.Tuple;
import java.util.List;

public interface IAggregateFunction {
    
    public IValue evaluate(List<Tuple> tuples);
    
    public String getName();
    
    public AttributeRef getAttributeRef();

}
