package bart.model.database.mainmemory.datasource.operators;

import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.nodes.*;
import bart.model.database.mainmemory.datasource.OID;
import java.util.List;

public class NodeToString {

    public String toString(INode node, boolean printOids, boolean printAnnotations) {
        NodeToStringVisitor printVisitor = new NodeToStringVisitor(printOids, printAnnotations);
        node.accept(printVisitor);
        return (String) printVisitor.getResult();
    }
}

class NodeToStringVisitor implements INodeVisitor {

    private static final String HEADER_SCHEMA = "----------- Schema ------------------\n";
    private static final String HEADER_INSTANCE = "----------- Instance ------------------\n";
    private static final String FOOTER = "------------------------------------------\n";
    private int MAXCHARS = 10000000;
    private int indentLevel = 0;
    private StringBuilder treeDescription = new StringBuilder();
    private boolean printOids = false;
    private boolean printAnnotations = false;

    public NodeToStringVisitor(boolean printOids, boolean printAnnotations) {
        this.printOids = printOids;
        this.printAnnotations = printAnnotations;
    }

    public String getResult() {
        if (treeDescription.length() > MAXCHARS) {
            treeDescription.append("....");
        }
        treeDescription.append(FOOTER);
        return treeDescription.toString();
    }

    public void visitSetNode(SetNode node) {
        visitGenericNode(node);
    }

    public void visitTupleNode(TupleNode node) {
        visitGenericNode(node);
    }

    public void visitSequenceNode(SequenceNode node) {
        visitGenericNode(node);
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
        if (treeDescription.length() > MAXCHARS) {
            return;
        }
        if (node.isSchemaNode()) {
            visitSchemaNode(node);
        } else {
            visitInstanceNode(node);
        }
        List<INode> listOfChildren = node.getChildren();
        if (listOfChildren != null) {
            this.indentLevel++;
            for (INode child : listOfChildren) {
                child.accept(this);
            }
            this.indentLevel--;
        }
    }

    private void visitSchemaNode(INode node) {
        if (treeDescription.length() > MAXCHARS) {
            return;
        }
        if (node.isRoot()) {
            treeDescription.append(HEADER_SCHEMA);
        }
        treeDescription.append(this.indentString());
        treeDescription.append(typeNodeDescription(node));
        if (node instanceof AttributeNode) {
            treeDescription.append(" (");
            treeDescription.append(node.getChild(0).getLabel());
            treeDescription.append(")");
        }
        treeDescription.append("\n");
        if (this.printAnnotations) {
            treeDescription.append(annotationsString(node));
        }
    }

    private void visitInstanceNode(INode node) {
        if (treeDescription.length() > MAXCHARS) {
            return;
        }
        if (node.isRoot()) {
            treeDescription.append(HEADER_INSTANCE);
        }
        treeDescription.append(this.indentString());
        treeDescription.append(instanceNodeDescription(node));
        if (this.printOids) {
            treeDescription.append(" - ");
            Object value = node.getValue();
            if (value instanceof OID) {
                OID oid = (OID) value;
                if (oid.getSkolemString() != null) {
                    treeDescription.append(oid.getSkolemString());
                }
            }
        }
        if (node instanceof SetNode) {
            treeDescription.append(" - Tuples: ").append(node.getChildren().size());
        }
        if (node instanceof TupleNode) {
            TupleNode tupleNode = (TupleNode) node;
            if (!tupleNode.getProvenance().isEmpty()) {
                treeDescription.append(" - Provenance: ");
                treeDescription.append(tupleNode.getProvenance());
            }
        }
        if (node instanceof AttributeNode) {
            treeDescription.append(" (");
            treeDescription.append(node.getChild(0).getValue());
            treeDescription.append(")");
        }
        treeDescription.append("\n");
        if (this.printAnnotations) {
            treeDescription.append(annotationsString(node));
        }
    }

    private StringBuilder typeNodeDescription(INode node) {
        StringBuilder result = new StringBuilder();
        result.append(node.getLabel());
        result.append(" : ");
        result.append(node.getClass().getSimpleName());
        if (node.isExcluded()) {
            result.append(" [EXCLUDED]");
        }
        if (node.isVirtual()) {
            result.append(" [virtual]");
        }
        if (node.isRequired()) {
            result.append(" [required]");
        }
        if (node.isNotNull()) {
            result.append(" [not nullable]");
        }
        return result;
    }

    private StringBuilder annotationsString(INode node) {
        StringBuilder result = new StringBuilder();
        if (node.getAnnotations() != null && !node.getAnnotations().isEmpty()) {
            result.append(indentString());
            result.append("--------ANNOTATIONS\n");
            for (String key : node.getAnnotations().keySet()) {
                result.append(indentString());
                result.append("        ");
                result.append(key);
                result.append(": ");
                result.append(node.getAnnotation(key));
                result.append("\n");
            }
        }
        return result;
    }

    private StringBuilder instanceNodeDescription(INode node) {
        StringBuilder result = new StringBuilder();
        result.append(node.getLabel());
        result.append(" (");
        result.append(node.getValue());
        result.append(") : ");
        result.append(node.getClass().getSimpleName());
        return result;
    }

    private StringBuilder indentString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.indentLevel; i++) {
            result.append("    ");
        }
        return result;
    }
}

