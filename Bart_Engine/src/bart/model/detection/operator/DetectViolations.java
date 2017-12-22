package bart.model.detection.operator;

import bart.utility.ErrorGeneratorStats;
import bart.model.errorgenerator.operator.*;
import bart.BartConstants;
import bart.IInitializableOperator;
import bart.exceptions.ErrorGeneratorException;
import bart.model.EGTask;
import bart.model.dependency.Dependency;
import speedy.model.database.IDatabase;
import bart.model.dependency.analysis.FindFormulaWithAdornments;
import bart.model.detection.Violations;
import bart.utility.BartUtility;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.utility.PrintUtility;

public class DetectViolations implements IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(DetectViolations.class);

    private SelectQueryExecutor executorSelector = new SelectQueryExecutor();
    private FindFormulaWithAdornments symmetryFinder = new FindFormulaWithAdornments();
    private DetectViolationStandard detectViolationsStandard = new DetectViolationStandard();

    //////////// CHECK IF DATABASE IS CLEAN
    public void check(List<Dependency> dependencies, IDatabase source, IDatabase target, EGTask task) throws ErrorGeneratorException {
        intitializeOperators(task);
        List<Dependency> violatedDependency = new ArrayList<Dependency>();
        for (Dependency dependency : dependencies) {
            symmetryFinder.findFormulaWithAdornments(dependency.getPremise().getPositiveFormula(), task);
            IDetectViolations detector = executorSelector.getExecutorForDependency(dependency, task);
            if (task.getConfiguration().isPrintLog()) PrintUtility.printInformation("* Checking dependency " + dependency.getId());
            if (logger.isDebugEnabled()) logger.debug("Detecting violations for dependency " + dependency.getId() + " using " + detector.getClass().getName());
//            boolean violated = detectViolationsStandard.check(dependency, source, target, task);
            boolean violated = detector.check(dependency, source, target, task);
            if (violated) {
                if (task.getConfiguration().isPrintLog()) PrintUtility.printError("** Dependency " + dependency.getId() + " is violated");
                violatedDependency.add(dependency);
            } else {
                if (task.getConfiguration().isPrintLog()) PrintUtility.printInformation("** No violations found");
            }
        }
        if (!violatedDependency.isEmpty()) {
            throw new ErrorGeneratorException("Target violates dependencies " + BartUtility.printDependencyIds(violatedDependency));
        }
    }

    //////////// FIND VIOLATIONS
    public Violations findViolations(IDatabase source, IDatabase target, EGTask task) throws ErrorGeneratorException {
        assert (target != null);
        intitializeOperators(task);
        if (logger.isDebugEnabled()) logger.debug("Detecting all violations on task " + task);
        if (task.getConfiguration().isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        if (task.getConfiguration().isPrintLog()) System.out.println("*** Detecting all violations on task\n" + task);
        if (task.getConfiguration().isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        Violations violations = new Violations();
        for (Dependency dependency : task.getDCs()) {
            symmetryFinder.findFormulaWithAdornments(dependency.getPremise().getPositiveFormula(), task);
            long startDep = new Date().getTime();
            IDetectViolations detector = executorSelector.getExecutorForDependency(dependency, task);
            detector.detect(dependency, violations, source, target, task);
            long endDep = new Date().getTime();
            ErrorGeneratorStats.getInstance().addDependencyTime(dependency, endDep - startDep);
        }
        if (logger.isDebugEnabled()) logger.debug(violations.toString());
        if (task.getConfiguration().isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        if (task.getConfiguration().isDebug()) System.out.println(violations);
        return violations;
    }

    public void intitializeOperators(EGTask task) {
    }

}
