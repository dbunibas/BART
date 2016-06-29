package bart.model.errorgenerator.operator.deltadb.mainmemory;

import bart.BartConstants;
import bart.model.EGTask;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.model.database.mainmemory.datasource.DataSource;
import speedy.model.database.mainmemory.datasource.INode;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.model.database.mainmemory.datasource.OID;
import speedy.model.database.mainmemory.datasource.nodes.AttributeNode;
import speedy.model.database.mainmemory.datasource.nodes.LeafNode;
import speedy.model.database.mainmemory.datasource.nodes.SetNode;
import speedy.model.database.mainmemory.datasource.nodes.TupleNode;
import bart.model.dependency.Dependency;
import bart.model.errorgenerator.operator.deltadb.IBuildDeltaDB;
import bart.persistence.PersistenceConstants;
import bart.persistence.Types;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import bart.utility.ErrorGeneratorStats;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildMainMemoryDeltaDB implements IBuildDeltaDB {

    private static Logger logger = LoggerFactory.getLogger(BuildMainMemoryDeltaDB.class);

    @Override
    public MainMemoryDB generate(IDatabase database, EGTask task, String rootName) {
        long start = new Date().getTime();
        List<AttributeRef> affectedAttributes = findAllAffectedAttributes(task);
//        List<AttributeRef> nonAffectedAttributes = findNonAffectedAttributes(scenario, affectedAttributes);
        INode schemaNode = new TupleNode(PersistenceConstants.DATASOURCE_ROOT_LABEL, IntegerOIDGenerator.getNextOID());
        schemaNode.setRoot(true);
        generateSchema(schemaNode, (MainMemoryDB) database, affectedAttributes);
        DataSource deltaDataSource = new DataSource(PersistenceConstants.TYPE_META_INSTANCE, schemaNode);
        MainMemoryDB deltaDB = new MainMemoryDB(deltaDataSource);
        generateInstance(deltaDB, (MainMemoryDB) database, rootName, affectedAttributes);
        if (logger.isDebugEnabled()) logger.debug("Delta DB:\n" + deltaDB.toString());
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

    private void generateSchema(INode schemaNode, MainMemoryDB database, List<AttributeRef> affectedAttributes) {
        for (String tableName : database.getTableNames()) {
            ITable table = database.getTable(tableName);
            List<Attribute> tableNonAffectedAttributes = new ArrayList<Attribute>();
            for (Attribute attribute : table.getAttributes()) {
                if (affectedAttributes.contains(new AttributeRef(table.getName(), attribute.getName()))) {
                    String deltaRelationName = BartUtility.getDeltaRelationName(table.getName(), attribute.getName());
                    INode setNodeSchema = new SetNode(deltaRelationName);
                    schemaNode.addChild(setNodeSchema);
                    TupleNode tupleNodeSchema = new TupleNode(deltaRelationName + "Tuple");
                    setNodeSchema.addChild(tupleNodeSchema);
                    tupleNodeSchema.addChild(createAttributeSchema(BartConstants.STEP));
                    tupleNodeSchema.addChild(createAttributeSchema(BartConstants.TID));
                    tupleNodeSchema.addChild(createAttributeSchema(attribute.getName()));
                    tupleNodeSchema.addChild(createAttributeSchema(BartConstants.GROUP_ID));
                } else {
                    tableNonAffectedAttributes.add(attribute);
                }
            }
            if (!tableNonAffectedAttributes.isEmpty()) {
                createTableForNonAffected(schemaNode, table.getName(), tableNonAffectedAttributes);
            }
        }
        createOccurrenceTables(schemaNode);
    }

    private void createTableForNonAffected(INode schemaNode, String tableName, List<Attribute> tableNonAffectedAttributes) {
        String deltaRelationName = tableName + BartConstants.NA_TABLE_SUFFIX;
        INode setNodeSchema = new SetNode(deltaRelationName);
        schemaNode.addChild(setNodeSchema);
        TupleNode tupleNodeSchema = new TupleNode(deltaRelationName + "Tuple");
        setNodeSchema.addChild(tupleNodeSchema);
        tupleNodeSchema.addChild(createAttributeSchema(BartConstants.TID));
        for (Attribute attribute : tableNonAffectedAttributes) {
            tupleNodeSchema.addChild(createAttributeSchema(attribute.getName()));
        }
    }

    private void createOccurrenceTables(INode schemaNode) {
        INode occurrenceSet = new SetNode(BartConstants.OCCURRENCE_TABLE);
        TupleNode occurrenceTuple = new TupleNode(BartConstants.OCCURRENCE_TABLE + "Tuple");
        occurrenceSet.addChild(occurrenceTuple);
        occurrenceTuple.addChild(createAttributeSchema(BartConstants.STEP));
        occurrenceTuple.addChild(createAttributeSchema(BartConstants.GROUP_ID));
        occurrenceTuple.addChild(createAttributeSchema(BartConstants.CELL_OID));
        occurrenceTuple.addChild(createAttributeSchema(BartConstants.CELL_TABLE));
        occurrenceTuple.addChild(createAttributeSchema(BartConstants.CELL_ATTRIBUTE));
        schemaNode.addChild(occurrenceSet);
        INode provenanceSet = new SetNode(BartConstants.PROVENANCE_TABLE);
        TupleNode provenanceTuple = new TupleNode(BartConstants.PROVENANCE_TABLE + "Tuple");
        provenanceSet.addChild(provenanceTuple);
        provenanceTuple.addChild(createAttributeSchema(BartConstants.STEP));
        provenanceTuple.addChild(createAttributeSchema(BartConstants.GROUP_ID));
        provenanceTuple.addChild(createAttributeSchema(BartConstants.CELL_OID));
        provenanceTuple.addChild(createAttributeSchema(BartConstants.CELL_TABLE));
        provenanceTuple.addChild(createAttributeSchema(BartConstants.CELL_ATTRIBUTE));
        provenanceTuple.addChild(createAttributeSchema(BartConstants.PROVENANCE_CELL_VALUE));
        schemaNode.addChild(provenanceSet);
    }

    private AttributeNode createAttributeSchema(String attributeName) {
        AttributeNode attributeNodeInstance = new AttributeNode(attributeName);
        LeafNode leafNodeInstance = new LeafNode(Types.STRING);
        attributeNodeInstance.addChild(leafNodeInstance);
        return attributeNodeInstance;
    }

    private void generateInstance(MainMemoryDB deltaDB, MainMemoryDB database, String rootName, List<AttributeRef> affectedAttributes) {
        DataSource dataSource = deltaDB.getDataSource();
        INode instanceNode = new TupleNode(PersistenceConstants.DATASOURCE_ROOT_LABEL, IntegerOIDGenerator.getNextOID());
        instanceNode.setRoot(true);
        initOccurrenceTables(instanceNode);
        insertTargetTablesIntoDeltaDB(database, instanceNode, affectedAttributes, rootName);
        dataSource.addInstanceWithCheck(instanceNode);
    }

    private void insertTargetTablesIntoDeltaDB(MainMemoryDB database, INode instanceNode, List<AttributeRef> affectedAttributes, String rootName) {
        for (String tableName : database.getTableNames()) {
            ITable table = database.getTable(tableName);
            initInstanceNode(table, instanceNode, affectedAttributes);
            ITupleIterator it = table.getTupleIterator();
            while (it.hasNext()) {
                Tuple tuple = it.next();
                TupleOID tupleOID = tuple.getOid();
                List<Cell> nonAffectedCells = new ArrayList<Cell>();
                for (Cell cell : tuple.getCells()) {
                    if (cell.getAttribute().equals(BartConstants.OID)) {
                        continue;
                    }
                    if (affectedAttributes.contains(cell.getAttributeRef())) {
                        String deltaRelationName = BartUtility.getDeltaRelationName(table.getName(), cell.getAttribute());
                        INode setNodeInstance = getSetNodeInstance(deltaRelationName, instanceNode);
//                        if (setNodeInstance == null) {
//                            setNodeInstance = new SetNode(deltaRelationName, IntegerOIDGenerator.getNextOID());
//                            instanceNode.addChild(setNodeInstance);
//                        }
                        OID oid = IntegerOIDGenerator.getNextOID();
                        TupleNode tupleNodeInstance = new TupleNode(deltaRelationName + "Tuple", oid);
                        tupleNodeInstance.addChild(createAttributeInstance(BartConstants.TID, tupleOID));
                        tupleNodeInstance.addChild(createAttributeInstance(BartConstants.STEP, rootName));
                        IValue value = cell.getValue();
                        tupleNodeInstance.addChild(createAttributeInstance(cell.getAttribute(), value));
                        if (value instanceof NullValue && ((NullValue) value).isLabeledNull()) {
                            CellRef cellRef = new CellRef(tupleOID, new AttributeRef(table.getName(), cell.getAttribute()));
                            addTupleForNullOccurrence(value, cellRef, instanceNode);
                        }
                        setNodeInstance.addChild(tupleNodeInstance);
                    } else {
                        nonAffectedCells.add(cell);
                    }
                }
                if (!nonAffectedCells.isEmpty()) {
                    createTupleForNonAffectedCells(instanceNode, table.getName(), tupleOID, nonAffectedCells);
                }
            }
            it.close();
        }
    }

    private void initInstanceNode(ITable table, INode instanceNode, List<AttributeRef> affectedAttributes) {
        for (Attribute attribute : table.getAttributes()) {
            if (attribute.getName().equals(BartConstants.OID)) {
                continue;
            }
            if (affectedAttributes.contains(new AttributeRef(attribute.getTableName(), attribute.getName()))) {
                String deltaRelationName = BartUtility.getDeltaRelationName(table.getName(), attribute.getName());
                INode setNodeInstance = new SetNode(deltaRelationName, IntegerOIDGenerator.getNextOID());
                instanceNode.addChild(setNodeInstance);
            }
        }
    }

    private void initOccurrenceTables(INode instanceNode) {
        instanceNode.addChild(new SetNode(BartConstants.OCCURRENCE_TABLE, IntegerOIDGenerator.getNextOID()));
        instanceNode.addChild(new SetNode(BartConstants.PROVENANCE_TABLE, IntegerOIDGenerator.getNextOID()));
    }

    private AttributeNode createAttributeInstance(String attributeName, Object value) {
        AttributeNode attributeNodeInstance = new AttributeNode(attributeName, IntegerOIDGenerator.getNextOID());
        LeafNode leafNodeInstance = new LeafNode(Types.STRING, value);
        attributeNodeInstance.addChild(leafNodeInstance);
        return attributeNodeInstance;
    }

    private INode getSetNodeInstance(String deltaRelationName, INode instanceNode) {
        for (INode node : instanceNode.getChildren()) {
            if (node.getLabel().equals(deltaRelationName)) {
                return node;
            }
        }
        return null;
    }

    private void createTupleForNonAffectedCells(INode instanceNode, String tableName, TupleOID tupleOID, List<Cell> nonAffectedCells) {
        String deltaRelationName = tableName + BartConstants.NA_TABLE_SUFFIX;
        INode setNodeInstance = getSetNodeInstance(deltaRelationName, instanceNode);
        if (setNodeInstance == null) {
            setNodeInstance = new SetNode(deltaRelationName, IntegerOIDGenerator.getNextOID());
            instanceNode.addChild(setNodeInstance);
        }
        OID oid = IntegerOIDGenerator.getNextOID();
        TupleNode tupleNodeInstance = new TupleNode(deltaRelationName + "Tuple", oid);
        tupleNodeInstance.addChild(createAttributeInstance(BartConstants.TID, tupleOID));
        for (Cell cell : nonAffectedCells) {
            tupleNodeInstance.addChild(createAttributeInstance(cell.getAttribute(), cell.getValue()));
        }
        setNodeInstance.addChild(tupleNodeInstance);
    }

    private void addTupleForNullOccurrence(IValue value, CellRef cellRef, INode instanceNode) {
        INode nullInsertSet = getSetNodeInstance(BartConstants.OCCURRENCE_TABLE, instanceNode);
        TupleNode nullInsertTuple = new TupleNode(BartConstants.OCCURRENCE_TABLE + "Tuple", IntegerOIDGenerator.getNextOID());
        nullInsertSet.addChild(nullInsertTuple);
        nullInsertTuple.addChild(createAttributeInstance(BartConstants.GROUP_ID, value));
        nullInsertTuple.addChild(createAttributeInstance(BartConstants.STEP, BartConstants.CHASE_STEP_ROOT));
        nullInsertTuple.addChild(createAttributeInstance(BartConstants.CELL_OID, cellRef.getTupleOID()));
        nullInsertTuple.addChild(createAttributeInstance(BartConstants.CELL_TABLE, cellRef.getAttributeRef().getTableName()));
        nullInsertTuple.addChild(createAttributeInstance(BartConstants.CELL_ATTRIBUTE, cellRef.getAttributeRef().getName()));
    }

}
