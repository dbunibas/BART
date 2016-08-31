package bart.test.operator.generator;

import bart.model.EGTask;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.operator.APrioriGenerator;
import bart.test.utility.UtilityTest;
import bart.utility.ErrorGeneratorStats;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPartialOrderErrors extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestPartialOrderErrors.class);
    private APrioriGenerator generator = new APrioriGenerator();
    private EGTask task;

    @Override
    protected void setUp() throws Exception {
        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getResourcesFolder("examplepartialorder/") + "egtask.xml");
        task.getConfiguration().setPrintLog(true);
//        task.getConfiguration().setDebug(true);
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
