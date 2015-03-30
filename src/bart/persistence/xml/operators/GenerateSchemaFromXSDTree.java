package bart.persistence.xml.operators;

import bart.model.database.mainmemory.datasource.nodes.*;
import bart.model.database.mainmemory.datasource.*;
import bart.persistence.xml.model.*;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateSchemaFromXSDTree {

    private static Logger logger = LoggerFactory.getLogger(GenerateSchemaFromXSDTree.class);

    static final String PCDATA_SUFFIX = "Text";
    static final String SET_SUFFIX = "Set";
    static final String SEQUENCE_SUFFIX = "Sequence";

    public INode generateSchema(XSDSchema xsdSchema) {
        SchemaGeneratorVisitor visitor = new SchemaGeneratorVisitor();
        IXSDNode schemaRoot = xsdSchema.getRoot();
        schemaRoot.accept(visitor);
        return visitor.getResult();
    }
}

class SchemaGeneratorVisitor implements IXSDNodeVisitor {

    private static Logger logger = LoggerFactory.getLogger(SchemaGeneratorVisitor.class);

    private Map<String, Integer> labelCounter = new HashMap<String, Integer>();
    private INode root;

    public void visitElementDeclaration(ElementDeclaration node) {
        if (logger.isDebugEnabled()) logger.debug("Visiting node: " + node);
        INode fatherNode = findFatherNode(node);
        // SIMPLE TYPE
        if (node.getChildren().size() == 1 && node.getChildren().get(0) instanceof SimpleType) {
            if (node.getMaxCardinality() > 1) {
                // set of simple type-elements
                SetNode setNode = new SetNode(buildVirtualNodeLabelForSet(node));
                setNode.setVirtual(true);
                addNode(node, fatherNode, setNode);
                TupleNode tupleNode = new TupleNode(buildVirtualNodeLabelForSequence(node));
                tupleNode.setVirtual(true);
                setNode.addChild(tupleNode);
                fatherNode = tupleNode;
            }
            IXSDNode child = node.getChildren().get(0);
            createAttributeNode(node, fatherNode, child);
            return;
        }
        // VIRTUAL SET NODE
        if (node.getMaxCardinality() > 1) {
            SetNode setNode = new SetNode(buildVirtualNodeLabelForSet(node));
            setNode.setVirtual(true);
            addNode(node, fatherNode, setNode);
            fatherNode = setNode;
        }
        // ATTLIST
        if (node.getChildren().size() > 1 || (node.getChildren().size() == 1 && node.getChildren().get(0).getLabel().equals(TypeCompositor.ATTLIST))) {
            IXSDNode attList = node.getChildren().get(0);
            assert (attList instanceof TypeCompositor && attList.getLabel().equals(TypeCompositor.ATTLIST)) : "Attlist must be the first child of an element declaration: " + node;
            TupleNode tupleNode = new TupleNode(node.getLabel());
            attList.setVisited(true);
            addNode(node, fatherNode, tupleNode);
        } else {
            IXSDNode child = node.getChildren().get(0);
            assert (child instanceof TypeCompositor) : "Element declarations must have a type compositor as a child: " + node;
            if (child.getLabel().equals(TypeCompositor.ALL)) {
                TupleNode tupleNode = new TupleNode(node.getLabel());
                addNode(node, fatherNode, tupleNode);
            }
            if (child.getLabel().equals(TypeCompositor.CHOICE)) {
                throw new IllegalArgumentException("Unable to handle UNION nodes");
            }
            if (child.getLabel().equals(TypeCompositor.SEQUENCE)) {
                if (node.getMaxCardinality() < 2 && child.getMaxCardinality() > 1) {
                    // SEQUENCE unbounded
                    SetNode setNode = new SetNode(node.getLabel());
                    child.setMaxCardinality(1);
                    addNode(node, fatherNode, setNode);
                } else if (node.getMaxCardinality() < 2 && child.getChildren().size() == 1 && child.getChildren().get(0).getMaxCardinality() > 1) {
                    // SEQUENCE with 1 element unbounded
                    SetNode setNode = new SetNode(node.getLabel());
                    IXSDNode descendant = child.getChildren().get(0);
                    descendant.setMaxCardinality(1);
                    addNode(node, fatherNode, setNode);
                } else {
                    SequenceNode sequenceNode = new SequenceNode(node.getLabel());
                    addNode(node, fatherNode, sequenceNode);
                }
            }
            if (child.getMaxCardinality() == 1) {
                child.setVisited(true);
            }
            node.setVisited(true);
        }
        visitChildren(node);
    }

