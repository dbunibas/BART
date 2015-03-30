package bart.model.algebra;

import bart.model.algebra.operators.*;
import bart.model.database.*;
import bart.model.database.mainmemory.datasource.IntegerOIDGenerator;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CartesianProduct extends AbstractOperator {

    private static Logger logger = LoggerFactory.getLogger(CartesianProduct.class);

    public String getName() {
        return "CARTESIAN PRODUCT";
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        List<ITupleIterator> tupleIterators = new ArrayList<ITupleIterator>();
        for (IAlgebraOperator child : children) {
            ITupleIterator childrenTuple = child.execute(source, target);
            tupleIterators.add(childrenTuple);
        }
        ITupleIterator firstChild = tupleIterators.remove(0);
        ITupleIterator secondChild = tupleIterators.remove(0);
        ITupleIterator currentResult = computeSimpleCartesianProduct(firstChild, secondChild);
        while(!tupleIterators.isEmpty()){
            currentResult = computeSimpleCartesianProduct(currentResult, tupleIterators.remove(0));
        }
        for (ITupleIterator tupleIterator : tupleIterators) {
            tupleIterator.close();
        }
        return currentResult;
    }

    private ITupleIterator computeSimpleCartesianProduct(ITupleIterator leftTuples, ITupleIterator rightTuples) {
        List<Tuple> result = new ArrayList<Tuple>();
        while (leftTuples.hasNext()) {
            Tuple leftTuple = leftTuples.next();
            if (logger.isDebugEnabled()) logger.debug("Left tuple in cartesian product: " + leftTuple);
            while (rightTuples.hasNext()) {
                Tuple rightTuple = rightTuples.next();
                if (logger.isDebugEnabled()) logger.debug("Right tuple in cartesian product: " + rightTuple);
                Tuple joinedTuple = joinTuples(leftTuple, rightTuple);
                result.add(joinedTuple);
            }
            rightTuples.reset();
        }
        leftTuples.reset();
        return new ListTupleIterator(result);
    }

    private Tuple joinTuples(Tuple firstTuple, Tuple secondTuple) {
        Tuple joinedTuple = firstTuple.clone();
        joinedTuple.setOid(new TupleOID(IntegerOIDGenerator.getNextOID()));
        joinedTuple.getCells().addAll(secondTuple.clone().getCells());
        if (logger.isDebugEnabled()) logger.trace("Joined tuple: \n" + joinedTuple);
        return joinedTuple;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitCartesianProduct(this);
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        result.addAll(this.children.get(0).getAttributes(source, target));
        result.addAll(this.children.get(1).getAttributes(source, target));
        return result;
    }
}
