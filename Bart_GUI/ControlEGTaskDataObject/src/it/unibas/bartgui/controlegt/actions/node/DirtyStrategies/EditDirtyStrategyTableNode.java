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
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import speedy.model.database.EmptyDB;
import speedy.model.database.IDatabase;

@ActionID(
        category = "DirtyStrategiesNode",
        id = "it.unibas.bartgui.controlegt.actions.node.DirtyStrategies.EditDirtyStrategyTableNode"
)
@ActionRegistration(
        displayName = "#CTL_EditDirtyStrategyTableNode",
        popupText = "#MSG_EditDirtyStrategyTableNode_PopUp"
)
@Messages({
    "CTL_EditDirtyStrategyTableNode=Edit",
    "MSG_EditDirtyStrategyTableNode_PopUp=Edit"        
})
public final class EditDirtyStrategyTableNode implements ActionListener {

    private final String context;

    public EditDirtyStrategyTableNode(String context) {
        this.context = context;
        
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        EGTaskDataObjectDataObject dto = Utilities.actionsGlobalContext().lookup(EGTaskDataObjectDataObject.class);
        EGTask egt = dto.getEgtask();
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
        panel.initTableCombo(context,db);
        initButton(panel,dto);
        Dialog d = ControlUtil.createDialog(panel, panel.getButtons());
        d.setTitle(Bundle.CTL_EditDirtyStrategyTableNode());
        d.setVisible(true);
    }
    
    private void initButton(DirtyStrategyPanel panel,EGTaskDataObjectDataObject dto)    {
        for(Object o : panel.getButtons())   {
            ((JButton)o).addActionListener(new EditDirtyStrategyButtonListener(panel, dto));
        }
    }
}
