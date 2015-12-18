package it.unibas.bartgui.egtaskdataobject.nodes;


import it.unibas.bartgui.egtaskdataobject.notifier.DataBaseConfigurationNotifier;
import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;


/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DataBaseConfigurationFactory extends ChildFactory.Detachable<String>  {

    private EGTask task;
    private EGTaskDataObjectDataObject dto;
    private ChangeListener listener;
    
    
    public DataBaseConfigurationFactory(EGTask task,EGTaskDataObjectDataObject dto) {
        this.task = task;
        this.dto = dto;
    }

    @Override
    protected boolean createKeys(List<String> toPopulate) {
        if(task.getSource() != null){
            toPopulate.add("Source");
        }
        if(task.getTarget() != null){
            toPopulate.add("Target");
        }
        if(task.getDirtyTarget() != null) {
            toPopulate.add("Dirty");
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        if(key.equals("Source")) return new DBNode(task,dto, task.getSource(), key);
        if(key.equals("Target")) return new DBNode(task,dto, task.getTarget(), key);
        if(key.equals("Dirty")) return new DBNode(task,dto, task.getDirtyTarget(), key);
        return null;
    }

    @Override
    protected void removeNotify() {
        DataBaseConfigurationNotifier.removeChangeListener(listener);
    }

    @Override
    protected void addNotify() {
        DataBaseConfigurationNotifier.addChangeListener(listener = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                DataBaseConfigurationFactory.this.refresh(true);
            }
        });
    }
    
}