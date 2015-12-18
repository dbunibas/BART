package bart.persistence;

import speedy.model.database.mainmemory.datasource.*;
import speedy.model.database.mainmemory.datasource.nodes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceUtility {

    private static Logger logger = LoggerFactory.getLogger(PersistenceUtility.class);

    public static void createEmptyTables(DataSource dataSource) {
        INode emptyInstance = PersistenceUtility.generateInstanceNode(dataSource.getSchema());
        for (INode setNode : dataSource.getSchema().getChildren()) {
            emptyInstance.addChild(PersistenceUtility.generateInstanceNode(setNode));
        }
        dataSource.addInstanceWithCheck(emptyInstance);
    }

    public static INode generateInstanceNode(INode schemaNode) {
        INode instanceNode = null;
        if (schemaNode instanceof SetNode) {
            instanceNode = new SetNode(schemaNode.getLabel(), getOID());
        } else if (schemaNode instanceof SequenceNode) {
            instanceNode = new SequenceNode(schemaNode.getLabel(), getOID());
        } else if (schemaNode instanceof TupleNode) {
            instanceNode = new TupleNode(schemaNode.getLabel(), getOID());
        } else if (schemaNode instanceof MetadataNode) {
            instanceNode = new MetadataNode(schemaNode.getLabel(), getOID());
        } else if (schemaNode instanceof AttributeNode) {
            instanceNode = new AttributeNode(schemaNode.getLabel(), getOID());
        }
        instanceNode.setRoot(schemaNode.isRoot());
        instanceNode.setVirtual(schemaNode.isVirtual());
        if (logger.isDebugEnabled()) logger.debug("Generated instance node: " + instanceNode.getLabel());
        return instanceNode;
    }

    public static OID getOID() {
        return IntegerOIDGenerator.getNextOID();
    }
}
