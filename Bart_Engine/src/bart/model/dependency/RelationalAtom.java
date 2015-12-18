package bart.model.dependency;

import speedy.model.database.TableAlias;
import speedy.model.expressions.Expression;
import java.util.ArrayList;
import java.util.List;

public class RelationalAtom implements IFormulaAtom, Cloneable {
    
    private IFormula formula;
    private TableAlias tableAlias;
    private List<FormulaAttribute> attributes = new ArrayList<FormulaAttribute>();
    
    public RelationalAtom(String tableName) {
        this.tableAlias = new TableAlias(tableName);
    }
    
    public RelationalAtom(String tableName, List<FormulaAttribute> attributes) {
        this(tableName);
        this.attributes = attributes;
    }
    
    public boolean isSource() {
        return tableAlias.isSource();
    }
    
    public void setSource(boolean source) {
        this.tableAlias.setSource(source);
    }
    
    public IFormula getFormula() {
        return formula;
    }
    
    public void setFormula(IFormula formula) {
        this.formula = formula;
    }
    
    public List<FormulaAttribute> getAttributes() {
        return attributes;
    }
    
    public void addAttribute(FormulaAttribute attribute) {
        this.attributes.add(attribute);
    }
    
    public TableAlias getTableAlias() {
        return tableAlias;
    }
    
    public void addAlias(String alias) {
        this.tableAlias.addAlias(alias);
        for (FormulaAttribute attribute : this.attributes) {
            IFormulaValue value = attribute.getValue();
            if (value instanceof FormulaVariableOccurrence) {
                ((FormulaVariableOccurrence) value).getAttributeRef().setTableAlias(this.tableAlias);
            }
        }
    }

    public void setAlias(String alias) {
        this.tableAlias.setAlias(alias);
        for (FormulaAttribute attribute : this.attributes) {
            IFormulaValue value = attribute.getValue();
            if (value instanceof FormulaVariableOccurrence) {
                ((FormulaVariableOccurrence) value).getAttributeRef().setTableAlias(this.tableAlias);
            }
        }
    }
    
    public boolean isAliased() {
        return this.tableAlias.isAliased();
    }
    
    public String getTableName() {
        return this.tableAlias.getTableName();
    }
    
    public String getTableNameWithAlias() {
        return this.tableAlias.toString();
    }
    
    public void addVariable(FormulaVariable variable) {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    public List<FormulaVariable> getVariables() {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    public Expression getExpression() {
        return null;
    }

    public boolean isRelational() {
        return true;
    }

    public boolean isComparison() {
        return false;
    }

    public boolean isBuiltIn() {
        return false;
    }
    
    @Override
    public RelationalAtom clone() {
        // atoms are superficially cloned; see PositiveFormula.clone() for deop cloning
        try {
            RelationalAtom c = (RelationalAtom) super.clone();
            c.tableAlias = this.tableAlias.clone();
            c.attributes = new ArrayList<FormulaAttribute>();
            for (FormulaAttribute formulaAttribute : this.attributes) {
                c.attributes.add(formulaAttribute.clone());
            }
            return c;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(tableAlias.getTableName()).append("(");
        for (FormulaAttribute attribute : attributes) {
            result.append(attribute).append(", ");
        }
        result.deleteCharAt(result.length() - 1);
        result.deleteCharAt(result.length() - 1);
        result.append(")");
        return result.toString();
    }

    public String toLongString() {
        return toString();
    }
}
