package bart.comparison;

import bart.comparison.operators.CheckTupleMatch;
import bart.comparison.operators.TupleMatchComparatorScore;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleWithTable;

public class ComparisonUtility {

    private final static Logger logger = LoggerFactory.getLogger(ComparisonUtility.class);
    private final static CheckTupleMatch tupleMatcher = new CheckTupleMatch();

    public static Set<AttributeRef> findAttributesWithGroundValue(Tuple tuple) {
        Set<AttributeRef> attributes = new HashSet<AttributeRef>();
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttribute().equals(SpeedyConstants.OID)) {
                continue;
            }
            if (!(cell.getValue() instanceof ConstantValue)) {
                continue;
            }
            attributes.add(cell.getAttributeRef());
        }
        return attributes;
    }

    public static void sortTupleMatches(TupleMatches tupleMatches) {
        for (TupleWithTable tuple : tupleMatches.getTuples()) {
            List<TupleMatch> matchesForTuple = tupleMatches.getMatchesForTuple(tuple);
            Collections.sort(matchesForTuple, new TupleMatchComparatorScore());
        }
    }

    public static boolean isCompatible(Set<AttributeRef> attributesWithGroundValues, List<AttributeRef> attributes) {
        return attributesWithGroundValues.containsAll(attributes);
    }

    public static TupleMatches findTupleMatches(List<TupleWithTable> secondDB, CompatibilityMap compatibilityMap) {
        long start = System.currentTimeMillis();
        TupleMatches tupleMatches = new TupleMatches();
        for (TupleWithTable secondTuple : secondDB) {
            //We associate, for each tuple, a list of compatible destination tuples (i.e. they don't have different constants)
            for (TupleWithTable destinationTuple : compatibilityMap.getCompatibleTuples(secondTuple)) {
                TupleMatch match = tupleMatcher.checkMatch(destinationTuple, secondTuple);
                if (match != null) {
                    if (logger.isDebugEnabled()) logger.debug("Match found: " + match);
                    tupleMatches.addTupleMatch(destinationTuple, match);
                }
            }
        }
        ComparisonStats.getInstance().addStat(ComparisonStats.FIND_TUPLE_MATCHES, System.currentTimeMillis() - start);
        return tupleMatches;
    }
}
