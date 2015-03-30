package bart.model.database.mainmemory.datasource.operators;

import bart.model.database.mainmemory.datasource.DataSource;
import bart.model.database.mainmemory.datasource.ForeignKeyConstraint;
import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.KeyConstraint;
import bart.model.database.mainmemory.paths.PathExpression;
import bart.model.database.mainmemory.paths.operators.GeneratePathExpression;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckJoinsAndKeys {

    private static Logger logger = LoggerFactory.getLogger(CheckJoinsAndKeys.class);


    private GeneratePathExpression pathGenerator = new GeneratePathExpression();
    private FindNode nodeFinder = new FindNode();

    public boolean checkIfIsKey(INode node, DataSource dataSource) {
        PathExpression pathExpression = pathGenerator.generatePathFromRoot(node);
        return checkIfIsKey(pathExpression, dataSource);
    }

    public boolean checkIfIsKey(PathExpression nodePath, DataSource dataSource) {
        List<KeyConstraint> listKeyConstraints = dataSource.getKeyConstraints();
        for (KeyConstraint keyConstraint : listKeyConstraints) {
            if (keyConstraint.getKeyPaths().contains(nodePath)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkIfIsForeignKey(INode node, DataSource dataSource) {
        PathExpression pathExpression = pathGenerator.generatePathFromRoot(node);
        return checkIfIsForeignKey(pathExpression, dataSource);
    }

    public boolean checkIfIsForeignKey(PathExpression nodePath, DataSource dataSource) {
        List<ForeignKeyConstraint> listForeignKeyConstraints = dataSource.getForeignKeyConstraints();
        for (ForeignKeyConstraint foreignKeyConstraint : listForeignKeyConstraints) {
            if (foreignKeyConstraint.getForeignKeyPaths().contains(nodePath)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkTypes(ForeignKeyConstraint foreignKeyConstraint, DataSource dataSource) {
        for (int i = 0; i < foreignKeyConstraint.getForeignKeyPaths().size(); i++) {
            PathExpression keyPath = foreignKeyConstraint.getKeyConstraint().getKeyPaths().get(i);
            INode keyNode = nodeFinder.findNodeInSchema(keyPath, dataSource);
            String keyType = keyNode.getChild(0).getLabel();
            PathExpression foreignKeyPath = foreignKeyConstraint.getForeignKeyPaths().get(i);
            INode foreignKeyNode = nodeFinder.findNodeInSchema(foreignKeyPath, dataSource);
            String foreignKeyType = foreignKeyNode.getChild(0).getLabel();
            if (!(keyType.equals(foreignKeyType))) {
                logger.error("Found mismatching types in foreign key constraint. Key path= " + keyPath + "- key type = " + keyType + " - Foreign key path=" + foreignKeyPath + " - foreign key type =" + foreignKeyType);
                return false;
            }
        }
        return true;
    }

}
