package bart.model.algebra;

import bart.model.algebra.operators.ListTupleIterator;
import bart.utility.AlgebraUtility;
import bart.model.algebra.operators.IAlgebraTreeVisitor;
import bart.model.algebra.operators.ITupleIterator;
import bart.model.database.AttributeRef;
import bart.model.database.IDatabase;
import bart.model.database.Tuple;
import java.util.ArrayList;
import java.util.List;

public class Union extends AbstractOperator {

    public String getName() {
        return "UNION";
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitUnion(this);
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        List<Tuple> result = new ArrayList<Tuple>();
        ITupleIterator leftTuples = children.get(0).execute(source, target);
        ITupleIterator rightTuples = children.get(1).execute(source, target);
        materializeResult(leftTuples, rightTuples, result);
        leftTuples.close();
        rightTuples.close();
        return new ListTupleIterator(result);
    }

    private void materializeResult(ITupleIterator leftTuples, ITupleIterator rightTuples, List<Tuple> result) {
        materializeTuples(leftTuples, result);
        int lastLeftTuplePosition = result.size();
        materializeTuples(rightTuples, result);
        int lastRightTuplePosition = result.size();
        checkIfTuplesHaveSameSize(result, lastLeftTuplePosition, lastRightTuplePosition);
        AlgebraUtility.removeDuplicates(result);
    }

    private void materializeTuples(ITupleIterator tupleIterator, List<Tuple> tupleCache) {
        while (tupleIterator.hasNext()) {
            tupleCache.add(tupleIterator.next());
        }
        tupleIterator.reset();
    }

    private void checkIfTuplesHaveSameSize(List<Tuple> result, int pos1, int pos2) {
        if (pos1 != 0 && pos2 != pos1) {
            if (result.get(pos1 - 1).size() != result.get(pos2 - 1).size()) {
                throw new IllegalArgumentException("Union arguments must have the same size: " + result.get(pos1 - 1) + " " + result.get(pos2 - 1));
            }
        }
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        return this.children.get(0).getAttributes(source, target);
    }
}
