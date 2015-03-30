package bart.model.database.mainmemory.datasource;

import bart.model.database.mainmemory.paths.PathExpression;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyConstraint implements Cloneable {

    private static Logger logger = LoggerFactory.getLogger(KeyConstraint.class);

    private List<PathExpression> keyPaths;
    private boolean primaryKey;
    
    public KeyConstraint(List<PathExpression> keyPaths) {
        this(keyPaths, false);
    }
    
    public KeyConstraint(PathExpression keyPath) {
        this(keyPath, false);
    }

    public KeyConstraint(List<PathExpression> keyPaths, boolean primaryKey) {
        if (keyPaths.size() < 1) {
            throw new IllegalArgumentException("Constraints cannot be empty: " + keyPaths);
        }
        this.keyPaths = keyPaths;
        this.primaryKey = primaryKey;
    }
    
    public KeyConstraint(PathExpression keyPath, boolean primaryKey) {
        this.keyPaths = new ArrayList<PathExpression>();
        this.keyPaths.add(keyPath);
        this.primaryKey = primaryKey;
    }

    public List<PathExpression> getKeyPaths() {
        return keyPaths;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    @Override
    public KeyConstraint clone() {
        KeyConstraint clone = null;
        try {
            clone = (KeyConstraint) super.clone();
            clone.keyPaths = new ArrayList<PathExpression>();
            for (PathExpression keyPath : keyPaths) {
                clone.keyPaths.add(keyPath.clone());
            }
        } catch (CloneNotSupportedException ex) {
            logger.error(ex.getLocalizedMessage());
        }
        return clone;
    }

    public String toString() {
        String result = "";
        if (this.primaryKey) {
            result += "Primary key: ";
        } else {
            result += "Key: ";
        }
        result += this.keyPaths;        
        return result;
    }
}
