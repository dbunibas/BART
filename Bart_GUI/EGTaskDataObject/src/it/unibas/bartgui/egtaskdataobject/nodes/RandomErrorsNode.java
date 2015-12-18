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
    "CTL_RandomErrorsNode=Random Errors",
    "HINT_RandomErrorsNode=Random Errors"
})
public class RandomErrorsNode extends AbstractNode {

    public RandomErrorsNode(EGTask egt,EGTaskDataObjectDataObject dto) {
        super(Children.create(new RandomErrorsFactory(egt,dto), true),
                new ProxyLookup(Lookups.fixed(egt,dto),dto.getAbstractLookup()));
        setName(NodeResource.NODE_RandomErrorsNode);
        setShortDescription(Bundle.HINT_RandomErrorsNode());
        setIconBaseWithExtension(R.IMAGE_NODE_RANDS_ERR);       
    }
    
    @Override
    public String getHtmlDisplayName() {
         EGTask task = getLookup().lookup (EGTask.class); 
         if (task != null) { 
                return "<font color='#000052'><strong>"
                         +Bundle.CTL_RandomErrorsNode()+"</strong></font>";
         } else{
             return null; 
         }
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] result = new Action[]{
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
