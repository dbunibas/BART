package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
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
public class RandomErrorAttributeFactory extends ChildFactory<String>   {
    
    private EGTask egt;
    private EGTaskDataObjectDataObject dto;
    private String tableName;
    
    public RandomErrorAttributeFactory(EGTask egt,EGTaskDataObjectDataObject dto,String tableName) {
        this.egt = egt;
        this.dto = dto;
        this.tableName=tableName;
    }

    @Override
    protected boolean createKeys(List<String> toPopulate) {
        for(String key : egt.getConfiguration().getAttributesForRandomErrors(tableName))   {
            toPopulate.add(key);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        return new RandomErrorAttribute(egt,dto, key);
    }

}
