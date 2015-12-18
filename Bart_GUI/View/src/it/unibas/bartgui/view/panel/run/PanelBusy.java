package it.unibas.bartgui.view.panel.run;

import it.unibas.bartgui.resources.R;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JLabel;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXPanel;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class PanelBusy extends JXPanel   {
    
    private JXBusyLabel busyLabel;
    private Image logo;

    public PanelBusy() {
        setLayout(new BorderLayout(196, 0));      
        setPreferredSize(new Dimension(589,250));
        logo = ImageUtilities.loadImage(R.IMAGE_LOGO);
        busyLabel = new JXBusyLabel(new Dimension(50, 50)); 
        busyLabel.setName("busyLabel"); 
        busyLabel.getBusyPainter().setHighlightColor(new Color(44, 61, 146).darker()); 
        busyLabel.getBusyPainter().setBaseColor(new Color(168, 204, 241).brighter()); 
        busyLabel.setBusy(true); 
        busyLabel.setDelay(40);
        busyLabel.getBusyPainter().setPoints(18);
        busyLabel.getBusyPainter().setTrailLength(5);
        add(new JLabel(),BorderLayout.WEST);
        add(new JLabel(),BorderLayout.EAST);
        add(busyLabel,BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        if(logo != null)   {
            g.drawImage(logo, 0, 0, null);
        }    
    }  
}
