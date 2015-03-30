package bart.model.errorgenerator.operator.changeselectors;

import bart.model.RepairabilityRange;
import bart.model.errorgenerator.CellChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APrioriRepairabilityChangeSelector implements IChangeSelector {

    private static Logger logger = LoggerFactory.getLogger(APrioriRepairabilityChangeSelector.class);

    public boolean acceptChange(CellChange change) {
        RepairabilityRange range = change.getVioGenQuery().getConfiguration().getRepairabilityRange();
        if (range == null) {
            throw new IllegalArgumentException("Unable to use RepairabilityChangeSelector. No reparability range specified for viogenquery " + change.getVioGenQuery().toShortString());
        }
        double repairability = change.getRepairabilityAPriori();
        if (range.getMaxValue() != null && repairability > range.getMaxValue()) {
            if (logger.isDebugEnabled()) logger.debug("Discarding change " + change + ". Repairability " + repairability + " higher than " + range.getMaxValue());
            return false;
        }
        if (range.getMinValue() != null && repairability < range.getMinValue()) {
            if (logger.isDebugEnabled()) logger.debug("Discarding change " + change + ". Repairability " + repairability + " lower than " + range.getMinValue());
            return false;
        }
        if (logger.isDebugEnabled()) logger.trace("Accepting change " + change);
        return true;
    }

}
