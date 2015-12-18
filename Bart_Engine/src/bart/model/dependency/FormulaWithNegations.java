package bart.model.dependency;

import bart.model.dependency.operators.IFormulaVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FormulaWithNegations implements IFormula {

    private IFormula father;
    private PositiveFormula positiveFormula;
    private List<IFormula> negatedSubFormulas = new ArrayList<IFormula>();

    public FormulaWithNegations() {
    }

    public FormulaWithNegations(PositiveFormula positiveFormula) {
        this.positiveFormula = positiveFormula;
    }

    public FormulaWithNegations(IFormula father, PositiveFormula positiveFormula) {
        this.father = father;
        this.positiveFormula = positiveFormula;
    }

    public IFormula getFather() {
        return father;
    }

    public void setFather(IFormula father) {
        this.father = father;
    }

    public PositiveFormula getPositiveFormula() {
        return positiveFormula;
    }

    public void setPositiveFormula(PositiveFormula formula) {
        this.positiveFormula = formula;
    }

    public List<IFormulaAtom> getAtoms() {
        return this.positiveFormula.getAtoms();
    }

    public void addAtom(IFormulaAtom a) {
        this.positiveFormula.addAtom(a);
    }

    public List<IFormula> getNegatedSubFormulas() {
        return negatedSubFormulas;
    }

    public void addNegatedFormula(IFormula formula) {
        this.negatedSubFormulas.add(formula);
    }

    public List<FormulaVariable> getLocalVariables() {
        return this.positiveFormula.getLocalVariables();
    }

    public List<VariableEquivalenceClass> getLocalVariableEquivalenceClasses() {
        return this.positiveFormula.getLocalVariableEquivalenceClasses();
    }

    @SuppressWarnings("unchecked")
    public List<FormulaVariable> getAllVariables() {
        if (father == null) {
            return Collections.EMPTY_LIST;
        }
        return father.getPositiveFormula().getAllVariables();
    }

    public CrossProductFormulas getCrossProductFormulas() {
        return this.positiveFormula.getCrossProductFormulas();
    }

    public void setCrossProductFormulas(CrossProductFormulas crossProductFormulas) {
        this.positiveFormula.setCrossProductFormulas(crossProductFormulas);
    }

    public FormulaWithAdornments getFormulaWithAdornments() {
        return this.positiveFormula.getFormulaWithAdornments();
    }

    public void setFormulaWithAdornments(FormulaWithAdornments formulaWithAdornments) {
        this.positiveFormula.setFormulaWithAdornments(formulaWithAdornments);
    }

    public FormulaSampling getFormulaSampling() {
        return this.positiveFormula.getFormulaSampling();
    }

    public void setFormulaSampling(FormulaSampling formulaSampling) {
        this.positiveFormula.setFormulaSampling(formulaSampling);
    }

    public boolean isSymmetric() {
        return this.positiveFormula.isSymmetric();
    }

    public boolean hasNegations() {
        return !this.negatedSubFormulas.isEmpty() || positiveFormula.hasNegations();
    }

    public boolean hasEqualityAdornments() {
        return this.getPositiveFormula().hasEqualityAdornments();
    }

    public void accept(IFormulaVisitor visitor) {
        visitor.visitFormulaWithNegations(this);
    }

    @Override
    public String toString() {
        return this.positiveFormula.toString();
    }

    @Override
    public FormulaWithNegations clone() {
        FormulaWithNegations clone = null;
        try {
            clone = (FormulaWithNegations) super.clone();
//            if (this.father != null) {
//                clone.father = this.father.clone();
//            } //Cyclic clone
            clone.positiveFormula = this.positiveFormula.clone();
            clone.negatedSubFormulas = new ArrayList<IFormula>();
            for (IFormula subFormula : this.negatedSubFormulas) {
                IFormula cloneSubFormula = subFormula.clone();
                cloneSubFormula.setFather(clone);
                clone.negatedSubFormulas.add(cloneSubFormula);
            }
        } catch (CloneNotSupportedException ex) {
        }
        return clone;
    }

    public String toLongString() {
        return toString();
    }
}
