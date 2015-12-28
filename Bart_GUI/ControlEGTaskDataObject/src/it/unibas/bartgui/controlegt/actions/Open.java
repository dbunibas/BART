/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions;

import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.OpenCookie;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "EGTaskDataObject",
        id = "it.unibas.bartgui.controlegt.actions.Open"
)
@ActionRegistration(
        displayName = "#CTL_Open"
)
@ActionReferences({
    @ActionReference(path = "Loaders/text/egtask+xml/Actions", position = 100)
})
@Messages({
    "CTL_Open=Open XML",
    "MSG_Open=If you want edit this XML file,"
            + "\n  make sure:  \n"
            + "1)Save current modifications\n"
            + "2)Edit XML file\n"
            + "3)Save and (Re)Load file (\"Double click\" on root node)"
})
public final class Open implements ActionListener {

    private final EGTaskDataObjectDataObject context;

    public Open(EGTaskDataObjectDataObject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        context.getLookup().lookup(OpenCookie.class).open();
        if(context.getEgtask() != null)   {
            DialogDisplayer.getDefault()
                .notify(new NotifyDescriptor
                        .Message(Bundle.MSG_Open(), 
                                NotifyDescriptor.INFORMATION_MESSAGE));
        }
    }
}
