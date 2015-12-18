package it.unibas.bartgui.egtaskdataobject.statistics;

import it.unibas.bartgui.resources.R;
import it.unibas.centrallookup.CentralLookup;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Action;
import org.openide.ErrorManager;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.RenameAction;
import org.openide.awt.Actions;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.InstanceDataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class StatisticNode extends AbstractNode {
  
    public StatisticNode(Statistic bean) throws IntrospectionException {
        super(Children.LEAF,Lookups.singleton(bean));
        setDisplayName(bean.getName());
        setIconBaseWithExtension(R.IMAGE_PIE_CHART);
    }

    @Override
    public String getHtmlDisplayName() {
        Statistic s = getLookup().lookup(Statistic.class);
        StringBuilder sb = new StringBuilder(R.HTML_Node);
        sb.append(s.getName());
        sb.append(R.HTML_CL_Node);
        sb.append(R.HTML_Hint);
        sb.append("  changes : ");
        sb.append(s.getNumCellChanges());
        sb.append(R.HTML_CL_Hint);
        return sb.toString();
    }
    
    

    @Override
    public Action[] getActions(boolean context) {
        
        Action[] a = {
                      Actions.forID("StatisticNode", "it.unibas.bartgui.controlegt.actions.node.Statistics.Open"),
                      Actions.forID("StatisticNode", "it.unibas.bartgui.controlegt.actions.node.Statistics.Export"),
                      null,
                      SystemAction.get(RenameAction.class),
                      SystemAction.get(DeleteAction.class),
                      null,
                      SystemAction.get(PropertiesAction.class),
                      };
        return a;
    }

    @Override
    public Action getPreferredAction() {
        return Actions.forID("StatisticNode", "it.unibas.bartgui.controlegt.actions.node.Statistics.Open");
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
    public boolean canRename() {
        return true;
    }
    
    @Override
    public String getName() {
        Statistic s = getLookup().lookup(Statistic.class);
        if (null != s.getName()) {
            return s.getName();
        }
        return super.getDisplayName();
    }

    @Override
    public void setName(String newValue) {
        Statistic stat = getLookup().lookup(Statistic.class);
        if(stat == null)return;
        String oldName = stat.getName();
        DataObject dobj = CentralLookup.getDefLookup().lookup(DataObject.class);
        if(dobj == null)return;
        
        StringBuilder path = new StringBuilder();
            path.append("statistics/");
            path.append(dobj.getPrimaryFile().getName());
             
        FileObject remStat = null;
        FileObject statFolder = FileUtil.getConfigFile(path.toString());
        if(statFolder != null)   {
            remStat = statFolder.getFileObject(oldName, "settings");
        }
        if(remStat != null)   {
            try{
                FileObject folder = FileUtil.getConfigFile(path.toString());
                remStat.delete();
                stat.setName(newValue);
                InstanceDataObject.create(DataFolder.findFolder(folder),
                                            stat.getName(),
                                            stat,
                                            null,
                                            true);
            }catch(Exception ex)   {
                
            }
        }
        StringBuilder pathConf = new StringBuilder();
        pathConf.append(path.toString());
        pathConf.append("/");
        pathConf.append(oldName);
        pathConf.append("Conf");
        
        FileObject remConf = FileUtil.getConfigFile(pathConf.toString());
        FileLock lockRemConf = null;
        try{           
            if(remConf != null)   {
                lockRemConf = remConf.lock();
                StringBuilder sb = new StringBuilder(newValue);
                sb.append("Conf");
                remConf.rename(lockRemConf, sb.toString(), null);
            }
        }catch(Exception ex)   {
            ErrorManager.getDefault().notify(ErrorManager.ERROR,ex);
        }finally{
            if(lockRemConf != null)   {
                lockRemConf.releaseLock();
            }
        }
        
        fireDisplayNameChange(oldName, newValue);
    }
    
    
    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public void destroy() throws IOException {
        fireNodeDestroyed();
    }  

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName("statInfo");set.setDisplayName("Statistics Result info");
        Sheet.Set set2 = Sheet.createPropertiesSet();
        set2.setName("systemtInfo");set2.setDisplayName("System info");
        
        final Statistic stat = getLookup().lookup(Statistic.class);
        
        if(stat == null)return sheet;
        
        try{
            //set1
            Property name = new PropertySupport.Reflection(stat,String.class,"getName",null);
            name.setName("name");name.setDisplayName("NAME :");
            StringBuilder sbName = new StringBuilder(R.HTML_Prop);
            sbName.append(stat.getName());
            sbName.append(R.HTML_CL_Prop);
            name.setValue("htmlDisplayValue",sbName.toString());
            name.setValue("suppressCustomEditor", Boolean.TRUE);
            set.put(name);
            
            Property startTime = new PropertySupport.Reflection(stat,String.class,"getStarTime",null);
            startTime.setName("startTime");startTime.setDisplayName("Start Time : ");
            StringBuilder sbStartTime = new StringBuilder(R.HTML_Prop);
            sbStartTime.append(stat.getStarTime());
            sbStartTime.append(R.HTML_CL_Prop);
            startTime.setValue("htmlDisplayValue",sbStartTime.toString());
            startTime.setValue("suppressCustomEditor", Boolean.TRUE);
            set.put(startTime);
            
            Property timeExecution = new PropertySupport.Reflection(stat,double.class,"getTimeExecution",null);
            timeExecution.setName("timeExecution");timeExecution.setDisplayName("Execution Time : ");
            StringBuilder sbTimeExecution = new StringBuilder(R.HTML_Prop);
            sbTimeExecution.append(stat.getTimeExecution());
            sbTimeExecution.append(R.HTML_CL_Prop);
            timeExecution.setValue("htmlDisplayValue",sbTimeExecution.toString());
            timeExecution.setValue("suppressCustomEditor", Boolean.TRUE);
            set.put(timeExecution);        
        
            Property changes = new PropertySupport.ReadOnly("changes", String.class , "Changes : ", "") {             
                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return stat.getNumCellChanges();
                }
            };
            StringBuilder sbchanges = new StringBuilder(R.HTML_Prop);
            sbchanges.append(stat.getNumCellChanges());
            sbchanges.append(R.HTML_CL_Prop);
            changes.setValue("htmlDisplayValue",sbchanges.toString());
            changes.setValue("suppressCustomEditor", Boolean.TRUE);
            set.put(changes);
            
            //Set 2
        
            Property osName = new PropertySupport.Reflection(stat,String.class,"getOsName",null);
            osName.setName("osname");osName.setDisplayName("OS Name : ");
            StringBuilder sbOsName = new StringBuilder(R.HTML_Prop);
            sbOsName.append(stat.getOsName());
            sbOsName.append(R.HTML_CL_Prop);
            osName.setValue("htmlDisplayValue",sbOsName.toString());
            osName.setValue("suppressCustomEditor", Boolean.TRUE);
            set2.put(osName);
            
            Property osArch = new PropertySupport.Reflection(stat,String.class,"getOsArch",null);
            osArch.setName("osArch");osArch.setDisplayName("OS Arch : ");
            StringBuilder sbOsArch = new StringBuilder(R.HTML_Prop);
            sbOsArch.append(stat.getOsArch());
            sbOsArch.append(R.HTML_CL_Prop);
            osArch.setValue("htmlDisplayValue",sbOsArch.toString());
            osArch.setValue("suppressCustomEditor", Boolean.TRUE);
            set2.put(osArch);
            
            Property osVersion = new PropertySupport.Reflection(stat,String.class,"getOsVersion",null);
            osVersion.setName("osVersion");osVersion.setDisplayName("OS Version : ");
            StringBuilder sbosVersion = new StringBuilder(R.HTML_Prop);
            sbosVersion.append(stat.getOsVersion());
            sbosVersion.append(R.HTML_CL_Prop);
            osVersion.setValue("htmlDisplayValue",sbosVersion.toString());
            osVersion.setValue("suppressCustomEditor", Boolean.TRUE);
            set2.put(osVersion);
            
            Property javaVersion = new PropertySupport.Reflection(stat,String.class,"getJavaVersion",null);
            javaVersion.setName("osArch");javaVersion.setDisplayName("Java Version : ");
            StringBuilder sbjavaVersion = new StringBuilder(R.HTML_Prop);
            sbjavaVersion.append(stat.getJavaVersion());
            sbjavaVersion.append(R.HTML_CL_Prop);
            javaVersion.setValue("htmlDisplayValue",sbjavaVersion.toString());
            javaVersion.setValue("suppressCustomEditor", Boolean.TRUE);
            set2.put(javaVersion);

            
        }catch(Exception ex)   {
            return sheet;
        }       
        sheet.put(set);
        sheet.put(set2);
        return sheet;
    }

    
}
