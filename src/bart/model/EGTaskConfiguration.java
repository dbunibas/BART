package bart.model;

import bart.BartConstants;
import java.util.HashMap;
import java.util.Map;

public class EGTaskConfiguration {

    private boolean printLog = false;
    private boolean debug = false;
    private boolean useDeltaDBForChanges = true;
    private boolean recreateDBOnStart = false;
    private boolean checkCleanInstance = false;
    private boolean checkChanges = false;
    private boolean excludeCrossProducts = false;
    private boolean avoidInteractions = true;
    private boolean applyCellChanges = false;
    private boolean exportCellChanges = false;
    private String exportCellChangesPath = null;
    private boolean exportDirtyDB = false;
    private String exportDirtyDBType = BartConstants.CSV;
    private String exportDirtyDBPath = null;
    private boolean estimateAPosterioriRepairability = false;
    private boolean estimateAPrioriRepairability = false;
    private boolean cloneTargetSchema = true;
    private String cloneSuffix = BartConstants.DIRTY_SUFFIX;
    private boolean useSymmetricOptimization = true;
    private boolean generateAllChanges = false;
    private String sampleStrategyForStandardQueries = BartConstants.SAMPLE_STRATEGY_TABLE_SIZE;
    private String sampleStrategyForSymmetricQueries = BartConstants.SAMPLE_STRATEGY_TABLE_SIZE;
    private String sampleStrategyForInequalityQueries = BartConstants.SAMPLE_STRATEGY_TABLE_SIZE;
    private boolean detectEntireEquivalenceClasses = true;
//    private Integer maxNumberOfInequalitiesInSymmetricQueries = null;
    private double sizeFactorReduction = 0.7;
    private Map<String, Double> vioGenQueryProbabilities = new HashMap<String, Double>();
    private Map<String, RepairabilityRange> vioGenQueryRepairabilityRanges = new HashMap<String, RepairabilityRange>();
    private Map<String, String> vioGenQueryStrategy = new HashMap<String, String>();
    private VioGenQueryConfiguration defaultVioGenQueryConfiguration = new VioGenQueryConfiguration();

    public boolean isRecreateDBOnStart() {
        return recreateDBOnStart;
    }

    public void setRecreateDBOnStart(boolean recreateDBOnStart) {
        this.recreateDBOnStart = recreateDBOnStart;
    }

    public boolean isUseSymmetricOptimization() {
        return useSymmetricOptimization;
    }

    public void setUseSymmetricOptimization(boolean useSymmetricOptimization) {
        this.useSymmetricOptimization = useSymmetricOptimization;
    }

    public boolean isUseDeltaDBForChanges() {
        return useDeltaDBForChanges;
    }

    public void setUseDeltaDBForChanges(boolean useDeltaDBForChanges) {
        this.useDeltaDBForChanges = useDeltaDBForChanges;
    }

    public String getSampleStrategyForStandardQueries() {
        return sampleStrategyForStandardQueries;
    }

    public void setSampleStrategyForStandardQueries(String sampleStrategyForStandardQueries) {
        this.sampleStrategyForStandardQueries = sampleStrategyForStandardQueries;
    }

    public String getSampleStrategyForSymmetricQueries() {
        return sampleStrategyForSymmetricQueries;
    }

    public void setSampleStrategyForSymmetricQueries(String sampleStrategyForSymmetricQueries) {
        this.sampleStrategyForSymmetricQueries = sampleStrategyForSymmetricQueries;
    }

    public String getSampleStrategyForInequalityQueries() {
        return sampleStrategyForInequalityQueries;
    }

    public void setSampleStrategyForInequalityQueries(String sampleStrategyForInequalityQueries) {
        this.sampleStrategyForInequalityQueries = sampleStrategyForInequalityQueries;
    }

    public boolean isCheckCleanInstance() {
        return checkCleanInstance;
    }

    public void setCheckCleanInstance(boolean checkCleanInstance) {
        this.checkCleanInstance = checkCleanInstance;
    }

    public boolean isAvoidInteractions() {
        return avoidInteractions;
    }

    public void setAvoidInteractions(boolean avoidInteractions) {
        this.avoidInteractions = avoidInteractions;
    }

    public boolean isPrintLog() {
        return printLog;
    }

    public void setPrintLog(boolean printLog) {
        this.printLog = printLog;
    }

    public boolean isApplyCellChanges() {
        return applyCellChanges;
    }

    public void setApplyCellChanges(boolean applyCellChanges) {
        this.applyCellChanges = applyCellChanges;
    }

    public boolean isExportCellChanges() {
        return exportCellChanges;
    }

    public void setExportCellChanges(boolean exportCellChanges) {
        this.exportCellChanges = exportCellChanges;
    }

    public String getExportCellChangesPath() {
        return exportCellChangesPath;
    }

    public void setExportCellChangesPath(String exportCellChangesPath) {
        this.exportCellChangesPath = exportCellChangesPath;
    }

    public boolean isExportDirtyDB() {
        return exportDirtyDB;
    }

