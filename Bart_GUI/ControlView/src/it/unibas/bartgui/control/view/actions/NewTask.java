/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.control.view.actions;

import it.unibas.bartgui.egtaskdataobject.api.ILoadEGTask;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.jdom.Document;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import it.unibas.bartgui.resources.R;

@ActionID(
        category = "File",
        id = "it.unibas.bartgui.control.view.actions.NewTask"
)
@ActionRegistration(
        displayName = "#CTL_NewTask",
        iconBase = R.IMAGE_New_FILE,
        popupText = "New Task"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = -200),
    @ActionReference(path = "Toolbars/File", position = 0),
    @ActionReference(path = "Shortcuts", name = "D-N")
})
@Messages({"CTL_NewTask=New Task",
            "HINT_NEWTASK=Do you want create New Task?\n\tChoose work directory.",
            "HINT_NEWTASKFileChooser=New Task, Choose work directory"
})
public final class NewTask implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {       
        Object option = DialogDisplayer.getDefault().notify(
                new NotifyDescriptor.Confirmation(
                        Bundle.HINT_NEWTASK(), 
                        Bundle.CTL_NewTask(),
                        NotifyDescriptor.YES_NO_OPTION));
        if(option.toString().equals("1"))return;
        File home = new File(System.getProperty("user.home"));
        File dir = new FileChooserBuilder("NEW_TASK")
                                        .setTitle(Bundle.HINT_NEWTASKFileChooser())
                                        .setDefaultWorkingDirectory(home)
                                        .setApproveText("Choose")
                                        .setDirectoriesOnly(true)                                        
                                        .showOpenDialog();
        if(dir == null)return;  
        StringBuilder path = new StringBuilder(dir.getAbsolutePath());
        path.append("/New_Empty_Task.xml");
        File file = null;
        FileObject newTask = null;
        BufferedWriter out = null;
        FileWriter fw = null;       
        try{
            file = new File(path.toString());
            Element root = new Element("task");
            Document doc = new Document(root);
            fw = new FileWriter(file);
            out = new BufferedWriter(fw);
            XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
            xmlOut.output(doc,out);
        }catch(Exception ex)   {
            ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
        }finally{
            try{
                if(fw != null)fw.close();
                if(out != null)out.close();
            }catch(Exception ex)   {
                
            }
        }
        if(file != null)   {
            newTask = FileUtil.toFileObject(file);
            ILoadEGTask load = Lookup.getDefault().lookup(ILoadEGTask.class);
            load.load(newTask);
        }
    
    }
}
