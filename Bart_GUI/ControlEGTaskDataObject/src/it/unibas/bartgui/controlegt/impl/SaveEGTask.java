package it.unibas.bartgui.controlegt.impl;

import bart.model.EGTask;
import bart.model.EGTaskConfiguration;
import bart.model.OutlierErrorConfiguration;
import bart.model.errorgenerator.operator.valueselectors.IDirtyStrategy;
import bart.model.errorgenerator.operator.valueselectors.TypoAddString;
import bart.model.errorgenerator.operator.valueselectors.TypoAppendString;
import bart.model.errorgenerator.operator.valueselectors.TypoRandom;
import bart.model.errorgenerator.operator.valueselectors.TypoRemoveString;
import bart.model.errorgenerator.operator.valueselectors.TypoSwitchValue;
import it.unibas.bartgui.controlegt.OutputWindow;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.api.ISave;
import it.unibas.bartgui.view.panel.run.BusyDialog;
import it.unibas.centrallookup.CentralLookup;
import java.awt.Dialog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import speedy.SpeedyConstants;
import speedy.model.database.AttributeRef;
import speedy.model.database.EmptyDB;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.persistence.file.CSVFile;
import speedy.persistence.file.IImportFile;
import speedy.persistence.file.XMLFile;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.xml.operators.TransformFilePaths;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@NbBundle.Messages({
    "# {0} - file name",
    "MSG_SaveEGTask_OK=File {0} Saved",
    "# {0} - file name",
    "MSG_SaveEGTask_Failed=File {0} Save FAIL!!!"
})
@ServiceProvider(service = ISave.class)
public class SaveEGTask implements ISave   {

    private static Logger log;
    static{
        log = Logger.getLogger(SaveEGTask.class.getName());
        log.setLevel(Level.INFO);
    }
    ///////////////////// DATABASE
    private static final String DB_TYPE_MAINMEMORY = "XML";
    private static final String DB_TYPE_MAINMEMORY_GENERATE = "GENERATE";
    private static final String DB_TYPE_DBMS = "DBMS";
    private final TransformFilePaths transformFilePaths = new TransformFilePaths();
    
    private EGTaskDataObjectDataObject dto;
    private EGTask egt;
    private boolean esito=false;

    
    @Override
    public void save() throws IOException {
        dto = CentralLookup.getDefLookup().lookup(EGTaskDataObjectDataObject.class);
        final InputOutput io = IOProvider.getDefault().getIO(dto.getPrimaryFile().getName(), false);
        io.select();
        OutputWindow.openOutputWindowStream(io.getOut(), io.getErr());
        final Dialog d = BusyDialog.getBusyDialog();
        RequestProcessor.Task T = RequestProcessor.getDefault().post(new SaveEgtaskRunnable());
        T.addTaskListener(new TaskListener() {

            @Override
            public void taskFinished(Task task) {
//                d.setVisible(false);
                if(esito)   {
                    System.out.println(Bundle.MSG_SaveEGTask_OK(dto.getPrimaryFile().getName()));
                }else{
                    System.err.println(Bundle.MSG_SaveEGTask_Failed(dto.getPrimaryFile().getName()));                  
                }
                OutputWindow.closeOutputWindowStream(io.getOut(), io.getErr());
//                d.setVisible(false);
            }
            
        });
//        d.setVisible(true);
    }   

     private class SaveEgtaskRunnable implements Runnable   {

