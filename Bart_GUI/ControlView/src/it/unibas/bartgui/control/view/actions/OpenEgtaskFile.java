package it.unibas.bartgui.control.view.actions;

import it.unibas.bartgui.egtaskdataobject.api.ILoadEGTask;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import it.unibas.bartgui.resources.R;
import org.jdom.Element;
import org.openide.ErrorManager;
import speedy.persistence.xml.DAOXmlUtility;

@ActionID(
        category = "File",
        id = "it.unibas.bartgui.control.view.actions.OpenEgtaskFile"
)
@ActionRegistration(
        displayName = "#CTL_OpenEgtaskFile",
        iconBase = R.IMAGE_Open_FILE,
        popupText = "Load Task"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = -100),
    @ActionReference(path = "Toolbars/File", position = 10,separatorAfter = 20),
    @ActionReference(path = "Shortcuts", name = "D-F")
})
@Messages({
    "CTL_OpenEgtaskFile=Load Task",
    "MSG_OpenWrongFile=is not a EGTask configuration",
    "# {0} - file name",
    "MSG_WRONG_FILE=The file :  {0} \nis not a EGTask configuration"
})
public final class OpenEgtaskFile implements ActionListener {
    
    @Override
    public void actionPerformed(ActionEvent e) {
        File home = new File(System.getProperty("user.home"));
        File toLoad = new FileChooserBuilder("EGTAK_CONF_FILE")
                                        .setTitle("Load File")
                                        .setDefaultWorkingDirectory(home)
                                        .setApproveText("Load")
                                        .setFilesOnly(true)
                                        .setAcceptAllFileFilterUsed(false)
                                        .addFileFilter(new FileNameExtensionFilter("XML Configuration", "xml","XML"))
                                        .showOpenDialog();
        if(toLoad==null){
            return;
        }
        FileObject fo = FileUtil.toFileObject(toLoad);
        if(fo.isFolder()){
            DialogDisplayer.getDefault().notify(myNotify("Select xml file no a folder"));
            return;
        }
        
        if(!controlFile(fo))return;

        ILoadEGTask load = Lookup.getDefault().lookup(ILoadEGTask.class);
        load.load(fo);
    }
    
    private boolean controlFile(FileObject fo)   {
        try{
            DAOXmlUtility daoUtility = new DAOXmlUtility();
            File taskFile = FileUtil.toFile(fo);
            String fileTask = taskFile.getAbsolutePath();
            org.jdom.Document document = daoUtility.buildDOM(fileTask);
            Element rootElement = document.getRootElement();
            if((rootElement == null)|| (!rootElement.getName().equalsIgnoreCase("task")))  {
                myNotify(Bundle.MSG_WRONG_FILE(fo.getName()));
                return false;
            }
            return true;
        }catch(Exception ex)   {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ex);
            return false;
        }
    }
    
    private NotifyDescriptor myNotify(String message)   {
        return new NotifyDescriptor(message, 
             "Wrog File", 
              NotifyDescriptor.DEFAULT_OPTION, 
              NotifyDescriptor.ERROR_MESSAGE, new Object[]{NotifyDescriptor.CLOSED_OPTION}, null);
    }
}