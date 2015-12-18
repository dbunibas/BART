/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.view.panel.run;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import javax.swing.JFrame;
import org.openide.windows.WindowManager;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class BusyDialog extends javax.swing.JDialog {

    private static BusyDialog singleton;
    /**
     * Creates new form busyDialog
     */
    
    private BusyDialog(java.awt.Frame parent) {
        super(parent, true);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        //setAlwaysOnTop(true);
        setMaximumSize(new Dimension(589, 250));
        setMinimumSize(new Dimension(589, 250));
        setPreferredSize(new Dimension(300, 200));
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().add(new PanelBusy(),BorderLayout.CENTER);
        setVisible(false);  
        //setOpacity(0.1f);
    }
    
    public static void initBusyDialog()   {
        singleton = new BusyDialog(WindowManager.getDefault().getMainWindow());
    }

    public static synchronized Dialog getBusyDialog()   {
        if(singleton == null)   {
            initBusyDialog();
        }
        return singleton;
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
