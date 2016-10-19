package bart.model.errorgenerator.operator.valueselectors;

import bart.model.EGTask;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypoAppendString implements IDirtyStrategy {

    private static final Logger logger = LoggerFactory.getLogger(TypoAppendString.class);
    private String chars;
    private int charsToAdd;

    public TypoAppendString(String chars, int charsToAdd) {
        this.chars = chars;
        this.charsToAdd = charsToAdd;
    }

    public IValue generateNewValue(IValue value, EGTask egTask) {
        StringBuilder sb = new StringBuilder(value + "");
        for (int i = 0; i < charsToAdd; i++) {
            sb.append(chars);
        }
        return new ConstantValue(sb.toString());
    }

    public String getChars() {
        return chars;
    }

    public int getCharsToAdd() {
        return charsToAdd;
    }

    @Override
    public String toString() {
        return "TypoAppendString{" + "chars=" + chars + ", charsToAdd=" + charsToAdd + '}';
    }

}
