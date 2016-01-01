/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import bart.model.errorgenerator.operator.valueselectors.IDirtyStrategy;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategiesFactoryNotifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import speedy.model.database.AttributeRef;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DirtyStrategiesFactory extends ChildFactory.Detachable<String>  {
    
    private ChangeListener listener;
    
    private Set<String> set = new HashSet<>();
    private EGTaskDataObjectDataObject dto;
    private EGTask egt;
    

    public DirtyStrategiesFactory(EGTaskDataObjectDataObject dto, EGTask egt) {
        this.dto = dto;
        this.egt = egt;
    } 

    @Override
    protected boolean createKeys(List<String> list) {
        set.clear();
        if(egt.getConfiguration().getDirtyStrategiesMap()== null)return true;
        Iterator<AttributeRef> it = egt.getConfiguration().getDirtyStrategiesMap().keySet().iterator();
        while(it.hasNext())   {
            AttributeRef att = it.next();
            if(!set.contains(att.getTableName().trim()) )   {
                set.add(att.getTableName().trim());
            }
        }
        list.addAll(set);
        return true;
    }
    
    @Override
    protected Node createNodeForKey(String key) {
        return new DirtyStrategyTableNode(egt, dto, key);
    }

    @Override
    protected void removeNotify() {
        DirtyStrategiesFactoryNotifier.removeChangeListener(listener);
    }

    @Override
    protected void addNotify() {
        DirtyStrategiesFactoryNotifier.addChangeListener(listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                refresh(true);
            }
        });
    }
   
}
