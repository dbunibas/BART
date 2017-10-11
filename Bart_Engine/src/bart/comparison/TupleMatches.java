package bart.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class TupleMatches {

    private final Map<TupleWithTable, List<TupleMatch>> tupleMatches = new HashMap<TupleWithTable, List<TupleMatch>>();
    private final List<TupleWithTable> nonMatchingTuples = new ArrayList<TupleWithTable>();

    public void addTupleMatch(TupleWithTable tuple, TupleMatch tupleMatch) {
        List<TupleMatch> matchesForTuple = this.tupleMatches.get(tuple);
        if (matchesForTuple == null) {
            matchesForTuple = new ArrayList<TupleMatch>();
            this.tupleMatches.put(tuple, matchesForTuple);
        }
        matchesForTuple.add(tupleMatch);
    }

    public Set<TupleWithTable> getTuples() {
        return this.tupleMatches.keySet();
    }

    public List<TupleMatch> getMatchesForTuple(TupleWithTable tuple) {
        return this.tupleMatches.get(tuple);
    }

    public List<TupleWithTable> getNonMatchingTuples() {
        return nonMatchingTuples;
    }

    public void addNonMatchingTuple(TupleWithTable tuple) {
        this.nonMatchingTuples.add(tuple);
    }

    public boolean hasNonMatchingTuples() {
        return !this.nonMatchingTuples.isEmpty();
    }

    @Override
    public String toString() {
        return "Tuple Matches [" + SpeedyUtility.printMap(tupleMatches) + "\nNon matching tuples:" + SpeedyUtility.printCollection(nonMatchingTuples) + ']';
    }

}
