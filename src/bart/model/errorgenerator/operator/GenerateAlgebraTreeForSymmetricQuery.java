package bart.model.errorgenerator.operator;

import bart.BartConstants;
import bart.exceptions.ErrorGeneratorException;
import bart.model.EGTask;
import bart.model.algebra.operators.BuildAlgebraTreeForSymmetricFormula;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.AttributeRef;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaWithAdornments;
import bart.model.dependency.PositiveFormula;
import bart.model.errorgenerator.EquivalenceClassQuery;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateAlgebraTreeForSymmetricQuery {

    private static Logger logger = LoggerFactory.getLogger(GenerateAlgebraTreeForSymmetricQuery.class);
    private BuildAlgebraTreeForSymmetricFormula symmetricQueryBuilder = new BuildAlgebraTreeForSymmetricFormula();

    @SuppressWarnings("unchecked")
    public void initializeQuery(FormulaWithAdornments formulaWithAdornment, EGTask task) {
        List<FormulaVariable> variablesWithEqAdornment = new ArrayList<FormulaVariable>();
        List<FormulaVariable> variablesWithIneqAdornment = new ArrayList<FormulaVariable>();
        partitionVariables(formulaWithAdornment, variablesWithEqAdornment, variablesWithIneqAdornment);
        if (logger.isDebugEnabled()) logger.debug("Variables with eqAdornment: " + variablesWithEqAdornment);
        if (logger.isDebugEnabled()) logger.debug("Variables with IneqAdornment: " + variablesWithIneqAdornment);
        List<EquivalenceClassQuery> equivalenceClassQueries = new ArrayList<EquivalenceClassQuery>();
        PositiveFormula symmetricFormula = formulaWithAdornment.getFormula().getPositiveFormula();
        List<AttributeRef> equalityAttributes = new ArrayList<AttributeRef>(DependencyUtility.extractAttributesForVariables(variablesWithEqAdornment));
        if (variablesWithIneqAdornment.isEmpty()) {
            IAlgebraOperator operator = symmetricQueryBuilder.buildTree(symmetricFormula, equalityAttributes, Collections.EMPTY_LIST, task);
            EquivalenceClassQuery equivalenceClassQuery = new EquivalenceClassQuery(operator, equalityAttributes, Collections.EMPTY_LIST);
            equivalenceClassQueries.add(equivalenceClassQuery);
        } else {
            List<List<AttributeRef>> inequalityAttributes = new ArrayList<List<AttributeRef>>();
            for (FormulaVariable inequalityVariable : variablesWithIneqAdornment) {
                List<AttributeRef> variableSet = new ArrayList<AttributeRef>(DependencyUtility.extractAttributesForVariable(inequalityVariable));
                inequalityAttributes.add(variableSet);
            }
            IAlgebraOperator operator = symmetricQueryBuilder.buildTree(symmetricFormula, equalityAttributes, inequalityAttributes, task);
            EquivalenceClassQuery equivalenceClassQuery = new EquivalenceClassQuery(operator, equalityAttributes, inequalityAttributes);
            equivalenceClassQueries.add(equivalenceClassQuery);
//            int inequalities = 0;
//            for (FormulaVariable inequalityVariable : variablesWithIneqAdornment) {
////                inequalities++;
//                List<AttributeRef> inequalityAttributes = new ArrayList<AttributeRef>(DependencyUtility.extractAttributesForVariable(inequalityVariable));
//                IAlgebraOperator operator = symmetricQueryBuilder.buildTree(symmetricFormula, equalityAttributes, inequalityAttributes, task);
//                EquivalenceClassQuery equivalenceClassQuery = new EquivalenceClassQuery(operator, equalityAttributes, inequalityAttributes, inequalityVariable);
//                equivalenceClassQueries.add(equivalenceClassQuery);
//                if (task.getConfiguration().getMaxNumberOfInequalitiesInSymmetricQueries() != null && task.getConfiguration().getMaxNumberOfInequalitiesInSymmetricQueries() >= inequalities) {
//                    break;
//                }
//            }
        }
        if (logger.isDebugEnabled()) logger.debug("EquivalenceClassQueries:\n" + BartUtility.printCollection(equivalenceClassQueries));
        formulaWithAdornment.setEquivalenceClassQueries(equivalenceClassQueries);
    }

    private void partitionVariables(FormulaWithAdornments formulaWithAdornment, List<FormulaVariable> variablesWithEqAdornment, List<FormulaVariable> variablesWithIneqAdornment) {
        for (FormulaVariable formulaVariable : formulaWithAdornment.getAdornments().keySet()) {
            String adornment = formulaWithAdornment.getAdornments().get(formulaVariable);
            if (adornment.equals(BartConstants.EQUAL)) {
                variablesWithEqAdornment.add(formulaVariable);
            } else if (adornment.equals(BartConstants.NOT_EQUAL)) {
                variablesWithIneqAdornment.add(formulaVariable);
            } else {
                throw new ErrorGeneratorException("Incorrect adornment: " + adornment + "\n" + formulaWithAdornment);
            }
        }
    }
}
