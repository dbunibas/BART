/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions.node.DirtyStrategies;

import bart.model.EGTask;
import bart.model.errorgenerator.operator.valueselectors.IDirtyStrategy;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategiesFactoryNotifier;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategyAttributeFactoryNotifier;
import it.unibas.bartgui.egtaskdataobject.notifier.DirtyStrategyAttributeNodeNotifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import speedy.model.database.AttributeRef;

@ActionID(
        category = "DirtyStrategiesNode",
        id = "it.unibas.bartgui.controlegt.actions.node.DirtyStrategies.RemoveDirtyStrategyTableNode"
)
@ActionRegistration(
        displayName = "#CTL_RemoveDirtyStrategyTableNode"
)
@Messages("CTL_RemoveDirtyStrategyTableNode=Remove")
public final class RemoveDirtyStrategyTableNode implements ActionListener {

    private final String context;

    public RemoveDirtyStrategyTableNode(String context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        EGTaskDataObjectDataObject dto = Utilities.actionsGlobalContext().lookup(EGTaskDataObjectDataObject.class);
        if(dto == null )return;
        EGTask egt = dto.getEgtask();
        if(egt == null)return;
        if(egt.getConfiguration().getDirtyStrategiesMap() == null)return;
        Map<AttributeRef,IDirtyStrategy> map = new HashMap<>(egt.getConfiguration().getDirtyStrategiesMap());
        Iterator<AttributeRef> it = map.keySet().iterator();
        boolean modified = false;
        while(it.hasNext())   {
            AttributeRef tmp = it.next();
            if(tmp.getTableName().equals(context))   {
                egt.getConfiguration().getDirtyStrategiesMap().remove(tmp);
                modified = true;
            }
        }
        if(modified)   {
            dto.setEgtModified(true);
            DirtyStrategiesFactoryNotifier.fire();
        }
    }
}
