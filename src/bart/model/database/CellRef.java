package bart.model.database;

import java.io.Serializable;

public class CellRef implements Serializable {
    
    private TupleOID tupleOID;
    private AttributeRef attributeRef;
    
    public CellRef(Cell cell) {
        this(cell.getTupleOID(), cell.getAttributeRef());
    }

    public CellRef(TupleOID tupleOID, AttributeRef attributeRef) {
        this.tupleOID = tupleOID;
        this.attributeRef = attributeRef;
    }

    public AttributeRef getAttributeRef() {
        return attributeRef;
    }

    public TupleOID getTupleOID() {
        return tupleOID;
    }

//    @Override
//    public int hashCode() {
//        return this.toString().hashCode();
//    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) return false;
//        return this.toString().equals(obj.toString());
//    }    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.tupleOID != null ? this.tupleOID.hashCode() : 0);
        hash = 57 * hash + (this.attributeRef != null ? this.attributeRef.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final CellRef other = (CellRef) obj;
        if (this.tupleOID != other.tupleOID && (this.tupleOID == null || !this.tupleOID.equals(other.tupleOID))) return false;
        if (this.attributeRef != other.attributeRef && (this.attributeRef == null || !this.attributeRef.equals(other.attributeRef))) return false;
        return true;
    }
    
    @Override
    public String toString() {
//        return tupleOID + ":" + attributeRef.getName();
        return tupleOID + ":" + attributeRef;
    }
    
}
