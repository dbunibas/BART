package bart.model.errorgenerator.operator.deltadb;

import bart.BartConstants;
import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CellGroupIDGenerator {

    private static Logger logger = LoggerFactory.getLogger(CellGroupIDGenerator.class);

    private static Long counter = 0L;

    public static void resetCounter() {
        counter = 0L;
    }
        
    public static IValue generateNewId(IValue value) {
        if (value instanceof NullValue ) {
            return value;
        }
        String valueString = value.toString();
        return new ConstantValue(valueString + BartConstants.VALUE_LABEL + (counter++));
    }


    public static IValue getValue(IValue cellGroupId) {
        IValue value = cellGroupId;
        if (cellGroupId instanceof ConstantValue) {
            if (cellGroupId.toString().contains(BartConstants.VALUE_LABEL)) {
                value = new ConstantValue(cellGroupId.toString().substring(0, cellGroupId.toString().indexOf(BartConstants.VALUE_LABEL)));
            }
        }
        return value;
    }
}