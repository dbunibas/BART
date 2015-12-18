package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.resources.R;
import javax.swing.Action;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@NbBundle.Messages({
    "HINT_DatabaseConfigurationNode=Setting Database parameters",
    "CTL_DatabaseConfigurationNode=Database Configurations "
})
public class DatabaseConfigurationNode extends AbstractNode  {
    
    public DatabaseConfigurationNode(EGTask task,EGTaskDataObjectDataObject dto) {
        super(Children.create(new DataBaseConfigurationFactory(task,dto), true), 
                new ProxyLookup(Lookups.fixed(task,dto),dto.getAbstractLookup()));
        setName(NodeResource.NODE_DatabaseConfigurationNode);
        setShortDescription(Bundle.HINT_DatabaseConfigurationNode());
        setIconBaseWithExtension(R.IMAGE_NODE_DBMS_CONF);       
    }
    
    @Override
    public String getHtmlDisplayName() {
         EGTask task = getLookup().lookup (EGTask.class); 
         if (task != null) { 
             StringBuilder sb = new StringBuilder();
             sb.append(R.HTML_R_Node);
             sb.append(Bundle.CTL_DatabaseConfigurationNode());
             sb.append(R.HTML_CL_R_Node);
             return sb.toString();
         } else{
             return null; 
         }
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] result = new Action[]{
            SystemAction.get(OpenLocalExplorerAction.class),
        };
        return result;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public boolean canRename() {
        return false;
    }
    
    @Override
    public boolean canCut() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return false;
    }

}