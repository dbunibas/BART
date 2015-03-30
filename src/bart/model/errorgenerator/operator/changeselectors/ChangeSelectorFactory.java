package bart.model.errorgenerator.operator.changeselectors;

import bart.model.EGTask;
import bart.model.errorgenerator.CellChange;

public class ChangeSelectorFactory {

    private static IChangeSelector aPrioryRepairabilitySelector = new APrioriRepairabilityChangeSelector();
    private static IChangeSelector standardSelectorSelector = new StandardChangeSelector();

    public static IChangeSelector getChangeSelector(CellChange change, EGTask task) {
        if (change.getVioGenQuery().getConfiguration().getRepairabilityRange() != null) {
            return aPrioryRepairabilitySelector;
        }
        return standardSelectorSelector;
    }

}
