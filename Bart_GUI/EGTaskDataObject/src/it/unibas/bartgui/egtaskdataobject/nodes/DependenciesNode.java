package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DependenciesNodeNotify;
import it.unibas.bartgui.resources.R;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.awt.Actions;
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
    "CTL_DependenciesNode=Dependencies",
    "HINT_DependenciesNode=List of dependencies and VioGenQueries ( %)"
})
public class DependenciesNode extends AbstractNode   {

    private ChangeListener listener;
    
    public DependenciesNode(EGTask egt,EGTaskDataObjectDataObject dto) {
        super(Children.create(new DependenciesFactory(egt,dto), true), 
                new ProxyLookup(Lookups.fixed(egt,dto),dto.getAbstractLookup()));
        setName(NodeResource.NODE_DependenciesNode);
        setShortDescription(Bundle.HINT_DependenciesNode());
        setIconBaseWithExtension(R.IMAGE_NODE_DCS); 
        DependenciesNodeNotify.addChangeListener(listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                fireDisplayNameChange(null, "");
            }
        });
    }
    
    @Override
    public String getHtmlDisplayName() {
         EGTask task = getLookup().lookup (EGTask.class); 
         if (task != null) { 
             StringBuilder sb = new StringBuilder();
             sb.append(R.HTML_R_Node);
             sb.append(Bundle.CTL_DependenciesNode());
             sb.append(R.HTML_CL_R_Node);
             sb.append(R.HTML_Hint+"<strong>");
             sb.append(" (");sb.append(task.getDCs().size());sb.append(") ");
             if(task.getConfiguration().isGenerateAllChanges())   {
                 sb.append("- Generate All Changes");
                 sb.append("</strong>"+R.HTML_CL_Hint); 
             }else{
                sb.append("- default ");
                sb.append(task.getConfiguration().getDefaultVioGenQueryConfiguration().getPercentage());
                sb.append("%");
                sb.append("</strong>"+R.HTML_CL_Hint); 
             }
           
             return sb.toString();
         } else{
             return null; 
         }
    }
    
    @Override
    public Action[] getActions(boolean context) {
        Action[] result = new Action[]{
            Actions.forID("DependenciesNode", "it.unibas.bartgui.controlegt.actions.node.DependenciesNode.Edit"),
            null,
            SystemAction.get(OpenLocalExplorerAction.class)
        };
        return result;
    }

    @Override
    public Action getPreferredAction() {
        return Actions.forID("DependenciesNode", "it.unibas.bartgui.controlegt.actions.node.DependenciesNode.Edit");
    }

    @Override
    public boolean canCut() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return false;
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
    protected void finalize() throws Throwable {
        DependenciesNodeNotify.removeChangeListener(listener);
        super.finalize();
    }

    
}