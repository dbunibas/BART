package bart.persistence.xml.operators;

import bart.persistence.xml.model.*;

public interface IXSDNodeVisitor {
        
    void visitSimpleType(SimpleType node);
    
    void visitElementDeclaration(ElementDeclaration node);
    
    void visitTypeCompositor(TypeCompositor node);
    
    void visitAttributeDeclaration(AttributeDeclaration node);
    
    void visitPCDATA(PCDATA node);
    
    Object getResult();
    
}
