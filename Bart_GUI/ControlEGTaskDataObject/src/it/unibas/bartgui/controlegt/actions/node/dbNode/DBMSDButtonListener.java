package it.unibas.bartgui.controlegt.actions.node.dbNode;

import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DatabaseTableNotifier;
import it.unibas.bartgui.egtaskdataobject.notifier.DbNodeNotifyer;
import it.unibas.bartgui.view.panel.editor.database.DbmsEditPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.persistence.file.IImportFile;
import speedy.persistence.relational.AccessConfiguration;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
    public class DBMSDButtonListener implements ActionListener   {
        
    private DbmsEditPanel panel;
    private String dbmsT;
    private EGTaskDataObjectDataObject dto;

    public DBMSDButtonListener(DbmsEditPanel panel, String dbmsT, EGTaskDataObjectDataObject dto) {
        this.panel = panel;
        this.dbmsT = dbmsT;
        this.dto = dto;
    }
        
        
    @Override
    public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equalsIgnoreCase("OK"))   {
                
                IDatabase tmpSource = dto.getEgtask().getSource();
                IDatabase tmpTarget = dto.getEgtask().getTarget();
                
                DBMSDB db = null;
                try{
                    AccessConfiguration acc = new AccessConfiguration();
                    acc.setDriver(panel.getPanelAcc().getDriverTextField().getText());
                    acc.setUri(panel.getPanelAcc().getUriTextField().getText());
                    acc.setSchemaName(panel.getPanelAcc().getSchemaTextField().getText());
                    acc.setLogin(panel.getPanelAcc().getLoginTextField().getText());
                    acc.setPassword(panel.getPanelAcc().getPasswordTextField().getText());
                    db = new DBMSDB(acc);
                    if(panel.getPanelInitDb().getInitDbCheckBox().isSelected())   {
                        if(panel.getPanelInitDb().getScriptArea().getText() != null)  {
                            db.getInitDBConfiguration().setInitDBScript(panel.getPanelInitDb().getScriptArea().getText());
                        }
                    }
                    
                    db.getInitDBConfiguration().setCreateTablesFromFiles(panel.getPanelImport().isCreatetable());
                    Map<String,List<IImportFile>> map = panel.getPanelImport().getMapFileImport();
                    Iterator<String> it = map.keySet().iterator();
                    while(it.hasNext())   {
                        String table = it.next();
                        for(IImportFile file : map.get(table))   {
                            db.getInitDBConfiguration().addFileToImportForTable(table, file);
                        }
                    }
                    
                }catch(Exception ex){
                    dto.getEgtask().setSource(tmpSource);
                    dto.getEgtask().setTarget(tmpTarget);
                    DbNodeNotifyer.fire();
                    DatabaseTableNotifier.fire();
                    StringBuilder sb = new StringBuilder();
                    sb.append("CHANGES CANCELED \n");
                    sb.append("Error to \n new DBMSDB(accessConfiguration,Egtask) \n");
                    sb.append(""+ex);
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(sb.toString(), 
                                NotifyDescriptor.ERROR_MESSAGE));
                    return;                                        
                } 
                if(db != null)   {
                    if(dbmsT.equals("Source"))  {   
                        dto.getEgtask().setSource(db);
                        dto.getEgtask().getAuthoritativeSources().clear();
                        dto.setXmlInstanceFilePathSourceDB(null);
                        dto.setXmlSchemaFilePathSourceDB(null);
                        dto.setMainMemoryGenerateSource(false);
                        dto.setPlainInstanceGenerateTargetDB(null);
                        dto.setEgtModified(true);   
                        dto.getEgtask().setTarget(tmpTarget);
                        DbNodeNotifyer.fire();
                        DatabaseTableNotifier.fire();
                        EditDBNodeAction.closeDBTopComponent("Source");
                        
                        
                    }
                    if(dbmsT.equals("Target"))  {                       
                        dto.getEgtask().setTarget(db);
                        dto.setXmlInstanceFilePathTargetDB(null);
                        dto.setXmlSchemaFilePathTargetDB(null);
                        dto.setPlainInstanceGenerateTargetDB(null);
                        dto.setMainMemoryGenerateTager(false);
                        dto.setEgtModified(true);
                        dto.getEgtask().setSource(tmpSource);
                        DbNodeNotifyer.fire();
                        DatabaseTableNotifier.fire();
                        EditDBNodeAction.closeDBTopComponent("Target");
                    }                    
                }
            }
        }   
    

    }
