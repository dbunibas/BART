package it.unibas.bartgui.egtaskdataobject.statistics;

import java.io.Serializable;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@SuppressWarnings("rawtypes")
public class VGQ_Stat implements Serializable,Comparable {

    private String viogenquery;
    private String dependencyID;

    public VGQ_Stat(String viogenquery, String dependencyID) {
        this.viogenquery = viogenquery;
        this.dependencyID = dependencyID;
    }

    public String getViogenquery() {
        return viogenquery;
    }

    public void setViogenquery(String viogenquery) {
        this.viogenquery = viogenquery;
    }

    public String getDependencyID() {
        return dependencyID;
    }

    public void setDependencyID(String dependencyID) {
        this.dependencyID = dependencyID;
    }

    @Override
    public int compareTo(Object o) {       
        VGQ_Stat v = (VGQ_Stat)o;
        return this.getViogenquery().compareTo(v.getViogenquery());   
    }

    @Override
    public String toString() {
        return getViogenquery();
    }

    
    
}
