/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions.node.DirtyStrategies;

import bart.model.EGTask;
import bart.model.errorgenerator.operator.valueselectors.IDirtyStrategy;
import bart.model.errorgenerator.operator.valueselectors.TypoAddString;
import bart.model.errorgenerator.operator.valueselectors.TypoAppendString;
import bart.model.errorgenerator.operator.valueselectors.TypoRandom;
import bart.model.errorgenerator.operator.valueselectors.TypoRemoveString;
import bart.model.errorgenerator.operator.valueselectors.TypoSwitchValue;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategiesFactoryNotifier;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategyAttributeFactoryNotifier;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategyAttributeNodeNotifier;
import it.unibas.bartgui.view.panel.editor.dirtyStrategies.DirtyStrategyPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import speedy.model.database.AttributeRef;

/**
 *
 * @author Musicrizz
 */
public class EditDirtyStrategyButtonListener implements ActionListener   {

    private DirtyStrategyPanel panel;
    private EGTaskDataObjectDataObject dto;
    private EGTask egt;

    public EditDirtyStrategyButtonListener(DirtyStrategyPanel panel, EGTaskDataObjectDataObject dto) {
        this.panel = panel;
        this.dto = dto;
        egt = dto.getEgtask();
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("OK"))   {
            IDirtyStrategy dirty = null;
            String tableName = panel.getTable();
            String attributeName = panel.getAttibute();
            if(panel.isTypoAddString())   {
                dirty = new TypoAddString(panel.getChars(),
                                          Integer.parseInt(panel.getCharTOAdd()));
            }
            if(panel.isTypoAppendString())   {
                dirty = new TypoAppendString(panel.getChars(),
                                             Integer.parseInt(panel.getCharTOAdd()));
            }
            if(panel.isTypoRandom())   {
                dirty = new TypoRandom();
            }
            if(panel.isTypoRemoveString())   {
                dirty = new TypoRemoveString(Integer.parseInt(panel.getCharsToRemove()));
            }
            if(panel.isTypoSwitchValue())   {
                dirty = new TypoSwitchValue(Integer.parseInt(panel.getCharToSwitch()));
            }         
            if((dirty==null)||(tableName==null)||(attributeName==null))return;
            AttributeRef attRef = new AttributeRef(tableName, attributeName);
            egt.getConfiguration().addDirtyStrategyForAttribute(attRef, dirty);
            dto.setEgtModified(true);
            DirtyStrategiesFactoryNotifier.fire();
            DirtyStrategyAttributeFactoryNotifier.fire();
            DirtyStrategyAttributeNodeNotifier.fire();
        }
    }
    
    
    
}
