package bart.model.errorgenerator.operator.valueselectors;

import bart.model.EGTask;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import java.util.Random;

public class TypoSwitchValue implements IDirtyStrategy {

    private int charsToSwitch;

    public TypoSwitchValue(int charsToSwitch) {
        this.charsToSwitch = charsToSwitch;
    }

    public IValue generateNewValue(IValue value, EGTask egTask) {
        String valueString = value + "";
        if (valueString.length() < 2) {
            return new ConstantValue("***");
        }
        String originalValue = valueString;
        for (int i = 0; i < charsToSwitch; i++) {
            int index = getRandomIndex(valueString);
            valueString = swapAt(valueString, index);
        }
        if (originalValue.equals(valueString)) {
            valueString += "***";
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
