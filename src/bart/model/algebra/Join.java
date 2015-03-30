package bart.model.algebra;

import bart.utility.AlgebraUtility;
import bart.utility.BartUtility;
import bart.model.algebra.operators.*;
import bart.model.database.*;
import bart.model.database.mainmemory.datasource.IntegerOIDGenerator;
import bart.model.database.NullValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Join extends AbstractOperator {

    private static Logger logger = LoggerFactory.getLogger(Join.class);

    protected List<AttributeRef> leftAttributes;
    protected List<AttributeRef> rightAttributes;
    protected AlgebraUtility utility = new AlgebraUtility();

    public Join(AttributeRef leftAttribute, AttributeRef rightAttribute) {
        this.leftAttributes = new ArrayList<AttributeRef>();
        this.leftAttributes.add(leftAttribute);
        this.rightAttributes = new ArrayList<AttributeRef>();
        this.rightAttributes.add(rightAttribute);
    }

    public Join(List<AttributeRef> leftAttributes, List<AttributeRef> rightAttributes) {
        if (leftAttributes.size() != rightAttributes.size()) {
            throw new IllegalArgumentException("Join attributes cannot have different size: " + leftAttributes + " - " + rightAttributes);
        }
        this.leftAttributes = leftAttributes;
        this.rightAttributes = rightAttributes;
    }

    public String getName() {
        return leftAttributes + "-JOIN-" + rightAttributes;
    }

    protected int getAttributeSize() {
        return this.leftAttributes.size();
    }

    public List<AttributeRef> getLeftAttributes() {
        return leftAttributes;
    }

    public void setLeftAttributes(List<AttributeRef> leftAttributes) {
        this.leftAttributes = leftAttributes;
    }

    public List<AttributeRef> getRightAttributes() {
        return rightAttributes;
    }

    public void setRightAttributes(List<AttributeRef> rightAttributes) {
        this.rightAttributes = rightAttributes;
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        List<Tuple> result = new ArrayList<Tuple>();
        ITupleIterator leftTuples = children.get(0).execute(source, target);
        ITupleIterator rightTuples = children.get(1).execute(source, target);
        materializeResult(leftTuples, rightTuples, result);
        if (logger.isDebugEnabled()) logger.debug(getName() + " - Result: \n" + BartUtility.printCollection(result));
        leftTuples.close();
        rightTuples.close();
        return new ListTupleIterator(result);
    }

    public void materializeResult(ITupleIterator leftTuples, ITupleIterator rightTuples, List<Tuple> result) {
        Map<String, ListPair> joinMap = new HashMap<String, ListPair>();
        while (leftTuples.hasNext()) {
            Tuple leftTuple = leftTuples.next();
            if (logger.isDebugEnabled()) logger.debug("Left tuple in join: " + leftTuple);
            List<Object> leftTupleValues = findTupleValues(leftAttributes, leftTuple);
            if (leftTupleValues.size() != getAttributeSize()) {
                if (logger.isDebugEnabled()) logger.debug("Tuple " + leftTuple + " has " + leftTupleValues.size() + " values, instead of " + getAttributeSize() + ". Skipping...");
                continue;
            }
            ListPair listPair = getListPair(leftTupleValues.toString(), joinMap);
            listPair.leftTuples.add(leftTuple);
            if (logger.isDebugEnabled()) logger.debug("Adding left tuple to join map " + leftTupleValues.toString());
        }
        leftTuples.reset();
        while (rightTuples.hasNext()) {
            Tuple rightTuple = rightTuples.next();
            if (logger.isDebugEnabled()) logger.debug("Right tuple in join: " + rightTuple);
            List<Object> rightTupleValues = findTupleValues(rightAttributes, rightTuple);
            if (rightTupleValues.size() != getAttributeSize()) {
                if (logger.isDebugEnabled()) logger.debug("Tuple " + rightTuple + " has " + rightTupleValues.size() + " values, instead of " + getAttributeSize() + ". Skipping...");
                continue;
            }
            ListPair listPair = getListPair(rightTupleValues.toString(), joinMap);
            listPair.rightTuples.add(rightTuple);
            if (logger.isDebugEnabled()) logger.debug("Adding right tuple to join map " + rightTupleValues.toString());
        }
        rightTuples.reset();
        if (logger.isDebugEnabled()) logger.debug("Join map: " + BartUtility.printMap(joinMap));
        for (String key : joinMap.keySet()) {
            ListPair listPair = joinMap.get(key);
            for (Tuple leftTuple : listPair.leftTuples) {
                for (Tuple rightTuple : listPair.rightTuples) {
                    if (logger.isDebugEnabled()) logger.debug("Joining tuples: \n" + leftTuple + "\n" + rightTuple + "\n" + leftAttributes + "=" + rightAttributes);
                    List<Object> leftTupleValues = findTupleValues(leftAttributes, leftTuple);
                    if (logger.isDebugEnabled()) logger.debug("Left values: " + leftTupleValues);
                    List<Object> rightTupleValues = findTupleValues(rightAttributes, rightTuple);
                    if (logger.isDebugEnabled()) logger.debug("Right values: " + rightTupleValues);
                    if (equalListsForJoin(leftTupleValues, rightTupleValues)) {
                        if (logger.isDebugEnabled()) logger.debug("References match, joining tuples");
                        result.add(joinTuples(leftTuple, rightTuple));
                    }
                }
            }
        }
    }

    private ListPair getListPair(String key, Map<String, ListPair> joinMap) {
        ListPair listResult = joinMap.get(key);
        if (listResult == null) {
            listResult = new ListPair();
            joinMap.put(key, listResult);
        }
        return listResult;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitJoin(this);
    }

    @SuppressWarnings("unchecked")
//    protected boolean equalListsForJoin(List list1, List list2) {
//        if (list1.size() != getAttributeSize() || list2.size() != getAttributeSize()) {
//            return false;
//        }
//        return (list1.containsAll(list2) && list2.containsAll(list1));
//    }
    protected boolean equalListsForJoin(List list1, List list2) {
        return list1.toString().equals(list2.toString());
    }

    protected static boolean isEmpty(List list) {
        for (Object element : list) {
            if (element != null && !(element instanceof NullValue) && !(element.toString().equals("NULL"))) {
                return false;
            }
        }
        return true;
    }

    protected List<Object> findTupleValues(List<AttributeRef> attributes, Tuple tuple) {
        List<Object> values = new ArrayList<Object>();
        for (AttributeRef attribute : attributes) {
            IValue attributeValue = AlgebraUtility.getCellValue(tuple, attribute);
            if ((attributeValue instanceof NullValue) && !((NullValue) attributeValue).isLabeledNull()) {
                continue;
            }
            values.add(attributeValue.getPrimitiveValue());
        }
        return values;
    }

    protected Tuple joinTuples(Tuple firstTuple, Tuple secondTuple) {
        Tuple joinedTuple = firstTuple.clone();
        joinedTuple.setOid(new TupleOID(IntegerOIDGenerator.getNextOID()));
        joinedTuple.getCells().addAll(secondTuple.clone().getCells());
        if (logger.isDebugEnabled()) logger.trace("Joined tuple: \n" + joinedTuple);
        return joinedTuple;
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        result.addAll(this.children.get(0).getAttributes(source, target));
        result.addAll(this.children.get(1).getAttributes(source, target));
        return result;
    }
}

class ListPair {

    List<Tuple> leftTuples = new ArrayList<Tuple>();
    List<Tuple> rightTuples = new ArrayList<Tuple>();

    @Override
    public String toString() {
        return "ListPair{" + "leftTuples=" + leftTuples + ", rightTuples=" + rightTuples + '}';
    }
}
