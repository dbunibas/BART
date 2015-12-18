package it.unibas.bartgui.controlegt.impl;

import bart.model.EGTask;
import bart.model.EGTaskConfiguration;
import bart.model.dependency.Dependency;
import bart.model.errorgenerator.operator.GenerateVioGenQueries;
import bart.persistence.DAOEGTask;
import bart.persistence.DAOEGTaskConfiguration;
import it.unibas.bartgui.controlegt.OutputWindow;
import it.unibas.bartgui.egtaskdataobject.api.ILoadEGTask;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.RootNodeNotifier;
import it.unibas.bartgui.egtaskdataobject.statistics.RootNodeStatistic;
import it.unibas.bartgui.egtaskdataobject.statistics.StatisticNodeFactory;
import it.unibas.bartgui.resources.StatusBar;
import it.unibas.bartgui.view.panel.run.BusyDialog;
import org.openide.loaders.DataObject;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import it.unibas.centrallookup.CentralLookup;
import java.awt.Dialog;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.ErrorManager;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import speedy.exceptions.DAOException;
import speedy.exceptions.DBMSException;
import speedy.model.database.EmptyDB;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.xml.DAOXmlUtility;
import speedy.persistence.xml.operators.TransformFilePaths;
import speedy.utility.DBMSUtility;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@SuppressWarnings("deprecation")
@ServiceProvider(service = ILoadEGTask.class)
@NbBundle.Messages({
    "MSG_STATUS_ConfLoaded=Configuration file loaded",
    "MSG_STATUS_ConfLoadError=WRONG Configuration",
    "MSG_STATUS_ConfNotLoaded=Configuration was not loaded",
    "# {0} - file name",
    "MSG_STATUS_Close=File {0} is modified. \n Save it ?"
})
public class LoadEGTask implements ILoadEGTask  {
    private static Logger log; 
    static{
        log = Logger.getLogger(LoadEGTask.class.getName());
        log.setLevel(Level.INFO);
    }
    
    private DataObject egtDO;
    private EGTaskConfiguration conf;
    private static DAOEGTaskConfiguration daoTaskConfiguration = new DAOEGTaskConfiguration();
    private static DAOEGTask daoTask = new DAOEGTask();
    private GenerateVioGenQueries vioGenQueriesGenerator = new GenerateVioGenQueries();
    private TransformFilePaths filePathTransformator = new TransformFilePaths();
    private EGTask task = null;
    private String fileTask;
    private boolean esito = false;
    
    
    @Override
    public void load(FileObject file) { 
        
        log.fine("Close EGTaskDataObjectDataObject");
        EGTaskDataObjectDataObject oldDto = CentralLookup.getDefLookup().lookup(EGTaskDataObjectDataObject.class);
        if((oldDto != null)&&(!oldDto.close()))return;
        
        log.fine("Close TopComponents");
        for(TopComponent tc : WindowManager.getDefault().getRegistry().getOpened())   {
            if(WindowManager.getDefault().isEditorTopComponent(tc))   {
                tc.close();
            }         
        }
        
        try{
            log.fine("Find DataObject for a file");
            egtDO = DataObject.find(file);
        }catch(DataObjectNotFoundException donf)   {
            ErrorManager.getDefault().notify(ErrorManager.ERROR, donf);
            StatusBar.setStatus(Bundle.MSG_STATUS_ConfNotLoaded(), 10, 3000);
            log.log(Level.SEVERE,"DataObject notFound",donf);
            return;
        }
        
        final Dialog d = BusyDialog.getBusyDialog();
        RequestProcessor.Task T = RequestProcessor.getDefault().post(new LoadEGTaskRunnable());
        T.addTaskListener(new TaskListener() {

            @Override
            public void taskFinished(Task task) {
                d.setVisible(false);
                if(esito)   {
                    log.fine("Esito true -> Dataobject to CentralLookup");
                    RootNodeNotifier.fire();
                    CentralLookup.getDefLookup().add(egtDO);
                    log.fine("Set Node Statistic");
                    CentralLookup.getDefLookup().add(new RootNodeStatistic(Children.create(new StatisticNodeFactory(), true)));
                    StatusBar.setStatus(Bundle.MSG_STATUS_ConfLoaded(), 10, 3000);
                }else{
                    log.fine("Esito false -> ");
                    RootNodeNotifier.fire();
                    CentralLookup.getDefLookup().add(egtDO);
                    egtDO.getLookup().lookup(OpenCookie.class).open();
                    StatusBar.setStatus(Bundle.MSG_STATUS_ConfLoadError(), 10, 3000);
                }
                d.setVisible(false);
            }
        });
        d.setVisible(true);
    }

    
   
