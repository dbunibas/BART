package bart.model.dependency;

import bart.BartConstants;
import speedy.model.expressions.Expression;
import java.util.ArrayList;
import java.util.List;

public class ComparisonAtom implements IFormulaAtom {

    private IFormula formula;
    private Expression expression;
    private List<FormulaVariable> variables = new ArrayList<FormulaVariable>();
    private String leftVariableId;
    private String rightVariableId;
    private String leftConstant;
    private String rightConstant;
    private String operator;

    public ComparisonAtom(IFormula formula, Expression expression, String leftVariableId, String rightVariableId, String leftConstant, String rightConstant, String operator) {
        this.formula = formula;
        this.expression = expression;
        this.leftVariableId = leftVariableId;
        this.rightVariableId = rightVariableId;
        this.leftConstant = leftConstant;
        this.rightConstant = rightConstant;
        this.operator = operator;
    }

    public Expression getExpression() {
        return expression;
    }

    public IFormula getFormula() {
        return formula;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setFormula(IFormula formula) {
        this.formula = formula;
    }

    public void addVariable(FormulaVariable variable) {
        this.variables.add(variable);
    }

    public List<FormulaVariable> getVariables() {
        return this.variables;
    }

    public boolean isEqualityComparison() {
        return BartConstants.EQUAL.equals(operator.trim());
    }

    public boolean isInequalityComparison() {
        return BartConstants.NOT_EQUAL.equals(operator.trim());
    }

    public boolean isVariableComparison() {
        return variables.size() == 2;
    }

    public boolean isVariableEqualityComparison() {
        return isEqualityComparison() && isVariableComparison();
    }

    public boolean isVariableInequalityComparison() {
        return isInequalityComparison() && isVariableComparison();
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public FormulaVariable getLeftVariable() {
        if (leftConstant == null) {
            return variables.get(0);
        }
        return null;
    }

    public FormulaVariable getRightVariable() {
        if (rightConstant == null && leftConstant != null) {
            return variables.get(0);
        } else if (rightConstant == null && leftConstant == null) {
            return variables.get(1);
        }
        return null;
    }

    public String getLeftVariableId() {
        return leftVariableId;
    }

    public void setLeftVariableId(String leftVariableId) {
        this.leftVariableId = leftVariableId;
    }

    public String getRightVariableId() {
        return rightVariableId;
    }

    public void setRightVariableId(String rightVariableId) {
        this.rightVariableId = rightVariableId;
    }

    public String getLeftConstant() {
        return leftConstant;
    }

    public void setLeftConstant(String leftConstant) {
        this.leftConstant = leftConstant;
    }

    public String getRightConstant() {
        return rightConstant;
    }

    public void setRightConstant(String rightConstant) {
        this.rightConstant = rightConstant;
    }

    public String getLeftArgument() {
        if (leftConstant == null) {
            return getLeftVariable().toString();
        }
        return leftConstant;
    }

    public String getRightArgument() {
        if (rightConstant == null) {
            return getRightVariable().toString();
        }
        return rightConstant;
    }

    public String getConstant() {
        if (leftConstant != null) {
            return leftConstant;
        }
        return rightConstant;
    }

    public boolean isNumericalComparison() {
        return !isEqualityComparison() && !isInequalityComparison();
    }

    public boolean isRelational() {
        return false;
    }

    public boolean isComparison() {
        return true;
    }

    public boolean isBuiltIn() {
        return false;
    }

    @Override
    public ComparisonAtom clone() {
        // atoms are superficially cloned; see PositiveFormula.clone() for deop cloning
        try {
            ComparisonAtom clone = (ComparisonAtom) super.clone();
            clone.expression = this.expression.clone();
            clone.variables = new ArrayList<FormulaVariable>(this.variables);
//            for (FormulaVariable variable : variables) {
//                clone.variables.add(variable.clone());
//            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalArgumentException("Unable to clone ComparisonAtom " + ex.getLocalizedMessage());
        }
    }

    @Override
    public String toString() {
        return this.expression.toString();
    }

    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Expression: ").append(this.expression.toLongString());
        sb.append("\n\t Left Variable Id: ").append(getLeftVariableId());
        sb.append("\n\t Right Variable Id: ").append(getRightVariableId());
        sb.append("\n\t Left Variable: ").append((getLeftVariable() != null ? getLeftVariable().toLongString() : "null"));
        sb.append("\n\t Right Variable: ").append((getRightVariable() != null ? getRightVariable().toLongString() : "null"));
        sb.append("\n\t Left Constant: ").append(leftConstant);
        sb.append("\n\t Right Constant: ").append(rightConstant);
        sb.append("\n\t Operator: ").append(operator);
        return sb.toString();
    }

}
