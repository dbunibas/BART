package it.unibas.bartgui.controlegt.actions.node.dbNode;

import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.util.TableNameContext;
import it.unibas.bartgui.view.ViewResource;
import it.unibas.bartgui.view.panel.editor.database.visual.DB_VMDGraphPanel;
import it.unibas.bartgui.view.panel.editor.database.visual.DB_VMDGraphPanelDescription;
import it.unibas.bartgui.view.panel.editor.database.visual.InfoDB;
import it.unibas.bartgui.view.panel.editor.database.visual.InfoDBDescription;
import it.unibas.bartgui.view.panel.editor.database.visual.TableDataView;
import it.unibas.bartgui.view.panel.editor.database.visual.TableDataViewDescription;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;
import org.netbeans.core.api.multiview.MultiViewHandler;
import org.netbeans.core.api.multiview.MultiViewPerspective;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.ErrorManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import speedy.model.database.IDatabase;

@ActionID(
        category = "DBNode",
        id = "it.unibas.bartgui.controlegt.actions.node.dbNode.Open"
)
@ActionRegistration(
        displayName = "#CTL_Open"
)
@Messages("CTL_Open=Open")
public final class Open implements ActionListener {

    private IDatabase database;
    private final EGTaskDataObjectDataObject dto;
    private final String dbmsT;
    private final TableNameContext tableName;
    private String panelID;

    public Open(EGTaskDataObjectDataObject dto) {
        this.dto = dto;      
        dbmsT = Utilities.actionsGlobalContext().lookup(String.class);
        tableName = Utilities.actionsGlobalContext().lookup(TableNameContext.class);
        if((dbmsT != null) && (dto != null))   {
            if(dbmsT.equals("Source"))   {
                database = dto.getEgtask().getSource();
                panelID=ViewResource.MULTW_ID_DB_Source_VMDGraphPanel;
            }
            if(dbmsT.equals("Target"))   {
                database = dto.getEgtask().getTarget();
                panelID=ViewResource.MULTW_ID_DB_Target_VMDGraphPanel;
            }
            if(dbmsT.equals("Dirty"))   {
                database = dto.getEgtask().getDirtyTarget();
                panelID=ViewResource.MULTW_ID_DB_Dirty_VMDGraphPanel;
            }
        }else{
            database = null;
        }
    }

    @Override 
    public void actionPerformed(ActionEvent ev) {       
        if(database == null)return;
        TopComponent dbGraph = null;
        Set<TopComponent> set = WindowManager.getDefault().getRegistry().getOpened();
        Iterator<TopComponent> it = set.iterator();
        while(it.hasNext())   {
            TopComponent tmp = it.next();
            if(tmp.getName().equals(dbmsT))   {
                dbGraph = tmp;
                break;
            }
        }       
        if(dbGraph != null)   {
            dbGraph.requestActive();
        }else{  
            try{
                MultiViewElement info = new InfoDB(database);
                MultiViewElement panel = new DB_VMDGraphPanel(dbmsT);  
                
                MultiViewDescription dsc[] = {
                    new DB_VMDGraphPanelDescription(panel,panelID,"DB Graph"),
                    new InfoDBDescription(info)};
                
                dbGraph = MultiViewFactory.createMultiView(dsc, dsc[0]);
                dbGraph.setName(dbmsT);
                dbGraph.setDisplayName(dbmsT);
                
                Mode md = WindowManager.getDefault().findMode("editor");
                if(md != null) md.dockInto(dbGraph);
                dbGraph.open();
                dbGraph.requestActive();        
            }catch(Exception ex)   {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
            }
        }
        if((dbGraph != null) && (tableName != null))   {
            MultiViewHandler mvh = MultiViews.findMultiViewHandler(dbGraph);     
            MultiViewPerspective[] mp = mvh.getPerspectives();
            for(MultiViewPerspective xxx : mp)   {
                if(tableName.getName().equals(xxx.preferredID()))   {
                    mvh.requestActive(xxx);
                    return;
                }
            }
            TableDataView panel = new TableDataView(database.getTable(tableName.getName()));
            mvh.addMultiViewDescription(
                    new TableDataViewDescription(tableName.getName(), tableName.getName(), panel), 30);
            panel.requestFocus();  
        }
    }
    
}
