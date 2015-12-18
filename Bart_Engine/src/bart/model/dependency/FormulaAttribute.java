package bart.model.dependency;

public class FormulaAttribute implements Cloneable {

    private String attributeName;
    private IFormulaValue value;

    public FormulaAttribute(String attributeName) {
        this.attributeName = attributeName;
    }

    public FormulaAttribute(String attributeName, IFormulaValue value) {
        this.attributeName = attributeName;
        this.value = value;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public IFormulaValue getValue() {
        return value;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setValue(IFormulaValue value) {
        this.value = value;
    }

    public FormulaAttribute clone() {
        try {
            FormulaAttribute c = (FormulaAttribute) super.clone();
            c.value = this.value.clone();
            return c;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException("Unable to clone FormulaAttribute " + ex.getLocalizedMessage());
        }
    }

    @Override
    public String toString() {
        return attributeName + ": " + value;
    }
}
