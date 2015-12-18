/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions.node.DependenciesNode;

import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.centrallookup.CentralLookup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextPane;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "DependenciesNode",
        id = "it.unibas.bartgui.controlegt.actions.node.DependenciesNode.Reset"
)
@ActionRegistration(
        displayName = "#CTL_Reset"
)
@Messages("CTL_Reset=Reset")
public final class Reset implements ActionListener {

    private EGTaskDataObjectDataObject dto;
    private JTextPane textpane;

    public Reset(JTextPane textpane) {
        this.textpane = textpane;
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        dto = CentralLookup.getDefLookup().lookup(EGTaskDataObjectDataObject.class);
        if(textpane == null || dto == null)return;
        String dcs = dto.getDependencies();
        if(dcs == null)   {
            textpane.setText("");
        }else{
            textpane.setText(dcs.trim());
        }
    }
}
