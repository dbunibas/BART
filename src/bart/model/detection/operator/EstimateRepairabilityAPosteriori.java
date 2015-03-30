package bart.model.detection.operator;

import bart.exceptions.ErrorGeneratorException;
import bart.model.EGTask;
import bart.model.dependency.Dependency;
import bart.model.detection.Violations;
import bart.model.errorgenerator.CellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.VioGenQuery;
import bart.model.errorgenerator.ViolationContext;
import bart.utility.BartUtility;
import bart.utility.ErrorGeneratorStats;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EstimateRepairabilityAPosteriori {
    
    private static Logger logger = LoggerFactory.getLogger(EstimateRepairabilityAPosteriori.class);
    private EstimateRepairabilityForCellChange changeRepairabilityEstimator = new EstimateRepairabilityForCellChange();
    
    public void estimateRepairability(CellChanges changes, Violations allViolations, EGTask task) throws ErrorGeneratorException {
        Map<VioGenQuery, List<CellChange>> changesForQueryMap = new HashMap<VioGenQuery, List<CellChange>>();
        Map<Dependency, List<CellChange>> changesForDependencyMap = new HashMap<Dependency, List<CellChange>>();
        for (CellChange cellChange : changes.getChanges()) {
            Map<Dependency, List<ViolationContext>> violationContextsForDependencies = findViolationContextsForChange(cellChange, allViolations);
            if (logger.isDebugEnabled()) logger.debug("Violation contexts for dependencies for change " + cellChange + " [" + cellChange.getVioGenQuery().toShortString() + "]\n" + BartUtility.printMap(violationContextsForDependencies));
            List<ViolationContext> violationsForDependency = groupViolationsForDependency(violationContextsForDependencies);
            double repairability = changeRepairabilityEstimator.computeRepairability(cellChange, violationsForDependency);
            cellChange.setRepairabilityAPosteriori(repairability);
            List<CellChange> changesForQuery = getChangesForQuery(changesForQueryMap, cellChange.getVioGenQuery());
            changesForQuery.add(cellChange);
            List<CellChange> changesForDependency = getChangesForDependency(changesForDependencyMap, cellChange.getVioGenQuery().getDependency());
            changesForDependency.add(cellChange);
        }
        computeAverageRepairabilityForVioGenQueries(changesForQueryMap);
        computeAverageRepairabilityForVioDependencies(changesForDependencyMap);
    }
    
    private List<ViolationContext> groupViolationsForDependency(Map<Dependency, List<ViolationContext>> violationContextsForDependencies) {
        List<ViolationContext> result = new ArrayList<ViolationContext>();
        for (Dependency dependency : violationContextsForDependencies.keySet()) {
            ViolationContext contextForDependency = new ViolationContext();
            contextForDependency.setDependencyId(dependency.getId());
            for (ViolationContext context : violationContextsForDependencies.get(dependency)) {
                contextForDependency.addAllCells(context.getCells());
            }
            result.add(contextForDependency);
        }
        return result;
    }
    
    private List<CellChange> getChangesForQuery(Map<VioGenQuery, List<CellChange>> changesForQueryMap, VioGenQuery vioGenQuery) {
        List<CellChange> changesForQuery = changesForQueryMap.get(vioGenQuery);
        if (changesForQuery == null) {
            changesForQuery = new ArrayList<CellChange>();
            changesForQueryMap.put(vioGenQuery, changesForQuery);
        }
        return changesForQuery;
    }
    
    private List<CellChange> getChangesForDependency(Map<Dependency, List<CellChange>> changesForDependencyMap, Dependency dependency) {
        List<CellChange> changesForQuery = changesForDependencyMap.get(dependency);
        if (changesForQuery == null) {
            changesForQuery = new ArrayList<CellChange>();
            changesForDependencyMap.put(dependency, changesForQuery);
        }
        return changesForQuery;
    }
    
    private Map<Dependency, List<ViolationContext>> findViolationContextsForChange(CellChange cellChange, Violations violations) {
        Map<Dependency, List<ViolationContext>> violationContexts = new HashMap<Dependency, List<ViolationContext>>();
        for (Dependency dependency : violations.getViolations().keySet()) {
            for (ViolationContext context : violations.getViolations().get(dependency)) {
                if (!context.containsCellRef(BartUtility.getCellRefNoAlias(cellChange.getCell()))) {
                    continue;
                }
                List<ViolationContext> contextsForDependency = getContextForDependency(violationContexts, dependency);
                contextsForDependency.add(context);
            }
        }
        return violationContexts;
    }
    
    private List<ViolationContext> getContextForDependency(Map<Dependency, List<ViolationContext>> violationContexts, Dependency dependency) {
        List<ViolationContext> contextsForDependency = violationContexts.get(dependency);
        if (contextsForDependency == null) {
            contextsForDependency = new ArrayList<ViolationContext>();
            violationContexts.put(dependency, contextsForDependency);
        }
        return contextsForDependency;
    }
    
    private void computeAverageRepairabilityForVioGenQueries(Map<VioGenQuery, List<CellChange>> changesForQueryMap) {
        for (VioGenQuery vioGenQuery : changesForQueryMap.keySet()) {
            double sum = 0.0;
            List<CellChange> changesForQuery = changesForQueryMap.get(vioGenQuery);
            for (CellChange change : changesForQuery) {
                sum += change.getRepairabilityAPosteriori();
            }
            double repairabilityForQuery = sum / (double) changesForQuery.size();
            if (logger.isInfoEnabled()) logger.info("Repairability for query " + vioGenQuery.toShortString() + ": " + repairabilityForQuery);
            ErrorGeneratorStats.getInstance().getVioGenQueriesRepairability().put(vioGenQuery, repairabilityForQuery);
        }
    }
    
    private void computeAverageRepairabilityForVioDependencies(Map<Dependency, List<CellChange>> changes) {
        for (Dependency dependency : changes.keySet()) {
            double sum = 0.0;
            List<CellChange> changesForQuery = changes.get(dependency);
            for (CellChange change : changesForQuery) {
                sum += change.getRepairabilityAPosteriori();
            }
            double repairabilityForQuery = sum / (double) changesForQuery.size();
            if (logger.isInfoEnabled()) logger.info("Repairability for query " + dependency.getId() + ": " + repairabilityForQuery);
            ErrorGeneratorStats.getInstance().getDependencyRepairability().put(dependency, repairabilityForQuery);
        }
    }
    
}
