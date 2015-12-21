/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.egtaskdataobject.notifier;

import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;
import org.openide.util.WeakListeners;

/**
 *
 * @author Musicrizz
 */
public class DirtyStrategyAttributeFactoryNotifier {
    
    private static final ChangeSupport cs = new ChangeSupport(DirtyStrategyAttributeFactoryNotifier.class);

    public static void addChangeListener(ChangeListener listener) {
        cs.addChangeListener(WeakListeners.change(listener, DirtyStrategyAttributeFactoryNotifier.class));
    }

    public static void removeChangeListener(ChangeListener listener) {
        cs.removeChangeListener(WeakListeners.change(listener, DirtyStrategyAttributeFactoryNotifier.class));
    }

    public static void fire() {
        cs.fireChange();
    }
}
