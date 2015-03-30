package bart.model.database.mainmemory.datasource.nodes;

import bart.model.database.mainmemory.datasource.operators.INodeVisitor;

public class SetNode extends IntermediateNode {

    boolean cloned = false;

    public SetNode(String label) {
        super(label);
    }
    
    public SetNode(String label, Object value) {
        super(label, value);
    }

    public boolean isCloned() {
        return cloned;
    }

    public void setCloned(boolean cloned) {
        this.cloned = cloned;
    }

    public boolean isRequiredSet() {
        return this.getChild(0).isRequired();
    }
    
    public void accept(INodeVisitor visitor) {
        visitor.visitSetNode(this);
    }
    
}
