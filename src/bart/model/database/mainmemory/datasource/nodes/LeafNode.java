package bart.model.database.mainmemory.datasource.nodes;

import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.operators.INodeVisitor;
import java.util.List;

public class LeafNode extends AbstractNode {
    
    public LeafNode(String type) {
        super(type);
    }
    
    public LeafNode(String label, Object value) {
        super(label, value);
    }
    
    public void addChild(INode node) {
        throw new UnsupportedOperationException("It is not possible to add children to leaf nodes");
    }
    
    public INode getChild(int pos) {
        throw new UnsupportedOperationException("Leaf nodes do not have children");
    }

    public INode getChild(String name) {
        throw new UnsupportedOperationException("Leaf nodes do not have children");
    }
    
    public INode searchChild(String name) {
        throw new UnsupportedOperationException("Leaf nodes do not have children");
    }

    public INode getChildStartingWith(String name) {
        throw new UnsupportedOperationException("Leaf nodes do not have children");
    }

    public void removeChild(String name) {
        throw new UnsupportedOperationException("Leaf nodes do not have children");
    }

    public void accept(INodeVisitor visitor) {
        visitor.visitLeafNode(this);
    }

    public List<INode> getChildren() {
        return null;
    }

}
