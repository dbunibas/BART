package it.unibas.bartgui.egtaskdataobject.statistics;

import bart.model.dependency.Dependency;
import bart.model.detection.RepairabilityStats;
import bart.model.errorgenerator.ICellChange;
import bart.model.errorgenerator.VioGenQuery;
import bart.utility.ErrorGeneratorStats;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.settings.ConvertAsJavaBean;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import speedy.model.database.Cell;
import speedy.model.database.IValue;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@SuppressWarnings("unchecked")
@ConvertAsJavaBean
public class Statistic {
    
    private static Logger log = Logger.getLogger(Statistic.class.getName());
    
    private static final String SAVE_CELL_CHANGES = "CellChanges.ser";
    private static final String LOAD_CELL_CHANGES = "CellChanges";
    
    private static final String SAVE_TAB_CHNG = "TabAliasChng.ser";
    private static final String LOAD_TAB_CHNG = "TabAliasChng";
    
    private static final String SAVE_VGQ_TIME = "vioGenQueryTimes.ser";
    private static final String LOAD_VGQ_TIME = "vioGenQueryTimes";
    
    private static final String SAVE_VGQ_ERR = "vioGenQueriesErrors.ser";
    private static final String LOAD_VGQ_ERR = "vioGenQueriesErrors";
    
    private static final String SAVE_VGQ_REP = "vioGenQueriesRepairability.ser";
    private static final String LOAD_VGQ_REP = "vioGenQueriesRepairability";
    
    private static final String SAVE_DEP_REP = "dependencyRepairability.ser";
    private static final String LOAD_DEP_REP = "dependencyRepairability";
    /////////////////////////////
    //@ConvertAsJavaBean
    private String name;
    private String starTime;
    private double timeExecution;
    private String detailedLog;   
    private long numCellChanges;
    private String osArch;
    private String osName;
    private String osVersion;
    private String javaVersion;
    /////////////////////////////
    //Serialized
    private Map<Cell,IValue> cellchanges = null; //FOR TableDataView model highlighter
    private List<String> tableAliasChange = null;//FOR GraphDBView highlighterError
    
    private Map<VGQ_Stat, Long> vioGenQueryTimes = null;//FOR CHart Panel vioGenQueryTimes
    private Map<VGQ_Stat, Long> vioGenQueriesErrors = null;//FOR CHart Panel vioGenQueriesErrors
    
    private Map<VGQ_Stat, Repairability> vioGenQueriesRepairability = null;//FOR CHart Panel vioGenQueriesRepairability
    private Map<String, Repairability> dependencyRepairability = null;//FOR CHart Panel dependencyRepairability
    //////////////////////////////    
    
    public void initCellChanges(Set<ICellChange> set)   {
        if(set.isEmpty())return;
        cellchanges = new HashMap<>();
        tableAliasChange = new ArrayList<>();
        Iterator<ICellChange> it = set.iterator();
        while(it.hasNext())   {
            ICellChange c = it.next();
            cellchanges.put(c.getCell(), c.getNewValue()); 
            StringBuilder sb = new StringBuilder();
            sb.append(c.getCell().getAttributeRef().getTableName());
            sb.append(c.getCell().getAttribute());
            tableAliasChange.add(sb.toString());
        }
        setNumCellChanges(cellchanges.size());
    }
    
    public void initStatisticsData(ErrorGeneratorStats er)   {
        detailedLog = er.toString();
        initMapVioGenQueryTimes(er.getVioGenQueryTimes());
        initMapVioGenQueriesErrors(er.getVioGenErrorsErrors());
        initMapVioGenQueriesRepairability(er.getVioGenQueriesRepairability());
        initMapDependencyRepairability(er.getDependencyRepairability());
    }
    
    private void initMapVioGenQueryTimes(Map<VioGenQuery,Long> map)   {
        if(map.isEmpty())return;
        vioGenQueryTimes = new HashMap<>();
        Iterator<VioGenQuery> it = map.keySet().iterator();
        while(it.hasNext())   {
            VioGenQuery v = it.next();
            vioGenQueryTimes.put(
                    new VGQ_Stat(v.toShortString(),v.getDependency().getId())
                    , map.get(v));
        }
    }
    
    private void initMapVioGenQueriesErrors(Map<VioGenQuery,Long> map)   {
        if(map.isEmpty())return;
        vioGenQueriesErrors = new HashMap<>();
        Iterator<VioGenQuery> it = map.keySet().iterator();
        while(it.hasNext())   {
            VioGenQuery v = it.next();
            vioGenQueriesErrors.put(
                    new VGQ_Stat(v.toShortString(),v.getDependency().getId())
                    , map.get(v));
        }
    }
    
