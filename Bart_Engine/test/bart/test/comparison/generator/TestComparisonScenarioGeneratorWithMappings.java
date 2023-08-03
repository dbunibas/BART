package bart.test.comparison.generator;

import bart.comparison.ComparisonConfiguration;
import bart.comparison.InstanceMatchTask;
import bart.comparison.generator.ComparisonScenarioGeneratorWithMappings;
import bart.comparison.generator.InstancePair;
import bart.comparison.operators.ComputeInstanceSimilarityBruteForce;
import bart.comparison.operators.ComputeInstanceSimilarityHashing;
import bart.comparison.operators.ComputeScore;
import bart.comparison.operators.IComputeInstanceSimilarity;
import bart.test.comparison.ComparisonUtilityTest;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleWithTable;
import speedy.persistence.file.operators.ExportCSVFile;
import speedy.utility.SpeedyUtility;

public class TestComparisonScenarioGeneratorWithMappings extends TestCase {

    private final static Logger logger = LoggerFactory.getLogger(TestComparisonScenarioGeneratorWithMappings.class);

    private int newReduntandTuples = 1;
    private int newRandomTuples = 0; // max 75 after happens weird things
    private int cellsToChange = 82;
    private List<String> results = new ArrayList<>();
    private boolean printInfo = false;

    public void xtestExecutionSmall() {
        String dataset = "doctors-100";
        for (int nre = 0; nre <= 99; nre+=10) {
            for (int nra = 0; nra <= 99; nra+=10) {
                for (int cc = 0; cc <= 99; cc+=10) {
                    this.newReduntandTuples = nre;
                    this.newRandomTuples = nra;
                    this.cellsToChange = cc;
                    modifyCellsInSource(dataset, null, true, null);
                    modifyCellsInTarget(dataset, null, true, null);
                    modifyCellsInSourceAndTarget(dataset, null, true, null);
                    addRedundantRowsInSource(dataset, null, true, null);
                    addRedundantRowsInTarget(dataset, null, true, null);
                    addRedundantRowsInSourceAndTarget(dataset, null, true, null);
                    addRandomRowsInSource(dataset, null, true, null);
                    addRandomRowsInTarget(dataset, null, true, null);
                    addRandomRowsInSourceAndTarget(dataset, null, true, null);
                    addRandomAndRedundantRowsInSourceAndTarget(dataset, null, true, null);
                }
            }
        }
    }

    public void xtestExecution() {
        //"conference", "doctors-100", ,
        String[] datasets = {"conference"};
        for (String dataset : datasets) {
            System.out.println(dataset);
            modifyCellsInSource(dataset, null, true, null);
            modifyCellsInTarget(dataset, null, true, null);
            modifyCellsInSourceAndTarget(dataset, null, true, null);
            addRedundantRowsInSource(dataset, null, true, null);
            addRedundantRowsInTarget(dataset, null, true, null);
            addRedundantRowsInSourceAndTarget(dataset, null, true, null);
            addRandomRowsInSource(dataset, null, true, null);
            addRandomRowsInTarget(dataset, null, true, null);
            addRandomRowsInSourceAndTarget(dataset, null, true, null);
            addRandomAndRedundantRowsInSourceAndTarget(dataset, null, true, null);
            System.out.print(".\n");
        }
    }

