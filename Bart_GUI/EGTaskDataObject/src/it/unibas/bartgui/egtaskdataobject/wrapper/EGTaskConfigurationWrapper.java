package it.unibas.bartgui.egtaskdataobject.wrapper;

import bart.model.EGTaskConfiguration;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.centrallookup.CentralLookup;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.Serializable;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import speedy.persistence.xml.operators.TransformFilePaths;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */

public class EGTaskConfigurationWrapper implements Serializable   {

    private PropertyChangeSupport pcs;
    private final TransformFilePaths transformFilePaths = new TransformFilePaths();
    //private VetoableChangeSupport vcs;
    
    private EGTaskConfiguration cfg;

    public EGTaskConfigurationWrapper() {
        pcs = new PropertyChangeSupport(this);
        //vcs = new VetoableChangeSupport(this);
    }
    
    
    
    public EGTaskConfigurationWrapper(EGTaskConfiguration cfg) {
        pcs = new PropertyChangeSupport(this);
        //vcs = new VetoableChangeSupport(this);
        this.cfg=cfg;
    }

    
    /**
     * @return the printLog
     */
    public boolean isPrintLog() {
        return cfg.isPrintLog();
    }

    /**
     * @param printLog the printLog to set
     */
    public void setPrintLog(boolean printLog) {
        boolean old = cfg.isPrintLog();
        cfg.setPrintLog(printLog);
        pcs.firePropertyChange("printLog", old, printLog);
    }

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return cfg.isDebug();
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug) {
        boolean old = cfg.isDebug();
        cfg.setDebug(debug);
        pcs.firePropertyChange("debug", old, debug);
    }

    /**
     * @return the queryExecutionTimeout
     */
    public Long getQueryExecutionTimeout() {
        return cfg.getQueryExecutionTimeout();
    }

    /**
     * @param queryExecutionTimeout the queryExecutionTimeout to set
     */
    public void setQueryExecutionTimeout(Long queryExecutionTimeout) {
        Long old = cfg.getQueryExecutionTimeout();
        cfg.setQueryExecutionTimeout(queryExecutionTimeout);
        pcs.firePropertyChange("queryExecutionTimeout", old, queryExecutionTimeout);
    }

    /**
     * @return the useDeltaDBForChanges
     */
    public boolean isUseDeltaDBForChanges() {
        return cfg.isUseDeltaDBForChanges();
    }

    /**
     * @param useDeltaDBForChanges the useDeltaDBForChanges to set
     */
    public void setUseDeltaDBForChanges(boolean useDeltaDBForChanges) {
        boolean old = cfg.isUseDeltaDBForChanges();
        cfg.setUseDeltaDBForChanges(useDeltaDBForChanges);
        pcs.firePropertyChange("useDeltaDBForChanges", old, useDeltaDBForChanges);
    }

    /**
     * @return the recreateDBOnStart
     */
    public boolean isRecreateDBOnStart() {
        return cfg.isRecreateDBOnStart();
    }

    /**
     * @param recreateDBOnStart the recreateDBOnStart to set
     */
    public void setRecreateDBOnStart(boolean recreateDBOnStart) {
        boolean old = cfg.isRecreateDBOnStart();
        cfg.setRecreateDBOnStart(recreateDBOnStart);
        pcs.firePropertyChange("recreateDBOnStart", old, recreateDBOnStart);
    }

    /**
     * @return the checkCleanInstance
     */
    public boolean isCheckCleanInstance() {
        return cfg.isCheckCleanInstance();
    }

    /**
     * @param checkCleanInstance the checkCleanInstance to set
     */
    public void setCheckCleanInstance(boolean checkCleanInstance) {
        boolean old = cfg.isCheckCleanInstance();
        cfg.setCheckCleanInstance(checkCleanInstance);
        pcs.firePropertyChange("checkCleanInstance", old, checkCleanInstance);
    }

    /**
     * @return the checkChanges
     */
    public boolean isCheckChanges() {
        return cfg.isCheckChanges();
    }

    /**
     * @param checkChanges the checkChanges to set
     */
    public void setCheckChanges(boolean checkChanges) {
        boolean old = cfg.isCheckChanges();
        cfg.setCheckChanges(checkChanges);
        pcs.firePropertyChange("checkChanges", old, checkChanges);
    }

    /**
     * @return the excludeCrossProducts
     */
    public boolean isExcludeCrossProducts() {
        return cfg.isExcludeCrossProducts();
    }

    /**
     * @param excludeCrossProducts the excludeCrossProducts to set
     */
    public void setExcludeCrossProducts(boolean excludeCrossProducts) {
        boolean old = cfg.isExcludeCrossProducts();
        cfg.setExcludeCrossProducts(excludeCrossProducts);
        pcs.firePropertyChange("excludeCrossProducts", old, excludeCrossProducts);
    }

    /**
     * @return the avoidInteractions
     */
    public boolean isAvoidInteractions() {
        return cfg.isAvoidInteractions();
    }

    /**
     * @param avoidInteractions the avoidInteractions to set
     */
    public void setAvoidInteractions(boolean avoidInteractions) {
        boolean old = cfg.isAvoidInteractions();
        cfg.setAvoidInteractions(avoidInteractions);
        pcs.firePropertyChange("avoidInteractions", old, avoidInteractions);
    }

    /**
     * @return the applyCellChanges
     */
    public boolean isApplyCellChanges() {
        return cfg.isApplyCellChanges();
    }

    /**
     * @param applyCellChanges the applyCellChanges to set
     */
    public void setApplyCellChanges(boolean applyCellChanges) {
        boolean old = cfg.isApplyCellChanges();
        cfg.setApplyCellChanges(applyCellChanges);
        pcs.firePropertyChange("applyCellChanges", old, applyCellChanges);
    }

    /**
     * @return the exportCellChanges
     */
    public boolean isExportCellChanges() {
        return cfg.isExportCellChanges();
    }

    /**
     * @param exportCellChanges the exportCellChanges to set
     */
    public void setExportCellChanges(boolean exportCellChanges) {
        boolean old = cfg.isExportCellChanges();
        cfg.setExportCellChanges(exportCellChanges);
        pcs.firePropertyChange("exportCellChanges", old, exportCellChanges);
    }

    /**
     * @return the exportCellChangesPath
     */
    public File getExportCellChangesPath() {
        File f = null;
        try{
            if(cfg.getExportCellChangesPath() != null)   {
                f = new File(cfg.getExportCellChangesPath());
            }
            return f;
        }catch(Exception ex)   {
            
        }
        return f;
    }

    /**
     * @param file the exportCellChangesPath to set
     */
    public void setExportCellChangesPath(File file) {
        EGTaskDataObjectDataObject dto = CentralLookup.getDefLookup().lookup(EGTaskDataObjectDataObject.class);
        String old = cfg.getExportCellChangesPath();
        if(file == null)   {
            cfg.setExportCellChangesPath(null);
            pcs.firePropertyChange("exportCellChangesPath", old, "");
            return;
        }
            StringBuilder sb = new StringBuilder(file.getAbsolutePath());
            sb.append("/cellChanges.csv");
            file = new File(sb.toString());  
            File basefile = FileUtil.toFile(dto.getPrimaryFile());                   
            String ExportCellChangesPath = transformFilePaths.relativize(basefile.getAbsolutePath(), file.getAbsolutePath());
            cfg.setExportCellChangesPath(ExportCellChangesPath);
            pcs.firePropertyChange("exportCellChangesPath", old, ExportCellChangesPath);           
    }

    /**
     * @return the exportDirtyDB
     */
    public boolean isExportDirtyDB() {
        return cfg.isExportDirtyDB();
    }

    /**
     * @param exportDirtyDB the exportDirtyDB to set
     */
    public void setExportDirtyDB(boolean exportDirtyDB) {
        boolean old = cfg.isExportDirtyDB();
        cfg.setExportDirtyDB(exportDirtyDB);
        pcs.firePropertyChange("exportDirtyDB", old, exportDirtyDB);
    }

    /**
     * @return the exportDirtyDBType
     */
    public String getExportDirtyDBType() {
        return cfg.getExportDirtyDBType();
    }

    /**
     * @param exportDirtyDBType the exportDirtyDBType to set
     */
    public void setExportDirtyDBType(String exportDirtyDBType) {
        String old = cfg.getExportDirtyDBType();
        cfg.setExportDirtyDBType(exportDirtyDBType);
        pcs.firePropertyChange("exportDirtyDBType", old, exportDirtyDBType);
    }

    /**
     * @return the exportDirtyDBPath
     */
    public File getExportDirtyDBPath() {
        File f = null;
        try{
            if(cfg.getExportDirtyDBPath() != null)   {
                f = new File(cfg.getExportDirtyDBPath());
            }
            return f;
        }catch(Exception ex)   {
            
        }
        return f;
    }

    /**
     * @param exportDirtyDBPath the exportDirtyDBPath to set
     */
    public void setExportDirtyDBPath(File file) {
        String old = cfg.getExportDirtyDBPath();
        EGTaskDataObjectDataObject dto = CentralLookup.getDefLookup().lookup(EGTaskDataObjectDataObject.class);
        if(file == null)   {
            cfg.setExportDirtyDBPath(null);
            pcs.firePropertyChange("exportDirtyDBPath", old, "");
            return;
        }

            File basefile = FileUtil.toFile(dto.getPrimaryFile());                   
            String ExportDirtyDBPath = transformFilePaths.relativize(basefile.getAbsolutePath(), file.getAbsolutePath());
            cfg.setExportDirtyDBPath(ExportDirtyDBPath);
            pcs.firePropertyChange("exportDirtyDBPath", old, ExportDirtyDBPath);
        
    }

    /**
     * @return the estimateRepairability
     */
    public boolean isEstimateRepairability() {
        return cfg.isEstimateRepairability();
    }

    /**
     * @param estimateRepairability the estimateRepairability to set
     */
    public void setEstimateRepairability(boolean estimateRepairability) {
        boolean old = cfg.isEstimateRepairability();
        cfg.setEstimateRepairability(estimateRepairability);
        pcs.firePropertyChange("estimateRepairability", old, estimateRepairability);
    }

    /**
     * @return the cloneTargetSchema
     */
    public boolean isCloneTargetSchema() {
        return cfg.isCloneTargetSchema();
    }

    /**
     * @param cloneTargetSchema the cloneTargetSchema to set
     */
    public void setCloneTargetSchema(boolean cloneTargetSchema) {
        boolean old = cfg.isCloneTargetSchema();
        cfg.setCloneTargetSchema(cloneTargetSchema);
        pcs.firePropertyChange("cloneTargetSchema", old, cloneTargetSchema);
    }

    /**
     * @return the cloneSuffix
     */
    public String getCloneSuffix() {
        return cfg.getCloneSuffix();
    }

    /**
     * @param cloneSuffix the cloneSuffix to set
     */
    public void setCloneSuffix(String cloneSuffix) {
        String old = cfg.getCloneSuffix();
        cfg.setCloneSuffix(cloneSuffix);
        pcs.firePropertyChange("cloneSuffix", old, cloneSuffix);
    }

    /**
     * @return the useSymmetricOptimization
     */
    public boolean isUseSymmetricOptimization() {
        return cfg.isUseSymmetricOptimization();
    }

    /**
     * @param useSymmetricOptimization the useSymmetricOptimization to set
     */
    public void setUseSymmetricOptimization(boolean useSymmetricOptimization) {
        boolean old = cfg.isUseSymmetricOptimization();
        cfg.setUseSymmetricOptimization(useSymmetricOptimization);
        pcs.firePropertyChange("useSymmetricOptimization", old, useSymmetricOptimization);
    }

    /**
     * @return the generateAllChanges
     */
    public boolean isGenerateAllChanges() {
        return cfg.isGenerateAllChanges();
    }

    /**
     * @param generateAllChanges the generateAllChanges to set
     */
    public void setGenerateAllChanges(boolean generateAllChanges) {
        boolean old = cfg.isGenerateAllChanges();
        cfg.setGenerateAllChanges(generateAllChanges);
        pcs.firePropertyChange("generateAllChanges", old, generateAllChanges);
    }

    /**
     * @return the sampleStrategyForStandardQueries
     */
    public String getSampleStrategyForStandardQueries() {
        return cfg.getSampleStrategyForStandardQueries();
    }

    /**
     * @param sampleStrategyForStandardQueries the sampleStrategyForStandardQueries to set
     */
    public void setSampleStrategyForStandardQueries(String sampleStrategyForStandardQueries) {
        String old = cfg.getSampleStrategyForStandardQueries();
        cfg.setSampleStrategyForStandardQueries(sampleStrategyForStandardQueries);
        pcs.firePropertyChange("sampleStrategyForStandardQueries", old, sampleStrategyForStandardQueries);
    }

    /**
     * @return the sampleStrategyForSymmetricQueries
     */
    public String getSampleStrategyForSymmetricQueries() {
        return cfg.getSampleStrategyForSymmetricQueries();
    }

    /**
     * @param sampleStrategyForSymmetricQueries the sampleStrategyForSymmetricQueries to set
     */
    public void setSampleStrategyForSymmetricQueries(String sampleStrategyForSymmetricQueries) {
        String old = cfg.getSampleStrategyForSymmetricQueries();
        cfg.setSampleStrategyForSymmetricQueries(sampleStrategyForSymmetricQueries);
        pcs.firePropertyChange("sampleStrategyForSymmetricQueries", old, sampleStrategyForSymmetricQueries);
    }

    /**
     * @return the sampleStrategyForInequalityQueries
     */
    public String getSampleStrategyForInequalityQueries() {
        return cfg.getSampleStrategyForInequalityQueries();
    }

    /**
     * @param sampleStrategyForInequalityQueries the sampleStrategyForInequalityQueries to set
     */
    public void setSampleStrategyForInequalityQueries(String sampleStrategyForInequalityQueries) {
        String old = cfg.getSampleStrategyForInequalityQueries();
        cfg.setSampleStrategyForInequalityQueries(sampleStrategyForInequalityQueries);
        pcs.firePropertyChange("sampleStrategyForInequalityQueries", old, sampleStrategyForInequalityQueries);
    }

    /**
     * @return the detectEntireEquivalenceClasses
     */
    public boolean isDetectEntireEquivalenceClasses() {
        return cfg.isDetectEntireEquivalenceClasses();
    }

    /**
     * @param detectEntireEquivalenceClasses the detectEntireEquivalenceClasses to set
     */
    public void setDetectEntireEquivalenceClasses(boolean detectEntireEquivalenceClasses) {
        boolean old = cfg.isDetectEntireEquivalenceClasses();
        cfg.setDetectEntireEquivalenceClasses(detectEntireEquivalenceClasses);
        pcs.firePropertyChange("detectEntireEquivalenceClasses", old, detectEntireEquivalenceClasses);
    }

    /**
     * @return the sizeFactorReduction
     */
    public double getSizeFactorReduction() {
        return cfg.getSizeFactorReduction();
    }

    /**
     * @param sizeFactorReduction the sizeFactorReduction to set
     */
    public void setSizeFactorReduction(double sizeFactorReduction) {
        double old = cfg.getSizeFactorReduction();
        cfg.setSizeFactorReduction(sizeFactorReduction);
        pcs.firePropertyChange("sizeFactorReduction", old, sizeFactorReduction);
    }

    /**
     * @return the randomErrors
     */
    public boolean isRandomErrors() {
        return cfg.isRandomErrors();
        
    }

    /**
     * @param randomErrors the randomErrors to set
     */
    public void setRandomErrors(boolean randomErrors) {
        boolean old = cfg.isRandomErrors();
        cfg.setRandomErrors(randomErrors);
        pcs.firePropertyChange("randomErrors", old, randomErrors);
    }

    /**
     * @return the outlierErrors
     */
    public boolean isOutlierErrors() {
        return cfg.isOutlierErrors();
    }

    /**
     * @param outlierErrors the outlierErrors to set
     */
    public void setOutlierErrors(boolean outlierErrors) {
        boolean old = cfg.isOutlierErrors();
        cfg.setOutlierErrors(outlierErrors);
        pcs.firePropertyChange("outlierErrors", old, outlierErrors);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    

}