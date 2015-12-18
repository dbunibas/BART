package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class OutilierErrorTableFactory extends ChildFactory<String>{
    
    private EGTask egt;
    private EGTaskDataObjectDataObject dto;
    
    public OutilierErrorTableFactory(EGTask egt,EGTaskDataObjectDataObject dto) {
        this.egt = egt;
        this.dto = dto;
    }

    @Override
    protected boolean createKeys(List<String> toPopulate) {
        for(String key : egt.getConfiguration().getOutlierErrorConfiguration().getTablesToDirty())   {
            toPopulate.add(key);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        return new OutlierErrorTableNode(egt,dto,key);
    }
}
