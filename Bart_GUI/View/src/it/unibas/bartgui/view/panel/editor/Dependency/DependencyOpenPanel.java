package it.unibas.bartgui.view.panel.editor.Dependency;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import org.jdesktop.swingx.JXTitledSeparator;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DependencyOpenPanel extends JPanel  {

    private JXTitledSeparator title;
    private DependecyWPanel panelDependecy;
    private VioGenQueriesWPanel panelVioGenQueriesWPanel;

    public DependencyOpenPanel() {
        setLayout(new GridLayout(1, 1));
        //InitTitle();
        initPanelDependency();
        intiPanelVioGenQueries();
        initLayout();
    }

    private void initLayout()   {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(panelDependecy,BorderLayout.NORTH);
        panel.add(panelVioGenQueriesWPanel,BorderLayout.CENTER);
        add(panel);
    }
    
    private void initPanelDependency()   {
        panelDependecy = new DependecyWPanel();
    }
    
    private void intiPanelVioGenQueries()   {
        panelVioGenQueriesWPanel = new VioGenQueriesWPanel();
    }
    
    
    /*private void InitTitle()   {
        title = new JXTitledSeparator();
        title.setIcon(ImageUtilities.image2Icon(ImageUtilities.loadImage(R.IMAGE_NODE_DCS)));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(Color.BLUE.darker());
        title.setFont(new Font("Times New Roman", Font.ITALIC, 16));
        title.setTitle("Dependency Configuration");       
    }*/

    /**
     * @return the panelDependecy
     */
    public DependecyWPanel getPanelDependecy() {
        return panelDependecy;
    }

    /**
     * @return the panelVioGenQueriesWPanel
     */
    public VioGenQueriesWPanel getPanelVioGenQueriesWPanel() {
        return panelVioGenQueriesWPanel;
    }
}
