package bart.model.database.mainmemory.datasource;

import bart.model.database.mainmemory.paths.PathExpression;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionalDependency implements Cloneable {

    private static Logger logger = LoggerFactory.getLogger(FunctionalDependency.class);

    private List<PathExpression> leftPaths = new ArrayList<PathExpression>();
    private List<PathExpression> rightPaths = new ArrayList<PathExpression>();

    public FunctionalDependency(List<PathExpression> leftPaths, List<PathExpression> rightPaths) {
        this.leftPaths = leftPaths;
        this.rightPaths = rightPaths;
    }

    public List<PathExpression> getLeftPaths() {
        return leftPaths;
    }

    public List<PathExpression> getRightPaths() {
        return rightPaths;
    }

    public boolean equals(Object object) {
        if (!(object instanceof FunctionalDependency)) {
            return false;
        }
        FunctionalDependency dependency = (FunctionalDependency) object;
        return (this.leftPaths.equals(dependency.leftPaths) && this.rightPaths.equals(dependency.rightPaths));
    }

    public FunctionalDependency clone() {
        FunctionalDependency clone = null;
        try {
            clone = (FunctionalDependency) super.clone();
            clone.leftPaths = new ArrayList<PathExpression>();
            for (PathExpression leftPath : leftPaths) {
                clone.leftPaths.add(leftPath.clone());
            }
            clone.rightPaths = new ArrayList<PathExpression>();
            for (PathExpression rightPath : rightPaths) {
                clone.rightPaths.add(rightPath.clone());
            }
        } catch (CloneNotSupportedException ex) {
            logger.error(ex.getLocalizedMessage());
        }
        return clone;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(leftPaths).append(" --> ").append(rightPaths);
        return result.toString();
    }

}
