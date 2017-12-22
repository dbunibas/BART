package bart.model;

import bart.BartConstants;
import bart.model.errorgenerator.OrderingAttribute;
import speedy.model.database.AttributeRef;
import bart.model.errorgenerator.operator.valueselectors.IDirtyStrategy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EGTaskConfiguration {

    private boolean printLog = false;
    private boolean debug = false;
    private Long queryExecutionTimeout;
    private boolean useDeltaDBForChanges = true;
    private boolean recreateDBOnStart = false;
    private boolean checkCleanInstance = false;
    private boolean checkChanges = false;
    private boolean excludeCrossProducts = false;
    private boolean avoidInteractions = true;
    private boolean applyCellChanges = false;
    private boolean exportCellChanges = false;
    private boolean exportCellChangesFull = false;
    private String exportCellChangesPath = null;
    private boolean exportCleanDB = false;
    private String exportCleanDBType = BartConstants.CSV;
    private String exportCleanDBPath = null;
    private boolean exportDirtyDB = false;
    private String exportDirtyDBType = BartConstants.CSV;
    private String exportDirtyDBPath = null;
    private boolean estimateRepairability = false;
    private boolean cloneTargetSchema = true;
    private String cloneSuffix = BartConstants.DIRTY_SUFFIX;
    private boolean useSymmetricOptimization = true;
    private boolean generateAllChanges = false;
    private String sampleStrategyForStandardQueries = BartConstants.SAMPLE_STRATEGY_TABLE_SIZE;
    private String sampleStrategyForSymmetricQueries = BartConstants.SAMPLE_STRATEGY_TABLE_SIZE;
    private String sampleStrategyForInequalityQueries = BartConstants.SAMPLE_STRATEGY_TABLE_SIZE;
    private boolean detectEntireEquivalenceClasses = true;
    private boolean autoSelectBestNumberOfThreads = true;
    private int maxNumberOfThreads = 1;
//    private Integer maxNumberOfInequalitiesInSymmetricQueries = null;
    private double sizeFactorReduction = 0.7;
    private Map<String, Double> vioGenQueryProbabilities = new HashMap<String, Double>();
    private Map<String, String> vioGenQueryStrategy = new HashMap<String, String>();
    private VioGenQueryConfiguration defaultVioGenQueryConfiguration = new VioGenQueryConfiguration();
    private boolean randomErrors = false;
    private Map<String, Set<String>> tablesForRandomErrors = new HashMap<String, Set<String>>(); // key: tableName  values: attributesToDirty
    private Map<String, Double> tablesPercentageForRandomErrors = new HashMap<String, Double>(); // key: tableName values: percentage;
    private boolean outlierErrors = false;
    private OutlierErrorConfiguration outlierErrorConfiguration = new OutlierErrorConfiguration();
    private IDirtyStrategy defaultDirtyStrategy;
    private Map<AttributeRef, IDirtyStrategy> dirtyStrategiesMap = new HashMap<AttributeRef, IDirtyStrategy>();
    private Map<String, OrderingAttribute> vioGenOrderingAttributes = new HashMap<String, OrderingAttribute>();

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

    public Long getQueryExecutionTimeout() {
        return queryExecutionTimeout;
    }

    public void setQueryExecutionTimeout(Long queryExecutionTimeout) {
        this.queryExecutionTimeout = queryExecutionTimeout;
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

    public boolean isExportCellChangesFull() {
        return exportCellChangesFull;
    }

    public void setExportCellChangesFull(boolean exportCellChangesFull) {
        this.exportCellChangesFull = exportCellChangesFull;
    }

    public String getExportCellChangesPath() {
        return exportCellChangesPath;
    }

    public void setExportCellChangesPath(String exportCellChangesPath) {
        this.exportCellChangesPath = exportCellChangesPath;
    }

    public boolean isExportCleanDB() {
        return exportCleanDB;
    }

    public void setExportCleanDB(boolean exportCleanDB) {
        this.exportCleanDB = exportCleanDB;
    }

    public String getExportCleanDBType() {
        return exportCleanDBType;
    }

    public void setExportCleanDBType(String exportCleanDBType) {
        this.exportCleanDBType = exportCleanDBType;
    }

    public String getExportCleanDBPath() {
        return exportCleanDBPath;
    }

    public void setExportCleanDBPath(String exportCleanDBPath) {
        this.exportCleanDBPath = exportCleanDBPath;
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

    public void addVioGenQueryProbabilities(String vioGenKey, double percentage) {
        this.vioGenQueryProbabilities.put(vioGenKey, percentage);
    }

    public Map<String, Double> getVioGenQueryProbabilities() {
        return vioGenQueryProbabilities;
    }

    public void setVioGenQueryProbabilities(Map<String, Double> vioGenQueryProbabilities) {
        this.vioGenQueryProbabilities = vioGenQueryProbabilities;
    }

    public void addVioGenQueryStrategy(String vioGenKey, String strategy) {
        this.vioGenQueryStrategy.put(vioGenKey, strategy);
    }

    public Map<String, String> getVioGenQueryStrategy() {
        return vioGenQueryStrategy;
    }

    public boolean isEstimateRepairability() {
        return estimateRepairability;
    }

    public void setEstimateRepairability(boolean estimateRepairability) {
        this.estimateRepairability = estimateRepairability;
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

    public boolean isRandomErrors() {
        return randomErrors;
    }

    public void setRandomErrors(boolean randomErrors) {
        this.randomErrors = randomErrors;
    }

    public Set<String> getTablesForRandomErrors() {
        return tablesForRandomErrors.keySet();
    }

    public void addTableForRandomErrors(String tableName, Set<String> attributes) {
        this.tablesForRandomErrors.put(tableName, attributes);
    }

    public Set<String> getAttributesForRandomErrors(String tableName) {
        return tablesForRandomErrors.get(tableName);
    }

    public void addPercentageForRandomErrors(String tableName, double percentage) {
        this.tablesPercentageForRandomErrors.put(tableName, percentage);
    }

    public double getPercentageForRandomErrors(String tableName) {
        return tablesPercentageForRandomErrors.get(tableName);
    }

    public boolean isOutlierErrors() {
        return outlierErrors;
    }

    public void setOutlierErrors(boolean outlierErrors) {
        this.outlierErrors = outlierErrors;
    }

    public OutlierErrorConfiguration getOutlierErrorConfiguration() {
        return outlierErrorConfiguration;
    }

    public void setOutlierErrorConfiguration(OutlierErrorConfiguration outlierErrorConfiguration) {
        this.outlierErrorConfiguration = outlierErrorConfiguration;
    }

    public IDirtyStrategy getDefaultDirtyStrategy() {
        return defaultDirtyStrategy;
    }

    public void setDefaultDirtyStrategy(IDirtyStrategy defaultDirtyStrategy) {
        this.defaultDirtyStrategy = defaultDirtyStrategy;
    }

    public void addDirtyStrategyForAttribute(AttributeRef attribute, IDirtyStrategy dirtyStrategy) {
        this.dirtyStrategiesMap.put(attribute, dirtyStrategy);
    }

    public IDirtyStrategy getDirtyStrategy(AttributeRef attribute) {
        IDirtyStrategy dirtyStrategy = this.dirtyStrategiesMap.get(attribute);
        if (dirtyStrategy != null) return dirtyStrategy;
        return defaultDirtyStrategy;
    }

    public Map<AttributeRef, IDirtyStrategy> getDirtyStrategiesMap() {
        return dirtyStrategiesMap;
    }

    public boolean containsOrderingAttributes() {
        return !this.vioGenOrderingAttributes.isEmpty();
    }

    public Map<String, OrderingAttribute> getVioGenOrderingAttributes() {
        return vioGenOrderingAttributes;
    }

    public void setVioGenOrderingAttributes(Map<String, OrderingAttribute> vioGenOrderingAttributes) {
        this.vioGenOrderingAttributes = vioGenOrderingAttributes;
    }

    public boolean isAutoSelectBestNumberOfThreads() {
        return autoSelectBestNumberOfThreads;
    }

    public void setAutoSelectBestNumberOfThreads(boolean autoSelectBestNumberOfThreads) {
        this.autoSelectBestNumberOfThreads = autoSelectBestNumberOfThreads;
    }

    public int getMaxNumberOfThreads() {
        return maxNumberOfThreads;
    }

    public void setMaxNumberOfThreads(int maxNumberOfThreads) {
        this.maxNumberOfThreads = maxNumberOfThreads;
    }

//    public Integer getMaxNumberOfInequalitiesInSymmetricQueries() {
//        return maxNumberOfInequalitiesInSymmetricQueries;
//    }
//
//    public void setMaxNumberOfInequalitiesInSymmetricQueries(Integer maxNumberOfInequalitiesInSymmetricQueries) {
//        this.maxNumberOfInequalitiesInSymmetricQueries = maxNumberOfInequalitiesInSymmetricQueries;
//    }
    @Override
    public String toString() {
        return toShortString()
                + "\n" + defaultVioGenQueryConfiguration.toString();
    }

    public Object toShortString() {
        String configuration = "Configuration:"
                + "\n\t printLog=" + printLog
                + "\n\t recreateDBOnStart=" + recreateDBOnStart
                + "\n\t useDeltaDBForChanges=" + useDeltaDBForChanges
                + "\n\t applyCellChanges=" + applyCellChanges
                + "\n\t cloneTargetSchema=" + cloneTargetSchema
                + "\n\t estimateRepairability=" + estimateRepairability
                + "\n\t generateAllChanges=" + generateAllChanges
                + "\n\t avoidInteractions=" + avoidInteractions
                + "\n\t checkCleanInstance=" + checkCleanInstance
                + "\n\t excludeCrossProducts=" + excludeCrossProducts
                + "\n\t useSymmetricOptimization=" + useSymmetricOptimization
                + "\n\t sampleStrategyForStandardQueries=" + sampleStrategyForStandardQueries
                + "\n\t sampleStrategyForSymmetricQueries=" + sampleStrategyForSymmetricQueries
                + "\n\t sampleStrategyForInequalityQueries=" + sampleStrategyForInequalityQueries
                + "\n\t detectEntireEquivalenceClasses=" + detectEntireEquivalenceClasses
                + "\n\t defaultDirtyStrategy " + defaultDirtyStrategy
                + printDetailedDirtyAttributeStrategy()
                + "\n\t orderingAttributes= " + containsOrderingAttributes()
                + "\n\t randomErrors=" + randomErrors
                + printDetailedRandomErrorsForTables()
                + "\n\t outlierErrors=" + outlierErrors
                + printDetaileOutlierErrors();
        return configuration.trim();
    }

    private String printDetailedRandomErrorsForTables() {
        StringBuilder sb = new StringBuilder();
        if (randomErrors) {
            for (String table : getTablesForRandomErrors()) {
                sb.append("\n\t\t Table: ").append(table)
                        .append("\n\t\t\t Random error(%)=").append(tablesPercentageForRandomErrors.get(table))
                        .append("\n\t\t\t Attributes to dirty=").append(tablesForRandomErrors.get(table));

            }
        }
        return sb.toString();
    }

    private String printDetailedDirtyAttributeStrategy() {
        StringBuilder sb = new StringBuilder();
        for (AttributeRef attribute : dirtyStrategiesMap.keySet()) {
            sb.append("\n\t\t Attribute: ").append(attribute.getTableName()).append(".").append(attribute.getName()).append("=").append(dirtyStrategiesMap.get(attribute));
        }
        return sb.toString();
    }

    private String printDetaileOutlierErrors() {
        if (!outlierErrors) return "";
        return "\n\t" + outlierErrorConfiguration.toString();
    }

}
