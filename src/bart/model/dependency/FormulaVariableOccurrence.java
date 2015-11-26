package bart.model.dependency;

import speedy.model.database.AttributeRef;
import speedy.model.database.TableAlias;

public class FormulaVariableOccurrence implements IFormulaValue {
    
    private AttributeRef attributeRef;
    private String variableId;
    
    public FormulaVariableOccurrence(AttributeRef attributeRef, String variableId) {
        this.attributeRef = attributeRef;
        this.variableId = variableId;
    }
    
    public void setVariableId(String id) {
        this.variableId = id;
    }
    
    public AttributeRef getAttributeRef() {
        return attributeRef;
    }
    
    public TableAlias getTableAlias() {
        return this.attributeRef.getTableAlias();
    }
    
    public String getVariableId() {
        return this.variableId;
    }
    
    public boolean isVariable() {
        return true;
    }
    
    public boolean isNull() {
        return false;
    }

    public boolean isConstant() {
        return false;
    }

    public boolean isExpression() {
        return false;
    }
    
    public String toString() {
        return this.variableId;
    }
    
    public String toLongString() {
        return this.variableId + ":" + this.attributeRef;
    }
    
    public String toFormulaString() {
        return "$" + this.variableId;
    }
    
    public IFormulaValue clone() {
        try {
            FormulaVariableOccurrence c = (FormulaVariableOccurrence) super.clone();
            c.attributeRef = this.attributeRef.clone();
            return c;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException("Unable to clone FormulaVariableOccurrence " + ex.getLocalizedMessage());
        }
    }
}
