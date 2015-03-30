package bart.model.database.operators.dbms;

import bart.BartConstants;
import bart.exceptions.DAOException;
import bart.model.EGTask;
import bart.model.EGTaskConfiguration;
import bart.model.database.Attribute;
import bart.model.database.dbms.DBMSDB;
import bart.model.database.dbms.InitDBConfiguration;
import bart.persistence.Types;
import bart.persistence.relational.AccessConfiguration;
import bart.utility.DBMSUtility;
import bart.persistence.relational.QueryManager;
import bart.persistence.xml.DAOXmlUtility;
import bart.utility.BartUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteInitDB {

    private static Logger logger = LoggerFactory.getLogger(ExecuteInitDB.class);
    private DAOXmlUtility daoUtility = new DAOXmlUtility();

    public void execute(DBMSDB db, EGTask task) {
        InitDBConfiguration configuration = db.getInitDBConfiguration();
        if (logger.isDebugEnabled()) logger.debug("Initializating DB with configuration " + configuration);
        AccessConfiguration accessConfiguration = db.getAccessConfiguration();
        if (configuration.getInitDBScript() == null && !configuration.getXmlFilesToImport().isEmpty()) {
            configuration.setInitDBScript(createSchemaScript(accessConfiguration.getSchemaName()));
        }
        if (configuration.getInitDBScript() != null) {
            QueryManager.executeScript(configuration.getInitDBScript(), accessConfiguration, false, true, false, false);
        }
        if (!configuration.getXmlFilesToImport().isEmpty()) {
            importXMLFiles(db, task);
        }
    }

    private void importXMLFiles(DBMSDB db, EGTask task) {
        InitDBConfiguration configuration = db.getInitDBConfiguration();
        Map<String, List<Attribute>> tablesAdded = new HashMap<String, List<Attribute>>();
        for (String xmlFile : configuration.getXmlFilesToImport()) {
            if (logger.isDebugEnabled()) logger.debug("Importing xml file " + xmlFile);
            importXMLFile(xmlFile, tablesAdded, db, task);
        }
    }

    @SuppressWarnings("unchecked")
    private void importXMLFile(String xmlFile, Map<String, List<Attribute>> tablesAdded, DBMSDB db, EGTask task) {
        InitDBConfiguration configuration = db.getInitDBConfiguration();
        try {
            Document document = daoUtility.buildDOM(xmlFile);
            Element tableElement = document.getRootElement();
            String tableName = tableElement.getName();
            if (tableElement.getChildren().isEmpty()) {
                throw new DAOException("Unable to import file from  " + tableName + ". Table " + tableName + " is empty");
            }
//            if (task.getConfiguration().isPrint()) 
                System.out.println("Importing file " + xmlFile + " into table " + tableName + "...");
            if (!tablesAdded.containsKey(tableName)) {
                List<Attribute> attributes = createTable(tableElement, db.getAccessConfiguration(), configuration.isCreateTablesFromXML());
                tablesAdded.put(tableName, attributes);
            }
            List<Attribute> attributes = tablesAdded.get(tableName);
            insertTuples(tableName, attributes, tableElement, db.getAccessConfiguration(), xmlFile, task);
        } catch (DAOException ex) {
            logger.error(ex.getLocalizedMessage());
            ex.printStackTrace();
            String message = "Unable to load XML file " + xmlFile;
            if (ex.getMessage() != null && !ex.getMessage().equals("NULL")) {
                message += "\n" + ex.getMessage();
            }
            throw new DAOException(message);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Attribute> createTable(Element tableElement, AccessConfiguration accessConfiguration, boolean createTable) {
        List<Attribute> attributes = new ArrayList<Attribute>();
        String tableName = tableElement.getName();
        StringBuilder sb = new StringBuilder();
        sb.append("create table ").append(DBMSUtility.getSchema(accessConfiguration)).append(tableName).append("(\n");
        sb.append(BartConstants.INDENT).append("oid serial,\n");
        Element firstChild = (Element) tableElement.getChildren().get(0);
        for (Element attributeElement : (List<Element>) firstChild.getChildren()) {
            String attributeName = attributeElement.getName();
            String attributeType = Types.STRING;
            if (attributeElement.getAttribute("type") != null) {
                attributeType = attributeElement.getAttribute("type").getValue();
            }
            Attribute attribute = new Attribute(tableName, attributeName, attributeType);
            attributes.add(attribute);
            sb.append(BartConstants.INDENT).append(attributeName).append(" ").append(DBMSUtility.convertDataSourceTypeToDBType(attributeType)).append(",\n");
        }
        BartUtility.removeChars(",\n".length(), sb);
//        sb.append(") with oids;");
        sb.append(");");
        if (logger.isDebugEnabled()) logger.debug("Executing script " + sb.toString());
        if (createTable) {
            QueryManager.executeScript(sb.toString(), accessConfiguration, false, true, false, false);
        }
        return attributes;
    }

    @SuppressWarnings("unchecked")
    private void insertTuples(String tableName, List<Attribute> attributes, Element tableElement, AccessConfiguration accessConfiguration, String xmlFile, EGTask task) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Element tupleElement : (List<Element>) tableElement.getChildren()) {
            if (count > 0 && count % 10000 == 0) {
                QueryManager.executeScript(sb.toString(), accessConfiguration, false, true, false, false);
                sb = new StringBuilder();
//                if (task.getConfiguration().isPrint()) 
                    System.out.println("..." + count + " tuple inserted in table " + tableName);
                if (logger.isDebugEnabled()) logger.debug("..." + count + " tuple inserted in table " + tableName);
            }
            sb.append("insert into ").append(DBMSUtility.getSchema(accessConfiguration)).append(tableName).append("(");
            for (Attribute attribute : attributes) {
                sb.append(attribute.getName()).append(", ");
            }
            BartUtility.removeChars(", ".length(), sb);
            sb.append(") values (");
            for (Attribute attribute : attributes) {
                Element attributeElement = tupleElement.getChild(attribute.getName());
                if (attributeElement == null) {
                    throw new DAOException("Error importing " + xmlFile + ". Attribute " + attribute.getName() + " in table " + tableName + " is missing");
                }
                String value = attributeElement.getText();
                if (notNull(value) && attribute.getType().equals(Types.STRING)) sb.append("'");
                sb.append(cleanValue(value));
                if (notNull(value) && attribute.getType().equals(Types.STRING)) sb.append("'");
                sb.append(", ");
            }
            BartUtility.removeChars(", ".length(), sb);
            sb.append(");\n");
            count++;
        }
        if (sb.toString().isEmpty()) {
            return;
        }
//        if (task.getConfiguration().isPrint()) 
            System.out.println(count + " tuple inserted in table " + tableName);
        if (logger.isDebugEnabled()) logger.debug(count + " tuple inserted in table " + tableName);
        QueryManager.executeScript(sb.toString(), accessConfiguration, false, true, false, false);
    }

    private String cleanValue(String string) {
        String sqlValue = string;
        sqlValue = sqlValue.replaceAll("'", "''");
        return sqlValue;
    }

    private String createSchemaScript(String schemaName) {
        if (schemaName.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("create schema ").append(schemaName).append(";\n");
        return sb.toString();
    }

    private boolean notNull(String value) {
        return value != null && !value.equalsIgnoreCase("NULL");
    }

}
