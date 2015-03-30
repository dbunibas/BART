package bart.model.database.mainmemory.paths.operators;

import bart.model.database.mainmemory.datasource.DataSource;
import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.nodes.*;
import bart.model.database.mainmemory.datasource.operators.FindNode;
import bart.model.database.mainmemory.datasource.operators.INodeVisitor;
import bart.persistence.PersistenceConstants;
import bart.exceptions.PathSyntaxException;
import bart.model.database.mainmemory.paths.PathExpression;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneratePathExpression {

    private static Logger logger = LoggerFactory.getLogger(GeneratePathExpression.class);

    private static FindNode nodeFinder = new FindNode();
    
    ////////////////   ABSOLUTE PATHS FROM NODE   //////////////////////

    public PathExpression generatePathFromRoot(INode node) {
        GeneratePathExpressionFromNodeVisitor visitor = new GeneratePathExpressionFromNodeVisitor();
        node.accept(visitor);
        return visitor.getResult();
    }

    public PathExpression generatePathFromNode(INode node, INode startingNode) {
        GeneratePathExpressionFromNodeVisitor visitor = new GeneratePathExpressionFromNodeVisitor(startingNode);
        node.accept(visitor);
        return visitor.getResult();
    }

    public PathExpression append(PathExpression prefixPath, PathExpression suffixPath, INode root) {
        PathExpression newPath = new PathExpression(suffixPath);
        if (prefixPath.getLastStep().equals(suffixPath.getFirstStep())) {
            newPath.getPathSteps().remove(0);
        }
        newPath.getPathSteps().addAll(0, prefixPath.getPathSteps());
        return newPath;
    }


    public List<PathExpression> generateFirstLevelChildrenAbsolutePaths(SetNode startingNode) {
        GenerateFirstLevelChildrenAbsolutePathsVisitor visitor = new GenerateFirstLevelChildrenAbsolutePathsVisitor(startingNode);
        startingNode.accept(visitor);
        return visitor.getResult();
    }

    public List<PathExpression> generateFirstLevelAttributeAbsolutePaths(PathExpression setPath, INode root) {
        List<PathExpression> childrenPaths = generateFirstLevelChildrenAbsolutePaths((SetNode)setPath.getLastNode(root));
        for (Iterator<PathExpression> it = childrenPaths.iterator(); it.hasNext(); ) {
            if (! (it.next().getLastNode(root) instanceof AttributeNode)) {
                it.remove();
            }
        }
        return childrenPaths;
    }

    public PathExpression generateSetPathForAttribute(PathExpression attributeAbsolutePath, DataSource dataSource) {
        assert (attributeAbsolutePath.getLastNode(dataSource.getSchema()) instanceof AttributeNode) : "Path must be an attribute path: " + attributeAbsolutePath;
        INode currentNode = attributeAbsolutePath.getLastNode(dataSource.getSchema());
        while (true) {
            if (currentNode instanceof SetNode) {
                return generatePathFromRoot(currentNode);
            }
            if (currentNode.isRoot()) {
                return null;
            }
            currentNode = currentNode.getFather();
        }
    }

    ////////////////   ABSOLUTE PATHS FROM STRING   //////////////////////

    private static final String PATH_SEPARATOR = ".";

    public PathExpression generatePathFromString(String pathDescription) {
        StringTokenizer tokenizer = new StringTokenizer(pathDescription, PATH_SEPARATOR);
        List<String> pathStepStrings = new ArrayList<String>();
        while (tokenizer.hasMoreTokens()) {
            pathStepStrings.add(tokenizer.nextToken());
        }
        if (pathStepStrings.isEmpty()) {
            throw new PathSyntaxException("Path string is empty or syntactically wrong: " + pathDescription);
        }
        return new PathExpression(pathStepStrings);
    }

    public List<INode> generatePathStepNodes(List<String> pathStepStrings, INode root) {
        List<INode> result = new ArrayList<INode>();
        String rootString = pathStepStrings.get(0);
        if (!rootString.equals(root.getLabel())) {
            throw new PathSyntaxException("Node does not exist: " + rootString);
        }
        result.add(root);
        INode currentNode = root;
        for (int i = 1; i < pathStepStrings.size(); i++) {
            String nextStepString = pathStepStrings.get(i);
            INode nextNode = currentNode.getChild(nextStepString);
            if (nextNode == null) {
                throw new PathSyntaxException("Node does not exist: " + nextStepString);
            }
            result.add(nextNode);
            currentNode = nextNode;
        }
        return result;
    }

}
class GeneratePathExpressionFromNodeVisitor implements INodeVisitor {

    private static Logger logger = LoggerFactory.getLogger(GeneratePathExpressionFromNodeVisitor.class);

    // if pathRoot == null path must be rooted
    // else path will be relative to pathRoot
    private INode pathRoot;
    private List<String> nodeList = new ArrayList<String>();

    public GeneratePathExpressionFromNodeVisitor() {}

    public GeneratePathExpressionFromNodeVisitor(INode pathRoot) {
        this.pathRoot = pathRoot;
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

    public void visitAttributeNode(AttributeNode node) {
        visitNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitNode(node);
    }

    public void visitLeafNode(LeafNode node) {
        this.nodeList.add(0, PersistenceConstants.LEAF);
        node.getFather().accept(this);
    }

    private void visitNode(INode node) {
        this.nodeList.add(0, node.getLabel());
        if (isPathRoot(node)) {
            return;
        }
        node.getFather().accept(this);
    }

    private boolean isPathRoot(INode node) {
        return (pathRoot == null && node.isRoot()) || (node.equals(pathRoot));
    }

    public PathExpression getResult() {
        return new PathExpression(nodeList);
    }

}

class GenerateFirstLevelChildrenAbsolutePathsVisitor implements INodeVisitor {

    private static Logger logger = LoggerFactory.getLogger(GenerateFirstLevelChildrenAbsolutePathsVisitor.class);

    private SetNode startingSetNode;
    private List<PathExpression> pathsToChildren = new ArrayList<PathExpression>();
    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    public GenerateFirstLevelChildrenAbsolutePathsVisitor(SetNode startingSetNode) {
        // extracts paths for descendants of a set node that do not belong to a child set
        this.startingSetNode = startingSetNode;
    }

    public void visitSetNode(SetNode node) {
        if (node.equals(startingSetNode)) {
            visitChildren(node);
        } else {
            return;
        }
    }

    public void visitTupleNode(TupleNode node) {
        visitNode(node);
        visitChildren(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitNode(node);
        visitChildren(node);
    }

    private void visitNode(INode node) {
        this.pathsToChildren.add(pathGenerator.generatePathFromRoot(node));
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }

    public void visitAttributeNode(AttributeNode node) {
        visitNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitAttributeNode(node);
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    public List<PathExpression> getResult() {
        return this.pathsToChildren;
    }
}


