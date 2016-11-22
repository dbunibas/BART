package bart.model.errorgenerator.operator;

import bart.BartConstants;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.VioGenQueryConfiguration;
import bart.model.algebra.operators.BuildAlgebraTree;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Limit;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.operators.IRunQuery;
import bart.model.dependency.ComparisonAtom;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaWithAdornments;
import bart.model.dependency.IFormula;
import bart.model.dependency.VariableEquivalenceClass;
import bart.model.errorgenerator.VioGenQueryCellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.EquivalenceClass;
import bart.model.errorgenerator.EquivalenceClassQuery;
import bart.model.errorgenerator.VioGenQuery;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteVioGenQueryUtility {

    private static Logger logger = LoggerFactory.getLogger(ExecuteVioGenQueryUtility.class);
    private static BuildAlgebraTree treeBuilder = new BuildAlgebraTree();

    public static boolean queryIsEmpty(VioGenQuery vioGenQuery, EGTask task) {
        if (DependencyUtility.hasOnlyVariableInequalities(vioGenQuery)) {
            return false;
        }
        IAlgebraOperator root = treeBuilder.buildTreeForPremise(vioGenQuery.getFormula(), task);
        Limit limit = new Limit(1);
        limit.addChild(root);
        root = limit;
        if (logger.isInfoEnabled()) logger.debug("IsEmptyQuery: " + root);
        ITupleIterator it = OperatorFactory.getInstance().getQueryRunner(task).run(root, task.getSource(), task.getTarget());
        boolean hasNext = it.hasNext();
        it.close();
        return !hasNext;
    }

    public static void executeEquivalenceClassQuery(List<EquivalenceClassQuery> subQueries, IRunQuery queryRunner, IDatabase source, IDatabase target) {
        for (EquivalenceClassQuery subQuery : subQueries) {
            ITupleIterator it = queryRunner.run(subQuery.getQuery(), source, target);
            subQuery.setIterator(it);
        }
    }

    public static void closeIterators(List<EquivalenceClassQuery> subQueries) {
        for (EquivalenceClassQuery subQuery : subQueries) {
            subQuery.getIterator().close();
        }
    }

    public static Set<FormulaVariable> getInequalityVariables(FormulaWithAdornments formulaWithAdornment) {
        Set<FormulaVariable> result = new HashSet<FormulaVariable>();
        for (FormulaVariable formulaVariable : formulaWithAdornment.getAdornments().keySet()) {
            if (formulaWithAdornment.getAdornments().get(formulaVariable).equals(BartConstants.NOT_EQUAL)) {
                result.add(formulaVariable);
            }
        }
        return result;
    }

    public static Collection<Tuple> intersectTuples(List<EquivalenceClass> equivalenceClasses) {
        if (logger.isInfoEnabled()) logger.info("Intersecting tuples in equivalence classes");
        for (EquivalenceClass equivalenceClass : equivalenceClasses) {
            if (logger.isInfoEnabled()) logger.info("EC: " + equivalenceClass.getTuples().size() + " tuples");
        }
        List<EquivalenceClass> equivalenceClassesSorted = new ArrayList<EquivalenceClass>();
        equivalenceClassesSorted.addAll(equivalenceClasses);
        Collections.sort(equivalenceClassesSorted, new EquivalenceClassSizeComparator());
        EquivalenceClass firstEquivalenceClass = equivalenceClassesSorted.get(0);
        Map<String, Tuple> result = new HashMap<String, Tuple>();
        for (Tuple tuple : firstEquivalenceClass.getTuples()) {
            String tupleOIDs = generateTupleOIDs(tuple, firstEquivalenceClass.getEqualityAttributes());
            result.put(tupleOIDs, tuple);
        }
        List<Set<String>> otherEquivalenceClassesOIDs = new ArrayList<Set<String>>();
        for (int i = 1; i < equivalenceClasses.size(); i++) {
            Set<String> oids = new HashSet<String>();
            EquivalenceClass otherEquivalenceClass = equivalenceClasses.get(i);
            for (Tuple tuple : otherEquivalenceClass.getTuples()) {
                String tupleOIDs = generateTupleOIDs(tuple, firstEquivalenceClass.getEqualityAttributes());
                oids.add(tupleOIDs);
            }
            otherEquivalenceClassesOIDs.add(oids);
        }
        for (Iterator<String> iterator = result.keySet().iterator(); iterator.hasNext();) {
            String tupleOIDs = iterator.next();
            if (!BartUtility.isContainedInAll(tupleOIDs, otherEquivalenceClassesOIDs)) {
                iterator.remove();
            }
        }
        if (logger.isInfoEnabled()) logger.info("Intersected! Resulting tuples " + result.values().size());
        return result.values();
    }

    public static String generateTupleOIDs(Tuple tuple, List<AttributeRef> equalityAttributes) {
        StringBuilder sb = new StringBuilder();
        for (AttributeRef equalityAttribute : equalityAttributes) {
            TupleOID oid = new TupleOID(BartUtility.getOriginalOid(tuple, equalityAttribute.getTableAlias()));
            sb.append(oid).append("|");
        }
        BartUtility.removeChars("|".length(), sb);
        return sb.toString();
    }

//    public static boolean verifyInequalitiesOnTuplePair(Set<FormulaVariable> inequalityVariables, TuplePair pair, EquivalenceClass equivalenceClass) {
//        boolean verified = true;
//        for (FormulaVariable inequalityVariable : inequalityVariables) {
//            if (inequalityVariable.equals(equivalenceClass.getInequalityVariable())) {
//                continue;
//            }
//            IValue firstValue = DependencyUtility.getValueForVariable(inequalityVariable, pair.getFirstTuple());
//            IValue secondValue = DependencyUtility.getValueForVariable(inequalityVariable, pair.getSecondTuple());
//            if (firstValue.equals(secondValue)) {
//                if (logger.isDebugEnabled()) logger.debug("Skipping values first value: " + firstValue + " - Second value: " + secondValue);
//                verified = false;
//            }
//        }
//        return verified;
//    }
    public static Set<AttributeRef> filterTargetOccurrenceInTuple(Tuple tuple, Set<AttributeRef> occurrences) {
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        for (AttributeRef attributeRef : occurrences) {
            if (attributeRef.isSource() || !tupleContainsCellForAttribute(tuple, attributeRef.getTableAlias())) {
                continue;
            }
            result.add(attributeRef);
        }
        return result;
    }

    public static boolean tupleContainsCellForAttribute(Tuple tuple, TableAlias tableAlias) {
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttributeRef().getTableAlias().equals(tableAlias)) {
                return true;
            }
        }
        return false;
    }

    public static VariableEquivalenceClass findVariableEquivalenceClass(FormulaVariable variable, IFormula formula) {
        for (VariableEquivalenceClass variableEquivalenceClass : formula.getLocalVariableEquivalenceClasses()) {
            if (variableEquivalenceClass.contains(variable)) {
                return variableEquivalenceClass;
            }
        }
        throw new IllegalArgumentException("Unable to find equivalence class for variable " + variable + " in formula " + formula);
    }

    public static boolean involved(ComparisonAtom comparison, List<VariableEquivalenceClass> variablesForCell) {
        if (comparison.getLeftVariable() != null && isInEquivalenceClass(comparison.getLeftVariable(), variablesForCell)) {
            return true;
        }
        if (comparison.getRightVariable() != null && isInEquivalenceClass(comparison.getRightVariable(), variablesForCell)) {
            return true;
        }
        return false;
    }

    private static boolean isInEquivalenceClass(FormulaVariable variable, List<VariableEquivalenceClass> variablesForCell) {
        for (VariableEquivalenceClass variableEquivalenceClass : variablesForCell) {
            if (variableEquivalenceClass.contains(variable)) {
                return true;
            }
        }
        return false;
    }

    public static IValue findVariableValue(Tuple tuple, FormulaVariable variable) {
        if (variable == null) {
            return null;
        }
        return tuple.getCell(variable.getRelationalOccurrences().get(0).getAttributeRef()).getValue();
    }

    public static IValue extractValue(FormulaVariable variable, String constant, Tuple tuple) {
        IValue value;
        if (constant != null) {
            return new ConstantValue(BartUtility.cleanConstantValue(constant));
        } else {
            value = tuple.getCell(variable.getRelationalOccurrences().get(0).getAttributeRef()).getValue();
        }
        return value;
    }

