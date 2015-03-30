package bart.model.database.mainmemory.datasource.nodes;

import bart.model.database.mainmemory.datasource.operators.INodeVisitor;

public class SequenceNode extends TupleNode {

    public SequenceNode(String label) {
        super(label);
    }
    
    public SequenceNode(String label, Object value) {
        super(label, value);
    }
    
    public void accept(INodeVisitor visitor) {
        visitor.visitSequenceNode(this);
    }
    
}
