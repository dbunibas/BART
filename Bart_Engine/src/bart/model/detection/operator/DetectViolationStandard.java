package bart.model.detection.operator;

import bart.model.errorgenerator.operator.*;
import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.algebra.operators.BuildAlgebraTree;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.IDatabase;
import speedy.model.database.Tuple;
import speedy.model.database.operators.IRunQuery;
import bart.model.dependency.Dependency;
import bart.model.dependency.IFormula;
import bart.model.detection.Violations;
import bart.model.errorgenerator.ViolationContext;
import bart.utility.DependencyUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.Limit;

public class DetectViolationStandard implements IDetectViolations, IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(DetectViolationStandard.class);

    private GenerateChangesAndContexts changesGenerator = new GenerateChangesAndContexts();
    private BuildAlgebraTree treeBuilder = new BuildAlgebraTree();
    private IRunQuery queryRunner;

    @Override
    public void detect(Dependency dependency, Violations violations, IDatabase source, IDatabase target, EGTask task) {
        intitializeOperators(task);
        if (DependencyUtility.hasOnlyVariableInequalities(dependency.getPremise())) {
            logger.warn("Detecting violations for dependencies without equalities is slow.");
        }
        IFormula formula = dependency.getPremise();
        IAlgebraOperator treeRoot = treeBuilder.buildTreeForPremise(formula, task);
        if (logger.isDebugEnabled()) logger.debug("Operator\n" + treeRoot.toString());
        ITupleIterator it = queryRunner.run(treeRoot, source, target);
        while (it.hasNext()) {
            Tuple tuple = it.next();
            ViolationContext context = changesGenerator.buildVioContext(formula, tuple, dependency.getId());
            violations.addViolation(dependency, context);
        }
        it.close();
    }

    @Override
    public boolean check(Dependency dependency, IDatabase source, IDatabase target, EGTask task) {
        intitializeOperators(task);
        if (DependencyUtility.hasOnlyVariableInequalities(dependency.getPremise())) {
            logger.warn("Detecting violations for dependencies without equalities is slow.");
        }
        IFormula formula = dependency.getPremise();
        IAlgebraOperator treeRoot = treeBuilder.buildTreeForPremise(formula, task);
        Limit limit = new Limit(1);
        limit.addChild(treeRoot);
        treeRoot = limit;
        if (logger.isDebugEnabled()) logger.debug("Operator\n" + treeRoot.toString());
        ITupleIterator it = queryRunner.run(treeRoot, source, target);
        if (it.hasNext()) {
            it.close();
            return true;
        }
        it.close();
        return false;
    }

    @Override
    public void intitializeOperators(EGTask task) {
        queryRunner = OperatorFactory.getInstance().getQueryRunner(task);
    }

}
