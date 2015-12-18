/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.egtaskdataobject.nodes;

import bart.model.EGTask;
import bart.model.errorgenerator.operator.valueselectors.IDirtyStrategy;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.resources.R;
import java.util.Map;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import speedy.model.database.AttributeRef;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@NbBundle.Messages({
    "HINT_DirtyStrategyTableNode= "
})
public class DirtyStrategyTableNode extends AbstractNode   {

    private String dirtyStrategyTable;
    
    public DirtyStrategyTableNode(EGTask egt, EGTaskDataObjectDataObject dto, String dirtyStrategyTable,Map<String,IDirtyStrategy> map)    {
        super(Children.create(new DirtyStrategyAttributeFactory(map, dto, egt), true),
                new ProxyLookup(Lookups.fixed(egt,dto),dto.getAbstractLookup()));
        this.dirtyStrategyTable = dirtyStrategyTable;
        setName(dirtyStrategyTable);
        setShortDescription(Bundle.HINT_DirtyStrategyTableNode());
        setIconBaseWithExtension(R.IMAGE_NODE_DIRTYSTRTABLE);
    }

    @Override
    public String getHtmlDisplayName() {
        StringBuilder sb = new StringBuilder();
        sb.append(R.HTML_Node);
        sb.append(dirtyStrategyTable);
        sb.append(R.HTML_CL_Node);
        return sb.toString();
    }
    
    
    
    
}
