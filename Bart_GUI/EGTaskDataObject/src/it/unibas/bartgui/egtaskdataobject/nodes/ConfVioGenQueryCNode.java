package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DependenciesNodeNotify;
import it.unibas.bartgui.egtaskdataobject.wrapper.VioGenQueryConfigurationWrapper;
import it.unibas.bartgui.resources.R;
import it.unibas.centrallookup.CentralLookup;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import org.netbeans.api.actions.Editable;
import org.openide.ErrorManager;
import org.openide.actions.PropertiesAction;
import org.openide.awt.Actions;
import org.openide.loaders.DataObject;
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
    "CTL_ConfVioGenQueryCNode=VioGenQuery Configuration",
    "HINT_ConfVioGenQueryCNode=Settings VioGenQuery Configuration Settings"
})
public class ConfVioGenQueryCNode extends AbstractNode implements PropertyChangeListener,Editable {
    
    private VioGenQueryConfigurationWrapper wr;

    public ConfVioGenQueryCNode(EGTask egt,EGTaskDataObjectDataObject dto) throws IntrospectionException   {
        super(Children.LEAF,
                new ProxyLookup(Lookups.fixed(egt,dto),dto.getAbstractLookup()));
        setName(NodeResource.NODE_ConfVioGenQueryCNode);
        setShortDescription(Bundle.HINT_ConfVioGenQueryCNode());
        setIconBaseWithExtension(R.IMAGE_SETTINGS);
    }
    
    
    @Override
    public String getHtmlDisplayName() {
         EGTask task = getLookup().lookup (EGTask.class); 
         if (task != null) { 
             StringBuilder s = new StringBuilder();
             s.append(R.HTML_Node);
             s.append(Bundle.CTL_ConfVioGenQueryCNode());
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
        EGTask task = getLookup().lookup(EGTask.class);
        VioGenQueryConfigurationWrapper wrp = new VioGenQueryConfigurationWrapper(task.getConfiguration().
                                                                                        getDefaultVioGenQueryConfiguration());
        try{
            Property percentage = new PropertySupport.Reflection(wrp,double.class,"percentage");
            percentage.setName("percentage");
            set.put(percentage);
            
            Property sizeFactorForStandardQueries = new PropertySupport.Reflection(wrp,double.class,"sizeFactorForStandardQueries");
            sizeFactorForStandardQueries.setName("sizeFactorForStandardQueries");
            set.put(sizeFactorForStandardQueries);
            
            Property sizeFactorForSymmetricQueries = new PropertySupport.Reflection(wrp,double.class,"sizeFactorForSymmetricQueries");
            sizeFactorForSymmetricQueries.setName("sizeFactorForSymmetricQueries");
            set.put(sizeFactorForSymmetricQueries);
            
            Property sizeFactorForInequalityQueries = new PropertySupport.Reflection(wrp,double.class,"sizeFactorForInequalityQueries");
            sizeFactorForInequalityQueries.setName("sizeFactorForInequalityQueries");
            set.put(sizeFactorForInequalityQueries);
            
            Property sizeFactorForSingleTupleQueries = new PropertySupport.Reflection(wrp,double.class,"sizeFactorForSingleTupleQueries");
            sizeFactorForSingleTupleQueries.setName("sizeFactorForSingleTupleQueries");
            set.put(sizeFactorForSingleTupleQueries);
            
            Property probabilityFactorForStandardQueries = new PropertySupport.Reflection(wrp,double.class,"probabilityFactorForStandardQueries");
            probabilityFactorForStandardQueries.setName("probabilityFactorForStandardQueries");
            set.put(probabilityFactorForStandardQueries);
            
            Property probabilityFactorForSymmetricQueries = new PropertySupport.Reflection(wrp,double.class,"probabilityFactorForSymmetricQueries");
            probabilityFactorForSymmetricQueries.setName("probabilityFactorForSymmetricQueries");
            set.put(probabilityFactorForSymmetricQueries);
            
            Property probabilityFactorForInequalityQueries = new PropertySupport.Reflection(wrp,double.class,"probabilityFactorForInequalityQueries");
            probabilityFactorForInequalityQueries.setName("probabilityFactorForInequalityQueries");
            set.put(probabilityFactorForInequalityQueries);
            
            Property probabilityFactorForSingleTupleQueries = new PropertySupport.Reflection(wrp,double.class,"probabilityFactorForSingleTupleQueries");
            probabilityFactorForSingleTupleQueries.setName("probabilityFactorForSingleTupleQueries");
            set.put(probabilityFactorForSingleTupleQueries);
            
            Property windowSizeFactorForStandardQueries = new PropertySupport.Reflection(wrp,double.class,"windowSizeFactorForStandardQueries");
            windowSizeFactorForStandardQueries.setName("windowSizeFactorForStandardQueries");
            set.put(windowSizeFactorForStandardQueries);
            
            Property windowSizeFactorForSymmetricQueries = new PropertySupport.Reflection(wrp,double.class,"windowSizeFactorForSymmetricQueries");
            windowSizeFactorForSymmetricQueries.setName("windowSizeFactorForSymmetricQueries");
            set.put(windowSizeFactorForSymmetricQueries);
            
            Property windowSizeFactorForInequalityQueries= new PropertySupport.Reflection(wrp,double.class,"windowSizeFactorForInequalityQueries");
            windowSizeFactorForInequalityQueries.setName("windowSizeFactorForInequalityQueries");
            set.put(windowSizeFactorForInequalityQueries);
            
            Property windowSizeFactorForSingleTupleQueries = new PropertySupport.Reflection(wrp,double.class,"windowSizeFactorForSingleTupleQueries");
            windowSizeFactorForSingleTupleQueries.setName("windowSizeFactorForSingleTupleQueries");
            set.put(windowSizeFactorForSingleTupleQueries);
            
            Property offsetFactorForStandardQueries = new PropertySupport.Reflection(wrp,double.class,"offsetFactorForStandardQueries");
            offsetFactorForStandardQueries.setName("offsetFactorForStandardQueries");
            set.put(offsetFactorForStandardQueries);
            
            Property offsetFactorForSymmetricQueries = new PropertySupport.Reflection(wrp,double.class,"offsetFactorForSymmetricQueries");
            offsetFactorForSymmetricQueries.setName("offsetFactorForSymmetricQueries");
            set.put(offsetFactorForSymmetricQueries);
            
            Property offsetFactorForInequalityQueries = new PropertySupport.Reflection(wrp,double.class,"offsetFactorForInequalityQueries");
            offsetFactorForInequalityQueries.setName("offsetFactorForInequalityQueries");
            set.put(offsetFactorForInequalityQueries);
            
            Property offsetFactorForSingleTupleQueries = new PropertySupport.Reflection(wrp,double.class,"offsetFactorForSingleTupleQueries");
            offsetFactorForSingleTupleQueries.setName("offsetFactorForSingleTupleQueries");
            set.put(offsetFactorForSingleTupleQueries);
            
            Property maxNumberOfRowsForSingleTupleQueries = new PropertySupport.Reflection(wrp,int.class,"maxNumberOfRowsForSingleTupleQueries");
            maxNumberOfRowsForSingleTupleQueries.setName("maxNumberOfRowsForSingleTupleQueries");
            set.put(maxNumberOfRowsForSingleTupleQueries);
            
            Property useLimitInStandardQueries = new PropertySupport.Reflection(wrp,boolean.class,"useLimitInStandardQueries");
            useLimitInStandardQueries.setName("useLimitInStandardQueries");
            set.put(useLimitInStandardQueries);
            
            Property useLimitInSymmetricQueries = new PropertySupport.Reflection(wrp,boolean.class,"useLimitInSymmetricQueries");
            useLimitInSymmetricQueries.setName("useLimitInSymmetricQueries");
            set.put(useLimitInSymmetricQueries);
            
            Property useLimitInInequalityQueries = new PropertySupport.Reflection(wrp,boolean.class,"useLimitInInequalityQueries");
            useLimitInInequalityQueries.setName("useLimitInInequalityQueries");
            set.put(useLimitInInequalityQueries);
            
            Property useLimitInSingleTupleQueries = new PropertySupport.Reflection(wrp,boolean.class,"useLimitInSingleTupleQueries");
            useLimitInSingleTupleQueries.setName("useLimitInSingleTupleQueries");
            set.put(useLimitInSingleTupleQueries);
            
            Property useOffsetInStandardQueries = new PropertySupport.Reflection(wrp,boolean.class,"useOffsetInStandardQueries");
            useOffsetInStandardQueries.setName("useOffsetInStandardQueries");
            set.put(useOffsetInStandardQueries);
            
            Property useOffsetInSymmetricQueries = new PropertySupport.Reflection(wrp,boolean.class,"useOffsetInSymmetricQueries");
            useOffsetInSymmetricQueries.setName("useOffsetInSymmetricQueries");
            set.put(useOffsetInSymmetricQueries);
            
            Property useOffsetInInequalityQueries = new PropertySupport.Reflection(wrp,boolean.class,"useOffsetInInequalityQueries");
            useOffsetInInequalityQueries.setName("useOffsetInInequalityQueries");
            set.put(useOffsetInInequalityQueries);
            
            Property useOffsetInSingleTupleQueries = new PropertySupport.Reflection(wrp,boolean.class,"useOffsetInSingleTupleQueries");
            useOffsetInSingleTupleQueries.setName("useOffsetInSingleTupleQueries");
            set.put(useOffsetInSingleTupleQueries);
            
            Property queryExecutor = new PropertySupport.Reflection(wrp,String.class,"queryExecutor");
            queryExecutor.setName("queryExecutor");
            set.put(queryExecutor);
            
            wrp.addPropertyChangeListener(WeakListeners.propertyChange(this, wrp));
        }catch(NoSuchMethodException nsmex)   {
            ErrorManager.getDefault().notify(nsmex);
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
        if(evt.getPropertyName().equals("percentage"))   {
            double d = (Double)evt.getNewValue();
            DependenciesNodeNotify.fire();
            //VioGenQueryNodeNotify.fire();
        }
        
        
    }  

    @Override
    public Action[] getActions(boolean context) {
            Action[] result = new Action[]{
            Actions.forID("ConfVioGenQueryCNode", "it.unibas.bartgui.controlegt.actions.node.ConfVioGenQ.Edit"),
            null,
            SystemAction.get(PropertiesAction.class),
            };
            return result;
    }

    @Override
    public Action getPreferredAction() {
        //return Actions.forID("ConfVioGenQueryCNode", "it.unibas.bartgui.controlegt.actions.node.ConfVioGenQ.Edit");
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