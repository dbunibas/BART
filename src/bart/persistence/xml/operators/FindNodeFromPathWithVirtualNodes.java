package bart.persistence.xml.operators;

import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.nodes.AttributeNode;
import bart.model.database.mainmemory.datasource.nodes.LeafNode;
import bart.model.database.mainmemory.datasource.nodes.MetadataNode;
import bart.model.database.mainmemory.datasource.nodes.SequenceNode;
import bart.model.database.mainmemory.datasource.nodes.SetNode;
import bart.model.database.mainmemory.datasource.nodes.TupleNode;
import bart.model.database.mainmemory.datasource.operators.INodeVisitor;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FindNodeFromPathWithVirtualNodes {

    INode findNodeInSchema(INode startingNode, List<String> pathSteps) {
        FindNodeWithVirtualNodesVisitor visitor = new FindNodeWithVirtualNodesVisitor(pathSteps);
        startingNode.accept(visitor);
        return visitor.getResult();
    }
}

class FindNodeWithVirtualNodesVisitor implements INodeVisitor {

    private static Logger logger = LoggerFactory.getLogger(FindNodeWithVirtualNodesVisitor.class);

    private List<String> pathSteps;
    private INode result;

    @SuppressWarnings("unchecked")
    FindNodeWithVirtualNodesVisitor(List<String> pathSteps) {
        this.pathSteps = (List<String>) ((ArrayList<String>) pathSteps).clone();
    }

    public void visitSetNode(SetNode node) {
        visitNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        visitNode(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitNode(node);
    }

    public void visitAttributeNode(AttributeNode node) {
        visitNode(node);
    }

    private void visitNode(INode node) {
        if (result != null) {
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Visiting node: " + node.getLabel() + " with path steps: " + pathSteps);
        if (node.isVirtual()) {
            if (logger.isDebugEnabled()) logger.debug("Node is virtual. Visiting children");
            visitChildren(node);
            return;
        }
        String firstStepLabel = pathSteps.get(0);
        if (!node.getLabel().equals(firstStepLabel)) {
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Match found, removing step " + pathSteps.get(0));
        pathSteps.remove(0);
        if (pathSteps.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("Steps empty, node found");
            this.result = node;
            return;
        }
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public INode getResult() {
        return this.result;
    }
}
