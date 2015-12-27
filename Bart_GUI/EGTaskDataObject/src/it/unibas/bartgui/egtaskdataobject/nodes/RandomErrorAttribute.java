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
    "HINT_RandomErrorAttribute=attributes to dirty in the table"
})
public class RandomErrorAttribute extends AbstractNode   {
    
    private String attribute;
    
    public RandomErrorAttribute(EGTask egt,EGTaskDataObjectDataObject dto,String att) {
        super(Children.LEAF,
                new ProxyLookup(Lookups.fixed(egt,dto),dto.getAbstractLookup()));
        this.attribute=att;
        setName(NodeResource.NODE_RandomErrorAttribute);
        setShortDescription(Bundle.HINT_RandomErrorAttribute());
        setIconBaseWithExtension(R.IMAGE_AUTORITATIVE); 
    }

    @Override
    public String getHtmlDisplayName() {
             return "<font color='#000000'>"
                    +attribute
                    +"</font>";
    }
    
    

    @Override
    public Action getPreferredAction() {
        return super.getPreferredAction();
    }
    
    
    @Override
    public Action[] getActions(boolean context) {
       Action[] result = new Action[]{
            Actions.forID("RandomErrorsNode", "it.unibas.bartgui.controlegt.actions.node.randomError.EditToDO"),
        };
        return result;
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

}
