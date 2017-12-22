package bart.utility;

import bart.model.dependency.Dependency;
import bart.model.detection.RepairabilityStats;
import bart.model.errorgenerator.VioGenQuery;
import bart.utility.comparator.DependencyComparator;
import bart.utility.comparator.VioGenQueryComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorGeneratorStats {

    public static final String TOTAL_TIME = "Total Time";
    public static final String APPLY_CHANGES_TIME = "Apply Changes Time";
    public static final String GENERATE_CHANGES_TIME = "Generate Changes Time";
    public static final String CHECK_CHANGES_TIME = "Check Changes Time";
    public static final String NUMBER_OF_DCS = "#DCs";
    /////
    public static final String NUMBER_CHANGES = "#Changes";
    public static final String NUMBER_VIOLATIONS = "#Violations";
    public static final String NUMBER_NON_DETECTABLE_CHANGES = "#Non detectable changes";
    public static final String NUMBER_ONLYONCE_CHANGES = "#Only once detectable changes";
    /////
    public static final String DELTA_DB_BUILDER = "Building Delta DB";
    public static final String DELTA_DB_STEP_BUILDER = "Building Delta DB for Chase Step";
    /////
    private static Logger logger = LoggerFactory.getLogger(ErrorGeneratorStats.class);
    private static ErrorGeneratorStats singleton = new ErrorGeneratorStats();
    private Map<String, Long> stats = new HashMap<String, Long>();
    /////
    private Map<VioGenQuery, Long> vioGenQueryTimes = new HashMap<VioGenQuery, Long>();
    private Map<Dependency, Long> dependencyTimes = new HashMap<Dependency, Long>();
    private Map<VioGenQuery, Long> vioGenQueriesErrors = new HashMap<VioGenQuery, Long>();
    private Map<Dependency, Long> dependenciesErrors = new HashMap<Dependency, Long>();
    ////
    private Map<VioGenQuery, RepairabilityStats> vioGenQueriesRepairability = new HashMap<VioGenQuery, RepairabilityStats>();
    private Map<Dependency, RepairabilityStats> dependencyRepairability = new HashMap<Dependency, RepairabilityStats>();

    public static ErrorGeneratorStats getInstance() {
        return singleton;
    }

    private ErrorGeneratorStats() {
    }

    public void printStatistics() {
        printStatistics("");
    }

    public void printStatistics(String prefix) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        logger.debug(prefix + this.toString());
    }

    public void addStat(String statName, long newTime) {
        long previousTime = 0;
        if (stats.containsKey(statName)) {
            previousTime = stats.get(statName);
        }
        long totalTime = previousTime + newTime;
        stats.put(statName, totalTime);
    }

    public void addVioGenQueryTime(VioGenQuery vioGenQuery, long newTime) {
        long previousTime = 0;
        if (vioGenQueryTimes.containsKey(vioGenQuery)) {
            previousTime = vioGenQueryTimes.get(vioGenQuery);
        }
        long totalTime = previousTime + newTime;
        vioGenQueryTimes.put(vioGenQuery, totalTime);
    }

    public void addDependencyTime(Dependency dependency, long newTime) {
        long previousTime = 0;
        if (dependencyTimes.containsKey(dependency)) {
            previousTime = dependencyTimes.get(dependency);
        }
        long totalTime = previousTime + newTime;
        dependencyTimes.put(dependency, totalTime);
    }

    public Map<VioGenQuery, RepairabilityStats> getVioGenQueriesRepairability() {
        return vioGenQueriesRepairability;
    }

    public Map<Dependency, RepairabilityStats> getDependencyRepairability() {
        return dependencyRepairability;
    }

    public Map<VioGenQuery, Long> getVioGenQueryTimes() {
        return vioGenQueryTimes;
    }

    public Map<VioGenQuery, Long> getVioGenErrorsErrors() {
        return vioGenQueriesErrors;
    }

    public void addVioGenQueryErrors(VioGenQuery vioGenQuery, long newErrors) {
        long previousErrors = 0;
        if (vioGenQueriesErrors.containsKey(vioGenQuery)) {
            previousErrors = vioGenQueriesErrors.get(vioGenQuery);
        }
        long totalErrors = previousErrors + newErrors;
        vioGenQueriesErrors.put(vioGenQuery, totalErrors);
        addDependencyErrors(vioGenQuery.getDependency(), newErrors);
    }

    public void addDependencyErrors(Dependency dependency, long newErrors) {
        long previousErrors = 0;
        if (dependenciesErrors.containsKey(dependency)) {
            previousErrors = dependenciesErrors.get(dependency);
        }
        long totalErrors = previousErrors + newErrors;
        dependenciesErrors.put(dependency, totalErrors);
    }

    public Long getStat(String statName) {
        return stats.get(statName);
    }

    public void resetStatistics() {
        stats.clear();
        vioGenQueryTimes.clear();
        vioGenQueriesErrors.clear();
        dependencyTimes.clear();
        vioGenQueriesRepairability.clear();
        dependencyRepairability.clear();
        dependenciesErrors.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("------ TASK STATS ------").append("\n");
        if (stats.containsKey(NUMBER_OF_DCS)) sb.append(NUMBER_OF_DCS + ": ").append(stats.get(NUMBER_OF_DCS)).append("\n");
        if (stats.containsKey(TOTAL_TIME)) sb.append(TOTAL_TIME + ": ").append(stats.get(TOTAL_TIME)).append(" ms").append("\n");
        if (stats.containsKey(GENERATE_CHANGES_TIME)) sb.append(GENERATE_CHANGES_TIME + ": ").append(stats.get(GENERATE_CHANGES_TIME)).append(" ms").append("\n");
        if (stats.containsKey(APPLY_CHANGES_TIME)) sb.append(APPLY_CHANGES_TIME + ": ").append(stats.get(APPLY_CHANGES_TIME)).append(" ms").append("\n");
        if (stats.containsKey(CHECK_CHANGES_TIME)) sb.append(CHECK_CHANGES_TIME + ": ").append(stats.get(CHECK_CHANGES_TIME)).append(" ms").append("\n");
        sb.append("-------------------------").append("\n");
        if (stats.containsKey(NUMBER_CHANGES)) sb.append(NUMBER_CHANGES + ": ").append(stats.get(NUMBER_CHANGES)).append(" ").append("\n");
        if (stats.containsKey(NUMBER_VIOLATIONS)) sb.append(NUMBER_VIOLATIONS + ": ").append(stats.get(NUMBER_VIOLATIONS)).append(" ").append("\n");
        if (stats.containsKey(NUMBER_NON_DETECTABLE_CHANGES)) sb.append(NUMBER_NON_DETECTABLE_CHANGES + ": ").append(stats.get(NUMBER_NON_DETECTABLE_CHANGES)).append(" ").append("\n");
        if (stats.containsKey(NUMBER_ONLYONCE_CHANGES)) sb.append(NUMBER_ONLYONCE_CHANGES + ": ").append(stats.get(NUMBER_ONLYONCE_CHANGES)).append(" ").append("\n");
        sb.append("-------------------------").append("\n");
        if (!vioGenQueriesErrors.isEmpty()) {
            sb.append("------ ERROR FOR VIOGENQUERIES ------").append("\n");
            int totalNumberOfChanges = 0;
            for (VioGenQuery d : sortVioGenQueries(vioGenQueriesErrors.keySet())) {
                Long errorsForVioGenQuery = vioGenQueriesErrors.get(d);
                totalNumberOfChanges += (errorsForVioGenQuery == null ? 0 : errorsForVioGenQuery);
                sb.append(d.toShortString()).append(": \t").append(vioGenQueriesErrors.get(d)).append(" changes").append("\n");
            }
            sb.append("-------------------------").append("\n");
            sb.append("Total: \t").append(totalNumberOfChanges).append(" changes").append("\n");
            sb.append("-------------------------").append("\n");
            sb.append("------ ERROR FOR DEPENDENCIES ------").append("\n");
            totalNumberOfChanges = 0;
            for (Dependency d : sortDependencies(dependenciesErrors.keySet())) {
                totalNumberOfChanges += dependenciesErrors.get(d);
                sb.append(d.getId()).append(": \t").append(dependenciesErrors.get(d)).append(" changes").append("\n");
            }
            sb.append("-------------------------").append("\n");
            sb.append("Total: \t").append(totalNumberOfChanges).append(" changes").append("\n");
            sb.append("-------------------------").append("\n");
        }
        if (!vioGenQueriesRepairability.isEmpty()) {
            sb.append("------ REPAIRABILITY FOR VIOGENQUERY ------").append("\n");
            for (VioGenQuery d : sortVioGenQueries(vioGenQueriesRepairability.keySet())) {
                sb.append(d.toShortString()).append(": \t").append(vioGenQueriesRepairability.get(d)).append("").append("\n");
            }
            sb.append("-------------------------").append("\n");
        }
        if (!dependencyRepairability.isEmpty()) {
            sb.append("------ REPAIRABILITY FOR DEPENDENCIES ------").append("\n");
            for (Dependency d : sortDependencies(dependencyRepairability.keySet())) {
                sb.append(d.getId()).append(": \t").append(dependencyRepairability.get(d)).append("").append("\n");
            }
            sb.append("-------------------------").append("\n");
        }
        if (!vioGenQueryTimes.isEmpty()) {
            sb.append("------ TIME FOR VIOGENQUERY ------").append("\n");
            for (VioGenQuery d : sortVioGenQueries(vioGenQueryTimes.keySet())) {
                sb.append(d.toShortString()).append(": \t").append(vioGenQueryTimes.get(d)).append(" ms").append("\n");
            }
            sb.append("-------------------------").append("\n");
        }
        if (!dependencyTimes.isEmpty()) {
            sb.append("------ TIME FOR DEPENDENCIES ------").append("\n");
            for (Dependency d : sortDependencies(dependencyTimes.keySet())) {
                sb.append(d.getId()).append(": \t").append(dependencyTimes.get(d)).append(" ms").append("\n");
            }
            sb.append("-------------------------").append("\n");
        }
        if (stats.containsKey(DELTA_DB_BUILDER)) sb.append(DELTA_DB_BUILDER + ": ").append(stats.get(DELTA_DB_BUILDER)).append(" ms").append("\n");
        if (stats.containsKey(DELTA_DB_STEP_BUILDER)) sb.append(DELTA_DB_STEP_BUILDER + ": ").append(stats.get(DELTA_DB_STEP_BUILDER)).append(" ms").append("\n");
        return sb.toString();
    }

    private List<VioGenQuery> sortVioGenQueries(Set<VioGenQuery> keySet) {
        List<VioGenQuery> sortedList = new ArrayList<VioGenQuery>(keySet);
        Collections.sort(sortedList, new VioGenQueryComparator());
        return sortedList;
    }

    private List<Dependency> sortDependencies(Set<Dependency> keySet) {
        List<Dependency> sortedList = new ArrayList<Dependency>(keySet);
        Collections.sort(sortedList, new DependencyComparator());
        return sortedList;
    }

}
