package bart.model.algebra;

import bart.model.database.AttributeRef;
import bart.model.database.ConstantValue;
import bart.model.database.IValue;
import bart.model.database.Tuple;
import java.util.List;

public class CountAggregateFunction implements IAggregateFunction {

    private AttributeRef attributeRef;

    public CountAggregateFunction(AttributeRef attributeRef) {
        this.attributeRef = attributeRef;
    }

    public IValue evaluate(List<Tuple> tuples) {
        return new ConstantValue(tuples.size());
    }

    public String getName() {
        return "count";
    }

    public String toString() {
        return "count(*)";
    }

    public AttributeRef getAttributeRef() {
        return attributeRef;
    }
}
