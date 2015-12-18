package it.unibas.bartgui.view.panel.editor.Dependency.tableModel;


import bart.model.errorgenerator.VioGenQuery;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

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
