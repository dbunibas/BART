package bart.model.expressions;

import bart.exceptions.ExpressionSyntaxException;
import bart.model.database.AttributeRef;
import bart.model.dependency.FormulaVariable;
import java.util.ArrayList;
import java.util.List;
import org.nfunk.jep.IExpressionVisitor;
import org.nfunk.jep.JEP;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Expression implements Cloneable {

    private static Logger logger = LoggerFactory.getLogger(Expression.class);

    private static Expression trueExpression = new Expression("true");

    public static Expression getTrueExpression() {
        return trueExpression;
    }
    private JEP jepExpression;

    public Expression(String expression) throws ExpressionSyntaxException {
        jepExpression = new JEP();
        jepExpression.setAllowUndeclared(true);
        jepExpression.addStandardConstants();
        jepExpression.addStandardFunctions();
        jepExpression.parseExpression(expression);
        if (jepExpression.hasError()) {
            throw new ExpressionSyntaxException("Unable to parse expression " + expression + ". " + jepExpression.getErrorInfo());
        }
        if (logger.isDebugEnabled()) logger.debug("Created expression: " + jepExpression);
    }

    public JEP getJepExpression() {
        return jepExpression;
    }

    public void accept(IExpressionVisitor visitor) {
        if (this.jepExpression != null) {
            jepExpression.getTopNode().accept(visitor);
        }
    }

    public List<String> getVariables() {
        List<String> result = new ArrayList<String>();
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable variable : symbolTable.getVariables()) {
            String variableDescription = variable.getDescription().toString();
            if (!result.contains(variableDescription)) {
                result.add(variableDescription);
            }
        }
        return result;
    }

    public List<Object> getConstants() {
        return jepExpression.getSymbolTable().getConstants();
    }

    public void changeVariableDescription(String oldDescription, Object newDescription) {
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable variableInExpression : symbolTable.getVariables()) {
            if (variableInExpression.getDescription().equals(oldDescription)) {
                variableInExpression.setDescription(newDescription);
            }
        }
    }
    
    public void setVariableDescription(String variableId, Object description){
        SymbolTable symbolTable = jepExpression.getSymbolTable();
        for (Variable variableInExpression : symbolTable.getVariables()) {
            if (variableInExpression.getName().equals(variableId)) {
                variableInExpression.setDescription(description);
            }
        }
    }

    public void setVariableValue(String variable, Object value) {
        jepExpression.setVarValue(variable, value);
    }

    public Object getValue() throws ExpressionSyntaxException {
        Object value = jepExpression.getValueAsObject();
        if (jepExpression.hasError()) {
            throw new ExpressionSyntaxException(jepExpression.getErrorInfo());
        }
        if (logger.isDebugEnabled()) logger.debug("Value of function: " + value);
        return value;
    }

    @Override
    public Expression clone() {
        Expression clone = null;
        try {
            clone = (Expression) super.clone();
            clone.jepExpression = (JEP) this.jepExpression.clone();
        } catch (CloneNotSupportedException ex) {
            logger.error(ex.getLocalizedMessage());
        }
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Expression)) {
            return false;
        }
        Expression expression = (Expression) obj;
        return this.toString().equals(expression.toString());
    }

    public boolean equalsUpToVariableIds(Object obj) {
        if (!(obj instanceof Expression)) {
            return false;
        }
        Expression expression = (Expression) obj;
        return this.toString().equals(expression.toString());
    }

    @Override
    public String toString() {
        return this.jepExpression.toString();
    }

    public String toSQLString() {
        return this.jepExpression.toSQLString();
    }

    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString()).append("\n");
        for (String variable : getVariables()) {
            sb.append("\t Variable ").append(variable);
            Variable jepVariable = this.getJepExpression().getSymbolTable().getVar(variable);
            if (jepVariable != null) {
                Object description = jepVariable.getDescription();
                if (description instanceof FormulaVariable) {
                    FormulaVariable formulaVariable = (FormulaVariable) description;
                    sb.append(": ").append(formulaVariable.toLongString());
                } else if (description instanceof AttributeRef) {
                    sb.append(": ").append(description);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
