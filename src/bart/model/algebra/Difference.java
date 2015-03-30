package bart.model.algebra;

import bart.utility.AlgebraUtility;
import bart.model.algebra.operators.*;
import bart.model.database.AttributeRef;
import bart.model.database.IDatabase;
import bart.model.database.Tuple;
import bart.utility.BartUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Difference extends AbstractOperator {
    
    private static Logger logger = LoggerFactory.getLogger(Difference.class);
    
    private TupleValueComparator comparator = new TupleValueComparator();
    
    public String getName() {
        return "DIFFERENCE";
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

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitDifference(this);
    }

    private void materializeResult(ITupleIterator leftTuples, ITupleIterator rightTuples, List<Tuple> result) {
        List<Tuple> leftTupleCache = new ArrayList<Tuple>();
        List<Tuple> rightTupleCache = new ArrayList<Tuple>();
        materializeTuples(leftTuples, leftTupleCache);
        materializeTuples(rightTuples, rightTupleCache);
        if (!leftTupleCache.isEmpty() && !rightTupleCache.isEmpty()) {
            if (leftTupleCache.get(0).size() != rightTupleCache.get(0).size()) {
                throw new IllegalArgumentException("Difference arguments must have the same size and schema: " + leftTupleCache.get(0) + " - " + rightTupleCache.get(0));
            }
        }
        Collections.sort(leftTupleCache, new TupleValueComparator());
        Collections.sort(rightTupleCache, new TupleValueComparator());
        if (logger.isDebugEnabled()) logger.debug("Left tuples for difference: \n" + BartUtility.printCollection(leftTupleCache));
        if (logger.isDebugEnabled()) logger.debug("Right tuples for difference: \n" + BartUtility.printCollection(rightTupleCache));
        Iterator<Tuple> itLeft = leftTupleCache.iterator();
        Iterator<Tuple> itRight = rightTupleCache.iterator();
        Tuple rightTuple = null;
        while (itLeft.hasNext()) {
            Tuple leftTuple = itLeft.next();
            rightTuple = scanValues(leftTuple, rightTuple, itRight);
            if (!AlgebraUtility.areEqualExcludingOIDs(leftTuple, rightTuple)) {
                result.add(leftTuple);
            }
        }
        AlgebraUtility.removeDuplicates(result);
        if (logger.isDebugEnabled()) logger.debug("Result:\n" + BartUtility.printCollection(result));
    }
    
    
    private void materializeTuples(ITupleIterator tupleIterator, List<Tuple> tupleCache) {
        while(tupleIterator.hasNext()) {
            tupleCache.add(tupleIterator.next());
        }
        tupleIterator.reset();
    }

    private Tuple scanValues(Tuple leftTuple, Tuple rightTuple, Iterator<Tuple> itRight) {
        if (rightTuple != null && equalsOrFollows(rightTuple, leftTuple)) {
            return rightTuple;
        }
        while (itRight.hasNext()) {
            rightTuple = itRight.next();
            if (equalsOrFollows(rightTuple, leftTuple)) {
                return rightTuple;
            }
        }
        return null;
    }

    private boolean equalsOrFollows(Tuple rightTuple, Tuple leftTuple) {
        return comparator.compare(rightTuple, leftTuple) >= 0; 
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        return this.children.get(0).getAttributes(source, target);
    }
}
