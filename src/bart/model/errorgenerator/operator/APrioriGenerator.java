package bart.model.errorgenerator.operator;

import bart.utility.ErrorGeneratorStats;
import bart.BartConstants;
import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.exceptions.ErrorGeneratorException;
import bart.model.EGTask;
import bart.model.EGTaskConfiguration;
import bart.model.dependency.Dependency;
import bart.model.detection.Violations;
import bart.model.detection.operator.CheckDetectableCellChanges;
import bart.model.detection.operator.DetectViolations;
import bart.model.detection.operator.EstimateRepairabilityAPosteriori;
import bart.model.errorgenerator.CellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.VioGenQuery;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import java.util.Date;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APrioriGenerator implements IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(APrioriGenerator.class);

    private DetectViolations cleanInstanceChecker = new DetectViolations();
    private GenerateVioGenQueries vioGenQueriesGenerator = new GenerateVioGenQueries();
    private SelectQueryExecutor executorSelector = new SelectQueryExecutor();
    private DetectViolations violationsDetector = new DetectViolations();
    private CheckDetectableCellChanges changeChecker = new CheckDetectableCellChanges();
    private EstimateRepairabilityAPosteriori repairabilityEstimator = new EstimateRepairabilityAPosteriori();
    private ExportCellChangesCSV cellChangesExporter = new ExportCellChangesCSV();
    private IExportDatabase databaseExporter;
    private IChangeApplier changeApplier;

    public CellChanges run(EGTask task) throws ErrorGeneratorException {
        intitializeOperators(task);
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Executing APriori Algorithm on task " + task);
        EGTaskConfiguration configuration = task.getConfiguration();
        if (configuration.isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        if (configuration.isPrintLog()) System.out.println("*** Executing a priori algorithm on task\n" + task);
        if (configuration.isCheckCleanInstance()) {
            cleanInstanceChecker.check(task.getDCs(), task.getSource(), task.getTarget(), task);
        }
        generateVioGenQueries(task);
        long startChanges = new Date().getTime();
        CellChanges cellChanges = executeVioGenQueries(task);
        if (configuration.isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        long endChanges = new Date().getTime();
        if (configuration.isPrintLog()) System.out.println("Changes have been generated... Time " + (endChanges - startChanges) + " ms");
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.GENERATE_CHANGES_TIME, endChanges - startChanges);
        if (configuration.isDebug()) System.out.println(cellChanges);
        if (configuration.isExportCellChanges()) {
            String path = configuration.getExportCellChangesPath();
            if (configuration.isPrintLog()) System.out.println("Exporting changes to path " + path);
            cellChangesExporter.export(cellChanges, path);
        }
        if(configuration.isExportDirtyDB()){
            String path = configuration.getExportDirtyDBPath();
            if (configuration.isPrintLog()) System.out.println("Exporting dirtydb to path " + path);
            databaseExporter.export(task.getTarget(), cellChanges, path);
        }
        if (configuration.isApplyCellChanges()) {
            if (configuration.isPrintLog()) System.out.println("Now applying changes to the db (this may be slow due to multiple updates...)");
            long startApplyChanges = new Date().getTime();
            changeApplier.apply(cellChanges, task);
            long endApplyChanges = new Date().getTime();
            ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.APPLY_CHANGES_TIME, endApplyChanges - startApplyChanges);
            if (configuration.isPrintLog()) System.out.println("Changes have been applied... Time " + (endApplyChanges - startApplyChanges) + " ms");
            if (configuration.isDebug()) System.out.println("\nResulting database\n" + task.getTarget().printInstances());
        }
        if (configuration.isCheckChanges()) {
            long startCheckChanges = new Date().getTime();
            checkChanges(cellChanges, task);
            long endCheckChanges = new Date().getTime();
            ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.CHECK_CHANGES_TIME, endCheckChanges - startCheckChanges);
            if (configuration.isPrintLog()) System.out.println("Changes have been checked... Time " + (endCheckChanges - startCheckChanges) + " ms");
        }
        long end = new Date().getTime();
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.TOTAL_TIME, end - start);
        return cellChanges;
    }

    private void generateVioGenQueries(EGTask task) {
        if (task.getConfiguration().isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        if (task.getConfiguration().isPrintLog()) System.out.println("*** Step 1: Generating vioGen queries");
        if (task.getConfiguration().isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        for (Dependency dc : task.getDCs()) {
            dc.setVioGenQueries(vioGenQueriesGenerator.generateVioGenQueries(dc, task));
            if (task.getConfiguration().isPrintLog()) System.out.println(BartUtility.printCollection(dc.getVioGenQueries()));
        }
        vioGenQueriesGenerator.setErrorPercentages(task);

    }

    private CellChanges executeVioGenQueries(EGTask task) {
        if (task.getConfiguration().isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        if (task.getConfiguration().isPrintLog()) System.out.println("*** Step 2: Executing vioGen queries");
        if (task.getConfiguration().isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        CellChanges allCellChanges = new CellChanges();
        for (Dependency dc : task.getDCs()) {
            for (VioGenQuery vioGenQuery : dc.getVioGenQueries()) {
                if (task.getConfiguration().isExcludeCrossProducts() && DependencyUtility.isCrossProduct(vioGenQuery.getFormula())) {
                    if (task.getConfiguration().isDebug()) System.out.println("Skipping cross product: " + vioGenQuery.toShortString());
                    if (logger.isDebugEnabled()) logger.debug("Skipping cross product: " + vioGenQuery.toShortString());
                    continue;
                }
                long start = new Date().getTime();
                int beforeChanges = allCellChanges.getChanges().size();
                IVioGenQueryExecutor executor = executorSelector.getExecutorForVioGenQuery(vioGenQuery, task);
                executor.execute(vioGenQuery, allCellChanges, task);
                int afterChanges = allCellChanges.getChanges().size();
                long end = new Date().getTime();
                ErrorGeneratorStats.getInstance().addVioGenQueryTime(vioGenQuery, end - start);
                ErrorGeneratorStats.getInstance().addVioGenQueryErrors(vioGenQuery, afterChanges - beforeChanges);
            }
        }
        if (logger.isDebugEnabled()) logger.debug(allCellChanges.toString());
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.NUMBER_CHANGES, allCellChanges.getChanges().size());
        return allCellChanges;
    }

    private void checkChanges(CellChanges cellChanges, EGTask task) {
        if (!task.getConfiguration().isApplyCellChanges() || !task.getConfiguration().isCloneTargetSchema()) {
            System.out.println("In order to check changes, please change set applyCellChanges and cloneTargetSchema to true");
            return;
        }
        Violations allViolations = violationsDetector.findViolations(task.getSource(), task.getDirtyTarget(), task);
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.NUMBER_VIOLATIONS, allViolations.getTotalViolations());
        if (task.getConfiguration().isPrintLog()) System.out.println(allViolations.toString());
        Set<CellChange> nonDetectableChanges = changeChecker.findNonDetectableChanges(cellChanges, allViolations, task);
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.NUMBER_NON_DETECTABLE_CHANGES, nonDetectableChanges.size());
        Set<CellChange> onlyOnceDetectable = changeChecker.findChangesDetectableOnce(cellChanges, allViolations, task);
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.NUMBER_ONLYONCE_CHANGES, onlyOnceDetectable.size());
        if (task.getConfiguration().isPrintLog()) System.out.println("Only once detectable: " + onlyOnceDetectable.size());
        if (task.getConfiguration().isEstimateAPosterioriRepairability()) {
            repairabilityEstimator.estimateRepairability(cellChanges, allViolations, task);
        }
        if (!nonDetectableChanges.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (CellChange nonDetectableChange : nonDetectableChanges) {
                sb.append("\t").append(nonDetectableChange.toString()).append(" [").append(nonDetectableChange.getVioGenQuery().toShortString()).append("]\n");
                sb.append("\t\t").append(nonDetectableChange.getGeneratingContext().toString()).append("\n");
            }
            if (logger.isDebugEnabled()) logger.debug("Non detectable changes:\n" + sb.toString());
            if (task.getConfiguration().isPrintLog()) System.out.println("Non detectable changes:\n" + sb.toString());
        } else {
            if (logger.isDebugEnabled()) logger.debug("All changes are detectable");
            if (task.getConfiguration().isPrintLog()) System.out.println("All changes are detectable");
        }
    }

    public void intitializeOperators(EGTask task) {
        this.changeApplier = OperatorFactory.getInstance().getChangeApplier(task);
        this.databaseExporter = OperatorFactory.getInstance().getDatabaseExporter(task);
    }

}
