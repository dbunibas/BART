package bart.model.errorgenerator.operator;

import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.database.AttributeRef;
import bart.model.database.Tuple;
import bart.model.database.operators.IRunQuery;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaWithAdornments;
import bart.model.detection.operator.EstimateRepairabilityAPriori;
import bart.model.errorgenerator.CellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.EquivalenceClass;
import bart.model.errorgenerator.EquivalenceClassQuery;
import bart.model.errorgenerator.VioGenQuery;
import bart.model.errorgenerator.operator.valueselectors.INewValueSelectorStrategy;
import bart.utility.AlgebraUtility;
import bart.utility.BartUtility;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteVioGenQuerySymmetric implements IVioGenQueryExecutor, IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(ExecuteVioGenQuerySymmetric.class);

    private ExtractEquivalenceClasses equivalenceClassExtractor = new ExtractEquivalenceClasses();
    private GenerateChangesAndContexts changesGenerator = new GenerateChangesAndContexts();
    private EstimateRepairabilityAPriori aPrioriRepairabilityEstimator = new EstimateRepairabilityAPriori();
    private GenerateAlgebraTreeForSymmetricQuery symmetricQueryBuilder = new GenerateAlgebraTreeForSymmetricQuery();
    private IRunQuery queryRunner;
    private INewValueSelectorStrategy valueSelector;

    public void execute(VioGenQuery vioGenQuery, CellChanges allCellChanges, EGTask task) {
        if (!task.getConfiguration().isGenerateAllChanges()) {
            throw new IllegalArgumentException("Stardard operator can be used only to generate all changes");
        }
        if (!vioGenQuery.getFormula().isSymmetric()) {
            throw new IllegalArgumentException("VioGenQuery is not symmetric.\n" + vioGenQuery);
        }
        if (!vioGenQuery.getFormula().hasEqualityAdornments()) {
            throw new IllegalArgumentException("VioGenQuery with no equalities is not supported.\n" + vioGenQuery);
        }
        if (task.getConfiguration().isPrintLog()) System.out.println("--- VioGen Query: " + vioGenQuery.toShortString());
        intitializeOperators(task);
        symmetricQueryBuilder.initializeQuery(vioGenQuery.getFormula().getFormulaWithAdornments(), task);
        List<EquivalenceClassQuery> equivalenceClassQueries = vioGenQuery.getFormula().getFormulaWithAdornments().getEquivalenceClassQueries();
        ExecuteVioGenQueryUtility.executeEquivalenceClassQuery(equivalenceClassQueries, queryRunner, task.getSource(), task.getTarget());
        while (true) {
            List<EquivalenceClass> equivalenceClasses = equivalenceClassExtractor.getNextCommonEquivalenceClasses(equivalenceClassQueries);
            if (logger.isInfoEnabled()) logger.info("Common equivalence classes read!");
            if (logger.isDebugEnabled()) logger.trace("*** Equivalence Classes ***\n" + BartUtility.printCollection(equivalenceClasses));
            if (equivalenceClasses.isEmpty()) {
                break;
            }
            findVioGenCellsForEquivalenceClasses(vioGenQuery, equivalenceClasses, allCellChanges, task);
        }
        ExecuteVioGenQueryUtility.closeIterators(equivalenceClassQueries);
    }

    private void findVioGenCellsForEquivalenceClasses(VioGenQuery vioGenQuery, List<EquivalenceClass> equivalenceClasses, CellChanges allCellChanges, EGTask task) {
        assert (!equivalenceClasses.isEmpty()) : "Equivalence classes are empty!";
        long start;
        EquivalenceClass firstEquivalenceClass = equivalenceClasses.get(0);
        FormulaWithAdornments formulaWithAdornment = vioGenQuery.getFormula().getFormulaWithAdornments();
        List<AttributeRef> equalityAttributes = firstEquivalenceClass.getEqualityAttributes();
        Set<FormulaVariable> inequalityVariables = ExecuteVioGenQueryUtility.getInequalityVariables(formulaWithAdornment);
        EquivalenceClass equivalenceClassForIntersection;
        if (equivalenceClasses.size() == 1) {
            equivalenceClassForIntersection = firstEquivalenceClass;
        } else {
            equivalenceClassForIntersection = new EquivalenceClass(equalityAttributes);
            start = new Date().getTime();
            equivalenceClassForIntersection.addAllTuple(ExecuteVioGenQueryUtility.intersectTuples(equivalenceClasses));
            if (logger.isDebugEnabled()) logger.debug("intersectTuples (ms): " + (new Date().getTime() - start));
            if (logger.isInfoEnabled()) logger.info("tuples after intersection: " + equivalenceClassForIntersection.getTuples().size());
        }
        if (logger.isInfoEnabled()) logger.info("Building context for equivalence class...");
        Set<Tuple> usedTuples = new HashSet<Tuple>();
        for (int i = 0; i < equivalenceClassForIntersection.getTuples().size() - 1; i++) {
            Tuple firstTuple = equivalenceClassForIntersection.getTuples().get(i);
            if (task.getConfiguration().isAvoidInteractions() && usedTuples.contains(firstTuple)) {
                continue;
            }
            for (int j = i + 1; j < equivalenceClassForIntersection.getTuples().size(); j++) {
                Tuple secondTuple = equivalenceClassForIntersection.getTuples().get(j);
                if (task.getConfiguration().isAvoidInteractions() && usedTuples.contains(firstTuple)) {
                    continue;
                }
                boolean verified = (inequalityVariables.size() <= 1) || AlgebraUtility.verifyComparisonsOnTuplePair(firstTuple, secondTuple, vioGenQuery.getFormula(), task);
                if (!verified) {
                    continue;
                }
                List<CellChange> changes = changesGenerator.handleTuplePair(firstTuple, secondTuple, vioGenQuery, allCellChanges, valueSelector, task);
                if (task.getConfiguration().isEstimateAPrioriRepairability()) {
                    aPrioriRepairabilityEstimator.estimateRepairabilityInEquivalenceClass(changes, equivalenceClassForIntersection, vioGenQuery);
                }
                changesGenerator.addTuplePairChanges(changes, firstTuple, secondTuple, allCellChanges, usedTuples, task);
            }
        }
    }

    ///////////////////////////////////////
    public void intitializeOperators(EGTask task) {
        queryRunner = OperatorFactory.getInstance().getQueryRunner(task);
        valueSelector = OperatorFactory.getInstance().getValueSelector(task);
    }

}

class EquivalenceClassSizeComparator implements Comparator<EquivalenceClass> {

    public int compare(EquivalenceClass e1, EquivalenceClass e2) {
        return e1.getTuples().size() - e2.getTuples().size();
    }

}
