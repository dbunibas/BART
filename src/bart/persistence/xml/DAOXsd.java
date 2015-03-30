package bart.persistence.xml;

import bart.exceptions.DAOException;
import bart.model.database.mainmemory.datasource.DataSource;
import bart.model.database.mainmemory.datasource.INode;
import bart.persistence.PersistenceConstants;
import bart.persistence.xml.model.XSDSchema;
import bart.persistence.xml.operators.GenerateSchemaFromXSDTree;
import bart.persistence.xml.operators.GenerateXSDNodeTree;
import bart.persistence.xml.operators.LoadXMLFile;
import bart.persistence.xml.operators.UpdateDataSourceWithConstraints;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DAOXsd {

    private static Logger logger = LoggerFactory.getLogger(DAOXsd.class);

    public DataSource loadSchema(String fileName) throws DAOException {
        try {
            GenerateXSDNodeTree xsdTreeGenerator = new GenerateXSDNodeTree();
            XSDSchema xsdSchema = xsdTreeGenerator.generateXSDNodeTree(fileName);
            if (logger.isDebugEnabled()) logger.debug("XSD Schema:\n" + xsdSchema);
            GenerateSchemaFromXSDTree schemaGenerator = new GenerateSchemaFromXSDTree();
            INode schema = schemaGenerator.generateSchema(xsdSchema);
            DataSource dataSource = new DataSource(PersistenceConstants.TYPE_XML, schema);
            dataSource.addAnnotation(PersistenceConstants.XML_SCHEMA_FILE, fileName);
            dataSource.addAnnotation(PersistenceConstants.XML_INSTANCE_FILE_LIST, new ArrayList<String>());
            // UpdateDataSourceWithConstraints has state, and therefore we need a fresh copy for each invocation
            UpdateDataSourceWithConstraints updateDataSource = new UpdateDataSourceWithConstraints();
            updateDataSource.updateDataSource(dataSource, xsdSchema);
            if (logger.isDebugEnabled()) logger.debug(dataSource.getSchema().toString());
            return dataSource;
        } catch (Throwable ex) {
            logger.error("Error: " + ex);
            throw new DAOException(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadInstance(DataSource dataSource, String fileName) throws DAOException {
        try {
            LoadXMLFile xmlLoader = new LoadXMLFile();
            INode instanceNode = xmlLoader.loadInstance(dataSource, fileName);
            dataSource.addInstanceWithCheck(instanceNode);
            List<String> instanceFiles = (List<String>) dataSource.getAnnotation(PersistenceConstants.XML_INSTANCE_FILE_LIST);
            if (instanceFiles == null) {
                instanceFiles = new ArrayList<String>();
                dataSource.addAnnotation(PersistenceConstants.XML_INSTANCE_FILE_LIST, instanceFiles);
            }
            instanceFiles.add(fileName);
        } catch (Throwable ex) {
            logger.error(ex.toString());
            throw new DAOException(ex.getMessage());
        }
    }
}