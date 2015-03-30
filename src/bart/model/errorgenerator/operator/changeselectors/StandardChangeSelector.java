package bart.model.errorgenerator.operator.changeselectors;

import bart.model.errorgenerator.CellChange;

public class StandardChangeSelector implements IChangeSelector {

    public boolean acceptChange(CellChange change) {
        return true;
    }

}
