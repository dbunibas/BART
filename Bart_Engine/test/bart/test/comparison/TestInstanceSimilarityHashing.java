package bart.test.comparison;

import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bart.comparison.ComparisonConfiguration;
import bart.comparison.InstanceMatchTask;
import bart.comparison.operators.ComputeInstanceSimilarityHashing;
import bart.comparison.operators.IComputeInstanceSimilarity;
import speedy.model.database.AttributeRef;
import speedy.model.database.IDatabase;
import speedy.model.database.NullValue;
import speedy.model.database.TupleWithTable;

public class TestInstanceSimilarityHashing extends TestCase {

    private final static Logger logger = LoggerFactory.getLogger(TestInstanceSimilarityHashing.class);

    private IComputeInstanceSimilarity similarityChecker = new ComputeInstanceSimilarityHashing();
    private static String BASE_FOLDER = "/resources/similarity/";

    public void test0() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("00/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("00/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertEquals("_N15", result.getTupleMapping().getLeftToRightMappingForValue(new NullValue("_N5")).toString());
        assertEquals("_N16", result.getTupleMapping().getLeftToRightMappingForValue(new NullValue("_N6")).toString());
        assertEquals("_N17", result.getTupleMapping().getLeftToRightMappingForValue(new NullValue("_N7")).toString());
        assertEquals("_N18", result.getTupleMapping().getLeftToRightMappingForValue(new NullValue("_N8")).toString());
        assertEquals(0, result.getTupleMapping().getLeftNonMatchingTuples().size());
        assertEquals(0, result.getTupleMapping().getRightNonMatchingTuples().size());
        assertEquals(1.0, result.getTupleMapping().getScore());
    }

    public void test1() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(false);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("01/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("01/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertEquals(0, result.getTupleMapping().getLeftNonMatchingTuples().size());
        assertEquals(0.91, result.getTupleMapping().getScore(), 0.01);
    }

    public void test2() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(false);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("02/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("02/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertEquals(1, result.getTupleMapping().getLeftNonMatchingTuples().size());
//        assertEquals(0.71, result.getTupleMapping().getScore(), 0.01);
        assertTrue(0.71 > result.getTupleMapping().getScore()); //Greedy
    }

    public void test3() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(false);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("03/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("03/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertEquals(3, result.getTupleMapping().getLeftNonMatchingTuples().size());
//        assertEquals(14.5, result.getTupleMapping().getScore());
        for (TupleWithTable nonMatchingTuple : result.getTupleMapping().getLeftNonMatchingTuples()) {
            if (nonMatchingTuple.getTable().equals("s")) {
                assertEquals("_N11", nonMatchingTuple.getTuple().getCell(new AttributeRef("s", "A")).getValue().toString());
            }
        }
        assertEquals(0.39875, result.getTupleMapping().getScore(), 0.01);
    }

    public void test4() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(false);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("04/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("04/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertNotNull(result.getTupleMapping());
        assertEquals(0, result.getTupleMapping().getLeftNonMatchingTuples().size());
        assertEquals(0.75, result.getTupleMapping().getScore(), 0.01);
    }

    public void test5() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(false);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("05/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("05/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertNotNull(result.getTupleMapping());
        assertEquals(0, result.getTupleMapping().getLeftNonMatchingTuples().size());
        assertEquals(0, result.getTupleMapping().getRightNonMatchingTuples().size());
        assertEquals((4 + 4 * ComparisonConfiguration.getK()) / 8.0, result.getTupleMapping().getScore());
    }

    public void test6() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("06/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("06/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertNotNull(result.getTupleMapping());
        assertEquals(1, result.getTupleMapping().getTupleMapping().size());
//        assertEquals("_N3", result.getTupleMapping().getLeftToRightMappingForValue(new NullValue("_N4")).toString());
//        assertEquals("1", result.getTupleMapping().getRightToLeftMappingForValue(new NullValue("_N2")).toString());
//        assertEquals("3", result.getTupleMapping().getLeftToRightMappingForValue(new NullValue("_N5")).toString());
        assertEquals(2, result.getTupleMapping().getLeftNonMatchingTuples().size());
        assertTrue(0.33 > result.getTupleMapping().getScore());
    }

