package bart.comparison;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import speedy.model.database.AttributeRef;
import speedy.model.database.IValue;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class AttributeValueMap {

    private final Map<AttributeRef, ValueTupleMap> attributeValueMap = new HashMap<AttributeRef, ValueTupleMap>();

    public void addValueMapForAttribute(AttributeRef attribute, IValue value, TupleWithTable tuple) {
        ValueTupleMap valueMap = attributeValueMap.get(attribute);
        if (valueMap == null) {
            valueMap = new ValueTupleMap();
            attributeValueMap.put(attribute, valueMap);
        }
        valueMap.addTuple(value, tuple);
    }

    @SuppressWarnings("unchecked")
    public Set<TupleWithTable> getTuplesWithValue(AttributeRef attribute, IValue value) {
        ValueTupleMap valueMap = attributeValueMap.get(attribute);
        if (valueMap == null) {
            return Collections.EMPTY_SET;
        }
        Set<TupleWithTable> result = valueMap.getTuples(value);
        if (result == null) {
            result = new HashSet<TupleWithTable>();
        }
        return result;
    }

    public ValueTupleMap getValueMapForAttribute(AttributeRef attribute) {
        return attributeValueMap.get(attribute);
    }

    @Override
    public String toString() {
        return SpeedyUtility.printMap(attributeValueMap);
    }

}
