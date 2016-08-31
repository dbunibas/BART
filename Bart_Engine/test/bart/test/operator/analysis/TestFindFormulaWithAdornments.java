package bart.test.operator.analysis;

import bart.model.EGTask;
import bart.model.dependency.Dependency;
import bart.model.dependency.analysis.FindFormulaWithAdornments;
import bart.test.utility.UtilityTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestFindFormulaWithAdornments {

    private static Logger logger = LoggerFactory.getLogger(TestFindFormulaWithAdornments.class);
    private FindFormulaWithAdornments symmetryFinder = new FindFormulaWithAdornments();
    private EGTask task;

    @Before
    public void setUp() {
        task = UtilityTest.loadEGTaskFromResources("misc/synthetic_01-egtask.xml");
        Assert.assertNotNull(task);
    }

    @Test
    public void testScenario() {
        Dependency d1 = task.getDependency("d1");
        symmetryFinder.findFormulaWithAdornments(d1.getPremise().getPositiveFormula(), task);
        if (logger.isDebugEnabled()) logger.debug("Dependency " + d1);
        Assert.assertTrue(d1.isSymmetric());
        Dependency d2 = task.getDependency("d2");
        symmetryFinder.findFormulaWithAdornments(d2.getPremise().getPositiveFormula(), task);
        if (logger.isDebugEnabled()) logger.debug("Dependency " + d2);
        Assert.assertFalse(d2.isSymmetric());
        Dependency d3 = task.getDependency("d3");
        symmetryFinder.findFormulaWithAdornments(d3.getPremise().getPositiveFormula(), task);
        if (logger.isDebugEnabled()) logger.debug("Dependency " + d3);
        Assert.assertTrue(d3.isSymmetric());
    }

}
