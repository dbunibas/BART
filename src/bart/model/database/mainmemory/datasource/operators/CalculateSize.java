package bart.model.database.mainmemory.datasource.operators;

import bart.model.database.mainmemory.datasource.DataSource;
import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.nodes.*;

public class CalculateSize {

    public int getSchemaSize(DataSource dataSource) {
        return getNumberOfNodes(dataSource.getSchema());
    }

    public int getNumberOfNodes(INode node) {
        CalculateSizeVisitor visitor = new CalculateSizeVisitor();
        node.accept(visitor);
        return visitor.getResult();
    }

    public long getNumberOfTuples(INode node) {
        if(node == null){
            return 0;
        }
        CalculateTuplesVisitor visitor = new CalculateTuplesVisitor();
        node.accept(visitor);
        return visitor.getResult();
    }
}

class CalculateSizeVisitor implements INodeVisitor {

    private int counter;

    public Integer getResult() {
        return counter;
    }

    public void visitSetNode(SetNode node) {
        visitGenericNode(node);
        visitChildren(node);
    }

    public void visitTupleNode(TupleNode node) {
        visitGenericNode(node);
        visitChildren(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitGenericNode(node);
        visitChildren(node);
    }

    public void visitAttributeNode(AttributeNode node) {
        visitGenericNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitGenericNode(node);
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    private void visitGenericNode(INode node) {
        if (!node.isExcluded()) {
            counter++;
        }
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }
}

class CalculateTuplesVisitor implements INodeVisitor {

    private long counter;

    public Long getResult() {
        return counter;
    }

    public void visitSetNode(SetNode node) {
        visitChildren(node);
    }

    public void visitTupleNode(TupleNode node) {
        if (!node.isExcluded()) {
            if (hasChildAttribute(node)) {
                counter++;
            }
        }
        visitChildren(node);
    }

    private boolean hasChildAttribute(INode node) {
        for (INode child : node.getChildren()) {
            if (child instanceof AttributeNode) {
                return true;
            }
        }
        return false;
    }

    public void visitSequenceNode(SequenceNode node) {
        visitTupleNode(node);
    }

    public void visitAttributeNode(AttributeNode node) {
        return;
    }

    public void visitMetadataNode(MetadataNode node) {
        return;
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    private void visitChildren(INode node) {
        for (INode child : node.getChildren()) {
            child.accept(this);
        }
    }
}
