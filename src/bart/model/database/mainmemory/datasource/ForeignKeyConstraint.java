package bart.model.database.mainmemory.datasource;

import bart.model.database.mainmemory.paths.PathExpression;
import java.util.ArrayList;
import java.util.List;

public class ForeignKeyConstraint {

    private KeyConstraint keyConstraint;
    private List<PathExpression> foreignKeyPaths;
        
    public ForeignKeyConstraint(KeyConstraint keyConstraint, List<PathExpression> foreignKeyPaths) {
        if (keyConstraint.getKeyPaths().size() != foreignKeyPaths.size()) {
            throw new IllegalArgumentException("Foreign key number does not match primary key number: " + keyConstraint + " - " + foreignKeyPaths);
        }
        this.keyConstraint = keyConstraint;
        this.foreignKeyPaths = foreignKeyPaths;
    }
    
    public ForeignKeyConstraint(KeyConstraint keyConstraint, PathExpression foreignKeyPath) {
        if (keyConstraint.getKeyPaths().size() != 1) {
            throw new IllegalArgumentException("Foreign key number does not match primary key number: " + keyConstraint + " - " + foreignKeyPaths);
        }
        this.keyConstraint = keyConstraint;
        this.foreignKeyPaths = new ArrayList<PathExpression>();
        this.foreignKeyPaths.add(foreignKeyPath);
    }

    public List<PathExpression> getForeignKeyPaths() {
        return foreignKeyPaths;
    }
    
    public KeyConstraint getKeyConstraint() {
        return keyConstraint;
    }

    public String toString() {
        return this.foreignKeyPaths + " references " + this.keyConstraint.getKeyPaths();
    }

}
