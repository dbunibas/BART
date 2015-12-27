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
public class TableDataViewDescription implements MultiViewDescription,Serializable  {
        
    private String ID;
    private String displayName;
    private MultiViewElement panel;

    public TableDataViewDescription(String ID, String displayName, MultiViewElement panel) {
        this.ID = ID;
        this.displayName = displayName;
        this.panel = panel;
    }
    
    
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public Image getIcon() {
        return ImageUtilities.loadImage(R.IMAGE_DB_TABLE);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public String preferredID() {
        return this.ID;
    }

    @Override
    public MultiViewElement createElement() {
        return panel;
    }

}
