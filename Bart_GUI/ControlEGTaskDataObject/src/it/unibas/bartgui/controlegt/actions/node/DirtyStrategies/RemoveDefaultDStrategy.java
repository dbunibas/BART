/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions.node.DirtyStrategies;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategiesNodeNotifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "DirtyStrategiesNode",
        id = "it.unibas.bartgui.controlegt.actions.node.DirtyStrategies.RemoveDefaultDStrategy"
)
@ActionRegistration(
        displayName = "#CTL_RemoveDefaultDStrategy"
)
@Messages("CTL_RemoveDefaultDStrategy=Remove Default Strategy")
public final class RemoveDefaultDStrategy implements ActionListener {

    private final EGTaskDataObjectDataObject context;

    public RemoveDefaultDStrategy(EGTaskDataObjectDataObject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        EGTask egt = context.getEgtask();
        if(egt == null)return;
        egt.getConfiguration().setDefaultDirtyStrategy(null);
        DirtyStrategiesNodeNotifier.fire();
        context.setEgtModified(true);
    }
}
