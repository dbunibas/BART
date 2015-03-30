package bart.persistence.xml.model;

import bart.model.database.mainmemory.datasource.INode;
import bart.persistence.xml.operators.XSDNodeToString;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Particle implements IXSDNode {
    
    public static final int UNBOUNDED = Integer.MAX_VALUE;
    
    private static Logger logger = LoggerFactory.getLogger(Particle.class);
    
    private String label;
    private int minCardinality;
    private int maxCardinality;
    private boolean nullable;
    private boolean nested;
    private boolean mixedContent;
    
    private List<IXSDNode> children = new ArrayList<IXSDNode>();
    private IXSDNode father;
    private INode correspondingSchemaNode;
    private boolean visited;
    
    public Particle(String label) {
        this.label = label;
    }
    
    public void addChild(IXSDNode child) {
        this.children.add(child);
        child.setFather(this);
    }
    
    public List<IXSDNode> getChildren() {
        return children;
    }
    
    public String getLabel() {
        return this.label;
    }

    public String getDescription() {
        String result = "";
        result += this.label + " : " + this.getClass().getSimpleName();
        result += "(minC=" + this.minCardinality + ", maxC=" + this.maxCardinality + ", nullable=" + nullable + ", mixed=" + this.mixedContent + 
                ", nested=" + this.nested + ") - iNode: " + correspondingSchemaNode + " - visited: " + visited;
        return result;
    }
    
    public int getMinCardinality() {
        return minCardinality;
    }
    
    public void setMinCardinality(int minCardinality) {
        this.minCardinality = minCardinality;
    }
    
    public int getMaxCardinality() {
        return maxCardinality;
    }
    
    public void setMaxCardinality(int maxCardinality) {
        this.maxCardinality = maxCardinality;
    }
    
    public boolean isNested() {
        return nested;
    }
    
    public void setNested(boolean nested) {
        this.nested = nested;
    }
    
    public IXSDNode getFather() {
        return father;
    }
    
    public void setFather(IXSDNode father) {
        this.father = father;
    }
    
    public boolean isMixedContent() {
        return mixedContent;
    }
    
    public void setMixedContent(boolean mixedContent) {
        this.mixedContent = mixedContent;
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

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String toString() {
        return new XSDNodeToString().toString(this);
    }
    
    public Particle clone() {
        Particle clone = null;
        try {
            clone = (Particle)super.clone();
            clone.father = null;
            clone.children = new ArrayList<IXSDNode>();
            for (IXSDNode child : children) {
                clone.addChild((IXSDNode) child.clone());
            }
        } catch (CloneNotSupportedException ex) {
            logger.error(ex.getLocalizedMessage());
        }
        return clone;
    }
    
}
