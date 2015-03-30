package bart.model.database;

import java.util.Comparator;

public class OidTupleComparator implements Comparator<Tuple> {

    public int compare(Tuple t1, Tuple t2) {
        return t1.getOid().getNumericalValue().compareTo(t2.getOid().getNumericalValue());
    }
    
}