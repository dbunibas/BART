/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import bart.model.errorgenerator.operator.valueselectors.IDirtyStrategy;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategyAttributeFactoryNotifier;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import speedy.model.database.AttributeRef;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DirtyStrategyAttributeFactory extends ChildFactory.Detachable<AttributeRef>   {

    private ChangeListener listener;
    
    private EGTaskDataObjectDataObject dto;
    private EGTask egt;
    private String dirtyStrategyTable;

    public DirtyStrategyAttributeFactory(String dirtyStrategyTable, EGTaskDataObjectDataObject dto, EGTask egt) {
        this.dto = dto;
        this.egt = egt;
        this.dirtyStrategyTable = dirtyStrategyTable;
    }
    
    
    @Override
    protected boolean createKeys(List<AttributeRef> list) {
        if(egt.getConfiguration().getDirtyStrategiesMap()== null)return true;
        Iterator<AttributeRef> it = egt.getConfiguration().getDirtyStrategiesMap().keySet().iterator();
        while(it.hasNext())   {
            AttributeRef tmp = it.next();
            if(tmp.getTableName().equals(dirtyStrategyTable))   {
                list.add(tmp);
            }
        }
        return true;
    }
    
    
    @Override
    protected Node createNodeForKey(AttributeRef key) {
        return new DirtyStrategyAttributeNode(egt, dto, key);
    }     

    @Override
    protected void removeNotify() {
        DirtyStrategyAttributeFactoryNotifier.removeChangeListener(listener);
    }

    @Override
    protected void addNotify() {
        DirtyStrategyAttributeFactoryNotifier.addChangeListener(listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                refresh(true);
            }
        });
    }   
}
