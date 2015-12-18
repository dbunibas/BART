package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import bart.model.dependency.Dependency;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DependencyNotifier;
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
public class DependencyNode extends AbstractNode  {
    
    private ChangeListener listener;
    
    public DependencyNode(EGTask egt,EGTaskDataObjectDataObject dto,Dependency dc) {
        super(Children.create(new VioGenQueryFactory(dc,egt,dto), true),
                new ProxyLookup(Lookups.fixed(egt,dto,dc),dto.getAbstractLookup()));
        setName(NodeResource.NODE_DependencyNode);
        setIconBaseWithExtension(R.IMAGE_LINK); 
        DependencyNotifier.addChangeListener(listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                fireDisplayNameChange(null, "");
                DependencyNode.this.setSheet(createSheet());
            }
        });
    }

    @Override
    public String getHtmlDisplayName() {
        Dependency dp = getLookup().lookup(Dependency.class);
        if (dp == null) return null;
        StringBuilder s = new StringBuilder();
        //s.append(R.HTML_Hint);
        s.append("ID : ");
        //s.append(R.HTML_CL_Hint);
        s.append(R.HTML_Val);s.append(dp.getId());s.append(R.HTML_CL_Val);
        s.append(R.HTML_Hint);s.append(" - VioGenQuery (");
        s.append(dp.getVioGenQueries().size());s.append(")");
        s.append(R.HTML_CL_Hint);
        return s.toString();
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
    protected Sheet createSheet() {
         Sheet sheet = Sheet.createDefault(); 
         Sheet.Set set = Sheet.createPropertiesSet(); 
         final Dependency dc = getLookup().lookup(Dependency.class);
         Property dependency= new PropertySupport.ReadOnly<String>("dependency", String.class, "Dependency ID: ", "") {
             
             @Override
             public String getValue() throws IllegalAccessException, InvocationTargetException {
                return dc.getId();
             }
         };
         dependency.setValue("htmlDisplayValue",R.HTML_Prop+dc.getId()+R.HTML_CL_Prop);
         dependency.setValue("suppressCustomEditor",Boolean.TRUE);
         
         Property dependencyType= new PropertySupport.ReadOnly<String>("dependencyType", String.class, "Type  : ", "") {
             
             @Override
             public String getValue() throws IllegalAccessException, InvocationTargetException {
                 return dc.getType();
             }
         };
         dependencyType.setValue("htmlDisplayValue",R.HTML_Prop+dc.getType()+R.HTML_CL_Prop);
         dependencyType.setValue("suppressCustomEditor",Boolean.TRUE);

         Property vioGQSize= new PropertySupport.ReadOnly<String>("VioGenQuerySize", String.class, "VioGenQuery Size : ", "") {
             
             @Override
             public String getValue() throws IllegalAccessException, InvocationTargetException {
                 return dc.getVioGenQueries().size()+"";
             }
         };                
         vioGQSize.setValue("htmlDisplayValue",R.HTML_Prop+dc.getVioGenQueries().size()+R.HTML_CL_Prop);
         vioGQSize.setValue("suppressCustomEditor",Boolean.TRUE);

        set.put(dependency);
        set.put(dependencyType);
        set.put(vioGQSize);
        sheet.put(set); 
        return sheet;
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
}