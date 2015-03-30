package bart.persistence.xml.operators;

import bart.persistence.xml.model.XSDSchema;
import org.apache.xerces.impl.xs.identity.KeyRef;
import org.apache.xerces.impl.xs.identity.UniqueOrKey;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LoadXsdConstraints {
    
    private static Logger logger = LoggerFactory.getLogger(LoadXsdConstraints.class);
        
    void checkElementConstraints(XSDSchema xsdSchema, XSElementDeclaration element){
        XSNamedMap constraints = element.getIdentityConstraints();
        for (int i = 0;  i < constraints.getLength();  i++) {
            XSObject constraint = constraints.item(i);
            if (logger.isDebugEnabled()) logger.debug(" *** " + constraint + " TYPE: " +constraint.getType());
            if (constraint instanceof UniqueOrKey) {
                processUniqueOrKeyConstraint(xsdSchema, (UniqueOrKey)constraint);
            }
            if (constraint instanceof KeyRef) {
                processForeignkeyConstraint(xsdSchema, (KeyRef)constraint);
            }
        }
    }
    
    private void processUniqueOrKeyConstraint(XSDSchema xsdSchema, UniqueOrKey constraint){
        StringList fields = constraint.getFieldStrs();
        String constraintName = constraint.getName();
        String fieldStr;
        String[] vectStrs;
        for(int i = 0; i < fields.getLength(); i++){
            fieldStr = fields.item(i);
            vectStrs = getVectorStrings(constraint.getElementName(),constraint.getSelectorStr(),fieldStr);
            if (constraint.getCategory() == UniqueOrKey.IC_KEY) {
                xsdSchema.addKeyXPathStr(constraintName, vectStrs);
            }
            if (constraint.getCategory() == UniqueOrKey.IC_UNIQUE) {
                xsdSchema.addUniqueXPathStr(constraintName, vectStrs);
            }
        }
    }
    
    private void processForeignkeyConstraint(XSDSchema xsdSchema, KeyRef constraint){
        StringList fields = constraint.getFieldStrs();
        // keyref's name: FK|PK
        String constraintName = constraint.getName() + "|" + constraint.getKey().getName();
        String fieldStr;
        String[] vectStrs;
        for(int i = 0; i < fields.getLength(); i++){
            fieldStr = fields.item(i);
            vectStrs = getVectorStrings(constraint.getElementName(),constraint.getSelectorStr(),fieldStr);
            xsdSchema.addForeignKeyXPathStr(constraintName, vectStrs);
        }
    }
    
    private String[] getVectorStrings(String elementStr, String selectorStr, String fieldStr) {
        String[] vectStrs = new String[3];
        vectStrs[0] = elementStr;
        vectStrs[1] = selectorStr;
        vectStrs[2] = fieldStr;
        return vectStrs;
    }
    
}