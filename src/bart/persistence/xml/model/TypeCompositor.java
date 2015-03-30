package bart.persistence.xml.model;

import bart.persistence.xml.operators.IXSDNodeVisitor;

public class TypeCompositor extends Particle {
    
    public static final String SEQUENCE = "SEQUENCE";
    public static final String ALL = "ALL";
    public static final String CHOICE = "CHOICE";
    public static final String ATTLIST = "ATTLIST";
    
    public TypeCompositor(String label) {
        super(label);
    }

    public void accept(IXSDNodeVisitor visitor) {
        visitor.visitTypeCompositor(this);
    }
    
}
