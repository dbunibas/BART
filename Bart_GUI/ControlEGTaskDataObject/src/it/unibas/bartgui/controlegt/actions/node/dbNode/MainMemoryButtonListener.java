package it.unibas.bartgui.controlegt.actions.node.dbNode;

import bart.persistence.parser.ParserMainMemoryDatabase;
import it.unibas.bartgui.controlegt.OutputWindow;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DatabaseTableNotifier;
import it.unibas.bartgui.egtaskdataobject.notifier.DbNodeNotifyer;
import it.unibas.bartgui.view.panel.editor.database.MainMemoryEditPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import speedy.model.database.IDatabase;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class MainMemoryButtonListener implements ActionListener   {
        
    private MainMemoryEditPanel panel;
    private String dbmsT;
    private EGTaskDataObjectDataObject dto;

    public MainMemoryButtonListener(MainMemoryEditPanel panel, String dbmsT, EGTaskDataObjectDataObject dto) {
        this.panel = panel;
        this.dbmsT = dbmsT;
        this.dto = dto;
    }
    
    
   @Override
    public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equalsIgnoreCase("OK"))   {
                
                String schema = panel.getPanelMainMem().getXmlSchemaTextField().getText();
                String instance = panel.getPanelMainMem().getXmlInstanceTextField().getText();
                IDatabase tmpSource = dto.getEgtask().getSource();               
                IDatabase tmpTarget = dto.getEgtask().getTarget();

                InputOutput io = IOProvider.getDefault().getIO(dto.getPrimaryFile().getName(), false);
                OutputWindow.openOutputWindowStream(io.getOut(), io.getErr());
                try{
                    io.select();
                    ParserMainMemoryDatabase parserMainMemoryDatabase = new ParserMainMemoryDatabase();                   
                    
                    IDatabase newDB = parserMainMemoryDatabase.loadXMLScenario(schema, instance);
                    
                    if(dbmsT.equals("Source"))   {
                        dto.getEgtask().setSource(newDB);
                        dto.getEgtask().getAuthoritativeSources().clear();
                        dto.setXmlInstanceFilePathSourceDB(instance);
                        dto.setXmlSchemaFilePathSourceDB(schema);
                        dto.setMainMemoryGenerateSource(false);
                        dto.setPlainInstanceGenerateSourceDB(null);
                        dto.setEgtModified(true);
                        dto.getEgtask().setTarget(tmpTarget);
                        DbNodeNotifyer.fire();
                        DatabaseTableNotifier.fire();
                        EditDBNodeAction.closeDBTopComponent("Source");
                    }
                    
                    if(dbmsT.equals("Target"))  {                      
                        dto.getEgtask().setTarget(newDB);
                        dto.setXmlInstanceFilePathTargetDB(instance);
                        dto.setXmlSchemaFilePathTargetDB(schema);
                        dto.setMainMemoryGenerateTager(false);
                        dto.setPlainInstanceGenerateTargetDB(null);
                        dto.setEgtModified(true);
                        dto.getEgtask().setSource(tmpSource);
                        DbNodeNotifyer.fire();
                        DatabaseTableNotifier.fire();
                        EditDBNodeAction.closeDBTopComponent("Target");
                    } 
                    
                }catch(Exception ex)   {
                    dto.getEgtask().setSource(tmpSource);
                    dto.getEgtask().setTarget(tmpTarget);
                    DbNodeNotifyer.fire();
                    DatabaseTableNotifier.fire();
                    StringBuilder sb = new StringBuilder();
                    sb.append("CHANGES CANCELED \n");
                    sb.append("Error to \n parserMainMemoryDatabase.loadXMLScenario(schema, instance)\n");
                    sb.append(""+ex);
                    DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message(sb.toString(), 
                                NotifyDescriptor.ERROR_MESSAGE));
                    return;
                }finally{
                    OutputWindow.closeOutputWindowStream(io.getOut(), io.getErr());
                }
            }
        }       
    }