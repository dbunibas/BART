/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions.node.randomError;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "RandomErrorsNode",
        id = "it.unibas.bartgui.controlegt.actions.node.randomError.EditToDO"
)
@ActionRegistration(
        displayName = "#CTL_EditToDO"
)
@Messages("CTL_EditToDO=Edit")
public final class EditToDO implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        DialogDisplayer.getDefault()
                .notify(
                        new NotifyDescriptor
                                .Message("TO DO", 
                                        NotifyDescriptor.INFORMATION_MESSAGE));
    }
}
