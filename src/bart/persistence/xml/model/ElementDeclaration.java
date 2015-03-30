package bart.persistence.xml.model;

import bart.persistence.xml.operators.IXSDNodeVisitor;

public class ElementDeclaration extends Particle {
    
    public ElementDeclaration(String label) {
        super(label);
    }

    public void accept(IXSDNodeVisitor visitor) {
        visitor.visitElementDeclaration(this);
    }

}
