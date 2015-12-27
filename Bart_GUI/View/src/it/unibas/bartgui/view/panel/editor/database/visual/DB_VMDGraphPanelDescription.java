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
public class DB_VMDGraphPanelDescription implements MultiViewDescription,Serializable{

    private String ID;
    private String displayName;
    private MultiViewElement panel;
    
    public DB_VMDGraphPanelDescription(MultiViewElement panel,String ID, String displayName) {
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
        return ImageUtilities.loadImage(R.IMAGE_NODE_DBMS);
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
