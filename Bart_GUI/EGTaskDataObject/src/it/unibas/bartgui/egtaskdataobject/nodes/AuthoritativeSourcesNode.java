package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.resources.R;
import javax.swing.Action;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@NbBundle.Messages({
    "CTL_AutoritativeSourcesNode=Autoritative Sources",
    "HINT_AutoritativeSourcesNode=List Autoritative Sources "
})
public class AuthoritativeSourcesNode extends AbstractNode   {

    public AuthoritativeSourcesNode(EGTask egt,EGTaskDataObjectDataObject dto) {
        super(Children.create(new AuthoritativeSourcesNodeFactory(dto, egt), true),
                new ProxyLookup(Lookups.fixed(egt,dto),dto.getAbstractLookup()));
        setName(NodeResource.NODE_AutoritativeSourcesNode);
        setShortDescription(Bundle.HINT_AutoritativeSourcesNode());
        setIconBaseWithExtension(R.IMAGE_AUTORITATIVE);
    }
    
    @Override
    public String getHtmlDisplayName() {
        StringBuilder s = new StringBuilder();
        s.append(R.HTML_Node);
        s.append(Bundle.CTL_AutoritativeSourcesNode());
        s.append(R.HTML_CL_Node);
        return s.toString();
    }
    
    @Override
    public Action[] getActions(boolean context) {
        Action[] result = new Action[]{
                Actions.forID("AutoritativeSourceNode", 
                        "it.unibas.bartgui.controlegt.actions.node.autoritativeSource.Add")
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
