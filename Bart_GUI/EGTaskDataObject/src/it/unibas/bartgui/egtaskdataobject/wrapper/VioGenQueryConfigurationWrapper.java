package it.unibas.bartgui.egtaskdataobject.wrapper;

import bart.model.VioGenQueryConfiguration;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import org.netbeans.api.settings.ConvertAsJavaBean;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@ConvertAsJavaBean
public class VioGenQueryConfigurationWrapper implements Serializable   {

    private VioGenQueryConfiguration cfg;
    private final PropertyChangeSupport pcs;
    //private VetoableChangeSupport vcs;

    public VioGenQueryConfigurationWrapper() {
        pcs = new PropertyChangeSupport(this);
        //vcs = new VetoableChangeSupport(this);
    }
     
    public VioGenQueryConfigurationWrapper(VioGenQueryConfiguration cfg) {
        this.cfg=cfg;
        pcs = new PropertyChangeSupport(this);
        //vcs = new VetoableChangeSupport(this);
    }

    
    /**
     * @return the percentage
     */
    public double getPercentage() {
        return cfg.getPercentage();
    }

    /**
     * @param percentage the percentage to set
     */
    public void setPercentage(double percentage) {
        double old = cfg.getPercentage();
        cfg.setPercentage(percentage);
        pcs.firePropertyChange("percentage",old,percentage);
    }

    /**
     * @return the sizeFactorForStandardQueries
     */
    public double getSizeFactorForStandardQueries() {
        return cfg.getSizeFactorForStandardQueries();
    }

    /**
     * @param sizeFactorForStandardQueries the sizeFactorForStandardQueries to set
     */
    public void setSizeFactorForStandardQueries(double sizeFactorForStandardQueries) {
        double old = cfg.getSizeFactorForStandardQueries();
        pcs.firePropertyChange("sizeFactorForStandardQueries", old, sizeFactorForStandardQueries);
        cfg.setSizeFactorForStandardQueries(sizeFactorForStandardQueries);
    }

    /**
     * @return the sizeFactorForSymmetricQueries
     */
    public double getSizeFactorForSymmetricQueries() {
        return cfg.getSizeFactorForSymmetricQueries();
    }

    /**
     * @param sizeFactorForSymmetricQueries the sizeFactorForSymmetricQueries to set
     */
    public void setSizeFactorForSymmetricQueries(double sizeFactorForSymmetricQueries) {
        double old = cfg.getSizeFactorForSymmetricQueries();
        pcs.firePropertyChange("sizeFactorForSymmetricQueries", old, sizeFactorForSymmetricQueries);
        cfg.setSizeFactorForSymmetricQueries(sizeFactorForSymmetricQueries);
    }

    /**
     * @return the sizeFactorForInequalityQueries
     */
    public double getSizeFactorForInequalityQueries() {
        return cfg.getSizeFactorForInequalityQueries();
    }

    /**
     * @param sizeFactorForInequalityQueries the sizeFactorForInequalityQueries to set
     */
    public void setSizeFactorForInequalityQueries(double sizeFactorForInequalityQueries) {
        double old = cfg.getSizeFactorForInequalityQueries();
        pcs.firePropertyChange("sizeFactorForInequalityQueries", old, sizeFactorForInequalityQueries);
        cfg.setSizeFactorForInequalityQueries(sizeFactorForInequalityQueries);
    }

    /**
     * @return the sizeFactorForSingleTupleQueries
     */
    public double getSizeFactorForSingleTupleQueries() {
        return cfg.getSizeFactorForSingleTupleQueries();
    }

    /**
     * @param sizeFactorForSingleTupleQueries the sizeFactorForSingleTupleQueries to set
     */
    public void setSizeFactorForSingleTupleQueries(double sizeFactorForSingleTupleQueries) {
        double old = cfg.getSizeFactorForSingleTupleQueries();
        pcs.firePropertyChange("sizeFactorForSingleTupleQueries", old, sizeFactorForSingleTupleQueries);
        cfg.setSizeFactorForSingleTupleQueries(sizeFactorForSingleTupleQueries);
    }

    /**
     * @return the probabilityFactorForStandardQueries
     */
    public double getProbabilityFactorForStandardQueries() {
        return cfg.getProbabilityFactorForStandardQueries();
    }

    /**
     * @param probabilityFactorForStandardQueries the probabilityFactorForStandardQueries to set
     */
    public void setProbabilityFactorForStandardQueries(double probabilityFactorForStandardQueries) {
        double old = cfg.getProbabilityFactorForStandardQueries();
        pcs.firePropertyChange("probabilityFactorForStandardQueries", old, probabilityFactorForStandardQueries);
        cfg.setProbabilityFactorForStandardQueries(probabilityFactorForStandardQueries);
    }

