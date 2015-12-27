package it.unibas.bartgui.view.panel.chart;

import it.unibas.bartgui.resources.R;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.openide.util.ImageUtilities;
import org.openide.windows.WindowManager;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class ChartDialog extends javax.swing.JDialog {

    static {
        setDefaultLookAndFeelDecorated(true);
    }
  
    
    public ChartDialog(JPanel inner,String title) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(ImageUtilities.loadImage(R.IMAGE_PIE_CHART));
        setResizable(true);
        setAlwaysOnTop(false);
        setName(title);
        setTitle(title);
        setMinimumSize(new Dimension(700, 440));
        Frame parent = WindowManager.getDefault().getMainWindow();
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        add(inner,BorderLayout.CENTER); 
        setVisible(false);
    }

    
}
