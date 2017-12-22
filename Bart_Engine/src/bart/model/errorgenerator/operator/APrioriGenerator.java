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
import bart.model.detection.operator.EstimateRepairability;
import bart.model.errorgenerator.VioGenQueryCellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ICellChange;
import bart.model.errorgenerator.OutlierCellChange;
import bart.model.errorgenerator.RandomCellChange;
import bart.utility.BartUtility;
import java.util.Date;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APrioriGenerator implements IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(APrioriGenerator.class);

    private DetectViolations cleanInstanceChecker = new DetectViolations();
    private GenerateVioGenQueries vioGenQueriesGenerator = new GenerateVioGenQueries();
    private ExecuteVioGenQueries vioGenQueriesExecutor = new ExecuteVioGenQueries();
    private DetectViolations violationsDetector = new DetectViolations();
    private CheckDetectableCellChanges changeChecker = new CheckDetectableCellChanges();
    private EstimateRepairability repairabilityEstimator = new EstimateRepairability();
    private ExportCellChangesCSV cellChangesExporter = new ExportCellChangesCSV();
    private ExecuteRandomErrors randomErrors = new ExecuteRandomErrors();
    private ExecuteOutlierErrors outlierErrors = new ExecuteOutlierErrors();

    private IExportDatabase databaseExporter;
    private IChangeApplier changeApplier;

    public CellChanges run(EGTask task) throws ErrorGeneratorException {
        intitializeOperators(task);
        long start = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Executing APriori Algorithm on task " + task);
        EGTaskConfiguration configuration = task.getConfiguration();
        if (configuration.isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
//        if (configuration.isPrintLog()) System.out.println("*** Executing a priori algorithm on task\n" + task);
        if (configuration.isCheckCleanInstance()) {
            cleanInstanceChecker.check(task.getDCs(), task.getSource(), task.getTarget(), task);
        }
        if (configuration.isExportCleanDB()) {
            String path = configuration.getExportCleanDBPath();
            if (configuration.isPrintLog()) System.out.println("Exporting clean db to path " + path);
            databaseExporter.export(task.getTarget(), "clean", path, task.getAbsolutePath());
        }
        generateVioGenQueries(task);
        long startChanges = new Date().getTime();
        CellChanges cellChanges = vioGenQueriesExecutor.executeVioGenQueries(task);
        CellChanges outlierCellChanges = executeOutlierCellChanges(task, cellChanges);
        BartUtility.mergeChanges(cellChanges, outlierCellChanges);
        CellChanges randomCellChanges = executeRandomCellChanges(task, cellChanges);
        BartUtility.mergeChanges(cellChanges, randomCellChanges);
        if (configuration.isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        long endChanges = new Date().getTime();
        if (configuration.isPrintLog()) System.out.println("Changes have been generated... Time " + (endChanges - startChanges) + " ms");
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.GENERATE_CHANGES_TIME, endChanges - startChanges);
        if (configuration.isDebug()) System.out.println(cellChanges);
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
        if (configuration.isExportCellChanges()) {
            String path = configuration.getExportCellChangesPath();
            if (configuration.isPrintLog()) System.out.println("Exporting changes to path " + path);
            cellChangesExporter.export(cellChanges, path, task.getAbsolutePath(), configuration.isExportCellChangesFull());
        }
        if (configuration.isExportDirtyDB()) {
            String path = configuration.getExportDirtyDBPath();
            if (configuration.isPrintLog()) System.out.println("Exporting dirty db to path " + path);
            databaseExporter.export(task.getDirtyTarget(), "dirty", cellChanges, path, task.getAbsolutePath());
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

    private CellChanges executeOutlierCellChanges(EGTask task, CellChanges detectableChanges) {
        if (!task.getConfiguration().isOutlierErrors()) return null;
        if (task.getConfiguration().isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        if (task.getConfiguration().isPrintLog()) System.out.println("*** Step 3: Executing outlier cell changes");
        if (task.getConfiguration().isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        long start = System.currentTimeMillis();
        CellChanges cellChanges = outlierErrors.execute(task, detectableChanges);
        long end = System.currentTimeMillis();
        long seconds = (end - start) / 1000;
        if (task.getConfiguration().isPrintLog()) System.out.println("Time for generating outliers (s): " + seconds);
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.NUMBER_CHANGES, cellChanges.getChanges().size());
        return cellChanges;
    }

    private CellChanges executeRandomCellChanges(EGTask task, CellChanges detectableChanges) {
        if (!task.getConfiguration().isRandomErrors()) return null;
        if (task.getConfiguration().isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        if (task.getConfiguration().isPrintLog()) System.out.println("*** Step 4: Executing random cell changes");
        if (task.getConfiguration().isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        CellChanges cellChanges = randomErrors.execute(task, detectableChanges);
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.NUMBER_CHANGES, cellChanges.getChanges().size());
        return cellChanges;
    }

    private void checkChanges(CellChanges cellChanges, EGTask task) {
        if (!task.getConfiguration().isApplyCellChanges() || !task.getConfiguration().isCloneTargetSchema()) {
            System.out.println("In order to check changes, please change set applyCellChanges and cloneTargetSchema to true");
            return;
        }
        Violations violations = violationsDetector.findViolations(task.getSource(), task.getDirtyTarget(), task);
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.NUMBER_VIOLATIONS, violations.getTotalViolations());
        if (task.getConfiguration().isPrintLog()) System.out.println(violations.toString());
        Set<ICellChange> nonDetectableChanges = changeChecker.findNonDetectableChanges(cellChanges, violations, task);
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.NUMBER_NON_DETECTABLE_CHANGES, nonDetectableChanges.size());
        Set<ICellChange> onlyOnceDetectable = changeChecker.findChangesDetectableOnce(cellChanges, violations, task);
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.NUMBER_ONLYONCE_CHANGES, onlyOnceDetectable.size());
        if (task.getConfiguration().isPrintLog()) System.out.println("Only once detectable: " + onlyOnceDetectable.size());
        if (task.getConfiguration().isEstimateRepairability()) {
            repairabilityEstimator.estimateRepairability(cellChanges, violations, task);
        }
        if (!nonDetectableChanges.isEmpty() && task.getConfiguration().isAvoidInteractions()) {
            StringBuilder sb = new StringBuilder();
            for (ICellChange nonDetectableChange : nonDetectableChanges) {
                if (nonDetectableChange.getType().equals(BartConstants.VIOGEN_CHANGE)) {
                    VioGenQueryCellChange vioGenQueryCellChange = (VioGenQueryCellChange) nonDetectableChange;
                    sb.append("\t").append(nonDetectableChange.toString()).append(" [").append(vioGenQueryCellChange.getVioGenQuery().toShortString()).append("]\n");
                    sb.append("\t\t").append(vioGenQueryCellChange.getContext().toString()).append("\n");
                }
                if (nonDetectableChange.getType().equals(BartConstants.OUTLIER_CHANGE)) {
                    OutlierCellChange outlierCellChange = (OutlierCellChange) nonDetectableChange;
                    sb.append(outlierCellChange.toLongString()).append("\n");
                }
                if (nonDetectableChange.getType().equals(BartConstants.RANDOM_CHANGE)) {
                    RandomCellChange randomCellChange = (RandomCellChange) nonDetectableChange;
                    sb.append(randomCellChange.toLongString());
                }
            }
            if (logger.isDebugEnabled()) logger.debug("Non detectable changes:\n" + sb.toString());
            if (task.getConfiguration().isPrintLog()) System.out.println("Non detectable changes:\n" + sb.toString());
        } else {
            if (logger.isDebugEnabled()) logger.debug("All changes are detectable");
            if (task.getConfiguration().isPrintLog()) System.out.println("All changes are detectable");
        }
    }

    @Override
    public void intitializeOperators(EGTask task) {
        this.changeApplier = OperatorFactory.getInstance().getChangeApplier(task);
        this.databaseExporter = OperatorFactory.getInstance().getDatabaseExporter(task);
    }

}
