package bart.model.errorgenerator.operator;

import bart.exceptions.ErrorGeneratorException;
import bart.model.EGTask;
import bart.model.EGTaskConfiguration;
import bart.model.dependency.CrossProductFormulas;
import bart.model.dependency.Dependency;
import bart.model.detection.operator.DetectViolationStandard;
import bart.model.detection.operator.DetectViolationsSymmetric;
import bart.model.detection.operator.IDetectViolations;
import bart.model.errorgenerator.VioGenQuery;
import bart.utility.DependencyUtility;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectQueryExecutor {

    private static Logger logger = LoggerFactory.getLogger(SelectQueryExecutor.class);
    private Map<String, IDetectViolations> dependencyExecutor = new HashMap<String, IDetectViolations>();
    private Map<String, IVioGenQueryExecutor> vioGenQueryExecutor = new HashMap<String, IVioGenQueryExecutor>();

    public IDetectViolations getExecutorForDependency(Dependency dependency, EGTask task) {
        IDetectViolations executor;
        if (dependency.isSymmetric()) {
            executor = getDependencyExecutor(DetectViolationsSymmetric.class);
        } else {
            executor = getDependencyExecutor(DetectViolationStandard.class);
        }
        if (logger.isInfoEnabled()) logger.info("Executor for dependency " + dependency.getId() + ": " + executor.getClass().getName());
        return executor;
    }

    public IVioGenQueryExecutor getExecutorForVioGenQuery(VioGenQuery vioGenQuery, EGTask task) {
        EGTaskConfiguration configuration = task.getConfiguration();
        IVioGenQueryExecutor executor;
        if (configuration.isGenerateAllChanges()) {
            executor = getExecutorForAllChanges(vioGenQuery, task);
        } else {
            executor = getExecutorForRandom(vioGenQuery, task);
        }
        if (logger.isInfoEnabled()) logger.info("Executor for vioGenQuery " + vioGenQuery.toShortString() + ": " + executor.getClass().getName());
        return executor;
    }

    private IVioGenQueryExecutor getExecutorForAllChanges(VioGenQuery vioGenQuery, EGTask task) {
        if (DependencyUtility.hasOnlyVariableInequalities(vioGenQuery)) {
            return getVioGenQueryExecutor(ExecuteVioGenQueryStandard.class.getName());
        } else if (task.getConfiguration().isUseSymmetricOptimization() && vioGenQuery.getFormula().isSymmetric() && vioGenQuery.getFormula().hasEqualityAdornments()) {
            return getVioGenQueryExecutor(ExecuteVioGenQuerySymmetric.class.getName());
        } else {
            return getVioGenQueryExecutor(ExecuteVioGenQueryStandard.class.getName());
        }
    }

    private IVioGenQueryExecutor getExecutorForRandom(VioGenQuery vioGenQuery, EGTask task) {
//        if(vioGenQuery.toShortString().equals("dc1(!=,==,!=)[(state1 != state2)]") || vioGenQuery.toShortString().equals("dc2(!=,!=,==)[(state1 != state2)]")){
//            System.out.println("**** TEST ****");
//            return getVioGenQueryExecutor(ExecuteVioGenQueryInequalityRAMRandomCPSymmetric.class);
//        }
        if (vioGenQuery.getConfiguration().getQueryExecutor() != null) {
            return getVioGenQueryExecutor(vioGenQuery.getConfiguration().getQueryExecutor());
        }
        if (task.getConfiguration().isUseSymmetricOptimization() && vioGenQuery.getFormula().isSymmetric()) {
            if (DependencyUtility.hasOnlyVariableInequalities(vioGenQuery)) {
                return getVioGenQueryExecutor(ExecuteVioGenQueryInequalityRAMRandomCPSymmetric.class.getName());
            } else {
                return getVioGenQueryExecutor(ExecuteVioGenQuerySymmetricMainMemoryRandom.class.getName());
            }
        } else {
            CrossProductFormulas crossProductFormula = vioGenQuery.getFormula().getCrossProductFormulas();
            if (crossProductFormula == null || crossProductFormula.getTableAliasInCrossProducts().size() == 1) {
                return getVioGenQueryExecutor(ExecuteVioGenQueryStandardMainMemoryRandom.class.getName());
            } else if (crossProductFormula.getTableAliasInCrossProducts().size() == 2) {
                return getVioGenQueryExecutor(ExecuteVioGenQueryInequalityRAMRandomCP.class.getName());
            } else {
                return getVioGenQueryExecutor(ExecuteVioGenQueryInequalityRAMRandomCPMultiQuery.class.getName());
            }
        }
    }

//    private IVioGenQueryExecutor getVioGenQueryExecutor(Class executorClass) {
    private IVioGenQueryExecutor getVioGenQueryExecutor(String executorClass) {
        try {
            IVioGenQueryExecutor executor = vioGenQueryExecutor.get(executorClass);
            if (executor == null) {
                Class c = Class.forName(executorClass);
                executor = (IVioGenQueryExecutor) c.newInstance();
                vioGenQueryExecutor.put(executorClass, executor);
            }
            return executor;
        } catch (InstantiationException ex) {
            throw new ErrorGeneratorException(ex);
        } catch (IllegalAccessException ex) {
            throw new ErrorGeneratorException(ex);
        } catch (ClassNotFoundException ex) {
            throw new ErrorGeneratorException(ex);
        }
    }

    private IDetectViolations getDependencyExecutor(Class executorClass) {
        try {
            IDetectViolations executor = dependencyExecutor.get(executorClass.getName());
            if (executor == null) {
                executor = (IDetectViolations) executorClass.newInstance();
                dependencyExecutor.put(executorClass.getName(), executor);
            }
            return executor;
        } catch (InstantiationException ex) {
            throw new ErrorGeneratorException(ex);
        } catch (IllegalAccessException ex) {
            throw new ErrorGeneratorException(ex);
        }
    }
}
