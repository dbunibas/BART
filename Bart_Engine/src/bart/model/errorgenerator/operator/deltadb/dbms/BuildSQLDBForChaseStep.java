package bart.model.errorgenerator.operator.deltadb.dbms;

import bart.BartConstants;
import speedy.model.algebra.Distinct;
import speedy.model.algebra.GroupBy;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Join;
import speedy.model.algebra.Project;
import speedy.model.algebra.RestoreOIDs;
import speedy.model.algebra.Scan;
import speedy.model.algebra.Select;
import speedy.model.algebra.operators.sql.AlgebraTreeToSQL;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.TableAlias;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.dbms.DBMSVirtualDB;
import bart.model.dependency.Dependency;
import bart.model.errorgenerator.operator.deltadb.IBuildDatabaseForChaseStep;
import speedy.model.expressions.Expression;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.relational.QueryManager;
import bart.utility.BartUtility;
import bart.utility.BartDBMSUtility;
import bart.utility.DependencyUtility;
import bart.utility.ErrorGeneratorStats;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.CreateTableAs;
import speedy.model.algebra.aggregatefunctions.IAggregateFunction;
import speedy.model.algebra.aggregatefunctions.MaxAggregateFunction;
import speedy.model.algebra.aggregatefunctions.ValueAggregateFunction;
import speedy.utility.SpeedyUtility;

public class BuildSQLDBForChaseStep implements IBuildDatabaseForChaseStep {

    private static Logger logger = LoggerFactory.getLogger(BuildSQLDBForChaseStep.class);

    private AlgebraTreeToSQL sqlGenerator = new AlgebraTreeToSQL();
    private boolean useHash = true;

