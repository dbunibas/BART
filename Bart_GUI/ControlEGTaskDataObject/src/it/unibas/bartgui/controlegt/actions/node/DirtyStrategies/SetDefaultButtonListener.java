/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions.node.DirtyStrategies;

import bart.model.EGTask;
import bart.model.errorgenerator.operator.valueselectors.TypoAddString;
import bart.model.errorgenerator.operator.valueselectors.TypoAppendString;
import bart.model.errorgenerator.operator.valueselectors.TypoRandom;
import bart.model.errorgenerator.operator.valueselectors.TypoRemoveString;
import bart.model.errorgenerator.operator.valueselectors.TypoSwitchValue;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategiesNodeNotifier;
import it.unibas.bartgui.view.panel.editor.dirtyStrategies.defaultStrategy.DefaultDirtyStrategyPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Musicrizz
 */
public class SetDefaultButtonListener implements ActionListener   {

    private DefaultDirtyStrategyPanel panel;
    private EGTaskDataObjectDataObject dto;
    private EGTask egt;

    public SetDefaultButtonListener(DefaultDirtyStrategyPanel panel, EGTaskDataObjectDataObject dto) {
        this.panel = panel;
        this.dto = dto;
        egt = dto.getEgtask();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("OK"))   {
            if(panel.isTypoAddString())   {
                egt.getConfiguration()
                        .setDefaultDirtyStrategy(
                                new TypoAddString(
                                        panel.getChars(),
                                        Integer.parseInt(panel.getCharTOAdd())));
            }
            if(panel.isTypoAppendString())   {
                egt.getConfiguration()
                        .setDefaultDirtyStrategy(
                                new TypoAppendString(
                                        panel.getChars(),
                                        Integer.parseInt(panel.getCharTOAdd())));
            }
            if(panel.isTypoRandom())   {
                egt.getConfiguration().setDefaultDirtyStrategy(new TypoRandom());
            }
            if(panel.isTypoRemoveString())   {
                 egt.getConfiguration()
                         .setDefaultDirtyStrategy(
                                 new TypoRemoveString(Integer.parseInt(panel.getCharsToRemove())));
            }
            if(panel.isTypoSwitchValue())   {
                egt.getConfiguration()
                         .setDefaultDirtyStrategy(
                                 new TypoSwitchValue(Integer.parseInt(panel.getCharToSwitch())));
            }
            DirtyStrategiesNodeNotifier.fire();
            dto.setEgtModified(true);
        }
    }
    
    
    
}
