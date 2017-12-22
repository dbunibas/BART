package bart.comparison.operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bart.comparison.ComparisonConfiguration;
import bart.comparison.ComparisonStats;
import bart.comparison.ComparisonUtility;
import bart.comparison.CompatibilityMap;
import bart.comparison.InstanceMatchTask;
import bart.comparison.SignatureAttributes;
import bart.comparison.SignatureMap;
import bart.comparison.SignatureMapCollection;
import bart.comparison.TupleMapping;
import bart.comparison.TupleMatch;
import bart.comparison.TupleMatches;
import bart.comparison.TupleSignature;
import bart.comparison.ValueMapping;
import speedy.model.database.AttributeRef;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class ComputeInstanceSimilarityHashing implements IComputeInstanceSimilarity {

    private final static Logger logger = LoggerFactory.getLogger(ComputeInstanceSimilarityHashing.class);
    private final SignatureMapCollectionGenerator signatureGenerator = new SignatureMapCollectionGenerator();
    private final CheckTupleMatch tupleMatcher = new CheckTupleMatch();
    private final CheckTupleMatchCompatibility compatibilityChecker = new CheckTupleMatchCompatibility();
    private final FindCompatibleTuples compatibleTupleFinder = new FindCompatibleTuples();
    private final ComputeScore scoreCalculator = new ComputeScore();
//    private final FindNonMatchingTuples nonMatchingTuplesFinder = new FindNonMatchingTuples();

    @Override
    public InstanceMatchTask compare(IDatabase leftDb, IDatabase rightDb) {
        InstanceMatchTask instanceMatch = new InstanceMatchTask(this.getClass().getSimpleName(), leftDb, rightDb);
        long start = System.currentTimeMillis();
        List<TupleWithTable> leftTuples = SpeedyUtility.extractAllTuplesFromDatabase(leftDb);
        List<TupleWithTable> rightTuples = SpeedyUtility.extractAllTuplesFromDatabase(rightDb);
        ComparisonStats.getInstance().addStat(ComparisonStats.PROCESS_INSTANCE_TIME, System.currentTimeMillis() - start);
        SignatureMapCollection leftSignatureMapCollection = signatureGenerator.generateIndexForTuples(leftTuples);
        if (logger.isDebugEnabled()) logger.debug("Left Signature Map Collection:\n" + leftSignatureMapCollection);
        List<TupleWithTable> remainingRightTuples = new ArrayList<TupleWithTable>();
        TupleMapping ltrMapping = new TupleMapping();
        findMapping(ltrMapping, leftSignatureMapCollection, rightTuples, remainingRightTuples, null, false);
        if (logger.isDebugEnabled()) logger.debug("LTR Mapping:\n" + ltrMapping);
        findRTLMapping(ltrMapping, leftSignatureMapCollection, rightTuples, remainingRightTuples);
        findRemainingMatches(ltrMapping, leftTuples, rightTuples);
        instanceMatch.setTupleMapping(ltrMapping);
//        nonMatchingTuplesFinder.find(leftTuples, rightTuples, instanceMatch.getTupleMapping()); //Non matching tuples are already maintained during the algorithm
        double similarityScore = scoreCalculator.computeScore(leftTuples, rightTuples, instanceMatch.getTupleMapping());
        instanceMatch.getTupleMapping().setScore(similarityScore);
        long end = System.currentTimeMillis();
        if (logger.isInfoEnabled()) logger.info("** Total time:" + (end - start) + " ms");
        return instanceMatch;
    }

    private void findMapping(TupleMapping tupleMapping, SignatureMapCollection srcSignatureMap, List<TupleWithTable> destTuples,
            List<TupleWithTable> extraDestTuples, List<TupleWithTable> extraSrcTuples, boolean maintainSrcTuples) {
        long start = System.currentTimeMillis();
        for (TupleWithTable destTuple : destTuples) {
            if (logger.isDebugEnabled()) logger.debug("Finding a tuple that can be mapped in " + destTuple);
            List<SignatureAttributes> signatureAttributesForTable = srcSignatureMap.getRankedAttributesForTable(destTuple.getTable());
            if (logger.isDebugEnabled()) logger.debug("Signature for table " + destTuple.getTable() + ": " + signatureAttributesForTable);
            List<TupleMatch> matchingTuples = findSignatureBasedMatches(destTuple, signatureAttributesForTable, srcSignatureMap, tupleMapping);
            if (logger.isDebugEnabled()) logger.debug("Possible matching tuples: " + matchingTuples);
            if (matchingTuples.isEmpty()) {
                if (logger.isDebugEnabled()) logger.debug("Extra tuple in dest instance: " + destTuple);
                extraDestTuples.add(destTuple);
                continue;
            }
            for (TupleMatch matchingTuple : matchingTuples) {
                tupleMapping.putTupleMapping(matchingTuple.getLeftTuple(), matchingTuple.getRightTuple());
                if (maintainSrcTuples) {
                    extraSrcTuples.remove(matchingTuple.getLeftTuple());
                }
            }
        }
        long end = System.currentTimeMillis();
        if (logger.isInfoEnabled()) logger.info("Finding mapping time:" + (end - start) + " ms");
    }

    private List<TupleMatch> findSignatureBasedMatches(TupleWithTable destinationTuple, List<SignatureAttributes> signatureAttributesForTable,
            SignatureMapCollection leftSignatureMaps, TupleMapping tupleMapping) {
        List<TupleMatch> matchingTuples = new ArrayList<TupleMatch>();
        Set<AttributeRef> attributesWithGroundValues = ComparisonUtility.findAttributesWithGroundValue(destinationTuple.getTuple());
        for (SignatureAttributes signatureAttribute : signatureAttributesForTable) {
            if (logger.isTraceEnabled()) logger.trace("Checking signature attribute " + signatureAttribute);
            if (!ComparisonUtility.isCompatible(attributesWithGroundValues, signatureAttribute.getAttributes())) {
                if (logger.isTraceEnabled()) logger.trace("Skipping not compatible signature attribute " + signatureAttribute);
                continue;
            }
            SignatureMap signatureMap = leftSignatureMaps.getSignatureForAttributes(signatureAttribute);
            TupleSignature rightTupleSignature = signatureGenerator.generateSignature(destinationTuple, signatureAttribute.getAttributes());
            List<Tuple> tuplesWithSameSignature = signatureMap.getTuplesForSignature(rightTupleSignature.getSignature());
            if (tuplesWithSameSignature == null || tuplesWithSameSignature.isEmpty()) {
                continue;
            }
            for (Iterator<Tuple> it = tuplesWithSameSignature.iterator(); it.hasNext();) {
                Tuple srcTuple = it.next();
                TupleWithTable srcTupleWithTable = new TupleWithTable(destinationTuple.getTable(), srcTuple);
                TupleMatch tupleMatch = tupleMatcher.checkMatch(srcTupleWithTable, destinationTuple);
                if (tupleMatch == null) {
                    continue;
                }
                boolean compatible = compatibilityChecker.checkCompatibilityAndMerge(tupleMapping.getValueMappings(), tupleMatch);
                if (!compatible) {
                    continue;
                }
                matchingTuples.add(tupleMatch);
                if (ComparisonConfiguration.isFunctional()) {
                    it.remove();
                }
                if (ComparisonConfiguration.isInjective()) {
                    return matchingTuples;
                }
            }
        }
        return matchingTuples;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    ////////      RIGHT TO LEFT
    //////////////////////////////////////////////////////////////////////////////////////////
    private void findRTLMapping(TupleMapping ltrMapping, SignatureMapCollection leftSignatureMapCollection, List<TupleWithTable> rightTuples, List<TupleWithTable> remainingRightTuples) {
        if (logger.isDebugEnabled()) logger.debug("Finding RTL Mapping...");
        Map<TupleWithTable, TupleWithTable> renamedTupleMap = new HashMap<TupleWithTable, TupleWithTable>();
        List<TupleWithTable> leftTuplesToMatch = findLeftTuplesToMatch(ltrMapping, leftSignatureMapCollection, renamedTupleMap);
        if (logger.isDebugEnabled()) logger.debug("Left tuples to match:\n" + SpeedyUtility.printCollection(leftTuplesToMatch));
        List<TupleWithTable> rightTuplesToMatch = findRightTuplesToMatch(rightTuples, remainingRightTuples);
        if (logger.isDebugEnabled()) logger.debug("Right tuples to match:\n" + SpeedyUtility.printCollection(rightTuplesToMatch));
        SignatureMapCollection rightSignatureMapCollection = signatureGenerator.generateIndexForTuples(rightTuplesToMatch);
        if (logger.isDebugEnabled()) logger.debug("Right Signature Map Collection:\n" + leftSignatureMapCollection);
        List<TupleWithTable> remainingLeftTuples = new ArrayList<TupleWithTable>();
        remainingLeftTuples.addAll(leftSignatureMapCollection.getTuplesWithoutGroundValues());
        TupleMapping rtlMapping = new TupleMapping();
        rtlMapping.getValueMappings().setRightToLeftValueMapping(ltrMapping.getValueMappings().getLeftToRightValueMapping());
        rtlMapping.getValueMappings().setLeftToRightValueMapping(ltrMapping.getValueMappings().getRightToLeftValueMapping());
        findMapping(rtlMapping, rightSignatureMapCollection, leftTuplesToMatch, remainingLeftTuples, remainingRightTuples, true);
        if (logger.isDebugEnabled()) logger.debug("RTL Mapping:\n" + rtlMapping);
        mergeMappings(ltrMapping, rtlMapping, renamedTupleMap);
        ltrMapping.setLeftNonMatchingTuples(remainingLeftTuples);
        ltrMapping.setRightNonMatchingTuples(remainingRightTuples);
    }

    private List<TupleWithTable> findLeftTuplesToMatch(TupleMapping tupleMapping, SignatureMapCollection leftSignatureMapCollection,
            Map<TupleWithTable, TupleWithTable> renamedTupleMap) {
        List<TupleWithTable> originalRemainingLeftTuples = collectRemainingTuples(leftSignatureMapCollection);
        List<TupleWithTable> renamedLeftTuples = new ArrayList<TupleWithTable>();
        for (TupleWithTable originalLeftTuple : originalRemainingLeftTuples) {
            TupleWithTable renamedTuple = applyValueMapping(originalLeftTuple, tupleMapping.getLeftToRightValueMapping());
            renamedLeftTuples.add(renamedTuple);
            renamedTupleMap.put(renamedTuple, originalLeftTuple);
        }
        return renamedLeftTuples;
    }

    private TupleWithTable applyValueMapping(TupleWithTable originalTuple, ValueMapping valueMapping) {
        return originalTuple;
        //Hard renaming tuples is not needed, since the renaming is handled passing the ltrValueMapping
//        Tuple tuple = new Tuple(originalTuple.getTuple().getOid());
//        TupleWithTable renamedTuple = new TupleWithTable(originalTuple.getTable(), tuple);
//        for (Cell cell : originalTuple.getTuple().getCells()) {
//            if (cell.getAttribute().equals(SpeedyConstants.OID)) {
//                tuple.addCell(cell);
//                continue;
//            }
//            IValue originalValue = cell.getValue();
//            IValue newValue = valueMapping.getValueMapping(originalValue);
//            if (newValue == null) {
//                tuple.addCell(cell);
//                continue;
//            }
//            tuple.addCell(new Cell(cell, newValue));
//        }
//        return renamedTuple;
    }

    private List<TupleWithTable> findRightTuplesToMatch(List<TupleWithTable> rightTuples, List<TupleWithTable> remainingRightTuples) {
        if (ComparisonConfiguration.isInjective()) {
            return remainingRightTuples;
        } else {
            return rightTuples;
        }
    }

    private List<TupleWithTable> collectRemainingTuples(SignatureMapCollection signatureMapCollection) {
        List<TupleWithTable> result = new ArrayList<TupleWithTable>();
        for (SignatureMap signatureMap : signatureMapCollection.getSignatureMaps()) {
            String tableName = signatureMap.getSignatureAttribute().getTableName();
            for (List<Tuple> tuples : signatureMap.getIndex().values()) {
                for (Tuple tuple : tuples) {
                    result.add(new TupleWithTable(tableName, tuple));
                }
            }
        }
        return result;
    }

    private void mergeMappings(TupleMapping ltrMapping, TupleMapping rtlMapping, Map<TupleWithTable, TupleWithTable> renamedTupleMap) {
        for (TupleWithTable rightTuple : rtlMapping.getTupleMapping().keySet()) {
            TupleWithTable leftTuple = rtlMapping.getFirstMappingForTuple(rightTuple);
            TupleWithTable originalLeftTuple = renamedTupleMap.get(leftTuple);
            ltrMapping.putTupleMapping(originalLeftTuple, rightTuple);
        }
        for (IValue rightValue : rtlMapping.getLeftToRightValueMapping().getKeys()) {
            IValue leftValue = rtlMapping.getLeftToRightMappingForValue(rightValue);//TODO++ leftValue may be a renamed constant
            ltrMapping.addRightToLeftMappingForValue(rightValue, leftValue);
        }
    }

    private void findRemainingMatches(TupleMapping ltrMapping, List<TupleWithTable> leftDB, List<TupleWithTable> rightDB) {
        List<TupleWithTable> leftTuples;
        if (ComparisonConfiguration.isFunctional()) {
            leftTuples = ltrMapping.getLeftNonMatchingTuples();
        } else {
            leftTuples = leftDB;
        }
        List<TupleWithTable> rightTuples;
        if (ComparisonConfiguration.isInjective()) {
            rightTuples = ltrMapping.getRightNonMatchingTuples();
        } else {
            rightTuples = rightDB;
        }
        if (leftTuples.isEmpty() || rightTuples.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("Empty tuples: Left " + leftTuples.size() + " - Right " + rightTuples.size());
            return;
        }
        long start = System.currentTimeMillis();
        if (logger.isDebugEnabled()) logger.debug("Finding remaining matches\n* Left tuples: \n" + SpeedyUtility.printCollection(leftTuples, "\t") + "\n* Right tuples: \n" + SpeedyUtility.printCollection(rightTuples, "\t"));
        if (logger.isDebugEnabled()) logger.debug("Current Mapping: \n" + ltrMapping);
        CompatibilityMap compatibilityMap = compatibleTupleFinder.find(leftTuples, rightTuples);
        if (logger.isDebugEnabled()) logger.debug("Compatibility map:\n" + compatibilityMap);
        TupleMatches remainingTupleMatches = findTupleMatches(rightTuples, compatibilityMap);
        if (logger.isDebugEnabled()) logger.debug("Matches btw Remaining Tuples:\n" + remainingTupleMatches);
        addRemainingTupleMatches(rightTuples, remainingTupleMatches, ltrMapping);
        ComparisonStats.getInstance().addStat(ComparisonStats.FIND_REMAINING_MATCHES_TIME, System.currentTimeMillis() - start);
    }

    private TupleMatches findTupleMatches(List<TupleWithTable> rightDB, CompatibilityMap compatibilityMap) {
        Set<TupleWithTable> matchedLeftTuples = new HashSet<TupleWithTable>();
        TupleMatches tupleMatches = new TupleMatches();
        for (TupleWithTable rightTuple : rightDB) {
            //We associate, for each target tuple, a compatible set of source tuples
            for (TupleWithTable leftTuples : compatibilityMap.getCompatibleTuples(rightTuple)) {
                if (matchedLeftTuples.contains(leftTuples)) {
                    continue;
                }
                TupleMatch match = tupleMatcher.checkMatch(leftTuples, rightTuple);
                if (match == null) {
                    continue;
                }
                if (logger.isDebugEnabled()) logger.debug("Match found: " + match);
                tupleMatches.addTupleMatch(rightTuple, match);
                if (ComparisonConfiguration.isFunctional()) {
                    matchedLeftTuples.add(leftTuples);
                }
                if (ComparisonConfiguration.isInjective()) {
                    break;
                }
            }
        }
        return tupleMatches;
    }

    private void addRemainingTupleMatches(List<TupleWithTable> secondDB, TupleMatches remainingTupleMatches, TupleMapping ltrMapping) {
        List<TupleWithTable> tuples = new ArrayList<TupleWithTable>(secondDB);
        for (TupleWithTable secondTuple : tuples) {
            List<TupleMatch> matchesForTuple = remainingTupleMatches.getMatchesForTuple(secondTuple);
            if (matchesForTuple == null) {
                continue;
            }
            for (TupleMatch tupleMatch : matchesForTuple) {
                if (logger.isDebugEnabled()) logger.debug("Adding remaining match " + tupleMatch + " in mapping\n" + ltrMapping);
                boolean compatible = compatibilityChecker.checkCompatibilityAndMerge(ltrMapping.getValueMappings(), tupleMatch);
                if (!compatible) {
                    if (logger.isDebugEnabled()) logger.debug("Incompatible match");
                    continue;
                }
                ltrMapping.putTupleMapping(tupleMatch.getLeftTuple(), tupleMatch.getRightTuple());
                ltrMapping.getLeftNonMatchingTuples().remove(tupleMatch.getLeftTuple());
                ltrMapping.getRightNonMatchingTuples().remove(tupleMatch.getRightTuple());
                if (logger.isDebugEnabled()) logger.debug("New match added. Resulting mapping\n" + ltrMapping);
                if (ComparisonConfiguration.isInjective()) {
                    break;
                }
            }
        }
    }

}
