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

public class TestInstanceSimilaritySimmetric extends TestCase {

    private final static Logger logger = LoggerFactory.getLogger(TestInstanceSimilaritySimmetric.class);
    private IComputeInstanceSimilarity similarityCheckerBruteForce = new ComputeInstanceSimilarityBruteForce();
    private IComputeInstanceSimilarity similarityCheckerBruteForceCompatibility = new ComputeInstanceSimilarityBruteForceCompatibility();
    private IComputeInstanceSimilarity similarityCheckerHashing = new ComputeInstanceSimilarityHashing();
    private static String BASE_FOLDER = "/resources/similarity/";

    private String[] INSTANCES = new String[]{
        "00", "01", "02", "03", "04", "05", "06",
        "07", "08",
        "09",
        "10",
        "11_diffnulls", "11_samenull", "12", "13", "14", "15", "16", "17", "18"
    };

    public void test() {
        for (String instance : INSTANCES) {
            if (logger.isDebugEnabled()) logger.debug("#### INSTANCE " + instance + " ####");
            IDatabase leftDb = ComparisonUtilityTest.loadDatabase(instance + "/left", BASE_FOLDER);
            IDatabase rightDb = ComparisonUtilityTest.loadDatabase(instance + "/right", BASE_FOLDER);
            compareInstances(leftDb, rightDb, instance, true, true, true); //Functional, Injective, Exaustive
//            compareInstances(leftDb, rightDb, instance, false, false, true); //Non Functional, Non Injective, Exaustive
        }
    }

    private void compareInstances(IDatabase leftDb, IDatabase rightDb, String instance, boolean functional, boolean injective, boolean exaustive) {
        ComparisonConfiguration.setFunctional(functional);
        ComparisonConfiguration.setInjective(injective);
        ComparisonConfiguration.setForceExaustiveSearch(exaustive);
        StringBuilder configuration = new StringBuilder();
        configuration.append(functional ? "functional" : "non functional").append(" - ");
        configuration.append(injective ? "injective" : "non injective").append(" - ");
        configuration.append(exaustive ? "exaustive" : "non exaustive");
        if (logger.isDebugEnabled()) logger.debug("- " + configuration);
        checkSimmetry(leftDb, rightDb, similarityCheckerBruteForce, instance);
        checkSimmetry(leftDb, rightDb, similarityCheckerBruteForceCompatibility, instance);
//        checkSimmetry(leftDb, rightDb, similarityCheckerHashing, instance);
    }

    private void checkSimmetry(IDatabase leftDb, IDatabase rightDb, IComputeInstanceSimilarity operator, String instance) {
        InstanceMatchTask lrMatch = operator.compare(leftDb, rightDb);
        InstanceMatchTask rlMatch = operator.compare(rightDb, leftDb);
//        System.out.println(leftDb + "\n " + rightDb + " \n" + rlMatch);
        if (ComparisonUtilityTest.isDifferent(lrMatch.getTupleMapping().getScore(), rlMatch.getTupleMapping().getScore())) {
            if (logger.isErrorEnabled()) logger.error("Different results btw L->R and R->L on scenario " + instance + ":\n" + lrMatch + "\n" + rlMatch);
        }
    }

}
