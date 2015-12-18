package it.unibas.bartgui.egtaskdataobject;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.api.ISave;
import it.unibas.bartgui.egtaskdataobject.nodes.RootNodeFactory;
import it.unibas.bartgui.egtaskdataobject.notifier.RootNodeNotifier;
import it.unibas.bartgui.resources.R;
import it.unibas.centrallookup.CentralLookup;
import java.io.IOException;
import org.netbeans.api.actions.Closable;
import org.netbeans.api.actions.Savable;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;


@Messages({
    "LBL_EGTaskDataObject_LOADER=Files of EGTask Configuration",
    "# {0} - file name",
    "MSG_EGTaskDataObject_Close=File {0} is modified. \n Save it ?"
})
@MIMEResolver.NamespaceRegistration(
        displayName = "#LBL_EGTaskDataObject_LOADER",
        mimeType = "text/egtask+xml",
        //elementNS = {"namespace"},
        elementName = "task"
)
@DataObject.Registration(
        mimeType = "text/egtask+xml",
        iconBase = "it/unibas/bartgui/resources/icons/gear.png",
        displayName = "#LBL_EGTaskDataObject_LOADER",
        position = 300
)
@ActionReferences({
    
    /*@ActionReference(
            path = "Loaders/text/egtask+xml/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100           
    ),*/   
    @ActionReference(
            path = "Loaders/text/egtask+xml/Actions",
            id = @ActionID(category = "System", id = "org-openide-actions-CloseAction"),
            position = 200,
            separatorAfter = 300
    ),
    @ActionReference(
            path = "Loaders/text/egtask+xml/Actions",
            id = @ActionID(category = "System", id = "org-openide-actions-SaveAction"),
            position = 400
    ),
    @ActionReference(
            path = "Loaders/text/egtask+xml/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 600
    ),
    @ActionReference(
            path = "Loaders/text/egtask+xml/Actions",
            id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 700,
            separatorAfter = 800
    ),
    @ActionReference(
            path = "Loaders/text/egtask+xml/Actions",
            id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400,
            separatorAfter = 1500
    )
})
public class EGTaskDataObjectDataObject extends MultiDataObject implements Closable{
    
    private EGTask task;
    
    private String dependencies;
    
    private boolean mainMemoryGenerateSource = false;
    private String plainInstanceGenerateSourceDB;
    
    private boolean mainMemoryGenerateTager = false;
    private String plainInstanceGenerateTargetDB;
    
    private String xmlSchemaFilePathSourceDB;
    private String xmlInstanceFilePathSourceDB;
        
    private String xmlSchemaFilePathTargetDB;
    private String xmlInstanceFilePathTargetDB;
    
    private boolean run = false;
    private boolean egtModified = false;
    
    private final InstanceContent ic;
    private final AbstractLookup abstractLookup;
    
