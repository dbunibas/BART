package bart.test.generator;

import bart.generator.Constants;
import bart.generator.ValuePoolGenerator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;

public class TestPool extends TestCase {

    public void testGenerateDistinctIntegers() {
        ValuePoolGenerator pool = new ValuePoolGenerator(Constants.INTEGER_TYPE);
        List<Object> values = pool.generateValues(1000);
        assertEquals(1000, values.size());
        assertEquals(1000, distinctSize(values));
    }

    public void testGenerateDistinctDoubles() {
        ValuePoolGenerator pool = new ValuePoolGenerator(Constants.DOUBLE_TYPE);
        List<Object> values = pool.generateValues(1000);
        assertEquals(1000, values.size());
        assertEquals(1000, distinctSize(values));
    }

    public void testGenerateDistinctStrings() {
        ValuePoolGenerator pool = new ValuePoolGenerator(Constants.STRING_TYPE);
        List<Object> values = pool.generateValues(1000);
        assertEquals(1000, values.size());
        assertEquals(1000, distinctSize(values));
    }

    public void testGenerateDistinctSkolems() {
        ValuePoolGenerator pool = new ValuePoolGenerator(Constants.SKOLEM_TYPE);
        List<Object> values = pool.generateValues(1000);
        assertEquals(1000, values.size());
        assertEquals(1000, distinctSize(values));
    }

    public void testGenerateIntegers() {
        ValuePoolGenerator pool = new ValuePoolGenerator(Constants.INTEGER_TYPE, 0.10);
        List<Object> values = pool.generateValues(1000);
        assertEquals(1000, values.size());
        assertEquals(900, distinctSize(values));
    }

    public void testGenerateDoubles() {
        ValuePoolGenerator pool = new ValuePoolGenerator(Constants.DOUBLE_TYPE, 0.10);
        List<Object> values = pool.generateValues(1000);
        assertEquals(1000, values.size());
        assertEquals(900, distinctSize(values));
    }

    public void testGenerateStrings() {
        ValuePoolGenerator pool = new ValuePoolGenerator(Constants.STRING_TYPE, 0.10);
        List<Object> values = pool.generateValues(1000);
        assertEquals(1000, values.size());
        assertEquals(900, distinctSize(values));
    }

//    public void testGenerateIntegers10M() {
//        int size = 10000000;
//        ValuePoolGenerator pool = new ValuePoolGenerator(Constants.INTEGER_TYPE, 0.10);
//        List<Object> values = pool.generateValues(size);
//        assertEquals(size, values.size());
//    }
//
//    public void testGenerateStrings1M() {
//        int size = 1000000;
//        ValuePoolGenerator pool = new ValuePoolGenerator(Constants.STRING_TYPE, 0.10);
//        List<Object> values = pool.generateValues(size);
//        assertEquals(size, values.size());
//    }
//
//    public void testGenerateStrings10M() {
//        int size = 10000000;
//        ValuePoolGenerator pool = new ValuePoolGenerator(Constants.STRING_TYPE, 0.10);
//        List<Object> values = pool.generateValues(size);
//        assertEquals(size, values.size());
//    }
//
//    public void testGenerateStrings100M() {
//        int size = 100000000;
//        ValuePoolGenerator pool = new ValuePoolGenerator(Constants.STRING_TYPE, 0.10);
//        List<Object> values = pool.generateValues(size);
//        assertEquals(size, values.size());
//    }

    private int distinctSize(List<Object> list) {
        Set<Object> set = new HashSet<Object>(list);
        return set.size();
    }
}
