package bart.model.algebra.operators;

import java.util.Comparator;

public class StringComparator<T> implements Comparator<T> {
    
    public int compare(T t1, T t2) {
        return t1.toString().compareTo(t2.toString());
    }
}
