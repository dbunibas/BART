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
import it.unibas.bartgui.controlegt.ControlUtil;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.view.panel.editor.dirtyStrategies.defaultStrategy.DefaultDirtyStrategyPanel;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "DirtyStrategiesNode",
        id = "it.unibas.bartgui.controlegt.actions.node.DirtyStrategies.EditDefaultStrategy"
)
@ActionRegistration(
        displayName = "#CTL_EditDefaultStrategy"
)
@Messages({
    "CTL_EditDefaultStrategy=Edit Default Strategy",
    "TITLE_DIALOG=Edit Default Strategy"
})
public final class EditDefaultStrategy implements ActionListener {

    private final EGTaskDataObjectDataObject context;

    public EditDefaultStrategy(EGTaskDataObjectDataObject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        EGTask egt = context.getEgtask();
        if(egt == null)return;
        DefaultDirtyStrategyPanel panel = new DefaultDirtyStrategyPanel();
        initButton(panel);
        IDirtyStrategy dirty = egt.getConfiguration().getDefaultDirtyStrategy();
        if(dirty != null)   {
            if(dirty instanceof TypoAddString)   {
                TypoAddString tmp = (TypoAddString)dirty;
                panel.setTypoAddString();
                panel.setChars(tmp.getChars());
                panel.setCharTOAdd(tmp.getCharsToAdd());
            }
            if(dirty instanceof TypoAppendString)   {
                TypoAppendString tmp = (TypoAppendString)dirty;
                panel.setTypoAppendString();
                panel.setChars(tmp.getChars());
                panel.setCharTOAdd(tmp.getCharsToAdd());
            }
            if(dirty instanceof TypoRandom)   {
                panel.setTypoRandom();
            }
            if(dirty instanceof TypoRemoveString)   {
                TypoRemoveString tmp = (TypoRemoveString)dirty;
                panel.setTypoRemoveString();
                panel.setCharsToRemove(tmp.getCharsToRemove());
            }
            if(dirty instanceof TypoSwitchValue)   {
                TypoSwitchValue tmp = (TypoSwitchValue)dirty;
                panel.setTypoSwitchValue();
                panel.setCharToSwitch(tmp.getCharsToSwitch());
            }          
        }
        Dialog d = ControlUtil.createDialog(panel, panel.getButtons());
        d.setTitle(Bundle.TITLE_DIALOG());
        d.setVisible(true);
    }
    
    private void initButton(DefaultDirtyStrategyPanel panel)   {
        for(Object o : panel.getButtons())   {
            ((JButton)o).addActionListener(new SetDefaultButtonListener(panel, context));
        }
    }
}
