package bart.model.errorgenerator;

import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.AttributeRef;
import speedy.model.database.Tuple;
import java.util.List;

public class EquivalenceClassQuery {

    private IAlgebraOperator query;
    private List<AttributeRef> equalityAttributes;
    private List<List<AttributeRef>> inequalityAttributes;
    private ITupleIterator iterator;
    private Tuple lastTuple;
    private boolean lastTupleHandled;

    public EquivalenceClassQuery(IAlgebraOperator query, List<AttributeRef> equalityAttributes, List<List<AttributeRef>> inequalityAttributes) {
        this.query = query;
        this.equalityAttributes = equalityAttributes;
        this.inequalityAttributes = inequalityAttributes;
    }

    public IAlgebraOperator getQuery() {
        return query;
    }

    public List<AttributeRef> getEqualityAttributes() {
        return equalityAttributes;
    }

    public List<List<AttributeRef>> getInequalityAttributes() {
        return inequalityAttributes;
    }

    public ITupleIterator getIterator() {
        return iterator;
    }

    public void setIterator(ITupleIterator iterator) {
        this.iterator = iterator;
    }

    public Tuple getLastTuple() {
        return this.lastTuple;
    }

    public void setLastTuple(Tuple lastTuple) {
        this.lastTuple = lastTuple;
    }

    public boolean isLastTupleHandled() {
        return this.lastTupleHandled;
    }

    public void setLastTupleHandled(boolean lastTupleHandled) {
        this.lastTupleHandled = lastTupleHandled;
    }

    @Override
    public String toString() {
        return "Equivalence Class Query:\n" + query + "\n\tequality attributes: " + equalityAttributes + "\n\tinequality attributes: " + inequalityAttributes;
    }

}
