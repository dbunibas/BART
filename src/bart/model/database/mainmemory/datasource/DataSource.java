package bart.model.database.mainmemory.datasource;

import bart.model.database.mainmemory.datasource.operators.CalculateSize;
import bart.model.database.mainmemory.datasource.operators.CheckInstance;
import bart.persistence.PersistenceConstants;
import bart.model.database.mainmemory.paths.PathExpression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSource implements Cloneable {

    private static Logger logger = LoggerFactory.getLogger(DataSource.class);

    // data source definition
    private String type;
    private INode schema;
    private List<INode> originalInstances = new ArrayList<INode>();
    private List<INode> instances = new ArrayList<INode>();
    private List<KeyConstraint> keyConstraints = new ArrayList<KeyConstraint>();
    private List<ForeignKeyConstraint> foreignKeyConstraints = new ArrayList<ForeignKeyConstraint>();

    protected Map<String, Object> annotations = new HashMap<String, Object>();

    public DataSource(String type, INode schema) {
        this.type = type;
        this.schema = schema;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public INode getSchema() {
        return schema;
    }

    public List<KeyConstraint> getKeyConstraints() {
        return keyConstraints;
    }

    public void addKeyConstraint(KeyConstraint keyConstraint) {
        this.keyConstraints.add(keyConstraint);
    }

    public List<ForeignKeyConstraint> getForeignKeyConstraints() {
        return foreignKeyConstraints;
    }

    public void addForeignKeyConstraint(ForeignKeyConstraint foreignKeyConstraint) {
        this.foreignKeyConstraints.add(foreignKeyConstraint);
    }

    public List<INode> getOriginalInstances() {
        return originalInstances;
    }

    public List<INode> getInstances() {
        return this.instances;
    }

    public void addInstance(INode instance) {
        if (!type.equals(PersistenceConstants.TYPE_ALGEBRA_RESULT)) {
            throw new IllegalArgumentException("Method can be used for algebra data sources only. Use addInstanceWithCheck()");
        }
        this.instances.add(instance);
    }

    public void addInstanceWithCheck(INode instance) {
        CheckInstance checker = new CheckInstance();
        checker.checkInstance(this, instance);
        INode instanceClone = instance.clone();
        this.originalInstances.add(instanceClone);
        //this.instances.add(new DuplicateSet().generateInstanceClone(duplications, instance, intermediateSchema));
        this.instances.add(instanceClone);
    }

    public int getSize() {
        CalculateSize calculator = new CalculateSize();
        return calculator.getSchemaSize(this);
    }
    
    @Override
    public DataSource clone()  {
        DataSource clone = null;
        try {
            clone = (DataSource) super.clone();
            clone.schema = this.schema.clone();
            clone.instances = new ArrayList<INode>();
            for (INode instance : instances) {
                clone.instances.add(instance.clone());
            }
            clone.originalInstances = new ArrayList<INode>();
            for (INode instance : originalInstances) {
                clone.originalInstances.add(instance.clone());
            }
            Map<KeyConstraint, KeyConstraint> keyMap = new HashMap<KeyConstraint, KeyConstraint>();
            clone.keyConstraints = new ArrayList<KeyConstraint>();
            for (KeyConstraint key : keyConstraints) {
                KeyConstraint keyClone = key.clone();
                keyMap.put(key, keyClone);
                clone.keyConstraints.add(keyClone);
            }
            clone.foreignKeyConstraints = new ArrayList<ForeignKeyConstraint>();
            for (ForeignKeyConstraint foreignKey : foreignKeyConstraints) {
                KeyConstraint keyClone = keyMap.get(foreignKey.getKeyConstraint());
                List<PathExpression> fkPathClones = new ArrayList<PathExpression>();
                for (PathExpression fkPath : foreignKey.getForeignKeyPaths()) {
                    fkPathClones.add(fkPath.clone());
                }
                ForeignKeyConstraint foreignKeyClone = new ForeignKeyConstraint(keyClone, fkPathClones);
                clone.foreignKeyConstraints.add(foreignKeyClone);
            }
        } catch (CloneNotSupportedException ex) {
            logger.error(ex.getLocalizedMessage());
        }
        return clone;
    }

    public void addAnnotation(String key, Object value) {
        this.annotations.put(key, value);
    }

    public Object getAnnotation(String key) {
        return this.annotations.get(key);
    }

    public Object removeAnnotation(String key) {
        return this.annotations.remove(key);
    }

    public Map<String, Object> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<String, Object> annotations) {
        this.annotations = annotations;
    }

}
