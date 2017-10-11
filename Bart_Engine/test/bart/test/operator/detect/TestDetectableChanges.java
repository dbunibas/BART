package bart.test.operator.detect;

import bart.model.EGTask;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.operator.APrioriGenerator;
import bart.model.detection.Violations;
import bart.model.detection.operator.CheckDetectableCellChanges;
import bart.model.detection.operator.DetectViolations;
import bart.model.errorgenerator.ICellChange;
import bart.model.errorgenerator.VioGenQueryCellChange;
import bart.test.utility.UtilityTest;
import bart.utility.ErrorGeneratorStats;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDetectableChanges extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestDetectableChanges.class);
    private APrioriGenerator generator = new APrioriGenerator();
    private DetectViolations violationsDetector = new DetectViolations();
    private CheckDetectableCellChanges changeChecker = new CheckDetectableCellChanges();
    private EGTask task;

    @Override
    public void setUp() {
//        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getResourcesFolder("employees/") + "employees-dbms-5-egtask.xml");
//        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getResourcesFolder("employees/") + "employees-dbms-50-egtask.xml");
        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getResourcesFolder("employees/") + "employees-dbms-2k-egtask.xml");
//        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getResourcesFolder("employees/") + "employees-dbms-10k-egtask.xml");
//        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getResourcesFolder("customers/") + "customers-dbms-5k-egtask.xml");
//        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getExperimentsFolder("tax/") + "tax-dbms-5k-egtask.xml");
        //
//        task.getConfiguration().setGenerateAllChanges(true);
//        task.getConfiguration().setAvoidInteractions(true);
        //RANDOM
        task.getConfiguration().setGenerateAllChanges(false);
        task.getConfiguration().setAvoidInteractions(true);
        task.getConfiguration().getDefaultVioGenQueryConfiguration().setPercentage(1);
        ////
        task.getConfiguration().setApplyCellChanges(true);
        task.getConfiguration().setCloneTargetSchema(true);
        task.getConfiguration().setRandomErrors(false);
    }

    public void testRun() {
        CellChanges cellChanges = generator.run(task);
//        if (logger.isDebugEnabled()) logger.debug(cellChanges.toString());
        if (logger.isDebugEnabled()) logger.debug("Number of changes: " + cellChanges.getChanges().size());
        Violations violations = violationsDetector.findViolations(task.getSource(), task.getDirtyTarget(), task);
        if (logger.isDebugEnabled()) logger.debug(violations.toString());
//        if (logger.isDebugEnabled()) logger.debug(violations.toLongString());
        Set<ICellChange> nonDetectableChanges = changeChecker.findNonDetectableChanges(cellChanges, violations, task);
        if (logger.isDebugEnabled()) logger.debug(ErrorGeneratorStats.getInstance().toString());
        Set<ICellChange> onlyOnceDetectable = changeChecker.findChangesDetectableOnce(cellChanges, violations, task);
        if (logger.isDebugEnabled()) logger.debug("Only once detectable: " + onlyOnceDetectable.size());
        if (!nonDetectableChanges.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ICellChange nonDetectableChange : nonDetectableChanges) {
                VioGenQueryCellChange notDetectable = (VioGenQueryCellChange) nonDetectableChange;
                sb.append("\t").append(nonDetectableChange.toShortString()).append(" [").append(notDetectable.getVioGenQuery().toShortString()).append("]\n");
                sb.append("\t\t").append(notDetectable.getContext().toString()).append("\n");
            }
            if (logger.isDebugEnabled()) logger.debug("Non detectable changes:\n" + sb.toString());
            Assert.fail("Non detectable changes!");
        }
    }

}