    private void initMapVioGenQueriesRepairability(Map<VioGenQuery,RepairabilityStats> map)   {
        if(map.isEmpty())return;
        vioGenQueriesRepairability = new HashMap<>();
        Iterator<VioGenQuery> it = map.keySet().iterator();
        while(it.hasNext())   {
            VioGenQuery v = it.next();
            RepairabilityStats r = map.get(v);
            vioGenQueriesRepairability.put(
                    new VGQ_Stat(v.toShortString(),v.getDependency().getId()), 
                    new Repairability(r.getMean(), r.getConfidenceInterval()));
        }
    }
    
    private void initMapDependencyRepairability(Map<Dependency,RepairabilityStats> map)   {
        if(map.isEmpty())return;
        dependencyRepairability = new HashMap<>();
        Iterator<Dependency> it = map.keySet().iterator();
        while(it.hasNext())   {
            Dependency d = it.next();
            RepairabilityStats r = map.get(d);
            dependencyRepairability.put(
                    d.getId(), 
                    new Repairability(r.getMean(), r.getConfidenceInterval()));
        }
    }   
    
    public Map<Cell,IValue> getCellChanges(String dtoName)   {
        if(cellchanges != null)return cellchanges;
        cellchanges = (Map<Cell,IValue>)loadData(dtoName,LOAD_CELL_CHANGES);
        return cellchanges;
    }
    
    public void saveCellChangesData(FileObject fo)   {
        if(cellchanges == null)return;
        if(fo == null)return;
        saveData(fo, SAVE_CELL_CHANGES,cellchanges);
        saveTableAttribAliasChange(fo);
    }
    
    public void saveStatisticData(FileObject fo)   {
        saveVioGenQueryTimes(fo);
        saveVioGenQueriesErrors(fo);
        saveVioGenQueriesRepairability(fo);
        saveDependencyRepairability(fo);
    }
    
    public List<String> getTableAliasChange(String dtoName)   {
        if(tableAliasChange != null)return tableAliasChange;
        tableAliasChange = (List<String>)loadData(dtoName, LOAD_TAB_CHNG);
        return tableAliasChange;
    }
    
    private void saveTableAttribAliasChange(FileObject fo)   {
        if(tableAliasChange == null)return;
        if(fo==null)return;
        saveData(fo, SAVE_TAB_CHNG,tableAliasChange);
    }
    
        /**
     * @return the vioGenQueryTimes
     */
    public Map<VGQ_Stat, Long> getVioGenQueryTimes(String dtoName) {
        if(vioGenQueryTimes != null)return vioGenQueryTimes;
        vioGenQueryTimes = (Map<VGQ_Stat,Long>)loadData(dtoName, LOAD_VGQ_TIME);
        return vioGenQueryTimes;
    }
    
    private void saveVioGenQueryTimes(FileObject fo)   {
        if(vioGenQueryTimes == null)return;
        if(fo == null)return;
        saveData(fo, SAVE_VGQ_TIME, vioGenQueryTimes);
    }

    /**
     * @return the vioGenQueriesErrors
     */
    public Map<VGQ_Stat, Long> getVioGenQueriesErrors(String dtoName) {
        if(vioGenQueriesErrors != null)return vioGenQueriesErrors;
        vioGenQueriesErrors = (Map<VGQ_Stat,Long>)loadData(dtoName, LOAD_VGQ_ERR);
        return vioGenQueriesErrors;
    }
    
    private void saveVioGenQueriesErrors(FileObject fo)   {
        if(vioGenQueriesErrors == null)return;
        if(fo == null)return;
        saveData(fo, SAVE_VGQ_ERR, vioGenQueriesErrors);
    }

    /**
     * @return the vioGenQueriesRepairability
     */
    public Map<VGQ_Stat, Repairability> getVioGenQueriesRepairability(String dtoName) {
       if(vioGenQueriesRepairability != null)return vioGenQueriesRepairability;
       vioGenQueriesRepairability = (Map<VGQ_Stat,Repairability>)loadData(dtoName, LOAD_VGQ_REP);
       return vioGenQueriesRepairability;
    }

    private void saveVioGenQueriesRepairability(FileObject fo)   {
        if(vioGenQueriesRepairability == null)return;
        if(fo == null)return;
        saveData(fo, SAVE_VGQ_REP, vioGenQueriesRepairability);
    }
    
