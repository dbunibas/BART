package bart.comparison;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import speedy.model.database.AttributeRef;
import speedy.model.database.IValue;
import speedy.model.database.TupleWithTable;

public class CompatibilityCache {

    private Map<AttributeWithValue, Set<TupleWithTable>> cache = new HashMap<AttributeWithValue, Set<TupleWithTable>>();

    public Set<TupleWithTable> getCompatibilitiesForValue(AttributeRef attribute, IValue value) {
        return cache.get(new AttributeWithValue(attribute, value));
    }

    public void addCompatibilitiesForValue(AttributeRef attribute, IValue value, Set<TupleWithTable> compatibilities) {
        cache.put(new AttributeWithValue(attribute, value), compatibilities);
    }

    class AttributeWithValue {

        public AttributeWithValue(AttributeRef attribute, IValue value) {
            this.attribute = attribute;
            this.value = value;
        }

        AttributeRef attribute;
        IValue value;

        @Override
        public String toString() {
            return attribute.toString() + "-" + value.toString();
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this.toString().equals(obj.toString());
        }

    }

}
