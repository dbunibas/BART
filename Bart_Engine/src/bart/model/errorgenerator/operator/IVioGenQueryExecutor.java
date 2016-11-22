package bart.model.errorgenerator.operator;

import bart.model.EGTask;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.VioGenQuery;

public interface IVioGenQueryExecutor {

    public int execute(VioGenQuery vioGenQuery, CellChanges allCellChanges, EGTask task);

//    public void initializeQuery(VioGenQuery vioGenQuery, EGTask task);

}
