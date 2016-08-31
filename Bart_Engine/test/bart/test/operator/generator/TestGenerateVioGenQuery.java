package bart.test.operator.generator;

import bart.model.errorgenerator.operator.GenerateVioGenQueries;
import bart.model.EGTask;
import bart.model.errorgenerator.VioGenQuery;
import bart.model.dependency.Dependency;
import bart.test.utility.UtilityTest;
import bart.utility.BartUtility;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestGenerateVioGenQuery extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestGenerateVioGenQuery.class);
    private GenerateVioGenQueries vioGenQueriesGenerator = new GenerateVioGenQueries();
    private EGTask task;

    @Override
    public void setUp() {
        task = UtilityTest.loadEGTaskFromResources("employees/employees-egtask.xml");
        task.getConfiguration().setGenerateAllChanges(true);
    }

    public void testE1() {
        Dependency dependency = task.getDCs().get(0);
        List<VioGenQuery> vioGenQueries = vioGenQueriesGenerator.generateVioGenQueries(dependency, task);
        if (logger.isDebugEnabled()) logger.debug(BartUtility.printCollection(vioGenQueries));
        Assert.assertEquals(2, vioGenQueries.size());
        Assert.assertEquals("(n1 != n2)", vioGenQueries.get(0).getVioGenComparison().toString());
        Assert.assertEquals("(d1 == d2)", vioGenQueries.get(1).getVioGenComparison().toString());
    }

    public void testE2() {
        Dependency dependency = task.getDCs().get(1);
        List<VioGenQuery> vioGenQueries = vioGenQueriesGenerator.generateVioGenQueries(dependency, task);
        if (logger.isDebugEnabled()) logger.debug(BartUtility.printCollection(vioGenQueries));
        Assert.assertEquals(4, vioGenQueries.size());
        Assert.assertEquals("(n1 != n2)", vioGenQueries.get(0).getVioGenComparison().toString());
        Assert.assertEquals("(d1 != d2)", vioGenQueries.get(1).getVioGenComparison().toString());
        Assert.assertEquals("(d1 != \"Sales\")", vioGenQueries.get(2).getVioGenComparison().toString());
        Assert.assertEquals("(m1 == m2)", vioGenQueries.get(3).getVioGenComparison().toString());
    }

}
