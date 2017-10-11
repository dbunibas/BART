package bart.comparison.operators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bart.comparison.ComparisonConfiguration;
import bart.comparison.ComparisonStats;
import bart.comparison.TupleMapping;
import bart.comparison.TupleMatch;
import bart.comparison.TupleMatches;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;
import speedy.utility.combinatorics.GenericListGeneratorIterator;
import speedy.utility.combinatorics.GenericPowersetGenerator;
import speedy.utility.combinatorics.GenericSizeOnePowersetGenerator;

public class FindBestTupleMapping {

    private final static Logger logger = LoggerFactory.getLogger(FindBestTupleMapping.class);
    private final CheckTupleMatchCompatibility compatibilityChecker = new CheckTupleMatchCompatibility();
    private final ComputeScore scoreCalculator = new ComputeScore();
    private final GenericSizeOnePowersetGenerator<TupleMatch> sizeOnePowersetsGenerator = new GenericSizeOnePowersetGenerator<TupleMatch>();

    public TupleMapping findBestTupleMapping(List<TupleWithTable> sourceTuples, List<TupleWithTable> destinationTuples, TupleMatches tupleMatches) {
        long start = System.currentTimeMillis();
        try {
            if (ComparisonConfiguration.isFunctional()) {
                return findBestFunctionalTupleMapping(sourceTuples, destinationTuples, tupleMatches);
            }
            return findBestNonFunctionalTupleMapping(sourceTuples, destinationTuples, tupleMatches);
        } finally {
            ComparisonStats.getInstance().addStat(ComparisonStats.FIND_BEST_TUPLE_MAPPING_TIME, System.currentTimeMillis() - start);
        }
    }

    private TupleMapping findBestFunctionalTupleMapping(List<TupleWithTable> sourceTuples, List<TupleWithTable> destinationTuples, TupleMatches tupleMatches) {
        if (logger.isDebugEnabled()) logger.debug("Finding best functional tuple mapping for\n" + SpeedyUtility.printCollection(sourceTuples) + "\n with " + tupleMatches);
        List<List<TupleMatch>> allTupleMatches = createListOfLists(sourceTuples, tupleMatches);
        if (logger.isDebugEnabled()) logger.debug("All tuple matches:\n" + SpeedyUtility.printCollection(allTupleMatches));
        GenericListGeneratorIterator<TupleMatch> iterator = new GenericListGeneratorIterator<TupleMatch>(allTupleMatches);
        TupleMapping bestTupleMapping = null;
        double bestScore = 0;
        int combinationNumber = 0;
        if (logger.isInfoEnabled()) logger.info("# Number of combination to evaluate: " + iterator.numberOfCombination());
        while (iterator.hasNext()) {
            if (logger.isInfoEnabled()) logger.info("# Evaluating combination " + ++combinationNumber);
            List<TupleMatch> combination = iterator.next();
            TupleMapping bestTupleMappingInPowerset = findBestPowerset(sourceTuples, destinationTuples, combination);
            if (bestTupleMappingInPowerset == null) {
                continue;
            }
            if (bestTupleMappingInPowerset.getScore() > bestScore) {
                bestScore = bestTupleMappingInPowerset.getScore();
                bestTupleMapping = bestTupleMappingInPowerset;
                if (logger.isDebugEnabled()) logger.debug("Found new best score: " + bestScore);
            }
            if (bestScore >= ComparisonConfiguration.getBestScoreThreshold()) {
                return bestTupleMapping;
            }
        }
        return bestTupleMapping;
    }

    private TupleMapping findBestNonFunctionalTupleMapping(List<TupleWithTable> sourceTuples, List<TupleWithTable> destinationTuples, TupleMatches tupleMatches) {
        if (logger.isDebugEnabled()) logger.debug("Finding best non functional tuple mapping for\n" + SpeedyUtility.printCollection(sourceTuples) + "\n with " + tupleMatches);
        List<TupleMatch> allTupleMatches = mergeTupleMatches(sourceTuples, tupleMatches);
        if (logger.isDebugEnabled()) logger.debug("All tuple matches:\n" + SpeedyUtility.printCollection(allTupleMatches));
        if (logger.isInfoEnabled()) logger.info("Finding best non functional tuple mapping for matches\n" + SpeedyUtility.printCollection(allTupleMatches));
        return findBestPowerset(sourceTuples, destinationTuples, allTupleMatches);
    }

    private TupleMapping findBestPowerset(List<TupleWithTable> sourceTuples, List<TupleWithTable> destinationTuples, List<TupleMatch> matches) {
        if (ComparisonConfiguration.isForceExaustiveSearch()) {
            return findBestPowersetExaustive(sourceTuples, destinationTuples, matches);
        } else {
            return findBestPowersetGreedy(sourceTuples, destinationTuples, matches);
        }
    }

