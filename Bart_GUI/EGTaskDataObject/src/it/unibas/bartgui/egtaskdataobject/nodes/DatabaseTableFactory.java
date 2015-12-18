/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.egtaskdataobject.nodes;

import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.notifier.DatabaseTableNotifier;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import speedy.model.database.EmptyDB;
import speedy.model.database.IDatabase;

/**
 *
 * @author Musicrizz
 */
public class DatabaseTableFactory extends ChildFactory.Detachable<String>  {
    
    private EGTaskDataObjectDataObject dto;
    private String dsname;
    private ChangeListener listener;

    public DatabaseTableFactory(EGTaskDataObjectDataObject dto,String dsname) {
        this.dto = dto;
        this.dsname = dsname;
    }

    @Override
    protected boolean createKeys(List<String> list) {
        IDatabase database = null;
        if(dsname.equals("Source"))database = dto.getEgtask().getSource();
        if(dsname.equals("Target"))database = dto.getEgtask().getTarget();
        if(dsname.equals("Dirty"))database = dto.getEgtask().getDirtyTarget();
        for(String s : database.getTableNames())   {
            list.add(s);
        }
        if(dsname.equals("Source") && (!(database instanceof EmptyDB)))   {
            list.add(NodeResource.NODE_AutoritativeSourcesNode);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        if(key.equals(NodeResource.NODE_AutoritativeSourcesNode))   {
            return new AuthoritativeSourcesNode(dto.getEgtask(), dto);
        }else{
            return new DBTableNode(dto, key, dsname);
        }
    }
   
        @Override
    protected void removeNotify() {
        DatabaseTableNotifier.removeChangeListener(listener);
    }

    @Override
    protected void addNotify() {
        DatabaseTableNotifier.addChangeListener(listener = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                refresh(true);
            }
        });
    }
    
    
    
}
