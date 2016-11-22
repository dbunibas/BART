package bart.utility;

import bart.BartConstants;
import speedy.model.database.*;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaVariableOccurrence;
import speedy.model.expressions.Expression;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import speedy.persistence.relational.SimpleDbConnectionFactory;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.nfunk.jep.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.OperatorFactory;
import speedy.exceptions.DAOException;
import speedy.exceptions.DBMSException;
import speedy.model.algebra.Scan;
import speedy.model.algebra.Select;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.operators.IRunQuery;
import speedy.utility.DBMSUtility;

public class BartDBMSUtility {

    private static Logger logger = LoggerFactory.getLogger(BartDBMSUtility.class);

//    public static final String TEMP_DB_NAME = "testdb";
    public static final String TEMP_DB_NAME = "template1";
    private static SimpleDbConnectionFactory simpleDataSourceDB = new SimpleDbConnectionFactory();

    public static List<String> loadTableNames(AccessConfiguration accessConfiguration) {
        List<String> tableNames = new ArrayList<String>();
        String schemaName = accessConfiguration.getSchemaName();
        ResultSet tableResultSet = null;
        Connection connection = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Loading table names: " + accessConfiguration);
            connection = QueryManager.getConnection(accessConfiguration);
            String catalog = connection.getCatalog();
            if (catalog == null) {
                catalog = accessConfiguration.getUri();
                if (logger.isDebugEnabled()) logger.debug("Catalog is null. Catalog name will be: " + catalog);
            }
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            tableResultSet = databaseMetaData.getTables(catalog, schemaName, null, new String[]{"TABLE"});
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        } catch (DAOException daoe) {
            throw new DBMSException("Error connecting to database.\n" + accessConfiguration + "\n" + daoe.getLocalizedMessage());
        } catch (SQLException sqle) {
            throw new DBMSException("Error connecting to database.\n" + accessConfiguration + "\n" + sqle.getLocalizedMessage());
        } finally {
            QueryManager.closeResultSet(tableResultSet);
            QueryManager.closeConnection(connection);
        }
        return tableNames;
    }

    public static List<Key> loadKeys(AccessConfiguration accessConfiguration) {
        List<Key> result = new ArrayList<Key>();
        String schemaName = accessConfiguration.getSchemaName();
        Connection connection = null;
        ResultSet tableResultSet = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Loading keys: " + accessConfiguration);
            connection = QueryManager.getConnection(accessConfiguration);
            String catalog = connection.getCatalog();
            if (catalog == null) {
                catalog = accessConfiguration.getUri();
                if (logger.isDebugEnabled()) logger.debug("Catalog is null. Catalog name will be: " + catalog);
            }
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            tableResultSet = databaseMetaData.getTables(catalog, schemaName, null, new String[]{"TABLE"});
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                if (logger.isDebugEnabled()) logger.debug("Searching primary keys. ANALYZING TABLE  = " + tableName);
                ResultSet resultSet = databaseMetaData.getPrimaryKeys(catalog, null, tableName);
                while (resultSet.next()) {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    if (logger.isDebugEnabled()) logger.debug("Analyzing primary key: " + columnName);
                    if (logger.isDebugEnabled()) logger.debug("Found a Primary Key: " + columnName);
                    Key key = new Key(new AttributeRef(tableName, columnName), true);
                    result.add(key);
                }
            }
        } catch (DAOException daoe) {
            throw new DBMSException("Error connecting to database.\n" + accessConfiguration + "\n" + daoe.getLocalizedMessage());
        } catch (SQLException sqle) {
            throw new DBMSException("Error connecting to database.\n" + accessConfiguration + "\n" + sqle.getLocalizedMessage());
        } finally {
            QueryManager.closeResultSet(tableResultSet);
            QueryManager.closeConnection(connection);
        }
        return result;
    }

    public static List<ForeignKey> loadForeignKeys(AccessConfiguration accessConfiguration) {
        Map<String, ForeignKey> foreignKeyMap = new HashMap<String, ForeignKey>();
        String schemaName = accessConfiguration.getSchemaName();
        Connection connection = null;
        ResultSet tableResultSet = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Loading foreign keys: " + accessConfiguration);
            connection = QueryManager.getConnection(accessConfiguration);
            String catalog = connection.getCatalog();
            if (catalog == null) {
                catalog = accessConfiguration.getUri();
                if (logger.isDebugEnabled()) logger.debug("Catalog is null. Catalog name will be: " + catalog);
            }
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            tableResultSet = databaseMetaData.getTables(catalog, schemaName, null, new String[]{"TABLE"});
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                if (logger.isDebugEnabled()) logger.debug("Searching foreign keys. ANALYZING TABLE  = " + tableName);
                ResultSet resultSet = databaseMetaData.getImportedKeys(catalog, null, tableName);
                while (resultSet.next()) {
                    String fkeyName = resultSet.getString("FK_NAME");
                    String pkTableName = resultSet.getString("PKTABLE_NAME");
                    String pkColumnName = resultSet.getString("PKCOLUMN_NAME");
                    String keyPrimaryKey = pkTableName + "." + pkColumnName;
                    String fkTableName = resultSet.getString("FKTABLE_NAME");
                    String fkColumnName = resultSet.getString("FKCOLUMN_NAME");
                    String keyForeignKey = fkTableName + "." + fkColumnName;
                    if (logger.isDebugEnabled()) logger.debug("Analyzing Primary Key: " + keyPrimaryKey + " Found a Foreign Key: " + fkColumnName + " in table " + fkTableName);
                    if (logger.isDebugEnabled()) logger.debug("Analyzing foreign key: " + keyForeignKey + " references " + keyPrimaryKey);
                    addFKToMap(foreignKeyMap, fkeyName, new AttributeRef(pkTableName, pkColumnName), new AttributeRef(fkTableName, fkColumnName));
                }
            }
        } catch (DAOException daoe) {
            throw new DBMSException("Error connecting to database.\n" + accessConfiguration + "\n" + daoe.getLocalizedMessage());
        } catch (SQLException sqle) {
            throw new DBMSException("Error connecting to database.\n" + accessConfiguration + "\n" + sqle.getLocalizedMessage());
        } finally {
            QueryManager.closeResultSet(tableResultSet);
            QueryManager.closeConnection(connection);
        }
        return new ArrayList<ForeignKey>(foreignKeyMap.values());
    }

    private static void addFKToMap(Map<String, ForeignKey> foreignKeyMap, String fkeyName, AttributeRef keyAttribute, AttributeRef refAttribute) {
        ForeignKey fk = foreignKeyMap.get(fkeyName);
        if (fk == null) {
            fk = new ForeignKey(keyAttribute, refAttribute);
            foreignKeyMap.put(fkeyName, fk);
            return;
        }
        fk.getKeyAttributes().add(keyAttribute);
        fk.getRefAttributes().add(refAttribute);
    }

    public static boolean isDBExists(AccessConfiguration accessConfiguration) {
        Connection connection = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Checking if db exists: " + accessConfiguration);
//            connection = new SimpleDbConnectionFactory().getConnection(accessConfiguration);
            connection = simpleDataSourceDB.getConnection(accessConfiguration);
//            connection = QueryManager.getConnection(accessConfiguration);
            return true;
        } catch (Exception daoe) {
        } finally {
            QueryManager.closeConnection(connection);
        }
        return false;
    }

    public static boolean isSchemaExists(AccessConfiguration accessConfiguration) {
        Connection connection = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Checking if schema exists: " + accessConfiguration);
            connection = new SimpleDbConnectionFactory().getConnection(accessConfiguration);
            connection = simpleDataSourceDB.getConnection(accessConfiguration);
//            connection = QueryManager.getConnection(accessConfiguration);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet schemaResultSet = databaseMetaData.getSchemas();
            while (schemaResultSet.next()) {
                String schemaName = schemaResultSet.getString("TABLE_SCHEM");
                if (schemaName.equals(accessConfiguration.getSchemaName())) {
                    return true;
                }
            }
        } catch (Exception daoe) {
        } finally {
            QueryManager.closeConnection(connection);
        }
        return false;
    }

    public static void createDB(AccessConfiguration accessConfiguration) {
        AccessConfiguration tempAccessConfiguration = getTempAccessConfiguration(accessConfiguration);
        Connection connection = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Creating db: " + accessConfiguration);
//            connection = new SimpleDbConnectionFactory().getConnection(tempAccessConfiguration);
            connection = simpleDataSourceDB.getConnection(tempAccessConfiguration);
//            connection = QueryManager.getConnection(tempAccessConfiguration);
            String createQuery = "create database " + accessConfiguration.getDatabaseName() + ";";
            ScriptRunner scriptRunner = getScriptRunner(connection);
            scriptRunner.setAutoCommit(true);
            scriptRunner.setStopOnError(true);
            scriptRunner.runScript(new StringReader(createQuery));
        } catch (Exception daoe) {
            throw new DBMSException("Unable to create new database " + accessConfiguration.getDatabaseName() + ".\n" + tempAccessConfiguration + "\n" + daoe.getLocalizedMessage());
        } finally {
            QueryManager.closeConnection(connection);
        }
    }

    public static void deleteDB(AccessConfiguration accessConfiguration) {
        String script = "DROP DATABASE " + accessConfiguration.getDatabaseName() + ";\n";
        AccessConfiguration tempAccessConfiguration = getTempAccessConfiguration(accessConfiguration);
        Connection connection = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Deleting db: " + accessConfiguration);
//            connection = new SimpleDbConnectionFactory().getConnection(tempAccessConfiguration);
            connection = simpleDataSourceDB.getConnection(tempAccessConfiguration);
//            connection = QueryManager.getConnection(tempAccessConfiguration);
            ScriptRunner scriptRunner = getScriptRunner(connection);
            scriptRunner.setAutoCommit(true);
            scriptRunner.setStopOnError(true);
            scriptRunner.runScript(new StringReader(script));
        } catch (Exception daoe) {
            throw new DBMSException("Unable to drop database " + accessConfiguration.getDatabaseName() + ".\n" + tempAccessConfiguration + "\n" + daoe.getLocalizedMessage());
        } finally {
            QueryManager.closeConnection(connection);
        }
    }

    private static ScriptRunner getScriptRunner(Connection connection) {
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        scriptRunner.setLogWriter(null);
//        scriptRunner.setErrorLogWriter(null);
        return scriptRunner;
    }

    public static ResultSet getTableResultSet(String tableName, AccessConfiguration accessConfiguration) {
//        String query = "SELECT " + BartConstants.OID + ",* FROM " + getSchema(accessConfiguration) + tableName;
        String query = "SELECT " + BartConstants.OID + ",* FROM " + getSchema(accessConfiguration) + tableName + " ORDER BY " + BartConstants.OID;
        return QueryManager.executeQuery(query, accessConfiguration);
    }

    public static String createTablePaginationQuery(String tableName, AccessConfiguration accessConfiguration, int offset, int limit) {
        return "SELECT " + BartConstants.OID + ",* FROM " + getSchema(accessConfiguration) + tableName + " LIMIT " + limit + " OFFSET " + offset;
    }

    public static ResultSet getTableOidsResultSet(String tableName, AccessConfiguration accessConfiguration) {
        String query = "SELECT " + BartConstants.OID + " FROM " + getSchema(accessConfiguration) + tableName;
        return QueryManager.executeQuery(query, accessConfiguration);
    }

    public static ResultSet getTupleResultSet(String tableName, TupleOID oid, AccessConfiguration accessConfiguration, Connection c) {
        String query = "SELECT " + BartConstants.OID + ",* FROM " + getSchema(accessConfiguration) + tableName + " WHERE " + BartConstants.OID + "=" + oid.toString();
        return QueryManager.executeQuery(query, c, accessConfiguration);
    }

    public static ResultSet getTableResultSetForSchema(String tableName, AccessConfiguration accessConfiguration) {
        String query = "SELECT " + BartConstants.OID + ",* FROM " + getSchema(accessConfiguration) + tableName + " LIMIT 0";
        return QueryManager.executeQuery(query, accessConfiguration);
    }

    public static String getSchema(AccessConfiguration accessConfiguration) {
        String schemaName = accessConfiguration.getSchemaName();
        if (schemaName == null || schemaName.isEmpty()) {
            return "";
        }
        return accessConfiguration.getSchemaName() + ".";
    }

    public static Tuple createTupleFromQuery(ResultSet resultSet) {
        return createTuple(resultSet, null);
    }

    public static Tuple createTuple(ResultSet resultSet, String tableName) {
        try {
            ResultSetMetaData metadata = resultSet.getMetaData();
            int startColumn = 1;
            Object oidValue = findOIDColumn(metadata, resultSet);
            if (metadata.getColumnCount() >= 1 && metadata.getColumnName(1).equals(BartConstants.OID)) {
                oidValue = resultSet.getObject(1);
                startColumn = 2;
            }
            if (metadata.getColumnCount() >= 2 && metadata.getColumnName(2).equals(BartConstants.OID)) {
                startColumn = 3;
            }
            TupleOID tupleOID = new TupleOID(oidValue);
            Tuple tuple = new Tuple(tupleOID);
            Cell oidCell = new Cell(tupleOID, new AttributeRef(tableName, BartConstants.OID), new ConstantValue(tupleOID));
            tuple.addCell(oidCell);
            int columns = metadata.getColumnCount();
            for (int col = startColumn; col <= columns; col++) {
//                String attributeName = metadata.getColumnName(col); //POSTGRES
                String attributeName = metadata.getColumnLabel(col);
                Object attributeValue = resultSet.getObject(col);
                IValue value = convertDBMSValue(attributeValue);
                AttributeRef attributeRef;
                if (tableName != null) {
                    attributeRef = new AttributeRef(tableName, attributeName);
                } else {
                    attributeRef = extractAttributeRef(attributeName);
                }
                Cell cell = new Cell(tupleOID, attributeRef, value);
                tuple.addCell(cell);
            }
            return tuple;
        } catch (Exception daoe) {
            daoe.printStackTrace();
            throw new DBMSException("Unable to read tuple.\n" + daoe.getLocalizedMessage());
        }
    }

    private static Object findOIDColumn(ResultSetMetaData metadata, ResultSet resultSet) throws SQLException {
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            if (metadata.getColumnName(i).equalsIgnoreCase(BartConstants.OID)) {
                return resultSet.getObject(i);
            }
        }
        return IntegerOIDGenerator.getNextOID();
    }

    public static List<Attribute> getTableAttributes(ResultSet resultSet, String tableName) throws SQLException {
        List<Attribute> result = new ArrayList<Attribute>();
        ResultSetMetaData metadata = resultSet.getMetaData();
        int columns = metadata.getColumnCount();
        for (int col = 1; col <= columns; col++) {
            String attributeName = metadata.getColumnName(col);
            String attributeType = metadata.getColumnTypeName(col);
            Attribute attribute = new Attribute(tableName, attributeName, DBMSUtility.convertDBTypeToDataSourceType(attributeType));
            result.add(attribute);
        }
        return result;
    }

    public static AccessConfiguration getTempAccessConfiguration(AccessConfiguration accessConfiguration) {
        AccessConfiguration tmpAccess = new AccessConfiguration();
        tmpAccess.setDriver(accessConfiguration.getDriver());
        tmpAccess.setUri(getTempDBName(accessConfiguration, TEMP_DB_NAME));
        tmpAccess.setLogin(accessConfiguration.getLogin());
        tmpAccess.setPassword(accessConfiguration.getPassword());
        return tmpAccess;
    }

