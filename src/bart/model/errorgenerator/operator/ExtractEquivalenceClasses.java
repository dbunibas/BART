package bart.model.errorgenerator.operator;

import bart.BartConstants;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.AttributeRef;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;
import bart.model.errorgenerator.EquivalenceClass;
import bart.model.errorgenerator.EquivalenceClassQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractEquivalenceClasses {

    private static Logger logger = LoggerFactory.getLogger(ExtractEquivalenceClasses.class);

    @SuppressWarnings("unchecked")
    public List<EquivalenceClass> getNextCommonEquivalenceClasses(List<EquivalenceClassQuery> equivalenceClassQueries) {
        EquivalenceClassQuery firstEquivalenceQuery = equivalenceClassQueries.get(0);
        while (firstEquivalenceQuery.getIterator().hasNext()) {
            if (noMoreTuples(equivalenceClassQueries)) {
                return Collections.EMPTY_LIST;
            }
            Tuple pivot;
            if (firstEquivalenceQuery.getLastTuple() != null && !firstEquivalenceQuery.isLastTupleHandled()) {
                pivot = firstEquivalenceQuery.getLastTuple();
            } else {
                pivot = firstEquivalenceQuery.getIterator().next();
            }
            if (logger.isDebugEnabled()) logger.debug("Testing pivot " + pivot);
            firstEquivalenceQuery.setLastTuple(pivot);
            firstEquivalenceQuery.setLastTupleHandled(false);
            boolean allFound = true;
            for (int j = 1; j < equivalenceClassQueries.size(); j++) {
                EquivalenceClassQuery otherEquivalenceQuery = equivalenceClassQueries.get(j);
                String pivotFingerprint = computeEqualityFingerprint(pivot, firstEquivalenceQuery.getEqualityAttributes());
                boolean foundedInList = moveIteratorOnPivot(pivotFingerprint, otherEquivalenceQuery);
                if (!foundedInList) {
                    allFound = false;
                }
            }
            if (allFound) {
                return readEquivalenceClasses(equivalenceClassQueries);
            }
            firstEquivalenceQuery.setLastTupleHandled(true);
        }
        return Collections.EMPTY_LIST;
    }

    private boolean noMoreTuples(List<EquivalenceClassQuery> equivalenceClassQueries) {
        for (EquivalenceClassQuery equivalenceClassQuery : equivalenceClassQueries) {
            if (!equivalenceClassQuery.getIterator().hasNext() && (equivalenceClassQuery.isLastTupleHandled() || equivalenceClassQuery.getLastTuple() == null)) {
                return true;
            }
        }
        return false;
    }

    private List<EquivalenceClass> readEquivalenceClasses(List<EquivalenceClassQuery> equivalenceClassQueries) {
        List<EquivalenceClass> result = new ArrayList<EquivalenceClass>();
        for (EquivalenceClassQuery equivalenceClassQuery : equivalenceClassQueries) {
            EquivalenceClass equivalenceClass = readNextEquivalenceClass(equivalenceClassQuery);
            result.add(equivalenceClass);
        }
        if (logger.isInfoEnabled()) logger.info("Common equivalence classes read!");
        if (logger.isDebugEnabled()) logger.trace("*** Equivalence Classes ***\n" + result);
        return result;
    }

    private boolean moveIteratorOnPivot(String pivot, EquivalenceClassQuery equivalenceClassQuery) {
        Tuple currentTuple = equivalenceClassQuery.getLastTuple();
        if (currentTuple == null || equivalenceClassQuery.isLastTupleHandled()) {
            if (!equivalenceClassQuery.getIterator().hasNext()) {
                return false;
            }
            currentTuple = equivalenceClassQuery.getIterator().next();
            equivalenceClassQuery.setLastTuple(currentTuple);
            equivalenceClassQuery.setLastTupleHandled(false);
        }
        if (logger.isDebugEnabled()) logger.debug("Searching pivot: " + pivot + " in equivalence query " + equivalenceClassQuery.getInequalityAttributes());
        do {
            String currentFingerprint = computeEqualityFingerprint(currentTuple, equivalenceClassQuery.getEqualityAttributes());
            if (logger.isDebugEnabled()) logger.debug("Current element: " + currentTuple);
            if (currentFingerprint.equals(pivot)) {
                if (logger.isDebugEnabled()) logger.debug("Founded!");
                return true;
            }
            if (currentFingerprint.compareTo(pivot) > 0) {
                return false;
            }
            if (equivalenceClassQuery.getIterator().hasNext()) {
                currentTuple = equivalenceClassQuery.getIterator().next();
                equivalenceClassQuery.setLastTuple(currentTuple);
                equivalenceClassQuery.setLastTupleHandled(false);
            }
        } while (equivalenceClassQuery.getIterator().hasNext());
        return false;
    }

    private EquivalenceClass readNextEquivalenceClass(EquivalenceClassQuery equivalenceClassQuery) {
        ITupleIterator it = equivalenceClassQuery.getIterator();
        if (!it.hasNext() && (equivalenceClassQuery.isLastTupleHandled() || equivalenceClassQuery.getLastTuple() == null)) {
            return null;
        }
        EquivalenceClass equivalenceClass = new EquivalenceClass(equivalenceClassQuery.getEqualityAttributes());
        equivalenceClass.setEquivalenceClassQuery(equivalenceClassQuery);
        if (equivalenceClassQuery.getLastTuple() != null && !equivalenceClassQuery.isLastTupleHandled()) {
            if (logger.isTraceEnabled()) logger.trace("Reading tuple : " + equivalenceClassQuery.getLastTuple().toStringWithOIDAndAlias());
            equivalenceClass.addTuple(equivalenceClassQuery.getLastTuple());
            equivalenceClassQuery.setLastTupleHandled(true);
        }
        if (logger.isDebugEnabled()) logger.debug("Reading next equivalence class...");
        while (it.hasNext()) {
            Tuple tuple = it.next();
            if (logger.isTraceEnabled()) logger.trace("Reading tuple : " + tuple.toStringWithOIDAndAlias());
            if (equivalenceClassQuery.getLastTuple() == null || sameEquivalenceClass(tuple, equivalenceClassQuery.getLastTuple(), equivalenceClassQuery.getEqualityAttributes())) {
                equivalenceClass.addTuple(tuple);
                equivalenceClassQuery.setLastTuple(tuple);
                equivalenceClassQuery.setLastTupleHandled(true);
            } else {
                if (logger.isDebugEnabled()) logger.debug("Equivalence class is finished...");
                equivalenceClassQuery.setLastTuple(tuple);
                equivalenceClassQuery.setLastTupleHandled(false);
                break;
            }
        }
        return equivalenceClass;
    }

    private boolean sameEquivalenceClass(Tuple tuple, Tuple lastTuple, List<AttributeRef> equalityAttributes) {
        return computeEqualityFingerprint(tuple, equalityAttributes).equals(computeEqualityFingerprint(lastTuple, equalityAttributes));
    }

    private String computeEqualityFingerprint(Tuple tuple, List<AttributeRef> equalityAttributes) {
        StringBuilder sb = new StringBuilder();
        for (AttributeRef attribute : equalityAttributes) {
            IValue tupleValue = tuple.getCell(attribute).getValue();
            sb.append(tupleValue).append(BartConstants.FINGERPRINT_SEPARATOR);
        }
        return sb.toString();
    }
}