    public void visitTypeCompositor(TypeCompositor node) {
        if (logger.isDebugEnabled()) logger.debug("Visiting node: " + node);
        if (!node.isVisited()) {
            if (node.getLabel().equals(TypeCompositor.CHOICE)) {
                throw new IllegalArgumentException("Unable to handle UNION nodes");
            }
            if (node.getLabel().equals(TypeCompositor.ALL)) {
                TupleNode tupleNode = new TupleNode(buildVirtualNodeLabelForSequence(node.getFather()));
                tupleNode.setVirtual(true);
                addNode(node, findFatherNode(node), tupleNode);
            }
            if (node.getLabel().equals(TypeCompositor.SEQUENCE)) {
                if (node.getMaxCardinality() > 1) {
                    SetNode setNode = new SetNode(buildVirtualNodeLabelForSet(node.getFather()));
                    setNode.setVirtual(true);
                    addNode(node, findFatherNode(node), setNode);
                    int childrenSize = node.getChildren().size();
                    IXSDNode firstChild = node.getChildren().get(0);
                    IXSDNode descendant = null;
                    if (firstChild.getChildren().size() > 0) {
                        descendant = firstChild.getChildren().get(0);
                    }
                    if (childrenSize > 1 || (childrenSize == 1 && descendant != null && descendant instanceof SimpleType)) {
                        SequenceNode sequenceNode = new SequenceNode(buildVirtualNodeLabelForSequence(node.getFather()));
                        sequenceNode.setVirtual(true);
                        addNode(node, setNode, sequenceNode);
                    }
                } else {
                    SequenceNode sequenceNode = new SequenceNode(buildVirtualNodeLabelForSequence(node.getFather()));
                    sequenceNode.setVirtual(true);
                    addNode(node, findFatherNode(node), sequenceNode);
                }
            }
        }
        visitChildren(node);
    }

    public void visitAttributeDeclaration(AttributeDeclaration node) {
        if (logger.isDebugEnabled()) logger.debug("Visiting node: " + node);
        MetadataNode metadataNode = new MetadataNode(node.getLabel());
        IXSDNode simpleType = node.getChildren().get(0);
        LeafNode leafNode = new LeafNode(simpleType.getLabel());
        metadataNode.addChild(leafNode);
        addNode(node, findFatherNode(node), metadataNode);
        node.setVisited(true);
        simpleType.setVisited(true);
        visitChildren(node);
    }

    public void visitPCDATA(PCDATA node) {
        if (logger.isDebugEnabled()) logger.debug("Visiting node: " + node);
        IXSDNode ancestor = node.getFather().getFather();
        AttributeNode attributeNode = new AttributeNode(ancestor.getLabel() + GenerateSchemaFromXSDTree.PCDATA_SUFFIX);
        LeafNode leafNode = new LeafNode("string");
        attributeNode.addChild(leafNode);
        addNode(node, findFatherNode(node), attributeNode);
        attributeNode.setVirtual(true);
        node.setVisited(true);
    }

    public void visitSimpleType(SimpleType node) {
        return;
    }

    private void visitChildren(IXSDNode node) {
        for (IXSDNode child : node.getChildren()) {
            child.accept(this);
        }
    }

    private void createAttributeNode(ElementDeclaration node, INode fatherNode, IXSDNode child) {
        AttributeNode attributeNode = new AttributeNode(node.getLabel());
        LeafNode leafNode = new LeafNode(child.getLabel());
        attributeNode.addChild(leafNode);
        addNode(node, fatherNode, attributeNode);
        node.setVisited(true);
        child.setVisited(true);
        if (logger.isDebugEnabled()) logger.debug("Created node: " + attributeNode);
    }

    private INode findFatherNode(IXSDNode xsdNode) {
        IXSDNode xsdFather = xsdNode.getFather();
        if (xsdFather == null) {
            if (logger.isDebugEnabled()) logger.debug("Father node in schema null");
            return null;
        }
        INode fatherNode = xsdFather.getCorrespondingSchemaNode();
        while (fatherNode == null && xsdFather != null) {
            xsdFather = xsdFather.getFather();
            fatherNode = xsdFather.getCorrespondingSchemaNode();
        }
        if (logger.isDebugEnabled()) logger.debug("Father node in schema: " + fatherNode.getLabel());
        return fatherNode;
    }

    private void addNode(IXSDNode xsdNode, INode fatherNode, INode node) {
        if (xsdNode.getMinCardinality() > 0) {
            node.setRequired(true);
        }
        node.setNotNull(!xsdNode.isNullable());
        if (logger.isDebugEnabled()) logger.debug("Adding node: " + node + " to father " + fatherNode);
        if (fatherNode == null) {
            node.setRoot(true);
            root = node;
        } else {
            fatherNode.addChild(node);
        }
        xsdNode.setCorrespondingSchemaNode(node);
    }

    private String buildVirtualNodeLabelForSequence(IXSDNode node) {
        String label = node.getLabel() + GenerateSchemaFromXSDTree.SEQUENCE_SUFFIX;
        return getStringWithCounter(label);
    }

    private String buildVirtualNodeLabelForSet(IXSDNode node) {
        String label = node.getLabel() + GenerateSchemaFromXSDTree.SET_SUFFIX;
        return getStringWithCounter(label);
    }

    private String getStringWithCounter(String label) {
        Integer occurrences = labelCounter.get(label);
        if (occurrences == null) {
            labelCounter.put(label, 0);
            return label;
        }
        labelCounter.put(label, ++occurrences);
        return label + occurrences;
    }

    public INode getResult() {
        return root;
    }
}
