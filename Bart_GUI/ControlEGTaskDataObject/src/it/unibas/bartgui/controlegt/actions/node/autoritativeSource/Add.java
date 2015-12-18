/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions.node.autoritativeSource;

import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.view.panel.editor.autoritativesrc.AutoritativeSrcPanel;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.WeakListeners;
import speedy.model.database.IDatabase;

@ActionID(
        category = "AutoritativeSourceNode",
        id = "it.unibas.bartgui.controlegt.actions.node.autoritativeSource.Add"
)
@ActionRegistration(
        displayName = "#CTL_Add"
)
@Messages({"CTL_Add=Add Autoritative Source",
           "MSG_DBSourceNull=Database Source is NULL",
           "MSG_DBSourceNotTables=Database Source not have tables",
           "MSG_AutoritativePanelTiTLE=Select Autoritative Source"
})
public final class Add implements ActionListener {

    private final EGTaskDataObjectDataObject dto;

    public Add(EGTaskDataObjectDataObject context) {
        this.dto = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        IDatabase db = dto.getEgtask().getSource();
        if(db == null)   {
            visualizeMessage(Bundle.MSG_DBSourceNull());
            return;
        }
        List<String> tables = db.getTableNames();
        if((tables == null) || (tables.isEmpty()))   {
            visualizeMessage(Bundle.MSG_DBSourceNotTables());
            return;
        }
        AutoritativeSrcPanel panel = new AutoritativeSrcPanel();
        panel.setItem(tables);
        initActionListener(panel.getButtons(),dto, panel);
        Dialog d = createDialog(panel, panel.getButtons());
        d.setVisible(true);
    }
    
    private void visualizeMessage(String message)   {
        DialogDisplayer.getDefault()
                .notify(new NotifyDescriptor
                        .Message(message, 
                                NotifyDescriptor.INFORMATION_MESSAGE));
    }
    
    private void initActionListener(JButton[] buttons,EGTaskDataObjectDataObject dto, AutoritativeSrcPanel panel)   {
        for(JButton b : buttons)   {
            b.addActionListener(
                    WeakListeners
                    .create(
                            ActionListener.class, 
                            new AuthoritativeSrcButtonListener(dto,panel),
                            this));
        }
    }
    
    
    private Dialog createDialog(JPanel inner, Object[] options)   {
        DialogDescriptor dsc = new DialogDescriptor(inner, 
                                null, 
                                true, 
                                options, 
                                null,
                                DialogDescriptor.DEFAULT_ALIGN, 
                                HelpCtx.DEFAULT_HELP, 
                                null);
        Dialog d = DialogDisplayer.getDefault().createDialog(dsc);
        d.setTitle(Bundle.MSG_AutoritativePanelTiTLE());
        return d;
    }
}
