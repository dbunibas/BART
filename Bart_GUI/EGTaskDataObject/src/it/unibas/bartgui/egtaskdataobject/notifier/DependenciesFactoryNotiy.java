package it.unibas.bartgui.egtaskdataobject.notifier;

import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;
import org.openide.util.WeakListeners;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DependenciesFactoryNotiy {
    
    private static final ChangeSupport cs = new ChangeSupport(DependenciesFactoryNotiy.class);

    public static void addChangeListener(ChangeListener listener) {
        cs.addChangeListener(WeakListeners.change(listener, DependenciesFactoryNotiy.class));
    }

    public static void removeChangeListener(ChangeListener listener) {
        cs.removeChangeListener(WeakListeners.change(listener, DependenciesFactoryNotiy.class));
    }

    public static void fire() {
        cs.fireChange();
    }
}
