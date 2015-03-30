package bart.model.algebra.operators;

import bart.model.database.Tuple;
import java.util.Iterator;
import java.util.List;

public class ListTupleIterator implements ITupleIterator {

    private List<Tuple> tuples;
    private Iterator<Tuple> iterator;

    public ListTupleIterator(List<Tuple> tuples) {
        this.tuples = tuples;
        this.iterator = tuples.iterator();
    }

    public void reset() {
        this.iterator = tuples.iterator();
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public Tuple next() {
        return this.iterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public int size() {
        return this.tuples.size();
    }

    public void close() {
    }
}
