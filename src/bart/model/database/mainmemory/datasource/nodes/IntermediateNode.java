package bart.model.database.mainmemory.datasource.nodes;

import bart.model.database.mainmemory.datasource.INode;
import java.util.ArrayList;
import java.util.List;

abstract class IntermediateNode extends AbstractNode {

    private List<INode> listOfChildren = new ArrayList<INode>(1);

    public IntermediateNode(String label) {
        super(label);
    }

    public IntermediateNode(String label, Object value) {
        super(label, value);
    }

    public void addChild(INode node) {
        assert (node != null) : "Child cannot be null";
        this.listOfChildren.add(node);
        node.setFather(this);
    }

    public INode getChild(int pos) {
        assert (0 <= pos && pos < listOfChildren.size()) : "Child does not exist: " + pos;
        return this.listOfChildren.get(pos);
    }

    public INode getChild(String name) {
        for (INode child : this.listOfChildren) {
            if (child.getLabel().equals(name)) {
                return child;
            }
        }
        return null;
    }

    public INode getChildStartingWith(String name) {
        for (INode child : this.listOfChildren) {
            if (child.getLabel().startsWith(name)) {
                return child;
            }
        }
        return null;
    }

    public void removeChild(String name) {
        for (INode child : this.listOfChildren) {
            if (child.getLabel().equals(name)) {
                child.setFather(null);
                this.listOfChildren.remove(child);
                return;
            }
        }
    }

    public List<INode> getChildren() {
        return listOfChildren;
    }

    public INode clone() {
        IntermediateNode clone = (IntermediateNode) super.clone();
//        if (this.getValue() != null) {
//            clone.setValue(new IntegerOIDGenerator().getNextOID());
//        }
        clone.listOfChildren = new ArrayList<INode>();
        for (INode child : listOfChildren) {
            clone.addChild((INode) child.clone());
        }
        return clone;
    }
}
