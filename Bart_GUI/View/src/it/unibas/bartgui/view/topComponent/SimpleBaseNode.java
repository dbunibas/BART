package it.unibas.bartgui.view.topComponent;

import it.unibas.bartgui.resources.R;
import javax.swing.Action;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@NbBundle.Messages({
    "CTL_SimpleBaseNode=New or Load Task"
})
public class SimpleBaseNode extends AbstractNode   {

    public SimpleBaseNode() {
        super(Children.LEAF);
        setIconBaseWithExtension(R.IMAGE_NODE_TEXT);
    }

    @Override
    public String getHtmlDisplayName() {
        return "<font color='#000052'><i>"+Bundle.CTL_SimpleBaseNode()+"</i></font>";
    }
    
    

    @Override
    public Action getPreferredAction() {
        return super.getPreferredAction();
    }
    
    @Override
    public Action[] getActions(boolean context) {
        Action[] a = {
                      Actions.forID("File", "it.unibas.bartgui.control.view.actions.NewTask"),
                      null,
                      Actions.forID("File", "it.unibas.bartgui.control.view.actions.OpenEgtaskFile")
                     };
        return a;
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
