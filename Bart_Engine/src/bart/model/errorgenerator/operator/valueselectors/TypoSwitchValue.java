package bart.model.errorgenerator.operator.valueselectors;

import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import java.util.Random;

public class TypoSwitchValue implements IDirtyStrategy {

    private int charsToSwitch;

    public TypoSwitchValue(int charsToSwitch) {
        this.charsToSwitch = charsToSwitch;
    }

    public IValue generateNewValue(IValue value) {
        String valueString = value + "";
        String originalValue = valueString;
        while (valueString.equals(originalValue)) {
            for (int i = 0; i < charsToSwitch; i++) {
                int index = getRandomIndex(valueString);
                valueString = swapAt(valueString, index);
            }
        }
        return new ConstantValue(valueString);
    }

    public int getCharsToSwitch() {
        return charsToSwitch;
    }

    private int getRandomIndex(String value) {
        Random generator = new Random();
        int index = generator.nextInt(value.length());
        if (index == value.length() - 1) index--;
        return index;
    }

    private String swapAt(String valueString, int index) {
        char[] toCharArray = valueString.toCharArray();
        if (index == toCharArray.length - 1) index--;
        char tmp = toCharArray[index];
        toCharArray[index] = toCharArray[index + 1];
        toCharArray[index + 1] = tmp;
        return new String(toCharArray);
    }

    @Override
    public String toString() {
        return "TypoSwitchValue{" + "charsToSwitch=" + charsToSwitch + '}';
    }

}
