package bart.model.errorgenerator.operator.deltadb.mainmemory;

import bart.BartConstants;
import speedy.model.algebra.Distinct;
import speedy.model.algebra.GroupBy;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Join;
import speedy.model.algebra.Project;
import speedy.model.algebra.RestoreOIDs;
import speedy.model.algebra.Scan;
import speedy.model.algebra.Select;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.TableAlias;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.model.database.mainmemory.MainMemoryVirtualDB;
import speedy.model.database.mainmemory.MainMemoryVirtualTable;
import bart.model.dependency.Dependency;
import bart.model.errorgenerator.operator.deltadb.IBuildDatabaseForChaseStep;
import speedy.model.expressions.Expression;
import bart.utility.BartUtility;
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
import speedy.model.algebra.aggregatefunctions.IAggregateFunction;
import speedy.model.algebra.aggregatefunctions.MaxAggregateFunction;
import speedy.model.algebra.aggregatefunctions.ValueAggregateFunction;
import speedy.utility.SpeedyUtility;

public class BuildMainMemoryDBForChaseStep implements IBuildDatabaseForChaseStep {

    private static Logger logger = LoggerFactory.getLogger(BuildMainMemoryDBForChaseStep.class);

    @Override
    public IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB) {
        return extractDatabase(stepId, deltaDB, originalDB, false);
    }

    @Override
    public IDatabase extractDatabaseWithDistinct(String stepId, IDatabase deltaDB, IDatabase originalDB) {
        return extractDatabase(stepId, deltaDB, originalDB, true);
    }

    public IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB, boolean distinct) {
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Extracting database for step " + stepId + " on deltaDB:\n" + deltaDB.printInstances());
        Map<String, List<AttributeRef>> attributeMap = new HashMap<String, List<AttributeRef>>();
        for (String tableName : originalDB.getTableNames()) {
            attributeMap.put(tableName, buildAttributeRefs(originalDB.getTable(tableName)));
        }
        IDatabase result = extractDatabase(stepId, deltaDB, originalDB, attributeMap, distinct);
        if (logger.isDebugEnabled()) logger.debug("Extracted database\n" + result);
        long end = new Date().getTime();
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.DELTA_DB_STEP_BUILDER, end - start);
        return result;
    }

    public IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB, Dependency dependency) {
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Extracting database for step " + stepId + " and dependency:\n" + dependency + "\non deltaDB:\n" + deltaDB.printInstances());
        Map<String, List<AttributeRef>> attributeMap = new HashMap<String, List<AttributeRef>>();
        Set<AttributeRef> requestedAttributesForDependency = DependencyUtility.findRelevantAttributes(dependency.getPremise());
        if (requestedAttributesForDependency.isEmpty()) {
            throw new IllegalArgumentException("Unable to find relevant attributes for dependency " + dependency);
        }
        if (logger.isDebugEnabled()) logger.debug("Requested attributes for dependency: " + requestedAttributesForDependency);
        for (AttributeRef attribute : requestedAttributesForDependency) {
            List<AttributeRef> attributesForTable = attributeMap.get(attribute.getTableName());
            if (attributesForTable == null) {
                attributesForTable = new ArrayList<AttributeRef>();
                attributeMap.put(attribute.getTableName(), attributesForTable);
            }
            attributesForTable.add(attribute);
        }
        if (logger.isDebugEnabled()) logger.debug("Attribute map: " + BartUtility.printMap(attributeMap));
        IDatabase result = extractDatabase(stepId, deltaDB, originalDB, attributeMap, false);
        if (logger.isDebugEnabled()) logger.debug("Extracted database for dependency\n" + dependency + "\n" + result);
        long end = new Date().getTime();
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.DELTA_DB_STEP_BUILDER, end - start);
        return result;
    }

    private IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB, Map<String, List<AttributeRef>> tablesAndAttributesToExtract, boolean distinct) {
        Set<String> tableNames = tablesAndAttributesToExtract.keySet();
        List<MainMemoryVirtualTable> virtualTables = new ArrayList<MainMemoryVirtualTable>();
        for (String tableName : tableNames) {
            ITable table = originalDB.getTable(tableName);
            List<AttributeRef> affectedAttributes = new ArrayList<AttributeRef>(tablesAndAttributesToExtract.get(tableName));
            List<AttributeRef> nonAffectedAttributes = new ArrayList<AttributeRef>();
            List<AttributeRef> deltaTableAttributes = new ArrayList<AttributeRef>();
            IAlgebraOperator initialTable = generateInitialTable(tableName, affectedAttributes, nonAffectedAttributes, deltaDB, deltaTableAttributes, stepId);
            AttributeRef oidAttributeRef = findOidAttribute(initialTable, deltaDB);
            if (affectedAttributes.isEmpty()) {
                IAlgebraOperator projection = new Project(SpeedyUtility.createProjectionAttributes(deltaTableAttributes), cleanNames(deltaTableAttributes), true);
                projection.addChild(initialTable);
                if (distinct) {
                    projection = createDistinctTable(projection, table.getAttributes());
                }
                MainMemoryVirtualTable firstTable = new MainMemoryVirtualTable(tableName, projection, (MainMemoryDB) deltaDB, (MainMemoryDB) originalDB);
                virtualTables.add(firstTable);
                if (logger.isDebugEnabled()) logger.debug("Initial table for " + tableName + "\n" + projection);
            } else {
                IAlgebraOperator algebraRoot = buildAlgebraTreeForTable(table, affectedAttributes, nonAffectedAttributes, stepId, oidAttributeRef, initialTable, deltaTableAttributes);
                if (distinct) {
                    algebraRoot = createDistinctTable(algebraRoot, table.getAttributes());
                }
                MainMemoryVirtualTable virtualTable = new MainMemoryVirtualTable(tableName, algebraRoot, (MainMemoryDB) deltaDB, (MainMemoryDB) originalDB);
                virtualTables.add(virtualTable);
            }
            ////////
            MainMemoryVirtualTable lastTable = virtualTables.get(virtualTables.size() - 1);
            IAlgebraOperator queryRoot = lastTable.getQuery();
            RestoreOIDs restore = new RestoreOIDs(new AttributeRef(table.getName(), BartConstants.OID));
            restore.addChild(queryRoot);
            lastTable.setQuery(restore);
            if (logger.isDebugEnabled()) logger.debug("Algebra tree for table: " + table.getName() + "\n" + restore);
            ////////
        }
        IDatabase result = new MainMemoryVirtualDB((MainMemoryDB) deltaDB, ((MainMemoryDB) originalDB).getDataSource(), virtualTables);
        if (logger.isDebugEnabled()) logger.debug("Building database for step : " + stepId + " over\n" + deltaDB.printInstances());
        return result;
    }

    private IAlgebraOperator createDistinctTable(IAlgebraOperator algebraRoot, List<Attribute> originalAttributes) {
        List<AttributeRef> attributes = new ArrayList<AttributeRef>();
        for (Attribute attribute : originalAttributes) {
            if (BartConstants.OID.equals(attribute.getName()) || BartConstants.TID.equals(attribute.getName())) {
                continue;
            }
            attributes.add(new AttributeRef(attribute.getTableName(), attribute.getName()));
        }
        Project project = new Project(SpeedyUtility.createProjectionAttributes(attributes));
        project.addChild(algebraRoot);
        Distinct distinct = new Distinct();
        distinct.addChild(project);
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
            initialTable = buildTreeForAttribute(firstAttributeRef, stepId);
        }
        return initialTable;
    }

    private IAlgebraOperator buildTreeForAttribute(AttributeRef attribute, String stepId) {
        // select * from R_A where step
        TableAlias table = new TableAlias(BartUtility.getDeltaRelationName(attribute.getTableName(), attribute.getName()));
        Scan tableScan = new Scan(table);
        Expression stepExpression = new Expression("startswith(\"" + stepId + "\", " + BartConstants.STEP + ")");
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
        if (logger.isDebugEnabled()) logger.debug("Algebra tree for attribute: " + attribute + "\n" + project);
        return project;
    }

    private IAlgebraOperator buildAlgebraTreeForTable(ITable table, List<AttributeRef> affectedAttributes, List<AttributeRef> nonAffectedAttributes, String stepId, AttributeRef oidAttributeRef, IAlgebraOperator deltaForFirstAttribute, List<AttributeRef> deltaTableAttributes) {
        IAlgebraOperator leftChild = deltaForFirstAttribute;
        for (AttributeRef attributeRef : affectedAttributes) {
            deltaTableAttributes.add(new AttributeRef(new TableAlias(BartUtility.getDeltaRelationName(table.getName(), attributeRef.getName()), "0"), attributeRef.getName()));
            IAlgebraOperator deltaForAttribute = buildTreeForAttribute(attributeRef, stepId);
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
//        sortAttributes(deltaTableAttributes, table.getAttributes());
//        sortAttributes(projectionAttributes, table.getAttributes());
        Project project = new Project(SpeedyUtility.createProjectionAttributes(deltaTableAttributes), projectionAttributes, true);
        project.addChild(leftChild);
        IAlgebraOperator restore = new RestoreOIDs(new AttributeRef(table.getName(), BartConstants.OID));
        restore.addChild(project);
        if (logger.isDebugEnabled()) logger.debug("Algebra tree for table: " + table.getName() + "\n" + restore);
        return restore;
    }

    private List<AttributeRef> buildAttributeRefs(ITable table) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (Attribute attribute : table.getAttributes()) {
            result.add(new AttributeRef(table.getName(), attribute.getName()));
        }
        return result;
    }

    private List<AttributeRef> cleanNames(List<AttributeRef> nonAffectedAttributes) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (AttributeRef attributeRef : nonAffectedAttributes) {
            String tableName = attributeRef.getTableName();
//            tableName = tableName.replaceAll(BartConstants.NA_TABLE_SUFFIX, "");
            tableName = tableName.substring(0, tableName.indexOf(BartConstants.DELTA_TABLE_SEPARATOR));
            String attributeName = attributeRef.getName();
            if (attributeRef.getName().equals(BartConstants.TID)) {
                attributeName = BartConstants.OID;
            }
            result.add(new AttributeRef(tableName, attributeName));
        }
        return result;
    }

    private boolean isNotAffected(AttributeRef attributeRef, IDatabase deltaDB) {
        return !deltaDB.getTableNames().contains(BartUtility.getDeltaRelationName(attributeRef.getTableName(), attributeRef.getName()));
    }
//    private void sortAttributes(List<AttributeRef> unsortedAttributes, List<Attribute> attributes) {
//        if (unsortedAttributes.isEmpty()) {
//            return;
//        }
//        List<AttributeRef> sortedAttributes = new ArrayList<AttributeRef>();
//        sortedAttributes.add(unsortedAttributes.get(0));
//        for (Attribute attributeToSearch : attributes) {
//            for (AttributeRef attributeRef : unsortedAttributes) {
//                if (attributeRef.getName().equalsIgnoreCase(attributeToSearch.getName())) {
//                    sortedAttributes.add(attributeRef);
//                }
//            }
//        }
//        unsortedAttributes.clear();
//        unsortedAttributes.addAll(sortedAttributes);
//    }
}
