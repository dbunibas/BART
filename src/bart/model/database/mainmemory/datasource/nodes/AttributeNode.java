package bart.model.database.mainmemory.datasource.nodes;

import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.operators.INodeVisitor;

public class AttributeNode extends IntermediateNode {

    public AttributeNode(String label) {
        super(label);
    }
        
    public AttributeNode(String label, Object value) {
        super(label, value);
    }
    
    public void accept(INodeVisitor visitor) {
        visitor.visitAttributeNode(this);
    }

    public void addChild(INode node) {
        assert(this.getChildren().isEmpty()) : "Attribute nodes may have a single child";
        assert(node instanceof LeafNode) : "Attribute nodes may only have leaves: " + node;
        super.addChild(node);
    }

}
