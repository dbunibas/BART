package bart.comparison.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.SpeedyConstants.ValueMatchResult;
import bart.comparison.ComparisonConfiguration;
import bart.comparison.TupleMapping;
import bart.comparison.InstanceMatchTask;
import bart.comparison.TupleMatch;
import bart.comparison.TupleMatches;
import bart.comparison.ValueMapping;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;
import speedy.utility.combinatorics.GenericListGeneratorIterator;

public class FindHomomorphism {

    private final static Logger logger = LoggerFactory.getLogger(FindHomomorphism.class);

    public InstanceMatchTask findHomomorphism(IDatabase sourceDb, IDatabase destinationDb) {
        InstanceMatchTask result = new InstanceMatchTask(this.getClass().getSimpleName(), sourceDb, destinationDb);
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabase(sourceDb);
        List<TupleWithTable> destinationTuples = SpeedyUtility.extractAllTuplesFromDatabase(destinationDb);
        TupleMatches tupleMatches = findTupleMatches(sourceTuples, destinationTuples);
        if (tupleMatches.hasNonMatchingTuples()) {
            result.getTupleMapping().setLeftNonMatchingTuples(tupleMatches.getNonMatchingTuples());
            return result;
        }
        sortTupleMatches(tupleMatches);
        if (logger.isTraceEnabled()) logger.trace("Tuple matches: " + tupleMatches);
        List<List<TupleMatch>> allTupleMatches = combineMatches(sourceTuples, tupleMatches);
        if (logger.isDebugEnabled()) logger.debug("Analyzing combinations...");
        GenericListGeneratorIterator<TupleMatch> iterator = new GenericListGeneratorIterator<TupleMatch>(allTupleMatches);
        while (iterator.hasNext()) {
            List<TupleMatch> candidateHomomorphism = iterator.next();
            if (logger.isDebugEnabled()) logger.debug("Analyzing combination: " + candidateHomomorphism);
            TupleMapping homomorphism = checkIfIsHomomorphism(candidateHomomorphism);
            if (homomorphism != null) {
                result.setTupleMapping(homomorphism);
                result.setIsomorphism(checkIsomorphism(result));
                break;
            }
        }
        return result;
    }

    private TupleMatches findTupleMatches(List<TupleWithTable> leftTuples, List<TupleWithTable> rightTuples) {
        TupleMatches tupleMatches = new TupleMatches();
        for (TupleWithTable leftTuple : leftTuples) {
            for (TupleWithTable rightTuple : rightTuples) {
                TupleMatch match = checkMatch(leftTuple, rightTuple);
                if (match != null) {
                    if (logger.isDebugEnabled()) logger.debug("Match found: " + match);
                    tupleMatches.addTupleMatch(leftTuple, match);
                }
            }
            List<TupleMatch> matchesForTuple = tupleMatches.getMatchesForTuple(leftTuple);
            if (matchesForTuple == null) {
                if (logger.isInfoEnabled()) logger.info("Non matching tuple: " + leftTuple);
                tupleMatches.addNonMatchingTuple(leftTuple);
                if (ComparisonConfiguration.isStopIfNonMatchingTuples()) {
                    return tupleMatches;
                }
            }
        }
        return tupleMatches;
    }

    private TupleMatch checkMatch(TupleWithTable sourceTuple, TupleWithTable destinationTuple) {
        if (!sourceTuple.getTable().equals(destinationTuple.getTable())) {
            return null;
        }
        if (logger.isDebugEnabled()) logger.debug("Comparing tuple: " + sourceTuple + " to tuple " + destinationTuple);
        ValueMapping valueMapping = new ValueMapping();
        for (int i = 0; i < sourceTuple.getTuple().getCells().size(); i++) {
            if (sourceTuple.getTuple().getCells().get(i).getAttribute().equals(SpeedyConstants.OID)) {
                continue;
            }
            IValue sourceValue = sourceTuple.getTuple().getCells().get(i).getValue();
            IValue destinationValue = destinationTuple.getTuple().getCells().get(i).getValue();
            if (logger.isTraceEnabled()) logger.trace("Comparing values: '" + sourceValue + "', '" + destinationValue + "'");
            ValueMatchResult matchResult = match(sourceValue, destinationValue);
            if (matchResult == ValueMatchResult.NOT_MATCHING) {
                if (logger.isTraceEnabled()) logger.trace("Values not match...");
                return null;
            }
            valueMapping = updateValueMapping(valueMapping, sourceValue, destinationValue, matchResult);
            if (valueMapping == null) {
                if (logger.isTraceEnabled()) logger.trace("Conflicting mapping for values...");
                return null;
            }
        }
        TupleMatch tupleMatch = new TupleMatch(sourceTuple, destinationTuple, valueMapping);
        return tupleMatch;
    }

