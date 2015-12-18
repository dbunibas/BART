/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.view.panel.editor.Dependency;

import it.unibas.bartgui.view.panel.editor.Dependency.tableModel.RelativePainterHighlighter;
import java.awt.Color;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.util.PaintUtils;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.ease.Spline;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class VioGenQueriesWPanel extends javax.swing.JPanel {

    private JXTable table;
    private HighlighterControl highlighterControl;
    private int percentageColumn = 2;
    private int comparisonColumn = 1;
    /**
     * Creates new form VioGenQueryWPanel
     */
    public VioGenQueriesWPanel() {
        initComponents();
        initTable();       
        jScrollPane1.setViewportView(table);
    }
    
    public void bind(TableModel model)   {
        //highlighterControl = new HighlighterControl();
        table.setModel(model);
    }

    private void initTable()   {
        table = new JXTable();
        table.setColumnControlVisible(true);
        table.setEditable(true);
        table.setCellSelectionEnabled(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setDragEnabled(false); 
        table.setSelectionBackground(new Color(214, 217, 223));
        highlighterControl = new HighlighterControl();
    }
    
    public synchronized void race()   {
        highlighterControl.race();
    }
    
    public synchronized void fade()   {
        highlighterControl.fadeIn();
    }
    
    
    public static class PercentageRelativizer extends RelativePainterHighlighter.NumberRelativizer { 
        public PercentageRelativizer(int column, boolean spreadColumns,  
                Number max, Number current) { 
            super(column, spreadColumns, max, current); 
        }        
    } 
      
    public class HighlighterControl     {
        private RelativePainterHighlighter tableValueBasedHighlighter; 
        private boolean spreadColumns; 
          
        private Timeline raceTimeline; 
        private Timeline fadeInTimeline; 
        private MattePainter matte; 
        private Color base = PaintUtils.setSaturation(Color.BLUE, .7f); 
          
        public HighlighterControl() { 
            matte = new MattePainter(PaintUtils.setAlpha(base, 125)); 
            tableValueBasedHighlighter = new RelativePainterHighlighter(matte); 
            table.addHighlighter(tableValueBasedHighlighter); 
              
            setSpreadColumns(false);     

        }   

        public void race() { 
            if (raceTimeline == null) { 
                raceTimeline = new Timeline(this); 
                raceTimeline.addPropertyToInterpolate("currentPercentage", 0, 100); 
            } 
            raceTimeline.replay(); 
        } 
           
        public void fadeIn() { 
            if (fadeInTimeline == null) { 
                fadeInTimeline = new Timeline(this); 
                fadeInTimeline.addPropertyToInterpolate("background",  0 , 125); 
                fadeInTimeline.setDuration(2000); 
                fadeInTimeline.setEase(new Spline(0.7f)); 
            } 
            fadeInTimeline.replay(); 
        } 
          
        public void setBackground(int alpha) { 
            matte.setFillPaint( PaintUtils.setAlpha(base, alpha)); 
        } 
          
        public void setCurrentPercentage(int percentage) { 
            PercentageRelativizer relativizer = createPercentageRelativizer(percentage); 
            tableValueBasedHighlighter.setRelativizer(relativizer); 
        } 
  
  
         /** 
          * Creates and returns a relativizer with the given intermediate value. 
          *  
          */ 
         private PercentageRelativizer createPercentageRelativizer(int intermediate) { 
             return new PercentageRelativizer(percentageColumn, isSpreadColumns(), 100, intermediate); 
         } 
          
         /** 
          *  
          */ 
         private void updateTableHighlighter() { 
             tableValueBasedHighlighter.setRelativizer(createPercentageRelativizer(100)); 
             if (isSpreadColumns()) { 
                 tableValueBasedHighlighter.setHighlightPredicate(HighlightPredicate.ALWAYS); 
             } else { 
                 tableValueBasedHighlighter.setHighlightPredicate( 
                         new HighlightPredicate.ColumnHighlightPredicate(percentageColumn)); 
             }     
         } 
  
         public boolean isSpreadColumns() { 
             return spreadColumns; 
         } 
          
         public void setSpreadColumns(boolean extendedMarker) { 
             boolean old = isSpreadColumns(); 
             this.spreadColumns = extendedMarker; 
             updateTableHighlighter(); 
             firePropertyChange("spreadColumns", old, isSpreadColumns()); 
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

        jScrollPane1 = new javax.swing.JScrollPane();

        setMinimumSize(new java.awt.Dimension(613, 202));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), org.openide.util.NbBundle.getMessage(VioGenQueriesWPanel.class, "VioGenQueriesWPanel.jScrollPane1.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 51, 204))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
