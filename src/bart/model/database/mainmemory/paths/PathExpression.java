package bart.model.database.mainmemory.paths;

import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.paths.operators.GeneratePathExpression;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathExpression implements Comparable<PathExpression>, Cloneable {

    private static Logger logger = LoggerFactory.getLogger(PathExpression.class);
    
    protected List<String> pathSteps = new ArrayList<String>();

    public PathExpression(List<String> pathSteps) {
        this.pathSteps = pathSteps;
    }

    public PathExpression(PathExpression pathExpression) {
        this.pathSteps.addAll(pathExpression.getPathSteps());
    }

    public List<String> getPathSteps() {
        return this.pathSteps;
    }

    public String getFirstStep() {
        if (pathSteps.isEmpty()) {
            return null;
        }
        return pathSteps.get(0);
    }

    public String getLastStep() {
        if (pathSteps.isEmpty()) {
            return null;
        }
        return pathSteps.get(pathSteps.size() - 1);
    }

    public List<INode> getPathNodes(INode root) {
        return new GeneratePathExpression().generatePathStepNodes(pathSteps, root);
    }

    public List<PathExpression> getAttributePaths(INode root) {
        return new GeneratePathExpression().generateFirstLevelAttributeAbsolutePaths(this, root);
    }

    public int getLevel() {
        return this.pathSteps.size();
    }

    public INode getLastNode(INode root) {
        if (this.pathSteps.isEmpty()) {
            return null;
        }
        List<INode> pathNodes = new GeneratePathExpression().generatePathStepNodes(pathSteps, root);
        return pathNodes.get(pathNodes.size() - 1);
    }

    public int compareTo(PathExpression pathExpression) {
        return this.toString().compareTo(pathExpression.toString());
    }

    public PathExpression clone() {
        PathExpression clone = null;
        try {
            clone = (PathExpression) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
        clone.pathSteps = new ArrayList<String>(this.pathSteps);
        return clone;
    }

    public boolean equals(Object object) {
        if (logger.isTraceEnabled()) logger.trace("Comparing: " + this + " to " + object);
        if (!(object instanceof PathExpression)) {
            return false;
        }
        PathExpression otherPathExpression = (PathExpression) object;
        if (this.getPathSteps().size() != otherPathExpression.getPathSteps().size()) {
            return false;
        }
        for (int i = 0; i < this.getPathSteps().size(); i++) {
            if (!this.getPathSteps().get(i).equals(otherPathExpression.getPathSteps().get(i))) {
                if (logger.isTraceEnabled()) logger.trace("Found a different step: " + this.pathSteps.get(i) + " - Returning false");
                return false;
            }
        }
        if (logger.isTraceEnabled()) logger.trace("Paths are equal");
        return true;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public String toString() {
        String result = "";
        for (String nodeLabel : this.pathSteps) {
            if (!result.equals("")) {
                result += ".";
            }
            result += nodeLabel;
        }
        return result;
    }

}