        @Override
        public void run() {
            egt = dto.getEgtask();
            FileLock lock = null;
            try{
                log.log(Level.FINE,"File name {0}",dto.getPrimaryFile().getName());
                lock = dto.getPrimaryFile().lock();
                log.fine("Lock acquired");
                            
                Element rootElement = new Element("task");
                log.log(Level.FINE, "RootElemet {0}", rootElement.getName());
                
                //Database Source
                IDatabase dbSource = egt.getSource();
                log.log(Level.FINE, "EGTask DB Source == null -> {0}", (dbSource==null));
                if(!((dbSource == null) || (dbSource instanceof EmptyDB)))   {
                    saveDB(dbSource, rootElement, "source");
                }
                
                //Database Target
                IDatabase dbTarget = egt.getTarget();
                log.log(Level.FINE, "EGTask DB Target == null -> {0}", (dbTarget==null));
                if(!((dbTarget == null) || (dbTarget instanceof EmptyDB)))   {
                    saveDB(dbTarget, rootElement, "target");
                }
                
                //DEPENDENCIES
                saveDependencies(rootElement);
                
                //AUTHORITATIVE SOURCE
                saveAutoritativeSource(rootElement);
                
                //CONFIGURATION
                saveConfiguration(rootElement);
                
                Document doc = new Document(rootElement);
                saveDOM(doc, FileUtil.toFile(dto.getPrimaryFile()));
                log.log(Level.FINE,"File {0} saved",dto.getPrimaryFile().getName());
                esito = true;
            }catch(IOException ioex)   {
                esito = false;
            }finally{
                if(lock != null)   {
                    lock.releaseLock();
                    log.fine("Lock released");
                }
            }
        }    
    }
    
    
    private void saveDOM(Document doc,File file) throws IOException  {
        BufferedWriter out = null;
        FileWriter fw = null;
        try{
            fw = new FileWriter(file);
            out = new BufferedWriter(fw);
            XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
            xmlOut.output(doc, out);
        }catch(IOException ioex)   {
            log.log(Level.SEVERE, "saveDOM", ioex);
            throw ioex;
        }finally{
            if(fw != null)   {
                fw.close();
            }
            if(out != null)   {
                out.close();
            }         
            log.fine("SaveDOM close resource");
        }
    }
    
    private void saveDB(IDatabase db, Element rootElement,String from)   {
        Element dbElement = new Element(from);
        log.log(Level.FINE, "dbElement  {0}", from);
        if(db instanceof DBMSDB)   {
            DBMSDB dbmsdb = (DBMSDB)db;
            log.log(Level.FINE,"DB {0} instanceof {1}",new Object[]{from,DB_TYPE_DBMS});
      
            Element typeElement = new Element("type");
            typeElement.setText(DB_TYPE_DBMS);
            dbElement.addContent(typeElement);
                
            AccessConfiguration acc = dbmsdb.getAccessConfiguration();
            Element accssElement = new Element("access-configuration");
                    accssElement.addContent(new Element("driver").setText(acc.getDriver()));
                    accssElement.addContent(new Element("uri").setText(acc.getUri()));
                    accssElement.addContent(new Element("schema").setText(acc.getSchemaName()));
                    accssElement.addContent(new Element("login").setText(acc.getLogin()));
                    accssElement.addContent(new Element("password").setText(acc.getPassword()));
            dbElement.addContent(accssElement);
                
            String initDbScript = dbmsdb.getInitDBConfiguration().getInitDBScript();
            if((initDbScript != null) && (!initDbScript.isEmpty()))   {
                    Element initdbScriptElement = new Element("init-db");
                    initdbScriptElement.addContent(new CDATA(initDbScript));
                    dbElement.addContent(initdbScriptElement);
            }
            
            if(dbmsdb.getInitDBConfiguration().hasFilesToImport())     {
                Element importElemet = new Element("import");
                importElemet.setAttribute("createTables", dbmsdb.getInitDBConfiguration().isCreateTablesFromFiles()+"");
                Iterator<String> tables = dbmsdb.getInitDBConfiguration().getTablesToImport().iterator();
                while(tables.hasNext())   {
                    String table = tables.next();
                    List<IImportFile> files = dbmsdb.getInitDBConfiguration().getFilesToImport(table);
                    for(IImportFile file : files)   {
                        Element inputElemet = new Element("input");
                        File basefile = FileUtil.toFile(dto.getPrimaryFile());
                        String path = transformFilePaths
                                .relativize(basefile.getAbsolutePath(), file.getFileName());
                        inputElemet.setText(path);
                        inputElemet.setAttribute("table", table);
                        if(file instanceof XMLFile)   {                        
                            inputElemet.setAttribute("type", SpeedyConstants.XML);                      
                        }
                        if(file instanceof CSVFile)   {
                            CSVFile tmp = (CSVFile)file;
                            inputElemet.setAttribute("type", SpeedyConstants.CSV);   
                            inputElemet.setAttribute("separator", tmp.getSeparator()+"");
                            inputElemet.setAttribute("quoteCharacter", tmp.getQuoteCharacter()+"");
                        }
                        importElemet.addContent(inputElemet);
                    }
                }
                dbElement.addContent(importElemet);
            }           
        }
        if(db instanceof MainMemoryDB)   {
            log.log(Level.FINE,"DB {0} instanceof MainMemoryDB",from);
            boolean isGenerate = false;
            boolean isSource = false;
            if(from.equals("source"))isSource = true;
            if(isSource)  {
                isGenerate = dto.isMainMemoryGenerateSource();
            }else{
                isGenerate = dto.isMainMemoryGenerateTager();
            }
            if(isGenerate)   {
                log.log(Level.FINE,"DB {0} is MainMemory Generate",from);
                Element typeElement = new Element("type");
                typeElement.setText(DB_TYPE_MAINMEMORY_GENERATE);
                dbElement.addContent(typeElement);
                
                Element generateElement = new Element("generate");
                if(isSource)   {
                    generateElement.addContent(new CDATA(dto.getPlainInstanceGenerateSourceDB()));
                }else{
                    generateElement.addContent(new CDATA(dto.getPlainInstanceGenerateTargetDB()));
                }
                dbElement.addContent(generateElement);
            }else{
                log.log(Level.FINE,"DB {0} is MainMemory",from);
                Element typeElement = new Element("type");
                typeElement.setText(DB_TYPE_MAINMEMORY);
                dbElement.addContent(typeElement);
                    
                Element xmlElement = new Element("xml");
                    
            }
        }
        rootElement.addContent(dbElement);
    }
    
