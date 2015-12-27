package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import bart.model.dependency.Dependency;
import bart.model.errorgenerator.VioGenQuery;
import bart.utility.DependencyUtility;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.VioGenQueryNodeNotify;
import it.unibas.bartgui.resources.R;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.actions.PropertiesAction;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class VioGenQueryNode extends AbstractNode   {
    
    private ChangeListener listener;
    
    public VioGenQueryNode(VioGenQuery vio,Dependency dc,EGTask egt,EGTaskDataObjectDataObject dto) {
        super(Children.LEAF,
                new ProxyLookup(Lookups.fixed(egt,dto,dc,vio),dto.getAbstractLookup()));
        setName(NodeResource.NODE_VioGenQueryNode);
        setIconBaseWithExtension(R.IMAGE_NODE_ARROW); 
        VioGenQueryNodeNotify.addChangeListener(listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                fireDisplayNameChange(null, "");
                VioGenQueryNode.this.setSheet(createSheet());
            }
        });
    }  
    
    @Override
    public String getHtmlDisplayName() {
        EGTask task = getLookup().lookup(EGTask.class);
        VioGenQuery vio = getLookup().lookup(VioGenQuery.class);
        
        if (vio == null || task == null ) return null;
        
        StringBuilder sb = new StringBuilder();
        //sb.append(R.HTML_Hint);
        //sb.append("Comp: ");
        //sb.append(R.HTML_CL_Hint);
        sb.append(R.HTML_Node);
        sb.append(vio.getVioGenComparison());
        sb.append(R.HTML_CL_Node);
        sb.append(getPercentage(vio, task));
        return sb.toString();
    }
    
    private String invertComparison(VioGenQuery vio)   {
        String invertedOperator = DependencyUtility.invertOperator(vio.getVioGenComparison().getOperator());
        StringBuilder stringExpression = new StringBuilder();
        stringExpression.append(vio.getVioGenComparison().getLeftArgument());
        stringExpression.append(" ");
        stringExpression.append(invertedOperator);
        stringExpression.append(" ");
        stringExpression.append(vio.getVioGenComparison().getRightArgument());
        return stringExpression.toString();
    }
    
    
    private String getPercentage(VioGenQuery vio,EGTask task)   {
        String comp = invertComparison(vio);
        for(String k : task.getConfiguration().getVioGenQueryProbabilities().keySet())   {
            if(k.contains(comp) && k.contains(vio.getDependency().getId().trim()))   {
                return R.HTML_Prop+"  "+task.getConfiguration().getVioGenQueryProbabilities().get(k)+"%"+R.HTML_CL_Prop;
            }
        }
        return "";
    }
    
    private String getStrategy(VioGenQuery vio,EGTask task)   {
        String comp = invertComparison(vio);
        for(String k : task.getConfiguration().getVioGenQueryStrategy().keySet())   {
            if(k.contains(comp) && k.contains(vio.getDependency().getId().trim()))   {
                return task.getConfiguration().getVioGenQueryStrategy().get(k);
            }
        }
        return "";
    }

    @Override
    protected Sheet createSheet() {
         Sheet sheet = Sheet.createDefault(); 
         Sheet.Set set = Sheet.createPropertiesSet(); 
         final VioGenQuery vio = getLookup().lookup(VioGenQuery.class); 
         final EGTask task = getLookup().lookup(EGTask.class); 
         
            Property dcId = new PropertySupport.ReadOnly<String>("idVIo", String.class, "ID :", "")   {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                     return vio.getDependency().getId();
                }
            };dcId.setValue("htmlDisplayValue",R.HTML_Prop+vio.getDependency().getId()+R.HTML_CL_Prop);
            dcId.setValue("suppressCustomEditor", Boolean.TRUE);
            
            Property comparison = new PropertySupport.ReadOnly<String>("comparison", String.class, "Comparison :", "") {
                
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return vio.getVioGenComparison()+"";
                }
            };comparison.setValue("htmlDisplayValue",R.HTML_Prop+vio.getVioGenComparison()+R.HTML_CL_Prop);
            comparison.setValue("suppressCustomEditor", Boolean.TRUE);
            
            Property percentage = new PropertySupport.ReadOnly<String>("percentage", String.class, "Percentage :", "") {        
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    String perc = getPercentage(vio, task);
                    if(!perc.isEmpty())   {
                        return perc;
                    }else{
                       return task.getConfiguration().getDefaultVioGenQueryConfiguration().getPercentage()+"";
                    }
                }
            };
            try{percentage.setValue("htmlDisplayValue",percentage.getValue());}catch(Exception ex){}
            percentage.setValue("suppressCustomEditor", Boolean.TRUE);
            
            Property strategy = new PropertySupport.ReadOnly<String>("strategy", String.class, "Strategy :", "") {        
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    String strategy = getStrategy(vio, task);
                    if(!strategy.isEmpty())   {
                        return strategy;
                    }else{
                        return task.getConfiguration().getDefaultVioGenQueryConfiguration().getQueryExecutor();
                    }                
                }
            };
            try{strategy.setValue("htmlDisplayValue",getStrategy(vio, task));}catch(Exception ex){}
            strategy.setValue("suppressCustomEditor", Boolean.TRUE);
            
         set.put(dcId);
         set.put(comparison);
         set.put(percentage); 
         set.put(strategy);
         sheet.put(set);
         return sheet;
    }
    
    
     @Override
    public Action[] getActions(boolean context) {
        Action[] result = new Action[]{
            Actions.forID("DependencyNode", 
                    "it.unibas.bartgui.controlegt.actions.node.DependenciesNode.Open"),
            null,
            Actions.forID("DependenciesNode", "it.unibas.bartgui.controlegt.actions.node.DependenciesNode.Edit"),
            null,
            SystemAction.get(PropertiesAction.class),
            };
        return result;
    }

    @Override
    public Action getPreferredAction() {
        return Actions.forID("DependencyNode", 
                    "it.unibas.bartgui.controlegt.actions.node.DependenciesNode.Open");
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
    public boolean canDestroy() {
        return false;
    }

    @Override
    public boolean canRename() {
        return false;
    }  

    @Override
    protected void finalize() throws Throwable {
        VioGenQueryNodeNotify.removeChangeListener(listener);
        super.finalize();
    } 
}