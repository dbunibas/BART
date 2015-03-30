package bart.model.database.mainmemory.datasource;

import bart.model.database.mainmemory.datasource.operators.INodeVisitor;
import java.util.List;
import java.util.Map;

public interface INode extends Cloneable {
    
    List<INode> getChildren();
    
    INode getFather();
    
    void setFather(INode father);
    
    void addChild(INode node);
    
    INode getChild(int pos);
    
    INode getChild(String name);

    INode getChildStartingWith(String name);

    void removeChild(String name);
    
    String getLabel();
    
    Object getValue();

    boolean isRoot();

    boolean isVirtual();

    boolean isSchemaNode();
    
    boolean isRequired();
    
    boolean isNotNull();
    
    boolean isExcluded();

    void setRoot(boolean root);

    void setVirtual(boolean virtual);
   
    void setRequired(boolean required);
                    
    void setNotNull(boolean notNull);
    
    void setExcluded(boolean excluded);

    void setLabel(String label);

    void setValue(Object value);

    void addAnnotation(String name, Object value);

    Object getAnnotation(String name);
    
    Object removeAnnotation(String name);

    Map<String, Object> getAnnotations();

    void accept(INodeVisitor visitor);
    
    INode clone();

    @Override
    String toString();

    String toStringNoOID();

    String toSaveString();

    String toStringWithOids();

    String toStringWithAnnotations();
}
