package bart.model.dependency;

import bart.model.dependency.operators.IFormulaVisitor;
import java.util.List;

public interface IFormula extends Cloneable{
    
    public IFormula getFather();
    public void setFather(IFormula father);
    public PositiveFormula getPositiveFormula();
    public void setPositiveFormula(PositiveFormula formula);
    public List<IFormula> getNegatedSubFormulas();
    public void addNegatedFormula(IFormula formula);
    public List<IFormulaAtom> getAtoms();
    public void addAtom(IFormulaAtom a);
    public List<FormulaVariable> getLocalVariables();
    public List<VariableEquivalenceClass> getLocalVariableEquivalenceClasses();
    public List<FormulaVariable> getAllVariables();
    public void accept(IFormulaVisitor visitor);
    public FormulaWithAdornments getFormulaWithAdornments();
    public void setFormulaWithAdornments(FormulaWithAdornments formulaWithAdornments);
    public CrossProductFormulas getCrossProductFormulas();
    public void setCrossProductFormulas(CrossProductFormulas crossProductFormulas);
    public FormulaSampling getFormulaSampling();
    public void setFormulaSampling(FormulaSampling formulaSampling);
    public boolean isSymmetric();
    public boolean hasNegations();
    public boolean hasEqualityAdornments();
    public IFormula clone();
    public String toLongString();
    
}
