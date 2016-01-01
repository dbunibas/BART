/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import bart.model.errorgenerator.operator.valueselectors.IDirtyStrategy;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategyAttributeNodeNotifier;
import it.unibas.bartgui.resources.R;
import it.unibas.centrallookup.CentralLookup;
import java.util.Iterator;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    "HINT_DirtyStrategyAttributeNode= attribute"
})
public class DirtyStrategyAttributeNode extends AbstractNode   {
 
    private ChangeListener listener;
    
    public DirtyStrategyAttributeNode(EGTask egt, EGTaskDataObjectDataObject dto, AttributeRef attribute) {
        super(Children.LEAF,new ProxyLookup(Lookups.fixed(attribute,egt,dto),dto.getAbstractLookup()));
        setName(attribute.getName().trim());
        setIconBaseWithExtension(R.IMAGE_NODE_DIRTYSTRGATTRB);
        setShortDescription(Bundle.HINT_DirtyStrategyAttributeNode());
        DirtyStrategyAttributeNodeNotifier.addChangeListener(listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                fireDisplayNameChange(null, "");
            }
        });
    }

    @Override
    public String getHtmlDisplayName() {
        EGTask egt = getLookup().lookup(EGTask.class);        
        AttributeRef attribute = getLookup().lookup(AttributeRef.class);
        IDirtyStrategy typo = egt.getConfiguration().getDirtyStrategy(attribute);
        StringBuilder sb = new StringBuilder();
        sb.append(R.HTML_Node);
        sb.append(attribute.getName());
        sb.append(" - ");
        sb.append(R.HTML_CL_Node);
        sb.append(R.HTML_Hint);
        sb.append((typo != null) ? typo : "");
        sb.append(R.HTML_CL_Hint);
        return sb.toString();
    }
 
    @Override
    public Action[] getActions(boolean context) {
        Action[] a = {
            Actions.forID("DirtyStrategiesNode", "it.unibas.bartgui.controlegt.actions.node.DirtyStrategies.RemoveAttributeDirtyStrategy"),
        };
        return a;
    }

    @Override
    public Action getPreferredAction() {
        return null;
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
    
    @Override
    protected void finalize() throws Throwable {
        DirtyStrategyAttributeNodeNotifier.removeChangeListener(listener);
        super.finalize();
    } 
}
