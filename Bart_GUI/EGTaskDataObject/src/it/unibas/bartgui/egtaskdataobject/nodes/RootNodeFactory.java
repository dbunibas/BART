package it.unibas.bartgui.egtaskdataobject.nodes;

import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.RootNodeNotifier;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class RootNodeFactory extends ChildFactory.Detachable<String> {
                                                        
    private EGTaskDataObjectDataObject dto;
    private ChangeListener listener;
    
    public RootNodeFactory(EGTaskDataObjectDataObject dto) {
        this.dto = dto;
    }
       
    @Override
    protected boolean createKeys(List<String> toPopulate) {
        if(dto.getEgtask() != null)   {
            toPopulate.addAll(Arrays.asList(NodeResource.FACTORY_KEY_RootNodeFactory));
        }
        return true;
    }

    @Override
    protected Node[] createNodesForKey(String key) {
        if(key.equals(NodeResource.FACTORY_KEY_RootNodeFactory))   {
            Node[] n = {new DatabaseConfigurationNode(dto.getEgtask(),dto),
                        new ConfigurationNode(dto.getEgtask(),dto),
                        new DependenciesNode(dto.getEgtask(),dto),
                        new DirtyStrategiesNode(dto.getEgtask(), dto),
                        new RandomErrorsNode(dto.getEgtask(),dto)
                        };
            return n;
        }else{
            return null;
        }
    }  
    
            @Override
    protected void removeNotify() {
        RootNodeNotifier.removeChangeListener(listener);
    }

    @Override
    protected void addNotify() {
        RootNodeNotifier.addChangeListener(listener = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                refresh(true);
            }
        });
    }
}