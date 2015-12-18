package it.unibas.bartgui.controlegt.actions.node.autoritativeSource;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.AutoritSrcsNodeFactNotifier;
import it.unibas.bartgui.view.panel.editor.autoritativesrc.AutoritativeSrcPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class AuthoritativeSrcButtonListener implements ActionListener   {

    EGTaskDataObjectDataObject dto;
    AutoritativeSrcPanel panel;

    public AuthoritativeSrcButtonListener(EGTaskDataObjectDataObject dto, AutoritativeSrcPanel panel) {
        this.dto = dto;
        this.panel = panel;
    }
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        EGTask task = dto.getEgtask();
        if(e.getActionCommand().equalsIgnoreCase("OK"))   {
            String src = (String)panel.getSelectedItem();
            if(src != null)   {
                if(task.getAuthoritativeSources().contains(src))return;
                task.addAuthoritativeSource(src);
                dto.setEgtModified(true);
                AutoritSrcsNodeFactNotifier.fire();
            }
        }
    }
}