    public void setExportDirtyDB(boolean exportDirtyDB) {
        this.exportDirtyDB = exportDirtyDB;
    }

    public String getExportDirtyDBType() {
        return exportDirtyDBType;
    }

    public void setExportDirtyDBType(String exportDirtyDBType) {
        this.exportDirtyDBType = exportDirtyDBType;
    }

    public String getExportDirtyDBPath() {
        return exportDirtyDBPath;
    }

    public void setExportDirtyDBPath(String exportDirtyDBPath) {
        this.exportDirtyDBPath = exportDirtyDBPath;
    }

    public VioGenQueryConfiguration getDefaultVioGenQueryConfiguration() {
        return defaultVioGenQueryConfiguration;
    }

    public boolean isGenerateAllChanges() {
        return generateAllChanges;
    }

    public void setGenerateAllChanges(boolean generateAllChanges) {
        this.generateAllChanges = generateAllChanges;
    }

    public double getSizeFactorReduction() {
        return sizeFactorReduction;
    }

    public void setSizeFactorReduction(double sizeFactorReduction) {
        this.sizeFactorReduction = sizeFactorReduction;
    }

    public void addVioGenQueryProbability(String vioGenKey, double percentage) {
        this.vioGenQueryProbabilities.put(vioGenKey, percentage);
    }

    public Map<String, Double> getVioGenQueryProbabilities() {
        return vioGenQueryProbabilities;
    }

    public void addVioGenQueryRepairabilityRange(String vioGenKey, RepairabilityRange range) {
        this.vioGenQueryRepairabilityRanges.put(vioGenKey, range);
    }

    public Map<String, RepairabilityRange> getVioGenQueryRepairabilityRanges() {
        return vioGenQueryRepairabilityRanges;
    }

    public void addVioGenQueryStrategy(String vioGenKey, String strategy) {
        this.vioGenQueryStrategy.put(vioGenKey, strategy);
    }

    public Map<String, String> getVioGenQueryStrategy() {
        return vioGenQueryStrategy;
    }

    public boolean isEstimateAPosterioriRepairability() {
        return estimateAPosterioriRepairability;
    }

    public void setEstimateAPosterioriRepairability(boolean estimateAPosterioriRepairability) {
        this.estimateAPosterioriRepairability = estimateAPosterioriRepairability;
    }

    public boolean isEstimateAPrioriRepairability() {
        return estimateAPrioriRepairability;
    }

    public void setEstimateAPrioriRepairability(boolean estimateAPrioriRepairability) {
        this.estimateAPrioriRepairability = estimateAPrioriRepairability;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isCloneTargetSchema() {
        return cloneTargetSchema;
    }

    public void setCloneTargetSchema(boolean cloneTargetSchema) {
        this.cloneTargetSchema = cloneTargetSchema;
    }

    public String getCloneSuffix() {
        return cloneSuffix;
    }

    public void setCloneSuffix(String cloneSuffix) {
        this.cloneSuffix = cloneSuffix;
    }

    public boolean isExcludeCrossProducts() {
        return excludeCrossProducts;
    }

    public void setExcludeCrossProducts(boolean excludeCrossProducts) {
        this.excludeCrossProducts = excludeCrossProducts;
    }

    public boolean isDetectEntireEquivalenceClasses() {
        return detectEntireEquivalenceClasses;
    }

    public void setDetectEntireEquivalenceClasses(boolean detectEntireEquivalenceClasses) {
        this.detectEntireEquivalenceClasses = detectEntireEquivalenceClasses;
    }

    public boolean isCheckChanges() {
        return checkChanges;
    }

    public void setCheckChanges(boolean checkChanges) {
        this.checkChanges = checkChanges;
    }

    @Override
    public String toString() {
        return toShortString()
                + "\n" + defaultVioGenQueryConfiguration.toString();
    }

    public Object toShortString() {
        return "Configuration:"
                + "\n\t printLog=" + printLog
                + "\n\t recreateDBOnStart=" + recreateDBOnStart
                + "\n\t useDeltaDBForChanges=" + useDeltaDBForChanges
                + "\n\t applyCellChanges=" + applyCellChanges
                + "\n\t cloneTargetSchema=" + cloneTargetSchema
                + "\n\t generateAllChanges=" + generateAllChanges
                + "\n\t avoidInteractions=" + avoidInteractions
                + "\n\t checkCleanInstance=" + checkCleanInstance
                + "\n\t excludeCrossProducts=" + excludeCrossProducts
                + "\n\t useSymmetricOptimization=" + useSymmetricOptimization
                + "\n\t sampleStrategyForStandardQueries=" + sampleStrategyForStandardQueries
                + "\n\t sampleStrategyForSymmetricQueries=" + sampleStrategyForSymmetricQueries
                + "\n\t sampleStrategyForInequalityQueries=" + sampleStrategyForInequalityQueries
                //                + "\n\t maxNumberOfInequalitiesInSymmetricQueries=" + maxNumberOfInequalitiesInSymmetricQueries
                + "\n\t detectEntireEquivalenceClasses=" + detectEntireEquivalenceClasses;
    }

}