    @Override
    public IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB) {
        return extractDatabase(stepId, deltaDB, originalDB, false);
    }

    @Override
    public IDatabase extractDatabaseWithDistinct(String stepId, IDatabase deltaDB, IDatabase originalDB) {
        if (logger.isDebugEnabled()) logger.debug("Extracting database with distinct for step " + stepId);
        return extractDatabase(stepId, deltaDB, originalDB, true);
    }

    public IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB, boolean distinct) {
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Generating database for step " + stepId);
        //Materialize join
        Map<String, List<AttributeRef>> attributeMap = new HashMap<String, List<AttributeRef>>();
        for (String tableName : originalDB.getTableNames()) {
            attributeMap.put(tableName, buildAttributeRefs(originalDB.getTable(tableName)));
        }
        StringBuilder script = new StringBuilder();
        Map<String, String> tableViews = extractDatabase("\"" + stepId + "\"", "", deltaDB, originalDB, attributeMap, distinct);
        for (String tableName : tableViews.keySet()) {
            String viewScript = tableViews.get(tableName);
            script.append(viewScript).append("\n");
        }
        if (logger.isDebugEnabled()) logger.debug("View paramized script:\n" + script);
        QueryManager.executeScript(script.toString(), ((DBMSDB) originalDB).getAccessConfiguration(), true, true, true, false);
        AccessConfiguration accessConfiguration = ((DBMSDB) deltaDB).getAccessConfiguration();
//        String cleanStepId = stepId.replaceAll("\\.", "_");
//        if (useHash) {
//            cleanStepId = getHash(cleanStepId);
//        }
//        DBMSVirtualDB virtualDB = new DBMSVirtualDB((DBMSDB) originalDB, ((DBMSDB) deltaDB), "__" + cleanStepId, accessConfiguration);
        DBMSVirtualDB virtualDB = new DBMSVirtualDB((DBMSDB) originalDB, ((DBMSDB) deltaDB), "", accessConfiguration);
        long end = new Date().getTime();
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.DELTA_DB_STEP_BUILDER, end - start);
        return virtualDB;
    }

    public IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB, Dependency dependency) {
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Generating database for step " + stepId + " and depedency " + dependency);
        //Materialize join
        Map<String, List<AttributeRef>> attributeMap = new HashMap<String, List<AttributeRef>>();
        Set<AttributeRef> requestedAttributesForDependency = DependencyUtility.findRelevantAttributes(dependency.getPremise());
        if (requestedAttributesForDependency.isEmpty()) {
            throw new IllegalArgumentException("Unable to find relevant attributes for dependency " + dependency);
        }
        for (AttributeRef attribute : requestedAttributesForDependency) {
            List<AttributeRef> attributesForTable = attributeMap.get(attribute.getTableName());
            if (attributesForTable == null) {
                attributesForTable = new ArrayList<AttributeRef>();
                attributeMap.put(attribute.getTableName(), attributesForTable);
            }
            attributesForTable.add(attribute);
        }
        StringBuilder script = new StringBuilder();
//        script.append("ANALYZE;\n");
        Map<String, String> tableViews = extractDatabase("\"" + stepId + "\"", dependency.getId(), deltaDB, originalDB, attributeMap, false);
        for (String tableName : tableViews.keySet()) {
            String viewScript = tableViews.get(tableName);
            script.append(viewScript).append("\n");
        }
        if (logger.isDebugEnabled()) logger.debug("View paramized script:\n" + script);
        QueryManager.executeScript(script.toString(), ((DBMSDB) originalDB).getAccessConfiguration(), true, true, true, false);
        AccessConfiguration accessConfiguration = ((DBMSDB) deltaDB).getAccessConfiguration();
        String cleanStepId = stepId.replaceAll("\\.", "_");
        if (useHash) {
            cleanStepId = getHash(cleanStepId);
        }
        DBMSVirtualDB virtualDB = new DBMSVirtualDB((DBMSDB) originalDB, ((DBMSDB) deltaDB), "_" + dependency.getId() + "_" + cleanStepId, accessConfiguration);
        long end = new Date().getTime();
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.DELTA_DB_STEP_BUILDER, end - start);
        return virtualDB;
    }

    private Map<String, String> extractDatabase(String stepId, String dependencyId, IDatabase deltaDB, IDatabase originalDB, Map<String, List<AttributeRef>> tablesAndAttributesToExtract,
            boolean distinct) {
        Set<String> tableNames = tablesAndAttributesToExtract.keySet();
        Map<String, String> tableViews = new HashMap<String, String>();
        for (String tableName : tableNames) {
            ITable table = originalDB.getTable(tableName);
            List<AttributeRef> affectedAttributes = new ArrayList<AttributeRef>(tablesAndAttributesToExtract.get(tableName));
            List<AttributeRef> nonAffectedAttributes = new ArrayList<AttributeRef>();
            List<AttributeRef> deltaTableAttributes = new ArrayList<AttributeRef>();
            IAlgebraOperator initialTable = generateInitialTable(tableName, affectedAttributes, nonAffectedAttributes, deltaDB, deltaTableAttributes, stepId);
            AttributeRef oidAttributeRef = findOidAttribute(initialTable, deltaDB);
            IAlgebraOperator algebraRoot;
            if (affectedAttributes.isEmpty()) {
                if (distinct) {
                    removeOIDAttribute(deltaTableAttributes);
                }
                IAlgebraOperator projection = new Project(SpeedyUtility.createProjectionAttributes(deltaTableAttributes), cleanNames(deltaTableAttributes), true);
                projection.addChild(initialTable);
                algebraRoot = projection;
            } else {
                algebraRoot = buildAlgebraTreeForTable(deltaDB, table, affectedAttributes, nonAffectedAttributes, stepId, oidAttributeRef, initialTable, deltaTableAttributes, distinct);
            }
            if (distinct) {
                algebraRoot = createDistinctTable(algebraRoot);
            }
            IAlgebraOperator resultOperator;
            String materializedTableName;
            if (dependencyId == null || dependencyId.isEmpty()) {
                materializedTableName = tableName;
            } else {
                String cleanStepId = stepId.replaceAll("\\.", "_");
                cleanStepId = cleanStepId.replaceAll("\"", "");
                if (useHash) {
                    cleanStepId = getHash(cleanStepId);
                }
                materializedTableName = tableName + "_" + dependencyId + "_" + cleanStepId;
            }
            CreateTableAs createTable = new CreateTableAs(materializedTableName, materializedTableName, ((DBMSDB) deltaDB).getAccessConfiguration().getSchemaName(), distinct, true);
            createTable.addChild(algebraRoot);
            resultOperator = createTable;
            if (logger.isDebugEnabled()) logger.debug("Algebra for extract database: \n" + resultOperator);
            String query = sqlGenerator.treeToSQL(resultOperator, null, deltaDB, "");
            if (logger.isDebugEnabled()) logger.debug("Script for extract database: \n" + query);
            tableViews.put(tableName, query);
        }
        return tableViews;
    }

    private IAlgebraOperator createDistinctTable(IAlgebraOperator algebraRoot) {
        Distinct distinct = new Distinct();
        distinct.addChild(algebraRoot);
        return distinct;
    }

    private AttributeRef findOidAttribute(IAlgebraOperator initialTable, IDatabase deltaDB) {
        for (AttributeRef attribute : initialTable.getAttributes(null, deltaDB)) {
            if (attribute.getName().equals(BartConstants.TID)) {
                return attribute;
            }
        }
        throw new IllegalArgumentException("Unable to find oid attribute in " + initialTable);
    }

    private IAlgebraOperator generateInitialTable(String tableName, List<AttributeRef> affectedAttributes, List<AttributeRef> nonAffectedAttributes, IDatabase deltaDB, List<AttributeRef> deltaTableAttributes, String stepId) {
        for (Iterator<AttributeRef> it = affectedAttributes.iterator(); it.hasNext();) {
            AttributeRef attributeRef = it.next();
            if (isNotAffected(attributeRef, deltaDB)) {
                it.remove();
                nonAffectedAttributes.add(attributeRef);
            }
        }
        IAlgebraOperator initialTable;
        if (!nonAffectedAttributes.isEmpty()) {
            String tableNameForNonAffected = tableName + BartConstants.NA_TABLE_SUFFIX;
            Scan scan = new Scan(new TableAlias(tableNameForNonAffected));
            initialTable = scan;
            deltaTableAttributes.add(new AttributeRef(tableNameForNonAffected, BartConstants.TID));
            for (AttributeRef attributeRef : nonAffectedAttributes) {
                deltaTableAttributes.add(new AttributeRef(tableNameForNonAffected, attributeRef.getName()));
            }
        } else {
            AttributeRef firstAttributeRef = affectedAttributes.remove(0);
            nonAffectedAttributes.add(firstAttributeRef);
            deltaTableAttributes.add(new AttributeRef(BartUtility.getDeltaRelationName(tableName, firstAttributeRef.getName()), BartConstants.TID));
            deltaTableAttributes.add(new AttributeRef(new TableAlias(BartUtility.getDeltaRelationName(tableName, firstAttributeRef.getName()), "0"), firstAttributeRef.getName()));
            initialTable = buildTreeForAttribute(deltaDB, firstAttributeRef, stepId);
        }
        return initialTable;
    }

    private IAlgebraOperator buildTreeForAttribute(IDatabase deltaDB, AttributeRef attribute, String stepId) {
        // select * from R_A where step
        TableAlias table = new TableAlias(BartUtility.getDeltaRelationName(attribute.getTableName(), attribute.getName()));
        Scan tableScan = new Scan(table);
        Expression stepExpression = new Expression("startswith(" + stepId + ", " + BartConstants.STEP + ")");
        stepExpression.changeVariableDescription(BartConstants.STEP, new AttributeRef(table, BartConstants.STEP));
        Select stepSelect = new Select(stepExpression);
        stepSelect.addChild(tableScan);
        // select max(step), oid from R_A group by oid
        AttributeRef oid = new AttributeRef(table, BartConstants.TID);
        AttributeRef step = new AttributeRef(table, BartConstants.STEP);
        List<AttributeRef> groupingAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oid}));
        IAggregateFunction max = new MaxAggregateFunction(step);
        IAggregateFunction oidValue = new ValueAggregateFunction(oid);
        List<IAggregateFunction> aggregateFunctions = new ArrayList<IAggregateFunction>(Arrays.asList(new IAggregateFunction[]{max, oidValue}));
        GroupBy groupBy = new GroupBy(groupingAttributes, aggregateFunctions);
        groupBy.addChild(stepSelect);
        // select * from R_A_1
        TableAlias alias = new TableAlias(table.getTableName(), "0");
        Scan aliasScan = new Scan(alias);
        // select * from (group-by) join R_A_1 on oid, step
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oid, step}));
        AttributeRef oidInAlias = new AttributeRef(alias, BartConstants.TID);
        AttributeRef stepInAlias = new AttributeRef(alias, BartConstants.STEP);
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oidInAlias, stepInAlias}));
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(groupBy);
        join.addChild(aliasScan);
        // select oid, A from (join)
        AttributeRef attributeInAlias = new AttributeRef(alias, attribute.getName());
        List<AttributeRef> projectionAttributes = new ArrayList<AttributeRef>(Arrays.asList(new AttributeRef[]{oid, attributeInAlias}));
        Project project = new Project(SpeedyUtility.createProjectionAttributes(projectionAttributes));
        project.addChild(join);
        String cleanStepId = stepId.replaceAll("\\.", "_");
        cleanStepId = cleanStepId.replaceAll("\"", "");
        if (useHash) {
            cleanStepId = getHash(cleanStepId);
        }
        IAlgebraOperator resultOperator;
        String tableName = BartConstants.DELTA_TMP_TABLES + BartDBMSUtility.attributeRefToAliasSQL(attribute) + "_" + cleanStepId;
        String tableAlias = BartDBMSUtility.attributeRefToAliasSQL(attribute);
        CreateTableAs createTable = new CreateTableAs(tableName, tableAlias, ((DBMSDB) deltaDB).getAccessConfiguration().getSchemaName(), false, true);
        createTable.addChild(project);
        resultOperator = createTable;
        if (logger.isDebugEnabled()) logger.debug("Algebra tree for attribute: " + attribute + "\n" + resultOperator);
        return resultOperator;
    }

    private IAlgebraOperator buildAlgebraTreeForTable(IDatabase deltaDB, ITable table, List<AttributeRef> affectedAttributes, List<AttributeRef> nonAffectedAttributes, String stepId, AttributeRef oidAttributeRef, IAlgebraOperator deltaForFirstAttribute, List<AttributeRef> deltaTableAttributes, boolean distinct) {
        IAlgebraOperator leftChild = deltaForFirstAttribute;
        for (AttributeRef attributeRef : affectedAttributes) {
            deltaTableAttributes.add(new AttributeRef(new TableAlias(BartUtility.getDeltaRelationName(table.getName(), attributeRef.getName()), "0"), attributeRef.getName()));
            IAlgebraOperator deltaForAttribute = buildTreeForAttribute(deltaDB, attributeRef, stepId);
            AttributeRef oid = new AttributeRef(BartUtility.getDeltaRelationName(table.getName(), attributeRef.getName()), BartConstants.TID);
            Join join = new Join(oidAttributeRef, oid);
            join.addChild(leftChild);
            join.addChild(deltaForAttribute);
            leftChild = join;
        }
        List<AttributeRef> projectionAttributes = new ArrayList<AttributeRef>();
        projectionAttributes.add(new AttributeRef(table.getName(), BartConstants.OID));
        projectionAttributes.addAll(nonAffectedAttributes);
        projectionAttributes.addAll(affectedAttributes);
        sortAttributes(deltaTableAttributes, table.getAttributes());
        sortAttributes(projectionAttributes, table.getAttributes());
        if (distinct) {
            removeOIDAttribute(deltaTableAttributes);
            removeOIDAttribute(projectionAttributes);
        }
        Project project = new Project(SpeedyUtility.createProjectionAttributes(deltaTableAttributes), projectionAttributes, true);
        project.addChild(leftChild);
        RestoreOIDs restore = new RestoreOIDs(new AttributeRef(table.getName(), BartConstants.OID));
        restore.addChild(project);
        if (logger.isDebugEnabled()) logger.debug("Algebra tree for table: " + table.getName() + "\n" + restore);
        return restore;
    }

    private List<AttributeRef> buildAttributeRefs(ITable table) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (Attribute attribute : getAttributes(table)) {
            result.add(new AttributeRef(table.getName(), attribute.getName()));
        }
        return result;
    }

    private List<AttributeRef> cleanNames(List<AttributeRef> nonAffectedAttributes) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (AttributeRef attributeRef : nonAffectedAttributes) {
            String tableName = attributeRef.getTableName();
            tableName = tableName.replaceAll(BartConstants.NA_TABLE_SUFFIX, "");
            String attributeName = attributeRef.getName();
            if (attributeName.equals(BartConstants.TID)) {
                attributeName = BartConstants.OID;
            }
            result.add(new AttributeRef(tableName, attributeName));
        }
        return result;
    }

    private boolean isNotAffected(AttributeRef attributeRef, IDatabase deltaDB) {
        List<String> tableNames = deltaDB.getTableNames();
        String deltaRelation = BartUtility.getDeltaRelationName(attributeRef.getTableName(), attributeRef.getName());
        for (String tableName : tableNames) {
            if (tableName.equalsIgnoreCase(deltaRelation)) {
                return false;
            }
        }
        return true;
    }

    private List<Attribute> getAttributes(ITable table) {
        List<Attribute> result = new ArrayList<Attribute>();
        for (Attribute attribute : table.getAttributes()) {
            if (attribute.getName().equals(BartConstants.OID)) {
                continue;
            }
            result.add(attribute);
        }
        return result;
    }

    private String getHash(String cleanStepId) {
        return Math.abs(cleanStepId.hashCode()) + "";
    }

    private void sortAttributes(List<AttributeRef> unsortedAttributes, List<Attribute> attributes) {
        if (unsortedAttributes.isEmpty()) {
            return;
        }
        List<AttributeRef> sortedAttributes = new ArrayList<AttributeRef>();
        sortedAttributes.add(unsortedAttributes.remove(0));
        for (Attribute attributeToSearch : attributes) {
            for (AttributeRef attributeRef : unsortedAttributes) {
                if (attributeRef.getName().equalsIgnoreCase(attributeToSearch.getName())) {
                    sortedAttributes.add(attributeRef);
                }
            }
        }
        unsortedAttributes.clear();
        unsortedAttributes.addAll(sortedAttributes);
    }

    private void removeOIDAttribute(List<AttributeRef> attributes) {
        for (Iterator<AttributeRef> it = attributes.iterator(); it.hasNext();) {
            AttributeRef attributeRef = it.next();
            if (attributeRef.getName().equals(BartConstants.OID)
                    || attributeRef.getName().equals(BartConstants.TID)) {
                it.remove();
            }
        }
    }

}
