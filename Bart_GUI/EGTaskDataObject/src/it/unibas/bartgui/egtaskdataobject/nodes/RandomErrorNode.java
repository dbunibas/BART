package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.resources.R;
import javax.swing.Action;
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
    "CTL_RandomErrorNode=Random Error ",
    "HINT_RandomErrorNode=List of tables"
})
public class RandomErrorNode extends AbstractNode {

    public RandomErrorNode(EGTask egt,EGTaskDataObjectDataObject dto)  {
        super(Children.create(new RandomErrorTableFactory(egt,dto), true),
                new ProxyLookup(Lookups.fixed(egt,dto),dto.getAbstractLookup()));
        setName(NodeResource.NODE_RandomErrorNode);
        setShortDescription(Bundle.HINT_RandomErrorNode());
        setIconBaseWithExtension(R.IMAGE_NODE_VIOs_PROB);       
    }

    
    @Override
    public String getHtmlDisplayName() {
         EGTask task = getLookup().lookup (EGTask.class); 
         if (task != null) { 
             return "<font color='#000052'>"
                     +Bundle.CTL_RandomErrorNode()+" </font>"+
                     "<font color='#888888'><i>"
                    +" tables ("+task.getConfiguration().getTablesForRandomErrors().size()
                     +")"+"</i></font>";
         } else{
             return null; 
         }
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] result = new Action[]{
            Actions.forID("RandomErrorsNode", "it.unibas.bartgui.controlegt.actions.node.randomError.EditToDO"),
            null,
            SystemAction.get(OpenLocalExplorerAction.class)
        };
        return result;
    }

    @Override
    public Action getPreferredAction() {
        return super.getPreferredAction();
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