    private class LoadEGTaskRunnable implements Runnable   {
        @Override
        public void run() {
            log.fine("START THREAD LOAD EGTASK");
            InputOutput io = IOProvider.getDefault().getIO(egtDO.getPrimaryFile().getName(), false);
            io.select();
            OutputWindow.openOutputWindowStream(io.getOut(), io.getErr());
            final ProgressHandle progr = ProgressHandleFactory.createHandle("Loading.... EGTask");
            progr.start();
            FileLock lock = null;
            try{
                log.fine("lock primary file");
                lock = egtDO.getPrimaryFile().lock();
                
                File taskFile = FileUtil.toFile(egtDO.getPrimaryFile());
                 progr.progress("File loaded");
                log.fine("File loaded -> "+taskFile.getName());
                fileTask = taskFile.getAbsolutePath();
                
                conf = daoTaskConfiguration.loadConfiguration(fileTask);
                 progr.progress("Configuration loaded");
                log.fine("Configuration loaded");
                
                if (conf.isRecreateDBOnStart()) {
                    log.fine("Remove DB if exist");
                    progr.progress("Remove DB if exist");
                    removeExistingDB(fileTask);
                }
                
                task = daoTask.loadTask(fileTask);  
                progr.progress("EGTask loaded");
                log.fine("EGTask loaded");
                
                if((task.getTarget()!= null) && (!(task.getTarget()instanceof EmptyDB)) )   {
                        log.fine("Generate Dependency");
                        for (Dependency dc : task.getDCs()) {
                        dc.setVioGenQueries(vioGenQueriesGenerator.generateVioGenQueries(dc, task));
                    }                   
                }

                progr.progress("Dependency generated");
                log.fine("Dependency generated");
               
                
                
                String dependencies = loadStringDependecyElement(egtDO.getPrimaryFile());
                loadStringMainMemoryDatabase(fileTask);
                task.setAbsolutePath(fileTask);
                ((EGTaskDataObjectDataObject)egtDO).setEGTask(task);
                ((EGTaskDataObjectDataObject)egtDO).setDependencies((dependencies == null) ? null : dependencies.trim());

                System.out.println("CONFIGURATION: "+egtDO.getPrimaryFile().getName()+"  LOADED");
                esito=true;
                log.fine("FINISH LOAD EGTASK");
            }catch(FileAlreadyLockedException farl){
                progr.progress("File Locked FileAlreadyLockedException");
                log.log(Level.SEVERE,"File Locked FileAlreadyLockedException",farl);
                ErrorManager.getDefault().notify(ErrorManager.USER,farl);
                System.err.println("File Locked FileAlreadyLockedException");
                esito=false;
            }catch(DAOException daoe){
                progr.progress("Error load configuration DAOException");
                log.log(Level.SEVERE,"Error load configuration DAOException",daoe);
                ErrorManager.getDefault().notify(ErrorManager.USER,daoe);
                System.err.println("ERROR LOANDING CONFIGURATION DAOException");
                esito=false;
            }catch(DBMSException dbmsex){
                progr.progress("Error load configuration DBMSException");
                log.log(Level.SEVERE,"Error load configuration DBMSException",dbmsex);
                CentralLookup.getDefLookup().add(egtDO);
                ErrorManager.getDefault().notify(ErrorManager.USER,dbmsex);
                System.err.println("ERROR LOANDING CONFIGURATION DBMSException");
                esito=false;
            }catch(Exception ex){
                progr.progress("Error load configuration");
                log.log(Level.SEVERE,"Error load configuration",ex);
                CentralLookup.getDefLookup().add(egtDO);
                ErrorManager.getDefault().notify(ErrorManager.USER,ex);
                System.err.println("ERROR LOANDING CONFIGURATION Exception");
                esito=false;
            }finally{
                if(lock != null)lock.releaseLock();
                OutputWindow.closeOutputWindowStream(io.getOut(), io.getErr());
                progr.finish();
                log.fine("Close OutputWindow and progressBar");
            }
        }
    }
    
    private String loadStringDependecyElement(FileObject fo)   { 
        //FOR EGTaskDataObject simple string 
        try{
            
            InputStream is = fo.getInputStream();
            Document doc = XMLUtil.parse(new InputSource(is), false, true, null, null);
            log.fine("Init load String dependency Element");
            NodeList dependenciesTAG = doc.getElementsByTagName("dependencies");
            if(dependenciesTAG.getLength() == 0)return null;
            String dependencies = dependenciesTAG.item(0).getTextContent();
            log.fine("Finish load String dependency Element");
            
            return dependencies;
        }catch(Exception ex)   {
            log.log(Level.WARNING,"load String Dependecy Element  FAILED",ex);
            System.err.println(".......");
        }
        return null;
    }
    
