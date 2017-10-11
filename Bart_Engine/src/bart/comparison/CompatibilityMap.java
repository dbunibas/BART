package bart.comparison;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class CompatibilityMap {

    private final Map<TupleWithTable, Set<TupleWithTable>> compatibilities = new HashMap<TupleWithTable, Set<TupleWithTable>>();

    public void setCompatibilityForTuple(TupleWithTable tuple, Set<TupleWithTable> compatibleTuples) {
        compatibilities.put(tuple, compatibleTuples);
    }

    public Set<TupleWithTable> getCompatibleTuples(TupleWithTable tuple) {
        return compatibilities.get(tuple);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (TupleWithTable tuple : compatibilities.keySet()) {
            sb.append("Tuple: ").append(tuple).append(" -> [\n");
            sb.append(SpeedyUtility.printCollection(compatibilities.get(tuple), "\t"));
            sb.append("\n]\n");
        }
        return sb.toString();
    }

}
