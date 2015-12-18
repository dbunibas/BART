/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.control.view.about;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Help",
        id = "it.unibas.bartgui.control.view.about.AboutBart"
)
@ActionRegistration(
        displayName = "#CTL_AboutBart"
)
@ActionReference(path = "Menu/Help", position = 100)
@Messages("CTL_AboutBart=About BART")
public final class AboutBart implements ActionListener {

    SwingController controller;
    SwingViewBuilder factory;
    JPanel panel;

    public AboutBart() {
        controller = new SwingController();
        factory = new SwingViewBuilder(controller);
        panel = factory.buildViewerPanel();

    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        ComponentKeyBinding.install(controller, panel);
        controller.getDocumentViewController().setAnnotationCallback(
                                    new org.icepdf.ri.common.MyAnnotationCallback(
                                                        controller.getDocumentViewController()));
        //controller.openDocument("C:/Users/Musicrizz/Desktop/TR-01-2015.pdf");
        //controller.openDocument("it/unibas/bartgui/resources/aboutbart/TR012015.pdf");
        try{           
            FileObject file = FileUtil.getConfigFile("AboutBart/AboutBart.pdf");
            if(file == null)throw new Exception();
            controller.openDocument(file.getInputStream(), "About Bart engine", file.getPath());           
        }catch(Exception ex)   {           
            StringBuffer sb = new StringBuffer();
            sb.append("Error Generation for Evaluating Data-Cleaning Algorithms \n\n");
            sb.append(" Patricia C. Arocena - University of Toronto, Canada \n");
            sb.append(" Giansalvatore Mecca - University of Basilicata, Italy \n");
            sb.append(" Boris Glavic - Illinois Inst. of Technology, US \n");
            sb.append(" Renée J. Miller - University of Toronto, Canada \n");
            sb.append(" Paolo Papotti - QCRI Doha, Qatar \n");
            sb.append(" Donatello Santoro - University of Basilicata, Italy \n");
            NotifyDescriptor nd = new NotifyDescriptor.Message(sb.toString(), NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        DialogDescriptor dd = new DialogDescriptor(panel,"About BART");
        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
    }
}
