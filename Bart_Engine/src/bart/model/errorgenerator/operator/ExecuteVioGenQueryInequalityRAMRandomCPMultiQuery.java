package bart.model.errorgenerator.operator;

import bart.BartConstants;
import bart.IInitializableOperator;
import bart.model.dependency.operators.ExtractFormulaSampling;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.NumberOfChanges;
import bart.model.algebra.operators.BuildAlgebraTree;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.IValue;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.database.operators.IRunQuery;
import bart.model.dependency.ComparisonAtom;
import bart.model.dependency.FormulaSampling;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaVariableOccurrence;
import bart.model.dependency.PositiveFormula;
import bart.model.errorgenerator.VioGenQueryCellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ISampleStrategy;
import bart.model.errorgenerator.SampleParameters;
import bart.model.errorgenerator.VioGenQuery;
import bart.model.errorgenerator.operator.valueselectors.INewValueSelectorStrategy;
import speedy.model.expressions.Expression;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.utility.PrintUtility;

public class ExecuteVioGenQueryInequalityRAMRandomCPMultiQuery implements IVioGenQueryExecutor, IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(ExecuteVioGenQueryInequalityRAMRandomCPMultiQuery.class);

    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();
    private GenerateChangesAndContexts changesGenerator = new GenerateChangesAndContexts();
    private ISampleStrategy sampleStrategy;
    private IRunQuery queryRunner;
    private INewValueSelectorStrategy valueSelector;
    private ExtractFormulaSampling treeBuilderForSampling = new ExtractFormulaSampling();

    public int execute(VioGenQuery vioGenQuery, CellChanges allCellChanges, EGTask task) {
        if (task.getConfiguration().isPrintLog()) System.out.println("--- VioGen Query: " + vioGenQuery.toShortString());
        intitializeOperators(task);
        if (task.getConfiguration().isGenerateAllChanges()) {
            throw new IllegalArgumentException("ExecuteVioGenQueryInequalityMainMemoryRandom requires a random execution. Please set generateAllChanges to false.");
        }
        long start = new Date().getTime();
        int sampleSize = ExecuteVioGenQueryUtility.computeSampleSize(vioGenQuery, task);
        if (sampleSize == 0) {
            if (task.getConfiguration().isPrintLog()) System.out.println("No changes required");
            return 0;
        }
        if (ExecuteVioGenQueryUtility.queryIsEmpty(vioGenQuery, task)) {
            if (logger.isInfoEnabled()) logger.info("VioGenQuery is empty! No iteration needed!");
            if (task.getConfiguration().isPrintLog()) System.out.println("VioGenQuery is empty! No iteration needed!");
            return 0;
        }
        if (logger.isInfoEnabled()) logger.info(sampleSize + " changes required");
        if (!task.getConfiguration().isGenerateAllChanges() && task.getConfiguration().isPrintLog()) {
            PrintUtility.printInformation("Error percentage: " + vioGenQuery.getConfiguration().getPercentage());
            PrintUtility.printInformation(sampleSize + " changes required");
        }
        initializeQuery(vioGenQuery, task);
        Set<Tuple> discardedTuples = new HashSet<Tuple>();
        NumberOfChanges numberOfChanges = new NumberOfChanges();
        findVioGenCells(vioGenQuery, allCellChanges, numberOfChanges, sampleSize, discardedTuples, start, task);
        if (!ExecuteVioGenQueryUtility.checkIfFinished(numberOfChanges.getChanges(), sampleSize)) {
            if (logger.isInfoEnabled()) logger.info("After first iteration there are " + (sampleSize - numberOfChanges.getChanges()) + " remaining changes to perform!");
            if (logger.isInfoEnabled()) logger.info("Discarded tuples: " + discardedTuples.size());
            executeDiscardedTuples(vioGenQuery, allCellChanges, discardedTuples, numberOfChanges, sampleSize, start, task);
        }
        if (task.getConfiguration().isPrintLog()) PrintUtility.printSuccess("Executed changes: " + numberOfChanges.getChanges());
        return numberOfChanges.getChanges();
    }

    private void findVioGenCells(VioGenQuery vioGenQuery, CellChanges allCellChanges, NumberOfChanges numberOfChanges, int sampleSize, Set<Tuple> discardedTuples, long start, EGTask task) {
        int offset = computeOffset(vioGenQuery, task);
        IAlgebraOperator pivotingOperator = vioGenQuery.getQuery();
        if (logger.isInfoEnabled()) logger.info("Pivoting operator:\n" + pivotingOperator);
        ITupleIterator pivotingIt = queryRunner.run(pivotingOperator, task.getSource(), task.getTarget());
        while (pivotingIt.hasNext()) {
            if (BartUtility.isTimeout(start, task)) {
                logger.warn("Timeout for vioGenQuery " + vioGenQuery);
                discardedTuples.clear();
                break;
            }
            Tuple pivotTuple = pivotingIt.next();
            if (discardedTuples.size() < offset) {
                discardedTuples.add(pivotTuple);
                continue;
            }
            if (!BartUtility.pickRandom(vioGenQuery.getConfiguration().getProbabilityFactorForInequalityQueries())) {
                discardedTuples.add(pivotTuple);
                continue;
            }
            handleTuple(vioGenQuery, pivotTuple, allCellChanges, numberOfChanges, start, task);
            if (ExecuteVioGenQueryUtility.checkIfFinished(numberOfChanges.getChanges(), sampleSize)) {
                break;
            }
        }
        pivotingIt.close();
    }

    private void executeDiscardedTuples(VioGenQuery vioGenQuery, CellChanges allCellChanges, Set<Tuple> discardedTuples, NumberOfChanges numberOfChanges, int sampleSize, long start, EGTask task) {
        Iterator<Tuple> it = discardedTuples.iterator();
        while (it.hasNext()) {
            if (BartUtility.isTimeout(start, task)) {
                logger.warn("Timeout for vioGenQuery " + vioGenQuery);
                break;
            }
            Tuple discardedTuple = it.next();
            handleTuple(vioGenQuery, discardedTuple, allCellChanges, numberOfChanges, start, task);
            if (ExecuteVioGenQueryUtility.checkIfFinished(numberOfChanges.getChanges(), sampleSize)) {
                return;
            }
        }
    }

    private void initializeQuery(VioGenQuery vioGenQuery, EGTask task) {
        FormulaSampling formulaSampling = treeBuilderForSampling.extractFormula(vioGenQuery.getFormula().getPositiveFormula(), task);
        IAlgebraOperator operator = formulaSampling.getSamplingQuery();
        vioGenQuery.setQuery(operator);
        vioGenQuery.getFormula().setFormulaSampling(formulaSampling);
        if (logger.isDebugEnabled()) logger.debug("Operator\n" + operator.toString());
    }

    private void handleTuple(VioGenQuery vioGenQuery, Tuple pivotTuple, CellChanges allCellChanges, NumberOfChanges numberOfChanges, long start, EGTask task) {
        if (logger.isDebugEnabled()) logger.debug("Pivot Tuple: " + pivotTuple.toStringWithOIDAndAlias());
        PositiveFormula formulaForTuple = buildFormulaForTuple(vioGenQuery.getFormula().getFormulaSampling(), pivotTuple).clone();
        formulaForTuple.setFormulaWithAdornments(null);
        if (logger.isDebugEnabled()) logger.debug("Formula for tuple: " + formulaForTuple);
        IAlgebraOperator queryForTuple = treeBuilder.buildTreeForPremise(formulaForTuple, task);
        if (logger.isDebugEnabled()) logger.trace("Query for tuple: \n" + queryForTuple);
        ITupleIterator it = queryRunner.run(queryForTuple, task.getSource(), task.getTarget());
        while (it.hasNext()) {
            if (BartUtility.isTimeout(start, task)) {
                logger.warn("Timeout for vioGenQuery " + vioGenQuery);
                break;
            }
            Tuple tuple = it.next();
            if (logger.isDebugEnabled()) logger.debug("Tuple: " + tuple);
            List<VioGenQueryCellChange> changesForTuple = changesGenerator.generateChangesForStandardTuple(vioGenQuery, tuple, allCellChanges, valueSelector, task);
            if (!changesForTuple.isEmpty()) {
                changesGenerator.addChanges(changesForTuple, allCellChanges, numberOfChanges);
                break;
            }
        }
        it.close();
    }

    private PositiveFormula buildFormulaForTuple(FormulaSampling formulaSampling, Tuple tuple) {
        PositiveFormula formulaForTuple = formulaSampling.getOriginalFormula().clone();
        for (Cell cell : tuple.getCells()) {
            if (cell.getAttribute().equalsIgnoreCase(BartConstants.OID)) {
                continue;
            }
            AttributeRef attribute = cell.getAttributeRef();
            FormulaVariable formulaVariable = findFormulaVariableForAttributeRef(formulaForTuple, attribute);
            if (logger.isDebugEnabled()) logger.trace("Formual variable: " + formulaVariable);
            ComparisonAtom comparison = buildComparisonAtom(formulaVariable, attribute, tuple, formulaForTuple);
            if (logger.isDebugEnabled()) logger.trace("New comparison " + comparison);
            formulaVariable.addNonRelationalOccurrence(comparison);
            formulaForTuple.addAtom(comparison);
        }
        return formulaForTuple;
    }

    private FormulaVariable findFormulaVariableForAttributeRef(PositiveFormula formulaForTuple, AttributeRef attributeRef) {
        for (FormulaVariable variable : formulaForTuple.getLocalVariables()) {
            for (FormulaVariableOccurrence occurrence : variable.getRelationalOccurrences()) {
                if (occurrence.getAttributeRef().equals(attributeRef)) {
                    return variable;
                }
            }
        }
        throw new IllegalArgumentException("Unable to find a variable for attribute " + attributeRef + " in formula " + formulaForTuple);
    }

    private ComparisonAtom buildComparisonAtom(FormulaVariable formulaVariable, AttributeRef attributeRef, Tuple tuple, PositiveFormula formulaForTuple) {
        IValue constantValue = tuple.getCell(attributeRef).getValue();
        String stringValue = constantValue.toString();
        BartUtility.cleanConstantValue(stringValue);
        stringValue = stringValue.replace("'", "''");
        stringValue = stringValue.replace("\\", "\\\\");
        String constant = "\"" + stringValue + "\"";
        String expressionString = formulaVariable.getId() + BartConstants.EQUAL + constant;
        Expression expression = new Expression(expressionString);
        expression.setVariableDescription(formulaVariable.getId(), formulaVariable);
        return new ComparisonAtom(formulaForTuple, expression, formulaVariable.getId(), null, null, constant, BartConstants.EQUAL);
    }

    private int computeOffset(VioGenQuery vioGenQuery, EGTask task) {
        if (!vioGenQuery.getConfiguration().isUseOffsetInInequalityQueries()) {
            return 0;
        }
        int sampleSize = ExecuteVioGenQueryUtility.computeSampleSize(vioGenQuery, task);
        Set<TableAlias> tableInFormula = DependencyUtility.extractTableAliasInFormula(vioGenQuery.getFormula());
        SampleParameters sampleParameters = sampleStrategy.computeParameters(vioGenQuery.getQuery(), tableInFormula, BartConstants.INEQUALITY_QUERY_TYPE, sampleSize, vioGenQuery.getConfiguration(), task);
        int offset = sampleParameters.getOffset();
        if (logger.isInfoEnabled()) logger.info("Offset: " + offset);
        return offset;
    }

    public void intitializeOperators(EGTask task) {
        queryRunner = OperatorFactory.getInstance().getQueryRunner(task);
        valueSelector = OperatorFactory.getInstance().getValueSelector(task);
        String strategy = task.getConfiguration().getSampleStrategyForInequalityQueries();
        sampleStrategy = OperatorFactory.getInstance().getSampleStrategy(strategy, task);
    }

}
