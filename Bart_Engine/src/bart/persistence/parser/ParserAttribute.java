package bart.persistence.parser;

import speedy.model.expressions.Expression;

public class ParserAttribute implements Cloneable {

    private String name;
    private String variable;
    private Object value;

    public ParserAttribute(String name, String value) {
        this.name = name;
        this.value = value;
        cleanValue();
    }

    public void setValue(Object value) {
        this.value = value;
        cleanValue();
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String getVariable() {
        return variable;
    }

    public boolean isVariable() {
        return this.variable != null;
    }

    public boolean isExpression() {
        return (this.value instanceof Expression );
    }

    private void cleanValue() {
        if (this.value instanceof String) {
            String stringValue = (String) value;
            if (stringValue.startsWith("\"") && stringValue.endsWith("\"")) {
                this.value = stringValue.substring(1, stringValue.length() - 1);
            }
        }
    }

    public ParserAttribute clone() {
        try {
            return (ParserAttribute) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    public String toString() {
        String result = name + ": ";
        String printValue = this.value + "";
        if (this.variable != null) {
            printValue = "$" + this.variable;
        }
        return result + printValue;
    }
}
