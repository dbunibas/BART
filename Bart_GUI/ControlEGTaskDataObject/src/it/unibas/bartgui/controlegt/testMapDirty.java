/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt;

import bart.model.EGTask;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import speedy.model.database.AttributeRef;

@SuppressWarnings({"unchecked","rawtypes"})
@ActionID(
        category = "test",
        id = "it.unibas.bartgui.controlegt.testMapDirty"
)
@ActionRegistration(
        displayName = "#CTL_testMapDirty"
)
@ActionReference(path = "Menu/File", position = -300)
@Messages("CTL_testMapDirty=testMapDirty")
public final class testMapDirty implements ActionListener {

    private final EGTask context;

    public testMapDirty(EGTask context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Map map = context.getConfiguration().getDirtyStrategiesMap();
        if(map == null)   {
            System.out.println("Mappa Nulla");
            return;
        }
        Iterator<AttributeRef> it = map.keySet().iterator();
        while(it.hasNext())   {
            AttributeRef tmp = it.next();
            System.out.println("\n");
            System.out.println("Attribute Table -> "+tmp.getTableName());
            System.out.println("Attribute Name -> "+tmp.getName());
            System.out.println(map.get(tmp));
            System.out.println("\n");
        }
    }
}