    private TupleMapping findBestPowersetExaustive(List<TupleWithTable> sourceTuples, List<TupleWithTable> destinationTuples, List<TupleMatch> matches) {
        double bestScore = 0;
        TupleMapping bestTupleMapping = null;
        if (logger.isInfoEnabled()) logger.info("Generating powerset for " + matches.size() + " elements");
        GenericPowersetGenerator<TupleMatch> powersetGenerator = new GenericPowersetGenerator<TupleMatch>(matches);
        if (logger.isInfoEnabled()) logger.info("## Evaluating " + powersetGenerator.numberOfPowersets() + " powersets");
        int powersetNumber = 0;
        while (powersetGenerator.hasNext()) {
            List<TupleMatch> candidateTupleMatches = powersetGenerator.next();
            if (logger.isInfoEnabled()) logger.info("## Evaluating powerset " + ++powersetNumber);
            if (logger.isDebugEnabled()) logger.debug("Candidate tuple matches: " + candidateTupleMatches);
            TupleMapping nextTupleMapping = extractTupleMapping(candidateTupleMatches);
            if (logger.isDebugEnabled()) logger.debug("Candidate tuple mapping: " + nextTupleMapping);
            if (nextTupleMapping == null) {
                if (logger.isDebugEnabled()) logger.debug("Candidate match discarded...");
                continue;
            }
            double similarityScore = scoreCalculator.computeScore(sourceTuples, destinationTuples, nextTupleMapping);
            if (logger.isInfoEnabled()) logger.info("Mapping score: " + similarityScore);
            nextTupleMapping.setScore(similarityScore);
            if (similarityScore > bestScore) {
                bestScore = similarityScore;
                bestTupleMapping = nextTupleMapping;
                if (logger.isDebugEnabled()) logger.debug("Found new best score: " + similarityScore);
            }
            if (bestScore >= ComparisonConfiguration.getBestScoreThreshold()) {
                return bestTupleMapping;
            }
        }
        return bestTupleMapping;
    }

    private TupleMapping findBestPowersetGreedy(List<TupleWithTable> sourceTuples, List<TupleWithTable> destinationTuples, List<TupleMatch> matches) {
        TupleMapping completeTupleMapping = extractTupleMapping(matches); //Checking the entire list.
        if (completeTupleMapping != null) { //If exists, this is the best mapping
            double similarityScore = scoreCalculator.computeScore(sourceTuples, destinationTuples, completeTupleMapping);
            completeTupleMapping.setScore(similarityScore);
            return completeTupleMapping;
        }
        if (logger.isInfoEnabled()) logger.info("Generating permutations of size one for " + matches.size() + " elements");
        List<List<TupleMatch>> powersets = sizeOnePowersetsGenerator.generatePermutationsOfSizeOne(matches);//Else checking powerset of size one
        if (logger.isInfoEnabled()) logger.info("## Evaluating " + powersets.size() + " powersets");
        double bestScore = 0;
        TupleMapping bestTupleMapping = null;
        for (List<TupleMatch> powerset : powersets) {
            TupleMapping bestMappingForPowerset = findBestPowersetGreedy(sourceTuples, destinationTuples, powerset);
            if (bestMappingForPowerset == null) {
                if (logger.isDebugEnabled()) logger.debug("Candidate match discarded...");
                continue;
            }
            double similarityScore = scoreCalculator.computeScore(sourceTuples, destinationTuples, bestMappingForPowerset);
            if (logger.isDebugEnabled()) logger.debug("Candidate Mapping: " + bestMappingForPowerset);
            bestMappingForPowerset.setScore(similarityScore);
            if (similarityScore > bestScore) {
                bestScore = similarityScore;
                bestTupleMapping = bestMappingForPowerset;
                if (logger.isDebugEnabled()) logger.debug("Found new best score: " + similarityScore);
            }
            if (bestScore >= ComparisonConfiguration.getBestScoreThreshold()) {
                if (logger.isInfoEnabled()) logger.info("- Best score: " + bestScore);
                return bestTupleMapping;
            }
        }
        if (logger.isInfoEnabled()) logger.info("- Best score: " + bestScore);
        return bestTupleMapping;
    }

    public TupleMapping extractTupleMapping(List<TupleMatch> tupleMatches) {
        if (logger.isDebugEnabled()) logger.debug("Extracting mapping using tuple matches:\n" + SpeedyUtility.printCollection(tupleMatches));
        TupleMapping tupleMapping = new TupleMapping();
        for (TupleMatch tupleMatch : tupleMatches) {
            boolean compatible = compatibilityChecker.checkCompatibilityAndMerge(tupleMapping.getValueMappings(), tupleMatch);
            if (!compatible) {
                if (logger.isDebugEnabled()) logger.debug("# Incompatible mapping. Skipping...");
                return null;
            }
            tupleMapping.putTupleMapping(tupleMatch.getLeftTuple(), tupleMatch.getRightTuple());
        }
        if (ComparisonConfiguration.isInjective() && !isRightInjective(tupleMapping)) {
            if (logger.isDebugEnabled()) logger.debug("# Mapping non injective. Skipping...");
            return null;
        }
        return tupleMapping;
    }

    private boolean isRightInjective(TupleMapping tupleMapping) {
        List<TupleWithTable> imageTuples = tupleMapping.getRightValues();
        Set<TupleWithTable> imageTupleSet = new HashSet<TupleWithTable>(imageTuples);
        return imageTuples.size() == imageTupleSet.size();
    }

    private List<List<TupleMatch>> createListOfLists(List<TupleWithTable> sourceTuples, TupleMatches tupleMatches) {
        List<List<TupleMatch>> allTupleMatches = new ArrayList<List<TupleMatch>>();
        for (TupleWithTable sourceTuple : sourceTuples) {
            List<TupleMatch> tupleMatchesForTuple = tupleMatches.getMatchesForTuple(sourceTuple);
            if (tupleMatchesForTuple == null || tupleMatchesForTuple.isEmpty()) {
                continue;
            }
            allTupleMatches.add(tupleMatchesForTuple);
        }
        return allTupleMatches;
    }

    private List<TupleMatch> mergeTupleMatches(List<TupleWithTable> sourceTuples, TupleMatches tupleMatches) {
        List<TupleMatch> result = new ArrayList<TupleMatch>();
        for (TupleWithTable sourceTuple : sourceTuples) {
            List<TupleMatch> tupleMatchesForTuple = tupleMatches.getMatchesForTuple(sourceTuple);
            if (tupleMatchesForTuple == null || tupleMatchesForTuple.isEmpty()) {
                continue;
            }
            result.addAll(tupleMatchesForTuple);
        }
        return result;
    }
}
