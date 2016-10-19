package bart.model.errorgenerator.operator.valueselectors;

import bart.model.EGTask;
import speedy.model.database.IValue;
import java.util.Random;

public class TypoRandom implements IDirtyStrategy {

    public IValue generateNewValue(IValue value, EGTask egTask) {
        Random r = new Random();
        int randomStrategy = r.nextInt(3);
        if (randomStrategy == 0) {
            return new TypoAddString(randomString(), 1).generateNewValue(value, egTask);
        }
        if (randomStrategy == 1) {
            return new TypoRemoveString(1).generateNewValue(value, egTask);
        }
        if (randomStrategy == 2) {
            return new TypoSwitchValue(1).generateNewValue(value, egTask);
        }
        throw new UnsupportedOperationException("Unknown strategy");
    }

    private String randomString() {
        Random r = new Random();
        char c = (char) (r.nextInt(26) + 'a');
        return c + "";
    }

    @Override
    public String toString() {
        return "TypoRandom";
    }

}
