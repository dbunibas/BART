package it.unibas.bartgui.view.panel.editor.database.visual;

import java.awt.BorderLayout;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class InfoDB extends JPanel  implements MultiViewElement {

    private MultiViewElementCallback callback = null;
    private final JToolBar toolbar = new JToolBar();
    private final JScrollPane scrPanel = new JScrollPane();
    private JTextArea infoArea;
    private IDatabase database;

    public InfoDB(IDatabase db) {
        this.database = db;
        initTextArea();
        initTextInfo();
    }
    
    private void initTextArea()  {
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setWrapStyleWord(true);
        scrPanel.setViewportView(infoArea);
        add(scrPanel,BorderLayout.CENTER);
    }
    
    private void initTextInfo()   {
        if(database == null)return;
        StringBuilder sb = new StringBuilder();
        for(String name : database.getTableNames())   {
            ITable t = database.getTable(name);
            long tuples = t.getSize();
            sb.append("Table: ");
            sb.append(name);
            sb.append(" -  N° of tuples: ");
            sb.append(tuples);
            sb.append("\n\n");          
        }
        infoArea.setText(sb.toString());
    }
    
    
    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        if(callback != null)   {
            return callback.createDefaultActions();
        }
        Action[] a = {};
        return a;
    }

    @Override
    public Lookup getLookup() {
        return Lookups.fixed(this);
    }

    @Override
    public void componentOpened() {
        
    }

    @Override
    public void componentClosed() {
        
    }

    @Override
    public void componentShowing() {
        
    }

    @Override
    public void componentHidden() {
        
    }

    @Override
    public void componentActivated() {
        
    }

    @Override
    public void componentDeactivated() {
        
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }
}
