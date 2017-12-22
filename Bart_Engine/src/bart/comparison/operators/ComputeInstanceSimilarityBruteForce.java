package bart.comparison.operators;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bart.comparison.ComparisonStats;
import bart.comparison.ComparisonUtility;
import bart.comparison.TupleMapping;
import bart.comparison.InstanceMatchTask;
import bart.comparison.TupleMatch;
import bart.comparison.TupleMatches;
import speedy.model.database.IDatabase;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class ComputeInstanceSimilarityBruteForce implements IComputeInstanceSimilarity {
    
    private final static Logger logger = LoggerFactory.getLogger(ComputeInstanceSimilarityBruteForce.class);
    private final CheckTupleMatch tupleMatcher = new CheckTupleMatch();
    private final FindBestTupleMapping bestTupleMappingFinder = new FindBestTupleMapping();
    private final FindNonMatchingTuples nonMatchingTuplesFinder = new FindNonMatchingTuples();
    
    public InstanceMatchTask compare(IDatabase leftDb, IDatabase rightDb) {
        long start = System.currentTimeMillis();
        InstanceMatchTask instanceMatch = new InstanceMatchTask(this.getClass().getSimpleName(), leftDb, rightDb);
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabase(leftDb);
        List<TupleWithTable> destinationTuples = SpeedyUtility.extractAllTuplesFromDatabase(rightDb);
        ComparisonStats.getInstance().addStat(ComparisonStats.PROCESS_INSTANCE_TIME, System.currentTimeMillis() - start);
        TupleMatches tupleMatches = findTupleMatches(sourceTuples, destinationTuples);
        ComparisonUtility.sortTupleMatches(tupleMatches);
        if (logger.isTraceEnabled()) logger.trace(tupleMatches.toString());
        TupleMapping bestTupleMapping = bestTupleMappingFinder.findBestTupleMapping(sourceTuples, destinationTuples, tupleMatches);
        nonMatchingTuplesFinder.find(sourceTuples, destinationTuples, bestTupleMapping);
        instanceMatch.setTupleMapping(bestTupleMapping);
        return instanceMatch;
    }
    
    private TupleMatches findTupleMatches(List<TupleWithTable> sourceTuples, List<TupleWithTable> destinationTuples) {
        long start = System.currentTimeMillis();
        TupleMatches tupleMatches = new TupleMatches();
        for (TupleWithTable sourceTuple : sourceTuples) {
            //We associate, for each source tuple, a list of compatible destination tuples (i.e. they don't have different constants)
            for (TupleWithTable destinationTuple : destinationTuples) {
                TupleMatch match = tupleMatcher.checkMatch(sourceTuple, destinationTuple);
                if (match != null) {
                    if (logger.isDebugEnabled()) logger.debug("Match found: " + match);
                    tupleMatches.addTupleMatch(sourceTuple, match);
                }
            }
            List<TupleMatch> matchesForTuple = tupleMatches.getMatchesForTuple(sourceTuple);
            if (matchesForTuple == null) {
                if (logger.isDebugEnabled()) logger.debug("Non matching tuple: " + sourceTuple);
                tupleMatches.addNonMatchingTuple(sourceTuple);
            }
        }
        ComparisonStats.getInstance().addStat(ComparisonStats.FIND_TUPLE_MATCHES, System.currentTimeMillis() - start);
        return tupleMatches;
    }
    
}
