package bart.persistence.xml.model;

import bart.model.database.mainmemory.datasource.INode;
import bart.persistence.xml.operators.IXSDNodeVisitor;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleType implements IXSDNode {
    
    private static Logger logger = LoggerFactory.getLogger(SimpleType.class);
    
    private String typeName;
    private IXSDNode father;
    private INode correspondingSchemaNode;
    private boolean visited;
    
    public SimpleType(String typeName) {
        this.typeName = typeName;
    }

    public void accept(IXSDNodeVisitor visitor) {
        visitor.visitSimpleType(this);
    }

    @SuppressWarnings("unchecked")
    public List<IXSDNode> getChildren() {
        return Collections.EMPTY_LIST;
    }

    public void addChild(IXSDNode child) {
        throw new UnsupportedOperationException("Simple types cannot have children");
    }

    public String getLabel() {
        return typeName;
    }

    public String getDescription() {
        return typeName;
    }

    public boolean isNested() {
        return false;
    }

    public void setNested(boolean nested) {
        throw new UnsupportedOperationException("Simple types cannot be nested");
    }

    public IXSDNode getFather() {
        return father;
    }

    public void setFather(IXSDNode father) {
        this.father = father;
    }

    public int getMinCardinality() {
        return 0;
    }

    public void setMinCardinality(int minCardinality) {
        throw new UnsupportedOperationException("Simple types do not have cardinality");
    }

    public int getMaxCardinality() {
        return 0;
    }

    public void setMaxCardinality(int maxCardinality) {
        throw new UnsupportedOperationException("Simple types do not have cardinality");
    }

    public boolean isNullable() {
        return false;
    }

    public void setNullable(boolean nullable) {
        throw new UnsupportedOperationException("PCDATA cannot be nullable");
    }

    public boolean isMixedContent() {
        return false;
    }

    public void setMixedContent(boolean mixedContent) {
        throw new UnsupportedOperationException("Simple types cannot have mixed content");
    }
 
    public INode getCorrespondingSchemaNode() {
        return correspondingSchemaNode;
    }

    public void setCorrespondingSchemaNode(INode correspondingSchemaNode) {
        this.correspondingSchemaNode = correspondingSchemaNode;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public SimpleType clone() {
        try {
            return (SimpleType)super.clone();
        } catch (CloneNotSupportedException ex) {
            logger.error(ex.getLocalizedMessage());
            return null;
        }
    }

}
