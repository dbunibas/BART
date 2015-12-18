package bart.model.dependency;

import bart.model.dependency.operators.IFormulaVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

public class PositiveFormula implements IFormula {

    private IFormula father;
    private List<IFormulaAtom> atoms = new ArrayList<IFormulaAtom>();
    private List<FormulaVariable> localVariables = new ArrayList<FormulaVariable>();
    private List<VariableEquivalenceClass> localVariableEquivalenceClasses = new ArrayList<VariableEquivalenceClass>();
    private FormulaWithAdornments formulaWithAdornments;
    private CrossProductFormulas crossProductFormulas;
    private FormulaSampling formulaSampling;

    public PositiveFormula() {
    }

    public IFormula getFather() {
        return this.father;
    }

    public void setFather(IFormula father) {
        this.father = father;
    }

    public PositiveFormula getPositiveFormula() {
        return this;
    }

    public void setPositiveFormula(PositiveFormula formula) {
        throw new IllegalArgumentException("Formula is already positive.");
    }

    @SuppressWarnings("unchecked")
    public List<IFormula> getNegatedSubFormulas() {
        return Collections.EMPTY_LIST;
    }

    public void addNegatedFormula(IFormula formula) {
        throw new IllegalArgumentException("Formula is positive.");
    }

    public List<IFormulaAtom> getAtoms() {
        return atoms;
    }

    public List<RelationalAtom> getRelationalAtoms() {
        List<RelationalAtom> result = new ArrayList<RelationalAtom>();
        for (IFormulaAtom atom : getAtoms()) {
            if (atom.isRelational()) {
                result.add((RelationalAtom) atom);
            }
        }
        return result;
    }

    public void addAtom(IFormulaAtom a) {
        this.atoms.add(a);
    }

    public List<FormulaVariable> getLocalVariables() {
        return localVariables;
    }

    public boolean addLocalVariable(FormulaVariable e) {
        return localVariables.add(e);
    }

    public List<FormulaVariable> getAllVariables() {
        List<FormulaVariable> result = new ArrayList<FormulaVariable>(localVariables);
        if (this.father != null) {
            result.addAll(father.getAllVariables());
        }
        return result;
    }

    public List<VariableEquivalenceClass> getLocalVariableEquivalenceClasses() {
        return localVariableEquivalenceClasses;
    }

    public void setLocalVariableEquivalenceClasses(List<VariableEquivalenceClass> variableEquivalenceClasses) {
        this.localVariableEquivalenceClasses = variableEquivalenceClasses;
    }

    public FormulaWithAdornments getFormulaWithAdornments() {
        return formulaWithAdornments;
    }

    public void setFormulaWithAdornments(FormulaWithAdornments formulaWithAdornments) {
        this.formulaWithAdornments = formulaWithAdornments;
    }

    public boolean isSymmetric() {
        return this.formulaWithAdornments != null;
    }

    public boolean hasEqualityAdornments() {
        return this.formulaWithAdornments.hasEqualityAdornments();
    }

    public boolean hasNegations() {
        return !this.getNegatedSubFormulas().isEmpty();
    }

    public FormulaSampling getFormulaSampling() {
        return formulaSampling;
    }

    public void setFormulaSampling(FormulaSampling formulaSampling) {
        this.formulaSampling = formulaSampling;
    }

    public CrossProductFormulas getCrossProductFormulas() {
        return crossProductFormulas;
    }

    public void setCrossProductFormulas(CrossProductFormulas crossProductFormulas) {
        this.crossProductFormulas = crossProductFormulas;
    }

    public void accept(IFormulaVisitor visitor) {
        visitor.visitPositiveFormula(this);
    }

