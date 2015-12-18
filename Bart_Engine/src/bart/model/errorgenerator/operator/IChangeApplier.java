package bart.model.errorgenerator.operator;

import bart.model.EGTask;
import bart.model.errorgenerator.CellChanges;


public interface IChangeApplier {

    void apply(CellChanges cellChanges, EGTask task);
    
}
