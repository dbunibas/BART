package bart.model.database.mainmemory.datasource.operators;

import bart.model.database.mainmemory.datasource.DataSource;
import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.nodes.*;
import bart.exceptions.NodeNotFoundException;
import bart.model.database.mainmemory.paths.PathExpression;
import bart.model.database.mainmemory.paths.operators.GeneratePathExpression;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindNode {

    private static GeneratePathExpression pathGenerator = new GeneratePathExpression();

    ////   FROM PATH
    public INode findNodeInSchema(PathExpression pathExpression, DataSource dataSource) {
        return findNodeInSchema(pathExpression, dataSource.getSchema());
    }

    public INode findNodeInSchema(PathExpression pathExpression, INode schema) {
        FindNodeFromAbsolutePathVisitor visitor = new FindNodeFromAbsolutePathVisitor(pathExpression);
        schema.accept(visitor);
        return visitor.getResult();
    }

    public INode findFirstNodeInInstance(PathExpression pathExpression, INode instance) {
        FindNodeFromAbsolutePathVisitor visitor = new FindNodeFromAbsolutePathVisitor(pathExpression, false);
        instance.accept(visitor);
        return visitor.getResult();
    }

    public List<INode> findNodesInInstance(PathExpression pathExpression, INode instance) {
        FindNodesFromAbsolutePathVisitor visitor = new FindNodesFromAbsolutePathVisitor(pathExpression);
        instance.accept(visitor);
        return visitor.getResult();
    }

    public List<INode> findNodesInInstance(PathExpression pathExpression, INode instance, int limit) {
        FindNodesFromAbsolutePathVisitor visitor = new FindNodesFromAbsolutePathVisitor(pathExpression, limit);
        instance.accept(visitor);
        return visitor.getResult();
    }

    ////   FROM STRING
    public INode findNodeInSchema(String pathString, DataSource dataSource) {
        PathExpression absolutePath = pathGenerator.generatePathFromString(pathString);
        return findNodeInSchema(absolutePath, dataSource);
    }

    public INode findFirstNodeInInstance(String pathString, INode instance) {
        PathExpression absolutePath = pathGenerator.generatePathFromString(pathString);
        return findFirstNodeInInstance(absolutePath, instance);
    }

    public List<INode> findNodesInInstance(String pathString, INode instance) {
        PathExpression absolutePath = pathGenerator.generatePathFromString(pathString);
        return findNodesInInstance(absolutePath, instance);
    }

    ////   FROM NODE
    public INode findNodeInSchema(INode instanceNode, DataSource dataSource) {
        assert (!(instanceNode.isSchemaNode())) : "Node must be an instance node: " + instanceNode;
        PathExpression absolutePath = pathGenerator.generatePathFromRoot(instanceNode);
        return findNodeInSchema(absolutePath, dataSource);
    }
}

class FindNodeFromAbsolutePathVisitor implements INodeVisitor {

    private static Logger logger = LoggerFactory.getLogger(FindNodeFromAbsolutePathVisitor.class);

    private PathExpression pathExpression;
    private boolean required = true;
    private List<String> pathSteps;
    private INode result;
    private boolean root = true;

    public FindNodeFromAbsolutePathVisitor(PathExpression pathExpression) {
        this(pathExpression, true);
    }

    @SuppressWarnings("unchecked")
    public FindNodeFromAbsolutePathVisitor(PathExpression pathExpression, boolean required) {
        this.pathExpression = pathExpression;
        this.required = required;
        List<String> pathSteps = pathExpression.getPathSteps();
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
        if (logger.isDebugEnabled()) logger.debug("Visiting node: " + node.getLabel());
        if (pathSteps.isEmpty()) {
            this.result = node;
            return;
        }
        String firstStepLabel = pathSteps.get(0);
        if (this.root) {
            if (node.getLabel().equals(firstStepLabel)) {
                this.root = false;
                pathSteps.remove(0);
                if (pathSteps.isEmpty()) {
                    this.result = node;
                    return;
                }
                firstStepLabel = pathSteps.get(0);
            }
        }
        for (INode child : node.getChildren()) {
            if (child.getLabel().equals(firstStepLabel)) {
                pathSteps.remove(0);
                child.accept(this);
            }
        }
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public INode getResult() {
        if (this.result == null && this.required) {
            throw new NodeNotFoundException("Node not found: " + pathExpression);
        }
        return this.result;
    }
}

class FindNodesFromAbsolutePathVisitor implements INodeVisitor {

    private static Logger logger = LoggerFactory.getLogger(FindNodesFromAbsolutePathVisitor.class);

    private PathExpression pathExpression;
    private Integer limit;
    private List<String> pathSteps;
    private List<INode> result = new ArrayList<INode>();
    private boolean root = true;

    @SuppressWarnings("unchecked")
    public FindNodesFromAbsolutePathVisitor(PathExpression pathExpression) {
        this.pathExpression = pathExpression;
        this.pathSteps = (List<String>) ((ArrayList<String>) pathExpression.getPathSteps()).clone();
    }

    @SuppressWarnings("unchecked")
    public FindNodesFromAbsolutePathVisitor(PathExpression pathExpression, Integer limit) {
        this.pathExpression = pathExpression;
        this.limit = limit;
        this.pathSteps = (List<String>) ((ArrayList<String>) pathExpression.getPathSteps()).clone();
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
        if (this.limit != null && this.result.size() >= limit) {
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Visiting node: " + node.getLabel());
        if (pathSteps.isEmpty()) {
            this.result.add(node);
            return;
        }
        String firstStepLabel = pathSteps.get(0);
        if (this.root) {
            if (node.getLabel().equals(firstStepLabel)) {
                this.root = false;
                pathSteps.remove(0);
                if (pathSteps.isEmpty()) {
                    this.result.add(node);
                    return;
                }
                firstStepLabel = pathSteps.get(0);
            }
        }
        for (INode child : node.getChildren()) {
            if (child.getLabel().equals(firstStepLabel)) {
                String firstNode = pathSteps.get(0);
                pathSteps.remove(0);
                child.accept(this);
                pathSteps.add(0, firstNode);
            }
        }
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public List<INode> getResult() {
        return this.result;
    }
}


