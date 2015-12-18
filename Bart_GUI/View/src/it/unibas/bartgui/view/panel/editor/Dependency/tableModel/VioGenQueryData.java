package it.unibas.bartgui.view.panel.editor.Dependency.tableModel;

import bart.model.errorgenerator.VioGenQuery;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class VioGenQueryData {

    private String id;
    private String comparison;//HTML
    private double percentage;
    private String queryExecutor;
    private VioGenQuery vioGenQuery;

    public VioGenQueryData(String id, String comparison, double percentage, String queryExecutor) {
        this.id = id;
        this.comparison = comparison;
        this.percentage = percentage;
        this.queryExecutor = queryExecutor;
    }

    public VioGenQueryData() {
    }

    
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the comparison
     */
    public String getComparison() {
        return comparison;
    }

    /**
     * @param comparison the comparison to set
     */
    public void setComparison(String comparison) {
        this.comparison = comparison;
    }

    /**
     * @return the percentage
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * @param percentage the percentage to set
     */
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    /**
     * @return the queryExecutor
     */
    public String getQueryExecutor() {
        return queryExecutor;
    }

    /**
     * @param queryExecutor the queryExecutor to set
     */
    public void setQueryExecutor(String queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    /**
     * @return the vioGenQuery
     */
    public VioGenQuery getVioGenQuery() {
        return vioGenQuery;
    }

    /**
     * @param vioGenQuery the vioGenQuery to set
     */
    public void setVioGenQuery(VioGenQuery vioGenQuery) {
        this.vioGenQuery = vioGenQuery;
    }

    
}