    /**
     * @return the probabilityFactorForSymmetricQueries
     */
    public double getProbabilityFactorForSymmetricQueries() {
        return cfg.getProbabilityFactorForSymmetricQueries();
    }

    /**
     * @param probabilityFactorForSymmetricQueries the probabilityFactorForSymmetricQueries to set
     */
    public void setProbabilityFactorForSymmetricQueries(double probabilityFactorForSymmetricQueries) {
        double old = cfg.getProbabilityFactorForSymmetricQueries();
        pcs.firePropertyChange("probabilityFactorForSymmetricQueries", old, probabilityFactorForSymmetricQueries);
        cfg.setProbabilityFactorForSymmetricQueries(probabilityFactorForSymmetricQueries);
    }

    /**
     * @return the probabilityFactorForInequalityQueries
     */
    public double getProbabilityFactorForInequalityQueries() {
        return cfg.getProbabilityFactorForInequalityQueries();
    }

    /**
     * @param probabilityFactorForInequalityQueries the probabilityFactorForInequalityQueries to set
     */
    public void setProbabilityFactorForInequalityQueries(double probabilityFactorForInequalityQueries) {
        double old = cfg.getProbabilityFactorForInequalityQueries();
        pcs.firePropertyChange("probabilityFactorForInequalityQueries", old, probabilityFactorForInequalityQueries);
        cfg.setProbabilityFactorForInequalityQueries(probabilityFactorForInequalityQueries);
    }

    /**
     * @return the probabilityFactorForSingleTupleQueries
     */
    public double getProbabilityFactorForSingleTupleQueries() {
        return cfg.getProbabilityFactorForSingleTupleQueries();
    }

    /**
     * @param probabilityFactorForSingleTupleQueries the probabilityFactorForSingleTupleQueries to set
     */
    public void setProbabilityFactorForSingleTupleQueries(double probabilityFactorForSingleTupleQueries) {
        double old  = cfg.getProbabilityFactorForSingleTupleQueries();
        pcs.firePropertyChange("probabilityFactorForSingleTupleQueries", old, probabilityFactorForSingleTupleQueries);
        cfg.setProbabilityFactorForSingleTupleQueries(probabilityFactorForSingleTupleQueries);
    }

    /**
     * @return the windowSizeFactorForStandardQueries
     */
    public double getWindowSizeFactorForStandardQueries() {
        return cfg.getWindowSizeFactorForStandardQueries();
    }

    /**
     * @param windowSizeFactorForStandardQueries the windowSizeFactorForStandardQueries to set
     */
    public void setWindowSizeFactorForStandardQueries(double windowSizeFactorForStandardQueries) {
        double old = cfg.getWindowSizeFactorForStandardQueries();
        pcs.firePropertyChange("windowSizeFactorForStandardQueries", old, windowSizeFactorForStandardQueries);
        cfg.setWindowSizeFactorForStandardQueries(windowSizeFactorForStandardQueries);
    }

    /**
     * @return the windowSizeFactorForSymmetricQueries
     */
    public double getWindowSizeFactorForSymmetricQueries() {
        return cfg.getWindowSizeFactorForSymmetricQueries();
    }

    /**
     * @param windowSizeFactorForSymmetricQueries the windowSizeFactorForSymmetricQueries to set
     */
    public void setWindowSizeFactorForSymmetricQueries(double windowSizeFactorForSymmetricQueries) {
        double old = cfg.getWindowSizeFactorForSymmetricQueries();
        pcs.firePropertyChange("windowSizeFactorForSymmetricQueries", old, windowSizeFactorForSymmetricQueries);
        cfg.setWindowSizeFactorForSymmetricQueries(windowSizeFactorForSymmetricQueries);
    }

    /**
     * @return the windowSizeFactorForInequalityQueries
     */
    public double getWindowSizeFactorForInequalityQueries() {
        return cfg.getWindowSizeFactorForInequalityQueries();
    }

    /**
     * @param windowSizeFactorForInequalityQueries the windowSizeFactorForInequalityQueries to set
     */
    public void setWindowSizeFactorForInequalityQueries(double windowSizeFactorForInequalityQueries) {
        double old = cfg.getWindowSizeFactorForInequalityQueries();
        pcs.firePropertyChange("windowSizeFactorForInequalityQueries", old, windowSizeFactorForInequalityQueries);
        cfg.setWindowSizeFactorForInequalityQueries(windowSizeFactorForInequalityQueries);
    }

    /**
     * @return the windowSizeFactorForSingleTupleQueries
     */
    public double getWindowSizeFactorForSingleTupleQueries() {
        return cfg.getWindowSizeFactorForSingleTupleQueries();
    }

