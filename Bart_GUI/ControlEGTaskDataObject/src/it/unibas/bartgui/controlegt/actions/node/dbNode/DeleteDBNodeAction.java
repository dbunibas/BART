package it.unibas.bartgui.controlegt.actions.node.dbNode;


import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DataBaseConfigurationNotifier;
import it.unibas.bartgui.egtaskdataobject.notifier.DatabaseTableNotifier;
import it.unibas.bartgui.egtaskdataobject.notifier.DbNodeNotifyer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import speedy.model.database.EmptyDB;
import speedy.model.database.IDatabase;

@ActionID(
        category = "DBNode",
        id = "it.unibas.bartgui.controlegt.actions.node.dbNode.DeleteDBNodeAction"
)
@ActionRegistration(
        displayName = "#CTL_DeleteDBNodeAction"
)
@Messages({"CTL_DeleteDBNodeAction=Delete (Set EmptyDB)",
           "# {0} - DB name",
           "MSG_DeleteNodeConfirm=Are you sure  \n that do you want to Set EmptyDB \n DB: {0}",
           "# {0} - DB name",
           "MSG_EXceptionDBMSDB=Unable to Set EmptyDB {0}",
           "# {0} - DB name",
           "MSG_DatabaseDeleted=Database {0} Set EmptyDB"
})
public final class DeleteDBNodeAction implements ActionListener {

    private IDatabase database;
    private EGTaskDataObjectDataObject dto = null;
    private String dbmsT = null;
    private boolean esito = false;
    private StringBuilder sb = new StringBuilder();
    
    public DeleteDBNodeAction(EGTaskDataObjectDataObject context) {
        dto = context;
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
        if(dbmsT.equals("Source"))   {
            dto.getEgtask().setSource(new EmptyDB());
            dto.getEgtask().getAuthoritativeSources().clear();
            dto.setMainMemoryGenerateSource(false);
            dto.setPlainInstanceGenerateSourceDB(null);
            dto.setXmlInstanceFilePathSourceDB(null);
            dto.setXmlSchemaFilePathSourceDB(null);
            dto.setEgtModified(true);
            DbNodeNotifyer.fire();
            DatabaseTableNotifier.fire();
            EditDBNodeAction.closeDBTopComponent("Source");
        }
        if(dbmsT.equals("Target"))   {
            dto.getEgtask().setTarget(new EmptyDB());
            dto.setMainMemoryGenerateTager(false);
            dto.setPlainInstanceGenerateTargetDB(null);
            dto.setXmlInstanceFilePathTargetDB(null);
            dto.setXmlSchemaFilePathTargetDB(null);
            dto.setEgtModified(true);
            DbNodeNotifyer.fire();
            DatabaseTableNotifier.fire();
            EditDBNodeAction.closeDBTopComponent("Target");
        }
        if(dbmsT.equals("Dirty"))    {
            dto.getEgtask().setDirtyTarget(null);
            DataBaseConfigurationNotifier.fire();
            EditDBNodeAction.closeDBTopComponent("Dirty");
        }
     
    }
}   