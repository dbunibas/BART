package bart.model.errorgenerator.operator;

import bart.BartConstants;
import bart.model.EGTask;
import bart.model.algebra.operators.GenerateTupleFromTuplePair;
import bart.model.database.Attribute;
import bart.model.database.AttributeRef;
import bart.model.database.Cell;
import bart.model.database.ConstantValue;
import bart.model.database.IValue;
import bart.model.database.Tuple;
import bart.model.database.TupleOID;
import bart.model.dependency.ComparisonAtom;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaVariableOccurrence;
import bart.model.dependency.IFormula;
import bart.model.dependency.IFormulaAtom;
import bart.model.dependency.RelationalAtom;
import bart.model.dependency.VariableEquivalenceClass;
import bart.model.errorgenerator.CellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ValueConstraint;
import bart.model.errorgenerator.VioGenCell;
import bart.model.errorgenerator.ViolationContext;
import bart.model.errorgenerator.VioGenQuery;
import bart.model.errorgenerator.operator.changeselectors.ChangeSelectorFactory;
import bart.model.errorgenerator.operator.valueselectors.INewValueSelectorStrategy;
import bart.persistence.Types;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateChangesAndContexts {

    private static Logger logger = LoggerFactory.getLogger(GenerateChangesAndContexts.class);
    private GenerateTupleFromTuplePair tupleMerger = new GenerateTupleFromTuplePair();

    public boolean hasInteractionsWithPreviousChanges(VioGenCell vioGenCell, ViolationContext context, CellChanges allCellChanges) {
        if (allCellChanges.isViolationContextCell(vioGenCell.getCell())) {
            return true;
        }
        for (Cell vioContextCell : context.getCells()) {
            if (allCellChanges.cellHasBeenChanged(vioContextCell)) {
                return true;
            }
        }
        return false;
    }

    public void addChanges(List<CellChange> changes, CellChanges allCellChanges) {
        for (CellChange change : changes) {
            allCellChanges.addChange(change);
            allCellChanges.addAllCellsInViolationContext(change.getGeneratingContext().getCells());
        }
    }

    public void filterChanges(List<CellChange> changes, EGTask task) {
        for (Iterator<CellChange> iterator = changes.iterator(); iterator.hasNext();) {
            CellChange change = iterator.next();
            boolean accepted = ChangeSelectorFactory.getChangeSelector(change, task).acceptChange(change);
            if (!accepted) {
                iterator.remove();
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    /////////////     CONTEXT AND CHANGE FOR STANDARD TUPLE
    /////////////////////////////////////////////////////////////////////////
    public List<CellChange> generateChangesForStandardTuple(VioGenQuery vioGenQuery, Tuple tuple, CellChanges allCellChanges, INewValueSelectorStrategy valueSelector, EGTask task) {
        List<CellChange> allChanges = new ArrayList<CellChange>();
        if (logger.isInfoEnabled()) logger.info("Generating changes for tuple " + tuple);
        FormulaVariable firstVariable = vioGenQuery.getVioGenComparison().getVariables().get(0);
        if (logger.isInfoEnabled()) logger.info("First variable: " + firstVariable);
        List<CellChange> changesForFirstVariable = generateChangesForVariable(firstVariable, tuple, vioGenQuery, allCellChanges, valueSelector, task);
        allChanges.addAll(changesForFirstVariable);
        if (logger.isInfoEnabled()) logger.info("Changes for first variable:\n" + BartUtility.printCollection(allChanges, "\t"));
        if (!useSecondVariable(firstVariable, changesForFirstVariable, vioGenQuery, task)) {
            return allChanges;
        }
        FormulaVariable secondVariable = vioGenQuery.getVioGenComparison().getVariables().get(1);
        if (logger.isInfoEnabled()) logger.info("Second variable: " + secondVariable);
        List<CellChange> changesForSecondVariable = generateChangesForVariable(secondVariable, tuple, vioGenQuery, allCellChanges, valueSelector, task);
        if (logger.isInfoEnabled()) logger.info("Changes for second variable:\n" + BartUtility.printCollection(changesForSecondVariable, "\t"));
        allChanges.addAll(changesForSecondVariable);
        return allChanges;
    }

    private boolean useSecondVariable(FormulaVariable firstVariable, List<CellChange> changesForFirstVariable, VioGenQuery vioGenQuery, EGTask task) {
        if (vioGenQuery.getVioGenComparison().getVariables().size() == 1) {
            return false;
        }
        if (!changesForFirstVariable.isEmpty() && task.getConfiguration().isAvoidInteractions()) {
            return false;
        }
        if (DependencyUtility.hasNumericComparison(vioGenQuery.getDependency().getPremise())) {
            return false;
        }
        if (vioGenQuery.getFormula().isSymmetric()) {
            return true;
        }
        Set<AttributeRef> variableOccurrences = extractOccurrencesForVariable(firstVariable);
        if (containsSourceOccurrences(variableOccurrences)) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private List<CellChange> generateChangesForVariable(FormulaVariable variable, Tuple tuple, VioGenQuery vioGenQuery, CellChanges allCellChanges, INewValueSelectorStrategy valueSelector, EGTask task) {
        Set<AttributeRef> variableOccurrences = extractOccurrencesForVariable(variable);
        if (logger.isInfoEnabled()) logger.info("Target occurrences " + variableOccurrences);
        if (containsSourceOccurrences(variableOccurrences)) {
            return Collections.EMPTY_LIST;
        }
        List<CellChange> changesForOccurrences = new ArrayList<CellChange>();
        for (AttributeRef occurrence : variableOccurrences) {
            CellChange change = buildCellChangeForStandardTuple(tuple, occurrence, allCellChanges, vioGenQuery, valueSelector, task);
            if (change == null) {
                break;
            }
            changesForOccurrences.add(change);
        }
        if (changesForOccurrences.size() == variableOccurrences.size()) {
            return changesForOccurrences;
        }
        return Collections.EMPTY_LIST;
    }

    private Set<AttributeRef> extractOccurrencesForVariable(FormulaVariable formulaVariable) {
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        for (FormulaVariableOccurrence occurrence : formulaVariable.getRelationalOccurrences()) {
            result.add(occurrence.getAttributeRef());
        }
        return result;
    }

    private boolean containsSourceOccurrences(Set<AttributeRef> occurrences) {
        for (AttributeRef occurrence : occurrences) {
            if (occurrence.isSource()) {
                return true;
            }
        }
        return false;
    }

    private CellChange buildCellChangeForStandardTuple(Tuple tuple, AttributeRef targetAttributeOccurrence, CellChanges allCellChanges, VioGenQuery vioGenQuery, INewValueSelectorStrategy valueSelector, EGTask task) {
        Cell cell = tuple.getCell(targetAttributeOccurrence);
        TupleOID originalOid = new TupleOID(BartUtility.getOriginalOid(tuple, targetAttributeOccurrence.getTableAlias()));
        cell = new Cell(originalOid, cell.getAttributeRef(), cell.getValue());
        VioGenCell vioGenCell = new VioGenCell(vioGenQuery, cell);
        ViolationContext context = buildVioContext(vioGenQuery.getFormula(), tuple, vioGenCell.getVioGenQuery().getDependency().getId());
        if (logger.isDebugEnabled()) logger.debug("Cells in violation context: " + context.getCells());
        if (task.getConfiguration().isAvoidInteractions() && hasInteractionsWithPreviousChanges(vioGenCell, context, allCellChanges)) {
            if (logger.isDebugEnabled()) logger.debug("Discarding vioGenCell " + vioGenCell + "...");
            return null;
        }
        CellChange cellChange = buildCellChange(vioGenCell, context, tuple, vioGenQuery.getFormula(), task);
        IValue newValue = valueSelector.generateNewValuesForContext(vioGenCell, cellChange, task);
        if (logger.isInfoEnabled()) logger.info("New value for context: " + newValue);
        if (newValue == null) {
            return null;
        }
        cellChange.setNewValue(newValue);
        vioGenCell.addViolationContext(context);
        if (logger.isInfoEnabled()) logger.info("### Adding new change: " + cellChange + " for context:\n" + context);
        if (task.getConfiguration().isDebug()) System.out.println(cellChange);
        return cellChange;
    }

    public ViolationContext buildVioContext(IFormula formula, Tuple tuple, String dependencyId) {
        ViolationContext context = new ViolationContext();
        context.setDependencyId(dependencyId);
        if (logger.isInfoEnabled()) logger.info("Generating context for formula " + formula + "\nand tuple " + tuple.toStringWithOID());
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (atom instanceof RelationalAtom) {
                continue;
            }
            if (logger.isDebugEnabled()) logger.debug("Analyzing atom " + atom);
            for (FormulaVariable formulaVariable : atom.getVariables()) {
                VariableEquivalenceClass variableEquivalenceClass = ExecuteVioGenQueryUtility.findVariableEquivalenceClass(formulaVariable, formula);
                if (logger.isInfoEnabled()) logger.info("VariableEquivalenceClass " + variableEquivalenceClass);
                for (FormulaVariableOccurrence formulaVariableOccurrence : variableEquivalenceClass.getRelationalOccurrences()) {
                    AttributeRef attribute = formulaVariableOccurrence.getAttributeRef();
                    Cell cell = tuple.getCell(attribute);
                    TupleOID originalOid = new TupleOID(BartUtility.getOriginalOid(tuple, attribute.getTableAlias()));
                    cell = new Cell(originalOid, cell.getAttributeRef(), cell.getValue());
                    if (logger.isInfoEnabled()) logger.info("Occurrence " + attribute);
                    if (logger.isInfoEnabled()) logger.info("Cell " + cell);
                    context.addCell(cell);
                }
            }
        }
        return context;
    }

    private CellChange buildCellChange(VioGenCell vioGenCell, ViolationContext context, Tuple tuple, IFormula formula, EGTask task) {
        CellChange cellChange = new CellChange(vioGenCell, context, vioGenCell.getVioGenQuery());
        ComparisonAtom targetComparison = vioGenCell.getVioGenQuery().getVioGenComparison();
        if (logger.isInfoEnabled()) logger.info("Generating VioGenContext for cell " + vioGenCell.getCell() + " and target comparison " + targetComparison + " in tuple\n\t" + tuple);
        List<VariableEquivalenceClass> variablesForCell = findVariableForCellInOriginalFormula(vioGenCell, tuple);
        if (logger.isDebugEnabled()) logger.debug("Variables for cell: " + variablesForCell);
        findValueConstraintsInComparisonsWithConstant(variablesForCell, formula, cellChange, targetComparison);
        IValue blackValue = vioGenCell.getCell().getValue();
        AttributeRef attributeRef = vioGenCell.getCell().getAttributeRef();
        Attribute attribute = BartUtility.getAttribute(attributeRef, task);
        String type = attribute.getType();
        cellChange.addBlackListValue(new ValueConstraint(blackValue, type));
        if (targetComparison.isEqualityComparison()) {
            if (cellChange.getWhiteList().isEmpty()) {
                ValueConstraint starValueConstraint = new ValueConstraint(new ConstantValue(BartConstants.STAR_VALUE), type);
                cellChange.addWhiteListValue(starValueConstraint);
            }
        } else if (targetComparison.isInequalityComparison()) {
            findWhiteListBlackListForInequalities(vioGenCell, cellChange, targetComparison, tuple, task);
        } else {
            findWhiteListBlackListForNumericalConstraints(vioGenCell, cellChange, targetComparison, tuple, task);
        }
        if (logger.isInfoEnabled()) logger.info("** CellChange " + cellChange.toLongString());
        return cellChange;
    }

    private List<VariableEquivalenceClass> findVariableForCellInOriginalFormula(VioGenCell vioGenCell, Tuple tuple) {
        List<VariableEquivalenceClass> result = new ArrayList<VariableEquivalenceClass>();
//        IFormula vioGenFormula = vioGenCell.getVioGenQuery().getFormula();
        IFormula originalFormula = vioGenCell.getVioGenQuery().getDependency().getPremise();
        if (logger.isInfoEnabled()) logger.info("Variable equivalence classes: " + originalFormula.getLocalVariableEquivalenceClasses());
        for (VariableEquivalenceClass equivalenceClass : originalFormula.getLocalVariableEquivalenceClasses()) {
            Set<Cell> cellsForEquivalenceClass = DependencyUtility.findCellsForAttributes(equivalenceClass.getRelationalOccurrences(), tuple);
            if (!DependencyUtility.containsNoAlias(vioGenCell.getCell(), cellsForEquivalenceClass)) {
                continue;
            }
            result.add(equivalenceClass);
        }
        return result;
    }

    private void findValueConstraintsInComparisonsWithConstant(List<VariableEquivalenceClass> variablesForCell, IFormula formula, CellChange cellChange, ComparisonAtom targetComparison) {
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (atom.isRelational()) {
                continue;
            }
            ComparisonAtom comparison = (ComparisonAtom) atom;
            if (comparison.isVariableComparison()) {
                continue;
            }
            if (comparison.isVariableInequalityComparison()) {
                continue;
            }
            if (!ExecuteVioGenQueryUtility.involved(comparison, variablesForCell)) {
                continue;
            }
            if (targetComparison == comparison) {
                continue;
            }
            String constant = BartUtility.cleanConstantValue(comparison.getConstant());
            if (comparison.isNumericalComparison()) {
                IValue value = new ConstantValue(Double.parseDouble(constant));
                String type = Types.DOUBLE;
                ValueConstraint constraint = buildValueConstraintForNumericalComparison(value, type, comparison.getOperator(), comparison.getRightConstant() != null);
                cellChange.addWhiteListValue(constraint);
            } else if (comparison.isEqualityComparison()) {
                cellChange.addWhiteListValue(new ValueConstraint(new ConstantValue(constant), BartConstants.NON_NUMERIC));
            } else if (comparison.isInequalityComparison()) {
                cellChange.addBlackListValue(new ValueConstraint(new ConstantValue(constant), BartConstants.NON_NUMERIC));
            }
        }
    }

    private void findWhiteListBlackListForInequalities(VioGenCell vioGenCell, CellChange cellChange, ComparisonAtom targetComparison, Tuple tuple, EGTask task) {
        if (logger.isInfoEnabled()) logger.info("Adding white/black list for comparison " + targetComparison);
        IValue vioGenCellValue = vioGenCell.getCell().getValue();
        AttributeRef attributeRef = vioGenCell.getCell().getAttributeRef();
        Attribute attribute = BartUtility.getAttribute(attributeRef, task);
        String type = attribute.getType();
        IValue leftCell = ExecuteVioGenQueryUtility.findVariableValue(tuple, targetComparison.getLeftVariable());
        IValue rightCell = ExecuteVioGenQueryUtility.findVariableValue(tuple, targetComparison.getRightVariable());
        if (leftCell != null && leftCell.equals(vioGenCellValue)) {
            IValue whiteValue = ExecuteVioGenQueryUtility.extractValue(targetComparison.getRightVariable(), targetComparison.getRightConstant(), tuple);
            cellChange.addWhiteListValue(new ValueConstraint(whiteValue, type));
        } else if (rightCell != null && rightCell.equals(vioGenCellValue)) {
            IValue whiteValue = ExecuteVioGenQueryUtility.extractValue(targetComparison.getLeftVariable(), targetComparison.getLeftConstant(), tuple);
            cellChange.addWhiteListValue(new ValueConstraint(whiteValue, type));
        } else {
            throw new IllegalArgumentException("Target comparison " + targetComparison + " has no occurrences in cell " + vioGenCell.getCell());
        }
    }

    private void findWhiteListBlackListForNumericalConstraints(VioGenCell vioGenCell, CellChange cellChange, ComparisonAtom targetComparison, Tuple tuple, EGTask task) {
        AttributeRef attributeRef = vioGenCell.getCell().getAttributeRef();
        Attribute attribute = BartUtility.getAttribute(attributeRef, task);
        String type = attribute.getType();
        IValue vioGenCellValue = vioGenCell.getCell().getValue();
        IValue leftCell = ExecuteVioGenQueryUtility.findVariableValue(tuple, targetComparison.getLeftVariable());
        IValue rightCell = ExecuteVioGenQueryUtility.findVariableValue(tuple, targetComparison.getRightVariable());
        String operator = targetComparison.getOperator();
        operator = DependencyUtility.invertOperator(operator);
        if (leftCell != null && leftCell.equals(vioGenCellValue)) {
            IValue otherValue = ExecuteVioGenQueryUtility.extractValue(targetComparison.getRightVariable(), targetComparison.getRightConstant(), tuple);
            ValueConstraint constraint = buildValueConstraintForNumericalComparison(otherValue, type, operator, true);
            cellChange.addWhiteListValue(constraint);
        } else if (rightCell != null && rightCell.equals(vioGenCellValue)) {
            IValue otherValue = ExecuteVioGenQueryUtility.extractValue(targetComparison.getLeftVariable(), targetComparison.getLeftConstant(), tuple);
            ValueConstraint constraint = buildValueConstraintForNumericalComparison(otherValue, type, operator, false);
            cellChange.addWhiteListValue(constraint);
        } else {
            throw new IllegalArgumentException("Target comparison " + targetComparison + " has no occurrences in cell " + vioGenCell.getCell());
        }
    }

    private ValueConstraint buildValueConstraintForNumericalComparison(IValue constant, String type, String operator, boolean rightConstant) {
        if (!rightConstant) {
            operator = DependencyUtility.invertOperator(operator);
        }
        if (operator.equals(BartConstants.GREATER)) {
            return new ValueConstraint(constant, BartConstants.POSITIVE_INFINITY, type);
        } else if (operator.equals(BartConstants.LOWER)) {
            IValue min = BartConstants.NEGATIVE_INFINITY;
            if (Types.INTEGER.equals(type)) {
                min = BartConstants.ZERO;
            }
            return new ValueConstraint(min, constant, type);
        } else if (operator.equals(BartConstants.GREATER_EQ)) {
            ValueConstraint constraint = new ValueConstraint(constant, BartConstants.POSITIVE_INFINITY, type);
            constraint.setInclusiveLeft(true);
            return constraint;
        } else if (operator.equals(BartConstants.LOWER_EQ)) {
            ValueConstraint constraint = new ValueConstraint(BartConstants.NEGATIVE_INFINITY, constant, type);
            constraint.setInclusiveRight(true);
            return constraint;
        } else {
            throw new IllegalArgumentException("Invalid operator " + operator);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    /////////////     CONTEXT AND CHANGE FOR TUPLE PAIRS
    /////////////////////////////////////////////////////////////////////////
    public List<CellChange> handleTuplePair(Tuple firstTuple, Tuple secondTuple, VioGenQuery vioGenQuery, CellChanges allCellChanges, INewValueSelectorStrategy valueSelector, EGTask task) {
        Tuple mergedTuple = tupleMerger.generateTuple(firstTuple, secondTuple);
        return generateChangesForStandardTuple(vioGenQuery, mergedTuple, allCellChanges, valueSelector, task);
    }

    public void addTuplePairChanges(List<CellChange> changesForTuple, Tuple firstTuple, Tuple secondTuple, CellChanges allCellChanges, Set<Tuple> usedTuples, EGTask task) {
        filterChanges(changesForTuple, task);
        if (!changesForTuple.isEmpty()) {
            addChanges(changesForTuple, allCellChanges);
            if (task.getConfiguration().isAvoidInteractions() && ExecuteVioGenQueryUtility.isUsingInChanges(firstTuple, changesForTuple)) {
                usedTuples.add(firstTuple);
            }
            if (task.getConfiguration().isAvoidInteractions() && ExecuteVioGenQueryUtility.isUsingInChanges(secondTuple, changesForTuple)) {
                usedTuples.add(secondTuple);
            }
        }
    }

}
