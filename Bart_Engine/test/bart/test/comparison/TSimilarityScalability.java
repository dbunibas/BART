package bart.test.comparison;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bart.comparison.ComparisonConfiguration;
import bart.comparison.ComparisonStats;
import bart.comparison.InstanceMatchTask;
import bart.comparison.operators.ComputeInstanceSimilarityBlock;
import bart.comparison.operators.ComputeInstanceSimilarityBruteForceCompatibility;
import bart.comparison.operators.ComputeInstanceSimilarityHashing;
import bart.comparison.operators.ComputeInstanceSimilarityBruteForce;
import bart.comparison.operators.IComputeInstanceSimilarity;
import speedy.SpeedyConstants;
import speedy.model.database.IDatabase;
import speedy.utility.PrintUtility;

public class TSimilarityScalability extends TestCase {

    private final static Logger logger = LoggerFactory.getLogger(TSimilarityScalability.class);

    private IComputeInstanceSimilarity similarityCheckerBruteForce = new ComputeInstanceSimilarityBruteForce();
    private IComputeInstanceSimilarity similarityCheckerCompatibility = new ComputeInstanceSimilarityBruteForceCompatibility();
    private IComputeInstanceSimilarity similarityCheckerHashing = new ComputeInstanceSimilarityHashing();
    private IComputeInstanceSimilarity similarityCheckerBlock = new ComputeInstanceSimilarityBlock();

    public void testDoctors() {
        String baseFolder = "/Temp/comparison/doctors/";
        SpeedyConstants.setStringSkolemPrefixes(new String[]{"_SK", "_:e"});
        ComparisonConfiguration.setConvertSkolemInHash(true);
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setForceExaustiveSearch(true);
//        String[] sizes = new String[]{"10k", "100k", "500k", "1m"};
//        String[] sizes = new String[]{"500k"};
        String[] sizes = new String[]{"10k"};
        for (String size : sizes) {
            PrintUtility.printMessage("================ Size: " + size + " ================");
            IDatabase leftDb = ComparisonUtilityTest.loadDatabase(baseFolder + "Llunatic-" + size);
            IDatabase rightDb = ComparisonUtilityTest.loadDatabase(baseFolder + "RDFox-" + size);
            PrintUtility.printMessage(ComparisonStats.getInstance().toString());
            execute(leftDb, rightDb, similarityCheckerHashing);
//            execute(leftDb, rightDb, similarityCheckerCompatibility);
//            execute(leftDb, rightDb, similarityCheckerBruteForce);
//            execute(leftDb, rightDb, similarityCheckerBlock);
            PrintUtility.printMessage("===============================================");
        }
    }

    private void execute(IDatabase leftDb, IDatabase rightDb, IComputeInstanceSimilarity similarityChecker) {
        ComparisonStats.getInstance().resetStatistics();
        PrintUtility.printMessage("----------- " + similarityChecker.getClass().getSimpleName() + " -----------------");
        long start = System.currentTimeMillis();
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        long totalTime = System.currentTimeMillis() - start;
        if (logger.isTraceEnabled()) logger.trace(result.toString());
        PrintUtility.printInformation("Total Time: " + totalTime + " ms");
        PrintUtility.printMessage("Score: " + result.getTupleMapping().getScore());
        PrintUtility.printMessage("Non matching tuples: " + result.getTupleMapping().getLeftNonMatchingTuples().size());
        PrintUtility.printMessage(ComparisonStats.getInstance().toString());
        PrintUtility.printMessage("--------------------------------------------------");
    }
}
