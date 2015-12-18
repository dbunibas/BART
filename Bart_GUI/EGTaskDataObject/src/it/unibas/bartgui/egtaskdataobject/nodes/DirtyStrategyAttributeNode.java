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
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DirtyStrategyAttributeNode extends AbstractNode   {

    private String attribute;
    private IDirtyStrategy typo;
    
    public DirtyStrategyAttributeNode(EGTask egt, EGTaskDataObjectDataObject dto, String attribute,IDirtyStrategy typo) {
        super(Children.LEAF,new ProxyLookup(Lookups.fixed(egt,dto),dto.getAbstractLookup()));
        this.attribute = attribute;
        this.typo = typo;
        setName(attribute);
        setShortDescription(typo.toString());
        setIconBaseWithExtension(R.IMAGE_NODE_DIRTYSTRGATTRB);
    }

    @Override
    public String getHtmlDisplayName() {
        StringBuilder sb = new StringBuilder();
        sb.append(R.HTML_Node);
        sb.append(attribute);
        sb.append(" - ");
        sb.append(R.HTML_CL_Node);
        sb.append(R.HTML_Hint);
        sb.append(typo);
        sb.append(R.HTML_CL_Hint);
        return sb.toString();
    }
    
    
    
    
    
    
}
