package bart.utility.comparator;

import java.util.Comparator;

public class StringComparator implements Comparator<Object> {

    public int compare(Object o1, Object o2) {
        return o1.toString().compareTo(o2.toString());
    }
}
