package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.NodeResource;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import java.beans.IntrospectionException;
import java.util.Arrays;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class ConfNodeFactory extends ChildFactory<String>   {
    
    private EGTask egt;
    private EGTaskDataObjectDataObject dto;
    
    public ConfNodeFactory(EGTask egt, EGTaskDataObjectDataObject dto) {
        this.egt=egt;
        this.dto = dto;
    }

    
    @Override
    protected boolean createKeys(List<String> toPopulate) {
        toPopulate.addAll(Arrays.asList(NodeResource.FACTORY_KEY_ConfNodeFactory));
        return true;
    }

    @Override
    protected Node[] createNodesForKey(String key) {
        try{
            if(key.equals(NodeResource.FACTORY_KEY_ConfNodeFactory))   {
            Node[] n = {new ConfEGTaskConfNode(egt,dto), 
                        new ConfVioGenQueryCNode(egt,dto)};
            return n;
            }
        }catch(IntrospectionException ine)   {
            
        }
        return null;
    }
}