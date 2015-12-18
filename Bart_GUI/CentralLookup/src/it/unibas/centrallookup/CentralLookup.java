package it.unibas.centrallookup;

import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class CentralLookup extends AbstractLookup   {
    
    private InstanceContent ic;
    private static CentralLookup def = new CentralLookup();
    
    private CentralLookup(InstanceContent content)   {
        super(content);
        this.ic = content;
    }
    
    private CentralLookup()  {
        this(new InstanceContent());
    }
    
    public void add(Object o)   {
        ic.add(o);
    }
    
    public void remove(Object o)   {
        ic.remove(o);
    }
    
    public void clean()   {
        for(Object o : def.lookupAll(Object.class))   {
            remove(o);
        }
    }
    
    public static CentralLookup getDefLookup()   {
        return def;
    }
}
