package bart.test.operator.generator;

import bart.BartConstants;
import bart.exceptions.ErrorGeneratorException;
import bart.model.EGTask;
import speedy.model.algebra.operators.IUpdateCell;
import speedy.model.algebra.operators.sql.SQLUpdateCell;
import speedy.model.database.CellRef;
import speedy.model.database.IDatabase;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.operator.APrioriGenerator;
import speedy.model.database.operators.IDatabaseManager;
import speedy.model.database.operators.dbms.SQLDatabaseManager;
import bart.model.detection.operator.DetectViolations;
import bart.model.errorgenerator.ICellChange;
import bart.test.utility.UtilityTest;
import junit.framework.TestCase;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSingleDetectableChanges extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestSingleDetectableChanges.class);
    private APrioriGenerator generator = new APrioriGenerator();
    private IUpdateCell cellUpdater = new SQLUpdateCell();
    private DetectViolations cleanInstanceChecker = new DetectViolations();
    private IDatabaseManager databaseManager = new SQLDatabaseManager();
    private EGTask task;

    @Override
    public void setUp() {
        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getResourcesFolder("employees/") + "employees-dbms-5-egtask.xml");
//        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getResourcesFolder("employees/") + "employees-dbms-2k-egtask.xml");
//        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getResourcesFolder("employees/") + "employees-dbms-10k-egtask.xml");
//        task = UtilityTest.loadEGTaskFromAbsolutePath(UtilityTest.getResourcesFolder("customers/") + "customers-dbms-5k-egtask.xml");
        //CUSTOMERS
//        task.getConfiguration().setAvoidInteractions(false);
//        task.getConfiguration().setGenerateAllChanges(true);
//        task.getConfiguration().setUseSymmetricOptimization(true);
        //
        task.getConfiguration().setGenerateAllChanges(true);
        task.getConfiguration().setAvoidInteractions(false);
        //RANDOM
//        task.getConfiguration().setGenerateAllChanges(false);
//        task.getConfiguration().setAvoidInteractions(true);
//        task.getConfiguration().getDefaultVioGenQueryConfiguration().setPercentage(1);
    }

    public void testRun() {
        CellChanges cellChanges = generator.run(task);
        if (logger.isDebugEnabled()) logger.debug("Number of changes: " + cellChanges.getChanges().size());
        for (ICellChange cellChange : cellChanges.getChanges()) {
            IDatabase targetClone = databaseManager.cloneTarget(task.getTarget(), BartConstants.CLONE_SUFFIX);
            cellUpdater.execute(new CellRef(cellChange.getCell()), cellChange.getNewValue(), targetClone);
            try {
                cleanInstanceChecker.check(task.getDCs(), task.getSource(), targetClone, task);
                Assert.fail("Cell change did not introduce a violation " + cellChange.toLongString() + "\nOriginal\n" + task.getTarget().printInstances() + "\nDirty\n" + targetClone.printInstances());
            } catch (ErrorGeneratorException ex) {
                databaseManager.removeClone(task.getTarget(), BartConstants.CLONE_SUFFIX);
            }
        }
    }

}