    private void loadStringMainMemoryDatabase(String path)   {
        //FOR EGTaskDataObject simple string 
        try{
            log.entering(getClass().getName(), "loadStringMainMemoryDatabase");
            log.fine("Init load String reference to file MainMemory DB");
            DAOXmlUtility daoUtility = new DAOXmlUtility();
            org.jdom.Document document = daoUtility.buildDOM(path);
            Element rootElement = document.getRootElement();
            Element keySource= rootElement.getChild("source");
            if(keySource != null)   {
                loadForKeyMainMem(keySource,path);
            }
            Element keyTarget= rootElement.getChild("target");
            if(keyTarget != null)   {
                loadForKeyMainMem(keyTarget,path);
            }
            log.fine("Finish load String reference to file MainMemory DB");
            log.exiting(getClass().getName(), "loadStringMainMemoryDatabase");
        }catch(Exception ex)   {
            log.log(Level.WARNING,"load String MainMemoryDatabase",ex);
            log.exiting(getClass().getName(), "loadStringMainMemoryDatabase");
            System.err.println(".......");
        }
    }
    
    private void loadForKeyMainMem(Element keyElement,String path)   {
        Element typeElement = keyElement.getChild("type");
        if(typeElement.getValue().equals("XML"))   {
            Element xmlElement = keyElement.getChild("xml");
            String schemaRelativeFile = xmlElement.getChild("xml-schema").getValue();
            String schemaAbsoluteFile = filePathTransformator.expand(path, schemaRelativeFile);
            String instanceRelativeFile = xmlElement.getChild("xml-instance").getValue();
            String instanceAbsoluteFile = null; //Optional field
            if (instanceRelativeFile != null && !instanceRelativeFile.trim().isEmpty()) {
                        instanceAbsoluteFile = filePathTransformator.expand(path, instanceRelativeFile);
            }
            if(keyElement.getValue().equals("source"))   {
                ((EGTaskDataObjectDataObject)egtDO).setXmlInstanceFilePathSourceDB(instanceAbsoluteFile);
                ((EGTaskDataObjectDataObject)egtDO).setXmlSchemaFilePathSourceDB(schemaAbsoluteFile);
                ((EGTaskDataObjectDataObject)egtDO).setMainMemoryGenerateSource(false);
            }
            if(keyElement.getValue().equals("target"))   {
                ((EGTaskDataObjectDataObject)egtDO).setXmlInstanceFilePathTargetDB(instanceAbsoluteFile);
                ((EGTaskDataObjectDataObject)egtDO).setXmlSchemaFilePathTargetDB(schemaAbsoluteFile);
                ((EGTaskDataObjectDataObject)egtDO).setMainMemoryGenerateTager(false);
            }
        }
        if(typeElement.getValue().equals("GENERATE"))   {
            Element plainInstanceElement = keyElement.getChild("generate");
            String plainInstance = plainInstanceElement.getValue();
            if(keyElement.getName().equals("source"))   {
                ((EGTaskDataObjectDataObject)egtDO).setPlainInstanceGenerateSourceDB(plainInstance.trim());
                ((EGTaskDataObjectDataObject)egtDO).setMainMemoryGenerateSource(true);
            }
            if(keyElement.getName().equals("target"))   {
                ((EGTaskDataObjectDataObject)egtDO).setPlainInstanceGenerateTargetDB(plainInstance.trim());
                ((EGTaskDataObjectDataObject)egtDO).setMainMemoryGenerateTager(true);
            }
        }
    }
    

       
    private static void removeExistingDB(String fileTask) {
        try {        
            AccessConfiguration accessConfiguration = loadTargetAccessConfiguration(fileTask);
            if (accessConfiguration == null) {
                return;
            }
            System.out.println("Removing db " + accessConfiguration.getDatabaseName() + ", if exist...");
            DBMSUtility.deleteDB(accessConfiguration);
            System.out.println("Database removed!");
        } catch (DBMSException ex) {
            String message = ex.getMessage();
            if (!message.contains("does not exist")) {
                log.log(Level.WARNING,"Unable to drop database.\n", ex);
                System.out.println("Unable to drop database. does not exist\n");
            }
        }catch (Exception ex)   {
           log.log(Level.WARNING,"Unable to drop database.\n",ex);
            System.out.println("Unable to drop database.\n"+ex);
        }
    }

    private static AccessConfiguration loadTargetAccessConfiguration(String fileTask) {
        org.jdom.Document document = new DAOXmlUtility().buildDOM(fileTask);
        Element rootElement = document.getRootElement();
        Element databaseElement = rootElement.getChild("target");
        Element dbmsElement = databaseElement.getChild("access-configuration");
        if (dbmsElement == null) {
            return null;
        }
        AccessConfiguration accessConfiguration = new AccessConfiguration();
        accessConfiguration.setDriver(dbmsElement.getChildText("driver").trim());
        accessConfiguration.setUri(dbmsElement.getChildText("uri").trim());
        accessConfiguration.setSchemaName(dbmsElement.getChildText("schema").trim());
        accessConfiguration.setLogin(dbmsElement.getChildText("login").trim());
        accessConfiguration.setPassword(dbmsElement.getChildText("password").trim());
        return accessConfiguration;
    }

} 