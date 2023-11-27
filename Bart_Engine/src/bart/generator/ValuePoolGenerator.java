package bart.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.database.NullValue;

public class ValuePoolGenerator {

    private static Logger logger = LoggerFactory.getLogger(ValuePoolGenerator.class);
    private static Random randomGenerator = new Random();
    private String type;
    private double duplicatePercentage;
    private int range;
    private double offset;

    public ValuePoolGenerator(String type) {
        this(type, 0.0, 1, 0.0);
    }

    public ValuePoolGenerator(String type, double duplicatePercentage) {
        this(type, duplicatePercentage, 1, 0.0);
    }

    public ValuePoolGenerator(String type, double duplicatePercentage, int range, double offset) {
        if (duplicatePercentage < 0 || duplicatePercentage > 1) {
            throw new IllegalArgumentException("Duplicate percentage must be in the range (0.0 - 1.0)");
        }
        this.type = type;
        this.duplicatePercentage = duplicatePercentage;
        this.range = range;
        this.offset = offset;
    }

    public List<Object> generateValues(int size) {
        int duplicateSize = (int) (size * duplicatePercentage);
        int distinctSize = size - duplicateSize;
        logger.debug("Generating " + size + " values (" + distinctSize + " distincts, " + duplicateSize + " duplicates)");
        Set<Object> distinctValues = generateDistinctValues(distinctSize);
        List<Object> values = new ArrayList<Object>(distinctValues);
        for (int i = 0; i < duplicateSize; i++) {
            int random = randomGenerator.nextInt(distinctSize);
            Object duplicateValue = values.get(random);
            values.add(duplicateValue);
        }
        Collections.shuffle(values);
        return values;
    }

    private Set<Object> generateDistinctValues(int size) {
        if (type.equals(Constants.INTEGER_TYPE)) {
            return generateDistinctIntegerValues(size);
        }
        if (type.equals(Constants.DOUBLE_TYPE)) {
            return generateDistinctDoubleValues(size);
        }
        if (type.equals(Constants.STRING_TYPE)) {
            return generateDistinctStringValues(size);
        }
        if (type.equals(Constants.SKOLEM_TYPE)) {
            return generateDistinctSkolemValues(size);
        }
        throw new IllegalArgumentException("Type " + type + " is not supported");
    }

    private Set<Object> generateDistinctIntegerValues(int size) {
        Set<Object> result = new HashSet<Object>(size);
        for (int i = 0; i < size; i++) {
            result.add((int) (i * this.range + this.offset));
        }
        return result;
    }

    private Set<Object> generateDistinctDoubleValues(int size) {
        Set<Object> result = new HashSet<Object>(size);
        while (result.size() < size) {
            result.add(randomGenerator.nextDouble() * this.range + this.offset);
        }
        return result;
    }

    private Set<Object> generateDistinctStringValues(int size) {
        Set<Object> result = new HashSet<Object>(size);
        int fails = 0;
        long lastSize = 0;
        while (result.size() < size) {
            String randomString = getRandomString();
            result.add(randomString);
            if (lastSize == result.size()) {
                fails++;
                if (fails > 1) System.out.println("Fails: " + fails);
            } else {
                lastSize = result.size();
                fails = 0;
            }
            if (fails > 10) {
                throw new IllegalArgumentException("Unable to generate " + size + " distinct strings");
            }
        }
        return result;
    }

    private Set<Object> generateDistinctSkolemValues(int size) {
        Set<Object> result = new HashSet<Object>(size);
        for (int i = 0; i < size; i++) {
            String skolemString = StringUtils.repeat("0", (size + "").length() - (i + "").length()) + i;
            result.add(new NullValue(SpeedyConstants.SKOLEM_PREFIX + skolemString));
        }
        return result;
    }

    private String getRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (new Random().nextFloat() * (rightLimit - leftLimit));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

}
