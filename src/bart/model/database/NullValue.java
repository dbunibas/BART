package bart.model.database;

import bart.BartConstants;

public class NullValue implements IValue {

    private Object value;

    public NullValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return BartConstants.NULL;
    }

    public Object getPrimitiveValue() {
        return this.value;
    }

    public boolean isLabeledNull() {
        return !value.toString().equals(BartConstants.NULL_VALUE);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final NullValue other = (NullValue) obj;
        return this.value.toString().equals(other.value.toString());
//        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) return false;
//        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
