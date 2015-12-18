package bart.model.dependency.operators;

import bart.model.dependency.Dependency;
import bart.model.dependency.FormulaWithNegations;
import bart.model.dependency.PositiveFormula;

public interface IFormulaVisitor {

    public void visitDependency(Dependency dependency);
    public void visitPositiveFormula(PositiveFormula formula);
    public void visitFormulaWithNegations(FormulaWithNegations formula);
    public Object getResult();
    
}
