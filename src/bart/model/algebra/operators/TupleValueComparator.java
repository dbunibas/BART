package bart.model.algebra.operators;

import bart.utility.AlgebraUtility;
import bart.model.database.Tuple;
import java.util.Comparator;
import java.util.List;

public class TupleValueComparator implements Comparator<Tuple> {

    public int compare(Tuple t1, Tuple t2) {
        List<Object> values1 = AlgebraUtility.getTupleValuesExceptOIDs(t1);
        List<Object> values2 = AlgebraUtility.getTupleValuesExceptOIDs(t2);
        return values1.toString().compareTo(values2.toString());
    }
}
