package it.unibas.bartgui.egtaskdataobject.nodes;

import it.unibas.bartgui.resources.R;
import javax.swing.Action;
import org.openide.awt.Actions;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@NbBundle.Messages({
    "CTL_SimpleNode=",
    "HINT_SimpleNode=Double Click for Load Configuration",
    "HINT_SimpleNodeHELP=Load"
})
public class SimpleNode extends DataNode   {
    
    private DataObject obj;
    
    public SimpleNode(DataObject obj,Lookup look) {
        super(obj,Children.LEAF,look);
        this.obj=obj;
        setShortDescription(Bundle.HINT_SimpleNode());      
    }
    
    @Override
    public String getHtmlDisplayName() {
        StringBuilder sb = new StringBuilder();
        sb.append(R.HTML_R_Node);
        sb.append(obj.getPrimaryFile().getName());
        sb.append(R.HTML_CL_R_Node);
        sb.append("   ");
        sb.append(R.HTML_Hint);
        sb.append(Bundle.HINT_SimpleNodeHELP());
        sb.append(R.HTML_CL_Hint);
        
        return sb.toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] a = super.getActions(context);
        Action[] all = new Action[a.length+ 2];
        System.arraycopy(a, 0, all, 2, a.length);
        all[0]=Actions.forID("SimpleNode", "it.unibas.bartgui.controlegt.actions.LoadSimpleNode");
        all[1]=null;
        return all;
    }

    @Override
    public Action getPreferredAction() {
        return Actions.forID("SimpleNode", "it.unibas.bartgui.controlegt.actions.LoadSimpleNode");
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