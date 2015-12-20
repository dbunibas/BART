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
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import speedy.model.database.AttributeRef;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DirtyStrategyAttributeNode extends AbstractNode   {

    private String attribute;
    private String dirtyStrategyTable;
    
    private ChangeListener listener;
    
    public DirtyStrategyAttributeNode(EGTask egt, EGTaskDataObjectDataObject dto, String dirtyStrategyTable, String attribute,IDirtyStrategy typo) {
        super(Children.LEAF,new ProxyLookup(Lookups.fixed(egt,dto),dto.getAbstractLookup()));
        this.attribute = attribute;
        this.dirtyStrategyTable = dirtyStrategyTable;
        setName(attribute);
        setIconBaseWithExtension(R.IMAGE_NODE_DIRTYSTRGATTRB);
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
        Iterator<AttributeRef> it = egt.getConfiguration().getDirtyStrategiesMap().keySet().iterator();
        IDirtyStrategy typo=null;
        while(it.hasNext())   {
             AttributeRef att = it.next();
             if(att.getName().equals(attribute)&&att.getTableName().equals(dirtyStrategyTable))   {
                 typo = egt.getConfiguration().getDirtyStrategy(att);
                 break;
             }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(R.HTML_Node);
        sb.append(attribute);
        sb.append(" - ");
        sb.append(R.HTML_CL_Node);
        sb.append(R.HTML_Hint);
        sb.append((typo != null) ? typo : "");
        sb.append(R.HTML_CL_Hint);
        return sb.toString();
    }
 
    @Override
    public Action[] getActions(boolean context) {
        Action[] a = {};
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
