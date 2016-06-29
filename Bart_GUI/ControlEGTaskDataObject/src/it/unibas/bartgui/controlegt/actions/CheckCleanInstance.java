package it.unibas.bartgui.controlegt.actions;

import bart.model.EGTask;

import bart.model.detection.operator.DetectViolations;
import it.unibas.bartgui.controlegt.OutputWindow;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.resources.StatusBar;
import it.unibas.bartgui.view.panel.run.BusyDialog;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import speedy.model.database.EmptyDB;

@SuppressWarnings("deprecation")
@ActionID(
        category = "File",
        id = "it.unibas.bartgui.controlegt.actions.CheckCleanInstance"
)
@ActionRegistration(
        displayName = "#CTL_CheckCleanInstance"
)@ActionReferences({
    @ActionReference(path = "Loaders/text/egtask+xml/Actions", position = 1600, separatorAfter = 1700),
    @ActionReference(path = "Shortcuts", name = "D-Q")
})
@Messages({"CTL_CheckCleanInstance=Check Clean Instance",
           "MSG_CheckCleanInstanceDCEmpty=List of Dependencies is empty !!",
           "MSG_CheckCleanInstanceSourceEmpty=Source DB is Empty !!",
           "MSG_CheckCleanInstanceTargetEmpty=Target DB is Empty !!",
           "MSG_CheckCleanInstanceClean=Instance is Clean",
           "# {0} - error",
           "MSG_CheckCleanInstanceViolated={0}",
           "MSG_STATUS_Clean=Instance is Clean",
           "MSG_STATUS_Violated=Target violates dependencies"
            
})
public final class CheckCleanInstance implements ActionListener {

    private static Logger log = Logger.getLogger(CheckCleanInstance.class.getName());
    
    private final EGTask task;
    private final DetectViolations cleanInstanceChecker;
    EGTaskDataObjectDataObject dto;
    final StringBuilder message = new StringBuilder();
    boolean esito = false;

    public CheckCleanInstance(EGTask task) {
        this.task = task;
        cleanInstanceChecker = new DetectViolations();
        dto = Utilities.actionsGlobalContext().lookup(EGTaskDataObjectDataObject.class);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final Dialog d = BusyDialog.getBusyDialog();
        
        RequestProcessor.Task T = RequestProcessor.getDefault().post(new checkCleanRunnable());
        T.addTaskListener(new TaskListener() {

            @Override
            public void taskFinished(Task task) {
//                d.setVisible(false);
                if(esito)   {                
                    StatusBar.setStatus(Bundle.MSG_STATUS_Clean(), 10,5000);
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                        Bundle.MSG_CheckCleanInstanceClean(), 
                        NotifyDescriptor.INFORMATION_MESSAGE));
                }else{
                    StatusBar.setStatus(Bundle.MSG_STATUS_Violated(), 10, 5000);
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                            message.toString(), 
                            NotifyDescriptor.WARNING_MESSAGE));
                }
            }
        });
//        d.setVisible(true);
    }
    
    private class checkCleanRunnable implements Runnable   {    
        @Override
        public void run() {    
            ProgressHandle progr = null;
            InputOutput io = null;
            try{    
                progr = ProgressHandleFactory.createHandle("Check Clean Instance ....");
                io = IOProvider.getDefault().getIO(dto.getPrimaryFile().getName(), false);
                io.select();
                OutputWindow.openOutputWindowStream(io.getOut(), io.getErr());
                progr.start();
                if(task == null)return;
                if (task.getDCs().isEmpty()) {
                    message.append(Bundle.MSG_CheckCleanInstanceDCEmpty());
                    esito = false;
                    return;
                }
                if((task.getTarget() == null) || (task.getTarget() instanceof EmptyDB))   {
                    message.append(Bundle.MSG_CheckCleanInstanceTargetEmpty());
                    esito = false;
                    return;
                }                
                cleanInstanceChecker.check(task.getDCs(), task.getSource(), task.getTarget(), task);
                esito = true;
            }catch(Exception ex)   {
                System.err.println(""+ex.getLocalizedMessage());
                String tmp = ex.toString().replaceAll("bart.exceptions.ErrorGeneratorException:", "");
                message.append(Bundle.MSG_CheckCleanInstanceViolated(tmp));
                esito = false;
            }finally{
                OutputWindow.closeOutputWindowStream(io.getOut(), io.getErr());
                progr.finish();
            }
        }
    }
}
