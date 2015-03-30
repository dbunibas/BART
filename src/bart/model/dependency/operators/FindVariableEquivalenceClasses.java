package bart.model.dependency.operators;

import bart.model.dependency.*;
import bart.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindVariableEquivalenceClasses {

    private static Logger logger = LoggerFactory.getLogger(FindVariableEquivalenceClasses.class);

    public void findVariableEquivalenceClasses(IFormula formula) {
        FindVariableEquivalenceClassesVisitor visitor = new FindVariableEquivalenceClassesVisitor();
        formula.accept(visitor);
        if (logger.isDebugEnabled()) logger.debug("VariableEquivalenceClasses for formula " + formula.getLocalVariableEquivalenceClasses());
    }

    public void findVariableEquivalenceClasses(Dependency dependency) {
        FindVariableEquivalenceClassesVisitor visitor = new FindVariableEquivalenceClassesVisitor();
        dependency.accept(visitor);
        if (logger.isDebugEnabled()) logger.debug("VariableEquivalenceClasses for dependency " + dependency.getPremise().getLocalVariableEquivalenceClasses());
    }
}

class FindVariableEquivalenceClassesVisitor implements IFormulaVisitor {

    private static Logger logger = LoggerFactory.getLogger(FindVariableEquivalenceClasses.class);

    public void visitDependency(Dependency dependency) {
        dependency.getPremise().accept(this);
    }

    public void visitPositiveFormula(PositiveFormula formula) {
        List<VariableEquivalenceClass> classes = generateVariableEquivalenceClasses(formula);
        formula.setLocalVariableEquivalenceClasses(classes);
    }

    public void visitFormulaWithNegations(FormulaWithNegations formula) {
        formula.getPositiveFormula().accept(this);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            negatedFormula.accept(this);
        }
    }

    public Object getResult() {
        return null;
    }

    List<VariableEquivalenceClass> generateVariableEquivalenceClasses(IFormula formula) {
        List<FormulaVariable> variables = formula.getLocalVariables();
        if (logger.isDebugEnabled()) logger.debug("Local variables: " + variables);
        List<VariableEquivalenceClass> result = initiEquivalenceClasses(variables);
        if (logger.isDebugEnabled()) logger.debug("Singleton equivalence classes: " + result);
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (!(atom instanceof ComparisonAtom)) {
                continue;
            }
            ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
            if (logger.isDebugEnabled()) logger.debug("Checking comparison atom " + comparisonAtom.toLongString());
            if (!comparisonAtom.isVariableEqualityComparison() || !isLocalVariableComparison(comparisonAtom, variables)) {
                continue;
            }
            mergeClasses(comparisonAtom, result);
        }
        return result;
    }

    private List<VariableEquivalenceClass> initiEquivalenceClasses(List<FormulaVariable> variables) {
        List<VariableEquivalenceClass> result = new ArrayList<VariableEquivalenceClass>();
        for (FormulaVariable variable : variables) {
            VariableEquivalenceClass newClass = new VariableEquivalenceClass();
            newClass.addVariable(variable);
            result.add(newClass);
        }
        return result;
    }

    private boolean isLocalVariableComparison(ComparisonAtom comparisonAtom, List<FormulaVariable> localVariables) {
        return localVariables.contains(comparisonAtom.getLeftVariable()) && localVariables.contains(comparisonAtom.getRightVariable());
    }

    private void mergeClasses(ComparisonAtom comparisonAtom, List<VariableEquivalenceClass> equivalenceClasses) {
        if (logger.isDebugEnabled()) logger.debug("Merging classes for comparison atom " + comparisonAtom + "\n " + equivalenceClasses);
        VariableEquivalenceClass firstClass = DependencyUtility.findEquivalenceClassForVariable(comparisonAtom.getLeftVariable(), equivalenceClasses);
        VariableEquivalenceClass secondClass = DependencyUtility.findEquivalenceClassForVariable(comparisonAtom.getRightVariable(), equivalenceClasses);
        if(firstClass == secondClass){
            return;
        }
        firstClass.addVariables(secondClass.getVariables());
        equivalenceClasses.remove(secondClass);
    }

}