    public void xtestExecutionGreedy() {
//"doctors-100", "doctors-1k"
        String[] datasets = {"conference", "doctors-100", "doctors-1k"};
        for (String dataset : datasets) {
            System.out.println(dataset);
            modifyCellsInSource(dataset, new ComputeInstanceSimilarityHashing(), true, null);
            System.out.print(".");
            modifyCellsInTarget(dataset, new ComputeInstanceSimilarityHashing(), true, null);
            System.out.print(".");
            modifyCellsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(), true, null);
            System.out.print(".");
            addRedundantRowsInSource(dataset, new ComputeInstanceSimilarityHashing(), true, null);
            System.out.print(".");
            addRedundantRowsInTarget(dataset, new ComputeInstanceSimilarityHashing(), true, null);
            System.out.print(".");
            addRedundantRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(), true, null);
            System.out.print(".");
            addRandomRowsInSource(dataset, new ComputeInstanceSimilarityHashing(), true, null);
            System.out.print(".");
            addRandomRowsInTarget(dataset, new ComputeInstanceSimilarityHashing(), true, null);
            System.out.print(".");
            addRandomRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(), true, null);
            System.out.print(".");
            addRandomAndRedundantRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(), true, null);
            System.out.print(".\n");
        }
        for (String result : this.results) {
            System.out.println(result);
        }
    }

    public void xtestExport() {
        // "conference", "doctors-100",
        String[] datasets = {"doctors-1k"};
        String exportPath = "/Users/enzoveltri/Desktop/instance-comparisons/";
        for (String dataset : datasets) {
            System.out.println(dataset);
            modifyCellsInSource(dataset, null, false, exportPath);
            System.out.print(".");
            modifyCellsInTarget(dataset, new ComputeInstanceSimilarityHashing(), false, exportPath);
            System.out.print(".");
            modifyCellsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(), false, exportPath);
            System.out.print(".");
            addRedundantRowsInSource(dataset, new ComputeInstanceSimilarityHashing(), false, exportPath);
            System.out.print(".");
            addRedundantRowsInTarget(dataset, new ComputeInstanceSimilarityHashing(), false, exportPath);
            System.out.print(".");
            addRedundantRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(), false, exportPath);
            System.out.print(".");
            addRandomRowsInSource(dataset, new ComputeInstanceSimilarityHashing(), false, exportPath);
            System.out.print(".");
            addRandomRowsInTarget(dataset, new ComputeInstanceSimilarityHashing(), false, exportPath);
            System.out.print(".");
            addRandomRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(), false, exportPath);
            System.out.print(".");
            addRandomAndRedundantRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(), false, exportPath);
            System.out.print(".\n");
        }
    }

    public void modifyCellsInSource(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
//        String scenarioName = "conference";
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setInjectiveFunctionalMapping();
        ComparisonConfiguration.setTwoWayValueMapping(true);
//        IDatabase originalDB = ComparisonUtilityTest.loadDatabase(sourceFile, "/resources/redundancy/");
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(0, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, false);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/modifyCellsInSource";
            exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
        }
        logger.info("InstancePair: {}", instancePair);
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            ComparisonConfiguration.setTwoWayValueMapping(true);
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) System.out.println("Time (ms):" + (end - start));
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            generateOutput("modifyCellsInSource", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");
        }
    }

    public void modifyCellsInTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