    private ValueMatchResult match(IValue sourceValue, IValue destinationValue) {
        if (sourceValue instanceof ConstantValue && destinationValue instanceof ConstantValue) {
            if (sourceValue.equals(destinationValue)) {
                return ValueMatchResult.EQUAL_CONSTANTS;
            }
        }
        if (sourceValue instanceof NullValue && destinationValue instanceof ConstantValue) {
            return ValueMatchResult.PLACEHOLDER_TO_CONSTANT;
        }
        if (sourceValue instanceof NullValue && destinationValue instanceof NullValue) {
            return ValueMatchResult.BOTH_PLACEHOLDER;
        }
        return ValueMatchResult.NOT_MATCHING;
    }

//    private int score(ValueMatchResult matchResult) {
//        if (matchResult == ValueMatchResult.EQUAL_CONSTANTS) {
//            return 1;
//        }
//        if (matchResult == ValueMatchResult.BOTH_PLACEHOLDER) {
//            return 1;
//        }
//        if (matchResult == ValueMatchResult.PLACEHOLDER_TO_CONSTANT) {
//            return 0;
//        }
//        return 0;
//    }

    private ValueMapping updateValueMapping(ValueMapping valueMapping, IValue sourceValue, IValue destinationValue, ValueMatchResult matchResult) {
        if (matchResult == ValueMatchResult.BOTH_PLACEHOLDER || matchResult == ValueMatchResult.PLACEHOLDER_TO_CONSTANT) {
            IValue valueForSourceValue = valueMapping.getValueMapping(sourceValue);
            if (valueForSourceValue != null && !valueForSourceValue.equals(destinationValue)) {
                return null;
            }
            valueMapping.putValueMapping(sourceValue, destinationValue);
        }
        return valueMapping;
    }

    private void sortTupleMatches(TupleMatches tupleMatches) {
        for (TupleWithTable tuple : tupleMatches.getTuples()) {
            List<TupleMatch> matchesForTuple = tupleMatches.getMatchesForTuple(tuple);
            Collections.sort(matchesForTuple, new TupleMatchComparatorScore());
        }
    }

    private List<List<TupleMatch>> combineMatches(List<TupleWithTable> sourceTuples, TupleMatches tupleMatches) {
        List<List<TupleMatch>> allTupleMatches = new ArrayList<List<TupleMatch>>();
        for (TupleWithTable sourceTuple : sourceTuples) {
            allTupleMatches.add(tupleMatches.getMatchesForTuple(sourceTuple));
        }
        return allTupleMatches;
    }

    private TupleMapping checkIfIsHomomorphism(List<TupleMatch> candidateHomomorphism) {
        TupleMapping homomorphism = new TupleMapping();
        for (TupleMatch tupleMatch : candidateHomomorphism) {
            homomorphism = addTupleMatch(homomorphism, tupleMatch);
            if (homomorphism == null) {
                return null;
            }
            homomorphism.putTupleMapping(tupleMatch.getLeftTuple(), tupleMatch.getRightTuple());
        }
        return homomorphism;
    }

    private TupleMapping addTupleMatch(TupleMapping homomorphism, TupleMatch tupleMatch) {
        for (IValue sourceValue : tupleMatch.getLeftToRightValueMapping().getKeys()) {
            IValue destinationValue = tupleMatch.getLeftToRightValueMapping().getValueMapping(sourceValue);
            IValue valueForSourceValueInHomomorphism = homomorphism.getLeftToRightMappingForValue(sourceValue);
            if (valueForSourceValueInHomomorphism != null && !valueForSourceValueInHomomorphism.equals(destinationValue)) {
                return null;
            }
            homomorphism.addLeftToRightMappingForValue(sourceValue, destinationValue);
        }
        return homomorphism;
    }

    private boolean checkIsomorphism(InstanceMatchTask instanceMatch) {
        if (!instanceMatch.hasHomomorphism()) {
            return false;
        }
        if (instanceMatch.getSourceDb().getSize() != instanceMatch.getTargetDb().getSize()) {
            return false;
        }
        if (!injective(instanceMatch)) {
            return false;
        }
        return true;
    }

    private boolean injective(InstanceMatchTask instanceMatch) {
        List<TupleWithTable> rightMatchedTuples = instanceMatch.getTupleMapping().getRightValues();
        Set<TupleWithTable> distinctMatchedTuples = new HashSet<TupleWithTable>(rightMatchedTuples);
        if (rightMatchedTuples.size() != distinctMatchedTuples.size()) {
            return false;
        }
        Collection<IValue> rightMatchedNulls = instanceMatch.getTupleMapping().getLeftToRightValueMapping().getValues();
        Set<IValue> distinctMatchedNulls = new HashSet<IValue>(rightMatchedNulls);
        if (rightMatchedNulls.size() != distinctMatchedNulls.size()) {
            return false;
        }
        for (IValue rightNull : distinctMatchedNulls) {
            if (rightNull instanceof ConstantValue) {
                return false;
            }
        }
        return true;
    }
}
