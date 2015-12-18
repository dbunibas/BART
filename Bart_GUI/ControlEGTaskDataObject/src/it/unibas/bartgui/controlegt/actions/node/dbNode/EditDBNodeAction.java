package it.unibas.bartgui.controlegt.actions.node.dbNode;

import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.view.panel.editor.database.DbmsEditPanel;
import it.unibas.bartgui.view.panel.editor.database.MainMeemoryGENEditPanel;
import it.unibas.bartgui.view.panel.editor.database.MainMemoryEditPanel;
import it.unibas.bartgui.view.panel.editor.database.SeletcTypeDBPanel;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JButton;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import speedy.model.database.EmptyDB;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.persistence.relational.AccessConfiguration;


@ActionID(
        category = "DBNode",
        id = "it.unibas.bartgui.controlegt.actions.node.dbNode.EditDBNodeAction"
)
@ActionRegistration(
        displayName = "#CTL_EditDBNodeAction"
)
@Messages({"CTL_EditDBNodeAction=Edit",
           "TITLE_DIALOG_DBMSDB=DBMS Configuration Settings",
           "TITLE_DIALOG_MainMemory=MainMemory Configuration Settings",
           "TITLE_DIALOG_MainMemoryGenerate=MainMemory Generate Plain instance"})
public final class EditDBNodeAction implements ActionListener {

    private IDatabase database;
    private final EGTaskDataObjectDataObject dto;
    private final String dbmsT;
    private DbmsEditPanel panelDBMS = null;
    private MainMemoryEditPanel panelMainMem = null;
    private MainMeemoryGENEditPanel panelMainMenGen = null;
    
    public EditDBNodeAction(EGTaskDataObjectDataObject dto) {
        this.dto = dto;      
        dbmsT = Utilities.actionsGlobalContext().lookup(String.class);
        if((dbmsT != null) && (dto != null))   {
            if(dbmsT.equals("Source"))database = dto.getEgtask().getSource();
            if(dbmsT.equals("Target"))database = dto.getEgtask().getTarget();
            if(dbmsT.equals("Dirty"))database = dto.getEgtask().getDirtyTarget();
        }else{
            database = null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent ev) { 
        if(dbmsT == null || dto == null || database == null)return;
        if(database instanceof EmptyDB)   {
            SeletcTypeDBPanel panel = new SeletcTypeDBPanel();
            Object result = DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Confirmation(panel,
                                                "", 
                                                NotifyDescriptor.OK_CANCEL_OPTION, 
                                                NotifyDescriptor.QUESTION_MESSAGE)
            );
            if(result.equals(NotifyDescriptor.CANCEL_OPTION))return;
            if(result.equals(NotifyDescriptor.OK_OPTION))   {
                
                if(panel.getDbmsdbRadioButton().isSelected()) {
                    panelDBMS = new DbmsEditPanel();
                    initButtonDBMS(panelDBMS.getButtons(),panelDBMS,dbmsT,dto);
                    Dialog d = createDialog(panelDBMS, panelDBMS.getButtons());
                    d.setTitle(Bundle.TITLE_DIALOG_DBMSDB());
                    d.setVisible(true);
                    return;
                }
                
                if(panel.getMainMemoryRadioButton().isSelected()) {
                    panelMainMem = new MainMemoryEditPanel();
                    initButtonMainMemory(panelMainMem.getButtons(),panelMainMem,dbmsT,dto);
                    Dialog d = createDialog(panelMainMem, panelMainMem.getButtons());
                    d.setTitle(Bundle.TITLE_DIALOG_MainMemory());
                    d.setVisible(true);
                    return;
                }
                
                if(panel.getMainMemGenerateRadioButton().isSelected()) {
                    panelMainMenGen = new MainMeemoryGENEditPanel();
                    initButtonMainMemoryGenerate(panelMainMenGen.getButtons(),panelMainMenGen,dbmsT,dto);
                    Dialog d = createDialog(panelMainMenGen, panelMainMenGen.getButtons());
                    d.setTitle(Bundle.TITLE_DIALOG_MainMemoryGenerate());
                    d.setVisible(true);
                    return;
                }
            }
            return;
        }
        if(database instanceof DBMSDB) {
            DBMSDB db = (DBMSDB)database;
            panelDBMS = new DbmsEditPanel();
            initButtonDBMS(panelDBMS.getButtons(),panelDBMS,dbmsT,dto);
            AccessConfiguration acc = db.getAccessConfiguration();
            if(acc != null)   {
                panelDBMS.getPanelAcc().getDriverTextField().setText(acc.getDriver());
                panelDBMS.getPanelAcc().getUriTextField().setText(acc.getUri());
                panelDBMS.getPanelAcc().getSchemaTextField().setText(acc.getSchemaName());
                panelDBMS.getPanelAcc().getLoginTextField().setText(acc.getLogin());
                panelDBMS.getPanelAcc().getPasswordTextField().setText(acc.getPassword());
            }
            if(db.getInitDBConfiguration().getInitDBScript() != null)  {
                panelDBMS.getPanelInitDb().getInitDbCheckBox().doClick();
                panelDBMS.getPanelInitDb().getScriptArea().setText(db.getInitDBConfiguration().getInitDBScript());
            }
            panelDBMS.getPanelImport().setCreatetable(db.getInitDBConfiguration().isCreateTablesFromFiles());
            panelDBMS.getPanelImport().setDataListModel(db.getInitDBConfiguration());
            Dialog d = createDialog(panelDBMS, panelDBMS.getButtons());
            d.setTitle(Bundle.TITLE_DIALOG_DBMSDB());
            d.setVisible(true);
            return;
        }
        
        if(database instanceof MainMemoryDB)   { 
            if(dbmsT.equals("Source") && dto.isMainMemoryGenerateSource())  {
                String plainInstance = dto.getPlainInstanceGenerateSourceDB();
                panelMainMenGen = new MainMeemoryGENEditPanel();
                initButtonMainMemoryGenerate(panelMainMenGen.getButtons(),panelMainMenGen,dbmsT,dto);
                panelMainMenGen.getMemoryGeneratePanel().getPlainInstanceArea().setText(plainInstance);
                Dialog d = createDialog(panelMainMenGen, panelMainMenGen.getButtons());
                d.setTitle(Bundle.TITLE_DIALOG_MainMemoryGenerate());
                d.setVisible(true);
            }else if(dbmsT.equals("Target") && dto.isMainMemoryGenerateTager())   {
                String plainInstance = dto.getPlainInstanceGenerateTargetDB();
                panelMainMenGen = new MainMeemoryGENEditPanel();
                initButtonMainMemoryGenerate(panelMainMenGen.getButtons(),panelMainMenGen,dbmsT,dto);
                panelMainMenGen.getMemoryGeneratePanel().getPlainInstanceArea().setText(plainInstance);
                Dialog d = createDialog(panelMainMenGen, panelMainMenGen.getButtons());
                d.setTitle(Bundle.TITLE_DIALOG_MainMemoryGenerate());
                d.setVisible(true);
            }else{
                String schema = null;
                String instance = null;
                if(dbmsT.equals("Source")){
                    schema = dto.getXmlSchemaFilePathSourceDB();
                    instance = dto.getXmlInstanceFilePathSourceDB();
                }
                if(dbmsT.equals("Target")){
                    schema = dto.getXmlSchemaFilePathTargetDB();
                    instance = dto.getXmlInstanceFilePathTargetDB();
                }
                panelMainMem = new MainMemoryEditPanel();
                initButtonMainMemory(panelMainMem.getButtons(),panelMainMem,dbmsT,dto);
                panelMainMem.getPanelMainMem().getXmlInstanceTextField().setText(instance);
                panelMainMem.getPanelMainMem().getXmlSchemaTextField().setText(schema);
                Dialog d = createDialog(panelMainMem, panelMainMem.getButtons());
                d.setTitle(Bundle.TITLE_DIALOG_MainMemory());
                d.setVisible(true);
                return;
            }
        }
    }
    
