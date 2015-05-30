package bart.model.errorgenerator.operator;

import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.algebra.operators.IUpdateCell;
import bart.model.database.CellRef;
import bart.model.database.IDatabase;
import bart.model.database.operators.IDatabaseManager;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ICellChange;
import bart.utility.BartUtility;

public class ApplyCellChanges implements IChangeApplier, IInitializableOperator {

    private IUpdateCell cellUpdater;
    private IDatabaseManager databaseManager;

    public void apply(CellChanges cellChanges, EGTask task) {
        intitializeOperators(task);
        IDatabase dirtyTarget = task.getTarget();
        if (task.getConfiguration().isCloneTargetSchema()) {
            String dirtySuffix = BartUtility.getDirtyCloneSuffix(task);
            try {
                databaseManager.removeClone(task, dirtySuffix);
            } catch (Exception e) {
            }
            dirtyTarget = databaseManager.cloneTarget(task, dirtySuffix);
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
