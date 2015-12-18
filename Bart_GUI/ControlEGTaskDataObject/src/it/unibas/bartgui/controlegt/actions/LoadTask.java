/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions;

import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.api.ILoadEGTask;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "controlEGTask",
        id = "it.unibas.bartgui.controlegt.actions.LoadTask"
)
@ActionRegistration(
        displayName = "#CTL_LoadTask"
)
@ActionReferences({
    @ActionReference(path = "Loaders/text/egtask+xml/Actions", position = 50, separatorAfter = 60),
    @ActionReference(path = "Shortcuts", name = "D-L")
})
@Messages("CTL_LoadTask=Load Task")
public final class LoadTask implements ActionListener {

    private final EGTaskDataObjectDataObject context;

    public LoadTask(EGTaskDataObjectDataObject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        ILoadEGTask load = Lookup.getDefault().lookup(ILoadEGTask.class);
        load.load(context.getPrimaryFile());
    }
}
