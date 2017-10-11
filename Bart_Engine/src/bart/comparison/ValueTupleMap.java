package bart.comparison;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import speedy.SpeedyConstants;
import speedy.model.database.IValue;
import speedy.model.database.TupleWithTable;

public class ValueTupleMap {

    private final Map<IValue, Set<TupleWithTable>> valueMap = new HashMap<IValue, Set<TupleWithTable>>();

    public void addTuple(IValue value, TupleWithTable tuple) {
        Set<TupleWithTable> result = valueMap.get(value);
        if (result == null) {
            result = new HashSet<TupleWithTable>();
            valueMap.put(value, result);
        }
        result.add(tuple);
    }

    public Set<TupleWithTable> getTuples(IValue value) {
        return valueMap.get(value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IValue value : valueMap.keySet()) {
            sb.append("Value: ").append(value).append(": ").append(valueMap.get(value)).append("\n").append(SpeedyConstants.INDENT);
        }
        return sb.toString();
    }

}
