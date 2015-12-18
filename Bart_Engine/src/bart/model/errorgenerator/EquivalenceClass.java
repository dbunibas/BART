package bart.model.errorgenerator;

import speedy.model.database.AttributeRef;
import speedy.model.database.Tuple;
import bart.utility.BartUtility;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EquivalenceClass {

    private EquivalenceClassQuery equivalenceClassQuery;
    private List<Tuple> tuples = new ArrayList<Tuple>();
    private List<AttributeRef> equalityAttributes;

    public EquivalenceClass(List<AttributeRef> equalityAttributes) {
        this.equalityAttributes = equalityAttributes;
    }

    public List<AttributeRef> getEqualityAttributes() {
        return equalityAttributes;
    }

    public void setEqualityAttributes(List<AttributeRef> equalityAttributes) {
        this.equalityAttributes = equalityAttributes;
    }

    public List<Tuple> getTuples() {
        return tuples;
    }

    public void addTuple(Tuple tuple) {
        this.tuples.add(tuple);
    }

    public void addAllTuple(Collection<Tuple> tuples) {
        this.tuples.addAll(tuples);
    }

    public EquivalenceClassQuery getEquivalenceClassQuery() {
        return equivalenceClassQuery;
    }

    public void setEquivalenceClassQuery(EquivalenceClassQuery equivalenceClassQuery) {
        this.equivalenceClassQuery = equivalenceClassQuery;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Equivalence Class for VioGenQuery\n");
        sb.append(BartUtility.printCollection(tuples));
        return sb.toString();
    }

}
