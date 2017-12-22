package bart.test.comparison;

import bart.comparison.ComparisonConfiguration;
import bart.comparison.InstanceMatchTask;
import bart.comparison.operators.ComputeInstanceSimilarityHashing;
import bart.comparison.operators.ComputeInstanceSimilarityBruteForce;
import bart.comparison.operators.ComputeInstanceSimilarityBruteForceCompatibility;
import bart.comparison.operators.IComputeInstanceSimilarity;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;

public class TestInstanceSimilarity extends TestCase {

    private final static Logger logger = LoggerFactory.getLogger(TestInstanceSimilarity.class);
    private IComputeInstanceSimilarity similarityCheckerBruteForce = new ComputeInstanceSimilarityBruteForce();
    private IComputeInstanceSimilarity similarityCheckerBruteForceCompatibility = new ComputeInstanceSimilarityBruteForceCompatibility();
    private IComputeInstanceSimilarity similarityCheckerHashing = new ComputeInstanceSimilarityHashing();
    private static String BASE_FOLDER = "/resources/similarity/";

    private String[] INSTANCES = new String[]{
        "00", "01", "02", "03", "04", "05", 
        "06",
        "07", "08", "09",
        "10",
        "11_diffnulls", "11_samenull", "12", "13", "14", "15", "16", "17", "18"
    };

    public void test() {
        for (String instance : INSTANCES) {
            if (logger.isDebugEnabled()) logger.debug("#### INSTANCE " + instance + " ####");
            IDatabase leftDb = ComparisonUtilityTest.loadDatabase(instance + "/left", BASE_FOLDER);
            IDatabase rightDb = ComparisonUtilityTest.loadDatabase(instance + "/right", BASE_FOLDER);
            compareInstances(leftDb, rightDb, instance, true, true, true); //Functional, Injective, Exaustive
//            compareInstances(leftDb, rightDb, instance, true, false, true);
//            compareInstances(leftDb, rightDb, instance, false, true, true);
//            compareInstances(leftDb, rightDb, instance, false, false, true);
//            compareInstances(leftDb, rightDb, instance, true, true, false);
//            compareInstances(leftDb, rightDb, instance, true, false, false);
//            compareInstances(leftDb, rightDb, instance, false, true, false);
//            compareInstances(leftDb, rightDb, instance, false, false, false);
        }
    }

    private void compareInstances(IDatabase leftDb, IDatabase rightDb, String instance, boolean functional, boolean injective, boolean exaustive) {
        IntegerOIDGenerator.resetCounter();
        ComparisonConfiguration.reset();
        ComparisonConfiguration.setFunctional(functional);
        ComparisonConfiguration.setInjective(injective);
        ComparisonConfiguration.setForceExaustiveSearch(exaustive);
        StringBuilder configuration = new StringBuilder();
        configuration.append(functional ? "functional" : "non functional").append(" - ");
        configuration.append(injective ? "injective" : "non injective").append(" - ");
        configuration.append(exaustive ? "exaustive" : "non exaustive");
        if (logger.isDebugEnabled()) logger.debug("- " + configuration);
        InstanceMatchTask resultBrute = similarityCheckerBruteForce.compare(leftDb, rightDb);
        if (logger.isTraceEnabled()) logger.trace(resultBrute.toString());
        InstanceMatchTask resultCompatibility = similarityCheckerBruteForceCompatibility.compare(leftDb, rightDb);
        if (logger.isDebugEnabled()) logger.debug(resultCompatibility.toString());
        InstanceMatchTask resultHashing = similarityCheckerHashing.compare(leftDb, rightDb);
        if (logger.isDebugEnabled()) logger.debug(resultHashing.toString());
        if (ComparisonUtilityTest.isDifferent(resultBrute.getTupleMapping().getScore(), resultCompatibility.getTupleMapping().getScore())) {
            if (logger.isErrorEnabled()) logger.error("Different results btw BruteForce and BruteForceCompatibility.\n" + printResults(resultBrute, resultCompatibility, instance, configuration));
        }
        if (ComparisonUtilityTest.isDifferent(resultCompatibility.getTupleMapping().getScore(), resultHashing.getTupleMapping().getScore())) {
            if (logger.isErrorEnabled()) logger.error("Different results btw BruteForceCompatibility and Hashing.\n" + printResults(resultCompatibility, resultHashing, instance, configuration));
        }
    }

    private String printResults(InstanceMatchTask resultA, InstanceMatchTask resultB, String instance, StringBuilder configuration) {
        StringBuilder sb = new StringBuilder();
        sb.append("* Instance: ").append(instance).append("\n");
        sb.append("* Configuration: ").append(configuration).append("\n");
        sb.append("-> ").append(resultA.getStrategy()).append(": ").append(resultA.getTupleMapping().getScore()).append("\n");
        sb.append("-> ").append(resultB.getStrategy()).append(": ").append(resultB.getTupleMapping().getScore()).append("\n");
        sb.append("--------------------\n").append(resultA).append("\n");
        sb.append("--------------------\n").append(resultB).append("\n");
        return sb.toString();
    }

}
