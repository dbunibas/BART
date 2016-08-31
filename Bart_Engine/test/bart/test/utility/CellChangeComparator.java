package bart.test.utility;

import bart.model.errorgenerator.VioGenQueryCellChange;
import java.util.Comparator;

public class CellChangeComparator implements Comparator<VioGenQueryCellChange> {

    public int compare(VioGenQueryCellChange o1, VioGenQueryCellChange o2) {
        return o1.getCell().getTupleOID().getNumericalValue().compareTo(o2.getCell().getTupleOID().getNumericalValue());
    }

}