//    public static boolean checkIfFinished(CellChanges allCellChanges, int initialChanges, int sampleSize) {
//        int totalChanges = allCellChanges.getChanges().size();
//        int executedChanges = totalChanges - initialChanges;
//        return (executedChanges >= sampleSize);
//    }

    public static boolean checkIfFinished(int executedChanges, int sampleSize) {
        return (executedChanges >= sampleSize);
    }

    public static int computeSampleSize(VioGenQuery vioGenQuery, EGTask task) {
        VioGenQueryConfiguration configuration = vioGenQuery.getConfiguration();
        double percentage = configuration.getPercentage();
        Set<TableAlias> tableInFormula = DependencyUtility.extractTableAliasInFormula(vioGenQuery.getFormula());
        long tableSize = getMaxTableSize(tableInFormula, task);
        int sampleSize = (int) (percentage * tableSize) / 100;
        if (logger.isDebugEnabled()) logger.debug("Percentage: " + percentage);
        if (logger.isDebugEnabled()) logger.debug("Tables in formula: " + tableInFormula);
        if (logger.isDebugEnabled()) logger.debug("Table size: " + tableSize);
        if (logger.isDebugEnabled()) logger.debug("SampleSize for vioGenQuery " + vioGenQuery.toShortString() + ": " + sampleSize);
        return sampleSize;
    }

    private static long getMaxTableSize(Set<TableAlias> tableInFormula, EGTask task) {
        if (tableInFormula.isEmpty()) {
            return 0;
        }
        long max = 0;
        for (TableAlias tableAlias : tableInFormula) {
            ITable table;
            if (tableAlias.isSource()) {
                table = task.getSource().getTable(tableAlias.getTableName());
            } else {
                table = task.getTarget().getTable(tableAlias.getTableName());
            }
            if (table.getSize() > max) {
                max = table.getSize();
            }
        }
        return max;
    }

    public static List<Tuple> materializeTuples(IAlgebraOperator operator, IRunQuery queryRunner, EGTask task) {
        List<Tuple> result = new ArrayList<Tuple>();
        ITupleIterator it = queryRunner.run(operator, task.getSource(), task.getTarget());
        while (it.hasNext()) {
            result.add(it.next());
        }
        it.close();
        return result;
    }

    public static boolean isUsedInChanges(Tuple tuple, List<VioGenQueryCellChange> changesForTuple) {
        for (VioGenQueryCellChange change : changesForTuple) {
            String changedTable = change.getCell().getAttributeRef().getTableName();
            TupleOID changedTupleOID = change.getCell().getTupleOID();
            for (Cell cell : tuple.getCells()) {
                String cellTable = cell.getAttributeRef().getTableName();
                if (cell.getAttribute().equals(BartConstants.OID) && changedTable.equals(cellTable) && cell.getValue().toString().equals(changedTupleOID.toString())) {
                    return true;
                }
            }
        }
        return false;
    }
}