    /**
     * @return the dependencyRepairability
     */
    public Map<String, Repairability> getDependencyRepairability(String dtoName) {
        if(dependencyRepairability != null) return dependencyRepairability;
        dependencyRepairability = (Map<String,Repairability>)loadData(dtoName, LOAD_DEP_REP);
        return dependencyRepairability;
    }
    
    private void saveDependencyRepairability(FileObject fo)   {
        if(dependencyRepairability == null)return;
        if(fo == null)return;
        saveData(fo, SAVE_DEP_REP, dependencyRepairability);
    }
    
    private void saveData(FileObject fo,String nameFile,Object toSave)   {
        log.setLevel(Level.INFO);
        ObjectOutputStream os = null;
        FileOutputStream fs = null;
        try{
            log.log(Level.FINE,"File to save -> {0}",nameFile);
            FileObject tmp = fo.createData(nameFile);
            fs = new FileOutputStream(FileUtil.toFile(tmp));
            log.fine("Create FileOutputStream");
            os = new ObjectOutputStream(fs);
            log.fine("Create ObjectOutputStream");
            os.writeObject(toSave);
            log.fine("Data written");
        }catch(Exception ex)   {
            log.log(Level.SEVERE,"saveData",ex);
        }finally{
            try{
                if(os != null)   {
                    os.close();
                    log.fine("ObjectOutputStream closed");
                }
            }catch(Exception ex){
                log.log(Level.SEVERE,"saveData close stream",ex);
            }              
        }
    }
    
    private Object loadData(String dtoName, String dataName)   {
        log.setLevel(Level.INFO);
        log.log(Level.FINE,"Data to load {0}",dataName);
        Object result = null;
        
        StringBuilder path = new StringBuilder();
        path.append("statistics/");
        path.append(dtoName);
        path.append("/");
        path.append(this.getName());
        path.append("Conf");
        
        FileObject confFolder = FileUtil.getConfigFile(path.toString());
        FileObject data = null;
        if(confFolder != null)   {
            log.log(Level.FINE,"confFolder found {0}",path.toString());
            data = confFolder.getFileObject(dataName, "ser");
        }
        if(data != null)   {
            log.log(Level.FINE,"data found {0}",data.getName());
            ObjectInputStream is = null;
            try{
                is = new ObjectInputStream(data.getInputStream());
                result =  is.readObject();  
                log.fine("data writed");
            }catch(Exception ex)   {
                log.log(Level.SEVERE,"Load Data "+dataName,ex);
            }finally{
                try{
                    if(data != null)data.getInputStream().close();
                    if(is!=null)is.close();
                    log.fine("Close Stream");
                }catch(Exception ex){
                    log.fine("Exception in close stream");
                }
            }
        }
        return result;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the osArch
     */
    public String getOsArch() {
        return osArch;
    }

    /**
     * @param osArch the osArch to set
     */
    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    /**
     * @return the osName
     */
    public String getOsName() {
        return osName;
    }

    /**
     * @param osName the osName to set
     */
    public void setOsName(String osName) {
        this.osName = osName;
    }

    /**
     * @return the osVersion
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * @param osVersion the osVersion to set
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * @return the javaVersion
     */
    public String getJavaVersion() {
        return javaVersion;
    }

    /**
     * @param javaVersion the javaVersion to set
     */
    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }
    /**
     * @return the starTime
     */
    public String getStarTime() {
        return starTime;
    }

    /**
     * @param starTime the starTime to set
     */
    
    public void setStarTime(String startTime)   {
        this.starTime = startTime;
    }
    
    public void setStarTime(long starTime) {
        Date d = new Date(starTime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        this.starTime = sdf.format(d);
    }

    /**
     * @return the timeExecution
     */
    public double getTimeExecution() {
        return timeExecution;
    }

    /**
     * @param timeExecution the timeExecution to set
     */
    public void setTimeExecution(double timeExecution) {
        this.timeExecution = timeExecution;
    }

    /**
     * @return the detailedLog
     */
    public String getDetailedLog() {
        return detailedLog;
    }

    /**
     * @param detailedLog the detailedLog to set
     */
    public void setDetailedLog(String detailedLog) {
        this.detailedLog = detailedLog;
    }


    /**
     * @return the numCellChanges
     */
    public long getNumCellChanges() {
        return numCellChanges;
    }

    /**
     * @param numCellChanges the numCellChanges to set
     */
    public void setNumCellChanges(long numCellChanges) {
        this.numCellChanges = numCellChanges;
    }
    
}
