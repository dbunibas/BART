package bart.model.algebra;

import bart.BartConstants;
import bart.utility.BartUtility;
import bart.model.algebra.operators.ListTupleIterator;
import bart.model.algebra.operators.IAlgebraTreeVisitor;
import bart.model.algebra.operators.ITupleIterator;
import bart.model.database.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Project extends AbstractOperator {

    private static Logger logger = LoggerFactory.getLogger(Project.class);
    
    private List<AttributeRef> attributes;
    private List<AttributeRef> newAttributes;
    private boolean discardOids;

    public Project(List<AttributeRef> attributes) {
        this.attributes = attributes;
    }

    public Project(List<AttributeRef> attributes, List<AttributeRef> newAttributes, boolean discardOids) {
        this.attributes = attributes;
        this.newAttributes = newAttributes;
        this.discardOids = discardOids;
    }

    public String getName() {
        return "PROJECT-" + attributes + (newAttributes != null ? " as " + newAttributes : "");
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitProject(this);
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        List<Tuple> result = new ArrayList<Tuple>();
        ITupleIterator originalTuples = children.get(0).execute(source, target);
        materializeResult(originalTuples, result);
        if (logger.isDebugEnabled()) logger.debug(getName() + " - Result: \n" + BartUtility.printCollection(result));
        originalTuples.close();
        return new ListTupleIterator(result);
    }

    private void materializeResult(ITupleIterator originalTuples, List<Tuple> result) {
        while (originalTuples.hasNext()) {
            Tuple originalTuple = originalTuples.next();
            if (logger.isDebugEnabled()) logger.debug("Originale tuple: " + originalTuple.toStringWithOIDAndAlias());
            Tuple projectedTuple = projectTuple(originalTuple);
            if (logger.isDebugEnabled()) logger.debug("Projected tuple: " + projectedTuple.toStringWithOIDAndAlias());
            if (newAttributes != null) {
                projectedTuple = renameAttributes(projectedTuple);
            }
            result.add(projectedTuple);
        }
        checkResult(result);
//        AlgebraUtility.removeDuplicates(result);
    }

    protected Tuple projectTuple(Tuple originalTuple) {
        Tuple tuple = originalTuple.clone();
        List<Cell> cells = tuple.getCells();
        for (Iterator<Cell> it = cells.iterator(); it.hasNext();) {
            Cell cell = it.next();
            if (cell.getAttribute().equals(BartConstants.OID) && !discardOids) {
                TableAlias tableAlias = cell.getAttributeRef().getTableAlias();
                if (isToRemove(tableAlias, attributes)) {
                    it.remove();
                }
            } else if (!attributes.contains(cell.getAttributeRef())) {
                it.remove();
            }
        }
        sortTupleAttributes(tuple, attributes);
        return tuple;
    }

    protected void sortTupleAttributes(Tuple tuple, List<AttributeRef> attributes) {
        List<Cell> sortedCells = new ArrayList<Cell>();
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttribute().equalsIgnoreCase(BartConstants.OID)) {
                BartUtility.addIfNotContained(sortedCells, cell);
            }
        }
        for (AttributeRef attributeRef : attributes) {
            BartUtility.addIfNotContained(sortedCells, tuple.getCell(attributeRef));
        }
        if (tuple.getCells().size() != sortedCells.size()) {
            throw new IllegalArgumentException("Tuples after sorting have differents cells:\n" + tuple + "\n" + sortedCells);
        }
        tuple.setCells(sortedCells);
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        return this.attributes;
    }

    public List<AttributeRef> getNewAttributes() {
        return newAttributes;
    }

    private boolean isToRemove(TableAlias tableAlias, List<AttributeRef> attributes) {
        for (AttributeRef attribute : attributes) {
            if (attribute.getTableAlias().equals(tableAlias)) {
                return false;
            }
        }
        return true;
    }

    private Tuple renameAttributes(Tuple projectedTuple) {
        int i = 0;
        for (Cell cell : projectedTuple.getCells()) {
            if (cell.isOID() && !discardOids) {
                continue;
            }
            cell.setAttributeRef(newAttributes.get(i));
            i++;
        }
        return projectedTuple;
    }

    private void checkResult(List<Tuple> result) {
        if (result.isEmpty()) {
            return;
        }
        Tuple firstTuple = result.get(0);
        List<AttributeRef> attributesToCheck = (newAttributes == null ? attributes : newAttributes);
        for (AttributeRef attribute : attributesToCheck) {
            if (!containsAttribute(firstTuple, attribute)) {
                throw new IllegalArgumentException("Missing attribute " + attribute + " after projection: " + firstTuple.getCells() + " - Expected attributes: " + attributesToCheck);
//                throw new IllegalArgumentException("Missing attribute " + attribute + " after projection: " + firstTuple + " - Expected attributes: " + attributesToCheck);
            }
        }
    }

    private boolean containsAttribute(Tuple tuple, AttributeRef attribute) {
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttributeRef().equals(attribute)) {
                return true;
            }
        }
        return false;
    }
}
