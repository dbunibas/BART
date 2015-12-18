package it.unibas.bartgui.view.panel.editor.database.visual;

import java.util.Iterator;
import java.util.Set;
import org.netbeans.api.visual.action.EditProvider;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.core.api.multiview.MultiViewHandler;
import org.netbeans.core.api.multiview.MultiViewPerspective;
import org.netbeans.core.api.multiview.MultiViews;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import speedy.model.database.ITable;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class TableEditProvider implements EditProvider   {

    private String topCompName;
    private ITable table;

    public TableEditProvider(String topCompName, ITable table) {
        this.topCompName = topCompName;
        this.table = table;
    }
    
    @Override
    public void edit(Widget widget) {
        Set<TopComponent> set = WindowManager.getDefault().getRegistry().getOpened();
        Iterator<TopComponent> it = set.iterator();
        TopComponent tableView = null;
        while(it.hasNext())   {
            TopComponent tmp = it.next();
            if(tmp.getName().equals(topCompName))   {
                tableView = tmp;
                break;
            }
        }
        if(tableView !=null)   {
            MultiViewHandler mvh = MultiViews.findMultiViewHandler(tableView);     
            MultiViewPerspective[] mp = mvh.getPerspectives();
            for(MultiViewPerspective xxx : mp)   {
                if(table.getName().equals(xxx.preferredID()))   {
                    mvh.requestActive(xxx);
                    return;
                }
            }
            TableDataView panel = new TableDataView(table);
            mvh.addMultiViewDescription(
                    new TableDataViewDescription(table.getName(), table.getName(), panel), 30);
            panel.requestFocus();         
        }
    }

    
}
