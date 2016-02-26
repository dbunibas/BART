/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.control.view.about;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Help",
        id = "it.unibas.bartgui.control.view.about.AboutBartGUI"
)
@ActionRegistration(
        displayName = "#CTL_AboutBartGUI"
)
@ActionReference(path = "Menu/Help", position = 200)
@Messages({"CTL_AboutBartGUI=About Bart GUI",
           "MSG_AboutBartGUI= Designed & Developed by \n\n Grandinetti Giovanni \n\n grandinetti.giovanni13@gmail.com"
})
public final class AboutBartGUI implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.MSG_AboutBartGUI(), NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(nd);
    }
}
