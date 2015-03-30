package bart.model.database.mainmemory.datasource.operators;

import bart.model.database.mainmemory.datasource.DataSource;
import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.nodes.*;
import bart.exceptions.IllegalInstanceException;
import bart.exceptions.NodeNotFoundException;
import bart.model.database.mainmemory.paths.PathExpression;
import bart.model.database.mainmemory.paths.operators.GeneratePathExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckInstance {

    public void checkInstance(DataSource dataSource, INode instanceRoot) {
        CheckInstanceVisitor visitor = new CheckInstanceVisitor(dataSource);
        instanceRoot.accept(visitor);
    }

}

class CheckInstanceVisitor implements INodeVisitor {

    private static Logger logger = LoggerFactory.getLogger(CheckInstanceVisitor.class);

    private DataSource dataSource;

    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private FindNode nodeFinder = new FindNode();

    CheckInstanceVisitor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void visitSetNode(SetNode node) {
        visitNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        visitRecordNode(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitRecordNode(node);
    }

    public void visitAttributeNode(AttributeNode node) {
        visitNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitNode(node);
    }

    public void visitLeafNode(LeafNode node) {
    }

    private void visitNode(INode node) {
        PathExpression nodePathExpression = pathGenerator.generatePathFromRoot(node);
        try {
            nodeFinder.findNodeInSchema(nodePathExpression, this.dataSource.getSchema());
        } catch (NodeNotFoundException nfe) {
            logger.error(nfe.getLocalizedMessage());
            throw new IllegalInstanceException("Node does not have a matching schema node: " + node.getLabel());
        }
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    private void visitRecordNode(INode node) {
        PathExpression nodePathExpression = pathGenerator.generatePathFromRoot(node);
        try {
            INode schemaNode = nodeFinder.findNodeInSchema(nodePathExpression, this.dataSource.getSchema());
            if (!checkRecordChildren(schemaNode, node)) {
                throw new IllegalInstanceException("Node children do not match schema: \n" + node.toStringWithOids() + " Current schema node: " + schemaNode.getLabel() + " in\n" + dataSource.getSchema());
            }
        } catch (NodeNotFoundException nfe) {
            logger.error(nfe.getLocalizedMessage());
            throw new IllegalInstanceException("Node does not have a matching schema node: " + node.getLabel() + " in " + dataSource.getSchema());
        }
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    private boolean checkRecordChildren(INode schemaNode, INode node) {
        int requiredChilds = countRequiredChilds(schemaNode);
        if (requiredChilds > node.getChildren().size()) {
            return false;
        }
        return true;
    }

    private int countRequiredChilds(INode schemaNode) {
        int counter = 0;
        for (INode child : schemaNode.getChildren()) {
            if (child.isRequired() && !child.isVirtual()) {
                counter++;
            }
        }
        return counter;
    }

    public Object getResult() {
        return null;
    }

}
