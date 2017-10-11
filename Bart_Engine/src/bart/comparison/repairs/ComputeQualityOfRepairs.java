package bart.comparison.repairs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DAOException;

public class ComputeQualityOfRepairs {

    private static Logger logger = LoggerFactory.getLogger(ComputeQualityOfRepairs.class);

    private static String[] defaultVariablePrefixes = "V_,_L,FV".split(",");

    public List<PrecisionAndRecall> calculatePrecisionAndRecallValue(String repairsFile, String expectedFile, double precisionForVariable) {
        return calculatePrecisionAndRecallValue(repairsFile, expectedFile, precisionForVariable, defaultVariablePrefixes);
    }

    public List<PrecisionAndRecall> calculatePrecisionAndRecallValue(String repairsFile, String expectedFile, double precisionForVariable, String[] variablePrefixes) {
        if (isMultiple(repairsFile)) {
            return checkExpectedAndCalculatedRepairsMultiple(expectedFile, repairsFile, precisionForVariable, variablePrefixes);
        } else {
            return checkExpectedAndCalculatedRepairs(expectedFile, repairsFile, precisionForVariable, variablePrefixes);
        }
    }

    public List<PrecisionAndRecall> calculatePrecisionAndRecallCell(String repairsFile, String expectedFile) {
        if (isMultiple(repairsFile)) {
            return checkExpectedAndCalculatedForCellRepairsMultiple(expectedFile, repairsFile);
        } else {
            return checkExpectedAndCalculatedForCellRepairs(expectedFile, repairsFile);
        }
    }

    private List<PrecisionAndRecall> checkExpectedAndCalculatedForCellRepairs(String expectectedAbsolutePath, String calculatedAbsolutePath) {
        Map<String, Repair> calculatedRepair = new DAOCSVRepair().loadRepairMap(calculatedAbsolutePath);
        Map<String, Repair> expectedRepair = new DAOCSVRepair().loadRepairMap(expectectedAbsolutePath);
        List<PrecisionAndRecall> result = new ArrayList<PrecisionAndRecall>();
        result.add(calculateOnCell(expectedRepair, calculatedRepair));
        return result;
    }

    private List<PrecisionAndRecall> checkExpectedAndCalculatedRepairs(String expectectedAbsolutePath, String calculatedAbsolutePath, double precisionForVariable, String[] variablePrefixes) {
        Map<String, Repair> calculatedRepair = new DAOCSVRepair().loadRepairMap(calculatedAbsolutePath);
        Map<String, Repair> expectedRepair = new DAOCSVRepair().loadRepairMap(expectectedAbsolutePath);
        List<PrecisionAndRecall> result = new ArrayList<PrecisionAndRecall>();
        result.add(calcutaorOnExactValue(expectedRepair, calculatedRepair, precisionForVariable, variablePrefixes));
        return result;
    }

    private List<PrecisionAndRecall> checkExpectedAndCalculatedForCellRepairsMultiple(String expectectedAbsolutePath, String calculatedAbsolutePath) {
        List<Map<String, Repair>> calculatedRepairs = new DAOCSVRepair().loadMultipleRepair(calculatedAbsolutePath);
        Map<String, Repair> expectedRepair = new DAOCSVRepair().loadRepairMap(expectectedAbsolutePath);

        List<PrecisionAndRecall> result = new ArrayList<PrecisionAndRecall>();
        for (Map<String, Repair> calculatedRepair : calculatedRepairs) {
            PrecisionAndRecall precisionAndRecall = calculateOnCell(expectedRepair, calculatedRepair);
            result.add(precisionAndRecall);
        }
        return result;
    }

    private List<PrecisionAndRecall> checkExpectedAndCalculatedRepairsMultiple(String expectectedAbsolutePath, String calculatedAbsolutePath, double precisionForVariable, String[] variablePrefixes) {
        List<Map<String, Repair>> calculatedRepairs = new DAOCSVRepair().loadMultipleRepair(calculatedAbsolutePath);
        Map<String, Repair> expectedRepair = new DAOCSVRepair().loadRepairMap(expectectedAbsolutePath);

        List<PrecisionAndRecall> result = new ArrayList<PrecisionAndRecall>();
        int solutionNumber = 1;
        for (Map<String, Repair> calculatedRepair : calculatedRepairs) {
            PrecisionAndRecall precisionAndRecall = calcutaorOnExactValue(expectedRepair, calculatedRepair, precisionForVariable, variablePrefixes);
            if (logger.isDebugEnabled()) logger.debug("Solution " + solutionNumber + "\t Quality: " + precisionAndRecall.getfMeasure());
            result.add(precisionAndRecall);
            solutionNumber++;
        }
        return result;
    }

    private PrecisionAndRecall calcutaorOnExactValue(Map<String, Repair> expectedRepairs, Map<String, Repair> calculatedRepairs, double precisionForVariable, String[] variablePrefixes) {
        double intersected = 0;
        for (String key : expectedRepairs.keySet()) {
            Repair expectedRepair = expectedRepairs.get(key);
            Repair calculatedRepair = calculatedRepairs.get(key);
            double result = 0;
            if (calculatedRepair != null && areEqual(calculatedRepair, expectedRepair)) {
                result = 1.0;
            } else if (calculatedRepair != null && calculatedRepair.equalsVariables(expectedRepair, variablePrefixes)) {
                result = precisionForVariable;
            }
            intersected += result;
        }
        double precision = intersected / calculatedRepairs.keySet().size();
        double recall = intersected / expectedRepairs.keySet().size();
        double fMeasure = (2 * precision * recall) / (precision + recall);
        return new PrecisionAndRecall(precision, recall, fMeasure);
    }

    private PrecisionAndRecall calculateOnCell(Map<String, Repair> expectedRepairs, Map<String, Repair> calculatedRepairs) {
        double intersected = 0;
        for (String key : expectedRepairs.keySet()) {
            Repair calculatedRepair = calculatedRepairs.get(key);
            if (calculatedRepair != null) {
                intersected++;
            }
        }
        double precision = intersected / calculatedRepairs.keySet().size();
        double recall = intersected / expectedRepairs.keySet().size();
        double fMeasure = (2 * precision * recall) / (precision + recall);
        return new PrecisionAndRecall(precision, recall, fMeasure);
    }

    private boolean areEqual(Repair calculatedRepair, Repair expectedRepair) {
        return calculatedRepair.toComparisonString().equalsIgnoreCase(expectedRepair.toComparisonString());
    }

    private boolean isMultiple(String fileName) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            return line != null && line.startsWith("+");
        } catch (IOException exception) {
            throw new DAOException("Unable to load file: " + fileName + "\n" + exception);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                }
            }
        }
    }
}
