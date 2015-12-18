package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.AutoritSrcsNodeFactNotifier;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Utilities;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class AuthoritativeSourcesNodeFactory extends ChildFactory.Detachable<String> implements NodeListener {

    private ChangeListener listener;
    private EGTaskDataObjectDataObject dto;
    private EGTask egt;

    public AuthoritativeSourcesNodeFactory(EGTaskDataObjectDataObject dto, EGTask egt) {
        this.dto = dto;
        this.egt = egt;
    }
    
    @Override
    protected boolean createKeys(List<String> toPopulate) {
        if(egt.getAuthoritativeSources() != null)   {
            toPopulate.addAll(egt.getAuthoritativeSources());
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        AbstractNode node = new AuthoritativeSourceNode(egt,dto,key);
        node.addNodeListener(this);
        return node;
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        String autoritaiveSource = ev.getNode().getLookup().lookup(String.class);
        if(autoritaiveSource != null)   {
            boolean esito = egt.getAuthoritativeSources().remove(autoritaiveSource);
            if(esito)dto.setEgtModified(true);
        }
        refresh(true);
    }

    @Override
    protected void removeNotify() {
        AutoritSrcsNodeFactNotifier.removeChangeListener(listener);
    }

    @Override
    protected void addNotify() {
        AutoritSrcsNodeFactNotifier.addChangeListener(listener = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                AuthoritativeSourcesNodeFactory.this.refresh(true);
            }
        });
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
        
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
        
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
    }
  
}
