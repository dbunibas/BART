package bart.model.database.mainmemory.datasource.operators;

import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.nodes.*;
import java.util.List;

public class NodeToSaveString {

    public String toString(INode node) {
        NodeToSaveStringVisitor printVisitor = new NodeToSaveStringVisitor();
        node.accept(printVisitor);
        return (String) printVisitor.getResult();
    }
}

class NodeToSaveStringVisitor implements INodeVisitor {

    private StringBuilder treeDescription = new StringBuilder();

    public String getResult() {
        return treeDescription.toString();
    }

    public void visitSetNode(SetNode node) {
        visitGenericNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        if (hasChildAttributes(node)) {
            String nodeLabel = node.getFather().getLabel();
            if (nodeLabel.matches(".+_\\d+_\\z")) {
                return;
            }
            treeDescription.append(node.getFather().getLabel());
//            treeDescription.append(node.getLabel());
            treeDescription.append("(");
            for (int i = 0; i < node.getChildren().size(); i++) {
                INode child = node.getChildren().get(i);
                if (child instanceof AttributeNode) {
                    treeDescription.append(child.getLabel()).append(": ").append("\"").append(child.getChild(0).getValue()).append("\"");
                }
                if (i != node.getChildren().size() - 1) {
                    treeDescription.append(",   ");
                }
            }
            treeDescription.append(")");
            treeDescription.append("\n");
        }
        for (INode child : node.getChildren()) {
            if (!(child instanceof AttributeNode)) {
                child.accept(this);
            }
        }
    }

    private boolean hasChildAttributes(TupleNode node) {
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
        visitGenericNode(node);
    }

    public void visitMetadataNode(MetadataNode node) {
        visitGenericNode(node);
    }

    public void visitLeafNode(LeafNode node) {
        return;
    }

    private void visitGenericNode(INode node) {
        visitInstanceNode(node);
        List<INode> listOfChildren = node.getChildren();
        if (listOfChildren != null) {
            for (INode child : listOfChildren) {
                child.accept(this);
            }
        }
    }

    private void visitInstanceNode(INode node) {
        return;
    }
}
