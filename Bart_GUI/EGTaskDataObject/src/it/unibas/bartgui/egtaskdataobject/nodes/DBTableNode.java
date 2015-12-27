/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.egtaskdataobject.nodes;

import it.unibas.bartgui.egtaskdataobject.util.TableNameContext;
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
 * @author Musicrizz
 */
public class DBTableNode extends AbstractNode   {

    private String key;
    
    public DBTableNode(EGTaskDataObjectDataObject dto, String key, String dsname) {
        super(Children.LEAF,new ProxyLookup(
                Lookups.fixed(dto.getEgtask(),dto,dsname,new TableNameContext(key)),
                dto.getAbstractLookup()));
        this.key = key;
        setIconBaseWithExtension(R.IMAGE_DB_TABLE_GO);
        setName(key);
    } 
    
    @Override
    public String getHtmlDisplayName() {
        StringBuilder sb = new StringBuilder(R.HTML_Node);
        sb.append(key);
        sb.append(R.HTML_CL_Node);
        return sb.toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] a = {
           Actions.forID("DBNode", "it.unibas.bartgui.controlegt.actions.node.dbNode.Open"), 
        };
        return a;
    }

    @Override
    public Action getPreferredAction() {
        return Actions.forID("DBNode", "it.unibas.bartgui.controlegt.actions.node.dbNode.Open"); 
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
