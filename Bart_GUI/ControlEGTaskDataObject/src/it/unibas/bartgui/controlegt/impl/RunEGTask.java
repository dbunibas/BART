package it.unibas.bartgui.controlegt.impl;

import bart.exceptions.ErrorGeneratorException;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.operator.APrioriGenerator;
import bart.utility.ErrorGeneratorStats;
import it.unibas.bartgui.controlegt.OutputWindow;
import it.unibas.bartgui.egtaskdataobject.api.IRunEGTask;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.api.ISaveStatistics;
import it.unibas.bartgui.egtaskdataobject.notifier.DataBaseConfigurationNotifier;
import it.unibas.bartgui.resources.StatusBar;
import it.unibas.bartgui.view.panel.run.BusyDialog;
import it.unibas.centrallookup.CentralLookup;
import java.awt.Dialog;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import speedy.model.database.EmptyDB;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@SuppressWarnings("deprecation")
@NbBundle.Messages({
    "MSG_STATUS_RUN_OK=EGTask Executed",
    "MSG_STATUS_RUN_NO=EGTask was NOT Executed!!",
    "MSG_RunEGTask_IS_RUN=EGTask is runnig....",
    "MSG_RunEGTask_NO_DataObj=EGTASK NULL IN DATAOBJECT",
    "MSG_RunEGTask_NO_DB_Source=DB Suorce is not set",
    "MSG_RunEGTask_NO_DB_Target=DB Target is not set",
    "MSG_RunEGTask_NO_DEPendencies=NO DEPENDENCIES",
})
@ServiceProvider(service = IRunEGTask.class)
public class RunEGTask implements IRunEGTask  {
    private static Logger log = Logger.getLogger(RunEGTask.class.getName());
    
    private static APrioriGenerator generator = null;
    private static boolean esito = false;
 
    
    @Override
    public void runEGTask() {
        log.setLevel(Level.INFO);
        log.fine("Load DataObject from CentralLookup");
        DataObject dObj = CentralLookup.getDefLookup().lookup(DataObject.class);
        if(dObj == null)return;
        EGTaskDataObjectDataObject dto = (EGTaskDataObjectDataObject)dObj;
        if(dto.isRun() == true)   {
            System.out.println("EGTask is runnig....");
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(Bundle.MSG_RunEGTask_IS_RUN()
                                                    , NotifyDescriptor.INFORMATION_MESSAGE));
            return;
        }
        if(dto.getEgtask() == null)   {
                log.log(Level.SEVERE, "EGTASK NULL IN DATAOBJECT");
                System.err.println("EGTASK NULL IN DATAOBJECT");
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(Bundle.MSG_RunEGTask_NO_DataObj()
                                                    , NotifyDescriptor.INFORMATION_MESSAGE));
                return;
        }
        
        
        if((dto.getEgtask().getTarget() == null) || (dto.getEgtask().getTarget() instanceof EmptyDB))  {
            log.fine("DB Target is not in correct state");
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(Bundle.MSG_RunEGTask_NO_DB_Target()
                                                    , NotifyDescriptor.INFORMATION_MESSAGE));
                return;
        }
//        if((dto.getDependencies() == null) || dto.getDependencies().isEmpty())  {
//            log.fine("No Dependencies");
//                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(Bundle.MSG_RunEGTask_NO_DEPendencies()
//                                                    , NotifyDescriptor.INFORMATION_MESSAGE));
//                return;
//        }
        
        generator = new APrioriGenerator();
        
        final Dialog d = BusyDialog.getBusyDialog();
        RequestProcessor.Task T =  RequestProcessor.getDefault().post(new RunEGTaskRunnable(dto));
        T.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(Task task) {
//                d.setVisible(false);
                if(esito)   {
                    log.fine("RUN Executed OK");
                    DataBaseConfigurationNotifier.fire();
                    StatusBar.setStatus(Bundle.MSG_STATUS_RUN_OK(), 10, 3000);                    
                }else{
                    log.fine("RUN NOT Executed correctly");
                    StatusBar.setStatus(Bundle.MSG_STATUS_RUN_NO(), 10, 3000);
                }
            }
        });
//        d.setVisible(true);
    } 
    
    private class RunEGTaskRunnable implements Runnable   {
        
        private EGTaskDataObjectDataObject dto;
        
        public RunEGTaskRunnable(EGTaskDataObjectDataObject dto) {
            this.dto = dto;
        }

        
        @Override
        public void run() {
            log.setLevel(Level.INFO);
            log.fine("START THREAD RUN");
            InputOutput io = IOProvider.getDefault().getIO(dto.getPrimaryFile().getName(), false);
            io.select();
            OutputWindow.openOutputWindowStream(io.getOut(), io.getErr());
            final ProgressHandle progr = ProgressHandleFactory.createHandle("Execute.... EGTask");
            try{
                log.fine("Init execution");
                progr.start();
                progr.progress("Executing EGTASK....");
                dto.setRun(true);
                System.out.println("*** START RUN EGTASK...");
                System.out.println("*** Messing up...");
                dto.getEgtask().setDirtyTarget(null);
                DataBaseConfigurationNotifier.fire();
                ErrorGeneratorStats.getInstance().resetStatistics();
                long start = new Date().getTime();
                CellChanges changes = generator.run(dto.getEgtask());
                long end = new Date().getTime();
                double executionTime = (end - start) / 1000.0;
                ErrorGeneratorStats er = ErrorGeneratorStats.getInstance();
                System.out.println("*** Execution time: " + executionTime + " sec");
                System.out.println("*** Total changes:  " + changes.getChanges().size());
                System.out.println(er.toString());
                progr.progress("EGTask Executed");
                log.fine("EGTask Executed");
                try{
                    log.fine("Save Statistics of execution");
                    progr.progress("Save Statistics .. execution");
                    ISaveStatistics saveStatistics = Lookup.getDefault().lookup(ISaveStatistics.class);
                    saveStatistics.save(dto, start, executionTime, changes.getChanges(),er);
                    log.fine("Statistics Saved OK");
                    progr.progress("Statistics Saved OK");
                }catch(Exception ex)   {
                    log.log(Level.SEVERE,"Save Statistic",ex);
                    System.err.println("EXCEPTION IN SAVE STATISTIC");
                    System.err.println("No Statistic saved !!");
                }
                er.resetStatistics();
                esito = true;
            }catch (ErrorGeneratorException eg) {
                log.log(Level.SEVERE,"ErrorGeneratorException",eg);
                ErrorManager.getDefault().notify(eg);
                progr.progress("ErrorGeneratorException");
                System.out.println("*** " + eg.getLocalizedMessage());
                esito = false;
            }catch(Exception ex)   {
                log.log(Level.SEVERE,"Exception",ex);
                ErrorManager.getDefault().notify(ex);
                System.err.println("Unexpected exception! " + ex.getLocalizedMessage());
                progr.progress("Error run EGTask");
                esito = false;
            }finally{
                OutputWindow.closeOutputWindowStream(io.getOut(), io.getErr());
                progr.finish();
                dto.setRun(false);
                log.fine("Close outputWindow and progressBar");
            }
        }
        
    }
}