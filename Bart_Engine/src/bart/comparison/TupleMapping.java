package bart.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import speedy.model.database.IValue;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class TupleMapping {

    private final Map<TupleWithTable, Set<TupleWithTable>> tupleMapping = new HashMap<TupleWithTable, Set<TupleWithTable>>();
    private ValueMappings valueMappings = new ValueMappings();
    private List<TupleWithTable> leftNonMatchingTuples = new ArrayList<TupleWithTable>();
    private List<TupleWithTable> rightNonMatchingTuples = new ArrayList<TupleWithTable>();
    private Double score;

    public void putTupleMapping(TupleWithTable tuple, TupleWithTable destinationTuple) {
        Set<TupleWithTable> tupleSet = this.tupleMapping.get(tuple);
        if (tupleSet == null) {
            tupleSet = new HashSet<TupleWithTable>();
            this.tupleMapping.put(tuple, tupleSet);
        }
        tupleSet.add(destinationTuple);
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

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "no mapping";
        }
        return "----------------- Tuple Mapping ------------------"
                + SpeedyUtility.printMapCompact(tupleMapping)
                + (this.valueMappings.getLeftToRightValueMapping().isEmpty() ? "" : "\nValue mapping: " + this.valueMappings.getLeftToRightValueMapping())
                + (this.valueMappings.getRightToLeftValueMapping().isEmpty() ? "" : "\nRight to left value mapping: " + this.valueMappings.getRightToLeftValueMapping())
                + (score != null ? "\nScore: " + score + "\n" : "")
                + (!leftNonMatchingTuples.isEmpty() ? "Non matching left tuples:\n" + SpeedyUtility.printCollection(leftNonMatchingTuples,"\t") : "")
                + (!rightNonMatchingTuples.isEmpty() ? "\nNon matching right tuples:\n" + SpeedyUtility.printCollection(rightNonMatchingTuples,"\t") : "");
    }

}