    private void saveAutoritativeSource(Element rootElement)   {
        List<String> list = egt.getAuthoritativeSources();
        if(list == null)return; 
        if(list.isEmpty())return;
        Element authoritativeSourcesElemet = new Element("authoritativeSources");
            for(String s : list)   {
                authoritativeSourcesElemet.addContent(new Element("source").setText(s));
            }
        rootElement.addContent(authoritativeSourcesElemet);
    }
    
    private void saveDependencies(Element rootElement)   {
        String dependencies = dto.getDependencies();
        if(dependencies == null)return;
        if(dependencies.isEmpty())return;
        Element dependenciesElement = new Element("dependencies");       
        dependenciesElement.addContent(new CDATA(dependencies));
        rootElement.addContent(dependenciesElement);
    }
    
    private void saveConfiguration(Element rootElement)   {
        EGTaskConfiguration conf = egt.getConfiguration();
        Element configurationElement = new Element("configuration");
            configurationElement.addContent(new Element("printLog").setText(conf.isPrintLog()+""));
            configurationElement.addContent(new Element("checkChanges").setText(conf.isCheckChanges()+""));
            configurationElement.addContent(new Element("recreateDBOnStart").setText(conf.isRecreateDBOnStart()+""));
            configurationElement.addContent(new Element("applyCellChanges").setText(conf.isApplyCellChanges()+""));
            configurationElement.addContent(new Element("estimateRepairability").setText(conf.isEstimateRepairability()+""));
            createExportCellChangesElement(configurationElement, conf);
            createExportDirtyDBElement(configurationElement, conf);
            configurationElement.addContent(new Element("useDeltaDBForChanges").setText(conf.isUseDeltaDBForChanges()+""));
            configurationElement.addContent(new Element("cloneTargetSchema").setText(conf.isCloneTargetSchema()+""));
            configurationElement.addContent(new Element("cloneSuffix").setText(conf.getCloneSuffix()));
            configurationElement.addContent(new Element("avoidInteractions").setText(conf.isAvoidInteractions()+""));
            //confElm.addContent(new Element("generateAllChanges").setText(conf.isGenerateAllChanges()+""));

            Element errorPercentagesElement = createErrorPercentagesElement(conf);
            if(errorPercentagesElement != null)configurationElement.addContent(errorPercentagesElement);
            
            Element dirtyStrategiesElement = createDirtyStrategiesElement(conf);
            if(dirtyStrategiesElement != null)configurationElement.addContent(dirtyStrategiesElement);
            
            Element randomErrorsElement = createRandomErrorsElement(conf);
            if(randomErrorsElement != null)configurationElement.addContent(randomErrorsElement);
            
            Element outlierErrorsElement = createOutlierErrorsElement(conf);
            if(outlierErrorsElement != null)configurationElement.addContent(outlierErrorsElement);
       
        rootElement.addContent(configurationElement);
    }
    
