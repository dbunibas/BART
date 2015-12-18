/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions.node.DependenciesNode;

import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.view.ViewResource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(
        category = "DependenciesNode",
        id = "it.unibas.bartgui.controlegt.actions.node.DependenciesNode.Edit"
)
@ActionRegistration(
        displayName = "#CTL_Edit"
)
@Messages({"CTL_Edit=Edit",
          "TITLE_Dialog=Edit Dependencies"})
public final class Edit implements ActionListener {

    private final EGTaskDataObjectDataObject context;
       

    public Edit(EGTaskDataObjectDataObject context) {
        this.context = context;
              
    }

    @Override
    public void actionPerformed(ActionEvent ev) {       
        TopComponent tc = WindowManager.getDefault().findTopComponent(ViewResource.TOP_ID_DependenciesEditorTopComponent);
        if(tc != null)   {
            if(tc.isOpened()) {
                tc.requestActive();
                return;
            }
            tc.open();
            tc.requestActive();
        }
    }
    

}
