package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.resources.R;
import javax.swing.Action;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class OutlierErrorAttribute extends AbstractNode  {

    private String attribute;
    private String tableName;
    
    public OutlierErrorAttribute(EGTask egt,EGTaskDataObjectDataObject dto,String tableName,String att) {
        super(Children.LEAF,
                new ProxyLookup(Lookups.fixed(egt,dto),dto.getAbstractLookup()));
        this.attribute=att;
        this.tableName=tableName;
        setName(NodeResource.NODE_OutlierErrorAttribute);
        setShortDescription(Bundle.HINT_RandomErrorAttribute());
        setIconBaseWithExtension(R.IMAGE_AUTORITATIVE); 
    }

    @Override
    public String getHtmlDisplayName() {
        EGTask task = getLookup().lookup (EGTask.class); 
        if (task != null) { 
             return "<font color='#000000'>"+attribute+"<i>  ("+
                     task.getConfiguration().getOutlierErrorConfiguration()
                                    .getPercentageToDirty(tableName, attribute)+"%)</i></font>"+
                     "<font color='#888888'><i> Detectable "+"</i></font>"+
                     "<font color='#006600'><i>"+task.getConfiguration().getOutlierErrorConfiguration()
                                    .isDetectable(tableName, attribute)+"</i></font>";
        }else{
            return null;
        }                    
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
