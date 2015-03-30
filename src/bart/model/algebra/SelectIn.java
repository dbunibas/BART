package bart.model.algebra;

import bart.BartConstants;
import bart.utility.BartUtility;
import bart.model.algebra.operators.IAlgebraTreeVisitor;
import bart.model.algebra.operators.ITupleIterator;
import bart.model.database.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectIn extends AbstractOperator {

    private static Logger logger = LoggerFactory.getLogger(SelectIn.class);

    private List<AttributeRef> attributes;
    private List<IAlgebraOperator> selectionOperators;

    public SelectIn(List<AttributeRef> attributes, List<IAlgebraOperator> selectionOperators) {
        assert(!selectionOperators.isEmpty());
        this.attributes = attributes;
        this.selectionOperators = selectionOperators;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitSelectIn(this);
    }

    public String getName() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT").append(attributes).append(" IN (\n");
        for (IAlgebraOperator selectionOperator : selectionOperators) {
            sb.append(selectionOperator.toString(BartConstants.INDENT + BartConstants.INDENT)).append("\n");
        }
        sb.append(")");
        return sb.toString();
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        for (IAlgebraOperator selectionOperator : selectionOperators) {
            if (attributes.size() != selectionOperator.getAttributes(source, target).size()) {
                throw new IllegalArgumentException("Attribute sizes are different: " + attributes + " - " + selectionOperator.getAttributes(source, target));
            }
        }
        List<Map<AttributeRef, Set<IValue>>> valueMap = materializeInnerOperator(source, target);
        SelectInTupleIterator tupleIterator = new SelectInTupleIterator(children.get(0).execute(source, target), valueMap);
        if (logger.isDebugEnabled()) logger.debug("Executing SelectIn: " + getName() + " in attributes\n" + attributes + "Map:\n" + BartUtility.printCollection(valueMap) + " on source\n" + (source == null ? "" : source.printInstances()) + "\nand target:\n" + target.printInstances());
        if (logger.isDebugEnabled()) logger.debug("Result: " + BartUtility.printTupleIterator(tupleIterator));
        if (logger.isDebugEnabled()) tupleIterator.reset();
        return tupleIterator;
    }

    private List<Map<AttributeRef, Set<IValue>>> materializeInnerOperator(IDatabase source, IDatabase target) {
        List<Map<AttributeRef, Set<IValue>>> result = new ArrayList<Map<AttributeRef, Set<IValue>>>();
        for (IAlgebraOperator selectionOperator : selectionOperators) {
            Map<AttributeRef, Set<IValue>> valuesForOperator = new HashMap<AttributeRef, Set<IValue>>();
            result.add(valuesForOperator);
            ITupleIterator tuples = selectionOperator.execute(source, target);
            while (tuples.hasNext()) {
                Tuple tuple = tuples.next();
                int i = 0;
                for (Cell cell : tuple.getCells()) {
                    if (cell.isOID()) {
                        continue;
                    }
                    IValue value = cell.getValue();
                    Set<IValue> attributeSet = getAttributeSet(valuesForOperator, attributes.get(i));
                    attributeSet.add(value);
                    i++;
                }
            }
        }
        return result;
    }

    private Set<IValue> getAttributeSet(Map<AttributeRef, Set<IValue>> map, AttributeRef attribute) {
        Set<IValue> result = map.get(attribute);
        if (result == null) {
            result = new HashSet<IValue>();
            map.put(attribute, result);
        }
        return result;
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        return attributes;
    }

    public List<IAlgebraOperator> getSelectionOperators() {
        return selectionOperators;
    }

    @Override
    public IAlgebraOperator clone() {
        SelectIn clone = (SelectIn) super.clone();
        clone.selectionOperators = new ArrayList<IAlgebraOperator>();
        for (IAlgebraOperator selectionOperator : selectionOperators) {
            clone.selectionOperators.add((Scan) selectionOperator.clone());
        }
        return clone;
    }

    class SelectInTupleIterator implements ITupleIterator {

        private ITupleIterator tableIterator;
        private Tuple nextTuple;
        private List<Map<AttributeRef, Set<IValue>>> valueMaps;

        public SelectInTupleIterator(ITupleIterator tableIterator, List<Map<AttributeRef, Set<IValue>>> valueMaps) {
            this.valueMaps = valueMaps;
            this.tableIterator = tableIterator;
        }

        public boolean hasNext() {
            if (nextTuple != null) {
                return true;
            } else {
                loadNextTuple();
                return nextTuple != null;
            }
        }

        private void loadNextTuple() {
            while (tableIterator.hasNext()) {
                Tuple tuple = tableIterator.next();
                if (conditionsAreTrue(tuple)) {
                    nextTuple = tuple;
                    return;
                }
            }
            nextTuple = null;
        }

        private boolean conditionsAreTrue(Tuple tuple) {
            for (Map<AttributeRef, Set<IValue>> valueMap : valueMaps) {
                if (valueMap.keySet().isEmpty()) {
                    return false;
                }
                for (AttributeRef attributeRef : valueMap.keySet()) {
                    IValue valueToCheck = tuple.getCell(attributeRef).getValue();
                    Set<IValue> valueToPick = valueMap.get(attributeRef);
                    if (!valueToPick.contains(valueToCheck)) {
                        return false;
                    }
                }
            }
            return true;
        }

        public Tuple next() {
            if (nextTuple != null) {
                Tuple result = nextTuple;
                nextTuple = null;
                return result;
            }
            return null;
        }

        public void reset() {
            this.tableIterator.reset();
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        public void close() {
            tableIterator.close();
        }
    }
}