    public String getId() {
        StringBuilder result = new StringBuilder();
        for (IFormulaAtom atom : atoms) {
            if (atom instanceof RelationalAtom) {
                result.append(((RelationalAtom) atom).getTableName()).append("-");
            } else {
                result.append(atom.toString()).append("-");
            }
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (IFormulaAtom atom : atoms) {
            result.append(atom.toString()).append(", ");
        }
        result.deleteCharAt(result.length() - 1);
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public String toLongString() {
        StringBuilder result = new StringBuilder();
        result.append(toString());
        result.append("\nAtoms:");
        for (IFormulaAtom atom : atoms) {
            result.append("\n\t" + atom.toLongString());
        }
        result.append("  Formula with adornment: ").append(formulaWithAdornments).append("\n");
        result.append("  Is Symmetric: ").append(isSymmetric()).append("\n");
        return result.toString();
    }

    @Override
    public PositiveFormula clone() {
        return new ClonePositiveFormula().clone(this);
    }

    private PositiveFormula superficialClone() {
        try {
            return (PositiveFormula) super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    private static class ClonePositiveFormula {

        private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ClonePositiveFormula.class.getName());

        public PositiveFormula clone(PositiveFormula formula) {
            PositiveFormula clone = formula.superficialClone();
            Map<FormulaVariable, FormulaVariable> variableClones = cloneVariables(formula, clone);
            Map<IFormulaAtom, IFormulaAtom> atomClones = cloneAtoms(formula, clone);
            changeVariablesInAtoms(clone, variableClones);
            changeAtomsInVariables(clone, atomClones);
            cloneEquivalenceClasses(clone, variableClones);
            return clone;
        }

        private Map<FormulaVariable, FormulaVariable> cloneVariables(PositiveFormula formula, PositiveFormula formulaClone) {
            formulaClone.localVariables = new ArrayList<FormulaVariable>();
            Map<FormulaVariable, FormulaVariable> result = new HashMap<FormulaVariable, FormulaVariable>();
            for (FormulaVariable variable : formula.getLocalVariables()) {
                FormulaVariable variableClone = variable.clone();
                formulaClone.localVariables.add(variableClone);
                result.put(variable, variableClone);
            }
            return result;
        }

        private Map<IFormulaAtom, IFormulaAtom> cloneAtoms(PositiveFormula formula, PositiveFormula formulaClone) {
            formulaClone.atoms = new ArrayList<IFormulaAtom>();
            Map<IFormulaAtom, IFormulaAtom> result = new HashMap<IFormulaAtom, IFormulaAtom>();
            for (IFormulaAtom atom : formula.getAtoms()) {
                IFormulaAtom atomClone = atom.clone();
                atomClone.setFormula(formulaClone);
                formulaClone.atoms.add(atomClone);
                result.put(atom, atomClone);
            }
            return result;
        }

        private void changeVariablesInAtoms(PositiveFormula clone, Map<FormulaVariable, FormulaVariable> variableClones) {
            for (IFormulaAtom atomClone : clone.atoms) {
                if (atomClone instanceof RelationalAtom) {
                    continue;
                }
                for (int i = 0; i < atomClone.getVariables().size(); i++) {
                    FormulaVariable variable = atomClone.getVariables().get(i);
                    FormulaVariable variableClone = variableClones.get(variable);
                    atomClone.getVariables().set(i, variableClone);
                    if (atomClone instanceof ComparisonAtom) {
                        ComparisonAtom comparisonAtom = (ComparisonAtom) atomClone;
                        comparisonAtom.getExpression().setVariableDescription(variable.getId(), variableClone);
                    }
                    if (atomClone instanceof BuiltInAtom) {
                        BuiltInAtom builtIn = (BuiltInAtom) atomClone;
                        builtIn.getExpression().setVariableDescription(variable.getId(), variableClone);
                    }
                }
            }
        }

        private void changeAtomsInVariables(PositiveFormula clone, Map<IFormulaAtom, IFormulaAtom> atomClones) {
            for (FormulaVariable variableClone : clone.localVariables) {
                for (int i = 0; i < variableClone.getNonRelationalOccurrences().size(); i++) {
                    IFormulaAtom atom = variableClone.getNonRelationalOccurrences().get(i);
                    IFormulaAtom atomClone = atomClones.get(atom);
                    variableClone.getNonRelationalOccurrences().set(i, atomClone);
                }
            }
        }

        private void cloneEquivalenceClasses(PositiveFormula clone, Map<FormulaVariable, FormulaVariable> variableClones) {
            List<VariableEquivalenceClass> equivalenceClasses = clone.localVariableEquivalenceClasses;
            clone.localVariableEquivalenceClasses = new ArrayList<VariableEquivalenceClass>();
            for (int i = 0; i < equivalenceClasses.size(); i++) {
                VariableEquivalenceClass equivalenceClass = equivalenceClasses.get(i);
                VariableEquivalenceClass equivalenceClassClone = new VariableEquivalenceClass();
                for (FormulaVariable variable : equivalenceClass.getVariables()) {
                    equivalenceClassClone.addVariable(variableClones.get(variable));
                }
                clone.localVariableEquivalenceClasses.add(equivalenceClassClone);
            }
        }

    }

}
