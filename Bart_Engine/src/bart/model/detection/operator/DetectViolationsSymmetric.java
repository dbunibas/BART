package bart.model.detection.operator;

import bart.model.errorgenerator.operator.*;
import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import speedy.model.algebra.operators.GenerateTupleFromTuplePair;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.operators.IRunQuery;
import bart.model.dependency.Dependency;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaWithAdornments;
import bart.model.detection.Violations;
import bart.model.errorgenerator.EquivalenceClass;
import bart.model.errorgenerator.EquivalenceClassQuery;
import bart.model.errorgenerator.ViolationContext;
import bart.utility.AlgebraUtility;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.operators.ITupleIterator;

public class DetectViolationsSymmetric implements IDetectViolations, IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(DetectViolationsSymmetric.class);

    private ExtractEquivalenceClasses equivalenceClassExtractor = new ExtractEquivalenceClasses();
    private GenerateChangesAndContexts changesGenerator = new GenerateChangesAndContexts();
    private GenerateAlgebraTreeForSymmetricQuery symmetricQueryBuilder = new GenerateAlgebraTreeForSymmetricQuery();
    private GenerateTupleFromTuplePair tupleMerger = new GenerateTupleFromTuplePair();
    private IRunQuery queryRunner;

    @Override
    public boolean check(Dependency dependency, IDatabase source, IDatabase target, EGTask task) {
        intitializeOperators(task);
        if (!dependency.isSymmetric()) {
            throw new IllegalArgumentException("Dependency is not symmetric.\n" + dependency);
        }
        if (!dependency.getPremise().hasEqualityAdornments()) {
            throw new IllegalArgumentException("Dependency with no equalities is not supported.\n" + dependency);
        }
        FormulaWithAdornments formulaWithAdornments = dependency.getPremise().getFormulaWithAdornments();
        symmetricQueryBuilder.initializeQuery(formulaWithAdornments, task);
        List<EquivalenceClassQuery> equivalenceClassQueries = formulaWithAdornments.getEquivalenceClassQueries();
        for (EquivalenceClassQuery subQuery : equivalenceClassQueries) {
            ITupleIterator it = queryRunner.run(subQuery.getQuery(), source, target);
            if (it.hasNext()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void detect(Dependency dependency, Violations violations, IDatabase source, IDatabase target, EGTask task) {
        intitializeOperators(task);
        if (!dependency.isSymmetric()) {
            throw new IllegalArgumentException("Dependency is not symmetric.\n" + dependency);
        }
        if (!dependency.getPremise().hasEqualityAdornments()) {
            throw new IllegalArgumentException("Dependency with no equalities is not supported.\n" + dependency);
        }
        FormulaWithAdornments formulaWithAdornments = dependency.getPremise().getFormulaWithAdornments();
        symmetricQueryBuilder.initializeQuery(formulaWithAdornments, task);
        List<EquivalenceClassQuery> equivalenceClassQueries = formulaWithAdornments.getEquivalenceClassQueries();
        ExecuteVioGenQueryUtility.executeEquivalenceClassQuery(equivalenceClassQueries, queryRunner, source, target);
        while (true) {
            List<EquivalenceClass> equivalenceClasses = equivalenceClassExtractor.getNextCommonEquivalenceClasses(equivalenceClassQueries);
            if (logger.isInfoEnabled()) logger.info("Common equivalence classes read!");
            if (logger.isDebugEnabled()) logger.debug("*** Equivalence Classes ***\n" + BartUtility.printCollection(equivalenceClasses));
            if (equivalenceClasses.isEmpty()) {
                break;
            }
            findViolationsForEquivalenceClasses(dependency, equivalenceClasses, violations, task);
        }
        ExecuteVioGenQueryUtility.closeIterators(equivalenceClassQueries);
    }

    private void findViolationsForEquivalenceClasses(Dependency dependency, List<EquivalenceClass> equivalenceClasses, Violations violations, EGTask task) {
        assert (!equivalenceClasses.isEmpty()) : "Equivalence classes are empty!";
        long start;
        EquivalenceClass firstEquivalenceClass = equivalenceClasses.get(0);
        List<AttributeRef> equalityAttributes = firstEquivalenceClass.getEqualityAttributes();
        FormulaWithAdornments formulaWithAdornments = dependency.getPremise().getFormulaWithAdornments();
        Set<FormulaVariable> inequalityVariables = ExecuteVioGenQueryUtility.getInequalityVariables(formulaWithAdornments);
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
        if (task.getConfiguration().isDetectEntireEquivalenceClasses()) {
            addViolationsForEquivalenceClasses(equivalenceClassForIntersection, dependency, violations);
        } else {
            addViolationsForTuplePairs(equivalenceClassForIntersection, inequalityVariables, dependency, violations, task);
        }
    }

    private void addViolationsForTuplePairs(EquivalenceClass equivalenceClassForIntersection, Set<FormulaVariable> inequalityVariables, Dependency dependency, Violations violations, EGTask task) {
        if (logger.isInfoEnabled()) logger.info("Building context for equivalence class...");
        for (int i = 0; i < equivalenceClassForIntersection.getTuples().size() - 1; i++) {
            Tuple firstTuple = equivalenceClassForIntersection.getTuples().get(i);
            for (int j = i + 1; j < equivalenceClassForIntersection.getTuples().size(); j++) {
                Tuple secondTuple = equivalenceClassForIntersection.getTuples().get(j);
                TuplePair tuplePair = new TuplePair(firstTuple, secondTuple);
//                boolean verified = (inequalityVariables.size() <= 1) || ExecuteVioGenQueryUtility.verifyInequalitiesOnTuplePair(inequalityVariables, tuplePair, equivalenceClassForIntersection);
                boolean verified = (inequalityVariables.size() <= 1) || AlgebraUtility.verifyComparisonsOnTuplePair(tuplePair.getFirstTuple(), tuplePair.getSecondTuple(), dependency.getPremise(), task);
                if (!verified) {
                    continue;
                }
                Tuple mergedTuple = tupleMerger.generateTuple(firstTuple, secondTuple);
                ViolationContext context = changesGenerator.buildVioContext(dependency.getPremise(), mergedTuple, dependency.getId());
                violations.addViolation(dependency, context);
            }
        }
    }

    private void addViolationsForEquivalenceClasses(EquivalenceClass equivalenceClassForIntersection, Dependency dependency, Violations violations) {
        if (logger.isInfoEnabled()) logger.info("Building context for equivalence class...");
        ViolationContext context = new ViolationContext();
        Set<AttributeRef> relevantAttributes = DependencyUtility.findRelevantAttributes(dependency.getPremise().getFormulaWithAdornments());
        if (logger.isInfoEnabled()) logger.info("Relevant attributes for dependency " + dependency.getId() + ": " + relevantAttributes);
        for (Tuple tuple : equivalenceClassForIntersection.getTuples()) {
            Set<Cell> relevantCells = getCells(tuple, relevantAttributes);
            context.addAllCells(relevantCells);
        }
        violations.addViolation(dependency, context);
    }

    private Set<Cell> getCells(Tuple tuple, Set<AttributeRef> relevantAttributes) {
        Set<Cell> result = new HashSet<Cell>();
        for (AttributeRef relevantAttribute : relevantAttributes) {
            Cell cell = tuple.getCell(relevantAttribute);
            TupleOID originalOid = new TupleOID(BartUtility.getOriginalOid(tuple, relevantAttribute.getTableAlias()));
            cell = new Cell(originalOid, cell.getAttributeRef(), cell.getValue());
            result.add(cell);
        }
        return result;
    }

    ///////////////////////////////////////
    @Override
    public void intitializeOperators(EGTask task) {
        queryRunner = OperatorFactory.getInstance().getQueryRunner(task);
    }

}

class EquivalenceClassSizeComparator implements Comparator<EquivalenceClass> {

    public int compare(EquivalenceClass e1, EquivalenceClass e2) {
        return e1.getTuples().size() - e2.getTuples().size();
    }

}
