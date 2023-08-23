package bart.test.comparison.generator;

import bart.comparison.ComparisonConfiguration;
import bart.comparison.InstanceMatchTask;
import bart.comparison.TupleMapping;
import bart.comparison.ValueMapping;
import bart.comparison.generator.ComparisonScenarioGeneratorWithMappingsBigInstances;
import bart.comparison.generator.InstancePair;
import bart.comparison.operators.ComputeInstanceSimilarityBruteForce;
import bart.comparison.operators.ComputeInstanceSimilarityHashing;
import bart.comparison.operators.ComputeScore;
import bart.comparison.operators.IComputeInstanceSimilarity;
import bart.test.comparison.ComparisonUtilityTest;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertTrue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleWithTable;
import speedy.persistence.file.operators.ExportCSVFile;
import speedy.utility.SpeedyUtility;

public class TestComparisonScenarioGeneratorBigInstances extends TestCase {

    private final static Logger logger = LoggerFactory.getLogger(TestComparisonScenarioGeneratorBigInstances.class);

    private int newReduntandTuples = 50;
    private int newRandomTuples = 50;
    private int cellsToChange = 10;
    private List<String> results = new ArrayList<>();
    private boolean printInfo = true;
    private boolean skipAssert = true;

    public void testExportAndExecuteGreedy() {
//        String[] datasets = {"conference", "doctors-100", "doctors-1k"};
        String[] datasets = {"bikeshare"};
        String exportPath = "/Users/enzoveltri/Desktop/instance-comparisons/"; //TODO: change it
        for (String dataset : datasets) {
            System.out.println(dataset);
            modifyCellsInSource(dataset, new ComputeInstanceSimilarityHashing(), true, exportPath);
            System.out.print(".");
            modifyCellsInTarget(dataset, new ComputeInstanceSimilarityHashing(), true, exportPath);
            System.out.print(".");
            modifyCellsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(), true, exportPath);
            System.out.print(".");
        }
        for (String result : this.results) {
            System.out.println(result);
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
        ComparisonScenarioGeneratorWithMappingsBigInstances generator = new ComparisonScenarioGeneratorWithMappingsBigInstances(0, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, false);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/modifyCellsInSource";
            exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
            saveMappings(instancePair, expPath);
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
            if (printInfo) {
                System.out.println("Time (ms):" + (end - start));
            }
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) {
                System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            }
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (!skipAssert && score != scoreSimilarity) {
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
            if (!skipAssert) {
                assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            }
            generateOutput("modifyCellsInSource", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");
        }
    }

    public void modifyCellsInTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setInjectiveFunctionalMapping();
        ComparisonScenarioGeneratorWithMappingsBigInstances generator = new ComparisonScenarioGeneratorWithMappingsBigInstances(0, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), false, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/modifyCellsInTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
            saveMappings(instancePair, expPath);
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
            if (printInfo) {
                System.out.println("Time (ms):" + (end - start));
            }
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) {
                System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            }
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (!skipAssert && score != scoreSimilarity) {
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
            if (!skipAssert) {
                assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            }
            generateOutput("modifyCellsInTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void modifyCellsInSourceAndTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setInjectiveFunctionalMapping();
        ComparisonScenarioGeneratorWithMappingsBigInstances generator = new ComparisonScenarioGeneratorWithMappingsBigInstances(0, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, true);
        System.out.println("Time Generation: " + generator.getTimeGeneration());
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/modifyCellsInSourceAndTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, false, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, false, expPath + "/right/");
            saveMappings(instancePair, expPath);
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
            if (printInfo) {
                System.out.println("Time (ms):" + (end - start));
            }
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) {
                System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            }
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (!skipAssert && score != scoreSimilarity) {
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
            if (!skipAssert) {
                assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + score + " Greedy: " + scoreSimilarity, scoreSimilarity == score);
            }
            generateOutput("modifyCellsInSourceAndTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");
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

    private void saveMappings(InstancePair instancePair, String expPath) {
        String fileName = expPath + "/mappings.json";
        TupleMapping tupleMapping = instancePair.getTupleMapping();
        MappingExport exportObj = new MappingExport(tupleMapping.getTupleMapping(), tupleMapping.getLeftToRightValueMapping(), tupleMapping.getRightToLeftValueMapping());
        ObjectMapper objectMapper = new ObjectMapper();
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(fileName));
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportObj);
            out.print(jsonString);
        } catch (Exception e) {
            logger.error("Exception in exporting json: " + e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {

                }
            }
        }

    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private class MappingExport implements Serializable {

        private Map<String, Set<String>> tupleMapping;
        private Map<String, String> leftToRightValueMapping;
        private Map<String, String> rightToLeftValueMapping;

        public MappingExport(Map<TupleWithTable, Set<TupleWithTable>> tupleMapping, ValueMapping leftToRightValueMapping, ValueMapping rightToLeftValueMapping) {
            this.tupleMapping = convertTupleMapping(tupleMapping);
            this.leftToRightValueMapping = convertMap(leftToRightValueMapping);
            this.rightToLeftValueMapping = convertMap(rightToLeftValueMapping);
        }

        private Map<String, Set<String>> convertTupleMapping(Map<TupleWithTable, Set<TupleWithTable>> tupleMapping) {
            Map<String, Set<String>> mapping = new HashMap<>();
            for (TupleWithTable key : tupleMapping.keySet()) {
                String sKey = key.getTuple().toStringNoOID();
                Set<String> values = mapping.get(sKey);
                if (values == null) {
                    values = new HashSet<>();
                    mapping.put(sKey, values);
                }
                Set<TupleWithTable> tupleSet = tupleMapping.get(key);
                for (TupleWithTable value : tupleSet) {
                    String sValue = value.getTuple().toStringNoOID();
                    values.add(sValue);
                }
            }
            return mapping;
        }

        private Map<String, String> convertMap(ValueMapping valueMapping) {
            Map<String, String> map = new HashMap<>();
            for (IValue key : valueMapping.getKeys()) {
                String sKey = key.getPrimitiveValue().toString();
                String sValue = valueMapping.getValueMapping(key).getPrimitiveValue().toString();
                map.put(sKey, sValue);
            }
            return map;
        }

    }
}
