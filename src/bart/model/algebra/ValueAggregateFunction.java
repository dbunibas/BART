package bart.model.algebra;

import bart.BartConstants;
import bart.exceptions.AlgebraException;
import bart.model.database.AttributeRef;
import bart.model.database.IValue;
import bart.model.database.Tuple;
import bart.model.database.NullValue;
import java.util.List;

public class ValueAggregateFunction implements IAggregateFunction {
    
    private AttributeRef attributeRef;

    public ValueAggregateFunction(AttributeRef attributeRef) {
        this.attributeRef = attributeRef;
    }

    public IValue evaluate(List<Tuple> tuples) {
        if (tuples.isEmpty()) {
            return new NullValue(BartConstants.NULL_VALUE);
        }
        if (!checkValues(tuples, attributeRef)) {
            throw new AlgebraException("Trying to extract aggregate value " + attributeRef + " from tuples with different values " + tuples);
        }
        return tuples.get(0).getCell(attributeRef).getValue();        
    }

    private boolean checkValues(List<Tuple> tuples, AttributeRef attribute) {
        IValue first = tuples.get(0).getCell(attribute).getValue();
        for (Tuple tuple : tuples) {
            IValue value = tuple.getCell(attribute).getValue();
            if (!value.equals(first)) {
                return false;
            }
        }
        return true;
    }

    public String getName() {
        return "value";
    }

    public AttributeRef getAttributeRef() {
        return attributeRef;
    }

    public String toString() {
        return attributeRef.toString();
    }
    
    
}
