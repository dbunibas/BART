package bart.model.errorgenerator.operator.changeselectors;

import bart.model.errorgenerator.CellChange;

public interface IChangeSelector {
    
    public boolean acceptChange(CellChange change);
    
}
