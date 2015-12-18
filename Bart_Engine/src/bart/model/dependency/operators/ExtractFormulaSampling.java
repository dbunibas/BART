package bart.model.dependency.operators;

import bart.model.EGTask;
import bart.model.algebra.operators.BuildAlgebraTreeForPositiveFormula;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.TableAlias;
import bart.model.dependency.FormulaSampling;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaVariableOccurrence;
import bart.model.dependency.IFormulaAtom;
import bart.model.dependency.PositiveFormula;
import bart.model.dependency.RelationalAtom;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractFormulaSampling  {

    private static Logger logger = LoggerFactory.getLogger(ExtractFormulaSampling.class);

    private BuildAlgebraTreeForPositiveFormula builderForPositiveFormula = new BuildAlgebraTreeForPositiveFormula();

    public FormulaSampling extractFormula(PositiveFormula formula, EGTask task) {
        TableAlias tableAlias = selectBiggerTableAlias(formula, task);
        if (logger.isDebugEnabled()) logger.debug("Selected table alias: " + tableAlias);
        PositiveFormula samplingFormula = formula.clone();
        filterAtoms(samplingFormula, tableAlias);
        if (logger.isDebugEnabled()) logger.debug("Sampling formula " + samplingFormula);
        FormulaSampling formulaSampling = new FormulaSampling(formula, samplingFormula, tableAlias);
        IAlgebraOperator operator = builderForPositiveFormula.buildTreeForPositiveFormula(samplingFormula, null, task);
        formulaSampling.setSamplingQuery(operator);
        return formulaSampling;
    }

    private TableAlias selectBiggerTableAlias(PositiveFormula formula, EGTask task) {
        TableAlias maxTableAlias = null;
        long maxSize = 0;
        for (RelationalAtom relationalAtom : formula.getRelationalAtoms()) {
            TableAlias tableAlias = relationalAtom.getTableAlias();
            if (tableAlias.isSource()) {
                continue;
            }
            if (maxTableAlias != null && maxTableAlias.getTableName().equals(tableAlias.getTableName())) {
                continue;
            }
            long size = task.getTarget().getTable(tableAlias.getTableName()).getSize();
            if (maxTableAlias == null || size > maxSize) {
                maxTableAlias = tableAlias;
                maxSize = size;
            }
        }
        return maxTableAlias;
    }

    private void filterAtoms(PositiveFormula formula, TableAlias tableAlias) {
        for (Iterator<IFormulaAtom> it = formula.getAtoms().iterator(); it.hasNext();) {
            IFormulaAtom atom = it.next();
            if (atom.isRelational()) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                if (!relationalAtom.getTableAlias().equals(tableAlias)) {
                    it.remove();
                }
            } else {
                if (!hasAllVariablesInAlias(atom.getVariables(), tableAlias)) {
                    it.remove();
                }
            }
        }
    }

    private boolean hasAllVariablesInAlias(List<FormulaVariable> variables, TableAlias tableAlias) {
        for (FormulaVariable variable : variables) {
            for (FormulaVariableOccurrence relationalOccurrence : variable.getRelationalOccurrences()) {
                if (!relationalOccurrence.getTableAlias().equals(tableAlias)) {
                    return false;
                }
            }
        }
        return true;
    }

}
