/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.view.fun;

import it.unibas.bartgui.view.panel.run.PanelBusy;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.windows.WindowManager;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class BartThemeDialog extends javax.swing.JDialog {

    
    private BartThemeDialog(java.awt.Frame parent,JLabel label) {
        super(parent);
        setModal(false);
        setResizable(false);
        setUndecorated(true);
        //setAlwaysOnTop(true);
        setMaximumSize(new Dimension(500, 342));
        setMinimumSize(new Dimension(500, 342));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().add(label,BorderLayout.CENTER);
        setVisible(false);
    }
    
    public static Dialog getFunDialog()   {
        FileObject img = FileUtil.getConfigFile("BartGIfImage/BartSaturdayNightFever.gif");
        FileObject audio = FileUtil.getConfigFile("BartAudio/STheme.wav");
        final AudioClip clip = Applet.newAudioClip(audio.toURL());
        Icon icon = new ImageIcon(img.toURL());   
        JLabel label = new JLabel(icon);
        final Dialog dialog = new BartThemeDialog(WindowManager.getDefault().getMainWindow(), label);
        label.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 1)   {
                    clip.stop();
                    dialog.dispose();
                }
            }
            
        });
        clip.loop();
        return dialog;
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
