package bart.model.dependency.operators;

import bart.model.dependency.Dependency;
import java.util.Comparator;

class DependencyComparator implements Comparator<Dependency> {

    public int compare(Dependency t1, Dependency t2) {
        return t1.getId().compareTo(t2.getId());
    }
    
}
