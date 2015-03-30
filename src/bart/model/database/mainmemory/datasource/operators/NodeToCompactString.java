package bart.model.database.mainmemory.datasource.operators;

import bart.BartConstants;
import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.nodes.*;
import java.util.List;

public class NodeToCompactString {

    public String toString(INode node, boolean printOids) {
        NodeToCompactStringVisitor printVisitor = new NodeToCompactStringVisitor(printOids);
        node.accept(printVisitor);
        String result = (String) printVisitor.getResult();
        CalculateSize sizeCalculator = new CalculateSize();
        if (node instanceof SetNode) {
            result += "\n" + "Total number of tuples: " + sizeCalculator.getNumberOfTuples(node);
        }
        return result;
    }
}

class NodeToCompactStringVisitor implements INodeVisitor {

    private int indentLevel = 0;
    private boolean printOids = true;
    private StringBuilder treeDescription = new StringBuilder();

    public NodeToCompactStringVisitor() {
    }

    public NodeToCompactStringVisitor(boolean printOids) {
        this.printOids = printOids;
    }

    public String getResult() {
        return treeDescription.toString();
    }

    public void visitSetNode(SetNode node) {
        visitGenericNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        treeDescription.append(this.indentString());
        treeDescription.append(node.getLabel());
        if (hasChildAttributes(node)) {
            treeDescription.append(" [");
            for (int i = 0; i < node.getChildren().size(); i++) {
                INode child = node.getChildren().get(i);
                if (child instanceof AttributeNode) {
                    if (this.printOids || !child.getLabel().equals(BartConstants.OID)) {
                        treeDescription.append(child.getLabel()).append(": ").append(child.getChild(0).getValue());
                    }
                }
                if (i != node.getChildren().size() - 1) {
                    treeDescription.append(",   ");
                }
            }
            treeDescription.append("]");
        }
        if (!node.getProvenance().isEmpty()) {
            treeDescription.append(" **Prov:");
            treeDescription.append(node.getProvenance());
        }
        treeDescription.append("\n");
        this.indentLevel++;
        for (INode child : node.getChildren()) {
            if (!(child instanceof AttributeNode)) {
                child.accept(this);
            }
        }
        this.indentLevel--;
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
            this.indentLevel++;
            for (INode child : listOfChildren) {
                child.accept(this);
            }
            this.indentLevel--;
        }
    }

    private void visitInstanceNode(INode node) {
        treeDescription.append(this.indentString());
        treeDescription.append(node.getLabel());
        if (node instanceof SetNode) {
            treeDescription.append(" - Tuples: " + node.getChildren().size());
        }
        treeDescription.append("\n");
    }

    private StringBuilder indentString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.indentLevel; i++) {
            result.append("    ");
        }
        return result;
    }
}
