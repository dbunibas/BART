package bart.test.comparison.generator;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bart.comparison.ComparisonConfiguration;
import bart.comparison.ComparisonStats;
import bart.comparison.InstanceMatchTask;
import bart.comparison.generator.ComparisonScenarioGenerator;
import bart.comparison.generator.InstancePair;
import bart.comparison.operators.ComputeInstanceSimilarityBruteForceCompatibility;
import bart.comparison.operators.ComputeInstanceSimilarityHashing;
import bart.comparison.operators.ComputeInstanceSimilarityBruteForce;
import bart.comparison.operators.IComputeInstanceSimilarity;
import bart.test.comparison.ComparisonUtilityTest;
import speedy.model.database.IDatabase;
import speedy.persistence.file.operators.ExportCSVFile;
import speedy.utility.PrintUtility;
import speedy.utility.SpeedyUtility;

public class TestComparisonScenarioGenerator extends TestCase {
    
    private final static Logger logger = LoggerFactory.getLogger(TestComparisonScenarioGenerator.class);
    private final ComparisonScenarioGenerator generator = new ComparisonScenarioGenerator();
    private final IComputeInstanceSimilarity similarityCheckerBruteForce = new ComputeInstanceSimilarityBruteForce();
    private final IComputeInstanceSimilarity similarityCheckerCompatibility = new ComputeInstanceSimilarityBruteForceCompatibility();
    private final IComputeInstanceSimilarity similarityCheckerHashing = new ComputeInstanceSimilarityHashing();
    
    public void test() {
//        execute("conference");
//        execute("doctors-1k");
        execute("doctors-100");
    }

    private void execute(String scenarioName) {
//        setInjectiveFunctionalMapping();   //no new tuples
        setNonInjectiveFunctionalMapping();//new left tuples (default)
//        setInjectiveNonFunctionalMapping();//new right tuples * non functional mappings are very slow with brute force and compatibility *
//        setNonInjectiveNonFunctionalMapping();//new left/right tuples
        ComparisonConfiguration.setTwoWayValueMapping(true); //Change constants in nulls in both instances (default)
        ComparisonConfiguration.setForceExaustiveSearch(false);
        String expPath = "/Temp/comparison/redundancy/" + scenarioName;
        String sourceFile = expPath + "/initial/";
        IDatabase sourceDB = ComparisonUtilityTest.loadDatabase(sourceFile);
        InstancePair instancePair = generator.generate(sourceDB);
        if (logger.isTraceEnabled()) logger.trace(instancePair.toString());
        ExportCSVFile exporter = new ExportCSVFile();
        exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
        exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
        execute(instancePair.getLeftDB(), instancePair.getRightDB(), similarityCheckerHashing);
        execute(instancePair.getLeftDB(), instancePair.getRightDB(), similarityCheckerCompatibility);
        execute(instancePair.getLeftDB(), instancePair.getRightDB(), similarityCheckerBruteForce);
    }
    
    private void execute(IDatabase leftDb, IDatabase rightDb, IComputeInstanceSimilarity similarityChecker) {
        ComparisonStats.getInstance().resetStatistics();
        PrintUtility.printInformation("----------- " + similarityChecker.getClass().getSimpleName() + " -----------------");
        long start = System.currentTimeMillis();
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        long totalTime = System.currentTimeMillis() - start;
        if (logger.isTraceEnabled()) logger.trace(result.toString());
        PrintUtility.printInformation("Total Time: " + totalTime + " ms");
        PrintUtility.printInformation("Score: " + result.getTupleMapping().getScore());
        PrintUtility.printMessage("Non matching left tuples: " + result.getTupleMapping().getLeftNonMatchingTuples().size());
        if (!result.getTupleMapping().getLeftNonMatchingTuples().isEmpty() && result.getTupleMapping().getLeftNonMatchingTuples().size() < 5) {
            PrintUtility.printMessage(SpeedyUtility.printCollection(result.getTupleMapping().getLeftNonMatchingTuples(), "\t"));
        }
        PrintUtility.printMessage("Non matching right tuples: " + result.getTupleMapping().getRightNonMatchingTuples().size());
        if (!result.getTupleMapping().getRightNonMatchingTuples().isEmpty() && result.getTupleMapping().getRightNonMatchingTuples().size() < 5) {
            PrintUtility.printMessage(SpeedyUtility.printCollection(result.getTupleMapping().getRightNonMatchingTuples(), "\t"));
        }
        PrintUtility.printMessage(ComparisonStats.getInstance().toString());
        PrintUtility.printMessage("--------------------------------------------------");
    }
    
    private void setInjectiveFunctionalMapping() {
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setFunctional(true);
    }
    
    private void setNonInjectiveFunctionalMapping() {
        ComparisonConfiguration.setInjective(false);
        ComparisonConfiguration.setFunctional(true);
    }
    
    private void setInjectiveNonFunctionalMapping() {
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setFunctional(false);
    }
    
    private void setNonInjectiveNonFunctionalMapping() {
        ComparisonConfiguration.setInjective(false);
        ComparisonConfiguration.setFunctional(false);
    }
    
}