    /**
     * @param windowSizeFactorForSingleTupleQueries the windowSizeFactorForSingleTupleQueries to set
     */
    public void setWindowSizeFactorForSingleTupleQueries(double windowSizeFactorForSingleTupleQueries) {
        double old = cfg.getWindowSizeFactorForSingleTupleQueries();
        pcs.firePropertyChange("windowSizeFactorForSingleTupleQueries", old, windowSizeFactorForSingleTupleQueries);
        cfg.setWindowSizeFactorForSingleTupleQueries(windowSizeFactorForSingleTupleQueries);
    }

    /**
     * @return the offsetFactorForStandardQueries
     */
    public double getOffsetFactorForStandardQueries() {
        return cfg.getOffsetFactorForStandardQueries();
    }

    /**
     * @param offsetFactorForStandardQueries the offsetFactorForStandardQueries to set
     */
    public void setOffsetFactorForStandardQueries(double offsetFactorForStandardQueries) {
        double old =  cfg.getOffsetFactorForStandardQueries();
        pcs.firePropertyChange("offsetFactorForStandardQueries", old, offsetFactorForStandardQueries);
        cfg.setOffsetFactorForStandardQueries(offsetFactorForStandardQueries);
    }

    /**
     * @return the offsetFactorForSymmetricQueries
     */
    public double getOffsetFactorForSymmetricQueries() {
        return cfg.getOffsetFactorForSymmetricQueries();
    }

    /**
     * @param offsetFactorForSymmetricQueries the offsetFactorForSymmetricQueries to set
     */
    public void setOffsetFactorForSymmetricQueries(double offsetFactorForSymmetricQueries) {
        double old = cfg.getOffsetFactorForSymmetricQueries();
        pcs.firePropertyChange("offsetFactorForSymmetricQueries", old, offsetFactorForSymmetricQueries);
        cfg.setOffsetFactorForSymmetricQueries(offsetFactorForSymmetricQueries);
    }

    /**
     * @return the offsetFactorForInequalityQueries
     */
    public double getOffsetFactorForInequalityQueries() {
        return cfg.getOffsetFactorForInequalityQueries();
    }

    /**
     * @param offsetFactorForInequalityQueries the offsetFactorForInequalityQueries to set
     */
    public void setOffsetFactorForInequalityQueries(double offsetFactorForInequalityQueries) {
        double old = cfg.getOffsetFactorForInequalityQueries();
        pcs.firePropertyChange("offsetFactorForInequalityQueries", old, offsetFactorForInequalityQueries);
        cfg.setOffsetFactorForInequalityQueries(offsetFactorForInequalityQueries);
    }

    /**
     * @return the offsetFactorForSingleTupleQueries
     */
    public double getOffsetFactorForSingleTupleQueries() {
        return cfg.getOffsetFactorForSingleTupleQueries();
    }

    /**
     * @param offsetFactorForSingleTupleQueries the offsetFactorForSingleTupleQueries to set
     */
    public void setOffsetFactorForSingleTupleQueries(double offsetFactorForSingleTupleQueries) {
        double old = cfg.getOffsetFactorForSingleTupleQueries();
        pcs.firePropertyChange("offsetFactorForSingleTupleQueries", old, offsetFactorForSingleTupleQueries);
        cfg.setOffsetFactorForSingleTupleQueries(offsetFactorForSingleTupleQueries);
    }

    /**
     * @return the useLimitInStandardQueries
     */
    public boolean isUseLimitInStandardQueries() {
        return cfg.isUseLimitInStandardQueries();
    }

    /**
     * @param useLimitInStandardQueries the useLimitInStandardQueries to set
     */
    public void setUseLimitInStandardQueries(boolean useLimitInStandardQueries) {
        boolean old = cfg.isUseLimitInStandardQueries();
        pcs.firePropertyChange("useLimitInStandardQueries", old, useLimitInStandardQueries);
        cfg.setUseLimitInStandardQueries(useLimitInStandardQueries);
    }

    /**
     * @return the useLimitInSymmetricQueries
     */
    public boolean isUseLimitInSymmetricQueries() {
        return cfg.isUseLimitInSymmetricQueries();
    }

    /**
     * @param useLimitInSymmetricQueries the useLimitInSymmetricQueries to set
     */
    public void setUseLimitInSymmetricQueries(boolean useLimitInSymmetricQueries) {
        boolean old = cfg.isUseLimitInSymmetricQueries();
        pcs.firePropertyChange("useLimitInSymmetricQueries", old, useLimitInSymmetricQueries);
        cfg.setUseLimitInSymmetricQueries(useLimitInSymmetricQueries);
    }

