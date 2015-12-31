package it.unibas.bartgui.view.panel.editor.Dependency.tableModel;


import java.util.Vector;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class VioGenQueriesData {

    private Vector<VioGenQueryData> vioGQVector;

    public VioGenQueriesData() {
        this.vioGQVector  = new Vector<VioGenQueryData>();   
    }
    
    public void addVio(VioGenQueryData v)   {
        getVioGQVector().add(v);
    }
    
    public void removeVio(VioGenQueryData v)   {
        getVioGQVector().remove(v);
    } 

    public Vector<VioGenQueryData> getVioGQVector() {
        return vioGQVector;
    }
}
