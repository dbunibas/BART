package bart.model.errorgenerator.operator.valueselectors;

import bart.model.EGTask;
import speedy.model.database.IValue;

public interface IDirtyStrategy {

    public static final String TYPO_APPEND_STRING = TypoAppendString.class.getSimpleName();
    public static final String TYPO_ADD_STRING = TypoAddString.class.getSimpleName();
    public static final String TYPO_RANDOM = TypoRandom.class.getSimpleName();
    public static final String TYPO_REMOVE_STRING = TypoRemoveString.class.getSimpleName();
    public static final String TYPO_SWITCH_VALUE = TypoSwitchValue.class.getSimpleName();
    public static final String TYPO_ACTIVE_DOMAIN = TypoActiveDomain.class.getSimpleName();

    public IValue generateNewValue(IValue value, EGTask egTask);

}
