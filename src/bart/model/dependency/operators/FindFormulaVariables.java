package bart.model.dependency.operators;

import bart.utility.BartUtility;
import speedy.model.database.AttributeRef;
import bart.model.dependency.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindFormulaVariables {

    public void findVariables(Dependency dependency, List<String> sourceTables, List<String> authoritativeSources) {
        FindFormulaVariablesVisitor visitor = new FindFormulaVariablesVisitor(sourceTables, authoritativeSources);
        dependency.accept(visitor);
    }
}

class FindFormulaVariablesVisitor implements IFormulaVisitor {

    private static Logger logger = LoggerFactory.getLogger(FindFormulaVariablesVisitor.class);

    private IFormula premise;
    private List<String> sourceTables;
    private List<String> authoritativeSources;

    public FindFormulaVariablesVisitor(List<String> sourceTables, List<String> authoritativeSources) {
        this.sourceTables = sourceTables;
        this.authoritativeSources = authoritativeSources;
    }

    public void visitDependency(Dependency dependency) {
        dependency.getPremise().accept(this);
        this.premise = dependency.getPremise();
        dependency.getConclusion().accept(this);
        if (logger.isDebugEnabled()) logger.debug("Variables found in dependency: " + dependency.getId() + "\n" + BartUtility.printVariablesWithOccurrences(dependency.getPremise().getLocalVariables()) + "\n" + BartUtility.printVariablesWithOccurrences(dependency.getConclusion().getLocalVariables()));
    }

    public void visitPositiveFormula(PositiveFormula formula) {
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (!(atom instanceof RelationalAtom)) {
                continue;
            }
            for (FormulaAttribute attribute : ((RelationalAtom) atom).getAttributes()) {
                if (!attribute.getValue().isVariable()) {
                    continue;
                }
                FormulaVariableOccurrence occurrence = (FormulaVariableOccurrence) attribute.getValue();
                FormulaVariable variable = findVariable(occurrence.getVariableId(), formula);
                if (variable == null) {
                    variable = new FormulaVariable(occurrence.getVariableId());
                    formula.addLocalVariable(variable);
                }
                variable.addRelationalOccurrence(occurrence);
                AttributeRef attributeRef = occurrence.getAttributeRef();
                if (sourceTables.contains(attributeRef.getTableName())) {
                    attributeRef.getTableAlias().setSource(true);
                }
                if (authoritativeSources.contains(attributeRef.getTableName())) {
                    attributeRef.getTableAlias().setAuthoritative(true);
                }
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Variables found in formula: " + formula.getId() + "\n" + BartUtility.printVariablesWithOccurrences(formula.getLocalVariables()));
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

    private FormulaVariable findVariable(String variableId, IFormula formula) {
        FormulaVariable variable = findVariableInList(variableId, formula.getLocalVariables());
        if (variable != null) {
            return variable;
        }
        if (formula.getFather() == null) {
            return null;
        }
        return findVariable(variableId, formula.getFather());
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
