package bart.model.dependency;

public class FormulaConstant implements IFormulaValue {

    private Object value;
    private boolean isNull;

    public FormulaConstant(Object value) {
        this.value = value;
    }

    public FormulaConstant(Object value, boolean isNull) {
        this.value = value;
        this.isNull = isNull;
    }

    public Object getValue() {
        return value;
    }

    public boolean isConstant() {
        return true;
    }

    public boolean isNull() {
        return this.isNull;
    }

    public boolean isVariable() {
        return false;
    }

    public boolean isExpression() {
        return false;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public IFormulaValue clone() {
        try {
            return (IFormulaValue) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException("Unable to clone FormulaValue " + ex.getLocalizedMessage());
        }
    }

}