    private Element createDirtyStrategiesElement(EGTaskConfiguration conf)    {
        Element dirtyStrategiesElement = null;
        IDirtyStrategy defaultDirtyStrategy = conf.getDefaultDirtyStrategy();
        if(defaultDirtyStrategy != null)   {
            dirtyStrategiesElement = new Element("dirtyStrategies");
                Element defaultDirtyStrategyElement = new Element("defaultStrategy");
                defaultDirtyStrategyElement.addContent(createStrategy(defaultDirtyStrategy));
            dirtyStrategiesElement.addContent(defaultDirtyStrategyElement);
            
            Map<AttributeRef,IDirtyStrategy> map =conf.getDirtyStrategiesMap();
            if((map != null)&&(!map.isEmpty()))   {
                Element attributeStrategyElemet = new Element("attributeStrategy");
                    Iterator<AttributeRef> itAtt = map.keySet().iterator();
                    while(itAtt.hasNext())   {
                        AttributeRef attributeRef = itAtt.next();
                        Element attributeElement = new Element("attribute");
                            attributeElement.setAttribute("table", attributeRef.getTableName());
                            attributeElement.setAttribute("name",attributeRef.getName());
                            attributeElement.addContent(createStrategy(map.get(attributeRef)));
                        attributeStrategyElemet.addContent(attributeElement);
                    }
                dirtyStrategiesElement.addContent(attributeStrategyElemet);
            }
        }
        return dirtyStrategiesElement;
    }
    
    private Element createStrategy(IDirtyStrategy ds)   {
        Element strategyElement = new Element("strategy");
        if(ds instanceof TypoAddString)   {
            TypoAddString tmp = (TypoAddString)ds;
            strategyElement.setAttribute("chars", tmp.getChars());
            strategyElement.setAttribute("charsToAdd", tmp.getCharsToAdd()+"");
            strategyElement.setText(IDirtyStrategy.TYPO_ADD_STRING);
        }
        if(ds instanceof TypoAppendString)   {
            TypoAppendString tmp = (TypoAppendString)ds;
            strategyElement.setAttribute("chars", tmp.getChars());
            strategyElement.setAttribute("charsToAdd", tmp.getCharsToAdd()+"");
            strategyElement.setText(IDirtyStrategy.TYPO_APPEND_STRING);            
        }
        if(ds instanceof TypoRandom)   {
            strategyElement.setText(IDirtyStrategy.TYPO_RANDOM);         
        }
        if(ds instanceof TypoRemoveString)   {
            TypoRemoveString tmp = (TypoRemoveString)ds;
            strategyElement.setAttribute("charsToRemove", tmp.getCharsToRemove()+"");
            strategyElement.setText(IDirtyStrategy.TYPO_REMOVE_STRING); 
        }
        if(ds instanceof TypoSwitchValue)    {
            TypoSwitchValue tmp = (TypoSwitchValue)ds;
            strategyElement.setAttribute("charsToSwitch", tmp.getCharsToSwitch()+"");
            strategyElement.setText(IDirtyStrategy.TYPO_SWITCH_VALUE); 
            
        }
        return strategyElement;
    }
    
    private Element createErrorPercentagesElement(EGTaskConfiguration conf)    {
        Element errorPercentagesElement = null;
        Map<String,Double> map = conf.getVioGenQueryProbabilities();
        if((map!=null)&&(!map.isEmpty()))   {
            errorPercentagesElement = new Element("errorPercentages");
            errorPercentagesElement.addContent(new Element("defaultPercentage")
                                    .setText(conf.getDefaultVioGenQueryConfiguration().getPercentage()+""));
            
                Element vioGenQueriesElement = new Element("vioGenQueries");
                Set<String> set = map.keySet();
                Iterator<String> it = set.iterator();
                while(it.hasNext())   {
                    String key = it.next();
                    StringTokenizer tok = new StringTokenizer(key);
                    String id = tok.nextToken(" ");
                    String comp = key.replace(id, "").trim();
                    Element vioGenQueryElement = new Element("vioGenQuery");
                        vioGenQueryElement.setAttribute("id", id);
                            vioGenQueryElement.addContent(new Element("comparison").setText(cleanXmlString(comp)));
                            vioGenQueryElement.addContent(new Element("percentage").setText(map.get(key)+""));
                        vioGenQueriesElement.addContent(vioGenQueryElement);
                    }
                errorPercentagesElement.addContent(vioGenQueriesElement);
            }
        return errorPercentagesElement;
    }
    
