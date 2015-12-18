package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DbNodeNotifyer;
import it.unibas.bartgui.resources.R;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.ErrorManager;
import org.openide.actions.PropertiesAction;
import org.openide.awt.Actions;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import speedy.model.database.EmptyDB;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.mainmemory.MainMemoryDB;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class DBNode extends AbstractNode   {   
    
    private ChangeListener listener;
    
    
    public DBNode(EGTask egt,EGTaskDataObjectDataObject dto,IDatabase db,String dsname) {
        super(Children.create(new DatabaseTableFactory(dto,dsname), true), 
                new ProxyLookup(Lookups.fixed(egt,dto,db,dsname),dto.getAbstractLookup()));
        if(dsname.equals("Source"))setName(NodeResource.NODE_DataBaseSourceNode);
        if(dsname.equals("Target"))setName(NodeResource.NODE_DatabaseTargetNode);
        if(dsname.equals("Dirty"))setName(NodeResource.NODE_DataBaseDirtyTargetNode);
        setIconBaseWithExtension(R.IMAGE_NODE_DBMS); 
        DbNodeNotifyer.addChangeListener(listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                fireDisplayNameChange(null, "");
                DBNode.this.setSheet(createSheet());
            }
        });
    }   

    @Override
    public String getHtmlDisplayName() {       
        IDatabase db = getDB();
        String tipe = getLookup().lookup(String.class);
        
        StringBuilder s = new StringBuilder();
        s.append(R.HTML_Node);
        s.append(tipe);
        s.append(R.HTML_CL_Node);   
        
        if((db!=null) && (db.getName().equals("EMPTY"))) {
            s.append(R.HTML_Hint);
            s.append("  EMPTY");
            s.append(R.HTML_CL_Hint);
        }
        
        if(tipe.equals("Dirty")) {
            s.append(R.HTML_Hint);
            s.append("  Delta DB");
            s.append(R.HTML_CL_Hint);
        }
        return s.toString();
    }
    
    @Override
    public Action[] getActions(boolean context) {
        IDatabase db = getDB();      
        if(db == null)return null;
        if(db instanceof EmptyDB)   {
            Action[] result = new Action[]{
            Actions.forID("DBNode", "it.unibas.bartgui.controlegt.actions.node.dbNode.EditDBNodeAction"),
            null,
            SystemAction.get(PropertiesAction.class),
            };
            return result;
        }
        if(getName().equals(NodeResource.NODE_DataBaseDirtyTargetNode)) {
            Action[] result = new Action[]{
                Actions.forID("DBNode", "it.unibas.bartgui.controlegt.actions.node.dbNode.Open"),
                Actions.forID("DBNode", "it.unibas.bartgui.controlegt.actions.node.dbNode.DeleteDBNodeAction"),
                null,
                SystemAction.get(PropertiesAction.class),
            };
            return result;
        }
        Action[] result = new Action[]{
            Actions.forID("DBNode", "it.unibas.bartgui.controlegt.actions.node.dbNode.Open"),
            Actions.forID("DBNode", "it.unibas.bartgui.controlegt.actions.node.dbNode.EditDBNodeAction"),
            null,
            Actions.forID("DBNode", "it.unibas.bartgui.controlegt.actions.node.dbNode.DeleteDBNodeAction"),
            null,
            SystemAction.get(PropertiesAction.class),
        };
        return result;
    }

    @Override
    public Action getPreferredAction() {
        IDatabase db = getDB(); 
        if(db == null)return null;
        if(db instanceof EmptyDB)   {
            return Actions.forID("DBNode", "it.unibas.bartgui.controlegt.actions.node.dbNode.EditDBNodeAction");
        }
        return Actions.forID("DBNode", "it.unibas.bartgui.controlegt.actions.node.dbNode.Open");
    }
    
    private IDatabase getDB()   {
        IDatabase db = null;
        EGTaskDataObjectDataObject dtoTmp = getLookup().lookup(EGTaskDataObjectDataObject.class);
        String tipe = getLookup().lookup(String.class);
        if(dtoTmp == null || tipe == null)return null;
        
        if(tipe.equals("Source"))db = dtoTmp.getEgtask().getSource();
        if(tipe.equals("Target"))db = dtoTmp.getEgtask().getTarget();
        if(tipe.equals("Dirty"))db = dtoTmp.getEgtask().getDirtyTarget();
         
        return db;
    }
    
    
    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName("dbmsinfo");set.setDisplayName("DB info");
          
        final EGTaskDataObjectDataObject dto = getLookup().lookup(EGTaskDataObjectDataObject.class);
        if(dto == null)return sheet;
        
        final EGTask egt = dto.getEgtask();
        if(egt == null)return sheet;
        
        final String dbmsN = getLookup().lookup(String.class);
        if(dbmsN == null)return sheet;
        
        IDatabase tmpDB = null;
        if(dbmsN.equals("Source"))tmpDB = dto.getEgtask().getSource();
        if(dbmsN.equals("Target"))tmpDB = dto.getEgtask().getTarget();
        if(dbmsN.equals("Dirty"))tmpDB = dto.getEgtask().getDirtyTarget();
        if(tmpDB == null)return sheet;
        
        final IDatabase db = tmpDB;
         
        if(db.getName().equals("EMPTY"))   {
            Property name = new PropertySupport.ReadOnly("name", String.class , "EMPTY DB", "") {             
                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return db.getName();
                }
            };
            name.setValue("htmlDisplayValue",R.HTML_Prop+db.getName()+R.HTML_CL_Prop);
            name.setValue("suppressCustomEditor", Boolean.TRUE);
            set.put(name);
            sheet.put(set); 
            return sheet;
        } 
        
        try{
            if(db instanceof DBMSDB)   {
                Property name = new PropertySupport.Reflection(((DBMSDB)db).getAccessConfiguration(),String.class,"getDatabaseName",null);
                name.setName("name");name.setDisplayName("Name : ");  
                name.setValue("htmlDisplayValue",R.HTML_Prop+name.getValue()+R.HTML_CL_Prop);
                name.setValue("suppressCustomEditor", Boolean.TRUE);
                set.put(name);
            }else{
                Property name = new PropertySupport.Reflection(db,String.class,"getName",null);
                name.setName("name");name.setDisplayName("Name : ");
                setValue("htmlDisplayValue",R.HTML_Prop+name.getValue()+R.HTML_CL_Prop);
                name.setValue("suppressCustomEditor", Boolean.TRUE);
                set.put(name);
            }
            
            if(db instanceof DBMSDB)   {
                final DBMSDB dbms = (DBMSDB)db;
                Sheet.Set set2 = Sheet.createPropertiesSet(); 
                set2.setDisplayName("Access Configuration"); 
                set2.setName("accessConfiguration");
                
                if(dbms.getInitDBConfiguration().getInitDBScript() != null)    {
                    Property script = new PropertySupport.ReadOnly("script", String.class , "Init DB Script :", "") {             
                        @Override
                        public Object getValue() throws IllegalAccessException, InvocationTargetException {
                            return dbms.getInitDBConfiguration().getInitDBScript();
                        }
                    };
                    script.setValue("htmlDisplayValue",R.HTML_Prop+".... open ...."+R.HTML_CL_Prop);
                    set.put(script);
                }
                
                Property type = new PropertySupport.ReadOnly("type", String.class , "Type DB :", "") {             
                    @Override
                    public Object getValue() throws IllegalAccessException, InvocationTargetException {
                        return "DBMS DB";
                    }
                };
                type.setValue("htmlDisplayValue",R.HTML_Prop+"DBMS DB"+R.HTML_CL_Prop);
                type.setValue("suppressCustomEditor", Boolean.TRUE);
                set.put(type);
                
                Property driver = new PropertySupport.Reflection(dbms.getAccessConfiguration(),String.class,"getDriver",null);
                driver.setName("driver");driver.setDisplayName("Driver :");
                driver.setValue("htmlDisplayValue",R.HTML_Prop+driver.getValue()+R.HTML_CL_Prop);
                driver.setValue("suppressCustomEditor", Boolean.TRUE);
                set2.put(driver);
                
                Property uri = new PropertySupport.Reflection(dbms.getAccessConfiguration(),String.class,"getUri",null);
                uri.setName("uri");uri.setDisplayName("URI :");
                uri.setValue("htmlDisplayValue",R.HTML_Prop+uri.getValue()+R.HTML_CL_Prop);
                uri.setValue("suppressCustomEditor", Boolean.TRUE);
                set2.put(uri);
                
                Property login = new PropertySupport.Reflection(dbms.getAccessConfiguration(),String.class,"getLogin",null);
                login.setName("login");login.setDisplayName("Login :");
                login.setValue("htmlDisplayValue",R.HTML_Prop+login.getValue()+R.HTML_CL_Prop);
                login.setValue("suppressCustomEditor", Boolean.TRUE);
                set2.put(login);
                
                Property password = new PropertySupport.Reflection(dbms.getAccessConfiguration(),String.class,"getPassword",null);
                password.setName("password");password.setDisplayName("Password :");
                password.setValue("htmlDisplayValue",R.HTML_Prop+"*********"+R.HTML_CL_Prop);
                //password.setValue("suppressCustomEditor", Boolean.TRUE);
                set2.put(password);
                
                Property schemaName = new PropertySupport.Reflection(dbms.getAccessConfiguration(),String.class,"getSchemaName",null);
                schemaName.setName("schemaName");schemaName.setDisplayName("Schema Name :");
                schemaName.setValue("htmlDisplayValue",R.HTML_Prop+schemaName.getValue()+R.HTML_CL_Prop);
                schemaName.setValue("suppressCustomEditor", Boolean.TRUE);
                set2.put(schemaName);
                
                sheet.put(set);
                sheet.put(set2);
                return sheet;
            }
            
            if(db instanceof MainMemoryDB){
                boolean t = false;
                if(dbmsN.equals("Source")) t = dto.isMainMemoryGenerateSource();
                if(dbmsN.equals("Target")) t = dto.isMainMemoryGenerateTager();
                
                final boolean tmp = t;
                Property type = new PropertySupport.ReadOnly("Type", String.class , "Type DB :", "") {             
                @Override
                    public Object getValue() throws IllegalAccessException, InvocationTargetException {
                        return tmp ? "Main Memory Generate" : "Main Memory";
                    }
                };
                type.setValue("htmlDisplayValue",R.HTML_Prop+(t ? "Main Memory Generate" : "Main Memory")+R.HTML_CL_Prop);
                type.setValue("suppressCustomEditor", Boolean.TRUE);
                set.put(type);
                
                if(t)   {
                    Property plain = new PropertySupport.ReadOnly<String>("plainInstance", String.class, "Plain Instance:", "") {         
                        @Override
                        public String getValue() throws IllegalAccessException, InvocationTargetException {
                            if(dbmsN.equals("Source"))return dto.getPlainInstanceGenerateSourceDB();
                            if(dbmsN.equals("Target"))return dto.getPlainInstanceGenerateTargetDB();
                            return  null;
                        }
                    };
                    plain.setValue("htmlDisplayValue",R.HTML_Prop+".... open ...."+R.HTML_CL_Prop);
                    set.put(plain);
                }                      
                
                sheet.put(set);
                return sheet;
            }
            
            sheet.put(set);
            return sheet;
        }catch(InvocationTargetException ivtgex)   {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,ivtgex); 
        }catch(IllegalAccessException accex)   {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,accex); 
        }catch(NoSuchMethodException nsmex)   {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION,nsmex);           
        } 
        return sheet;
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
        return false;
    }
    
    @Override
    public boolean canDestroy() {
        return false;
    }  

    @Override
    protected void finalize() throws Throwable {
        DbNodeNotifyer.removeChangeListener(listener);
        super.finalize();
    } 
    
}