package it.unibas.bartgui.egtaskdataobject.api;

import bart.model.errorgenerator.ICellChange;
import bart.utility.ErrorGeneratorStats;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import java.util.Set;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public interface ISaveStatistics {
    
    public void save(EGTaskDataObjectDataObject egtDO, 
            long startTime, 
            double timeEsecution, 
            Set<ICellChange> set,
            ErrorGeneratorStats er);
}