//    public static AccessConfiguration getWorkAccessConfiguration(AccessConfiguration accessConfiguration) {
//        AccessConfiguration workSchema = accessConfiguration.clone();
//        workSchema.setSchemaName(BartConstants.WORK_SCHEMA);
//        return workSchema;
//    }

    private static String getTempDBName(AccessConfiguration accessConfiguration, String tempDBName) {
        String uri = accessConfiguration.getUri();
        if (uri.lastIndexOf("/") != -1) {
            return uri.substring(0, uri.lastIndexOf("/") + 1) + tempDBName;
        }
        return uri.substring(0, uri.lastIndexOf(":") + 1) + tempDBName;
    }

    public static IValue convertDBMSValue(Object attributeValue) {
        IValue value;
        if (attributeValue == null) {
            value = new NullValue(BartConstants.NULL_VALUE);
        } else {
            value = new ConstantValue(attributeValue);
        }
        return value;
    }

    public static String tableAliasToSQL(TableAlias tableAlias) {
        StringBuilder result = new StringBuilder();
        result.append(tableAlias.isSource() ? "source_" : "");
        result.append(tableAlias.getTableName());
        result.append(tableAlias.getAlias().equals("") ? "" : BartConstants.DELTA_TABLE_SEPARATOR + tableAlias.getAlias());
        return result.toString();
    }

    public static String attributeRefToSQL(AttributeRef attribureRef) {
        String tableAliasScript = BartDBMSUtility.tableAliasToSQL(attribureRef.getTableAlias());
        if (!tableAliasScript.isEmpty()) {
            tableAliasScript += BartConstants.DELTA_TABLE_SEPARATOR;
        }
        return tableAliasScript + attribureRef.getName();
    }

    public static String attributeRefToSQLDot(AttributeRef attributeRef) {
        StringBuilder sb = new StringBuilder();
        sb.append(BartDBMSUtility.tableAliasToSQL(attributeRef.getTableAlias())).append(".").append(attributeRef.getName());
        return sb.toString();
    }

    public static String attributeRefToAliasSQL(AttributeRef attributeRef) {
        StringBuilder sb = new StringBuilder();
        sb.append(BartDBMSUtility.tableAliasToSQL(attributeRef.getTableAlias())).append(BartConstants.DELTA_TABLE_SEPARATOR).append(attributeRef.getName());
        return sb.toString();
    }

    public static String expressionToSQL(Expression expression) {
        return expressionToSQL(expression, true);
    }

    public static String cleanRelationName(String name) {
        String clean = name;
        clean = clean.replaceAll("-", BartConstants.DELTA_TABLE_SEPARATOR);
        return clean;
    }

    public static String expressionToSQL(Expression expression, boolean useAlias) {
        if (logger.isDebugEnabled()) logger.debug("Converting expression " + expression);
        Expression expressionClone = expression.clone();
        String expressionString = expression.toString();
        List<Variable> jepVariables = expressionClone.getJepExpression().getSymbolTable().getVariables();
        List<String> variables = expressionClone.getVariables();
        if (expressionString.startsWith("isNull(") || expressionString.startsWith("isNotNull(")) {
            Variable var = jepVariables.get(0);
            String attributeName = extractAttributeNameFromVariable(var.getDescription().toString(), expressionClone, useAlias);
            var.setDescription(attributeName);
            return expressionClone.toSQLString();
        }
        if (expressionString.startsWith("\"") && expressionString.endsWith("\"") && expressionString.length() > 1) {
            return expressionString.substring(1, expressionString.length() - 1);
        }
        for (String variable : variables) {
            String attributeName = extractAttributeNameFromVariable(variable, expressionClone, useAlias);
            Variable var = expressionClone.getJepExpression().getSymbolTable().getVar(variable);
            if (var == null) {
                var = expressionClone.getJepExpression().getSymbolTable().getVar("Source." + variable);
            }
            if (var != null) {
                var.setDescription(attributeName);
            }
        }
        return expressionClone.toSQLString();
    }

    private static String extractAttributeNameFromVariable(String variable, Expression expression, boolean useAlias) {
        if (logger.isDebugEnabled()) logger.debug("Extracting attribute name for variable " + variable + " in expression " + expression);
        Variable variableExpression = expression.getJepExpression().getVar(variable);
        for (Variable var : expression.getJepExpression().getSymbolTable().getVariables()) {
            if (var.getDescription() != null && var.getDescription().toString().equals(variable)) {
                variableExpression = var;
            }
        }
        if (variableExpression == null) {
            throw new IllegalArgumentException("Unknow variable " + variable + " in expression " + expression);
//            return variable;
        }
        Object objectVariable = variableExpression.getDescription();
        if (objectVariable instanceof FormulaVariable) {
            FormulaVariable variableDescription = (FormulaVariable) variableExpression.getDescription();
            FormulaVariableOccurrence occurrence = variableDescription.getRelationalOccurrences().get(0);
            if (logger.isDebugEnabled()) logger.debug("Occurrences: " + variableDescription.getRelationalOccurrences());
            AttributeRef attributeRef = occurrence.getAttributeRef();
            String result;
            if (useAlias) {
                result = BartDBMSUtility.attributeRefToSQLDot(attributeRef);
            } else {
                result = BartDBMSUtility.attributeRefToSQL(attributeRef);
            }
            if (logger.isDebugEnabled()) logger.debug("Return " + result);
            return result;
        }
        if (objectVariable instanceof AttributeRef) {
            AttributeRef attributeRef = (AttributeRef) objectVariable;
            String result;
            if (useAlias) {
                result = BartDBMSUtility.attributeRefToSQLDot(attributeRef);
            } else {
                result = BartDBMSUtility.attributeRefToSQL(attributeRef);
            }
            if (logger.isDebugEnabled()) logger.debug("Return " + result);
            return result;
        }
        if (logger.isDebugEnabled()) logger.debug("Return " + variable);
        return variable;
    }

    private static AttributeRef extractAttributeRef(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Unable to extract attribute from empty string");
        }
        boolean source = false;
        if (value.startsWith("source_")) {
            source = true;
            value = value.substring("source_".length());
        }
        int lastIndex = value.lastIndexOf(BartConstants.DELTA_TABLE_SEPARATOR);
        if (lastIndex == -1) {
            return new AttributeRef("", value);
//            throw new IllegalArgumentException("Unable to extract attribute from string " + value);
        }
        String tableAliasSQL = value.substring(0, lastIndex);
        String attributeSQL = value.substring(lastIndex + BartConstants.DELTA_TABLE_SEPARATOR.length());
        TableAlias tableAlias;
        if (tableAliasSQL.contains(BartConstants.DELTA_TABLE_SEPARATOR)) {
            int firstIndex = tableAliasSQL.indexOf(BartConstants.DELTA_TABLE_SEPARATOR);
            String tableName = tableAliasSQL.substring(0, firstIndex);
            String alias = tableAliasSQL.substring(firstIndex + BartConstants.DELTA_TABLE_SEPARATOR.length());
            tableAlias = new TableAlias(tableName, alias);
        } else {
            tableAlias = new TableAlias(tableAliasSQL);
        }
        tableAlias.setSource(source);
        AttributeRef attributeRef = new AttributeRef(tableAlias, attributeSQL);
        return attributeRef;
    }

    public static void createSchema(AccessConfiguration accessConfiguration) {
        StringBuilder result = new StringBuilder();
        result.append("DROP SCHEMA IF EXISTS ").append(accessConfiguration.getSchemaName()).append(" CASCADE;\n");
        result.append("CREATE SCHEMA ").append(accessConfiguration.getSchemaName()).append(";\n\n");
        QueryManager.executeScript(result.toString(), accessConfiguration, true, true, false, true);
    }
    
    public static Tuple getTuple(IDatabase database, long rowId, String tableName) {
        IRunQuery queryRunner = OperatorFactory.getInstance().getQueryRunner(database);
        TableAlias tableAlias = new TableAlias(tableName);
        Scan scan = new Scan(tableAlias);
        Expression expression = new Expression("oid == \"" + rowId + "\"");
        expression.changeVariableDescription("oid", new AttributeRef(tableAlias, "oid"));
        Select select = new Select(expression);
        select.addChild(scan);
        ITupleIterator result = queryRunner.run(select, null, database);
        Tuple tuple = null;
        if (result.hasNext()) tuple = result.next();
        result.close();
        return tuple;
    }

}
