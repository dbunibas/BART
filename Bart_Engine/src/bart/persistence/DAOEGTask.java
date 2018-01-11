package bart.persistence;

import speedy.exceptions.DAOException;
import bart.model.EGTask;
import bart.model.EGTaskConfiguration;
import bart.persistence.parser.ParserMainMemoryDatabase;
import bart.persistence.parser.operators.ParseDependencies;
import speedy.model.database.EmptyDB;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.xml.DAOXmlUtility;
import speedy.persistence.xml.operators.TransformFilePaths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.persistence.file.CSVFile;
import speedy.persistence.file.IImportFile;
import speedy.persistence.file.XMLFile;

public class DAOEGTask {

    ///////////////////// DATABASE
    private static final String DB_TYPE_MAINMEMORY = "XML";
    private static final String DB_TYPE_MAINMEMORY_GENERATE = "GENERATE";
    private static final String DB_TYPE_DBMS = "DBMS";
    ///////////////////// PARTIAL ORDER
    private static Logger logger = LoggerFactory.getLogger(DAOEGTask.class);
    private DAOXmlUtility daoUtility = new DAOXmlUtility();
    private DAOEGTaskConfiguration daoEGTaskConfiguration = new DAOEGTaskConfiguration();
    private TransformFilePaths filePathTransformator = new TransformFilePaths();
    private ParserMainMemoryDatabase parserMainMemoryDatabase = new ParserMainMemoryDatabase();
    private String fileTask;

    public EGTask loadTask(String fileTask) {
        this.fileTask = fileTask;
        try {
            EGTask task = new EGTask(fileTask);
            task.setAbsolutePath(fileTask);
            Document document = daoUtility.buildDOM(fileTask);
            Element rootElement = document.getRootElement();
            //CONFIGURATION
            Element configurationElement = rootElement.getChild("configuration");
            EGTaskConfiguration configuration = daoEGTaskConfiguration.loadConfiguration(configurationElement, fileTask);
            task.setConfiguration(configuration);
            //SOURCE
            Element sourceElement = rootElement.getChild("source");
            IDatabase sourceDatabase = loadDatabase(sourceElement, task);
            task.setSource(sourceDatabase);
            //TARGET
            Element targetElement = rootElement.getChild("target");
            IDatabase targetDatabase = loadDatabase(targetElement, task);
            task.setTarget(targetDatabase);
            //AUTHORITATIVE SOURCES
            Element authoritativeSourcesElement = rootElement.getChild("authoritativeSources");
            List<String> authoritativeSources = loadAuthoritativeSources(authoritativeSourcesElement);
            task.setAuthoritativeSources(authoritativeSources);
            //DEPENDENCIES
            Element dependenciesElement = rootElement.getChild("dependencies");
            loadDependecies(dependenciesElement, task);
            return task;
        } catch (Throwable ex) {
            System.out.println("****** " + ex);
            logger.error(ex.getLocalizedMessage());
            ex.printStackTrace();
            String message = "Unable to load egtask from file " + fileTask;
            if (ex.getMessage() != null && !ex.getMessage().equals("NULL")) {
                message += "\n" + ex.getMessage();
            }
            throw new DAOException(message);
        }
    }

