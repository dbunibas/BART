package bart.model.dependency.operators;

import bart.model.dependency.*;
import bart.utility.BartUtility;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.ParserException;

public class CheckVariablesInExpressions {

    public void checkVariables(Dependency dependency) {
        CheckFormulaVariablesVisitor visitor = new CheckFormulaVariablesVisitor();
        dependency.accept(visitor);
    }
}

class CheckFormulaVariablesVisitor implements IFormulaVisitor {

    private static Logger logger = LoggerFactory.getLogger(CheckVariablesInExpressions.class);

    private Dependency dependency;

    public void visitDependency(Dependency dependency) {
        this.dependency = dependency;
        dependency.getPremise().accept(this);
        dependency.getConclusion().accept(this);
    }

    public void visitPositiveFormula(PositiveFormula formula) {
        for (IFormulaAtom atom : formula.getAtoms()) {
            if ((atom instanceof RelationalAtom)) {
                continue;
            }
            List<String> variableIds;
            if (atom instanceof ComparisonAtom) {
                ComparisonAtom comparison = (ComparisonAtom) atom;
                variableIds = new ArrayList<String>();
                BartUtility.addIfNotNull(variableIds, comparison.getLeftVariableId());
                BartUtility.addIfNotNull(variableIds, comparison.getRightVariableId());
            } else {
                variableIds = atom.getExpression().getVariables();
            }
            for (String variableId : variableIds) {
                FormulaVariable variable = findVariableInList(variableId, atom.getFormula().getAllVariables());
                if (variable == null) {
                    variable = findVariableInList(variableId, dependency.getPremise().getLocalVariables());
                    if (variable == null) {
                        throw new ParserException("Unable to find variable " + variableId + " in expression " + atom.getExpression() + " in formula " + formula.getId());
                    }
                }
                atom.getExpression().changeVariableDescription(variableId, variable);
                atom.addVariable(variable);
                variable.addNonRelationalOccurrence(atom);
                if (logger.isDebugEnabled()) logger.debug("Adding non relational occurrence to variable " + variable + " in atom " + atom);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Variables found in dependency: " + dependency.getId() + "\n" + BartUtility.printVariablesWithOccurrences(dependency.getPremise().getLocalVariables()) + "\n" + BartUtility.printVariablesWithOccurrences(dependency.getConclusion().getLocalVariables()));
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

    private FormulaVariable findVariableInList(String variableId, List<FormulaVariable> variables) {
        for (FormulaVariable variable : variables) {
            if (variable.getId().equals(variableId)) {
                return variable;
            }
        }
        return null;
    }

}
