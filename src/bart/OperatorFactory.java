package bart;

import bart.model.database.operators.IRunQuery;
import bart.model.database.operators.dbms.SQLRunQuery;
import bart.model.database.operators.mainmemory.MainMemoryRunQuery;
import bart.model.EGTask;
import bart.model.algebra.operators.IInsertTuple;
import bart.model.algebra.operators.IUpdateCell;
import bart.model.algebra.operators.mainmemory.InsertTuple;
import bart.model.algebra.operators.mainmemory.UpdateCell;
import bart.model.algebra.operators.sql.SQLInsertTuple;
import bart.model.algebra.operators.sql.SQLUpdateCell;
import bart.model.database.operators.IDatabaseManager;
import bart.model.database.operators.IExplainQuery;
import bart.model.database.operators.dbms.SQLDatabaseManager;
import bart.model.database.operators.dbms.SQLExplainQuery;
import bart.model.database.operators.mainmemory.MainMemoryDatabaseManager;
import bart.model.database.operators.mainmemory.MainMemoryExplainQuery;
import bart.model.errorgenerator.ISampleStrategy;
import bart.model.errorgenerator.operator.ApplyCellChanges;
import bart.model.errorgenerator.operator.ExportDatabaseCSV;
import bart.model.errorgenerator.operator.IChangeApplier;
import bart.model.errorgenerator.operator.IExportDatabase;
import bart.model.errorgenerator.operator.TableSizeSampleStrategy;
import bart.model.errorgenerator.operator.deltadb.ApplyCellChangesOnDeltaDB;
import bart.model.errorgenerator.operator.deltadb.IBuildDatabaseForChaseStep;
import bart.model.errorgenerator.operator.deltadb.IBuildDeltaDB;
import bart.model.errorgenerator.operator.deltadb.dbms.BuildSQLDBForChaseStep;
import bart.model.errorgenerator.operator.deltadb.dbms.BuildSQLDeltaDB;
import bart.model.errorgenerator.operator.deltadb.mainmemory.BuildMainMemoryDBForChaseStep;
import bart.model.errorgenerator.operator.deltadb.mainmemory.BuildMainMemoryDeltaDB;
import bart.model.errorgenerator.operator.valueselectors.BasicValueSelector;
import bart.model.errorgenerator.operator.valueselectors.INewValueSelectorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperatorFactory {

    private static Logger logger = LoggerFactory.getLogger(OperatorFactory.class);
    private static OperatorFactory singleton = new OperatorFactory();
    //
    private IRunQuery mainMemoryQueryRunner = new MainMemoryRunQuery();
    private IRunQuery sqlQueryRunner = new SQLRunQuery();
    //
    private IExplainQuery mainMemoryQueryExplanator = new MainMemoryExplainQuery();
    private IExplainQuery sqlQueryExplanator = new SQLExplainQuery();
    //
    private IUpdateCell mainMemoryCellUpdater = new UpdateCell();
    private IUpdateCell sqlCellUpdater = new SQLUpdateCell();
    //
    private IInsertTuple mainMemoryInsertOperator = new InsertTuple();
    private IInsertTuple sqlInsertOperator = new SQLInsertTuple();
    //
    private IDatabaseManager mainMemoryDatabaseManager = new MainMemoryDatabaseManager();
    private IDatabaseManager sqlDatabaseManager = new SQLDatabaseManager();
    //
    private IBuildDatabaseForChaseStep mainMemoryDatabaseBuilder = new BuildMainMemoryDBForChaseStep();
    private IBuildDatabaseForChaseStep sqlDatabaseBuilder = new BuildSQLDBForChaseStep();
    //
    private IBuildDeltaDB mainMemoryDeltaBuilder = new BuildMainMemoryDeltaDB();
    private IBuildDeltaDB sqlDeltaBuilder = new BuildSQLDeltaDB();

    private OperatorFactory() {
    }

    public static OperatorFactory getInstance() {
        return singleton;
    }

    public IRunQuery getQueryRunner(EGTask task) {
        if (task.isMainMemory()) {
            return mainMemoryQueryRunner;
        }
        return sqlQueryRunner;
    }

    public IUpdateCell getCellUpdater(EGTask task) {
        if (task.isMainMemory()) {
            return mainMemoryCellUpdater;
        }
        return sqlCellUpdater;
    }

    public INewValueSelectorStrategy getValueSelector(EGTask task) {
        return new BasicValueSelector();
    }

    public IExplainQuery getQueryExplanator(EGTask task) {
        if (task.isMainMemory()) {
            return mainMemoryQueryExplanator;
        }
        return sqlQueryExplanator;
    }

    public IDatabaseManager getDatabaseManager(EGTask task) {
        if (task.isMainMemory()) {
            return mainMemoryDatabaseManager;
        }
        return sqlDatabaseManager;
    }

    public ISampleStrategy getSampleStrategy(String strategy, EGTask task) {
        if (strategy.equals(BartConstants.SAMPLE_STRATEGY_TABLE_SIZE)) {
            return new TableSizeSampleStrategy();
        }
        throw new IllegalArgumentException("Unknown sample strategy " + strategy);
    }

    public IInsertTuple getInsertOperator(EGTask task) {
        if (task.isMainMemory()) {
            return mainMemoryInsertOperator;
        }
        return sqlInsertOperator;
    }

    public IBuildDeltaDB getDeltaBuilder(EGTask task) {
        if (task.isMainMemory()) {
            return mainMemoryDeltaBuilder;
        }
        return sqlDeltaBuilder;
    }

    public IBuildDatabaseForChaseStep getDatabaseBuilder(EGTask task) {
        if (task.isMainMemory()) {
            return mainMemoryDatabaseBuilder;
        }
        return sqlDatabaseBuilder;
    }

    public IChangeApplier getChangeApplier(EGTask task) {
        if(task.getConfiguration().isUseDeltaDBForChanges()){
            return new ApplyCellChangesOnDeltaDB();
        }
        return new ApplyCellChanges();
    }

    public IExportDatabase getDatabaseExporter(EGTask task) {
        String type = task.getConfiguration().getExportDirtyDBType();
        if(BartConstants.CSV.equalsIgnoreCase(type)){
            return new ExportDatabaseCSV();
        }
        throw new IllegalArgumentException("Unsupported export type " + type);
    }

}