    public void test7() {
        ComparisonConfiguration.setFunctional(false);
        ComparisonConfiguration.setInjective(false);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("07/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("07/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertNotNull(result.getTupleMapping());
        assertEquals(1, result.getTupleMapping().getTupleMapping().size());
        assertEquals(2, result.getTupleMapping().getTupleMapping().values().iterator().next().size());
        assertEquals("1", result.getTupleMapping().getLeftToRightMappingForValue(new NullValue("_N2")).toString());
        assertEquals("2", result.getTupleMapping().getLeftToRightMappingForValue(new NullValue("_N3")).toString());
        assertEquals("3", result.getTupleMapping().getRightToLeftMappingForValue(new NullValue("_N1")).toString());
        assertEquals(0, result.getTupleMapping().getLeftNonMatchingTuples().size());
        assertEquals(1, result.getTupleMapping().getRightNonMatchingTuples().size());
        assertEquals(0.43, result.getTupleMapping().getScore(), 0.01);
    }

    public void test8() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("08/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("08/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertNotNull(result.getTupleMapping());
        assertEquals(7, result.getTupleMapping().getTupleMapping().size());
        assertEquals("3", result.getTupleMapping().getLeftToRightMappingForValue(new NullValue("_N1")).toString());
        assertEquals("3", result.getTupleMapping().getRightToLeftMappingForValue(new NullValue("_N4")).toString());
        assertEquals("_N2", result.getTupleMapping().getRightToLeftMappingForValue(new NullValue("_N5")).toString());
        assertEquals("_N2", result.getTupleMapping().getRightToLeftMappingForValue(new NullValue("_N5")).toString());
        assertEquals("9", result.getTupleMapping().getLeftToRightMappingForValue(new NullValue("_N3")).toString());
        assertEquals("9", result.getTupleMapping().getRightToLeftMappingForValue(new NullValue("_N7")).toString());
        assertEquals("9", result.getTupleMapping().getRightToLeftMappingForValue(new NullValue("_N8")).toString());
        assertEquals(0, result.getTupleMapping().getLeftNonMatchingTuples().size());
        assertEquals(0, result.getTupleMapping().getRightNonMatchingTuples().size());
        assertEquals(0.83333, result.getTupleMapping().getScore(), 0.01);
    }

    public void test9() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("09/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("09/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertNotNull(result.getTupleMapping());
        assertEquals(3, result.getTupleMapping().getTupleMapping().size());
        assertEquals("3", result.getTupleMapping().getLeftToRightMappingForValue(new NullValue("_N3")).toString());
        assertEquals("3", result.getTupleMapping().getRightToLeftMappingForValue(new NullValue("_N4")).toString());
        assertEquals("3", result.getTupleMapping().getRightToLeftMappingForValue(new NullValue("_N9")).toString());
        assertEquals("3", result.getTupleMapping().getRightToLeftMappingForValue(new NullValue("_N0")).toString());
        assertEquals(0, result.getTupleMapping().getLeftNonMatchingTuples().size());
        assertEquals(0, result.getTupleMapping().getRightNonMatchingTuples().size());
        assertEquals(0.54629, result.getTupleMapping().getScore(), 0.01);
    }

    public void test10() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setForceExaustiveSearch(true);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("10/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("10/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertNotNull(result.getTupleMapping());
        assertEquals(6, result.getTupleMapping().getTupleMapping().size());
        assertEquals("_N0", result.getTupleMapping().getLeftToRightMappingForValue(new NullValue("_N2")).toString());
        assertEquals(0, result.getTupleMapping().getLeftNonMatchingTuples().size());
        assertEquals(0, result.getTupleMapping().getRightNonMatchingTuples().size());
//        assertEquals(0.8333, result.getTupleMapping().getScore(), 0.01);
        assertTrue(0.8333 > result.getTupleMapping().getScore());
    }

    public void test11_diffnulls() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("11_diffnulls/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("11_diffnulls/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertEquals((12 + ((8 / (double) 3) * ComparisonConfiguration.getK())) / 16.0, result.getTupleMapping().getScore());
    }

    public void test11_samenull() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("11_samenull/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("11_samenull/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertEquals((12 + 4 * ComparisonConfiguration.getK()) / 16.0, result.getTupleMapping().getScore());
    }

    public void test12() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("12/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("12/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertNotNull(result.getTupleMapping());
        assertEquals(1, result.getTupleMapping().getLeftNonMatchingTuples().size());
        assertEquals(1, result.getTupleMapping().getRightNonMatchingTuples().size());
        assertEquals((12 + 4 * ComparisonConfiguration.getK()) / 24.0, result.getTupleMapping().getScore());
    }

    public void test13() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("13/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("13/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertNotNull(result.getTupleMapping());
        assertEquals(0, result.getTupleMapping().getLeftNonMatchingTuples().size());
        assertEquals(0, result.getTupleMapping().getRightNonMatchingTuples().size());
//        assertEquals((12 + 4 * ComparisonConfiguration.getK()) / 24.0, result.getTupleMapping().getScore());
    }

    public void test14() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("14/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("14/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertNotNull(result.getTupleMapping());
        assertEquals(0, result.getTupleMapping().getLeftNonMatchingTuples().size());
        assertEquals(0, result.getTupleMapping().getRightNonMatchingTuples().size());
        assertEquals(0.888, result.getTupleMapping().getScore(), 0.01);
    }

    public void test18() {
        ComparisonConfiguration.setFunctional(true);
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("18/left", BASE_FOLDER);
        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("18/right", BASE_FOLDER);
        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
        logger.info(result.toString());
        assertNotNull(result.getTupleMapping());
        assertEquals(1, result.getTupleMapping().getLeftNonMatchingTuples().size());
        assertEquals(1, result.getTupleMapping().getRightNonMatchingTuples().size());
        assertTrue(0.666 > result.getTupleMapping().getScore());
    }
    
    
//    public void testGenerated() {
//        ComparisonConfiguration.setFunctional(true);
//        ComparisonConfiguration.setInjective(true);
//        ComparisonConfiguration.setForceExaustiveSearch(true);
//        IDatabase leftDb = ComparisonUtilityTest.loadDatabase("/Temp/comparison/redundancy/conference/left/");
//        IDatabase rightDb = ComparisonUtilityTest.loadDatabase("/Temp/comparison/redundancy/conference/right/");
//        InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
//        logger.info(result.toString());
//        assertNotNull(result.getTupleMapping());
////        assertEquals(0.6, result.getTupleMapping().getScore(), 0.01);
//    }

    @Override
    public void tearDown() {
        ComparisonConfiguration.reset();
    }
}
