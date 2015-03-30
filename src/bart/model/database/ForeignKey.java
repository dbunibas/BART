package bart.model.database;

import java.util.ArrayList;
import java.util.List;

public class ForeignKey {

    private List<AttributeRef> keyAttributes;
    private List<AttributeRef> refAttributes;

    public ForeignKey(List<AttributeRef> keyAttributes, List<AttributeRef> refAttributes) {
        if (keyAttributes.size() != refAttributes.size()) throw new IllegalArgumentException("Key and foreign key paths have different sizes: " + keyAttributes + " - " + refAttributes);
        this.keyAttributes = keyAttributes;
        this.refAttributes = refAttributes;
    }
          
    public ForeignKey(AttributeRef keyAttribute, AttributeRef referenceAttribute) {
        this.keyAttributes = new ArrayList<AttributeRef>();
        this.keyAttributes.add(keyAttribute);
        this.refAttributes = new ArrayList<AttributeRef>();
        this.refAttributes.add(referenceAttribute);
    }

    public List<AttributeRef> getRefAttributes() {
        return refAttributes;
    }

    public List<AttributeRef> getKeyAttributes() {
        return keyAttributes;
    }
    

    public String toString() {
        return this.refAttributes + " references " + this.keyAttributes;
    }
    
    
}