    public EGTaskDataObjectDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("text/egtask+xml", false); 
        ic = new InstanceContent();
        abstractLookup = new AbstractLookup(ic);
    }
    
    public void setEGTask(EGTask egt)  {
        task = egt;
    }
    
    public EGTask getEgtask()   {
        return this.task;
    }
    
    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    public String getDependencies() {
        return dependencies;
    }
    
    public boolean isRun()   {
        return run;
    }
    
    public void setRun(boolean run)  {
        this.run=run;
    }  

    @Override
    protected int associateLookup() {
        return 1;
    }   
    
    public Lookup getAbstractLookup()   {
        return abstractLookup;
    }

    
    @Override
    protected Node createNodeDelegate() {
        return new DataNode(this, 
                            Children.create(new RootNodeFactory(this), true),
                            new ProxyLookup(getLookup(),abstractLookup))   {
    
            @Override
            public String getHtmlDisplayName() {
                        StringBuilder sb = new StringBuilder();
                        sb.append(R.HTML_R_Node);
                        sb.append(EGTaskDataObjectDataObject.this.getPrimaryFile().getName());
                        sb.append(R.HTML_CL_R_Node);
                        return sb.toString();
            }           
        };
    }

    @Override
    protected FileObject handleRename(String name) throws IOException {
        StringBuilder path = new StringBuilder("statistics/");
        path.append(getPrimaryFile().getName());
        FileObject statFolder = FileUtil.getConfigFile(path.toString());
        if(statFolder != null)   {
            FileLock lockStatFolder = null;
            try{           
                lockStatFolder = statFolder.lock();
                statFolder.rename(lockStatFolder, name, null);            
            }catch(IOException ex)   {
                ErrorManager.getDefault().notify(ErrorManager.WARNING,ex);
            }finally{
                if(lockStatFolder != null)   {
                    lockStatFolder.releaseLock();
                }
            }
        }
        return super.handleRename(name);
    }

    @Override
    protected void handleDelete() throws IOException {
        StringBuilder path = new StringBuilder("statistics/");
        path.append(getPrimaryFile().getName());
        FileObject statFolder = FileUtil.getConfigFile(path.toString());
        if(statFolder != null)   {
            try{
                statFolder.delete();
                CentralLookup.getDefLookup().clean();
            }catch(IOException ioe)   {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, ioe);
            }
        }else{
            CentralLookup.getDefLookup().clean();
        }
        super.handleDelete(); 
    }

    @Override
    public boolean isMoveAllowed() {
        return false;
    }
    

    @Override
    public boolean close() {
        if(isEgtModified())   {
            Object result =DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Confirmation(
                            Bundle.MSG_EGTaskDataObject_Close(this.getPrimaryFile().getName()),
                            "Save", 
                            NotifyDescriptor.YES_NO_OPTION)
            );
            if(result.equals(NotifyDescriptor.YES_OPTION))   {
                for(TopComponent tc : WindowManager.getDefault().getRegistry().getOpened())   {
                    if(tc.getDisplayName().equals(this.getPrimaryFile().getName()))   {
                        tc.close();
                    }         
                }
                try{
                    abstractLookup.lookup(Savable.class).save();
                }catch(IOException ioe)   {
                    ErrorManager.getDefault().notify(ErrorManager.ERROR, ioe);
                    return false;
                }
            }
        }
        resetContent();
        closeEditorTopComponet();
        this.setEgtModified(false);
        this.setEGTask(null);
        RootNodeNotifier.fire();
        CentralLookup.getDefLookup().clean();
        return true;
    }

    
    /**
     * @return the egtModified
     */
    public boolean isEgtModified() {
        return egtModified;
    }

    
    /**
     * @param egtModified the egtModified to set
     */
    public synchronized void setEgtModified(boolean modified) {
        setModified(false);
        if(egtModified && modified)return;
        egtModified = modified;
        if(this.egtModified)   {
            addContent(new Save());
        }else{
            resetContent();
        }
        
    }

    /**
     * @return the plainInstanceGenerateSourceDB
     */
    public String getPlainInstanceGenerateSourceDB() {
        return plainInstanceGenerateSourceDB;
    }

    /**
     * @param plainInstanceGenerateSourceDB the plainInstanceGenerateSourceDB to set
     */
    public void setPlainInstanceGenerateSourceDB(String plainInstanceGenerateSourceDB) {
        this.plainInstanceGenerateSourceDB = plainInstanceGenerateSourceDB;
    }

    /**
     * @return the xmlSchemaFilePathSourceDB
     */
    public String getXmlSchemaFilePathSourceDB() {
        return xmlSchemaFilePathSourceDB;
    }

    /**
     * @param xmlSchemaFilePathSourceDB the xmlSchemaFilePathSourceDB to set
     */
    public void setXmlSchemaFilePathSourceDB(String xmlSchemaFilePathSourceDB) {
        this.xmlSchemaFilePathSourceDB = xmlSchemaFilePathSourceDB;
    }

    /**
     * @return the xmlInstanceFilePathSourceDB
     */
    public String getXmlInstanceFilePathSourceDB() {
        return xmlInstanceFilePathSourceDB;
    }

    /**
     * @param xmlInstanceFilePathSourceDB the xmlInstanceFilePathSourceDB to set
     */
    public void setXmlInstanceFilePathSourceDB(String xmlInstanceFilePathSourceDB) {
        this.xmlInstanceFilePathSourceDB = xmlInstanceFilePathSourceDB;
    }

    /**
     * @return the plainInstanceGenerateTargetDB
     */
    public String getPlainInstanceGenerateTargetDB() {
        return plainInstanceGenerateTargetDB;
    }

    /**
     * @param plainInstanceGenerateTargetDB the plainInstanceGenerateTargetDB to set
     */
    public void setPlainInstanceGenerateTargetDB(String plainInstanceGenerateTargetDB) {
        this.plainInstanceGenerateTargetDB = plainInstanceGenerateTargetDB;
    }

    /**
     * @return the xmlSchemaFilePathTargetDB
     */
    public String getXmlSchemaFilePathTargetDB() {
        return xmlSchemaFilePathTargetDB;
    }

    /**
     * @param xmlSchemaFilePathTargetDB the xmlSchemaFilePathTargetDB to set
     */
    public void setXmlSchemaFilePathTargetDB(String xmlSchemaFilePathTargetDB) {
        this.xmlSchemaFilePathTargetDB = xmlSchemaFilePathTargetDB;
    }

    /**
     * @return the xmlInstanceFilePathTargetDB
     */
    public String getXmlInstanceFilePathTargetDB() {
        return xmlInstanceFilePathTargetDB;
    }

    /**
     * @param xmlInstanceFilePathTargetDB the xmlInstanceFilePathTargetDB to set
     */
    public void setXmlInstanceFilePathTargetDB(String xmlInstanceFilePathTargetDB) {
        this.xmlInstanceFilePathTargetDB = xmlInstanceFilePathTargetDB;
    }

    /**
     * @return the mainMemoryGenerate
     */
    public boolean isMainMemoryGenerateSource() {
        return mainMemoryGenerateSource;
    }

    /**
     * @param mainMemoryGenerate the mainMemoryGenerate to set
     */
    public void setMainMemoryGenerateSource(boolean mainMemoryGenerateSource) {
        this.mainMemoryGenerateSource = mainMemoryGenerateSource;
    }
    
        
    /**
     * @return the mainMemoryGenerateTager
     */
    public boolean isMainMemoryGenerateTager() {
        return mainMemoryGenerateTager;
    }

    /**
     * @param mainMemoryGenerateTager the mainMemoryGenerateTager to set
     */
    public void setMainMemoryGenerateTager(boolean mainMemoryGenerateTager) {
        this.mainMemoryGenerateTager = mainMemoryGenerateTager;
    }


    public void addContent(Object o)   {
        this.ic.add(o);
    }
    
    public void removeContent(Object o)   {
        this.ic.remove(o);
    }
    
    public void resetContent()   {
        for(Savable o : this.abstractLookup.lookupAll(Savable.class))   {
            this.ic.remove(o);
        }
    }
    
    private void closeEditorTopComponet()   {
        for(TopComponent tc : WindowManager.getDefault().getRegistry().getOpened())   {
            if(WindowManager.getDefault().isEditorTopComponent(tc))   {
                tc.close();
            }         
        } 
    }
    
    private class Save implements Savable   {
        
        @Override
        public void save() throws IOException {
            if(isModified())setModified(false);
            ISave saveEgtask = Lookup.getDefault().lookup(ISave.class);
            saveEgtask.save();
            setEgtModified(false);
        }      
    }
}
