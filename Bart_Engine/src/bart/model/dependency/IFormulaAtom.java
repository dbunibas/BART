package bart.model.dependency;

import speedy.model.expressions.Expression;
import java.util.List;

public interface IFormulaAtom extends Cloneable {

    public void addVariable(FormulaVariable variable);

    public List<FormulaVariable> getVariables();

    public Expression getExpression();

    public IFormula getFormula();

    public void setFormula(IFormula formula);

    public String toLongString();
    
    public boolean isRelational();
    
    public boolean isComparison();
    
    public boolean isBuiltIn();

    // atoms are superficially cloned; see PositiveFormula.clone() for deop cloning
    public IFormulaAtom clone();
}
