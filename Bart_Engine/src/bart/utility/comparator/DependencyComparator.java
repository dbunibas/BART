package bart.utility.comparator;

import bart.model.dependency.Dependency;
import java.util.Comparator;

public class DependencyComparator implements Comparator<Dependency> {

    public int compare(Dependency o1, Dependency o2) {
        return o1.getId().compareTo(o2.getId());
    }
}
