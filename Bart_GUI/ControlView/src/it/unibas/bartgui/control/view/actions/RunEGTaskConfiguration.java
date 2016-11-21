package it.unibas.bartgui.control.view.actions;

import it.unibas.bartgui.egtaskdataobject.api.IRunEGTask;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.resources.R;
import it.unibas.centrallookup.CentralLookup;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.CookieAction;


@ActionID(
        category = "File",
        id = "it.unibas.bartgui.control.view.actions.RunEGTaskConfiguration"
)
@ActionRegistration(
        displayName ="Run",
        iconInMenu = true,
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Menu/Run", position = 1),
    @ActionReference(path = "Toolbars/File", position = 150),
    @ActionReference(path = "Shortcuts", name = "D-R")
})
@Messages("CTL_RunEGTask=Run")
@SuppressWarnings("rawtypes")
public final class RunEGTaskConfiguration extends CookieAction  {
    
    private EGTaskDataObjectDataObject egtDO;

    public RunEGTaskConfiguration() {
        setEnabled(false);
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        DataObject obj = CentralLookup.getDefLookup().lookup(DataObject.class);
        if(obj == null)return false;
        egtDO = (EGTaskDataObjectDataObject)obj;
        if(egtDO.getEgtask() == null) return false;
        if(egtDO.isRun())return false;
        return true;
    }
    
    
    @Override
    protected void performAction(Node[] activatedNodes) {
        IRunEGTask runegt = Lookup.getDefault().lookup(IRunEGTask.class);
        if(runegt !=null && egtDO != null)   {
            runegt.runEGTask();
        }
    }

    @Override
    protected int mode() {
        //MODE_EXACTLY_ONE
        return CookieAction.MODE_ANY;
    }

    @Override
    protected Class<?>[] cookieClasses() {
        return new Class[] {EGTaskDataObjectDataObject.class};
    }

    @Override
    public String getName() {
        return Bundle.CTL_RunEGTask();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected String iconResource() {
        return R.IMAGE_RUN;
    } 

    @Override
    protected boolean asynchronous() {
        return false;
    }
    
    
}