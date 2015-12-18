package it.unibas.bartgui.controlegt.impl;

import bart.model.errorgenerator.ICellChange;
import bart.utility.ErrorGeneratorStats;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.api.ISaveStatistics;
import it.unibas.bartgui.egtaskdataobject.statistics.Statistic;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.InstanceDataObject;
import org.openide.util.lookup.ServiceProvider;



/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@ServiceProvider(service = ISaveStatistics.class)
public class SaveStatistics implements ISaveStatistics  {
    
    private static Logger log = Logger.getLogger(SaveStatistics.class.getName());   
    private String nameData;
      
    @Override
    public void save(EGTaskDataObjectDataObject egtDO, long startTime, double timeEsecution, Set<ICellChange> set,ErrorGeneratorStats er) { 
        log.setLevel(Level.INFO);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        nameData = sdf.format(new Date());
        nameData = nameData.replace(':', '-');
        nameData = nameData.replace('.', '-');  
        log.fine("Create and set base parameter of statistics");
        Statistic stat = new Statistic();
        stat.setName(nameData);
        stat.setStarTime(startTime);
        stat.setTimeExecution(timeEsecution);
        stat.setOsName(System.getProperty("os.name"));
        stat.setOsArch(System.getProperty("os.arch"));
        stat.setOsVersion(System.getProperty("os.version"));
        stat.setJavaVersion(System.getProperty("java.version"));
        stat.initCellChanges(set);
        stat.initStatisticsData(er);
        log.fine("set base parameter of statistics OK");

        try{          
            log.fine("check folder for fileName");
            FileObject statFolder = FileUtil.getConfigFile("statistics");
            
            StringBuilder folderNamePath = new StringBuilder();
            folderNamePath.append("statistics/");
            folderNamePath.append(egtDO.getPrimaryFile().getName());
            
            FileObject folderName = FileUtil.getConfigFile(folderNamePath.toString());
            if(folderName == null)  {
              folderName = statFolder.createFolder(egtDO.getPrimaryFile().getName());
              log.fine("folder fileName create");
            }          
            log.fine("folder filename: "+folderName.getPath());
            
            log.fine("Create folder for list serialized");
            StringBuilder foldStatsName = new StringBuilder(stat.getName());
            foldStatsName.append("Conf");
            FileObject serList = folderName.createFolder(foldStatsName.toString());
            log.fine("FolderCreate -> "+serList.getName());
            
            log.fine("Save Statistics class");
            InstanceDataObject.create(DataFolder.findFolder(folderName),
                                      stat.getName(),
                                      stat,
                                      null,
                                      true);
            log.fine("Save statistics class OK");
            
            stat.saveCellChangesData(serList);
            log.fine("CellChanged Lists serialized OK");

            stat.saveStatisticData(serList);
            log.fine("Save Statistics DATA Ok");
            System.out.println("Save Statistics DATA Ok");
        }catch(Exception ex)   {
            log.log(Level.SEVERE,"Save Statistics ->\n",ex);
            System.err.println("Error in SaveStatistics, unable to save ->\n"+ex.getLocalizedMessage());
        }
    }
    
}