package bart.persistence.xml.operators;

import bart.persistence.Types;
import bart.persistence.xml.IllegalSchemaException;
import bart.persistence.xml.model.*;
import java.util.HashMap;
import java.util.Map;
import org.apache.xerces.impl.xs.XSAttributeUseImpl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

public class GenerateXSDNodeTree {
    
    private static Logger logger = LoggerFactory.getLogger(GenerateXSDNodeTree.class);
    
    private Map<String, IXSDNode> globalElements = new HashMap<String, IXSDNode>();
    private XSDSchema xsdSchema;
    
    private LoadXsdConstraints constraintLoader = new LoadXsdConstraints();
    
    public XSDSchema generateXSDNodeTree(String fileName) throws Exception {
        this.xsdSchema = new XSDSchema();
        XSModel model = initModel(fileName);
        XSNamedMap elements = model.getComponents(XSConstants.ELEMENT_DECLARATION);
        for (int i = 0;  i < elements.getLength();  i++) {
            XSElementDecl element = (XSElementDecl) elements.item(i);
            analyzeElement(null, null, element);
        }
        IXSDNode rootNode = findRoot();
        xsdSchema.setRoot(rootNode);
        return xsdSchema;
    }
    
    private XSModel initModel(String fileName) throws Exception {
        System.setProperty(DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMXSImplementationSourceImpl");
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        XSImplementation impl = (XSImplementation) registry.getDOMImplementation("XS-Loader");
        XSLoader schemaLoader = impl.createXSLoader(null);
        DOMConfiguration config = schemaLoader.getConfig();
        config.setParameter("validate", Boolean.TRUE);
        return schemaLoader.loadURI(fileName);
    }
    
    private void analyzeElement(IXSDNode currentNode, XSParticleDecl fatherParticle, XSElementDecl elementDeclaration) {
        if (logger.isDebugEnabled()) logger.debug("analyzeElement: " + elementDeclaration.getName());
        String elementName = elementDeclaration.getName();
        IXSDNode element = null;
        if (elementDeclaration.getScope() == XSConstants.SCOPE_GLOBAL) {
            if (logger.isDebugEnabled()) logger.debug(elementName + " has GLOBAL SCOPE");
            element = globalElements.get(elementName);
            if (element == null) {
                element = createNewElement(elementDeclaration);
                globalElements.put(elementName, element);
                analyzeType(element, fatherParticle, elementDeclaration);
            } else {
                element.setNested(true);
                element = element.clone();
                setCardinality(element, fatherParticle);
            }
        } else {
            element = createNewElement(elementDeclaration);
            analyzeType(element, fatherParticle, elementDeclaration);
        }
        if (currentNode != null) {
            currentNode.addChild(element);
        }
    }
    
    private IXSDNode createNewElement(XSElementDecl elementDeclaration) {
        String elementName = elementDeclaration.getName();
        IXSDNode element = new ElementDeclaration(elementName);
        constraintLoader.checkElementConstraints(xsdSchema, elementDeclaration);
        element.setNullable(elementDeclaration.getNillable());
        return element;
    }
    
    private void analyzeType(IXSDNode currentNode, XSParticleDecl fatherParticle, XSElementDecl currentElementDeclaration) {
        if (currentElementDeclaration.getTypeDefinition().getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
            manageSimpleTypeElement(currentNode, fatherParticle, currentElementDeclaration);
        } else {
            manageComplexTypeElement(currentNode, fatherParticle, currentElementDeclaration);
        }
    }
    
    //// SIMPLE TYPE
    private void manageSimpleTypeElement(IXSDNode currentNode, XSParticleDecl fatherParticle, XSElementDecl currentElementDeclaration) {
        XSSimpleTypeDefinition simpleTypeDefinition = (XSSimpleTypeDefinition) currentElementDeclaration.getTypeDefinition();
        if (logger.isDebugEnabled()) logger.debug("Found a simple type: " + currentElementDeclaration.getName() + " with type: " + simpleTypeDefinition.getBuiltInKind());
        SimpleType simpleType = new SimpleType(getLeafType(simpleTypeDefinition.getBuiltInKind()));
        currentNode.addChild(simpleType);
        setCardinality(currentNode, fatherParticle);
    }
    
    private String getLeafType(int value){
        switch(value){
            case XSConstants.BOOLEAN_DT:
                return Types.BOOLEAN;
            case XSConstants.STRING_DT:
                return Types.STRING;
            case XSConstants.BYTE_DT:
            case XSConstants.SHORT_DT:
            case XSConstants.INTEGER_DT:
            case XSConstants.INT_DT:
            case XSConstants.POSITIVEINTEGER_DT:
            case XSConstants.NEGATIVEINTEGER_DT:
            case XSConstants.NONPOSITIVEINTEGER_DT:
            case XSConstants.NONNEGATIVEINTEGER_DT:
                return Types.INTEGER;
            case XSConstants.LONG_DT:
                return Types.LONG;
            case XSConstants.DECIMAL_DT:
            case XSConstants.FLOAT_DT:
            case XSConstants.DOUBLE_DT:
                return Types.DOUBLE;
            case XSConstants.DATE_DT:
                return Types.DATE;
            case XSConstants.TIME_DT:
            case XSConstants.DATETIME_DT:
                return Types.DATETIME;
            default:
                return Types.STRING;
        }
    }
    
    //// COMPLEX TYPE
    private void manageComplexTypeElement(IXSDNode currentNode, XSParticleDecl fatherParticle, XSElementDecl currentElementDeclaration) {
        if (logger.isDebugEnabled()) logger.debug("Found a complex type: " + currentElementDeclaration.getName());
        setCardinality(currentNode, fatherParticle);
        XSComplexTypeDecl complexTypeDefinition = (XSComplexTypeDecl) currentElementDeclaration.getTypeDefinition();
        if (checkContentTypeMixed(complexTypeDefinition)) {
            currentNode.setMixedContent(true);
            if (logger.isDebugEnabled()) logger.debug("Current node: " + currentNode + " with father particle: " + fatherParticle + " and current element declaration: " + currentElementDeclaration);
        }
        XSParticleDecl currentParticle = (XSParticleDecl) complexTypeDefinition.getParticle();
        analyzeAttributes(currentNode, currentElementDeclaration);
        if (currentParticle == null){
            return;
        }
        analyzeParticle(currentNode, fatherParticle, currentParticle);
    }
    
    private boolean checkContentTypeMixed(XSComplexTypeDecl complexTypeDefinition){
        if (logger.isDebugEnabled()) logger.debug("ContentType = " + complexTypeDefinition.getContentType());
        if(complexTypeDefinition.getContentType() == XSComplexTypeDecl.CONTENTTYPE_MIXED){
            return true;
        } else{
            return false;
        }
    }
    
    private void analyzeParticle(IXSDNode currentNode, XSParticleDecl fatherParticle, XSParticleDecl currentParticle) {
        if (logger.isDebugEnabled()) logger.debug("analyzing particle: " + currentParticle);
        XSTerm currentTerm = currentParticle.getTerm();
        if (currentTerm instanceof XSWildcard) {
            throw new IllegalSchemaException("Term is a wildcard: " + currentParticle + " - Current node: " + currentNode + " - Father particle: " + fatherParticle);
        }
        if (currentTerm instanceof XSElementDecl) {
            if (logger.isDebugEnabled()) logger.debug("Found an element: " + currentTerm.toString() + " with scope " + ((XSElementDecl)currentTerm).getScope());
            analyzeElement(currentNode, currentParticle, (XSElementDecl) currentTerm);
        }
        if (currentTerm instanceof XSModelGroupImpl) {
            if (logger.isDebugEnabled()) logger.debug("Found a nested group in: " + currentTerm);
            analyzeModelGroup(currentNode, currentParticle, (XSModelGroupImpl)currentTerm);
        }
    }
    
    private void analyzeAttributes(IXSDNode currentNode, XSElementDecl currentElementDeclaration) {
        XSComplexTypeDefinition complexTypeDefinition = (XSComplexTypeDefinition) currentElementDeclaration.getTypeDefinition();
        XSObjectList listOfAttributes = complexTypeDefinition.getAttributeUses();
        if (listOfAttributes.getLength() != 0) {
            TypeCompositor attList = new TypeCompositor(TypeCompositor.ATTLIST);
            for (int i = 0; i < listOfAttributes.getLength(); i++) {
                XSAttributeUseImpl xsdAttribute = (XSAttributeUseImpl) listOfAttributes.item(i);
                AttributeDeclaration attribute = new AttributeDeclaration(xsdAttribute.getAttrDeclaration().getName());
                if (logger.isDebugEnabled()) logger.debug(" --- AttributeName: " + xsdAttribute.getAttrDeclaration().getName() + " type: " + xsdAttribute.getType());
                if (xsdAttribute.getRequired()) {
                    attribute.setMinCardinality(1);
                }
                int leafType = xsdAttribute.getAttrDeclaration().getTypeDefinition().getBuiltInKind();
                attribute.addChild(new SimpleType(getLeafType(leafType)));
                attList.addChild(attribute);
            }
            currentNode.addChild(attList);
        }
    }
    
    private void analyzeModelGroup(IXSDNode currentNode, XSParticleDecl fatherParticle, XSModelGroupImpl currentTerm) {
        IXSDNode typeCompositor = createTypeCompositor(currentTerm);
        setCardinality(typeCompositor, fatherParticle);
        currentNode.addChild(typeCompositor);
        XSObjectList  childrenParticles = currentTerm.getParticles();
        if (logger.isDebugEnabled()) logger.debug("Particles size = " +  childrenParticles.getLength());
        for (int i = 0; i < childrenParticles.getLength(); i++) {
            XSObject xsObject = childrenParticles.item(i);
            if (logger.isDebugEnabled()) logger.debug("Particle object: " + xsObject);
            XSParticleDecl particle = (XSParticleDecl)xsObject;
            analyzeParticle(typeCompositor, fatherParticle, particle);
        }
        checkMixedContent(typeCompositor);
    }
    
    private IXSDNode createTypeCompositor(XSModelGroupImpl currentTerm) {
        IXSDNode typeCompositor = null;
        switch (currentTerm.getCompositor()) {
            case  XSModelGroupImpl.COMPOSITOR_ALL : typeCompositor = new TypeCompositor(TypeCompositor.ALL); break;
            case  XSModelGroupImpl.COMPOSITOR_CHOICE : typeCompositor = new TypeCompositor(TypeCompositor.CHOICE); break;
            case  XSModelGroupImpl.COMPOSITOR_SEQUENCE : typeCompositor = new TypeCompositor(TypeCompositor.SEQUENCE); break;
        }
        return typeCompositor;
    }
    
    private void checkMixedContent(IXSDNode currentNode) {
        if (currentNode.getFather().isMixedContent()) {
            PCDATA pcdata = new PCDATA();
            currentNode.addChild(pcdata);
        }
    }
    
    private void setCardinality(IXSDNode currentNode, XSParticleDecl fatherParticle) {
        if (fatherParticle == null) {
            currentNode.setMinCardinality(1);
            currentNode.setMaxCardinality(1);
        } else {
            currentNode.setMinCardinality(fatherParticle.getMinOccurs());
            if (fatherParticle.getMaxOccursUnbounded()) {
                currentNode.setMaxCardinality(Particle.UNBOUNDED);
            } else {
                assert(fatherParticle.getMaxOccurs() > 0) : "Negative cardinalities are not allowed";
                currentNode.setMaxCardinality(fatherParticle.getMaxOccurs());
            }
            if (logger.isDebugEnabled()) logger.debug("Analyzing cardinality of: " + currentNode.getDescription() + " with particle " + fatherParticle);
        }
    }
    
    private IXSDNode findRoot() {
        int counter = 0;
        IXSDNode rootNode = null;
        for (IXSDNode node : globalElements.values()) {
            if (!node.isNested()) {
                counter++;
                rootNode = node;
            }
        }
        if (counter != 1) {
            logger.error("Schema is not a tree:");
            for(IXSDNode node : globalElements.values()) {
                if (!node.isNested()) logger.error(node.getLabel());
            }
            throw new IllegalSchemaException("Schema is not a tree... counter of NOT nested = " + counter);
        }
        return rootNode;
    }
    
}