//        String scenarioName = "conference";
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setInjectiveFunctionalMapping();
//        IDatabase originalDB = ComparisonUtilityTest.loadDatabase(sourceFile, "/resources/redundancy/");
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(0, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), false, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/modifyCellsInTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
        }
        logger.info("InstancePair: {}", instancePair);
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) System.out.println("Time (ms):" + (end - start));
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            generateOutput("modifyCellsInTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void modifyCellsInSourceAndTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
//        String scenarioName = "conference";
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setInjectiveFunctionalMapping();
//        IDatabase originalDB = ComparisonUtilityTest.loadDatabase(sourceFile, "/resources/redundancy/");
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(0, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/modifyCellsInSourceAndTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
        }
        logger.info("InstancePair: {}", instancePair);
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) System.out.println("Time (ms):" + (end - start));
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + score + " Greedy: " + scoreSimilarity, scoreSimilarity == score);
            generateOutput("modifyCellsInSourceAndTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");
        }
    }

    public void addRedundantRowsInSource(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
//        String scenarioName = "conference";
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
//        IDatabase originalDB = ComparisonUtilityTest.loadDatabase(sourceFile, "/resources/redundancy/");
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(newReduntandTuples, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, false);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRedundantRowsInSource";
            exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
        }
        logger.info("InstancePair: {}", instancePair);
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) System.out.println("Time (ms):" + (end - start));
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            generateOutput("addRedundantRowsInSource", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void addRedundantRowsInTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
//        String scenarioName = "conference";
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
//        IDatabase originalDB = ComparisonUtilityTest.loadDatabase(sourceFile, "/resources/redundancy/");
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(newReduntandTuples, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), false, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRedundantRowsInTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
        }
        logger.info("InstancePair: {}", instancePair);
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) System.out.println("Time (ms):" + (end - start));
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            generateOutput("addRedundantRowsInTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void addRedundantRowsInSourceAndTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
//        String scenarioName = "conference";
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
//        IDatabase originalDB = ComparisonUtilityTest.loadDatabase(sourceFile, "/resources/redundancy/");
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(newReduntandTuples, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRedundantRowsInSourceAndTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
        }
        logger.info("InstancePair: {}", instancePair);
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) System.out.println("Time (ms):" + (end - start));
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            generateOutput("addRedundantRowsInSourceAndTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void addRandomRowsInSource(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
//        String scenarioName = "conference";
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
//        IDatabase originalDB = ComparisonUtilityTest.loadDatabase(sourceFile, "/resources/redundancy/");
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(0, newRandomTuples, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, false);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRandomRowsInSource";
            exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
        }
        logger.info("InstancePair: {}", instancePair);
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) System.out.println("Time (ms):" + (end - start));
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            generateOutput("addRandomRowsInSource", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void addRandomRowsInTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
//        String scenarioName = "conference";
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
//        IDatabase originalDB = ComparisonUtilityTest.loadDatabase(sourceFile, "/resources/redundancy/");
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(0, newRandomTuples, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), false, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRandomRowsInTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
        }
        logger.info("InstancePair: {}", instancePair);
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) System.out.println("Time (ms):" + (end - start));
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            generateOutput("addRandomRowsInTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void addRandomRowsInSourceAndTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
//        String scenarioName = "conference";
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
//        IDatabase originalDB = ComparisonUtilityTest.loadDatabase(sourceFile, "/resources/redundancy/");
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(0, newRandomTuples, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRandomRowsInSourceAndTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
        }
        logger.info("InstancePair: {}", instancePair);
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
//            ComparisonConfiguration.setForceExaustiveSearch(false);
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) System.out.println("Time (ms):" + (end - start));
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + score + " Greedy: " + scoreSimilarity, scoreSimilarity == score);
            generateOutput("addRandomRowsInSourceAndTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void addRandomAndRedundantRowsInSourceAndTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
//        String scenarioName = "conference";
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
//        IDatabase originalDB = ComparisonUtilityTest.loadDatabase(sourceFile, "/resources/redundancy/");
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(newReduntandTuples, newRandomTuples, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRandomAndRedundantRowsInSourceAndTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
        }
        logger.info("InstancePair: {}", instancePair);
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) System.out.println("Time (ms):" + (end - start));
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            generateOutput("addRandomAndRedundantRowsInSourceAndTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");
        }
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

    private void generateOutput(String methodName, InstancePair instancePair, double bruteForceScore, Double greedyScore, long timeGenerationInstances, long greedyTime, String bruteForceTime) {
        String configuration = this.cellsToChange + " | " + this.newReduntandTuples + " | " + this.newRandomTuples;
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        String sourceStats = getDBStats(sourceDB);
        String targetStats = getDBStats(targetDB);
        long tupleMappingSize = instancePair.getTupleMapping().getTupleMapping().size();
        long leftNonMatchingTuples = instancePair.getTupleMapping().getLeftNonMatchingTuples().size();
        long rightNonMatchingTuples = instancePair.getTupleMapping().getRightNonMatchingTuples().size();
        String experiment = configuration + "\t"
                + methodName + "\t"
                + sourceStats + "\t"
                + targetStats + "\t"
                + tupleMappingSize + "\t"
                + leftNonMatchingTuples + "\t"
                + rightNonMatchingTuples + "\t"
                + bruteForceScore + "\t"
                + greedyScore + "\t"
                + timeGenerationInstances + "\t"
                + greedyTime + "\t"
                + bruteForceTime;
        experiment = experiment.replace(".", ",");
        this.results.add(experiment);
    }

    private String getDBStats(IDatabase db) {
        long dbSize = 0;
        long vars = 0;
        long consts = 0;
        for (String tableName : db.getTableNames()) {
            ITable table = db.getTable(tableName);
            long tableSize = table.getSize();
            dbSize += tableSize;
            ITupleIterator tupleIterator = table.getTupleIterator();
            while (tupleIterator.hasNext()) {
                Tuple tuple = tupleIterator.next();
                for (Cell cell : tuple.getCells()) {
                    if (cell.getValue() instanceof NullValue) {
                        vars++;
                    }
                    if (cell.getValue() instanceof ConstantValue) {
                        consts++;
                    }
                }
            }
        }
        String dbStats = dbSize + " | " + consts + " | " + vars;
        return dbStats;
    }

    private void print(MapDifference<TupleWithTable, Set<TupleWithTable>> difference) {
        Map<TupleWithTable, Set<TupleWithTable>> onlyOnGenerated = difference.entriesOnlyOnLeft();
        String onlyOnGeneratedString = "Only on Generated:\n" + SpeedyUtility.printMapCompact(onlyOnGenerated);
        Map<TupleWithTable, Set<TupleWithTable>> onlyOnAlgorithm = difference.entriesOnlyOnRight();
        String onlyOnAlgorithmString = "Only on Algorithm:\n" + SpeedyUtility.printMapCompact(onlyOnAlgorithm);
        Map<TupleWithTable, MapDifference.ValueDifference<Set<TupleWithTable>>> entriesDiffering = difference.entriesDiffering();
        String differencesInMapString = "Differences in common:\n" + SpeedyUtility.printMapCompact(entriesDiffering);
        String toPrint = "-----------------------------\n"
                + onlyOnGeneratedString
                + "-----------------------------\n"
                + onlyOnAlgorithmString
                + "-----------------------------\n"
                + differencesInMapString;
        System.out.println(toPrint);
    }

}
