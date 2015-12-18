package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import bart.model.dependency.Dependency;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DependenciesFactoryNotiy;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DependenciesFactory extends ChildFactory.Detachable<Dependency>  {

    private EGTask egt;
    private EGTaskDataObjectDataObject dto;
    private ChangeListener listener;

    public DependenciesFactory(EGTask egt, EGTaskDataObjectDataObject dto) {
        this.egt = egt;
        this.dto = dto;
    }
    

    @Override
    protected boolean createKeys(List<Dependency> toPopulate) {
       toPopulate.addAll(egt.getDCs());
       return true;
    }

    @Override
    protected Node createNodeForKey(Dependency key) {
        return new DependencyNode(egt,dto, key);
    }
    
        @Override
    protected void removeNotify() {
        DependenciesFactoryNotiy.removeChangeListener(listener);
    }

    @Override
    protected void addNotify() {
        DependenciesFactoryNotiy.addChangeListener(listener = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                refresh(true);
            }
        });
    }
    
}