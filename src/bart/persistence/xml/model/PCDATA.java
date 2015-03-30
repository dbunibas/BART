package bart.persistence.xml.model;

import bart.model.database.mainmemory.datasource.INode;
import bart.persistence.xml.operators.IXSDNodeVisitor;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PCDATA implements IXSDNode {
    
    private static Logger logger = LoggerFactory.getLogger(PCDATA.class);
    
    private IXSDNode father;
    private INode correspondingSchemaNode;
    private boolean visited;
    
    public void accept(IXSDNodeVisitor visitor) {
        visitor.visitPCDATA(this);
    }

    @SuppressWarnings("unchecked")
    public List<IXSDNode> getChildren() {
        return Collections.EMPTY_LIST;
    }

    public void addChild(IXSDNode child) {
        throw new UnsupportedOperationException("PCDATA cannot have children");
    }

    public String getLabel() {
        return "PCDATA";
    }

    public String getDescription() {
        return "PCDATA";
    }

    public boolean isNested() {
        return false;
    }

    public void setNested(boolean nested) {
        throw new UnsupportedOperationException("PCDATA cannot be nested");
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
        throw new UnsupportedOperationException("PCDATA do not have cardinality");
    }

    public int getMaxCardinality() {
        return 0;
    }

    public void setMaxCardinality(int maxCardinality) {
        throw new UnsupportedOperationException("PCDATA do not have cardinality");
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
        throw new UnsupportedOperationException("PCDATA cannot have mixed content");
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

    public PCDATA clone() {
        try {
            return (PCDATA)super.clone();
        } catch (CloneNotSupportedException ex) {
            logger.error(ex.getLocalizedMessage());
            return null;
        }
    }
}

