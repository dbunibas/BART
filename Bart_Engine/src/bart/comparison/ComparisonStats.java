package bart.comparison;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComparisonStats {

    public static final String TOTAL_TIME = "Chasing Time";
    public static final String LOAD_INSTANCE_TIME = "Loading Instance Time";
    public static final String PROCESS_INSTANCE_TIME = "Processing Instance Time";
    public static final String GENERATE_SIGNATURE_MAP_COLLECTION_TIME = "Generating Signature Map Collection Time";
    public static final String GENERATE_TUPLE_SIGNATURE_TIME = "Generating Tuple Signature Time";
    public static final String CHECK_TUPLE_MATCH_TIME = "Checking Tuple Match Time";
    public static final String FIND_REMAINING_MATCHES_TIME = "Finding Remaining Matches Time";
    public static final String CHECK_TUPLE_MATCH_COMPATIBILITY_TIME = "Checking Tuple Match Compatibility Time";
    public static final String COMPUTE_SCORE_TIME = "Compute Mapping Score Time";
    public static final String FIND_TUPLE_MATCHES = "Find Tuple Matches Time";
    public static final String FIND_BEST_TUPLE_MAPPING_TIME = "Find Best Tuple Mapping Time";
    public static final String FIND_COMPATIBLE_TUPLES_TIMES = "Find Compatible Tuple Time";
    public static final String BUILD_INSTANCES_GRAPH = "Build Instances Graph";
    /////
    public static final String TEMP_1 = "TEMP_1";
    public static final String TEMP_2 = "TEMP_2";
    public static final String TEMP_3 = "TEMP_3";
    public static final String TEMP_4 = "TEMP_4";
    public static final String TEMP_5 = "TEMP_5";
    /////
    private static Logger logger = LoggerFactory.getLogger(ComparisonStats.class);
    private static ComparisonStats singleton = new ComparisonStats();
    private Map<String, Long> stats = new HashMap<String, Long>();

    public static ComparisonStats getInstance() {
        return singleton;
    }

    private ComparisonStats() {
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
        if (logger.isDebugEnabled()) logger.debug("## Adding stat: " + statName + ": " + newTime);
        long previousTime = 0;
        if (stats.containsKey(statName)) {
            previousTime = stats.get(statName);
        }
        long totalTime = previousTime + newTime;
        stats.put(statName, totalTime);
    }

    public Long getStat(String statName) {
        return stats.get(statName);
    }

    public void resetStatistics() {
        stats.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("------ COMPARISON STATS ------").append("\n");
        Set<String> printedStats = new HashSet<String>();
        appendStat(TOTAL_TIME, "ms", sb, printedStats);
        appendStat(PROCESS_INSTANCE_TIME, "ms", sb, printedStats);
        appendStat(GENERATE_SIGNATURE_MAP_COLLECTION_TIME, "ms", sb, printedStats);
        appendStat(GENERATE_TUPLE_SIGNATURE_TIME, "ms", sb, printedStats);
        appendStat(CHECK_TUPLE_MATCH_TIME, "ms", sb, printedStats);
        appendStat(CHECK_TUPLE_MATCH_COMPATIBILITY_TIME, "ms", sb, printedStats);
        sb.append("-------------------------").append("\n");
        appendOtherStats(printedStats, sb);
        return sb.toString();
    }

    private void appendStat(String key, String suffix, StringBuilder sb, Set<String> printedStats) {
        if (stats.containsKey(key)) {
            sb.append(key).append(": ").append(stats.get(key)).append(" ").append(suffix).append("\n");
            printedStats.add(key);
        }
    }

    private void appendOtherStats(Set<String> printedStats, StringBuilder sb) {
        List<String> otherStats = new ArrayList<String>(this.stats.keySet());
        otherStats.removeAll(printedStats);
        Collections.sort(otherStats);
        if (!otherStats.isEmpty()) {
            sb.append("------ OTHER ------").append("\n");
            for (String key : otherStats) {
                appendStat(key, "", sb, printedStats);
            }
        }
    }

}