    private Element createRandomErrorsElement(EGTaskConfiguration conf)   {
        Element randomErrorsElement = null;
        Set<String> tables = conf.getTablesForRandomErrors();
        if((tables != null)&&(!tables.isEmpty()))   {
            randomErrorsElement = new Element("randomErrors");
                Element tablesElement = new Element("tables");
                    Iterator<String> tablesIterator = tables.iterator();
                    while(tablesIterator.hasNext())   {
                        String name = tablesIterator.next();
                        Element tableElement = new Element("table");
                        tableElement.setAttribute("name",name);
                        tableElement.addContent(new Element("percentage").setText(conf.getPercentageForRandomErrors(name)+""));
                            Element attributesElement = new Element("attributes");
                            Set<String> attributes = conf.getAttributesForRandomErrors(name);
                            Iterator<String> attributesIterator = attributes.iterator();
                            while(attributesIterator.hasNext())   {
                                attributesElement.addContent(new Element("attribute").setText(attributesIterator.next()));
                            }
                            tableElement.addContent(attributesElement);
                        tablesElement.addContent(tableElement);
                        }
                randomErrorsElement.addContent(tablesElement);                   
        }
        return randomErrorsElement;
    }
    
    private Element createOutlierErrorsElement(EGTaskConfiguration conf)    {
        Element outlierErrorsElement = null;
        OutlierErrorConfiguration outConf = conf.getOutlierErrorConfiguration();
            if((outConf != null) && (!outConf.getTablesToDirty().isEmpty()))   {
                outlierErrorsElement = new Element("outlierErrors");
                    Element tablesElement = new Element("tables");
                    Set<String> tables = outConf.getTablesToDirty();
                    Iterator<String> tablesIterator = tables.iterator();
                    while(tablesIterator.hasNext())   {
                        String tableName = tablesIterator.next();
                        Element tableElement = new Element("table");
                        tableElement.setAttribute("name", tableName);
                            Element attributesElement = new Element("attributes");
                            Set<String> attributes = outConf.getAttributesToDirty(tableName);
                            Iterator<String> attributesIterator = attributes.iterator();
                            while(attributesIterator.hasNext())   {
                                String attributeName = attributesIterator.next();
                                Element attributeElement = new Element("attribute");
                                attributeElement.setAttribute("percentage", outConf.getPercentageToDirty(tableName, attributeName)+"");
                                attributeElement.setAttribute("detectable", outConf.isDetectable(tableName, attributeName)+"");
                                attributeElement.setText(attributeName);
                                attributesElement.addContent(attributeElement);
                            }
                        tableElement.addContent(attributesElement);
                    tablesElement.addContent(tableElement);
                    }
            outlierErrorsElement.addContent(tablesElement);
            }
        return outlierErrorsElement;
    }
    
    private void createExportCellChangesElement(Element configurationElement, EGTaskConfiguration conf)   {
        if(conf.isExportCellChanges())   {
            configurationElement.addContent(new Element("exportCellChanges").setText(conf.isExportCellChanges()+""));
            configurationElement.addContent(new Element("exportCellChangesPath").setText(conf.getExportCellChangesPath()));       
        }
    }
    
    private void createExportDirtyDBElement(Element configurationElement, EGTaskConfiguration conf)   {
        if(conf.isExportDirtyDB())   {
            configurationElement.addContent(new Element("exportDirtyDB").setText(conf.isExportDirtyDB()+""));
            configurationElement.addContent(new Element("exportDirtyDBPath").setText(conf.getExportDirtyDBPath())); 
            configurationElement.addContent(new Element("exportDirtyDBType").setText(conf.getExportDirtyDBType())); 
        }
    }
    
    private static String cleanXmlString(String xmlString) {
        String stringCleaned = xmlString;
        stringCleaned.replaceAll("&gt;", ">");
        stringCleaned.replaceAll("&lt;", "<");
        stringCleaned.replaceAll("&quot;", "\"");
        stringCleaned.replaceAll("&apos;", "'");
        stringCleaned.replaceAll("&amp;", "&");     
        return stringCleaned;
    }
}
    
