package bart.test.operator.detect;

import bart.model.EGTask;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import bart.model.detection.Violations;
import bart.model.detection.operator.DetectViolations;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ICellChange;
import bart.model.errorgenerator.operator.APrioriGenerator;
import speedy.persistence.relational.AccessConfiguration;
import bart.test.utility.UtilityTest;
import bart.utility.BartUtility;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDetectViolations extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestDetectViolations.class);
    private DetectViolations violationsDetector = new DetectViolations();
    private EGTask task;
    private APrioriGenerator generator = new APrioriGenerator();

    @Override
    public void setUp() {
//        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getResourcesFolder("employees/") + "employees-dbms-2k-egtask.xml");
//        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getResourcesFolder("employees/") + "employees-dbms-10k-egtask.xml");
        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getResourcesFolder("customers/") + "customers-dbms-5k-egtask.xml");
        //
        task.getConfiguration().setPrintLog(false);
        task.getConfiguration().setAvoidInteractions(false);
        task.getConfiguration().setGenerateAllChanges(true);
//        task.getConfiguration().setUseSymmetricOptimization(true);
        //RANDOM
        task.getConfiguration().setGenerateAllChanges(false);
        task.getConfiguration().setAvoidInteractions(true);
        task.getConfiguration().getDefaultVioGenQueryConfiguration().setPercentage(1);
        ////
        task.getConfiguration().setApplyCellChanges(true);
        task.getConfiguration().setCloneTargetSchema(true);

        task.getConfiguration().setDetectEntireEquivalenceClasses(false);
    }

    public void testRun() {
        CellChanges cellChanges = generator.run(task);
        if (logger.isDebugEnabled()) logger.debug("Changes: " + cellChanges.getChanges().size());
        for (ICellChange change : cellChanges.getChanges()) {
            if (logger.isTraceEnabled()) logger.trace(change.toString());
        }
        AccessConfiguration dirtyTargetAC = ((DBMSDB) task.getTarget()).getAccessConfiguration().clone();
        String dirtySuffix = BartUtility.getDirtyCloneSuffix(task);
        dirtyTargetAC.setSchemaName(dirtyTargetAC.getSchemaName() + dirtySuffix);
        IDatabase dirtyTarget = new DBMSDB(dirtyTargetAC);
        Violations violations = violationsDetector.findViolations(task.getSource(), dirtyTarget, task);
        if (logger.isDebugEnabled()) logger.debug(violations.toString());
//        if (logger.isDebugEnabled()) logger.debug(violations.toLongString());
    }

}
