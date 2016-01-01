/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions.node.DirtyStrategies;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategiesFactoryNotifier;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategyAttributeFactoryNotifier;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategyAttributeNodeNotifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import speedy.model.database.AttributeRef;

@ActionID(
        category = "DirtyStrategiesNode",
        id = "it.unibas.bartgui.controlegt.actions.node.DirtyStrategies.RemoveAttributeDirtyStrategy"
)
@ActionRegistration(
        displayName = "#CTL_RemoveAttributeDirtyStrategy"
)
@Messages("CTL_RemoveAttributeDirtyStrategy=Remove")
public final class RemoveAttributeDirtyStrategy implements ActionListener {

    private final AttributeRef context;

    public RemoveAttributeDirtyStrategy(AttributeRef context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        EGTaskDataObjectDataObject dto = Utilities.actionsGlobalContext().lookup(EGTaskDataObjectDataObject.class);
        if(dto == null)return;
        EGTask egt = dto.getEgtask();
        if(egt == null)return;
        egt.getConfiguration().getDirtyStrategiesMap().remove(context);
        dto.setEgtModified(true);
        DirtyStrategiesFactoryNotifier.fire();
        DirtyStrategyAttributeFactoryNotifier.fire();
    }
}
