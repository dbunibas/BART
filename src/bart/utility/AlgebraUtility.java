package bart.utility;

import bart.BartConstants;
import bart.model.EGTask;
import speedy.model.algebra.operators.GenerateTupleFromTuplePair;
import speedy.model.database.*;
import bart.model.dependency.*;
import bart.persistence.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.nfunk.jep.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.utility.comparator.StringComparator;

@SuppressWarnings("unchecked")
public class AlgebraUtility {

    private static Logger logger = LoggerFactory.getLogger(AlgebraUtility.class);
    private static GenerateTupleFromTuplePair tupleMerger = new GenerateTupleFromTuplePair();

    public static void addIfNotContained(List list, Object object) {
        BartUtility.addIfNotContained(list, object);
    }

    public static IValue getCellValue(Tuple tuple, AttributeRef attributeRef) {
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttributeRef().equals(attributeRef)) {
                return cell.getValue();
            }
        }
        throw new IllegalArgumentException("Unable to find attribute " + attributeRef + " in tuple " + tuple.toStringWithOIDAndAlias());
    }

    public static boolean contains(Tuple tuple, AttributeRef attributeRef) {
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttributeRef().equals(attributeRef)) {
                return true;
            }
        }
        return false;
    }

    public static List<Object> getTupleValuesExceptOIDs(Tuple tuple) {
        List<Object> values = new ArrayList<Object>();
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttribute().equals(BartConstants.OID)) {
                continue;
            }
            IValue attributeValue = cell.getValue();
            values.add(attributeValue.getPrimitiveValue().toString());
        }
        return values;
    }

    public static List<Object> getNonOidTupleValues(Tuple tuple) {
        List<Object> values = new ArrayList<Object>();
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttribute().equals(BartConstants.OID)) {
                continue;
            }
            IValue attributeValue = cell.getValue();
            values.add(attributeValue.getPrimitiveValue());
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public static boolean equalLists(List list1, List list2) {
        return (list1.containsAll(list2) && list2.containsAll(list1));
    }

    public static boolean areEqualExcludingOIDs(Tuple t1, Tuple t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        return equalLists(getTupleValuesExceptOIDs(t1), getTupleValuesExceptOIDs(t2));
    }

    public static void removeDuplicates(List result) {
        if (result.isEmpty()) {
            return;
        }
        Collections.sort(result, new StringComparator());
        Iterator tupleIterator = result.iterator();
        String prevValues = tupleIterator.next().toString();
        while (tupleIterator.hasNext()) {
            Object currentTuple = tupleIterator.next();
            String currentValues = currentTuple.toString();
            if (prevValues.equals(currentValues)) {
                tupleIterator.remove();
            } else {
                prevValues = currentValues;
            }
        }
    }

    public static List<TableAlias> findAliasesForAtom(IFormulaAtom atom) {
        List<TableAlias> result = new ArrayList<TableAlias>();
        for (FormulaVariable variable : atom.getVariables()) {
            for (TableAlias tableAlias : findAliasesForVariable(variable)) {
                if (!result.contains(tableAlias)) {
                    result.add(tableAlias);
                }
            }
        }
        return result;
    }

    public static List<TableAlias> findAliasesForVariable(FormulaVariable variable) {
        List<TableAlias> result = new ArrayList<TableAlias>();
        for (FormulaVariableOccurrence occurrence : variable.getRelationalOccurrences()) {
            TableAlias tableAlias = occurrence.getAttributeRef().getTableAlias();
            if (!result.contains(tableAlias)) {
                result.add(tableAlias);
            }
        }
        return result;
    }

    public static List<TableAlias> findAliasesForFormula(PositiveFormula formula) {
        List<TableAlias> result = new ArrayList<TableAlias>();
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (atom instanceof RelationalAtom) {
                result.add(((RelationalAtom) atom).getTableAlias());
            }
        }
        return result;
    }

    public static FormulaVariable findVariable(String variableId, List<FormulaVariable> variables) {
        for (FormulaVariable formulaVariable : variables) {
            if (formulaVariable.getId().equals(variableId)) {
                return formulaVariable;
            }
        }
        return null;
    }

    public static String getPlaceholderId(FormulaVariable variable) {
        return "$$" + variable.getId() + "";
    }

    public static boolean isPlaceholder(Variable jepVariable) {
        return jepVariable.getDescription().toString().startsWith("$$");
//        return jepVariable.getDescription().toString().startsWith("$$") &&
//                jepVariable.getDescription().toString().endsWith("#");
    }

    @SuppressWarnings("unchecked")
    public static boolean verifyComparisonsOnTuplePair(Tuple firstTuple, Tuple secondTuple, IFormula formula, EGTask task) {
        Tuple mergedTuple = tupleMerger.generateTuple(firstTuple, secondTuple);
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (atom.isRelational()) {
                continue;
            }
            if (atom.isBuiltIn()) {
                throw new UnsupportedOperationException("BuiltIn are not supported");
            }
            ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
            if (!comparisonAtom.isVariableComparison()) {
                continue;
            }
            FormulaVariable leftVariable = comparisonAtom.getLeftVariable();
            FormulaVariable rightVariable = comparisonAtom.getRightVariable();
            AttributeRef firstVariableRelationalOccurrence = DependencyUtility.getFirstAttributeForVariable(leftVariable, mergedTuple);
            AttributeRef secondVariableRelationalOccurrence = DependencyUtility.getFirstAttributeForVariable(rightVariable, mergedTuple);
            String firstType = BartUtility.getAttribute(firstVariableRelationalOccurrence, task).getType();
            String secondType = BartUtility.getAttribute(secondVariableRelationalOccurrence, task).getType();
            if (areNotCompatible(firstType, secondType)) {
                throw new IllegalArgumentException("Attribute types for comparison " + comparisonAtom + " are not compatible: " + firstType + ", " + secondType);
            }
            IValue firstValue = mergedTuple.getCell(firstVariableRelationalOccurrence).getValue();
            IValue secondValue = mergedTuple.getCell(secondVariableRelationalOccurrence).getValue();
            String operator = comparisonAtom.getOperator();
            if (BartConstants.EQUAL.equals(operator)) {
                if (!firstValue.toString().equals(secondValue.toString())) {
                    if (logger.isInfoEnabled()) logger.info("Comparison " + comparisonAtom + " is violated in tuple " + mergedTuple);
                    return false;
                }
            } else if (BartConstants.NOT_EQUAL.equals(operator)) {
                if (firstValue.toString().equals(secondValue.toString())) {
                    if (logger.isInfoEnabled()) logger.info("Comparison " + comparisonAtom + " is violated in tuple " + mergedTuple);
                    return false;
                }
            } else if (BartConstants.LOWER.equals(operator)) {
//                if (getTypedValue(firstValue, firstType) >= getTypedValue(secondValue, secondType)) {
                if (getTypedValue(firstValue, firstType).compareTo(getTypedValue(secondValue, secondType)) >= 0) {
                    if (logger.isInfoEnabled()) logger.info("Comparison " + comparisonAtom + " is violated in tuple " + mergedTuple);
                    return false;
                }
            } else if (BartConstants.LOWER_EQ.equals(operator)) {
//                if (getTypedValue(firstValue, firstType) > getTypedValue(secondValue, secondType)) {
                if (getTypedValue(firstValue, firstType).compareTo(getTypedValue(secondValue, secondType)) > 0) {
                    if (logger.isInfoEnabled()) logger.info("Comparison " + comparisonAtom + " is violated in tuple " + mergedTuple);
                    return false;
                }
            } else if (BartConstants.GREATER.equals(operator)) {
//                if (getTypedValue(firstValue, firstType) <= getTypedValue(secondValue, secondType)) {
                if (getTypedValue(firstValue, firstType).compareTo(getTypedValue(secondValue, secondType)) <= 0) {
                    if (logger.isInfoEnabled()) logger.info("Comparison " + comparisonAtom + " is violated in tuple " + mergedTuple);
                    return false;
                }
            } else if (BartConstants.GREATER_EQ.equals(operator)) {
//                if (getTypedValue(firstValue, firstType) < getTypedValue(secondValue, secondType)) {
                if (getTypedValue(firstValue, firstType).compareTo(getTypedValue(secondValue, secondType)) < 0) {
                    if (logger.isInfoEnabled()) logger.info("Comparison " + comparisonAtom + " is violated in tuple " + mergedTuple);
                    return false;
                }
            } else {
                throw new UnsupportedOperationException("Operator " + operator + " is not supported");
            }
        }
        return true;
    }

    private static Comparable getTypedValue(IValue value, String type) {
        if (type.equals(Types.LONG) || type.equals(Types.DOUBLE) || type.equals(Types.INTEGER)) {
            return Double.parseDouble(value.toString());
        }
        return value.toString();
    }

    private static boolean areNotCompatible(String firstType, String secondType) {
        return !firstType.equals(secondType);
    }

}
