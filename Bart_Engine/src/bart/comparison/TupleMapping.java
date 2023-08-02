package bart.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class TupleMapping {

    private final Map<TupleWithTable, Set<TupleWithTable>> tupleMapping = new HashMap<TupleWithTable, Set<TupleWithTable>>();
    private final Map<TupleWithTable, Set<TupleWithTable>> reverseTupleMapping = new HashMap<TupleWithTable, Set<TupleWithTable>>();

    private ValueMappings valueMappings = new ValueMappings();
    private List<TupleWithTable> leftNonMatchingTuples = new ArrayList<TupleWithTable>();
    private List<TupleWithTable> rightNonMatchingTuples = new ArrayList<TupleWithTable>();
    private Double score;
    private boolean enableReverse = false;

    private final static Logger logger = LoggerFactory.getLogger(TupleMapping.class);

    public void putTupleMapping(TupleWithTable tuple, TupleWithTable destinationTuple) {
        logger.debug("Call put Tuple mapping for: {} target {}", tuple, destinationTuple);
        logger.debug("Tuple Mapping\n: {}", SpeedyUtility.printMapCompact(this.tupleMapping));
        Set<TupleWithTable> tupleSet = this.tupleMapping.get(tuple);
        logger.debug("TupleSet: {}", tupleSet);
        if (tupleSet == null) {
            tupleSet = new HashSet<TupleWithTable>();
            logger.debug("Add {}", tuple);
            this.tupleMapping.put(tuple, tupleSet);
        }
        tupleSet.add(destinationTuple);
//        if (enableReverse) {
//            for (TupleWithTable keyForReverse : tupleSet) {
//                Set<TupleWithTable> reverseSet = this.reverseTupleMapping.get(keyForReverse);
//                if (reverseSet == null) {
//                    reverseSet = new HashSet<TupleWithTable>();
//                    this.reverseTupleMapping.put(keyForReverse, reverseSet);
//                }
//                reverseSet.add(tuple);
//            }
//        }
        logger.debug("Tuple Mapping\n: {}", SpeedyUtility.printMapCompact(this.tupleMapping));
//        logger.debug("Reverse Tuple Mapping\n: {}", SpeedyUtility.printMapCompact(this.reverseTupleMapping));
    }

//    public void putInTupleSet(TupleWithTable originalTuple, TupleWithTable redundantTuple) {
//        Set<TupleWithTable> keysForTupleMapping = this.reverseTupleMapping.get(originalTuple);
//        if (keysForTupleMapping != null) {
//            for (TupleWithTable tupleWithTable : keysForTupleMapping) {
//                this.putTupleMapping(tupleWithTable, redundantTuple);
//            }
//        }
//    }
//    public void replaceInTupleSet(TupleWithTable originalTuple, TupleWithTable newTuple) {
//        logger.info("ReplateInTupleSet: {} with {}", originalTuple, newTuple);
//        Set<TupleWithTable> keysForTupleMapping = this.reverseTupleMapping.get(originalTuple);
//        logger.info("Keys: ", keysForTupleMapping);
//        if (keysForTupleMapping != null) {
//            this.reverseTupleMapping.remove(originalTuple);
//            this.reverseTupleMapping.put(newTuple, keysForTupleMapping);
//            logger.info("Keys: {}", keysForTupleMapping);
//            for (TupleWithTable tupleWithTable : keysForTupleMapping) {
//                Set<TupleWithTable> tupleMappingSet = this.tupleMapping.get(tupleWithTable);
//                tupleMappingSet.remove(originalTuple);
//                tupleMappingSet.add(newTuple);
//            }
//        }
//    }
//    public void cloneTupleMapping(TupleWithTable oldTuple, TupleWithTable newTuple) {
//        Set<TupleWithTable> tupleSet = this.tupleMapping.get(oldTuple);
//        for (TupleWithTable tupleWithTable : tupleSet) {
//            putTupleMapping(newTuple, tupleWithTable);
//        }
//    }
    public void removeTupleMapping(TupleWithTable source, TupleWithTable target) {
        logger.debug("Call Remove Tuple mapping with key: {} and value {}", source, target);
        this.tupleMapping.remove(source);
//        Set<TupleWithTable> possibleMatches = this.tupleMapping.get(target);
//        if (possibleMatches != null && possibleMatches.contains(source)) {
//            possibleMatches.remove(source);
//        }
//        if (possibleMatches != null && possibleMatches.isEmpty()) {
//            this.tupleMapping.remove(target);
//        }
////        logger.debug("Tuples to remove from reversing mapping: {}", setForReverse);
////        for (TupleWithTable tupleReverse : setForReverse) {
////            logger.debug("Remove from reverseTupleMapping {}", tupleReverse);
////            this.reverseTupleMapping.remove(tupleReverse);
////        }
////        logger.debug("Tuple Mapping\n: {}", SpeedyUtility.printMapCompact(tupleMapping));
////        logger.debug("Reverse Tuple Mapping\n: {}", SpeedyUtility.printMapCompact(reverseTupleMapping));
    }

//    public void removeTupleMappingInTarget(TupleWithTable tuple) {
//        Set<TupleWithTable> tupleSetLeft = this.reverseTupleMapping.get(tuple);
//        if (tupleSetLeft != null) {
//            for (TupleWithTable tupleWithTable : tupleSetLeft) {
//                removeTupleMapping(tupleWithTable);
//            }
//        }
//        this.tupleMapping.remove(tuple);
//    }
    public void updateTupleMapping(TupleWithTable oldKey, TupleWithTable newKey, TupleWithTable newValue) {
        logger.debug("OLD Key: {}", oldKey);
        logger.debug("New Key: {}", newKey);
        logger.debug("New value: {}", newValue);
        Set<TupleWithTable> tupleSet = this.tupleMapping.get(oldKey);
        logger.debug("TupleSet for oldKey: {}", tupleSet);
        if (tupleSet != null) {
            tupleSet.add(newValue);
            logger.debug("Remove old key: {}", oldKey);
            this.tupleMapping.remove(oldKey);
            logger.debug("Add the mapping to {} with {}", newKey, tupleSet);
            this.tupleMapping.put(newKey, tupleSet);
            logger.debug("Check: key {}, value {}", newKey, this.tupleMapping.get(newKey));
        } else {
            logger.debug("TupleSet is null, add new key {} to {}", newKey, newValue);
            this.putTupleMapping(newKey, newValue);
        }
        //check if old key was in the tupleSet
        logger.debug("TupleMapping before check in tupleSet: {}", this.tupleMapping);
        Set<TupleWithTable> tupleSetMatchesWithOldKey = this.tupleMapping.get(newValue);
        logger.debug("Keys with value {}: {}", newValue, tupleSetMatchesWithOldKey);
        if (tupleSetMatchesWithOldKey != null && tupleSetMatchesWithOldKey.contains(oldKey)) {
            logger.debug("Remove also match {} with {}", oldKey, newValue);
            tupleSetMatchesWithOldKey.remove(oldKey);
            tupleSetMatchesWithOldKey.add(newKey);
            logger.debug("After update: {}", tupleSetMatchesWithOldKey);
        }
        logger.debug("TupleMapping after update: {}", this.tupleMapping);
//       
//        this.tupleMapping.remove(oldKey);
//        logger.debug("TupleSet: {}", tupleSet);
//        if (tupleSet != null) {
//            tupleSet.remove(newValue); // remove old version of the tuple
//            //this.tupleMapping.remove(oldTuple);
//            //this.tupleMapping.put(newTuple, tupleSet);
////            removeTupleMapping(oldKey);
//            for (TupleWithTable tupleWithTable : tupleSet) {
//                putTupleMapping(newKey, tupleWithTable);
//            }
//            putTupleMapping(newKey, newValue);
//        } else {
//            putTupleMapping(newKey, newValue);
//        }
    }

    public void removeValueMapping(TupleWithTable key, TupleWithTable target, boolean isLeft) {
        ValueMapping valueMapping = this.valueMappings.getLeftToRightValueMapping();
        if (!isLeft) {
            valueMapping = this.valueMappings.getRightToLeftValueMapping();
        }
        for (int i = 0; i < key.getTuple().getCells().size(); i++) {
            IValue valueInCell = key.getTuple().getCells().get(i).getValue();
            IValue valueInTarget = target.getTuple().getCells().get(i).getValue();
            if (valueInCell instanceof NullValue) {
                valueMapping.removeValueMapping(valueInCell, valueInTarget);
            }
        }
    }

    public void addValueMapping(TupleWithTable source, TupleWithTable target, boolean isLeft) {
        ValueMapping valueMapping = this.valueMappings.getLeftToRightValueMapping();
        if (!isLeft) {
            valueMapping = this.valueMappings.getRightToLeftValueMapping();
        }
        for (int i = 0; i < source.getTuple().getCells().size(); i++) {
            IValue valueInCell = source.getTuple().getCells().get(i).getValue();
            IValue valueInTarget = target.getTuple().getCells().get(i).getValue();
            if (valueInCell instanceof NullValue) {
                valueMapping.putValueMapping(valueInCell, valueInTarget);
            }
        }
    }

    public void updateValueMappings() {
        valueMappings = new ValueMappings();
        for (TupleWithTable sourceTuple : tupleMapping.keySet()) {
            Set<TupleWithTable> targetTuples = tupleMapping.get(sourceTuple);
            for (TupleWithTable targetTuple : targetTuples) {
                updateValueMappingsSourceToTarget(sourceTuple, targetTuple);
            }
        }
    }

    public void updateValueMapping(TupleWithTable sourceTuple, TupleWithTable targetTuple) {
        for (int i = 0; i < sourceTuple.getTuple().getCells().size(); i++) {
            IValue sourceValue = sourceTuple.getTuple().getCells().get(i).getValue();
            IValue targetValue = targetTuple.getTuple().getCells().get(i).getValue();
            if (!targetValue.equals(sourceValue)) {
                if (sourceValue instanceof NullValue && targetValue instanceof ConstantValue) {
                    IValue toValuePrev = this.valueMappings.getLeftToRightValueMapping().getValueMapping(sourceValue);
                    this.valueMappings.getLeftToRightValueMapping().removeValueMapping(sourceValue, toValuePrev);
                }
                if (sourceValue instanceof NullValue && targetValue instanceof NullValue) {
                    IValue toValuePrevSource = this.valueMappings.getLeftToRightValueMapping().getValueMapping(sourceValue);
                    IValue toValuePrevTarget = this.valueMappings.getLeftToRightValueMapping().getValueMapping(targetValue);
                    this.valueMappings.getLeftToRightValueMapping().removeValueMapping(sourceValue, toValuePrevSource);
                    this.valueMappings.getRightToLeftValueMapping().removeValueMapping(targetValue, toValuePrevTarget);
                    this.valueMappings.getLeftToRightValueMapping().putValueMapping(sourceValue, targetValue);
                    this.valueMappings.getRightToLeftValueMapping().putValueMapping(targetValue, sourceValue);
                }
                if (sourceValue instanceof ConstantValue && targetValue instanceof NullValue) {
                    this.valueMappings.getRightToLeftValueMapping().putValueMapping(targetValue, sourceValue);
                }
            }
        }

    }

    private void updateValueMappingsSourceToTarget(TupleWithTable sourceTuple, TupleWithTable targetTuple) {
        ValueMapping leftToRightValueMapping = this.valueMappings.getLeftToRightValueMapping();
        ValueMapping rightToLeftValueMapping = this.valueMappings.getRightToLeftValueMapping();
        logger.info("Source: {}", sourceTuple);
        logger.info("Target: {}", targetTuple);
        for (int i = 0; i < sourceTuple.getTuple().getCells().size(); i++) {
            IValue sourceValue = sourceTuple.getTuple().getCells().get(i).getValue();
            IValue targetValue = targetTuple.getTuple().getCells().get(i).getValue();
            logger.info("Source: {}, Target: {}", sourceValue, targetValue);
            if (sourceValue instanceof NullValue) {
                leftToRightValueMapping.putValueMapping(sourceValue, targetValue);
            }
            if (targetValue instanceof NullValue) {
                rightToLeftValueMapping.putValueMapping(targetValue, sourceValue);
            }
        }
    }

    public TupleWithTable getFirstMappingForTuple(TupleWithTable tuple) {
        Set<TupleWithTable> tupleSet = this.tupleMapping.get(tuple);
        if (tupleSet == null) {
            return null;
        }
        return tupleSet.iterator().next();
    }

    public ValueMappings getValueMappings() {
        return valueMappings;
    }

    public void setValueMappings(ValueMappings valueMappings) {
        this.valueMappings = valueMappings;
    }

    public ValueMapping getLeftToRightValueMapping() {
        return this.valueMappings.getLeftToRightValueMapping();
    }

    public IValue getLeftToRightMappingForValue(IValue value) {
        return this.valueMappings.getLeftToRightValueMapping().getValueMapping(value);
    }

    public void addLeftToRightMappingForValue(IValue sourceValue, IValue destinationValue) {
        this.valueMappings.getLeftToRightValueMapping().putValueMapping(sourceValue, destinationValue);
    }

    public ValueMapping getRightToLeftValueMapping() {
        return this.valueMappings.getRightToLeftValueMapping();
    }

    public IValue getRightToLeftMappingForValue(IValue value) {
        return this.valueMappings.getRightToLeftValueMapping().getValueMapping(value);
    }

    public void addRightToLeftMappingForValue(IValue sourceValue, IValue destinationValue) {
        this.valueMappings.getRightToLeftValueMapping().putValueMapping(sourceValue, destinationValue);
    }

    public List<TupleWithTable> getLeftNonMatchingTuples() {
        return leftNonMatchingTuples;
    }

    public void setLeftNonMatchingTuples(List<TupleWithTable> leftNonMatchingTuples) {
        this.leftNonMatchingTuples = leftNonMatchingTuples;
    }

    public List<TupleWithTable> getRightNonMatchingTuples() {
        return rightNonMatchingTuples;
    }

    public void setRightNonMatchingTuples(List<TupleWithTable> rightNonMatchingTuples) {
        this.rightNonMatchingTuples = rightNonMatchingTuples;
    }

    public Map<TupleWithTable, Set<TupleWithTable>> getTupleMapping() {
        return tupleMapping;
    }

    public Map<TupleWithTable, Set<TupleWithTable>> getReverseTupleMapping() {
        return reverseTupleMapping;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void addScore(double score) {
        this.score += score;
    }

    public boolean isEmpty() {
        return tupleMapping.isEmpty();
    }

    public List<TupleWithTable> getRightValues() {
        List<TupleWithTable> result = new ArrayList<TupleWithTable>();
        for (Set<TupleWithTable> value : tupleMapping.values()) {
            result.addAll(value);
        }
        return result;
    }

    public void setEnableReverse(boolean enableReverse) {
        this.enableReverse = enableReverse;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "no mapping";
        }
        return "----------------- Tuple Mapping ------------------\n"
                + SpeedyUtility.printMapCompact(tupleMapping) + "\n"
                + "----------------- Reverse Tuple Mapping ------------------\n"
                + SpeedyUtility.printMapCompact(reverseTupleMapping) + "\n"
                + (this.valueMappings.getLeftToRightValueMapping().isEmpty() ? "" : "\nValue mapping: " + this.valueMappings.getLeftToRightValueMapping())
                + (this.valueMappings.getRightToLeftValueMapping().isEmpty() ? "" : "\nRight to left value mapping: " + this.valueMappings.getRightToLeftValueMapping())
                + (score != null ? "\nScore: " + score + "\n" : "")
                + (!leftNonMatchingTuples.isEmpty() ? "Non matching left tuples:\n" + SpeedyUtility.printCollection(leftNonMatchingTuples, "\t") : "")
                + (!rightNonMatchingTuples.isEmpty() ? "\nNon matching right tuples:\n" + SpeedyUtility.printCollection(rightNonMatchingTuples, "\t") : "")
                + "\n";
    }

}
