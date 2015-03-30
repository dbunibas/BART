package bart.model.database.mainmemory.paths.operators;

import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.paths.PathExpression;
import java.util.List;

public class CheckPathContainment {
    
    public boolean isPrefixOf(PathExpression firstPath, PathExpression secondPath) {
        if (firstPath.getPathSteps().size() > secondPath.getPathSteps().size()) {
            return false;
        }
        for (int i = 0; i < firstPath.getPathSteps().size(); i++) {
            if (!firstPath.getPathSteps().get(i).equals(secondPath.getPathSteps().get(i))) {
                return false;
            }
        }
        return true;
    }
    
    public boolean correctExclusion(PathExpression exclusionPath, List<PathExpression> inclusions) {
        for (PathExpression inclusionPath : inclusions) {
            if (isPrefixOf(inclusionPath, exclusionPath)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsNode(PathExpression pathExpression, INode node, INode root) {
        for (INode pathStep : pathExpression.getPathNodes(root)) {
            if (pathStep.equals(node)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsNode(List<PathExpression> pathList, INode node, INode root) {
        for (PathExpression pathExpression : pathList) {
            if (containsNode(pathExpression, node, root)) {
                return true;
            }
        }
        return false;
    }

}
