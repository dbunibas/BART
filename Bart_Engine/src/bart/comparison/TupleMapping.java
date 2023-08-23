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
        Set<TupleWithTable> tupleSet = this.tupleMapping.get(tuple);
        if (tupleSet == null) {
            tupleSet = new HashSet<TupleWithTable>();
            this.tupleMapping.put(tuple, tupleSet);
        }
        tupleSet.add(destinationTuple);
        if (enableReverse) {
            Set<TupleWithTable> tupleSetReverse = this.reverseTupleMapping.get(destinationTuple);
            if (tupleSetReverse == null) {
                tupleSetReverse = new HashSet<TupleWithTable>();
                this.reverseTupleMapping.put(destinationTuple, tupleSetReverse);
            }
            tupleSetReverse.add(tuple);
        }
    }

    public void removeKeyTupleMapping(TupleWithTable source) {
        if (enableReverse) {
            Set<TupleWithTable> keysForReverse = this.tupleMapping.get(source);
            if (keysForReverse != null) {
                for (TupleWithTable keyReverse : keysForReverse) {
                    Set<TupleWithTable> tupleSetReverse = this.reverseTupleMapping.get(keyReverse);
                    if (tupleSetReverse != null) {
                        tupleSetReverse.remove(source);
                        if (tupleSetReverse.isEmpty()) {
                            this.reverseTupleMapping.remove(keyReverse);
                        }
                    }
                }
            }
        }
        logger.debug("Call Remove Tuple mapping with key: {}", source);
        this.tupleMapping.remove(source);
    }

    public void removeMappingForKey(TupleWithTable tuple, boolean isLeft) {
        if (isLeft) {
//            this.tupleMapping.remove(tuple);
            this.removeKeyTupleMapping(tuple);
        } else {
            if (!enableReverse) {
                Set<TupleWithTable> keysToDrop = new HashSet<>();
                for (TupleWithTable key : this.tupleMapping.keySet()) {
                    Set<TupleWithTable> tupleSet = this.tupleMapping.get(key);
                    if (tupleSet.contains(tuple)) {
                        tupleSet.remove(tuple);
                    }
                    if (tupleSet.isEmpty()) {
                        keysToDrop.add(key);
                    }
                }
                for (TupleWithTable key : keysToDrop) {
                    this.tupleMapping.remove(key);
                }
            } else {
                Set<TupleWithTable> tupleSetReverse = this.reverseTupleMapping.remove(tuple);
                if (tupleSetReverse != null) {
                    for (TupleWithTable keyForMapping : tupleSetReverse) {
                        Set<TupleWithTable> tupleSet = this.tupleMapping.get(keyForMapping);
                        if (tupleSet != null) {
                            tupleSet.remove(tuple);
                            if (tupleSet.isEmpty()) {
                                this.tupleMapping.remove(keyForMapping);
                            }
                        }
                    }
                }
            }
        }
    }

    public void removeFromTupleSetTupleMapping(TupleWithTable key, TupleWithTable target) {
        logger.debug("Call Remove Value {} with key {}", target, key);
        Set<TupleWithTable> tupleSet = this.tupleMapping.get(key);
        if (tupleSet != null && !tupleSet.isEmpty()) {
            tupleSet.remove(target);
        }
        if (tupleSet != null && tupleSet.isEmpty()) {
            this.tupleMapping.remove(key);
        }
        if (enableReverse) {
            Set<TupleWithTable> tupleSetReverse = this.reverseTupleMapping.get(target);
            if (tupleSetReverse != null) {
                tupleSetReverse.remove(key);
                if (tupleSetReverse.isEmpty()) {
                    this.reverseTupleMapping.remove(target);
                }
            }
        }
    }

    public void updateKeyTupleMapping(TupleWithTable oldKey, TupleWithTable newKey) {
        logger.debug("Replace Old Key {} with new key {}", oldKey, newKey);
        Set<TupleWithTable> tupleSet = this.tupleMapping.get(oldKey);
        if (enableReverse) {
            if (tupleSet != null) {
                for (TupleWithTable keyReverse : tupleSet) {
                    Set<TupleWithTable> tupleSetReverse = this.reverseTupleMapping.get(keyReverse);
                    tupleSetReverse.remove(oldKey);
                    tupleSetReverse.add(newKey);
                }
            }
        }
        if (tupleSet != null) {
            logger.debug("TupleSet for oldKey: {}", tupleSet);
            this.tupleMapping.put(newKey, tupleSet);
            this.tupleMapping.remove(oldKey);
        }
    }

    public void updateTupleSetTupleMapping(TupleWithTable key, TupleWithTable oldTarget, TupleWithTable newTarget) {
        // We assume that there is only one match for key
        logger.debug("Replace mapping in target. Key: {}", key);
        logger.debug("Change target {} to new target {}", oldTarget, newTarget);
        if (enableReverse) {
            this.reverseTupleMapping.remove(oldTarget);
            Set<TupleWithTable> tupleSetReverse = this.reverseTupleMapping.get(newTarget);
            if (tupleSetReverse == null) {
                tupleSetReverse = new HashSet<TupleWithTable>();
                this.reverseTupleMapping.put(newTarget, tupleSetReverse);
            }
            tupleSetReverse.add(key);
        }
        Set<TupleWithTable> tupleSet = this.tupleMapping.get(key);
        if (tupleSet != null) {
            logger.debug("Replace old target {} with the new {}", oldTarget, newTarget);
            logger.debug("TupleSet Before: {}", tupleSet);
            logger.debug("Old Target {}", oldTarget.isIsForGeneration());
            logger.debug("New Target {}", newTarget.isIsForGeneration());
            tupleSet.remove(oldTarget);
            tupleSet.add(newTarget);
            logger.debug("TupleSet After: {}", tupleSet);
        } else {
            putTupleMapping(key, newTarget);
        }
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
            if (sourceValue instanceof NullValue) {
                this.valueMappings.getLeftToRightValueMapping().putValueMapping(sourceValue, targetValue);
            }
            if (targetValue instanceof NullValue) {
                this.valueMappings.getRightToLeftValueMapping().putValueMapping(targetValue, sourceValue);
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
                //                + "----------------- Reverse Tuple Mapping ------------------\n"
                //                + SpeedyUtility.printMapCompact(reverseTupleMapping) + "\n"
                + (this.valueMappings.getLeftToRightValueMapping().isEmpty() ? "" : "\nValue mapping: " + this.valueMappings.getLeftToRightValueMapping())
                + (this.valueMappings.getRightToLeftValueMapping().isEmpty() ? "" : "\nRight to left value mapping: " + this.valueMappings.getRightToLeftValueMapping())
                + (score != null ? "\nScore: " + score + "\n" : "")
                + (!leftNonMatchingTuples.isEmpty() ? "Non matching left tuples:\n" + SpeedyUtility.printCollection(leftNonMatchingTuples, "\t") : "")
                + (!rightNonMatchingTuples.isEmpty() ? "\nNon matching right tuples:\n" + SpeedyUtility.printCollection(rightNonMatchingTuples, "\t") : "")
                + "\n";
    }

}
