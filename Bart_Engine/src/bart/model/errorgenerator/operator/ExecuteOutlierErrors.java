package bart.model.errorgenerator.operator;

import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.OutlierErrorConfiguration;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.OutlierCellChange;
import bart.model.errorgenerator.ValueConstraint;
import bart.model.errorgenerator.operator.valueselectors.INewValueSelectorStrategy;
import bart.model.errorgenerator.operator.valueselectors.OutlierValueSelector;
import bart.utility.BartUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.utility.SpeedyUtility;

public class ExecuteOutlierErrors implements IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(ExecuteOutlierErrors.class);
    private INewValueSelectorStrategy valueSelector;

    public void intitializeOperators(EGTask task) {
        valueSelector = OperatorFactory.getInstance().getValueSelector(task);
        valueSelector = new OutlierValueSelector();
    }

    public CellChanges execute(EGTask task, CellChanges detectableChanges) {
        intitializeOperators(task);
        CellChanges cellChanges = new CellChanges();
        if (!task.getConfiguration().isOutlierErrors()) return null;
        if (task.getConfiguration().isDebug()) System.out.println("Start to dirty OUTLIERS");
        OutlierErrorConfiguration outlierErrorConfiguration = task.getConfiguration().getOutlierErrorConfiguration();
        Set<String> tablesToDirty = outlierErrorConfiguration.getTablesToDirty();
        for (String tableName : tablesToDirty) {
            dirtyTable(tableName, task, cellChanges, detectableChanges);
        }
        if (task.getConfiguration().isPrintLog()) System.out.println("Outliers CellChanges generated: " + cellChanges.getChanges().size());
        return cellChanges;
    }

    private void dirtyTable(String tableName, EGTask task, CellChanges cellChanges, CellChanges detectableChanges) {
        if (task.getConfiguration().isPrintLog()) System.out.println("Start to dirty: " + tableName);
        if (task.getConfiguration().isDebug()) System.out.println("Table to dirty: " + tableName);
        ITable table = task.getTarget().getTable(tableName);
        OutlierErrorConfiguration outlierErrorConfiguration = task.getConfiguration().getOutlierErrorConfiguration();
        Set<String> attributesToDirty = outlierErrorConfiguration.getAttributesToDirty(tableName);
        if (task.getConfiguration().isDebug()) System.out.println("Attributes to dirty: " + attributesToDirty);
        checkAttributes(table, attributesToDirty);
        for (String attribute : attributesToDirty) {
            double percentageToDirty = outlierErrorConfiguration.getPercentageToDirty(tableName, attribute);
            boolean detectable = outlierErrorConfiguration.isDetectable(tableName, attribute);
            dirtyAttribute(table, attribute, percentageToDirty, detectable, cellChanges, detectableChanges, task);
        }
    }

    private void dirtyAttribute(ITable table, String attribute, double percentageToDirty, boolean detectable, CellChanges cellChanges, CellChanges detectableChanges, EGTask task) {
        if (task.getConfiguration().isDebug()) System.out.println("Attribute to dirty: " + attribute);
        if (task.getConfiguration().isDebug()) System.out.println("Percentage to dirty: " + percentageToDirty);
        if (task.getConfiguration().isDebug()) System.out.println("Detectable: " + detectable);
        double percentage = percentageToDirty / 100;
        // TODO select attribute from table ???
        ITupleIterator it = table.getTupleIterator();
        List<Cell> originalDistribution = new ArrayList<Cell>();
        while (it.hasNext()) {
            Tuple tuple = it.next();
            List<Cell> cells = tuple.getCells();
            for (Cell cell : cells) {
                if (!attribute.equalsIgnoreCase(cell.getAttribute())) {
                    continue;
                }
                // TODO skip from cell already changed?
                if (task.getConfiguration().isAvoidInteractions() && detectableChanges.isViolationContextCell(cell)) {
                    continue;
                }
                // now skip already changed
                originalDistribution.add(cell);
            }
        }
        it.close();
        double[] distribution = getDistribution(originalDistribution, task);
        if (detectable) {
            if (task.getConfiguration().isDebug()) System.out.println("Generate detectable changes");
            generateDetectableChanges(originalDistribution, distribution, percentage, cellChanges, task, table, attribute);
        } else {
            if (task.getConfiguration().isDebug()) System.out.println("Generate not detectable changes");
            generateNotDetectableChanges(originalDistribution, distribution, percentage, cellChanges, task);
        }
    }

    private void generateDetectableChanges(List<Cell> originalDistribution, double[] distribution, double percentage, CellChanges cellChanges, EGTask task, ITable table, String attribute) {
        List<OutlierCellChange> generatedOutliers = generateOutliers(originalDistribution, distribution, percentage, task);
        double[] temporaryDistribution = getDistribution(originalDistribution, generatedOutliers, task);
        DescriptiveStatistics statsNew = new DescriptiveStatistics(temporaryDistribution);
        if (task.getConfiguration().isDebug()) System.out.println(printStat(statsNew));
        double q1 = statsNew.getPercentile(25);
        double q3 = statsNew.getPercentile(75);

        if (task.getConfiguration().isPrintLog()) System.out.println("Generated outliers: " + generatedOutliers.size());
        if (task.getConfiguration().isDebug()) System.out.println("Now cheching if are detectable as outliers... ");
        Iterator<OutlierCellChange> iterator = generatedOutliers.iterator();
        while (iterator.hasNext()) {
            OutlierCellChange outlier = iterator.next();
            if (!outlier.isDetectable(q1, q3)) {
                iterator.remove();
            }
        }
        if (task.getConfiguration().isPrintLog()) System.out.println("Generated outliers detectable: " + generatedOutliers.size());
        if (task.getConfiguration().isDebug()) System.out.println("Now updating cell changes.. ");
        double[] newDistribution = getDistribution(originalDistribution, generatedOutliers, task);
        statsNew = new DescriptiveStatistics(newDistribution);
        if (task.getConfiguration().isDebug()) System.out.println(printStat(statsNew));

//        q1 = statsNew.getPercentile(25);
//        q3 = statsNew.getPercentile(75);
//        iterator = generatedOutliers.iterator();
//        while (iterator.hasNext()) {
//            OutlierCellChange outlier = iterator.next();
//            if (!outlier.isDetectable(q1, q3)) {
//                if (task.getConfiguration().isPrintLog()) System.out.println("Houston we have a problem :)");
//                throw new ErrorGeneratorException("Unable to generate error with specified percentage");
//            }
//        }
        for (OutlierCellChange generateOutlier : generatedOutliers) {
            generateOutlier.setDetectable(true);
            cellChanges.addChange(generateOutlier);
        }
    }

    private void generateNotDetectableChanges(List<Cell> originalDistribution, double[] distribution, double percentage, CellChanges cellChanges, EGTask task) {
        List<OutlierCellChange> generateOutliers = generateOutliers(originalDistribution, distribution, percentage, task);
        for (OutlierCellChange generateOutlier : generateOutliers) {
            cellChanges.addChange(generateOutlier);
        }
    }

    private List<OutlierCellChange> generateOutliers(List<Cell> originalDistribution, double[] distribution, double percentage, EGTask task) {
        List<OutlierCellChange> outliers = new ArrayList<OutlierCellChange>();
        DescriptiveStatistics stats = new DescriptiveStatistics(distribution);
        if (task.getConfiguration().isDebug()) System.out.println(printStat(stats));

        double q1 = stats.getPercentile(25);
        double q3 = stats.getPercentile(75);
        double iqr = q3 - q1;
        // using Tukey’s Outlier rule
        // outlier if value < q1 - 1.5 * iqr or value > q3 + 1.5 * iqr
        double minRange = q1 - 1.5 * iqr;
        double maxRange = q3 + 1.5 * iqr;
        for (Cell cell : originalDistribution) {
            if (BartUtility.pickRandom(percentage)) {
                OutlierCellChange cellChange = buildCellChange(cell, task, minRange, maxRange, q1, q3);
                outliers.add(cellChange);
            }
        }
        return outliers;
    }

    private OutlierCellChange buildCellChange(Cell cell, EGTask task, double min, double max, double q1, double q3) {
        OutlierCellChange cellChange = new OutlierCellChange(cell);
        AttributeRef attributeRef = cell.getAttributeRef();
        Attribute attribute = BartUtility.getAttribute(attributeRef, task);
        String type = attribute.getType();
        int maxExtension = 50; // % wrt value
        ValueConstraint valueConstraintWhite = null;
        if (BartUtility.pickRandom(0.50)) {
            double from = min - (maxExtension * Math.abs(min) / 100);
            valueConstraintWhite = new ValueConstraint(new ConstantValue(from), new ConstantValue(min), type);
        } else {
            double to = max + (maxExtension * Math.abs(max) / 100);
            valueConstraintWhite = new ValueConstraint(new ConstantValue(max), new ConstantValue(to), type);
        }
        if (task.getConfiguration().isDebug()) System.out.println("Range from " + valueConstraintWhite.getStart().getPrimitiveValue() + " to " + valueConstraintWhite.getEnd().getPrimitiveValue());
        ValueConstraint valueConstraintBlack = new ValueConstraint(new ConstantValue(min), new ConstantValue(max), type);
        cellChange.addWhiteListValue(valueConstraintWhite);
        cellChange.addBlackListValue(valueConstraintBlack);
        IValue newValue = valueSelector.generateNewValuesForContext(cell, cellChange, task);
        cellChange.setNewValue(newValue);
        cellChange.setRange(min, max);
        if (task.getConfiguration().isDebug()) System.out.println("Changing Cell: " + cell + " *** From value: " + cell.getValue() + " to: " + newValue);
        return cellChange;
    }

    private double[] getDistribution(List<Cell> originalDistribution, EGTask task) {
        double[] distribution = new double[originalDistribution.size()];
        int counter = 0;
        for (Cell cell : originalDistribution) {
            checkNumeric(cell, task);
            Number numericalValue = (Number) cell.getValue().getPrimitiveValue();
            distribution[counter] = numericalValue.doubleValue();
            counter++;
        }
        return distribution;
    }
    
        private double[] getDistribution(List<Cell> originalDistribution, List<OutlierCellChange> generatedOutliers, EGTask task) {
        double[] distribution = new double[originalDistribution.size()];
        int counter = 0;
        Map<Cell, OutlierCellChange> map = generateMap(generatedOutliers);
        for (Cell cell : originalDistribution) {
            checkNumeric(cell, task);
            if (map.get(cell) != null) {
                OutlierCellChange outlier = map.get(cell);
                Number numericalValue = (Number) outlier.getNewValue().getPrimitiveValue();
                distribution[counter] = numericalValue.doubleValue();
            } else {
                Number numericalValue = (Number) cell.getValue().getPrimitiveValue();
                distribution[counter] = numericalValue.doubleValue();
            }
            counter++;
        }
        return distribution;
    }

    private void checkNumeric(Cell cell, EGTask task) throws UnsupportedOperationException {
        AttributeRef attributeRef = cell.getAttributeRef();
        Attribute attribute = BartUtility.getAttribute(attributeRef, task);
        String type = attribute.getType();
        if (!SpeedyUtility.isNumeric(type)) {
            throw new UnsupportedOperationException("Unable to generate outlier errors with non-numeric value. Type used:" + type);
        }
    }

    private Map<Cell, OutlierCellChange> generateMap(List<OutlierCellChange> generatedOutliers) {
        Map<Cell, OutlierCellChange> map = new HashMap<Cell, OutlierCellChange>();
        for (OutlierCellChange outlier : generatedOutliers) {
            map.put(outlier.getCell(), outlier);
        }
        return map;
    }

    private String printStat(DescriptiveStatistics stats) {
        double mean = stats.getMean();
        double std = stats.getStandardDeviation();
        double median = stats.getPercentile(50);
        double q1 = stats.getPercentile(25);
        double q3 = stats.getPercentile(75);
        double iqr = q3 - q1;
        double trimmedMean = (q1 + q3 + 2 * median) / 4;
        double skewness = stats.getSkewness();

        StringBuilder sb = new StringBuilder();
        sb.append(" *** Distribution Analysis ***").append("\n")
                .append("\tMean= ").append(mean).append("\n")
                .append("\tStd= ").append(std).append("\n")
                .append("\tMedian= ").append(median).append("\n")
                .append("\tQ1= ").append(q1).append("\tQ3=").append(q3).append("\tIQR=").append(iqr).append("\n")
                .append("\tTrimmed Mean= ").append(trimmedMean).append("\n")
                .append("\tSkewness= ").append(skewness).append("\n");
        return sb.toString();
    }


    private void checkAttributes(ITable table, Set<String> attributesForRandomErrors) {
        for (String attributesForRandomError : attributesForRandomErrors) {
            Attribute attribute = table.getAttribute(attributesForRandomError); //Exception if it not exists
            if (logger.isDebugEnabled()) logger.debug("Attribute: " + attribute);
        }
    }
}
