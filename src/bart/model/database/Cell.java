package bart.model.database;

import bart.BartConstants;
import java.io.Serializable;

public class Cell implements Serializable {

    private TupleOID tupleOid;
    private AttributeRef attributeRef;
    private IValue value;

    public Cell(TupleOID tupleOid, AttributeRef attributeRef, IValue value) {
        this.tupleOid = tupleOid;
        this.attributeRef = attributeRef;
        this.value = value;
    }

    public Cell(CellRef cellRef, IValue value) {
        this(cellRef.getTupleOID(), cellRef.getAttributeRef(), value);
    }

    public Cell(Cell originalCell, Tuple newTuple) {
        this.tupleOid = newTuple.getOid();
        this.attributeRef = originalCell.attributeRef;
        this.value = originalCell.value;
    }

    public boolean isOID() {
        return attributeRef.getName().equals(BartConstants.OID);
    }

    public AttributeRef getAttributeRef() {
        return attributeRef;
    }

    public void setAttributeRef(AttributeRef attributeRef) {
        this.attributeRef = attributeRef;
    }

    public String getAttribute() {
        return attributeRef.getName();
    }

    public IValue getValue() {
        return value;
    }

    public TupleOID getTupleOID() {
        return tupleOid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    public boolean equalsModuloAlias(Cell other) {
        return this.getTupleOID().equals(other.getTupleOID()) && this.getAttributeRef().toStringNoAlias().equals(other.getAttributeRef().toStringNoAlias());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return tupleOid + ":" + attributeRef.toStringNoAlias() + "-" + value;
    }

    public String toShortString() {
        return attributeRef.getName() + ":" + value;
    }

    public String toStringWithAlias() {
        return attributeRef + ":" + value;
    }

    public String toStringWithOIDAndAlias() {
        return tupleOid + ":" + attributeRef + ":" + value;
    }
}
