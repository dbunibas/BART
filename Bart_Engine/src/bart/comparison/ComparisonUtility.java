package bart.comparison;

import bart.comparison.operators.TupleMatchComparatorScore;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import speedy.SpeedyConstants;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleWithTable;

public class ComparisonUtility {

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

//    public static boolean valueMappingsAreCompatible(ValueMapping leftToRightValueMapping, ValueMapping rightToLeftValueMapping) {
//        for (IValue leftValue : leftToRightValueMapping.getKeys()) {
//            IValue rightValue = leftToRightValueMapping.getValueMapping(leftValue);
//            IValue leftValueInRightToLeft = rightToLeftValueMapping.getValueMapping(rightValue);
//            if (leftValueInRightToLeft != null) {
//                return false;
//            }
//        }
//        return true;
//    }
    public static void sortTupleMatches(TupleMatches tupleMatches) {
        for (TupleWithTable tuple : tupleMatches.getTuples()) {
            List<TupleMatch> matchesForTuple = tupleMatches.getMatchesForTuple(tuple);
            Collections.sort(matchesForTuple, new TupleMatchComparatorScore());
        }
    }

//    public static void addTupleMapping(TupleMapping tupleMapping, TupleMatch tupleMatch) {
//        for (IValue leftValue : tupleMatch.getLeftToRightValueMapping().getKeys()) {
//            IValue rightValue = tupleMatch.getLeftToRightValueMapping().getValueMapping(leftValue);
//            tupleMapping.addLeftToRightMappingForValue(leftValue, rightValue);
//        }
//        for (IValue rightValue : tupleMatch.getRightToLeftValueMapping().getKeys()) {
//            IValue leftValue = tupleMatch.getRightToLeftValueMapping().getValueMapping(rightValue);
//            tupleMapping.addRightToLeftMappingForValue(rightValue, leftValue);
//        }
//    }
//    public static void mergeValueMappings(TupleMapping tupleMapping, ValueMappings newValueMappings) {
//        updateValueMapping(tupleMapping.getLeftToRightValueMapping(), newValueMappings.getLeftToRightValueMapping());
//        updateValueMapping(tupleMapping.getRightToLeftValueMapping(), newValueMappings.getRightToLeftValueMapping());
//    }
//    public static void mergeValueMappings(ValueMappings tupleMapping, ValueMappings newValueMappings) {
//        updateValueMapping(tupleMapping.getLeftToRightValueMapping(), newValueMappings.getLeftToRightValueMapping());
//        updateValueMapping(tupleMapping.getRightToLeftValueMapping(), newValueMappings.getRightToLeftValueMapping());
//    }

    private static void updateValueMapping(ValueMapping src, ValueMapping dst) {
        for (IValue key : src.getKeys()) {
            IValue value = src.getValueMapping(key);
            dst.putValueMapping(key, value);
        }
    }

}
