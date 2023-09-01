package bart.comparison.operators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import bart.comparison.TupleMapping;
import speedy.model.database.TupleWithTable;

public class FindNonMatchingTuples {

    public void find(List<TupleWithTable> sourceTuples, List<TupleWithTable> destinationTuples, TupleMapping tupleMapping) {
        findLeftNonMatchingTuple(sourceTuples, tupleMapping);
        findRightNonMatchingTuple(destinationTuples, tupleMapping);
    }

    private void findLeftNonMatchingTuple(List<TupleWithTable> sourceTuples, TupleMapping tupleMapping) {
        Set<TupleWithTable> leftNonMatchingTuple = findTuplesNotContained(sourceTuples, tupleMapping.getTupleMapping().keySet());
        tupleMapping.setLeftNonMatchingTuples(leftNonMatchingTuple);
    }

    private void findRightNonMatchingTuple(List<TupleWithTable> destinationTuples, TupleMapping tupleMapping) {
        Set<TupleWithTable> rightNonMatchingTuple = findTuplesNotContained(destinationTuples, new HashSet<TupleWithTable>(tupleMapping.getRightValues()));
        tupleMapping.setRightNonMatchingTuples(rightNonMatchingTuple);
    }

    private Set<TupleWithTable> findTuplesNotContained(List<TupleWithTable> expected, Set<TupleWithTable> found) {
        Set<TupleWithTable> result = new HashSet<TupleWithTable>();
        for (TupleWithTable expectedTuple : expected) {
            if (!found.contains(expectedTuple)) {
                result.add(expectedTuple);
            }
        }
        return result;
    }

}