    /**
     * @return the useLimitInInequalityQueries
     */
    public boolean isUseLimitInInequalityQueries() {
        return cfg.isUseLimitInInequalityQueries();
    }

    /**
     * @param useLimitInInequalityQueries the useLimitInInequalityQueries to set
     */
    public void setUseLimitInInequalityQueries(boolean useLimitInInequalityQueries) {
        boolean old = cfg.isUseLimitInInequalityQueries();
        pcs.firePropertyChange("useLimitInInequalityQueries", old, useLimitInInequalityQueries);
        cfg.setUseLimitInInequalityQueries(useLimitInInequalityQueries);
    }

    /**
     * @return the useLimitInSingleTupleQueries
     */
    public boolean isUseLimitInSingleTupleQueries() {
        return cfg.isUseLimitInSingleTupleQueries();
    }

    /**
     * @param useLimitInSingleTupleQueries the useLimitInSingleTupleQueries to set
     */
    public void setUseLimitInSingleTupleQueries(boolean useLimitInSingleTupleQueries) {
        boolean old = cfg.isUseLimitInSingleTupleQueries();
        pcs.firePropertyChange("useLimitInSingleTupleQueries", old, useLimitInSingleTupleQueries);
        cfg.setUseLimitInSingleTupleQueries(useLimitInSingleTupleQueries);
    }

    /**
     * @return the useOffsetInStandardQueries
     */
    public boolean isUseOffsetInStandardQueries() {
        return cfg.isUseOffsetInStandardQueries();
    }

    /**
     * @param useOffsetInStandardQueries the useOffsetInStandardQueries to set
     */
    public void setUseOffsetInStandardQueries(boolean useOffsetInStandardQueries) {
        boolean old = cfg.isUseOffsetInStandardQueries();
        pcs.firePropertyChange("useOffsetInStandardQueries", old, useOffsetInStandardQueries);
        cfg.setUseOffsetInStandardQueries(useOffsetInStandardQueries);
    }

    /**
     * @return the useOffsetInSymmetricQueries
     */
    public boolean isUseOffsetInSymmetricQueries() {
        return cfg.isUseOffsetInSymmetricQueries();
    }

    /**
     * @param useOffsetInSymmetricQueries the useOffsetInSymmetricQueries to set
     */
    public void setUseOffsetInSymmetricQueries(boolean useOffsetInSymmetricQueries) {
        boolean old = cfg.isUseOffsetInSymmetricQueries();
        pcs.firePropertyChange("useOffsetInSymmetricQueries", old, useOffsetInSymmetricQueries);
        cfg.setUseOffsetInSymmetricQueries(useOffsetInSymmetricQueries);
    }

    /**
     * @return the useOffsetInInequalityQueries
     */
    public boolean isUseOffsetInInequalityQueries() {
        return cfg.isUseOffsetInInequalityQueries();
    }

    /**
     * @param useOffsetInInequalityQueries the useOffsetInInequalityQueries to set
     */
    public void setUseOffsetInInequalityQueries(boolean useOffsetInInequalityQueries) {
        boolean old = cfg.isUseOffsetInInequalityQueries();
        pcs.firePropertyChange("useOffsetInInequalityQueries", old, useOffsetInInequalityQueries);
        cfg.setUseOffsetInInequalityQueries(useOffsetInInequalityQueries);
    }

    /**
     * @return the useOffsetInSingleTupleQueries
     */
    public boolean isUseOffsetInSingleTupleQueries() {
        return cfg.isUseOffsetInSingleTupleQueries();
    }

    /**
     * @param useOffsetInSingleTupleQueries the useOffsetInSingleTupleQueries to set
     */
    public void setUseOffsetInSingleTupleQueries(boolean useOffsetInSingleTupleQueries) {
        boolean old = cfg.isUseOffsetInSingleTupleQueries();
        pcs.firePropertyChange("useOffsetInSingleTupleQueries", old, useOffsetInSingleTupleQueries);
        cfg.setUseOffsetInSingleTupleQueries(useOffsetInSingleTupleQueries);
    }

    /**
     * @return the queryExecutor
     */
    public String getQueryExecutor() {
        return cfg.getQueryExecutor();
    }

    /**
     * @param queryExecutor the queryExecutor to set
     */
    public void setQueryExecutor(String queryExecutor) {
        String old = cfg.getQueryExecutor();
        pcs.firePropertyChange("queryExecutor", old, queryExecutor);
        if(queryExecutor.isEmpty())  {
            cfg.setQueryExecutor(null);
        }else{
            cfg.setQueryExecutor(queryExecutor);
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
}
