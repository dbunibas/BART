/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.view.panel.chart;

import it.unibas.bartgui.egtaskdataobject.statistics.Repairability;
import it.unibas.bartgui.egtaskdataobject.statistics.VGQ_Stat;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;


/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class ChartsPanel extends javax.swing.JPanel implements PropertyChangeListener  {

    private PropertyChangeSupport pcs = null;
    
    private Map<VGQ_Stat, Long> vioGenQueryTimes = null;//FOR CHart Panel vioGenQueryTimes
    private Map<VGQ_Stat, Long> vioGenQueriesErrors = null;//FOR CHart Panel vioGenQueriesErrors
    
    private Map<VGQ_Stat, Repairability> vioGenQueriesRepairability = null;//FOR CHart Panel vioGenQueriesRepairability
    private Map<String, Repairability> dependencyRepairability = null;//FOR CHart Panel dependencyRepairability
    
    private String nameStatisitcResult;
    
    public ChartsPanel() {
        initComponents();
        pcs = new PropertyChangeSupport(this);       
        initButton();
    }

    
    private void initButton()   {
        this.addPCSListener(this);
        vgq_time_barButton.setName("vgq_time_barButton");
        vgq_time_barButton.setEnabled(false);
        vgq_time_barButton.addActionListener(new ButtonActionListener());
        vgq_err_barButton.setName("vgq_err_barButton");
        vgq_err_barButton.setEnabled(false);
        vgq_err_barButton.addActionListener(new ButtonActionListener());
        vgq_repButton.setName("vgq_repButton");
        vgq_repButton.setEnabled(false);
        vgq_repButton.addActionListener(new ButtonActionListener());
        dep_repButton.setName("dep_repButton");
        dep_repButton.setEnabled(false);    
        dep_repButton.addActionListener(new ButtonActionListener());
    }

    public void setVioGenQueryTimes(Map<VGQ_Stat, Long> vioGenQueryTimes) {
        this.vioGenQueryTimes = vioGenQueryTimes;
        pcs.firePropertyChange("vioGenQueryTimes", null, null);
    }
    
    private CategoryDataset vioGenQueryTimesBarDataSet()   {
        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();
        Iterator<VGQ_Stat> it = vioGenQueryTimes.keySet().iterator();
        while(it.hasNext())   {
            VGQ_Stat v = it.next();
            String dependencyID = v.getDependencyID();
            dataset.addValue(vioGenQueryTimes.get(v), v, dependencyID);
        }
        return dataset;
    }
    

    public void setVioGenQueriesErrors(Map<VGQ_Stat, Long> vioGenQueriesErrors) {
        this.vioGenQueriesErrors = vioGenQueriesErrors;
        pcs.firePropertyChange("vioGenQueriesErrors", null, null);
    }
    
    private CategoryDataset vioGenQueryErrorsDataSet()   {
        DefaultCategoryDataset dataset =
                new DefaultCategoryDataset();
        Iterator<VGQ_Stat> it = vioGenQueriesErrors.keySet().iterator();
        while(it.hasNext())   {
            VGQ_Stat v = it.next();
            String dependencyID = v.getDependencyID();
            dataset.addValue(vioGenQueriesErrors.get(v), v, dependencyID);
        }
        return dataset;
    }

    public void setVioGenQueriesRepairability(Map<VGQ_Stat, Repairability> vioGenQueriesRepairability) {
        this.vioGenQueriesRepairability = vioGenQueriesRepairability;
        pcs.firePropertyChange("vioGenQueriesRepairability", null, null);
    }
    
    private CategoryDataset VioGenQueriesRepairabilityDataSet()   {
        DefaultStatisticalCategoryDataset dts = new DefaultStatisticalCategoryDataset();
        Iterator<VGQ_Stat> it = vioGenQueriesRepairability.keySet().iterator();
        while(it.hasNext())   {
            VGQ_Stat v = it.next();
            Repairability r = vioGenQueriesRepairability.get(v);
            //dts.add(r.getMean(), r.getConfidenceInterval(), v.getDependencyID(), v.getViogenquery());
            dts.add(r.getMean(), r.getConfidenceInterval(),v.getViogenquery(),v.getDependencyID());
        }
        return dts;
    }
    
    

    public void setDependencyRepairability(Map<String, Repairability> dependencyRepairability) {
        this.dependencyRepairability = dependencyRepairability;
        pcs.firePropertyChange("dependencyRepairability", null, null);
    }
    
    private CategoryDataset DependencyRepairabilityDataSet()   {
        DefaultStatisticalCategoryDataset dts = new DefaultStatisticalCategoryDataset();
        Iterator<String> it = dependencyRepairability.keySet().iterator();
        while(it.hasNext())   {
            String dc = it.next();
            Repairability r = dependencyRepairability.get(dc);
            dts.add(r.getMean(), r.getConfidenceInterval(), dc, dc);
        }
        return dts;
    }
    
    
    public void addPCSListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePCSListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(vioGenQueryTimes == null)   {
            vgq_time_barButton.setEnabled(false);
        }else{
            vgq_time_barButton.setEnabled(true);         
        }
        if(vioGenQueriesErrors == null)   {
            vgq_err_barButton.setEnabled(false);
        }else{
            vgq_err_barButton.setEnabled(true);
        }
        if(vioGenQueriesRepairability == null)   {
            vgq_repButton.setEnabled(false);
        }else{
            vgq_repButton.setEnabled(true);
        }
        if(dependencyRepairability == null)   {
            dep_repButton.setEnabled(false);
        }else{
            dep_repButton.setEnabled(true);
        }
    }
    
    private final List<Dialog> dialogs = new ArrayList<Dialog>();

    /**
     * @return the nameStatisitcResult
     */
    public String getNameStatisitcResult() {
        return nameStatisitcResult;
    }

    /**
     * @param nameStatisitcResult the nameStatisitcResult to set
     */
    public void setNameStatisitcResult(String nameStatisitcResult) {
        this.nameStatisitcResult = nameStatisitcResult;
    }
    
    private class ButtonActionListener implements ActionListener   {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton)e.getSource();
            
            if(b.getName().equals("vgq_time_barButton"))   {
                StringBuilder title = new StringBuilder(getNameStatisitcResult());
                title.append(" - VioGenQueries Time");
                Dialog d = findDialog(title.toString());
                if(d != null)   {
                    d.requestFocus();
                    return;
                }
                JPanel panel = StatisticChart.createBarChart("VioGenQueries Time", 
                        "Viogenqueries", "Time ms", vioGenQueryTimesBarDataSet());
                d = createDialog(panel, title.toString());
                dialogs.add(d);
                d.setVisible(true);
            }
            
            if(b.getName().equals("vgq_err_barButton"))   {   
                StringBuilder title = new StringBuilder(getNameStatisitcResult());
                title.append(" - VioGenQueries Errors");
                Dialog d = findDialog(title.toString());
                if(d != null)   {
                    d.requestFocus();
                    return;
                }
                JPanel panel = StatisticChart.createBarChart("VioGenQueries Errors", 
                        "Viogenqueries", "Errors", vioGenQueryErrorsDataSet());
                d = createDialog(panel, title.toString());
                dialogs.add(d);
                d.setVisible(true);
            }        
            if(b.getName().equals("vgq_repButton"))   {   
                StringBuilder title = new StringBuilder(getNameStatisitcResult());
                title.append(" - VioGenQueries Repairability");
                Dialog d = findDialog(title.toString());
                if(d != null)   {
                    d.requestFocus();
                    return;
                }
                JPanel panel = StatisticChart.createStatisticBarChart("VioGenQueries Repairability", 
                        "Viogenqueries", "Value", VioGenQueriesRepairabilityDataSet());
                d = createDialog(panel, title.toString());
                dialogs.add(d);
                d.setVisible(true);
            }            
            if(b.getName().equals("dep_repButton"))   {   
                StringBuilder title = new StringBuilder(getNameStatisitcResult());
                title.append(" - Dependency Repairability");
                Dialog d = findDialog(title.toString());
                if(d != null)   {
                    d.requestFocus();
                    return;
                }
                JPanel panel = StatisticChart.createStatisticBarChart("Dependency Repairability", 
                        "Dependency", "Value", DependencyRepairabilityDataSet());
                d = createDialog(panel, title.toString());
                dialogs.add(d);
                d.setVisible(true);
            } 
        }     
        
        private Dialog findDialog(String name)   {
            for(Dialog d : dialogs)   {
                if(d.getName().equals(name))return d;
            }
            return null;
        }
        
        private Dialog createDialog(JPanel inner, String title)   {
            final Dialog d = new ChartDialog(inner, title);
            d.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent e) {
                    dialogs.remove(d);
                }
                
            });
            return d;
        }
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        vgq_time_barButton = new javax.swing.JButton();
        vgq_err_barButton = new javax.swing.JButton();
        vgq_repButton = new javax.swing.JButton();
        dep_repButton = new javax.swing.JButton();

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(ChartsPanel.class, "ChartsPanel.jPanel2.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 204))); // NOI18N

        vgq_time_barButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/it/unibas/bartgui/view/panel/chart/icon/barchart.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(vgq_time_barButton, org.openide.util.NbBundle.getMessage(ChartsPanel.class, "ChartsPanel.vgq_time_barButton.text")); // NOI18N
        vgq_time_barButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/it/unibas/bartgui/view/panel/chart/icon/disablebarchart.png"))); // NOI18N

        vgq_err_barButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/it/unibas/bartgui/view/panel/chart/icon/barchart.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(vgq_err_barButton, org.openide.util.NbBundle.getMessage(ChartsPanel.class, "ChartsPanel.vgq_err_barButton.text")); // NOI18N
        vgq_err_barButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/it/unibas/bartgui/view/panel/chart/icon/disablebarchart.png"))); // NOI18N

        vgq_repButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/it/unibas/bartgui/view/panel/chart/icon/statistic.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(vgq_repButton, org.openide.util.NbBundle.getMessage(ChartsPanel.class, "ChartsPanel.vgq_repButton.text")); // NOI18N
        vgq_repButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/it/unibas/bartgui/view/panel/chart/icon/disablestatistic.png"))); // NOI18N

        dep_repButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/it/unibas/bartgui/view/panel/chart/icon/statistic.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(dep_repButton, org.openide.util.NbBundle.getMessage(ChartsPanel.class, "ChartsPanel.dep_repButton.text")); // NOI18N
        dep_repButton.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/it/unibas/bartgui/view/panel/chart/icon/disablestatistic.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vgq_time_barButton)
                .addGap(18, 18, 18)
                .addComponent(vgq_err_barButton)
                .addGap(18, 18, 18)
                .addComponent(vgq_repButton)
                .addGap(18, 18, 18)
                .addComponent(dep_repButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vgq_time_barButton)
                    .addComponent(vgq_err_barButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vgq_repButton)
                    .addComponent(dep_repButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton dep_repButton;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton vgq_err_barButton;
    private javax.swing.JButton vgq_repButton;
    private javax.swing.JButton vgq_time_barButton;
    // End of variables declaration//GEN-END:variables
}
