package bart.model.detection.operator;

import bart.exceptions.ErrorGeneratorException;
import bart.model.EGTask;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.IValue;
import bart.model.dependency.Dependency;
import bart.model.detection.RepairabilityStats;
import bart.model.detection.Violations;
import bart.model.errorgenerator.VioGenQueryCellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ICellChange;
import bart.model.errorgenerator.VioGenQuery;
import bart.model.errorgenerator.ViolationContext;
import bart.utility.BartUtility;
import bart.utility.ErrorGeneratorStats;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EstimateRepairability {

    private static Logger logger = LoggerFactory.getLogger(EstimateRepairability.class);

    public void estimateRepairability(CellChanges changes, Violations violations, EGTask task) throws ErrorGeneratorException {
        Map<VioGenQuery, List<VioGenQueryCellChange>> changesForQueryMap = new HashMap<VioGenQuery, List<VioGenQueryCellChange>>();
        Map<Dependency, List<VioGenQueryCellChange>> changesForDependencyMap = new HashMap<Dependency, List<VioGenQueryCellChange>>();
        for (ICellChange cellChange : changes.getChanges()) {
            if (cellChange instanceof VioGenQueryCellChange) {
                VioGenQueryCellChange vioGenQueryCellChange = (VioGenQueryCellChange) cellChange;
                List<ViolationContext> violationContexts = findViolationContextsForChange(vioGenQueryCellChange, violations);
                vioGenQueryCellChange.setViolationContexts(violationContexts);
                if (logger.isDebugEnabled()) logger.debug("Violation contexts for change " + cellChange + " [" + vioGenQueryCellChange.getVioGenQuery().toShortString() + "]\n" + BartUtility.printCollection(violationContexts, "\t"));
                double repairability = computeRepairability(vioGenQueryCellChange);
                vioGenQueryCellChange.setRepairability(repairability);
                List<VioGenQueryCellChange> changesForQuery = getChangesForQuery(changesForQueryMap, vioGenQueryCellChange.getVioGenQuery());
                changesForQuery.add(vioGenQueryCellChange);
                List<VioGenQueryCellChange> changesForDependency = getChangesForDependency(changesForDependencyMap, vioGenQueryCellChange.getVioGenQuery().getDependency());
                changesForDependency.add(vioGenQueryCellChange);
            }
        }
        computeAverageRepairabilityForVioGenQueries(changesForQueryMap);
        computeAverageRepairabilityForVioDependencies(changesForDependencyMap);
    }

    private void computeAverageRepairabilityForVioGenQueries(Map<VioGenQuery, List<VioGenQueryCellChange>> changesForQueryMap) {
        for (VioGenQuery vioGenQuery : changesForQueryMap.keySet()) {
            RepairabilityStats repairabilityStats = new RepairabilityStats();
            SummaryStatistics stats = new SummaryStatistics();
            List<VioGenQueryCellChange> changesForQuery = changesForQueryMap.get(vioGenQuery);
            for (VioGenQueryCellChange change : changesForQuery) {
                stats.addValue(change.getRepairability());
            }
            repairabilityStats.setMean(stats.getMean());
            double confidenceInterval = calcMeanCI(stats, 0.95);
            repairabilityStats.setConfidenceInterval(confidenceInterval);
            if (logger.isInfoEnabled()) logger.info("Repairability for query " + vioGenQuery.toShortString() + ": " + repairabilityStats);
            ErrorGeneratorStats.getInstance().getVioGenQueriesRepairability().put(vioGenQuery, repairabilityStats);
        }
    }

    private void computeAverageRepairabilityForVioDependencies(Map<Dependency, List<VioGenQueryCellChange>> changes) {
        for (Dependency dependency : changes.keySet()) {
            RepairabilityStats repairabilityStats = new RepairabilityStats();
            SummaryStatistics stats = new SummaryStatistics();
            List<VioGenQueryCellChange> changesForQuery = changes.get(dependency);
            for (VioGenQueryCellChange change : changesForQuery) {
                stats.addValue(change.getRepairability());
            }
            repairabilityStats.setMean(stats.getMean());
            double confidenceInterval = calcMeanCI(stats, 0.95);
            repairabilityStats.setConfidenceInterval(confidenceInterval);
            if (logger.isInfoEnabled()) logger.info("Repairability for query " + dependency.getId() + ": " + repairabilityStats);
            ErrorGeneratorStats.getInstance().getDependencyRepairability().put(dependency, repairabilityStats);
        }
    }

    private static double calcMeanCI(SummaryStatistics stats, double level) {
        try {
            TDistribution tDist = new TDistribution(stats.getN() - 1);
            double critVal = tDist.inverseCumulativeProbability(1.0 - (1 - level) / 2);
            return critVal * stats.getStandardDeviation() / Math.sqrt(stats.getN());
        } catch (MathIllegalArgumentException e) {
            return Double.NaN;
        }
    }

    private List<VioGenQueryCellChange> getChangesForQuery(Map<VioGenQuery, List<VioGenQueryCellChange>> changesForQueryMap, VioGenQuery vioGenQuery) {
        List<VioGenQueryCellChange> changesForQuery = changesForQueryMap.get(vioGenQuery);
        if (changesForQuery == null) {
            changesForQuery = new ArrayList<VioGenQueryCellChange>();
            changesForQueryMap.put(vioGenQuery, changesForQuery);
        }
        return changesForQuery;
    }

    private List<VioGenQueryCellChange> getChangesForDependency(Map<Dependency, List<VioGenQueryCellChange>> changesForDependencyMap, Dependency dependency) {
        List<VioGenQueryCellChange> changesForQuery = changesForDependencyMap.get(dependency);
        if (changesForQuery == null) {
            changesForQuery = new ArrayList<VioGenQueryCellChange>();
            changesForDependencyMap.put(dependency, changesForQuery);
        }
        return changesForQuery;
    }

    private List<ViolationContext> findViolationContextsForChange(VioGenQueryCellChange cellChange, Violations violations) {
        List<ViolationContext> violationContexts = new ArrayList<ViolationContext>();
        for (Dependency dependency : violations.getViolations().keySet()) {
            for (ViolationContext context : violations.getViolations().get(dependency)) {
                if (context.containsCellRef(BartUtility.getCellRefNoAlias(cellChange.getCell()))) {
                    violationContexts.add(context);
//                    continue;
                }
            }
        }
        return violationContexts;
    }

    private double computeRepairability(VioGenQueryCellChange cellChange) {
        if (logger.isDebugEnabled()) logger.debug("Computing reparability for change " + cellChange);
        List<ViolationContext> violationContexts = cellChange.getViolationContexts();
        if (violationContexts.isEmpty()) {
            if (logger.isInfoEnabled()) logger.info("Non detectable change " + cellChange);
            return 0.0;
        }
        double maxRepairability = 0.0;
        for (ViolationContext violationContext : violationContexts) {
            IValue originalValue = cellChange.getCell().getValue();
            if (hasSourceCellsWithValue(violationContext, originalValue)) {
                maxRepairability = 1.0;
                break;
            }
            List<Cell> cellsForAttribute = findCellsForAttribute(violationContext, cellChange.getCell().getAttributeRef());
            if (logger.isDebugEnabled()) logger.debug("Candidate cells: " + cellsForAttribute);
            List<Cell> cellsWithOriginalValue = findCellsWithValue(cellsForAttribute, originalValue);
            if (logger.isDebugEnabled()) logger.debug("Cells with original value: " + cellsWithOriginalValue);
            double repairability = cellsWithOriginalValue.size() / (double) cellsForAttribute.size();
            if (maxRepairability < repairability) {
                maxRepairability = repairability;
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Repairability: " + maxRepairability);
        return maxRepairability;
    }

    private boolean hasSourceCellsWithValue(ViolationContext violationContext, IValue value) {
        for (Cell cell : violationContext.getCells()) {
            if (cell.getAttributeRef().isSource() && cell.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private List<Cell> findCellsForAttribute(ViolationContext violationContext, AttributeRef attributeRef) {
        List<Cell> cellsForAttribute = new ArrayList<Cell>();
        for (Cell cell : violationContext.getCells()) {
            if (cell.getAttributeRef().equalsModuloAlias(attributeRef)) {
                cellsForAttribute.add(cell);
            }
        }
        return cellsForAttribute;
    }

    private List<Cell> findCellsWithValue(List<Cell> cellsForAttribute, IValue value) {
        List<Cell> result = new ArrayList<Cell>();
        for (Cell cell : cellsForAttribute) {
            if (cell.getValue().equals(value)) {
                result.add(cell);
            }
        }
        return result;
    }

}
