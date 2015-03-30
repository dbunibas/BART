package bart.model.database.mainmemory.datasource.operators;

import bart.model.database.mainmemory.datasource.nodes.*;


public interface INodeVisitor {
        
    void visitSetNode(SetNode node);
    
    void visitTupleNode(TupleNode node);
    
    void visitSequenceNode(SequenceNode node);
    
    void visitAttributeNode(AttributeNode node);
    
    void visitMetadataNode(MetadataNode node);
        
    void visitLeafNode(LeafNode node);

    Object getResult();
    
}
