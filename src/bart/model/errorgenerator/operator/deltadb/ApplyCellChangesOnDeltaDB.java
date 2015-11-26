package bart.model.errorgenerator.operator.deltadb;

import bart.model.errorgenerator.operator.IChangeApplier;
import bart.BartConstants;
import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.model.database.operators.IDatabaseManager;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ICellChange;
import bart.utility.BartUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.dbms.DBMSDB;

public class ApplyCellChangesOnDeltaDB implements IInitializableOperator, IChangeApplier {

    private static Logger logger = LoggerFactory.getLogger(ApplyCellChangesOnDeltaDB.class);
    private IBuildDeltaDB deltaBuilder;
    private IBuildDatabaseForChaseStep databaseBuilder;
    private IInsertTuple insertOperator;
    private IDatabaseManager databaseManager;

    @Override
    public void apply(CellChanges cellChanges, EGTask task) {
        intitializeOperators(task);
        String dirtySuffix = BartUtility.getDirtyCloneSuffix(task);
        //TODO handle task.getConfiguration().isCloneTargetSchema()
        try {
            DBMSDB target = (DBMSDB) task.getTarget();
            databaseManager.removeClone(target, dirtySuffix);
        } catch (Exception e) {
        }
        IDatabase deltaDB = deltaBuilder.generate(task.getTarget(), task, BartConstants.CHASE_STEP_ROOT);
        String dirtyStepID = BartConstants.CHASE_STEP_ROOT + "_d";
        applyChangesOnDeltaDB(cellChanges, deltaDB, dirtyStepID, task);
        IDatabase database = databaseBuilder.extractDatabase(dirtyStepID, deltaDB, task.getTarget());
        removeDeltaDBTables(deltaDB);
        task.setDirtyTarget(database);
    }

    private void applyChangesOnDeltaDB(CellChanges cellChanges, IDatabase deltaDB, String dirtyStepID, EGTask task) {
        for (ICellChange change : cellChanges.getChanges()) {
            if (logger.isInfoEnabled()) logger.info("Applying change " + change);
            String tableName = change.getCell().getAttributeRef().getTableName();
            String attributeName = change.getCell().getAttributeRef().getName();
            TupleOID tid = change.getCell().getTupleOID();
            IValue newValue = change.getNewValue();
            IValue groupId = CellGroupIDGenerator.generateNewId(newValue);
            insertNewValue(tableName, attributeName, tid, dirtyStepID, newValue, groupId, deltaDB, task);
        }
    }

    private void removeDeltaDBTables(IDatabase deltaDB) {
        for (String tableName : deltaDB.getTableNames()) {
            databaseManager.removeTable(tableName, deltaDB);
        }
    }

    private void insertNewValue(String tableName, String attributeName, TupleOID tid, String stepId, IValue newValue, IValue groupID, IDatabase deltaDB, EGTask task) {
        if (logger.isDebugEnabled()) logger.debug("Inserting new value in TableName: " + tableName + " AttributeName: " + attributeName);
        String deltaTableName = BartUtility.getDeltaRelationName(tableName, attributeName);
        Tuple tupleToInsert = buildTuple(tid, stepId, newValue, groupID, tableName, attributeName);
        insertOperator.execute(deltaDB.getTable(deltaTableName), tupleToInsert, task.getSource(), task.getTarget());
    }

    private Tuple buildTuple(TupleOID tid, String stepId, IValue newValue, IValue groupID, String deltaTableName, String attributeName) {
        TupleOID oid = new TupleOID(IntegerOIDGenerator.getNextOID());
        Tuple tuple = new Tuple(oid);
        tuple.addCell(new Cell(oid, new AttributeRef(deltaTableName, BartConstants.TID), new ConstantValue(tid)));
        tuple.addCell(new Cell(oid, new AttributeRef(deltaTableName, BartConstants.STEP), new ConstantValue(stepId)));
        tuple.addCell(new Cell(oid, new AttributeRef(deltaTableName, attributeName), newValue));
        tuple.addCell(new Cell(oid, new AttributeRef(deltaTableName, BartConstants.GROUP_ID), groupID));
        return tuple;
    }

    public void intitializeOperators(EGTask task) {
        this.insertOperator = OperatorFactory.getInstance().getInsertOperator(task);
        this.databaseManager = OperatorFactory.getInstance().getDatabaseManager(task);
        this.deltaBuilder = OperatorFactory.getInstance().getDeltaBuilder(task);
        this.databaseBuilder = OperatorFactory.getInstance().getDatabaseBuilder(task);
    }

}
