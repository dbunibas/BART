package bart.utility.comparator;

import bart.model.errorgenerator.VioGenQuery;
import java.util.Comparator;

public class VioGenQueryComparator implements Comparator<VioGenQuery> {

    public int compare(VioGenQuery o1, VioGenQuery o2) {
//        return o1.getDependency().getId().compareTo(o2.getDependency().getId());
        return o1.toShortString().compareTo(o2.toShortString());
    }
}
