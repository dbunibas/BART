package it.unibas.bartgui.controlegt.actions.node.DependenciesNode;

import bart.model.EGTask;
import bart.model.dependency.Dependency;
import bart.model.errorgenerator.operator.GenerateVioGenQueries;
import bart.persistence.parser.operators.ParseDependencies;
import it.unibas.bartgui.controlegt.OutputWindow;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DependenciesFactoryNotiy;
import it.unibas.bartgui.egtaskdataobject.notifier.DependenciesNodeNotify;
import it.unibas.bartgui.egtaskdataobject.notifier.VioGenQueryFactoryNotify;
import it.unibas.bartgui.resources.StatusBar;
import it.unibas.bartgui.view.panel.run.BusyDialog;
import it.unibas.centrallookup.CentralLookup;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextPane;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import speedy.model.database.EmptyDB;

@SuppressWarnings("deprecation")
@ActionID(
        category = "DependenciesNode",
        id = "it.unibas.bartgui.controlegt.actions.node.DependenciesNode.ReloadDependencies"
)
@ActionRegistration(
        displayName = "#CTL_ReloadDependencies",popupText = "#HINT_ReloadDependencies"
)
@Messages({"CTL_ReloadDependencies=Save Dependencies",
           "HINT_ReloadDependencies=Load Dependencies and VioGenQueries generated",
          "MSG_ReloadDependenciesException=Error Parse dependencies",
          "MSG_ReloadDependenciesExecuted=Dependencies Loaded \n   and  \nGenerate VioGenQueries",
          "MSG_ReloadDependenciesNoDBTarget=Database Target is EMPTY"})
public final class ReloadDependencies implements ActionListener {

    private final JTextPane textPanel;
    
    private EGTaskDataObjectDataObject dto;
    private final EGTask egtask;
    private boolean esito = false;
    private final ParseDependencies parse;
    private final GenerateVioGenQueries vioGenQueriesGenerator;
    
    public ReloadDependencies(JTextPane textPanel) {
        this.textPanel = textPanel;
        dto = CentralLookup.getDefLookup().lookup(EGTaskDataObjectDataObject.class);
        if(dto!=null)   {
            egtask = dto.getEgtask();
        }else{
            egtask = null;
        }
        parse = new ParseDependencies();
        vioGenQueriesGenerator = new GenerateVioGenQueries();
    }

    @Override// closeDependencyViewTopComponent
    public void actionPerformed(ActionEvent ev) {
        if(dto == null || dto.getEgtask() == null)return;       
        if(textPanel.getText().isEmpty())return;
        if((egtask.getTarget() == null) || (egtask.getTarget() instanceof EmptyDB))   {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                        Bundle.MSG_ReloadDependenciesNoDBTarget(), 
                        NotifyDescriptor.INFORMATION_MESSAGE));
            return;
        }   
        final InputOutput io = IOProvider.getDefault().getIO(dto.getPrimaryFile().getName(), false);
        io.select();
        OutputWindow.openOutputWindowStream(io.getOut(), io.getErr());
        final Dialog d = BusyDialog.getBusyDialog();
        RequestProcessor.Task T = RequestProcessor.getDefault().post(new reloadDependeciesRunnable());
        T.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(Task task) {
//                d.setVisible(false);
                if(esito)   {     
                    dto.setEgtModified(true);
                    StatusBar.setStatus(Bundle.MSG_ReloadDependenciesExecuted(), 10,5000);
                    System.out.println(Bundle.MSG_ReloadDependenciesExecuted());
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                            Bundle.MSG_ReloadDependenciesExecuted(), 
                            NotifyDescriptor.INFORMATION_MESSAGE));
                }else{
                    System.err.println(Bundle.MSG_ReloadDependenciesException());
                    StatusBar.setStatus(Bundle.MSG_ReloadDependenciesExecuted(), 10,5000);
                }
                OutputWindow.closeOutputWindowStream(io.getOut(), io.getErr());
            }
        });
//        d.setVisible(true);
    }
    
    private class reloadDependeciesRunnable implements Runnable   {

        @Override
        public void run() {
            ProgressHandle progr = ProgressHandleFactory.createHandle("Load Dependencies ....");
            progr.start();
            List<Dependency> ListDCsTmp = egtask.getDCs();
            try{
                egtask.setDCs(new ArrayList<Dependency>());
                DependenciesNodeNotify.fire();
                DependenciesFactoryNotiy.fire();
                parse.generateDependencies(textPanel.getText().trim(), egtask);
                for (Dependency dc : egtask.getDCs()) {
                    dc.setVioGenQueries(vioGenQueriesGenerator
                            .generateVioGenQueries(dc, egtask));
                }
                DependenciesNodeNotify.fire();
                DependenciesFactoryNotiy.fire();
                VioGenQueryFactoryNotify.fire();
               
                dto.setDependencies(textPanel.getText().trim());
                esito = true;
            }catch(Exception ex)   {
                esito = false;
                egtask.setDCs(ListDCsTmp);
                DependenciesNodeNotify.fire();
                DependenciesFactoryNotiy.fire();
                VioGenQueryFactoryNotify.fire();    
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
            }finally{
                progr.finish();
            }
        }    
    }
}