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
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DirtyStrategyAttributeFactory extends ChildFactory.Detachable<String>   {

    private ChangeListener listener;
    
    private Map<String,IDirtyStrategy> map;
    private EGTaskDataObjectDataObject dto;
    private EGTask egt;
    private String dirtyStrategyTable;

    public DirtyStrategyAttributeFactory(String dirtyStrategyTable, Map<String, IDirtyStrategy> map, EGTaskDataObjectDataObject dto, EGTask egt) {
        this.map = map;
        this.dto = dto;
        this.egt = egt;
        this.dirtyStrategyTable = dirtyStrategyTable;
    }
    
    
    @Override
    protected boolean createKeys(List<String> list) {
        list.addAll(map.keySet());
        return true;
    }
    
    
    @Override
    protected Node createNodeForKey(String key) {
        return new DirtyStrategyAttributeNode(egt, dto, dirtyStrategyTable, key, map.get(key));
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
