package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import bart.model.dependency.Dependency;
import bart.model.errorgenerator.VioGenQuery;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.VioGenQueryFactoryNotify;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class VioGenQueryFactory extends ChildFactory.Detachable<VioGenQuery>   {

    private Dependency dc;
    private EGTask egt;
    private EGTaskDataObjectDataObject dto;
    private ChangeListener listener;
    
    public VioGenQueryFactory(Dependency dc,EGTask egt,EGTaskDataObjectDataObject dto) {
        this.dc=dc;
        this.egt = egt;
        this.dto = dto;
    }
    
    @Override
    protected boolean createKeys(List<VioGenQuery> toPopulate) {
        toPopulate.addAll(dc.getVioGenQueries());
        return true;
    }

    @Override
    protected Node createNodeForKey(VioGenQuery key) {
        return new VioGenQueryNode(key,dc, egt, dto);
    }

            @Override
    protected void removeNotify() {
        VioGenQueryFactoryNotify.removeChangeListener(listener);
    }

    @Override
    protected void addNotify() {
        VioGenQueryFactoryNotify.addChangeListener(listener = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                refresh(true);
            }
        });
    }
    

}