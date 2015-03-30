package bart.model.algebra;

import bart.exceptions.AlgebraException;
import bart.model.algebra.operators.*;
import bart.model.database.AttributeRef;
import bart.model.database.IDatabase;
import bart.model.database.Tuple;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Offset extends AbstractOperator {

    private static Logger logger = LoggerFactory.getLogger(Offset.class);

    private int offset;

    public Offset(int size) {
        this.offset = size;
    }

    public String getName() {
        return "OFFSET " + offset;
    }

    public int getOffset() {
        return offset;
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        ITupleIterator leftTuples = children.get(0).execute(source, target);
        return new OffsetTupleIterator(leftTuples, offset);
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitOffset(this);
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        return this.children.get(0).getAttributes(source, target);
    }
}

class OffsetTupleIterator implements ITupleIterator {

    private ITupleIterator tupleIterator;

    public OffsetTupleIterator(ITupleIterator tupleIterator, int offset) {
        this.tupleIterator = tupleIterator;
        for (int i = 0; i < offset; i++) {
            if (tupleIterator.hasNext()) {
                tupleIterator.next();
            }
        }
    }

    public void reset() {
        this.tupleIterator.reset();
    }

    public boolean hasNext() {
        return tupleIterator.hasNext();
    }

    public Tuple next() {
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
