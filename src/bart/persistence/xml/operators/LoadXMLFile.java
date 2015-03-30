package bart.persistence.xml.operators;

import bart.exceptions.DAOException;
import bart.model.database.mainmemory.datasource.*;
import bart.model.database.mainmemory.datasource.nodes.AttributeNode;
import bart.model.database.mainmemory.datasource.nodes.LeafNode;
import bart.model.database.mainmemory.datasource.nodes.SequenceNode;
import bart.model.database.mainmemory.datasource.nodes.SetNode;
import bart.model.database.mainmemory.datasource.nodes.TupleNode;
import bart.persistence.PersistenceUtility;
import bart.persistence.Types;
import bart.persistence.xml.DAOXmlUtility;
import bart.persistence.xml.IllegalSchemaException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadXMLFile {
    
    private static Logger logger = LoggerFactory.getLogger(LoadXMLFile.class);
    
    private static IntegerOIDGenerator oidGenerator = new IntegerOIDGenerator();
    
    private DataSource dataSource;
    private List<String> currentPathInDOM = new ArrayList<String>();
    private Map<String, INode> nodeMap = new HashMap<String, INode>();
        
    private INode instanceRoot;
    
    public INode loadInstance(DataSource dataSource, String fileName) throws IllegalSchemaException, DAOException {
        this.dataSource = dataSource;
        if (logger.isDebugEnabled()) logger.debug("Data source schema: " + dataSource.getSchema());
        DAOXmlUtility daoUtility = new DAOXmlUtility();
        Document document = daoUtility.buildDOM(fileName);
        Element domRoot = document.getRootElement();
        analyzeElement(domRoot, null);
        return instanceRoot;
    }
    
    
    private void analyzeElement(Element element, INode fatherInInstance) throws DAOException {
        String elementLabel = element.getName();
        if (logger.isDebugEnabled()) logger.debug("Visiting element: " + elementLabel + " - Current path in DOM: " + currentPathInDOM + " - Father in instance: " + fatherInInstance);
        currentPathInDOM.add(elementLabel);
        INode nodeInSchema = findNodeInSchema(currentPathInDOM);
        assert(nodeInSchema != null) : "Element must be present in schema: " + elementLabel;
        INode instanceNode = generateInstanceNodes(nodeInSchema, fatherInInstance);
        if (nodeInSchema instanceof AttributeNode) {
            assignValue(nodeInSchema, instanceNode, element);
        }
        if (logger.isDebugEnabled()) logger.debug("Instance node: " + instanceNode.getLabel());
        if (logger.isTraceEnabled()) logger.trace("Current instance: " + instanceRoot);
        List listOfChildren = element.getChildren();
        List listOfAttributes = element.getAttributes();
        for (Iterator it = listOfAttributes.iterator(); it.hasNext(); ) {
            Attribute attribute = (Attribute) it.next();
            analyzeAttribute(attribute, instanceNode);
        }
        for (Iterator it = listOfChildren.iterator(); it.hasNext(); ) {
            Element child = (Element) it.next();
            analyzeElement(child, instanceNode);
        }
        checkPCDATA(element, nodeInSchema, instanceNode);
        currentPathInDOM.remove(currentPathInDOM.size() - 1);
    }
    
    private void analyzeAttribute(Attribute attribute, INode fatherInInstance) throws DAOException {
        String attributeLabel = attribute.getName();
        if (logger.isDebugEnabled()) logger.debug("Visiting attribute: " + attributeLabel + " - Current path in DOM: " + currentPathInDOM + " - Father in instance: " + fatherInInstance);
        currentPathInDOM.add(attributeLabel);
        INode nodeInSchema = findNodeInSchema(currentPathInDOM);
        if (nodeInSchema != null) {
            INode metadataNode = PersistenceUtility.generateInstanceNode(nodeInSchema);
            assignMetadataValue(attribute, nodeInSchema, metadataNode);
            fatherInInstance.addChild(metadataNode);
        }
        currentPathInDOM.remove(currentPathInDOM.size() - 1);
    }
    
    private INode findNodeInSchema(List<String> pathSteps) {
        if (logger.isTraceEnabled()) logger.debug("Searching schema node: " + pathSteps);
        //TODO: check efficiency of caching
        INode node = nodeMap.get(pathSteps.toString());
        if (node == null) {
            if (logger.isTraceEnabled()) logger.debug("Node not found in cache. Searching...");
            FindNodeFromPathWithVirtualNodes nodeFinder = new FindNodeFromPathWithVirtualNodes();
            node = nodeFinder.findNodeInSchema(dataSource.getSchema(), pathSteps);
        }
        if (node != null) {
            if (logger.isTraceEnabled()) logger.debug("Result: " + node.getLabel());
            nodeMap.put(pathSteps.toString(), node);
        }
        return node;
    }
    
    private INode generateInstanceNodes(INode nodeInSchema, INode fatherInInstance) {
        if (logger.isDebugEnabled()) logger.debug("Generating new nodes for node: " + nodeInSchema.getLabel());
        List<String> pathSteps = generatePathStepsInSchema(nodeInSchema, fatherInInstance);
        if (logger.isDebugEnabled()) logger.debug("Path steps: " + pathSteps);
        if (fatherInInstance != null) {
            fatherInInstance = removeStepsForExistingNodes(nodeInSchema, fatherInInstance, pathSteps);
            if (logger.isDebugEnabled()) logger.debug("Path steps after removal: " + pathSteps);
        }
        List<INode> ancestorsInSchema = generateAncestors(nodeInSchema, pathSteps);
        INode currentFather = fatherInInstance;
        INode instanceNode = null;
        for (INode ancestorInSchema : ancestorsInSchema) {
            instanceNode = PersistenceUtility.generateInstanceNode(ancestorInSchema);
            if (fatherInInstance == null) {
                this.instanceRoot = instanceNode;
            } else {
                currentFather.addChild(instanceNode);
            }
            currentFather = instanceNode;
        }
        return instanceNode;
    }
    
    private List<String> generatePathStepsInSchema(INode nodeInSchema, INode fatherInInstance) {
        List<String> pathSteps = new ArrayList<String>();
        INode node = nodeInSchema;
        while(node != null && (fatherInInstance == null || !node.getLabel().equals(fatherInInstance.getLabel()))) {
            pathSteps.add(0, node.getLabel());
            node = node.getFather();
        }
        return pathSteps;
    }
    
    private INode removeStepsForExistingNodes(INode nodeInSchema, INode fatherInInstance, List<String> pathSteps) {
        INode father = fatherInInstance;
        for (Iterator<String> it = pathSteps.iterator(); it.hasNext(); ) {
            String step = it.next();
            if (step.equals(nodeInSchema.getLabel())) {
                break;
            }
            INode existingNode = findChild(father, step);
            if (existingNode == null) {
                break;
            }
            if (isVirtualTupleForSingleAttribute(existingNode)) {
                break;
            }
            father = existingNode;
            it.remove();
        }
        return father;
    }
    
    private INode findChild(INode father, String label) {
        for (INode child : father.getChildren()) {
            if (child.getLabel().equals(label)) {
                return child;
            }
        }
        return null;
    }
    
    private boolean isVirtualTupleForSingleAttribute(INode existingNode) {
        if ((existingNode instanceof TupleNode) && existingNode.isVirtual() &&
              (existingNode.getFather() instanceof SetNode) && existingNode.getChildren().size() == 1){
            return true;
        }
        return false;
    }

    private List<INode> generateAncestors(INode nodeInSchema, List<String> pathSteps) {
        List<INode> result = new ArrayList<INode>();
        INode node = nodeInSchema;
        while (node != null && pathSteps.contains(node.getLabel())) {
            result.add(0, node);
            node = node.getFather();
        }
        return result;
    }
    
    private void assignValue(INode schemaNode, INode instanceNode, Element element) throws DAOException {
        String type = schemaNode.getChild(0).getLabel();
        String value = element.getValue();
        if (logger.isDebugEnabled()) logger.debug("Found an attribute node: " + instanceNode.getLabel() + " - Value: " + value);
        if (!value.trim().isEmpty()) {
            instanceNode.addChild(new LeafNode(type, Types.getTypedValue(type, value.trim())));
        } else {
            instanceNode.addChild(new LeafNode(schemaNode.getChild(0).getLabel(), NullValueFactory.getNullValue()));
        }
    }
    
    private void assignMetadataValue(final Attribute attribute, final INode nodeInSchema, final INode metadataNode) throws DAOException {
        String value = attribute.getValue();
        if (logger.isDebugEnabled()) logger.debug("Found a metadata node: " + metadataNode.getLabel() + " - Value: " + value);
        INode leafInSchema = nodeInSchema.getChild(0);
        String type = leafInSchema.getLabel();
        if (!value.trim().isEmpty()) {
            metadataNode.addChild(new LeafNode(type, Types.getTypedValue(type, value.trim())));
        } else {
            metadataNode.addChild(new LeafNode(type, NullValueFactory.getNullValue()));
        }
    }
    
    private void checkPCDATA(Element element, INode nodeInSchema, INode fatherInInstance) throws DAOException {
        if (nodeInSchema instanceof TupleNode && !(nodeInSchema instanceof SequenceNode)) {
            for (INode child : nodeInSchema.getChildren()) {
                if (child instanceof SequenceNode && child.getChildren().size() == 1) {
                    INode descendant = child.getChild(0);
                    if (descendant.isVirtual() && descendant.getLabel().contains(GenerateSchemaFromXSDTree.PCDATA_SUFFIX)) {
                        INode sequence = child;
                        INode PCDATA = descendant;
                        INode PCDATAInstance = PersistenceUtility.generateInstanceNode(PCDATA);
                        assignValue(PCDATA, PCDATAInstance, element);
                        INode sequenceInInstance = PersistenceUtility.generateInstanceNode(sequence);
                        sequenceInInstance.addChild(PCDATAInstance);
                        fatherInInstance.addChild(sequenceInInstance);
                    }
                }
            }
        } else if (nodeInSchema instanceof SequenceNode) {
            for (INode child : nodeInSchema.getChildren()) {
                if (child.isVirtual() && child.getLabel().contains(GenerateSchemaFromXSDTree.PCDATA_SUFFIX)) {
                    INode sequence = nodeInSchema;
                    INode PCDATA = child;
                    INode PCDATAInstance = PersistenceUtility.generateInstanceNode(PCDATA);
                    assignValue(PCDATA, PCDATAInstance, element);
                    fatherInInstance.addChild(PCDATAInstance);
                }
            }
        }
    }
        
    
}

