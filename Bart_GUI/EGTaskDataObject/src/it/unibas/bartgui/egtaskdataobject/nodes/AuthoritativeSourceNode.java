package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.resources.R;
import java.io.IOException;
import javax.swing.Action;
import org.openide.actions.DeleteAction;
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
        "HINT_AutoritativeSourceNode=Authoritative Source"
})
public class AuthoritativeSourceNode extends AbstractNode   {

    private String autoritativeSource;
    
    public AuthoritativeSourceNode(EGTask egt, EGTaskDataObjectDataObject dto, String autoritativeSource) {
        super(Children.LEAF,
                new ProxyLookup(Lookups.fixed(egt,dto,autoritativeSource),dto.getAbstractLookup()));
        this.autoritativeSource = autoritativeSource;
        setName(autoritativeSource);
        setShortDescription(Bundle.HINT_AutoritativeSourceNode());
        setIconBaseWithExtension(R.IMAGE_DB_TABLE);       
    }
    
    @Override
    public String getHtmlDisplayName() {
        StringBuilder s = new StringBuilder();
        s.append(R.HTML_Node);
        s.append(autoritativeSource);
        s.append(R.HTML_CL_Node);
        return s.toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] result = new Action[]{
                SystemAction.get(DeleteAction.class),
        };
        return result;
    }

    @Override
    public Action getPreferredAction() {
        return super.getPreferredAction();
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
    public boolean canRename() {
        return false;
    }
    
    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public void destroy() throws IOException {
        fireNodeDestroyed();
    }  
}