    private Dialog createDialog(Object innerPane, Object[] options)   {
         DialogDescriptor dsc = new DialogDescriptor(innerPane, 
                                null, 
                                true, 
                                options, 
                                null,
                                DialogDescriptor.DEFAULT_ALIGN, 
                                HelpCtx.DEFAULT_HELP, 
                                null);
        return DialogDisplayer.getDefault().createDialog(dsc);
    }
    
    private void initButtonDBMS(Object[] obj,DbmsEditPanel panel, String dbmsT, EGTaskDataObjectDataObject dto)   {
        for(Object o : obj)   {
            ((JButton)o).addActionListener(new DBMSDButtonListener(panel, dbmsT, dto));
        }
    }
    
    private void initButtonMainMemory(Object[] obj,MainMemoryEditPanel panel, String dbmsT, EGTaskDataObjectDataObject dto)   {
        for(Object o : obj)   {
            ((JButton)o).addActionListener(new MainMemoryButtonListener(panel, dbmsT, dto));
        }        
    }
    
    private void initButtonMainMemoryGenerate(Object[] obj,MainMeemoryGENEditPanel panel, String dbmsT, EGTaskDataObjectDataObject dto)   {
        for(Object o : obj)   {
            ((JButton)o).addActionListener(new MainMemoryGenerateButtonListener(panel, dbmsT, dto));
        } 
    }
    
    public static void closeDBTopComponent(String name)   {
        Set<TopComponent> set = WindowManager.getDefault().getRegistry().getOpened();
        Iterator<TopComponent> it = set.iterator();
        while(it.hasNext())   {
            TopComponent tmp = it.next();
            if(tmp.getName().equals(name))   {
                tmp.close();
                break;
            }
        } 
    }
}