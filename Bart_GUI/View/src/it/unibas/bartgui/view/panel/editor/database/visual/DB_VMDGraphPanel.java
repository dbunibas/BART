package it.unibas.bartgui.view.panel.editor.database.visual;

import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.statistics.Statistic;
import it.unibas.centrallookup.CentralLookup;
import java.awt.BorderLayout;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import speedy.model.database.IDatabase;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DB_VMDGraphPanel extends JPanel  implements MultiViewElement  {

    private MultiViewElementCallback callback = null;
    private DB_VMDGraph dbGraph;
    private JScrollPane scrPanel;
    private JToolBar toolbar;
    private Lookup.Result<Statistic> result;
    private final LookupStat listener = new LookupStat();

    public DB_VMDGraphPanel(String topCompName) {
        toolbar = new JToolBar();
        IDatabase database = getDB();
        if(database != null)  {
            setLayout(new BorderLayout());
            scrPanel = new JScrollPane();
            dbGraph = new DB_VMDGraph(database,topCompName);
            scrPanel.setViewportView(dbGraph.getView());
            dbGraph.invokeLayout();
            
            add(scrPanel,BorderLayout.CENTER);
            add(dbGraph.getScene().createSatelliteView(),BorderLayout.WEST);
        }
        
    }
    
    private IDatabase getDB()   {
        IDatabase db = null;
        EGTaskDataObjectDataObject dtoTmp =
                Utilities.actionsGlobalContext().lookup(EGTaskDataObjectDataObject.class);
        String tipe = Utilities.actionsGlobalContext().lookup(String.class);
        if(dtoTmp == null || tipe == null)return null;
        
        if(tipe.equals("Source"))db = dtoTmp.getEgtask().getSource();
        if(tipe.equals("Target"))db = dtoTmp.getEgtask().getTarget();
        if(tipe.equals("Dirty"))db = dtoTmp.getEgtask().getDirtyTarget();
         
        return db;
    }
    
    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        if(callback != null)   {
            return callback.createDefaultActions();
        }
        Action[] a = {};
        return a;
    }

    @Override
    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    @Override
    public void componentOpened() {
        result = Utilities.actionsGlobalContext().lookupResult(Statistic.class);
        result.addLookupListener(listener);
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(listener);
    }

    @Override
    public void componentShowing() {
        
    }

    @Override
    public void componentHidden() {
        
    }

    @Override
    public void componentActivated() {
        
    }

    @Override
    public void componentDeactivated() {
        
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    /**
     * @return the dbGraph
     */
    public DB_VMDGraph getDbGraph() {
        return dbGraph;
    }

    private class LookupStat implements LookupListener   {

        @Override
        public void resultChanged(LookupEvent ev) {
            Statistic s = Utilities.actionsGlobalContext().lookup(Statistic.class);
            DataObject dto = CentralLookup.getDefLookup().lookup(DataObject.class);
            if(dto == null)return;
            if((s != null)&&(dbGraph != null))   {
                dbGraph.highlighterError(s.getTableAliasChange(dto.getPrimaryFile().getName()));
            }else{
                //dbGraph.highlighterError(null);
            }
        }
        
    }
}
