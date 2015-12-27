package it.unibas.bartgui.view.panel.editor.database.visual;

import it.unibas.bartgui.resources.R;
import java.awt.Image;
import java.io.Serializable;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class InfoDBDescription implements MultiViewDescription,Serializable   {
    
    private MultiViewElement panel;

    public InfoDBDescription(MultiViewElement panel) {
        this.panel = panel;
    }
    
    
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public String getDisplayName() {
        return "Info";
    }

    @Override
    public Image getIcon() {
        return ImageUtilities.loadImage(R.IMAGE_NODE_DBMS);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public String preferredID() {
        return "InfoDBPanelSupport";
    }

    @Override
    public MultiViewElement createElement() {
        return panel;
    }
 

}
