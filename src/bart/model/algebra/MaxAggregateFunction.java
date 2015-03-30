package bart.model.algebra;

import bart.BartConstants;
import bart.exceptions.AlgebraException;
import bart.model.database.AttributeRef;
import bart.model.database.IValue;
import bart.model.database.Tuple;
import bart.model.database.NullValue;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MaxAggregateFunction implements IAggregateFunction {
    
    private AttributeRef attributeRef;

    public MaxAggregateFunction(AttributeRef attributeRef) {
        this.attributeRef = attributeRef;
    }

    public IValue evaluate(List<Tuple> tuples) {
        if (tuples.isEmpty()) {
            return new NullValue(BartConstants.NULL_VALUE);
        }
        Collections.sort(tuples, new TupleComparatorAttributeValue(attributeRef));
        return tuples.get(0).getCell(attributeRef).getValue();        
    }

    public String getName() {
        return "max";
    }
    
    public String toString() {
        return "max(" + attributeRef + ") as " + attributeRef.getName();
    }

    public AttributeRef getAttributeRef() {
        return attributeRef;
    }

}
class TupleComparatorAttributeValue implements Comparator<Tuple> {
    
    private AttributeRef attribute;

    public TupleComparatorAttributeValue(AttributeRef attribute) {
        this.attribute = attribute;
    }    

    public int compare(Tuple t1, Tuple t2) {
        if (t1.getCell(attribute) == null || t2.getCell(attribute) == null) {
            throw new AlgebraException("Unable to find attribute " + attribute + " in tuples " + t1 + " - " + t2);
        }
        IValue t1Value = t1.getCell(attribute).getValue();
        IValue t2Value = t2.getCell(attribute).getValue();
        return t2Value.toString().compareTo(t1Value.toString());
    }
}