    private IDatabase loadDatabase(Element databaseElement, EGTask task) throws DAOException {
        if (databaseElement == null || databaseElement.getChildren().isEmpty()) {
            return new EmptyDB();
        }
        Element typeElement = databaseElement.getChild("type");
        if (typeElement == null) {
            throw new DAOException("Unable to load scenario from file " + fileTask + ". Missing tag <type>");
        }
        String databaseType = typeElement.getValue();
        if (DB_TYPE_MAINMEMORY.equalsIgnoreCase(databaseType)) {
            Element xmlElement = databaseElement.getChild("xml");
            if (xmlElement == null) {
                throw new DAOException("Unable to load scenario from file " + fileTask + ". Missing tag <xml>");
            }
            String schemaRelativeFile = xmlElement.getChild("xml-schema").getValue();
            String schemaAbsoluteFile = filePathTransformator.expand(fileTask, schemaRelativeFile);
            String instanceRelativeFile = xmlElement.getChild("xml-instance").getValue();
            String instanceAbsoluteFile = null; //Optional field
            if (instanceRelativeFile != null && !instanceRelativeFile.trim().isEmpty()) {
                instanceAbsoluteFile = filePathTransformator.expand(fileTask, instanceRelativeFile);
            }
            return parserMainMemoryDatabase.loadXMLScenario(schemaAbsoluteFile, instanceAbsoluteFile);
        } else if (DB_TYPE_MAINMEMORY_GENERATE.equalsIgnoreCase(databaseType)) {
            Element xmlElement = databaseElement.getChild("generate");
            if (xmlElement == null) {
                throw new DAOException("Unable to load scenario from file " + fileTask + ". Missing tag <generate>");
            }
            String plainInstance = xmlElement.getValue();
            return parserMainMemoryDatabase.loadPlainScenario(plainInstance);
        } else if (DB_TYPE_DBMS.equalsIgnoreCase(databaseType)) {
            Element dbmsElement = databaseElement.getChild("access-configuration");
            if (dbmsElement == null) {
                throw new DAOException("Unable to load scenario from file " + fileTask + ". Missing tag <access-configuration>");
            }
            AccessConfiguration accessConfiguration = new AccessConfiguration();
            accessConfiguration.setDriver(dbmsElement.getChildText("driver").trim());
            accessConfiguration.setUri(dbmsElement.getChildText("uri").trim());
            accessConfiguration.setSchemaName(dbmsElement.getChildText("schema").trim());
            accessConfiguration.setLogin(dbmsElement.getChildText("login").trim());
            accessConfiguration.setPassword(dbmsElement.getChildText("password").trim());
            Element initDbElement = databaseElement.getChild("init-db");
            DBMSDB database = new DBMSDB(accessConfiguration);
            if (initDbElement != null) {
                database.getInitDBConfiguration().setInitDBScript(initDbElement.getValue());
            }
            if (databaseElement.getChild("import-xml") != null) {
                throw new DAOException("The 'import-xml' tag is deprecated. Use Import instead");
            }
            Element importXmlElement = databaseElement.getChild("import");
            if (importXmlElement != null) {
                Attribute createTableAttribute = importXmlElement.getAttribute("createTables");
                if (createTableAttribute != null) {
                    database.getInitDBConfiguration().setCreateTablesFromFiles(Boolean.parseBoolean(createTableAttribute.getValue()));
                }
                for (Object inputFileObj : importXmlElement.getChildren("input")) {
                    Element inputFileElement = (Element) inputFileObj;
                    String fileName = inputFileElement.getText();
                    if (inputFileElement.getAttribute("table") == null) throw new DAOException("Attribute table for the tag 'input' is mandatory");
                    if (inputFileElement.getAttribute("type") == null) throw new DAOException("Attribute type for the tag 'input' is mandatory. Use CSV or XML as value");
                    String tableName = inputFileElement.getAttribute("table").getValue();
                    String type = inputFileElement.getAttribute("type").getValue();
                    fileName = filePathTransformator.expand(fileTask, fileName);
                    IImportFile fileToImport;
                    if (type.equalsIgnoreCase(SpeedyConstants.XML)) {
                        fileToImport = new XMLFile(fileName);
                    } else if (type.equalsIgnoreCase(SpeedyConstants.CSV)) {
                        CSVFile csvFile = new CSVFile(fileName);
                        if (inputFileElement.getAttribute("separator") != null) {
                            String separator = inputFileElement.getAttribute("separator").getValue();
                            csvFile.setSeparator(separator.charAt(0));
                        }
                        if (inputFileElement.getAttribute("quoteCharacter") != null) {
                            String quoteCharacter = inputFileElement.getAttribute("quoteCharacter").getValue();
                            csvFile.setQuoteCharacter(quoteCharacter.charAt(0));
                        }
                        if (inputFileElement.getAttribute("hasHeader") != null) {
                            boolean hasHeader = Boolean.parseBoolean(inputFileElement.getAttribute("hasHeader").getValue());
                            csvFile.setHasHeader(hasHeader);
                        }
                        if (inputFileElement.getAttribute("random") != null) {
                            boolean random = Boolean.parseBoolean(inputFileElement.getAttribute("random").getValue());
                            csvFile.setRandomizeInput(random);
                            database.getInitDBConfiguration().setUseCopyStatement(false);
                        }
                        if (inputFileElement.getAttribute("recordsToImport") != null) {
                            String recordsToImportString = inputFileElement.getAttribute("recordsToImport").getValue();
                            try {
                                Integer recordsToImport = Integer.parseInt(recordsToImportString);
                                csvFile.setRecordsToImport(recordsToImport);
                            } catch (NumberFormatException nfe) {
                                throw new DAOException("Attribute recordsToImport needs a valid integer");
                            }
                        }
                        fileToImport = csvFile;
                    } else {
                        throw new DAOException("Type " + type + " is not supported");
                    }
                    database.getInitDBConfiguration().addFileToImportForTable(tableName, fileToImport);
                }
            }
            return database;
        } else {
            throw new DAOException("Unable to load scenario from file " + fileTask + ". Unknown database type " + databaseType);
        }
    }

    private void loadDependecies(Element dependenciesElement, EGTask task) throws DAOException {
        if (dependenciesElement == null) {
            return;
        }
        String dependenciesString = dependenciesElement.getValue().trim();
        ParseDependencies generator = new ParseDependencies();
        try {
            generator.generateDependencies(dependenciesString, task);
        } catch (Exception ex) {
            throw new DAOException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> loadAuthoritativeSources(Element authoritativeSourcesElement) {
        if (authoritativeSourcesElement == null || authoritativeSourcesElement.getChildren().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<String> sources = new ArrayList<String>();
        List<Element> sourceElements = authoritativeSourcesElement.getChildren("source");
        for (Element sourceElement : sourceElements) {
            sources.add(sourceElement.getText());
        }
        return sources;
    }
}
