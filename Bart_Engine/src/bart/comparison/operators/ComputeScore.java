package bart.comparison.operators;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import bart.comparison.ComparisonConfiguration;
import bart.comparison.ComparisonStats;
import bart.comparison.TupleMapping;
import bart.comparison.ValueMapping;
import speedy.model.database.Cell;
import speedy.model.database.IValue;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class ComputeScore {

    private final static Logger logger = LoggerFactory.getLogger(ComputeScore.class);
    private final static CheckTupleMatch tupleMatcher = new CheckTupleMatch();

    public double computeScore(List<TupleWithTable> leftTuples, List<TupleWithTable> rightTuples, TupleMapping tupleMapping) {
        long start = System.currentTimeMillis();
        if (logger.isDebugEnabled()) logger.debug("Computing score btw source tuples: \n " + SpeedyUtility.printCollection(leftTuples, "\t") + "\nand right tuples: \n " + SpeedyUtility.printCollection(rightTuples, "\t") + "\nwith mapping " + tupleMapping);
        Map<IValue, Integer> leftCoveredByMap = computeCoveredByMapForValueMapping(tupleMapping.getValueMappings().getLeftToRightValueMapping());
        if (logger.isDebugEnabled()) logger.debug("Left covered by map:\n" + SpeedyUtility.printMap(leftCoveredByMap));
        Map<IValue, Integer> rightCoveredByMap = computeCoveredByMapForValueMapping(tupleMapping.getValueMappings().getRightToLeftValueMapping());
        if (logger.isDebugEnabled()) logger.debug("Right covered by map:\n" + SpeedyUtility.printMap(rightCoveredByMap));
        Map<TupleWithTable, Set<TupleWithTable>> directMapping = tupleMapping.getTupleMapping();
        Map<TupleWithTable, Set<TupleWithTable>> inverseMapping = computeInverseMapping(directMapping);
        double leftTupleScores = computeScoreForTuples(leftTuples, directMapping, leftCoveredByMap, rightCoveredByMap, tupleMapping.getLeftToRightValueMapping());
        double rightTupleScores = computeScoreForTuples(rightTuples, inverseMapping, rightCoveredByMap, leftCoveredByMap, tupleMapping.getRightToLeftValueMapping());
        if (logger.isDebugEnabled()) logger.debug("* Left Tuples Score: " + leftTupleScores);
        if (logger.isDebugEnabled()) logger.debug("* Right Tuples Score: " + rightTupleScores);
        if (logger.isDebugEnabled()) logger.debug("* Number of Left Cells: " + numberOfCells(leftTuples));
        if (logger.isDebugEnabled()) logger.debug("* Number of Right Cells: " + numberOfCells(rightTuples));
        double score = (leftTupleScores + rightTupleScores) / (numberOfCells(leftTuples) + numberOfCells(rightTuples));
        if (logger.isDebugEnabled()) logger.debug("* Total Score: " + score);
        ComparisonStats.getInstance().addStat(ComparisonStats.COMPUTE_SCORE_TIME, System.currentTimeMillis() - start);
        return score;
    }

    private Map<IValue, Integer> computeCoveredByMapForValueMapping(ValueMapping valueMapping) {
        Map<IValue, Integer> result = new HashMap<IValue, Integer>();
        for (IValue invertedKey : valueMapping.getInvertedKeys()) {
            if (valueMapping.getInvertedValueMapping(invertedKey).isEmpty()) {
                continue;
            }
            result.put(invertedKey, valueMapping.getInvertedValueMapping(invertedKey).size());
        }
        return result;
    }

    private Map<TupleWithTable, Set<TupleWithTable>> computeInverseMapping(Map<TupleWithTable, Set<TupleWithTable>> tupleMapping) {
        Map<TupleWithTable, Set<TupleWithTable>> result = new HashMap<TupleWithTable, Set<TupleWithTable>>();
        for (TupleWithTable leftTuple : tupleMapping.keySet()) {
            Set<TupleWithTable> rightTuples = tupleMapping.get(leftTuple);
            for (TupleWithTable rightTuple : rightTuples) {
                addTupleMapping(rightTuple, leftTuple, result);
            }
        }
        return result;
    }

    private void addTupleMapping(TupleWithTable rightTuple, TupleWithTable leftTuple, Map<TupleWithTable, Set<TupleWithTable>> result) {
        Set<TupleWithTable> mappedTuples = result.get(rightTuple);
        if (mappedTuples == null) {
            mappedTuples = new HashSet<TupleWithTable>();
            result.put(rightTuple, mappedTuples);
        }
        mappedTuples.add(leftTuple);
    }

    private double computeScoreForTuples(List<TupleWithTable> tuples, Map<TupleWithTable, Set<TupleWithTable>> mapping, Map<IValue, Integer> srcCoveredByMap, Map<IValue, Integer> dstCoveredByMap, ValueMapping valueMapping) {
        double scoreForTuples = 0.0;
        for (TupleWithTable tuple : tuples) {
            double scoreForTuple = computeScoreForTuple(tuple, mapping, srcCoveredByMap, dstCoveredByMap, valueMapping);
            if (logger.isDebugEnabled()) logger.debug("# Score for tuple: " + tuple + ": " + scoreForTuple);
            scoreForTuples += scoreForTuple;
        }
        return scoreForTuples;
    }

    private double computeScoreForTuple(TupleWithTable tuple, Map<TupleWithTable, Set<TupleWithTable>> mapping, Map<IValue, Integer> srcCoveredByMap, Map<IValue, Integer> dstCoveredByMap, ValueMapping valueMapping) {
        Set<TupleWithTable> mappedTuples = mapping.get(tuple);
        if (mappedTuples == null || mappedTuples.isEmpty()) {
            if (logger.isDebugEnabled()) logger.debug("Tuple " + tuple + " is not mapped in mapping\n " + SpeedyUtility.printMap(mapping));
            return 0;
        }
        double sumTuplePair = 0.0;
        for (TupleWithTable mappedTuple : mappedTuples) {
            sumTuplePair += computeScoreForMapping(tuple, mappedTuple, srcCoveredByMap, dstCoveredByMap, valueMapping);
        }
        return (sumTuplePair / (double) mappedTuples.size());
    }

    private double computeScoreForMapping(TupleWithTable srcTuple, TupleWithTable mappedTuple, Map<IValue, Integer> leftCoveredByMap, Map<IValue, Integer> rightCoveredByMap, ValueMapping valueMapping) {
        double score = 0.0;
        for (int i = 0; i < srcTuple.getTuple().getCells().size(); i++) {
            if (srcTuple.getTuple().getCells().get(i).getAttribute().equals(SpeedyConstants.OID)) {
                continue;
            }
            IValue srcValue = srcTuple.getTuple().getCells().get(i).getValue();
            IValue dstValue = mappedTuple.getTuple().getCells().get(i).getValue();
            double scoreForAttribute = scoreForAttribute(srcValue, dstValue, leftCoveredByMap, rightCoveredByMap, valueMapping);
            if (logger.isDebugEnabled()) logger.debug("Score for value mapping: " + srcValue + " -> " + dstValue + ": " + scoreForAttribute);
            score += scoreForAttribute;
        }
        return score;
    }

    private double scoreForAttribute(IValue srcValue, IValue dstValue, Map<IValue, Integer> leftCoveredByMap, Map<IValue, Integer> rightCoveredByMap, ValueMapping valueMapping) {
        if (logger.isTraceEnabled()) logger.trace("Comparing values: '" + srcValue + "', '" + dstValue + "'");
        SpeedyConstants.ValueMatchResult matchResult = tupleMatcher.match(srcValue, dstValue);
        if (matchResult == SpeedyConstants.ValueMatchResult.NOT_MATCHING) {
            if (logger.isTraceEnabled()) logger.trace("Values not match...");
            return 0.0;
        }
        if (matchResult == SpeedyConstants.ValueMatchResult.EQUAL_CONSTANTS) {
            return 1.0;
        }
        IValue mappedValue = valueMapping.getValueMapping(srcValue);
        if (mappedValue == null) {
            mappedValue = srcValue;
        }
        double leftCoveredByValue = computeCoveredByValue(srcValue, mappedValue, leftCoveredByMap);
        if (logger.isDebugEnabled()) logger.debug("Value " + mappedValue + " is covered " + leftCoveredByValue + " times by src tuples");
        double rightCoveredByValue = computeCoveredByValue(dstValue, mappedValue, rightCoveredByMap);
        if (logger.isDebugEnabled()) logger.debug("Value " + mappedValue + " is covered " + rightCoveredByValue + " times by dst tuples");
        double coveredByValue = leftCoveredByValue + rightCoveredByValue;
        if (matchResult == SpeedyConstants.ValueMatchResult.PLACEHOLDER_TO_CONSTANT
                || matchResult == SpeedyConstants.ValueMatchResult.CONSTANT_TO_PLACEHOLDER) {
            return 2 * ComparisonConfiguration.getK() / (double) coveredByValue;
        }
        //Else Placeholder -> Placeholder
        return 2 / (double) coveredByValue;
    }

    private double computeCoveredByValue(IValue value, IValue mappedValue, Map<IValue, Integer> coveredByMap) {
        if (SpeedyUtility.isConstant(value)) {
            return 1;
        }
        Integer coveredByValue = coveredByMap.get(mappedValue);
        if (coveredByValue == null) {
            coveredByValue = 1;
        }
        return coveredByValue;
    }

    private int numberOfCells(List<TupleWithTable> tuples) {
        if (tuples.isEmpty()) {
            return 0;
        }
        Map<String, Integer> attributesForTable = new HashMap<String, Integer>();
        int numberOfCells = 0;
        for (TupleWithTable tuple : tuples) {
            numberOfCells += getNumberOfCellsForTable(tuple, attributesForTable);
        }
        return numberOfCells;
    }

    private int getNumberOfCellsForTable(TupleWithTable tuple, Map<String, Integer> attributesForTable) {
        String table = tuple.getTable();
        Integer attributes = attributesForTable.get(table);
        if (attributes != null) {
            return attributes;
        }
        int numberOfCells = 0;
        for (Cell cell : tuple.getTuple().getCells()) {
            if (cell.isOID()) {
                continue;
            }
            numberOfCells++;
        }
        attributesForTable.put(table, numberOfCells);
        return numberOfCells;
    }

}
