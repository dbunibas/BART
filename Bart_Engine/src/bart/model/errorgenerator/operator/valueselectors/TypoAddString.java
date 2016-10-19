package bart.model.errorgenerator.operator.valueselectors;

import bart.model.EGTask;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypoAddString implements IDirtyStrategy {

    private String chars;
    private int charsToAdd;
    private static final Logger logger = LoggerFactory.getLogger(TypoAddString.class);

    public TypoAddString(String chars, int charsToAdd) {
        assert (charsToAdd > 0 && chars.length() > 0);
        this.chars = chars;
        this.charsToAdd = charsToAdd;
    }

    public IValue generateNewValue(IValue value, EGTask egTask) {
        String valueString = value + "";
        for (int i = 0; i < charsToAdd; i++) {
            valueString = insertValue(valueString, chars, selectRandomIndex(valueString));
        }
        return new ConstantValue(valueString);
    }

    private int selectRandomIndex(String value) {
        if (value.isEmpty()) {
            return 0;
        }
        Random generator = new Random();
        return generator.nextInt(value.length());
    }

    public String getChars() {
        return chars;
    }

    public int getCharsToAdd() {
        return charsToAdd;
    }

    private String insertValue(String valueString, String chars, int selectRandomIndex) {
        String prefix = valueString.substring(0, selectRandomIndex);
        String suffix = valueString.substring(selectRandomIndex, valueString.length());
        if (logger.isDebugEnabled()) {
            logger.debug("Value: " + valueString);
            logger.debug("Prefix: " + prefix);
            logger.debug("Suffix: " + suffix);
        }
        return prefix + chars + suffix;
    }

    @Override
    public String toString() {
        return "TypoAddString{" + "chars=" + chars + ", charsToAdd=" + charsToAdd + '}';
    }

}
