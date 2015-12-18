/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import bart.model.errorgenerator.operator.valueselectors.IDirtyStrategy;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
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
    "HINT_DirtyStrategiesNode=Dirty Strategies"
})
public class DirtyStrategiesNode extends AbstractNode   {

    public DirtyStrategiesNode(EGTask task,EGTaskDataObjectDataObject dto) {
        super(Children.create(new DirtyStrategiesFactory(dto, task), true), 
                new ProxyLookup(Lookups.fixed(task,dto),dto.getAbstractLookup()));
        setName(NodeResource.NODE_DirtyStrategiesNode);
        setShortDescription(Bundle.HINT_DirtyStrategiesNode());
        setIconBaseWithExtension(R.IMAGE_NODE_DIRTYSTRATEGIES);
    }
    
    @Override
    public String getHtmlDisplayName() {
         EGTask task = getLookup().lookup (EGTask.class); 
         if (task != null) { 
             IDirtyStrategy dStr = task.getConfiguration().getDefaultDirtyStrategy();
             if(dStr != null)setShortDescription(dStr.toString());
             StringBuilder sb = new StringBuilder();
             sb.append(R.HTML_R_Node);
             sb.append(Bundle.HINT_DirtyStrategiesNode());           
             sb.append(R.HTML_CL_R_Node);
             sb.append("  ");
             sb.append(R.HTML_Hint);
             if(dStr != null)sb.append(dStr);
             sb.append(R.HTML_CL_Hint);
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
