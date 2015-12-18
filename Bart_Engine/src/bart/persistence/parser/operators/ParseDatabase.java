package bart.persistence.parser.operators;

import bart.persistence.parser.ParserAttribute;
import bart.persistence.parser.ParserFact;
import bart.persistence.parser.ParserInstance;
import bart.persistence.parser.ParserSchema;
import bart.persistence.parser.ParserTable;
import bart.persistence.parser.output.DatabaseLexer;
import bart.persistence.parser.output.DatabaseParser;
import speedy.model.database.IDatabase;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.model.database.mainmemory.datasource.DataSource;
import speedy.model.database.mainmemory.datasource.INode;
import speedy.model.database.mainmemory.datasource.nodes.AttributeNode;
import speedy.model.database.mainmemory.datasource.nodes.LeafNode;
import speedy.model.database.mainmemory.datasource.nodes.SetNode;
import speedy.model.database.mainmemory.datasource.nodes.TupleNode;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.model.database.mainmemory.datasource.NullValueFactory;
import bart.persistence.PersistenceConstants;
import bart.persistence.PersistenceUtility;
import bart.persistence.Types;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.ParserException;

public class ParseDatabase {

    private static Logger logger = LoggerFactory.getLogger(ParseDatabase.class);

    private ParserSchema schema;
    private ParserInstance instance;
    private IntegerOIDGenerator oidGenerator = new IntegerOIDGenerator();
    public final static String NULL = "#NULL#";

    public IDatabase generateDatabase(String text) throws Exception {
        try {
            DatabaseLexer lex = new DatabaseLexer(new ANTLRStringStream(text));
            CommonTokenStream tokens = new CommonTokenStream(lex);
            DatabaseParser g = new DatabaseParser(tokens);
            try {
                g.setGenerator(this);
                g.prog();
            } catch (RecognitionException ex) {
                logger.error("Unable to load mapping task: " + ex.getMessage());
                throw new ParserException(ex);
            }
            //Load schema
            INode schemaNode = new TupleNode(PersistenceConstants.DATASOURCE_ROOT_LABEL, oidGenerator.getNextOID());
            schemaNode.setRoot(true);
            generateSchema(schemaNode, schema);
            DataSource dataSource = new DataSource(PersistenceConstants.TYPE_XML, schemaNode);
            //Load instance
            if (instance != null) {
                INode instanceNode = new TupleNode(PersistenceConstants.DATASOURCE_ROOT_LABEL, oidGenerator.getNextOID());
                instanceNode.setRoot(true);
                for (INode setNode : dataSource.getSchema().getChildren()) {
                    instanceNode.addChild(PersistenceUtility.generateInstanceNode(setNode));
                }
                addFactsToInstance(instanceNode, instance);
                dataSource.addInstanceWithCheck(instanceNode);
            } else {
                PersistenceUtility.createEmptyTables(dataSource);
            }
            return new MainMemoryDB(dataSource);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
            throw new ParserException(e);
        }
    }

    private void generateSchema(INode schemaNode, ParserSchema parserSchema) {
        if (logger.isDebugEnabled()) logger.debug("Parser schema: " + parserSchema);
        for (ParserTable parserTable : parserSchema.getTables()) {
            INode setNodeSchema = findSetNode(parserTable.getSet(), schemaNode);
            if (setNodeSchema == null) {
                setNodeSchema = new SetNode(parserTable.getSet(), oidGenerator.getNextOID());
                schemaNode.addChild(setNodeSchema);
            }
            TupleNode tupleNodeSchema = new TupleNode(parserTable.getSet() + "Tuple", oidGenerator.getNextOID());
            setNodeSchema.addChild(tupleNodeSchema);
            for (ParserAttribute parserAttribute : parserTable.getAttributes()) {
                AttributeNode attributeNodeInstance = new AttributeNode(parserAttribute.getName(), oidGenerator.getNextOID());
                LeafNode leafNodeInstance = new LeafNode(Types.STRING, NullValueFactory.getNullValue());
                attributeNodeInstance.addChild(leafNodeInstance);
                tupleNodeSchema.addChild(attributeNodeInstance);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Final instance" + schemaNode);
    }

    private void addFactsToInstance(INode rootInstance, ParserInstance parserInstance) {
        if (logger.isDebugEnabled()) logger.debug("Parser instance: " + parserInstance);
        for (ParserFact parserFact : parserInstance.getFacts()) {
            INode setNodeInstance = findSetNode(parserFact.getSet(), rootInstance);
            if (setNodeInstance == null) {
                setNodeInstance = new SetNode(parserFact.getSet(), oidGenerator.getNextOID());
                rootInstance.addChild(setNodeInstance);
            }
            TupleNode tupleNodeInstance = new TupleNode(parserFact.getSet() + "Tuple", oidGenerator.getNextOID());
            setNodeInstance.addChild(tupleNodeInstance);
            for (ParserAttribute parserAttribute : parserFact.getAttributes()) {
                AttributeNode attributeNodeInstance = new AttributeNode(parserAttribute.getName(), oidGenerator.getNextOID());
                LeafNode leafNodeInstance = null;
                if (parserAttribute.getValue().equals(NULL)) {
                    leafNodeInstance = new LeafNode(Types.STRING, NullValueFactory.getNullValue());
                } else {
                    String valueType = findType(parserAttribute.getValue());
                    leafNodeInstance = new LeafNode(valueType, parserAttribute.getValue());
                }
                attributeNodeInstance.addChild(leafNodeInstance);
                tupleNodeInstance.addChild(attributeNodeInstance);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Final instance" + rootInstance);
    }

    private String findType(Object value) {
        try {
            Integer.parseInt(value.toString());
            return Types.INTEGER;
        } catch (NumberFormatException e) {
        }
        try {
            Double.parseDouble(value.toString());
            return Types.DOUBLE;
        } catch (NumberFormatException e) {
        }
        return Types.STRING;
    }

    private INode findSetNode(String setName, INode rootInstance) {
        for (INode child : rootInstance.getChildren()) {
            if (child.getLabel().equals(setName)) {
                return child;
            }
        }
        return null;
    }

    public void setInstance(ParserInstance instance) {
        this.instance = instance;
    }

    public void setSchema(ParserSchema schema) {
        this.schema = schema;
    }
}
