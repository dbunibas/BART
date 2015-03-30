package bart.model.algebra;

import bart.model.algebra.operators.IAlgebraTreeVisitor;
import bart.model.algebra.operators.ITupleIterator;
import bart.model.database.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestoreOIDs extends AbstractOperator {

    private static Logger logger = LoggerFactory.getLogger(RestoreOIDs.class);

    private AttributeRef oidAttribute;

    public RestoreOIDs(AttributeRef oidAttribute) {
        this.oidAttribute = oidAttribute;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitRestoreOIDs(this);
    }

    public String getName() {
        return "RESTORE-OIDS-";
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        if (logger.isDebugEnabled()) logger.debug("Executing RestoreOID: " + getName() + " on source\n" + (source == null ? "" : source.printInstances()) + "\nand target:\n" + target.printInstances());
        return new RestoreOIDTupleIterator(children.get(0).execute(source, target));
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        List<AttributeRef> attributes = new ArrayList<AttributeRef>(this.children.get(0).getAttributes(source, target));
        attributes.remove(oidAttribute);
        return attributes;
    }

    @Override
    public IAlgebraOperator clone() {
        RestoreOIDs clone = (RestoreOIDs) super.clone();
        return clone;
    }

    class RestoreOIDTupleIterator implements ITupleIterator {

        private ITupleIterator childIterator;

        public RestoreOIDTupleIterator(ITupleIterator tableIterator) {
            this.childIterator = tableIterator;
        }

        public boolean hasNext() {
            return childIterator.hasNext();
        }

        public Tuple next() {
            Tuple tuple = childIterator.next();
            if (logger.isDebugEnabled()) logger.debug("Original tuple: " + tuple.toStringWithOID());
            IValue oid = tuple.getCell(oidAttribute).getValue();
            Tuple clone = new Tuple(new TupleOID(oid));
            for (Cell cell : tuple.getCells()) {
//                if (cell.isOID()) {
//                    continue;
//                }
                clone.addCell(new Cell(cell, clone));
            }
            if (logger.isDebugEnabled()) logger.debug("New tuple: " + clone.toStringWithOID());
            return clone;
        }

        public void reset() {
            this.childIterator.reset();
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        public void close() {
            childIterator.close();
        }
    }
}
