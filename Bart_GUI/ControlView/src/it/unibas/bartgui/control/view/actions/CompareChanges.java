package it.unibas.bartgui.control.view.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import it.unibas.bartgui.resources.R;
import it.unibas.bartgui.view.panel.CompareChangesDialog;
import javax.swing.JFrame;
import org.openide.windows.WindowManager;

@ActionID(
        category = "Run",
        id = "it.unibas.bartgui.control.view.actions.CompareChanges"
)
@ActionRegistration(
        displayName = "#CTL_CompareChanges",
        iconBase = R.IMAGE_COMPARE,
        popupText = "Compare Changes"
)
@ActionReferences({
    @ActionReference(path = "Menu/Run", position = 2),
    @ActionReference(path = "Toolbars/Run", position = 10, separatorAfter = 20),})
@Messages({
    "CTL_CompareChanges=Compare Changes",})
public final class CompareChanges implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame mainFrame = (JFrame) WindowManager.getDefault().getMainWindow();
        CompareChangesDialog dialog = new CompareChangesDialog(mainFrame);
        dialog.showDialog();
    }
}
