package it.unibas.bartgui.view.fun;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@SuppressWarnings("static")
@ServiceProvider(service = StatusLineElementProvider.class)
public class StatusClock implements StatusLineElementProvider {
    
    private JPanel panel = new JPanel(new BorderLayout());
    private JLabel label = new JLabel("BART");

    public StatusClock() {

        panel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.WEST);
        panel.add(label,BorderLayout.CENTER);
        panel.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 6)   {
                    BartThemeDialog.getFunDialog().setVisible(true);
                }
            }    
        });
    }

    @Override
    public Component getStatusLineElement() {
        return panel;
    }
    

    
}

