package it.unibas.bartgui.controlegt.actions.node.DependenciesNode;

import bart.model.dependency.Dependency;
import it.unibas.bartgui.view.ViewResource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(
        category = "DependencyNode",
        id = "it.unibas.bartgui.controlegt.actions.node.DependenciesNode.Open"
)
@ActionRegistration(
        displayName = "#CTL_Open"
)
@Messages("CTL_Open=Open")
public final class Open implements ActionListener {

    private final Dependency context;

    public Open(Dependency context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        TopComponent tc = WindowManager.getDefault().findTopComponent(ViewResource.TOP_ID_DependencyViewTopComponent);
        if(tc != null) {
            if(tc.isOpened())  {
                tc.requestActive();
            }else{
                tc.open();
                tc.requestActive();
            }
        }   
          
    }
}