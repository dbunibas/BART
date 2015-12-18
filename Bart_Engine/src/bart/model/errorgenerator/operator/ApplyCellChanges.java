package bart.model.errorgenerator.operator;

import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import speedy.model.algebra.operators.IUpdateCell;
import speedy.model.database.CellRef;
import speedy.model.database.IDatabase;
import speedy.model.database.operators.IDatabaseManager;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ICellChange;
import bart.utility.BartUtility;
import speedy.model.database.dbms.DBMSDB;

public class ApplyCellChanges implements IChangeApplier, IInitializableOperator {

    private IUpdateCell cellUpdater;
    private IDatabaseManager databaseManager;

    public void apply(CellChanges cellChanges, EGTask task) {
        intitializeOperators(task);
        IDatabase dirtyTarget = task.getTarget();
        if (task.getConfiguration().isCloneTargetSchema()) {
            String dirtySuffix = BartUtility.getDirtyCloneSuffix(task);
            DBMSDB target = (DBMSDB) task.getTarget();
            try {
                databaseManager.removeClone(target, dirtySuffix);
            } catch (Exception e) {
            }
            dirtyTarget = databaseManager.cloneTarget(target, dirtySuffix);
            task.setDirtyTarget(dirtyTarget);
        }
        for (ICellChange cellChange : cellChanges.getChanges()) {
            cellUpdater.execute(new CellRef(cellChange.getCell()), cellChange.getNewValue(), dirtyTarget);
        }
    }

    public void intitializeOperators(EGTask task) {
        databaseManager = OperatorFactory.getInstance().getDatabaseManager(task);
        cellUpdater = OperatorFactory.getInstance().getCellUpdater(task);
    }

}
