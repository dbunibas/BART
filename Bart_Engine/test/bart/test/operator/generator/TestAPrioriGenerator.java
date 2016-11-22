package bart.test.operator.generator;

import bart.model.EGTask;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.operator.APrioriGenerator;
import bart.test.utility.UtilityTest;
import bart.utility.ErrorGeneratorStats;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestAPrioriGenerator extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestAPrioriGenerator.class);
    private APrioriGenerator generator = new APrioriGenerator();
    private EGTask task;

    @Override
    public void setUp() {
//        task = UtilityTest.loadEGTaskFromResources("employees/employees-egtask.xml");
//        task = UtilityTest.loadEGTaskFromResources("employees/employees-random-egtask.xml");
        task = UtilityTest.loadEGTaskFromResources("employees/employees-dbms-egtask.xml");
//        task = UtilityTest.loadEGTaskFromResources("employees/employees-dbms-2k-egtask.xml");
//        task.getConfiguration().setGenerateAllChanges(true);
//        task.getConfiguration().setUseSymmetricOptimization(false);
//        task.getConfiguration().setGenerateAllChanges(true);
//        task.getConfiguration().setHandleInteraction(false);
//        task.getConfiguration().setPrintLog(true);
//        task.getConfiguration().setDebug(true);
//        task.getConfiguration().setApplyCellChanges(false);
//        task.getConfiguration().getDefaultVioGenQueryConfiguration().setPercentage(0.1);
        //
        task.getConfiguration().setEstimateRepairability(true);
        task.getConfiguration().setApplyCellChanges(true);
        task.getConfiguration().setUseDeltaDBForChanges(true);
        task.getConfiguration().setCheckChanges(true);
    }

    public void testRun() {
        CellChanges cellChanges = generator.run(task);
        if (logger.isDebugEnabled()) logger.debug("Number of changes: " + cellChanges.getChanges().size());
        if (cellChanges.getChanges().size() < 100) {
            if (logger.isDebugEnabled()) logger.debug("Cell changes: " + cellChanges);
        }
        if (logger.isInfoEnabled()) logger.info(ErrorGeneratorStats.getInstance().toString());
    }

}
