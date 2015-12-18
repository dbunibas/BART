package it.unibas.bartgui.egtaskdataobject.statistics;

import it.unibas.centrallookup.CentralLookup;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class StatisticNodeFactory extends ChildFactory.Detachable<Statistic> implements LookupListener,NodeListener  {
    
    private static Logger log = Logger.getLogger(StatisticNodeFactory.class.getName());
    private Lookup.Result<Statistic> res;

    
    @Override
    protected boolean createKeys(List<Statistic> list) {
        list.addAll(res.allInstances());
        return true;
    }

    @Override
    protected Node createNodeForKey(Statistic key) {
        StatisticNode stat = null;
        try {
            stat = new StatisticNode(key);
            stat.addNodeListener(this);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return stat;
    }
    
    @Override
    protected void addNotify() {
        DataObject dobj = CentralLookup.getDefLookup().lookup(DataObject.class);
        StringBuilder path = new StringBuilder();
        path.append("statistics/");
        path.append(dobj.getPrimaryFile().getName());
        res = Lookups.forPath(path.toString()).lookupResult(Statistic.class);
        res.addLookupListener(this);
    }

    @Override
    protected void removeNotify() {
        //stop listening lookup
        res.removeLookupListener(this);
    }
    
    
    @Override
    public void resultChanged(LookupEvent le) {
        refresh(true);
    }
    
    @Override
    public void nodeDestroyed(NodeEvent ev) {
        log.setLevel(Level.INFO);
        Collection<? extends Statistic> selectedStat = ev.getNode().getLookup().lookupAll(Statistic.class);
        DataObject dobj = CentralLookup.getDefLookup().lookup(DataObject.class);
        if(dobj == null)return;
        
        
        StringBuilder path = new StringBuilder();
        path.append("statistics/");
        path.append(dobj.getPrimaryFile().getName());
        for(Statistic remove : selectedStat)   {
            
            FileObject delStat = null;
            FileObject statFolder = FileUtil.getConfigFile(path.toString());
            if(statFolder != null)   {
                delStat = statFolder.getFileObject(remove.getName(), "settings");
                log.log(Level.FINE,"delStat FO -> {0}",(delStat == null));
            }
            
            StringBuilder delPathConf = new StringBuilder(path.toString());
            delPathConf.append("/");
            delPathConf.append(remove.getName());           
            delPathConf.append("Conf");
            FileObject delConf = FileUtil.getConfigFile(delPathConf.toString());
            log.log(Level.FINE,"delConf FO -> {0}",(delConf == null));
            
            try{
                if(delStat != null)delStat.delete();
            }catch(Exception ex)   {
                log.log(Level.SEVERE,"Exception delete statistic.ser",ex);
                //ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
            }
            try{
                if(delConf != null)delConf.delete();
            }catch(Exception ex)   {
                log.log(Level.SEVERE,"Exception delete folder statistic configuration",ex);
                //ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
            }            
            
        }      
        refresh(true);
    }
    

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
        
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
        
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
 
}

