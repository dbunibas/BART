package bart.model.algebra;

import bart.exceptions.AlgebraException;
import bart.model.algebra.operators.*;
import bart.model.database.AttributeRef;
import bart.model.database.IDatabase;
import bart.model.database.Tuple;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Limit extends AbstractOperator {

    private static Logger logger = LoggerFactory.getLogger(Limit.class);

    private int size;

    public Limit(int size) {
        this.size = size;
    }

    public String getName() {
        return "LIMIT " + size;
    }

    public int getSize() {
        return size;
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        ITupleIterator leftTuples = children.get(0).execute(source, target);
        return new LimitTupleIterator(leftTuples, size);
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitLimit(this);
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        return this.children.get(0).getAttributes(source, target);
    }
}

class LimitTupleIterator implements ITupleIterator {

    private ITupleIterator tupleIterator;
    private int read = 0;
    private int limit;

    public LimitTupleIterator(ITupleIterator tupleIterator, int limit) {
        this.tupleIterator = tupleIterator;
        this.limit = limit;
    }

    public void reset() {
        this.tupleIterator.reset();
        this.read = 0;
    }

    public boolean hasNext() {
        return tupleIterator.hasNext() && read < limit;
    }

    public Tuple next() {
        if (read >= limit) {
            throw new AlgebraException("No more elements in limit");
        }
        read++;
        return tupleIterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public int size() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void close() {
        tupleIterator.close();
    }
}
