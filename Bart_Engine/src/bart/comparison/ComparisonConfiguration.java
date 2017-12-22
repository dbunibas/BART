package bart.comparison;

import java.util.Arrays;
import speedy.SpeedyConstants;

public class ComparisonConfiguration implements Cloneable {

    private static ComparisonConfiguration singleton = new ComparisonConfiguration();
    private static final ComparisonConfiguration defaultSingleton = new ComparisonConfiguration();

    public static ComparisonConfiguration getInstance() {
        return singleton;
    }

    public static void reset() {
        singleton = defaultSingleton.clone();
    }

    private boolean twoWayValueMapping = true;
    private boolean injective = false;
    private boolean functional = true;
    private boolean stopIfNonMatchingTuples = false;
    private double K = 0.5;
//    private double bestScoreThreshold = 0.99;
    private double bestScoreThreshold = 99;
    private boolean convertSkolemInHash = false;
    private boolean forceExaustiveSearch = false;

    public static boolean isTwoWayValueMapping() {
        return getInstance().twoWayValueMapping;
    }

    public static void setTwoWayValueMapping(boolean twoWayValueMapping) {
        getInstance().twoWayValueMapping = twoWayValueMapping;
    }

    public static boolean isInjective() {
        return getInstance().injective;
    }

    public static void setInjective(boolean injective) {
        getInstance().injective = injective;
    }

    public static boolean isFunctional() {
        return getInstance().functional;
    }

    public static void setFunctional(boolean functional) {
        getInstance().functional = functional;
    }

    public static double getK() {
        return getInstance().K;
    }

    public static void setK(double K) {
        getInstance().K = K;
    }

    public static boolean isStopIfNonMatchingTuples() {
        return getInstance().stopIfNonMatchingTuples;
    }

    public static void setStopIfNonMatchingTuples(boolean stopIfNonMatchingTuples) {
        getInstance().stopIfNonMatchingTuples = stopIfNonMatchingTuples;
    }

    public static boolean isConvertSkolemInHash() {
        return getInstance().convertSkolemInHash;
    }

    public static void setConvertSkolemInHash(boolean convertSkolemInHash) {
        getInstance().convertSkolemInHash = convertSkolemInHash;
    }

    public static double getBestScoreThreshold() {
        return getInstance().bestScoreThreshold;
    }

    public static void setForceExaustiveSearch(boolean forceExaustiveSearch) {
        getInstance().forceExaustiveSearch = forceExaustiveSearch;
    }

    public static boolean isForceExaustiveSearch() {
        return getInstance().forceExaustiveSearch;
    }

    public ComparisonConfiguration clone() {
        try {
            return (ComparisonConfiguration) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("---- CONFIGURATION ----\n");
        sb.append(" funcional    :").append(functional).append("\n");
        sb.append(" injective    :").append(injective).append("\n");
        sb.append(" K            :").append(K).append("\n");
        sb.append(" nullPrefixes :").append(Arrays.asList(SpeedyConstants.getStringSkolemPrefixes())).append("\n");
        sb.append("-----------------------\n");
        sb.append(" forceExaustiveSearch:").append(forceExaustiveSearch).append("\n");
        sb.append(" twoWayValueMapping:").append(twoWayValueMapping).append("\n");
        sb.append(" stopIfNonMatchingTuples:").append(stopIfNonMatchingTuples).append("\n");
        sb.append(" convertSkolemInHash:").append(convertSkolemInHash).append("\n");
        return sb.toString();
    }

}
