package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.resources.R;
import java.util.Arrays;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class RandomErrorsFactory extends ChildFactory<String>   {

    private EGTask egt;
    private EGTaskDataObjectDataObject dto;
    
    public RandomErrorsFactory(EGTask egt,EGTaskDataObjectDataObject dto) {
        this.egt = egt;
        this.dto = dto;
    }

    @Override
    protected boolean createKeys(List<String> toPopulate) {
        toPopulate.addAll(Arrays.asList(NodeResource.FACTORY_KEY_RandomErrorsFactory));
        return true;
    }

    @Override
    protected Node[] createNodesForKey(String key) {
        if(key.equals(NodeResource.FACTORY_KEY_RandomErrorsFactory))   {
            Node[] n = {
                new RandomErrorNode(egt,dto),
                new OutlierErrorNode(egt,dto),
            };
            return n;
        }
        return null;
    }

}
