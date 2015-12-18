package it.unibas.bartgui.controlegt.actions.node.dbNode;

import bart.persistence.parser.ParserMainMemoryDatabase;
import it.unibas.bartgui.controlegt.OutputWindow;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DatabaseTableNotifier;
import it.unibas.bartgui.egtaskdataobject.notifier.DbNodeNotifyer;
import it.unibas.bartgui.view.panel.editor.database.MainMeemoryGENEditPanel;
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
public class MainMemoryGenerateButtonListener implements ActionListener   {
    
    private MainMeemoryGENEditPanel panel;
    private String dbmsT;
    private EGTaskDataObjectDataObject dto;

    public MainMemoryGenerateButtonListener(MainMeemoryGENEditPanel panel, String dbmsT, EGTaskDataObjectDataObject dto) {
        this.panel = panel;
        this.dbmsT = dbmsT;
        this.dto = dto;
    }
    
    
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equalsIgnoreCase("OK"))   {
                String plainInstance = panel.getMemoryGeneratePanel().getPlainInstanceArea().getText();
                
                IDatabase tmpSource = dto.getEgtask().getSource();
                IDatabase tmpTarget = dto.getEgtask().getTarget();
                
                InputOutput io = IOProvider.getDefault().getIO(dto.getPrimaryFile().getName(), false);
                OutputWindow.openOutputWindowStream(io.getOut(), io.getErr());
                try{
                    io.select();
                    ParserMainMemoryDatabase parserMainMemoryDatabase = new ParserMainMemoryDatabase();
                    IDatabase newDB = parserMainMemoryDatabase.loadPlainScenario(plainInstance);
                    
                    if(dbmsT.equals("Source"))   {
                        dto.getEgtask().setSource(newDB);
                        dto.getEgtask().getAuthoritativeSources().clear();
                        dto.setPlainInstanceGenerateSourceDB(plainInstance);
                        dto.setMainMemoryGenerateSource(true);
                        dto.setXmlInstanceFilePathSourceDB(null);
                        dto.setXmlSchemaFilePathSourceDB(null);
                        dto.setEgtModified(true);
                        dto.getEgtask().setTarget(tmpTarget);
                        DbNodeNotifyer.fire();
                        DatabaseTableNotifier.fire();
                        EditDBNodeAction.closeDBTopComponent("Source");
                    }
                    
                    if(dbmsT.equals("Target"))  {
                        dto.getEgtask().setTarget(newDB);
                        dto.setPlainInstanceGenerateTargetDB(plainInstance);
                        dto.setMainMemoryGenerateTager(true);
                        dto.setXmlInstanceFilePathTargetDB(null);
                        dto.setXmlSchemaFilePathTargetDB(null);
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
                    sb.append("Error to \n parserMainMemoryDatabase.loadloadPlainScenario(plainInstance)\n");
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