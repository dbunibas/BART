package bart.comparison.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import bart.comparison.ComparisonConfiguration;
import bart.comparison.ComparisonStats;
import bart.comparison.TupleMatch;
import bart.comparison.ValueMappings;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class CheckTupleMatch {

    private final static Logger logger = LoggerFactory.getLogger(CheckTupleMatch.class);
    private final CheckTupleMatchCompatibility compatibilityChecker = new CheckTupleMatchCompatibility();
    private boolean debug = false;

    public TupleMatch checkMatch(TupleWithTable leftTuple, TupleWithTable rightTuple) {
        this.debug = checkDebug(leftTuple, rightTuple);
        long start = System.currentTimeMillis();
        TupleMatch result = check(leftTuple, rightTuple);
        long end = System.currentTimeMillis();
        ComparisonStats.getInstance().addStat(ComparisonStats.CHECK_TUPLE_MATCH_TIME, end - start);
        return result;
    }

    private TupleMatch check(TupleWithTable leftTuple, TupleWithTable rightTuple) {
        if (debug) if (logger.isWarnEnabled()) logger.warn("Comparing tuple: " + leftTuple + " to tuple " + rightTuple);
        if (logger.isDebugEnabled()) logger.debug("Comparing tuple: " + leftTuple + " to tuple " + rightTuple);
        if (!leftTuple.getTable().equals(rightTuple.getTable())) {
            return null;
        }
        ValueMappings valueMappings = new ValueMappings();
        double scoreEstimate = 0.0;
        for (int i = 0; i < leftTuple.getTuple().getCells().size(); i++) {
            if (leftTuple.getTuple().getCells().get(i).getAttribute().equals(SpeedyConstants.OID)) {
                continue;
            }
            IValue leftValue = leftTuple.getTuple().getCells().get(i).getValue();
            IValue rightValue = rightTuple.getTuple().getCells().get(i).getValue();
            if (logger.isTraceEnabled()) logger.trace("Comparing values: '" + leftValue + "', '" + rightValue + "'");
            SpeedyConstants.ValueMatchResult matchResult = match(leftValue, rightValue);
            if (matchResult == SpeedyConstants.ValueMatchResult.NOT_MATCHING) {
                if (logger.isTraceEnabled()) logger.trace("Values not match...");
                return null;
            }
            ValueMappings valueMappingForAttribute = generateValueMappingForAttribute(leftValue, rightValue, matchResult);
            double matchScore = scoreEstimate(matchResult);
            TupleMatch tupleMatch = new TupleMatch(leftTuple, rightTuple, valueMappingForAttribute, scoreEstimate);
            boolean consistent = compatibilityChecker.checkCompatibilityAndMerge(valueMappings, tupleMatch);
//            boolean consistent = updateValueMappings(valueMappings, leftValue, rightValue, matchResult);
            if (!consistent) {
                if (logger.isTraceEnabled()) logger.trace("Conflicting mapping for values: '" + leftValue + "', '" + rightValue + "'. Current value mapping: " + valueMappings);
                return null;
            }
            if (logger.isTraceEnabled()) logger.trace("Match score " + matchScore);
            scoreEstimate += matchScore;
        }
        TupleMatch tupleMatch = new TupleMatch(leftTuple, rightTuple, valueMappings, scoreEstimate);
        ValueMappings compatibleMapping = new ValueMappings();
        boolean compatible = compatibilityChecker.checkCompatibilityAndMerge(compatibleMapping, tupleMatch);
        if (!compatible) {
            if (logger.isDebugEnabled()) logger.debug("Inconsistent value mappings, discarding...");
            return null;
        }
        tupleMatch.setValueMappings(compatibleMapping);
        if (logger.isDebugEnabled()) logger.debug("** Corrected tuple match: " + tupleMatch);
        return tupleMatch;
    }

    private ValueMappings generateValueMappingForAttribute(IValue leftValue, IValue rightValue, SpeedyConstants.ValueMatchResult matchResult) {
        ValueMappings valueMappingForAttribute = new ValueMappings();
        if (matchResult == SpeedyConstants.ValueMatchResult.BOTH_PLACEHOLDER || matchResult == SpeedyConstants.ValueMatchResult.PLACEHOLDER_TO_CONSTANT) {
            valueMappingForAttribute.getLeftToRightValueMapping().putValueMapping(leftValue, rightValue);
        }
        if (matchResult == SpeedyConstants.ValueMatchResult.CONSTANT_TO_PLACEHOLDER) {
            valueMappingForAttribute.getRightToLeftValueMapping().putValueMapping(rightValue, leftValue);
        }
        return valueMappingForAttribute;
    }

    public SpeedyConstants.ValueMatchResult match(IValue sourceValue, IValue destinationValue) {
        if (sourceValue instanceof ConstantValue && destinationValue instanceof ConstantValue) {
            if (sourceValue.equals(destinationValue)) {
                return SpeedyConstants.ValueMatchResult.EQUAL_CONSTANTS;
            }
        }
        if (SpeedyUtility.isPlaceholder(sourceValue) && destinationValue instanceof ConstantValue) {
            return SpeedyConstants.ValueMatchResult.PLACEHOLDER_TO_CONSTANT;
        }
        if (SpeedyUtility.isPlaceholder(sourceValue) && SpeedyUtility.isPlaceholder(destinationValue)) {
            return SpeedyConstants.ValueMatchResult.BOTH_PLACEHOLDER;
        }
        if (ComparisonConfiguration.isTwoWayValueMapping() && sourceValue instanceof ConstantValue && SpeedyUtility.isPlaceholder(destinationValue)) {
            return SpeedyConstants.ValueMatchResult.CONSTANT_TO_PLACEHOLDER;
        }
        return SpeedyConstants.ValueMatchResult.NOT_MATCHING;
    }

    public double scoreEstimate(SpeedyConstants.ValueMatchResult matchResult) {
        if (matchResult == SpeedyConstants.ValueMatchResult.EQUAL_CONSTANTS) {
            return 1;
        }
        if (matchResult == SpeedyConstants.ValueMatchResult.BOTH_PLACEHOLDER) {
            return 1;
        }
        if (matchResult == SpeedyConstants.ValueMatchResult.PLACEHOLDER_TO_CONSTANT) {
            return ComparisonConfiguration.getK();
        }
        if (matchResult == SpeedyConstants.ValueMatchResult.CONSTANT_TO_PLACEHOLDER) {
            return ComparisonConfiguration.getK();
        }
        return 0;
    }

    private boolean checkDebug(TupleWithTable leftTuple, TupleWithTable rightTuple) {
//        if (rightTuple.getTuple().getOid().toString().equals("11")
//                && leftTuple.getTuple().getOid().toString().equals("18")) {
//            return true;
//        }
        return false;
    }

}
