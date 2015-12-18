package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DependenciesNodeNotify;
import it.unibas.bartgui.egtaskdataobject.wrapper.EGTaskConfigurationWrapper;
import it.unibas.bartgui.resources.R;
import it.unibas.centrallookup.CentralLookup;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Action;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.netbeans.api.actions.Editable;
import org.openide.ErrorManager;
import org.openide.actions.PropertiesAction;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@SuppressWarnings({"unchecked","rawtypes"})
@NbBundle.Messages({
    "CTL_ConfEGTaskConfNode=EGTask Configuration",
    "HINT_ConfEGTaskConfNode=Settings EGTask configuration parameter "
})
public class ConfEGTaskConfNode extends AbstractNode  implements PropertyChangeListener,Editable{
    
    public ConfEGTaskConfNode(EGTask egt,EGTaskDataObjectDataObject dto)  {
        super(Children.LEAF,
                new ProxyLookup(Lookups.fixed(egt,dto),dto.getAbstractLookup()));
        setName(NodeResource.NODE_ConfEGTaskConfNode);
        setShortDescription(Bundle.HINT_ConfEGTaskConfNode());
        setIconBaseWithExtension(R.IMAGE_SETTINGS);
    } 
    
    
    @Override
    public String getHtmlDisplayName() {
         EGTask task = getLookup().lookup (EGTask.class); 
         if (task != null) { 
             StringBuilder s = new StringBuilder();
             s.append(R.HTML_Node);
             s.append(Bundle.CTL_ConfEGTaskConfNode());
             s.append(R.HTML_CL_Node);
             return s.toString();
         } else{
             return null; 
         }
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName("egtkConf");setDisplayName("EGTask Configuration Parameter");
        
        final EGTask task = getLookup().lookup(EGTask.class);
        final EGTaskConfigurationWrapper wrp = new EGTaskConfigurationWrapper(task.getConfiguration());
        
        try{
            Property printLog = new PropertySupport.Reflection(wrp,boolean.class,"printLog");
            printLog.setName("printLog");
            set.put(printLog);
            
            Property debug = new PropertySupport.Reflection(wrp,boolean.class,"debug");
            debug.setName("debug");
            set.put(debug);
            
            Property queryExecutionTimeout= new PropertySupport.Reflection(wrp,Long.class,"queryExecutionTimeout");
            queryExecutionTimeout.setName("queryExecutionTimeout");
            set.put(queryExecutionTimeout);
            
            Property useDeltaDBForChanges = new PropertySupport.Reflection(wrp,boolean.class,"useDeltaDBForChanges");
            useDeltaDBForChanges.setName("useDeltaDBForChanges");
            set.put(useDeltaDBForChanges);
            
            Property recreateDBOnStart = new PropertySupport.Reflection(wrp,boolean.class,"recreateDBOnStart");
            recreateDBOnStart.setName("recreateDBOnStart");
            set.put(recreateDBOnStart);
            
            Property checkCleanInstance = new PropertySupport.Reflection(wrp,boolean.class,"checkCleanInstance");
            checkCleanInstance.setName("checkCleanInstance");
            set.put(checkCleanInstance);
            
            Property checkChanges = new PropertySupport.Reflection(wrp,boolean.class,"checkChanges");
            checkChanges.setName("checkChanges");
            set.put(checkChanges);
            
            Property excludeCrossProducts = new PropertySupport.Reflection(wrp,boolean.class,"excludeCrossProducts");
            excludeCrossProducts.setName("excludeCrossProducts");
            set.put(excludeCrossProducts);
            
            Property avoidInteractions = new PropertySupport.Reflection(wrp,boolean.class,"avoidInteractions");
            avoidInteractions.setName("avoidInteractions");
            set.put(avoidInteractions);
            
            Property applyCellChanges = new PropertySupport.Reflection(wrp,boolean.class,"applyCellChanges");
            applyCellChanges.setName("applyCellChanges");
            set.put(applyCellChanges);
            
            Property exportCellChanges = new PropertySupport.Reflection(wrp,boolean.class,"exportCellChanges");
            exportCellChanges.setName("exportCellChanges");
            set.put(exportCellChanges);
            
            //Property exportCellChangesPath = new PropertySupport.Reflection(wrp,File.class,"exportCellChangesPath");
            Property exportCellChangesPath = new PropertySupport.ReadWrite<File>("exportCellChangesPath",File.class, "ExportCellChangesPath", "") {           
                @Override
                public File getValue() throws IllegalAccessException, InvocationTargetException {
                    return wrp.getExportCellChangesPath();
                }       
                @Override
                public void setValue(File val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    try{                      
                        wrp.setExportCellChangesPath(val);
                    }catch(Exception ex)   {
                        wrp.setExportCellChangesPath(null);
                    }
                }
            };
            exportCellChangesPath.setName("exportCellChangesPath"); 
            exportCellChangesPath.setValue("baseDir", new File(System.getProperty("user.home")));
            exportCellChangesPath.setValue("filter",new FileNameExtensionFilter("CVS File", "cvs","CVS"));
            exportCellChangesPath.setValue("directories",Boolean.TRUE);
            exportCellChangesPath.setValue("files",Boolean.FALSE);
            set.put(exportCellChangesPath);
            
            Property exportDirtyDB = new PropertySupport.Reflection(wrp,boolean.class,"exportDirtyDB");
            exportDirtyDB.setName("exportDirtyDB");
            set.put(exportDirtyDB);
            
            Property exportDirtyDBType = new PropertySupport.Reflection(wrp,String.class,"exportDirtyDBType");
            exportDirtyDBType.setName("exportDirtyDBType");
            set.put(exportDirtyDBType);
            
            Property exportDirtyDBPath = new PropertySupport.ReadWrite<File>("exportDirtyDBPath",File.class, "ExportDirtyDBPath", "") {           
                @Override
                public File getValue() throws IllegalAccessException, InvocationTargetException {
                    return wrp.getExportDirtyDBPath();
                }       
                @Override
                public void setValue(File val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    try{
                        wrp.setExportDirtyDBPath(val);
                    }catch(Exception ex)   {
                        wrp.setExportDirtyDBPath(null);
                    }
                }
            };
            exportDirtyDBPath.setValue("baseDir", new File(System.getProperty("user.home")));
            exportDirtyDBPath.setValue("directories",Boolean.TRUE);
            exportDirtyDBPath.setValue("files",Boolean.FALSE);
            exportDirtyDBPath.setName("exportDirtyDBPath");
            set.put(exportDirtyDBPath);
            
            Property estimateRepairability = new PropertySupport.Reflection(wrp,boolean.class,"estimateRepairability");
            estimateRepairability.setName("estimateRepairability");
            set.put(estimateRepairability);
            
            Property cloneTargetSchema = new PropertySupport.Reflection(wrp,boolean.class,"cloneTargetSchema");
            cloneTargetSchema.setName("cloneTargetSchema");
            set.put(cloneTargetSchema);
            
            Property cloneSuffix = new PropertySupport.Reflection(wrp,String.class,"cloneSuffix");
            cloneSuffix.setName("cloneSuffix");
            set.put(cloneSuffix);
            
            Property useSymmetricOptimization = new PropertySupport.Reflection(wrp,boolean.class,"useSymmetricOptimization");
            useSymmetricOptimization.setName("useSymmetricOptimization");
            set.put(useSymmetricOptimization);
            
            Property generateAllChanges = new PropertySupport.Reflection(wrp,boolean.class,"generateAllChanges");
            generateAllChanges.setName("generateAllChanges");
            set.put(generateAllChanges);
            
            Property sampleStrategyForStandardQueries = new PropertySupport.Reflection(wrp,String.class,"sampleStrategyForStandardQueries");
            sampleStrategyForStandardQueries.setName("sampleStrategyForStandardQueries");
            sampleStrategyForStandardQueries.setValue("suppressCustomEditor", Boolean.TRUE);
            set.put(sampleStrategyForStandardQueries);
            
            Property sampleStrategyForSymmetricQueries = new PropertySupport.Reflection(wrp,String.class,"sampleStrategyForSymmetricQueries");
            sampleStrategyForSymmetricQueries.setName("sampleStrategyForSymmetricQueries");
            sampleStrategyForSymmetricQueries.setValue("suppressCustomEditor", Boolean.TRUE);
            set.put(sampleStrategyForSymmetricQueries);
            
            Property sampleStrategyForInequalityQueries = new PropertySupport.Reflection(wrp,String.class,"sampleStrategyForInequalityQueries");
            sampleStrategyForInequalityQueries.setName("sampleStrategyForInequalityQueries");
            sampleStrategyForInequalityQueries.setValue("suppressCustomEditor", Boolean.TRUE);
            set.put(sampleStrategyForInequalityQueries);
            
            Property detectEntireEquivalenceClasses= new PropertySupport.Reflection(wrp,boolean.class,"detectEntireEquivalenceClasses");
            detectEntireEquivalenceClasses.setName("detectEntireEquivalenceClasses");
            set.put(detectEntireEquivalenceClasses);
            
            Property sizeFactorReduction = new PropertySupport.Reflection(wrp,double.class,"sizeFactorReduction");
            sizeFactorReduction.setName("sizeFactorReduction");
            set.put(sizeFactorReduction);
            
            Property randomErrors = new PropertySupport.Reflection(wrp,boolean.class,"randomErrors");
            randomErrors.setName("randomErrors");
            set.put(randomErrors);
            
            Property outlierErrors = new PropertySupport.Reflection(wrp,boolean.class,"outlierErrors");
            outlierErrors.setName("outlierErrors");
            set.put(outlierErrors);
            
            wrp.addPropertyChangeListener(this);
        }catch(NoSuchMethodException nsme)   {
            ErrorManager.getDefault().notify(nsme);
        }
        sheet.put(set);
        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        EGTaskDataObjectDataObject dto = CentralLookup.getDefLookup().lookup(EGTaskDataObjectDataObject.class);
        if(dto != null)   {
            dto.setEgtModified(true);
        }
        EGTask  task = dto.getEgtask();
        if(evt.getPropertyName().equals("generateAllChanges")) {
            if(Boolean.parseBoolean(evt.getNewValue().toString()))  {
                task.getConfiguration().setRandomErrors(false);
                task.getConfiguration().setOutlierErrors(false);
            }           
            DependenciesNodeNotify.fire();
        }
    }

    @Override
    public Action[] getActions(boolean context) {
            Action[] result = new Action[]{
            Actions.forID("ConfRGTaskConfNode", "it.unibas.bartgui.controlegt.actions.node.ConfEGTNode.Edit"),
            null,
            SystemAction.get(PropertiesAction.class),
            };
            return result;
    }

    @Override
    public Action getPreferredAction() {
        //return Actions.forID("ConfRGTaskConfNode", "it.unibas.bartgui.controlegt.actions.node.ConfEGTNode.Edit");
        return SystemAction.get(PropertiesAction.class);
    } 

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public boolean canRename() {
        return false;
    }
    
    @Override
    public boolean canCut() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public void edit() {
        fireDisplayNameChange(null, "");
        setSheet(createSheet());
    }
    
}