package bart.model.errorgenerator.operator.deltadb.dbms;

import bart.BartConstants;
import bart.model.EGTask;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.dbms.DBMSTable;
import bart.model.dependency.Dependency;
import bart.model.errorgenerator.OrderingAttribute;
import bart.model.errorgenerator.operator.deltadb.IBuildDeltaDB;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import bart.utility.BartUtility;
import bart.utility.BartDBMSUtility;
import bart.utility.DependencyUtility;
import bart.utility.ErrorGeneratorStats;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.utility.DBMSUtility;

public class BuildSQLDeltaDB implements IBuildDeltaDB {

    private static Logger logger = LoggerFactory.getLogger(BuildSQLDeltaDB.class);

    @Override
    public DBMSDB generate(IDatabase database, EGTask task, String rootName) {
        long start = new Date().getTime();
        String dirtySuffix = BartUtility.getDirtyCloneSuffix(task);
        AccessConfiguration accessConfiguration = ((DBMSDB) database).getAccessConfiguration().clone();
        accessConfiguration.setSchemaName(accessConfiguration.getSchemaName() + dirtySuffix);
        DBMSDB deltaDB = new DBMSDB(accessConfiguration);
        BartDBMSUtility.createSchema(accessConfiguration);
        StringBuilder script = new StringBuilder();
        List<AttributeRef> affectedAttributes = findAllAffectedAttributes(task);
        script.append(createDeltaRelationsSchema(database, accessConfiguration, affectedAttributes));
        script.append(insertIntoDeltaRelations(database, accessConfiguration, rootName, affectedAttributes));
        QueryManager.executeScript(script.toString(), accessConfiguration, true, true, true, false);
        long end = new Date().getTime();
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.DELTA_DB_BUILDER, end - start);
        return deltaDB;
    }

    private List<AttributeRef> findAllAffectedAttributes(EGTask task) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (Dependency dc : task.getDCs()) {
            Set<AttributeRef> attributes = DependencyUtility.findRelevantAttributes(dc.getPremise());
            for (AttributeRef attribute : attributes) {
                BartUtility.addIfNotContained(result, DependencyUtility.unAlias(attribute));
            }
        }
        if (task.getConfiguration().containsOrderingAttributes()) {
            Map<String, OrderingAttribute> mapOrderingAttributes = task.getConfiguration().getVioGenOrderingAttributes();
            for (String dependencyId : mapOrderingAttributes.keySet()) {
                OrderingAttribute oa = mapOrderingAttributes.get(dependencyId);
                AttributeRef attributeRef = oa.getAttributeRef();
                BartUtility.addIfNotContained(result, DependencyUtility.unAlias(attributeRef));
            }
        }
        if (task.getConfiguration().isRandomErrors()) {
            for (String table : task.getConfiguration().getTablesForRandomErrors()) {
                Set<String> attributes = task.getConfiguration().getAttributesForRandomErrors(table);
                for (String attribute : attributes) {
                    AttributeRef attributeRef = new AttributeRef(table, attribute);
                    result.add(attributeRef);
                }
            }
        }
        return result;
    }

    private String createDeltaRelationsSchema(IDatabase database, AccessConfiguration accessConfiguration, List<AttributeRef> affectedAttributes) {
        String deltaDBSchema = accessConfiguration.getSchemaName();
        StringBuilder script = new StringBuilder();
        script.append("----- Generating Delta Relations Schema -----\n");
        for (String tableName : database.getTableNames()) {
            DBMSTable table = (DBMSTable) database.getTable(tableName);
            List<Attribute> tableNonAffectedAttributes = new ArrayList<Attribute>();
            for (Attribute attribute : table.getAttributes()) {
                if (attribute.getName().equals(BartConstants.OID)) {
                    continue;
                }
                if (affectedAttributes.contains(new AttributeRef(table.getName(), attribute.getName()))) {
                    script.append(createDeltaRelationSchemaAndTrigger(deltaDBSchema, table.getName(), attribute.getName(), attribute.getType()));
                } else {
                    tableNonAffectedAttributes.add(attribute);
                }
            }
            script.append(createTableForNonAffected(deltaDBSchema, table.getName(), tableNonAffectedAttributes));
        }
        if (logger.isDebugEnabled()) logger.debug("\n----Generating Delta Relations Schema: " + script);
//        QueryManager.executeScript(script.toString(), ((DBMSDB) database).getAccessConfiguration(), true, true, true);
        return script.toString();
    }

    private String createDeltaRelationSchemaAndTrigger(String deltaDBSchema, String tableName, String attributeName, String attributeType) {
        StringBuilder script = new StringBuilder();
        String deltaRelationName = BartUtility.getDeltaRelationName(tableName, attributeName);
        script.append("CREATE TABLE ").append(deltaDBSchema).append(".").append(deltaRelationName).append("(").append("\n");
        script.append(BartConstants.INDENT).append(BartConstants.STEP).append(" text,").append("\n");
        script.append(BartConstants.INDENT).append(BartConstants.TID).append(" bigint,").append("\n");
        script.append(BartConstants.INDENT).append(attributeName).append(" ").append(DBMSUtility.convertDataSourceTypeToDBType(attributeType)).append(",").append("\n");
        script.append(BartConstants.INDENT).append(BartConstants.GROUP_ID).append(" text").append("\n");
        script.append(") WITH OIDS;").append("\n\n");
//        script.append("CREATE INDEX ").append(attributeName).append("_oid  ON ").append(deltaDBSchema).append(".").append(deltaRelationName).append(" USING btree(tid ASC);\n");
//        script.append("CREATE INDEX ").append(attributeName).append("_step  ON ").append(deltaDBSchema).append(".").append(deltaRelationName).append(" USING btree(step ASC);\n\n");
//        script.append("REINDEX TABLE ").append(deltaDBSchema).append(".").append(deltaRelationName).append(";\n");
        return script.toString();
    }

    private String createTableForNonAffected(String deltaDBSchema, String tableName, List<Attribute> tableNonAffectedAttributes) {
        String deltaRelationName = tableName + BartConstants.NA_TABLE_SUFFIX;
        StringBuilder script = new StringBuilder();
        script.append("CREATE TABLE ").append(deltaDBSchema).append(".").append(deltaRelationName).append("(").append("\n");
        script.append(BartConstants.INDENT).append(BartConstants.TID).append(" bigint,").append("\n");
//        script.append(BartConstants.INDENT).append(BartConstants.OID).append(" integer,").append("\n");
        for (Attribute attribute : tableNonAffectedAttributes) {
            script.append(BartConstants.INDENT).append(attribute.getName()).append(" ").append(DBMSUtility.convertDataSourceTypeToDBType(attribute.getType())).append(",\n");
        }
        BartUtility.removeChars(",\n".length(), script);
        script.append("\n").append(") WITH OIDS;").append("\n\n");
//        script.append("CREATE INDEX ").append(deltaRelationName).append("_oid  ON ").append(deltaDBSchema).append(".").append(deltaRelationName).append(" USING btree(tid ASC);\n");
        return script.toString();
    }

    private String insertIntoDeltaRelations(IDatabase database, AccessConfiguration accessConfiguration, String rootStepId, List<AttributeRef> affectedAttributes) {
        String originalDBSchema = ((DBMSDB) database).getAccessConfiguration().getSchemaName();
        String deltaDBSchema = accessConfiguration.getSchemaName();
        StringBuilder script = new StringBuilder();
        script.append("----- Insert into Delta Relations -----\n");
        for (String tableName : database.getTableNames()) {
            DBMSTable table = (DBMSTable) database.getTable(tableName);
            List<Attribute> tableNonAffectedAttributes = new ArrayList<Attribute>();
            for (Attribute attribute : table.getAttributes()) {
                if (attribute.getName().equals(BartConstants.OID)) {
                    continue;
                }
                if (affectedAttributes.contains(new AttributeRef(table.getName(), attribute.getName()))) {
                    script.append(insertIntoDeltaRelation(originalDBSchema, deltaDBSchema, table.getName(), attribute.getName(), rootStepId));
                } else {
                    tableNonAffectedAttributes.add(attribute);
                }
            }
            script.append(insertIntoNonAffectedRelation(originalDBSchema, deltaDBSchema, table.getName(), tableNonAffectedAttributes));
        }
        if (logger.isDebugEnabled()) logger.debug("----Insert into Delta Relations: " + script);
        return script.toString();
    }

    private String insertIntoDeltaRelation(String originalDBSchema, String deltaDBSchema, String tableName, String attributeName, String rootStepId) {
        StringBuilder script = new StringBuilder();
        String deltaRelationName = BartUtility.getDeltaRelationName(tableName, attributeName);
        script.append("INSERT INTO ").append(deltaDBSchema).append(".").append(deltaRelationName).append("\n");
        script.append("SELECT cast('").append(rootStepId).append("' AS varchar) AS step, ").append(BartConstants.OID).append(", ").append(attributeName);
        script.append("\n").append(BartConstants.INDENT);
        script.append("FROM ").append(originalDBSchema).append(".").append(tableName).append(";");
        script.append("\n");
//        script.append("ALTER TABLE ").append(deltaDBSchema).append(".").append(deltaRelationName);
//        script.append(" ADD UNIQUE (\"" + BartConstants.STEP + "\", \"" + BartConstants.TID + "\");");
//        script.append("\n\n");
        return script.toString();
    }

    private String insertIntoNonAffectedRelation(String originalDBSchema, String deltaDBSchema, String tableName, List<Attribute> tableNonAffectedAttributes) {
        String deltaRelationName = tableName + BartConstants.NA_TABLE_SUFFIX;
        StringBuilder script = new StringBuilder();
        script.append("INSERT INTO ").append(deltaDBSchema).append(".").append(deltaRelationName).append("\n");
        script.append("SELECT ").append(BartConstants.OID).append(",");
        for (Attribute attribute : tableNonAffectedAttributes) {
            script.append(attribute.getName()).append(",");
        }
        BartUtility.removeChars(",".length(), script);
        script.append("\n").append(BartConstants.INDENT);
        script.append("FROM ").append(originalDBSchema).append(".").append(tableName).append(";");
        script.append("\n");
        return script.toString();
    }

}
