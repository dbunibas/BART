/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions.node.DirtyStrategies;

import bart.model.EGTask;
import it.unibas.bartgui.controlegt.ControlUtil;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.view.panel.editor.dirtyStrategies.DirtyStrategyPanel;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import speedy.model.database.EmptyDB;
import speedy.model.database.IDatabase;

@ActionID(
        category = "DirtyStrategyNode",
        id = "it.unibas.bartgui.controlegt.actions.node.DirtyStrategies.EditDirtyStrategy"
)
@ActionRegistration(
        displayName = "#CTL_EditDirtyStrategy"
)
@Messages({
        "CTL_EditDirtyStrategy=New/Edit - Attribute Dirty Strategy",
        "MSG_NO_Target_DB=DataBase Target is Empty !!",
        "MSG_DB_NO_TABLE=DataBase Target not have tables"
})
public final class EditDirtyStrategy implements ActionListener {

    private final EGTaskDataObjectDataObject context;

    public EditDirtyStrategy(EGTaskDataObjectDataObject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        EGTask egt = context.getEgtask();
        if(egt == null)return;
        IDatabase db = egt.getTarget();
        if((db == null)||(db instanceof EmptyDB))   {
            DialogDisplayer.getDefault()
                    .notify(new NotifyDescriptor.Message(Bundle.MSG_NO_Target_DB()
                            , NotifyDescriptor.INFORMATION_MESSAGE));
            return;
        }
        if((db.getTableNames() == null)||(db.getTableNames().isEmpty()))   {
            DialogDisplayer.getDefault()
                    .notify(new NotifyDescriptor.Message(Bundle.MSG_DB_NO_TABLE()
                            , NotifyDescriptor.INFORMATION_MESSAGE));
            return;
        }
        
        DirtyStrategyPanel panel = new DirtyStrategyPanel();
        panel.initTableCombo(db);
        initButton(panel);
        Dialog d = ControlUtil.createDialog(panel, panel.getButtons());
        d.setTitle(Bundle.CTL_EditDirtyStrategy());
        d.setVisible(true);
    }
    
    private void initButton(DirtyStrategyPanel panel)    {
        for(Object o : panel.getButtons())   {
            ((JButton)o).addActionListener(new EditDirtyStrategyButtonListener(panel, context));
        }
    }
}
