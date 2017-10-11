package bart.comparison.operators;

import java.util.Comparator;
import bart.comparison.TupleMatch;

public class TupleMatchComparatorScore implements Comparator<TupleMatch> {

    public int compare(TupleMatch o1, TupleMatch o2) {
        return o2.getScoreEstimate().compareTo(o1.getScoreEstimate());
    }

}
