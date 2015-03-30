package bart.persistence.xml.model;

import bart.model.database.mainmemory.datasource.INode;
import bart.persistence.xml.operators.IXSDNodeVisitor;
import java.util.List;

public interface IXSDNode extends Cloneable {
        
    String getLabel();
    
    String getDescription();

    int getMinCardinality();
    
    void setMinCardinality(int minCardinality);
    
    int getMaxCardinality();
    
    void setMaxCardinality(int maxCardinality);
    
    boolean isNullable();
    
    void setNullable(boolean nullable);

    String toString();
    
    IXSDNode getFather();
    
    void setFather(IXSDNode father);
    
    List<IXSDNode> getChildren();
    
    void addChild(IXSDNode child);
    
    boolean isNested();
    
    void setNested(boolean nested);
    
    boolean isMixedContent();
    
    void setMixedContent(boolean mixedContent);

    void accept(IXSDNodeVisitor visitor);
    
    INode getCorrespondingSchemaNode();
    
    void setCorrespondingSchemaNode(INode node);
    
    boolean isVisited();
    
    void setVisited(boolean visited);
    
    IXSDNode clone();
}
