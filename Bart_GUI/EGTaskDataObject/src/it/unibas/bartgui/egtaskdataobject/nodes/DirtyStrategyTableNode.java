/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import bart.model.errorgenerator.operator.valueselectors.IDirtyStrategy;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.resources.R;
import java.util.Map;
import javax.swing.Action;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import speedy.model.database.AttributeRef;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@NbBundle.Messages({
    "HINT_DirtyStrategyTableNode= table"
})
public class DirtyStrategyTableNode extends AbstractNode   {

    
    public DirtyStrategyTableNode(EGTask egt, EGTaskDataObjectDataObject dto, String dirtyStrategyTable)    {
        super(Children.create(new DirtyStrategyAttributeFactory(dirtyStrategyTable,dto, egt), true),
                new ProxyLookup(Lookups.fixed(egt,dto,dirtyStrategyTable),dto.getAbstractLookup()));
        setName(dirtyStrategyTable);
        setShortDescription(Bundle.HINT_DirtyStrategyTableNode());
        setIconBaseWithExtension(R.IMAGE_NODE_DIRTYSTRTABLE);
    }

    @Override
    public String getHtmlDisplayName() {
        String dirtyStrategyTable = getLookup().lookup(String.class);
        StringBuilder sb = new StringBuilder();
        sb.append(R.HTML_Node);
        sb.append(dirtyStrategyTable);
        sb.append(R.HTML_CL_Node);
        return sb.toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] a = {
            Actions.forID("DirtyStrategiesNode", "it.unibas.bartgui.controlegt.actions.node.DirtyStrategies.EditDirtyStrategyTableNode"),
            null,
            Actions.forID("DirtyStrategiesNode", "it.unibas.bartgui.controlegt.actions.node.DirtyStrategies.RemoveDirtyStrategyTableNode"),    
        };
        return a;
    }

    @Override
    public Action getPreferredAction() {
        return Actions.forID("DirtyStrategiesNode", "it.unibas.bartgui.controlegt.actions.node.DirtyStrategies.EditDirtyStrategyTableNode");
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
