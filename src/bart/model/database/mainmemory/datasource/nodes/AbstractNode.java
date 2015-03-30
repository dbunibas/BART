package bart.model.database.mainmemory.datasource.nodes;

import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.operators.NodeToCompactString;
import bart.model.database.mainmemory.datasource.operators.NodeToSaveString;
import bart.model.database.mainmemory.datasource.operators.NodeToString;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractNode implements INode {

    private static Logger logger = LoggerFactory.getLogger(AbstractNode.class);

    private String label;
    private Object value;
    private boolean root;
    private boolean virtual;
    private boolean required;
    private boolean notNull;
    private boolean excluded;
    private INode father;
    private NodeToString nodeToString = new NodeToString();
    private NodeToCompactString nodeToCompactString = new NodeToCompactString();
    private NodeToSaveString nodeToSaveString = new NodeToSaveString();
    private Map<String, Object> annotations;

    public AbstractNode(String label) {
        this.label = label;
    }

    public AbstractNode(String label, Object value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public boolean isSchemaNode() {
        return this.value == null;
    }

    public INode getFather() {
        return this.father;
    }

    public void setFather(INode father) {
        this.father = father;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public void setExcluded(boolean excluded) {
        this.excluded = excluded;
    }

    public void addAnnotation(String name, Object value) {
        if (this.annotations == null) {
            this.annotations =  new HashMap<String, Object>();
        }
        this.annotations.put(name, value);
    }

    public Object getAnnotation(String name) {
        if (this.annotations == null) {
            return null;
        }
        return this.annotations.get(name);
    }

    public Object removeAnnotation(String name) {
        if (this.annotations == null) {
            return null;
        }
        return this.annotations.remove(name);
    }

    public Map<String, Object> getAnnotations() {
        return this.annotations;
    }

//    public String toString() {
//        return nodeToString.toString(this, false, false);
//    }

    public String toString() {
        return nodeToCompactString.toString(this, true);
    }

    public String toStringNoOID() {
        return nodeToCompactString.toString(this, false);
    }

    public String toSaveString() {
        return nodeToSaveString.toString(this);
    }

    public String toStringWithOids() {
        return nodeToString.toString(this, true, false);
    }

    public String toStringWithAnnotations() {
        return nodeToString.toString(this, false, true);
    }

    @SuppressWarnings("unchecked")
    public INode clone() {
        AbstractNode clone = null;
        try {
            clone = (AbstractNode) super.clone();
            clone.father = null;
            if (this.annotations != null) {
                clone.annotations = (Map<String, Object>) ((HashMap<String, Object>)this.annotations).clone();
            }
        } catch (CloneNotSupportedException ex) {
            logger.error(ex.getLocalizedMessage());
        }
        return clone;
    }
}
