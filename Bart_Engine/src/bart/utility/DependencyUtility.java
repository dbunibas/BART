package bart.utility;

import bart.BartConstants;
import speedy.model.database.*;
import bart.model.dependency.*;
import bart.model.errorgenerator.VioGenQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependencyUtility {

    private static Logger logger = LoggerFactory.getLogger(DependencyUtility.class);

    public static Set<AttributeRef> extractAttributesForVariables(List<FormulaVariable> variables) {
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        for (FormulaVariable variable : variables) {
            result.addAll(extractAttributesForVariable(variable));
        }
        return result;
    }

    public static Set<AttributeRef> extractAttributesForVariable(FormulaVariable variable) {
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        if (variable == null) {
            return result;
        }
        for (FormulaVariableOccurrence variableOccurrence : variable.getRelationalOccurrences()) {
            if (variableOccurrence.getAttributeRef().isSource()) {
                continue;
            }
            result.add(variableOccurrence.getAttributeRef());
        }
        return result;
    }

    public static List<AttributeRef> getUniversalAttributesInPremise(List<FormulaVariable> universalVariables) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (FormulaVariable formulaVariable : universalVariables) {
            BartUtility.addIfNotContained(result, formulaVariable.getRelationalOccurrences().get(0).getAttributeRef());
        }
        return result;
    }

    public static AttributeRef findFirstOccurrenceInFormula(IFormula formula, List<FormulaVariableOccurrence> occurrences) {
        List<TableAlias> aliasesInFormula = AlgebraUtility.findAliasesForFormula(formula.getPositiveFormula());
        for (FormulaVariableOccurrence occurrence : occurrences) {
            AttributeRef attribute = occurrence.getAttributeRef();
            if (aliasesInFormula.contains(attribute.getTableAlias())) {
                return attribute;
            }
        }
        return null;
    }

    public static List<AttributeRef> findQueriedAttributes(Dependency dependency) {
        if (logger.isTraceEnabled()) logger.trace("Searching query attributes for dependency: \n" + dependency);
        List<AttributeRef> queriedAttributes = new ArrayList<AttributeRef>();
        for (FormulaVariable variable : dependency.getPremise().getLocalVariables()) {
            if (logger.isTraceEnabled()) logger.trace("Inspecting variable: " + variable);
            if (hasSingleOccurrence(variable)) {
                continue;
            }
            for (FormulaVariableOccurrence occurrence : variable.getRelationalOccurrences()) {
                if (logger.isTraceEnabled()) logger.trace("Inspecting occurrence: " + occurrence);
                AttributeRef attribute = occurrence.getAttributeRef();
                if (attribute.getTableAlias().isSource()) {
                    continue;
                }
                AttributeRef unaliasedAttribute = DependencyUtility.unAlias(attribute);
                BartUtility.addIfNotContained(queriedAttributes, unaliasedAttribute);
            }
        }
        if (logger.isTraceEnabled()) logger.trace("Result: " + queriedAttributes);
        return queriedAttributes;
    }

    private static boolean hasSingleOccurrence(FormulaVariable variable) {
        if (logger.isTraceEnabled()) logger.trace("Occurrences for variable: " + variable.toLongString());
        int relationalPremiseOccurrences = variable.getRelationalOccurrences().size();
        int nonRelationalOccurrences = variable.getNonRelationalOccurrences().size();
        return relationalPremiseOccurrences + nonRelationalOccurrences <= 1;
    }

    public static List<AttributeRef> findTargetJoinAttributes(IFormula formula) {
        List<VariableEquivalenceClass> relevantVariableClasses = DependencyUtility.findJoinVariablesInTarget(formula);
        List<AttributeRef> targetJoinAttributes = new ArrayList<AttributeRef>();
        for (VariableEquivalenceClass variableEquivalenceClass : relevantVariableClasses) {
            for (FormulaVariableOccurrence occurrence : DependencyUtility.findTargetOccurrences(variableEquivalenceClass)) {
                targetJoinAttributes.add(occurrence.getAttributeRef());
            }
        }
        return targetJoinAttributes;
    }

    public static List<VariableEquivalenceClass> findJoinVariablesInTarget(IFormula formula) {
        List<VariableEquivalenceClass> result = new ArrayList<VariableEquivalenceClass>();
        for (VariableEquivalenceClass variableEquivalenceClass : formula.getLocalVariableEquivalenceClasses()) {
            List<FormulaVariableOccurrence> targetOccurrences = findTargetOccurrences(variableEquivalenceClass);
            List<FormulaVariableOccurrence> positiveOccurrences = findPositiveOccurrences(formula.getPositiveFormula(), variableEquivalenceClass.getRelationalOccurrences());
            if (positiveOccurrences.size() > 1 && !targetOccurrences.isEmpty()) {
                result.add(variableEquivalenceClass);
            }
        }
        return result;
    }

    public static List<FormulaVariableOccurrence> findTargetOccurrences(VariableEquivalenceClass variableEquivalenceClass) {
        List<FormulaVariableOccurrence> result = new ArrayList<FormulaVariableOccurrence>();
        for (FormulaVariableOccurrence occurrence : variableEquivalenceClass.getRelationalOccurrences()) {
            if (occurrence.getAttributeRef().getTableAlias().isSource()) {
                continue;
            }
            result.add(occurrence);
        }
        return result;
    }

    private static List<FormulaVariableOccurrence> findPositiveOccurrences(PositiveFormula positiveFormula, List<FormulaVariableOccurrence> premiseRelationalOccurrences) {
        List<FormulaVariableOccurrence> result = new ArrayList<FormulaVariableOccurrence>();
        for (FormulaVariableOccurrence formulaVariableOccurrence : premiseRelationalOccurrences) {
            if (containsAlias(positiveFormula, formulaVariableOccurrence.getTableAlias())) {
                result.add(formulaVariableOccurrence);
            }
        }
        return result;
    }

    public static boolean containsAlias(PositiveFormula positiveFormula, TableAlias tableAlias) {
        for (IFormulaAtom formulaAtom : positiveFormula.getAtoms()) {
            if (formulaAtom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) formulaAtom;
                if (relationalAtom.getTableAlias().equals(tableAlias)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static FormulaVariable findVariableInList(FormulaVariableOccurrence occurrence, List<FormulaVariable> variables) {
        for (FormulaVariable formulaVariable : variables) {
            if (formulaVariable.getId().equals(occurrence.getVariableId())) {
                return formulaVariable;
            }
        }
        return null;
    }

    public static AttributeRef unAlias(AttributeRef attribute) {
        TableAlias unaliasedTable = new TableAlias(attribute.getTableName(), attribute.getTableAlias().isSource());
        return new AttributeRef(unaliasedTable, attribute.getName());
    }

    public static TableAlias unAlias(TableAlias alias) {
        TableAlias unaliasedTable = new TableAlias(alias.getTableName(), alias.isSource());
        return unaliasedTable;
    }

    public static VariableEquivalenceClass findEquivalenceClassForVariable(FormulaVariable variable, List<VariableEquivalenceClass> equivalenceClasses) {
        for (VariableEquivalenceClass equivalenceClass : equivalenceClasses) {
            if (equivalenceClass.contains(variable)) {
                return equivalenceClass;
            }
        }
        throw new IllegalArgumentException("Unable to find equivalence class for variable " + variable + "\n" + equivalenceClasses);
    }

    public static Set<Cell> findCellsForAttributes(List<FormulaVariableOccurrence> relationalOccurrence, Tuple tuple) {
        Set<Cell> result = new HashSet<Cell>();
        for (FormulaVariableOccurrence formulaVariableOccurrence : relationalOccurrence) {
            AttributeRef attribute = formulaVariableOccurrence.getAttributeRef();
            Cell cell = tuple.getCell(attribute);
            TupleOID originalOid = new TupleOID(BartUtility.getOriginalOid(tuple, attribute.getTableAlias()));
            cell = new Cell(originalOid, cell.getAttributeRef(), cell.getValue());
            result.add(cell);
        }
        return result;
    }

    public static boolean containsNoAlias(Cell vioGenCell, Set<Cell> cellsForEquivalenceClass) {
        for (Cell cell : cellsForEquivalenceClass) {
            if (cell.equalsModuloAlias(vioGenCell)) {
                return true;
            }
        }
        return false;
    }

    public static String invertOperator(String operator) {
        if (operator.equals(BartConstants.EQUAL)) {
            return BartConstants.NOT_EQUAL;
        }
        if (operator.equals(BartConstants.NOT_EQUAL)) {
            return BartConstants.EQUAL;
        }
        if (operator.equals(BartConstants.GREATER)) {
            return BartConstants.LOWER_EQ;
        }
        if (operator.equals(BartConstants.LOWER)) {
            return BartConstants.GREATER_EQ;
        }
        if (operator.equals(BartConstants.GREATER_EQ)) {
            return BartConstants.LOWER;
        }
        if (operator.equals(BartConstants.LOWER_EQ)) {
            return BartConstants.GREATER;
        }
        throw new IllegalArgumentException("Unkown operator " + operator);
    }

    public static AttributeRef getFirstOIDAttribute(List<AttributeRef> attributes) {
        for (AttributeRef attribute : attributes) {
            if (attribute.getName().equals(BartConstants.OID)) {
                return (attribute);
            }
        }
        return null;
    }

    public static boolean hasNumericComparison(IFormula formula) {
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (atom.isRelational() || atom.isBuiltIn()) {
                continue;
            }
            ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
            if (!comparisonAtom.isEqualityComparison() && !comparisonAtom.isInequalityComparison()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasBuiltIns(IFormula formula) {
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (atom.isBuiltIn()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCrossProduct(IFormula formula) {
        if (formula.isSymmetric()) {
            if (hasOnlyVariableInequalities(formula)) {
                return true;
            } else {
                return false;
            }
        } else if (!formula.isSymmetric()) {
            CrossProductFormulas crossProductFormula = formula.getCrossProductFormulas();
            if (crossProductFormula == null || crossProductFormula.getTableAliasInCrossProducts().size() == 1) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasOnlyVariableInequalities(VioGenQuery vioGenQuery) {
        return hasOnlyVariableInequalities(vioGenQuery.getFormula());
    }

    public static boolean hasOnlyVariableInequalities(IFormula formula) {
        return hasVariableInequalities(formula) && !hasVariableEqualities(formula);
    }

    private static boolean hasVariableInequalities(IFormula formula) {
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (!atom.isComparison()) {
                continue;
            }
            ComparisonAtom comparison = (ComparisonAtom) atom;
            if (comparison.isVariableInequalityComparison()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasVariableEqualities(IFormula formula) {
        for (VariableEquivalenceClass variableEquivalenceClass : formula.getLocalVariableEquivalenceClasses()) {
            if (variableEquivalenceClass.getVariables().size() > 1) {
                return true;
            }
        }
        return false;
    }

    public static Collection<FormulaVariable> findVariablesInAtom(IFormulaAtom atom) {
        if (atom.isComparison() || atom.isBuiltIn()) {
            return atom.getVariables();
        }
        Set<FormulaVariable> result = new HashSet<FormulaVariable>();
        RelationalAtom relationalAtom = (RelationalAtom) atom;
        for (FormulaAttribute formulaAttribute : relationalAtom.getAttributes()) {
            if (formulaAttribute.getValue().isVariable()) {
                FormulaVariableOccurrence variableOccurrence = (FormulaVariableOccurrence) formulaAttribute.getValue();
                result.add(DependencyUtility.findVariableInList(variableOccurrence, atom.getFormula().getLocalVariables()));
            }
        }
        return result;
    }

    public static IValue getValueForVariable(FormulaVariable variable, Tuple tuple) {
        return tuple.getCell(variable.getRelationalOccurrences().get(0).getAttributeRef()).getValue();
    }

    public static AttributeRef getFirstAttributeForVariable(FormulaVariable variable, Tuple tuple) {
        return variable.getRelationalOccurrences().get(0).getAttributeRef();
    }

    public static Set<TableAlias> extractTableAliasInFormula(IFormula formula) {
        Set<TableAlias> result = new HashSet<TableAlias>();
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (!atom.isRelational()) {
                continue;
            }
            result.add(((RelationalAtom) atom).getTableAlias());
        }
        return result;
    }

    public static Set<AttributeRef> findRelevantAttributes(FormulaWithAdornments formulaWithAdornments) {
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        for (FormulaVariable variableWithAdornment : formulaWithAdornments.getAdornments().keySet()) {
            for (FormulaVariableOccurrence relationalOccurrence : variableWithAdornment.getRelationalOccurrences()) {
                result.add(relationalOccurrence.getAttributeRef());
            }
        }
        result.addAll(findRelevantAttributes(formulaWithAdornments.getFormula()));
        return result;
    }

    public static Set<AttributeRef> findRelevantAttributes(IFormula formula) {
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        for (FormulaVariable formulaVariable : formula.getLocalVariables()) {
            int numberOfOccurrences = formulaVariable.getNonRelationalOccurrences().size();
            numberOfOccurrences += formulaVariable.getRelationalOccurrences().size();
            if (numberOfOccurrences > 1) {
                for (FormulaVariableOccurrence relationalOccurrence : formulaVariable.getRelationalOccurrences()) {
                    result.add(relationalOccurrence.getAttributeRef());
                }
            }
        }
        return result;
    }

}
