package bart.model.errorgenerator.operator.valueselectors;

import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import java.util.Random;

public class TypoRemoveString implements IDirtyStrategy {

    private int charsToRemove;

    public TypoRemoveString(int charsToRemove) {
        this.charsToRemove = charsToRemove;
    }

    public IValue generateNewValue(IValue value) {
        String valueString = value + "";
        String originalValue = valueString;
        while (originalValue.equals(valueString)) {
            int changes = 0;
            while (!valueString.isEmpty() && changes < charsToRemove) {
                int randomIndex = selectRandomIndex(valueString);
                valueString = removeChars(valueString, randomIndex);
                changes++;
            }
        }
        return new ConstantValue(valueString);
    }

    private int selectRandomIndex(String value) {
        Random generator = new Random();
        return generator.nextInt(value.length());
    }

    private String removeChars(String valueString, int selectRandomIndex) {
        if (selectRandomIndex == 0) selectRandomIndex = 1;
        String prefix = valueString.substring(0, selectRandomIndex - 1);
        String suffix = valueString.substring(selectRandomIndex, valueString.length());
        return prefix + suffix;
    }
    
    public int getCharsToRemove() {
        return charsToRemove;
    }

    @Override
    public String toString() {
        return "TypoRemoveString{" + "charsToRemove=" + charsToRemove + '}';
    }

}
