/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.view.panel.editor.ConfEGTask;

import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.centrallookup.CentralLookup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import speedy.persistence.xml.operators.TransformFilePaths;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class ConfEGTaskPanel extends javax.swing.JPanel {

    private final TransformFilePaths transformFilePaths = new TransformFilePaths();
    /**
     * Creates new form ConfEGTaskPanel
     */
    public ConfEGTaskPanel() {
        initComponents();
        initListenerButton();     
        initState();
        initApplyCellCheckBox();
    }
    
    private void initState()   {
        exportCellChangesPathTextField.setName("ExportCellChangesPath");
        exportDirtyDBPathTextField.setName("ExportDirtyDBPath");
        queryWxecutionTimeOutTextField.setName("queryWxecutionTimeOut");
        sizeFactorReductionTextField.setName("sizeFactorReduction");
        
        exportCellChangesCheckBox.setSelected(false);
        exportCellChangesPathTextField.setEnabled(false);
        exportCellChangesButton.setEnabled(false);
        
        exportDirtyDBCheckBox.setSelected(false);
        exportDirtyDbTypeTextField.setEnabled(false);
        exportDirtyDBPathTextField.setEnabled(false);
        exportDirtyDbButton.setEnabled(false);
        
        cloneTargetSchemaCheckBox.setSelected(false);
        cloneSuffixTextField.setEnabled(false);
    }
    
    private void initApplyCellCheckBox()   {
        applyCellChangesCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!applyCellChangesCheckBox.isSelected())   {
                    exportCellChangesCheckBox.setSelected(false);
                    exportDirtyDBCheckBox.setSelected(false);
                }
            }
        });
    }

    
    private void initListenerButton()   {
       exportCellChangesButton.addActionListener(new ButtonActionListenerPathCellChanges(exportCellChangesPathTextField));
       
       exportDirtyDbButton.addActionListener(new ButtonActionListenerPathDirtyDb(exportDirtyDBPathTextField));
    }

    private class ButtonActionListenerPathCellChanges implements ActionListener   {

        private JTextField  text;

        public ButtonActionListenerPathCellChanges(JTextField text) {
            this.text = text;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            EGTaskDataObjectDataObject dto = CentralLookup.getDefLookup().lookup(EGTaskDataObjectDataObject.class);
            File toLoad = new FileChooserBuilder("Create_CVS_FILE")
                                        .setTitle("Create cvs file")
                                        .setDefaultWorkingDirectory(new File(System.getProperty("user.home")))
                                        .setApproveText("ok")
                                        .setDirectoriesOnly(false)
                                        .setFilesOnly(true)
                                        .setAcceptAllFileFilterUsed(false)
                                        .addFileFilter(new FileNameExtensionFilter("CVS File", "csv","CSV"))
                                        .showSaveDialog();
            if(toLoad != null)   {
                if(!(toLoad.getName().contains(".csv") || toLoad.getName().contains(".CSV")))   {
                    StringBuilder sb = new StringBuilder(toLoad.getAbsolutePath());
                    sb.append(".csv");
                    toLoad = new File(sb.toString());
                }
                File basefile = FileUtil.toFile(dto.getPrimaryFile());                   
                text.setText(transformFilePaths.relativize(basefile.getAbsolutePath(), toLoad.getAbsolutePath()));
            }
        }    
    }
    private class ButtonActionListenerPathDirtyDb implements ActionListener   {

        private JTextField  text;

        public ButtonActionListenerPathDirtyDb(JTextField text) {
            this.text = text;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            EGTaskDataObjectDataObject dto = CentralLookup.getDefLookup().lookup(EGTaskDataObjectDataObject.class);
            File toLoad = new FileChooserBuilder("Choose_folder")
                                        .setTitle("Choose folder for save DirtyDB csv")
                                        .setDefaultWorkingDirectory(new File(System.getProperty("user.home")))
                                        .setApproveText("ok")
                                        .setDirectoriesOnly(true)
                                        //.addFileFilter(new FileNameExtensionFilter("CVS File", "cvs","CVS"))
                                        .showSaveDialog();
            if(toLoad != null)   {
                File basefile = FileUtil.toFile(dto.getPrimaryFile());                   
                text.setText(transformFilePaths.relativize(basefile.getAbsolutePath(), toLoad.getAbsolutePath()));
            }
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

        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        applyCellChangesCheckBox = new javax.swing.JCheckBox();
        estimateRepairabilityCheckBox = new javax.swing.JCheckBox();
        useSymmetricOptimizationCheckBox = new javax.swing.JCheckBox();
        generateAllChangesCheckBox = new javax.swing.JCheckBox();
        detectEntireEquivalenceClassesCheckBox = new javax.swing.JCheckBox();
        randomErrorsCheckBox = new javax.swing.JCheckBox();
        outlierErrorsCheckBox = new javax.swing.JCheckBox();
        queryWxecutionTimeOutTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        printLogCheckBox = new javax.swing.JCheckBox();
        debugCheckBox = new javax.swing.JCheckBox();
        useDeltaDBForChangesCheckBox = new javax.swing.JCheckBox();
        recreateDBOnStartCheckBox = new javax.swing.JCheckBox();
        checkCleanInstanceCheckBox = new javax.swing.JCheckBox();
        checkChangesCheckBox = new javax.swing.JCheckBox();
        excludeCrossProductsCheckBox = new javax.swing.JCheckBox();
        avoidInteractionsCheckBox = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        exportCellChangesCheckBox = new javax.swing.JCheckBox();
        exportCellChangesPathTextField = new javax.swing.JTextField();
        exportDirtyDBCheckBox = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        exportDirtyDbTypeTextField = new javax.swing.JTextField();
        exportDirtyDBPathTextField = new javax.swing.JTextField();
        cloneTargetSchemaCheckBox = new javax.swing.JCheckBox();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        cloneSuffixTextField = new javax.swing.JTextField();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        sizeFactorReductionTextField = new javax.swing.JTextField();
        exportCellChangesButton = new javax.swing.JButton();
        exportDirtyDbButton = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(1000, 1000));
        setMinimumSize(new java.awt.Dimension(660, 340));
        setPreferredSize(new java.awt.Dimension(660, 340));

        org.openide.awt.Mnemonics.setLocalizedText(applyCellChangesCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.applyCellChangesCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(estimateRepairabilityCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.estimateRepairabilityCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(useSymmetricOptimizationCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.useSymmetricOptimizationCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(generateAllChangesCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.generateAllChangesCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(detectEntireEquivalenceClassesCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.detectEntireEquivalenceClassesCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(randomErrorsCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.randomErrorsCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(outlierErrorsCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.outlierErrorsCheckBox.text")); // NOI18N

        queryWxecutionTimeOutTextField.setText(org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.queryWxecutionTimeOutTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.jLabel1.text")); // NOI18N

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(detectEntireEquivalenceClassesCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(randomErrorsCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(generateAllChangesCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(useSymmetricOptimizationCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(estimateRepairabilityCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(applyCellChangesCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(queryWxecutionTimeOutTextField))
                    .addComponent(outlierErrorsCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(applyCellChangesCheckBox)
                .addGap(18, 18, 18)
                .addComponent(estimateRepairabilityCheckBox)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(queryWxecutionTimeOutTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(21, 21, 21)
                .addComponent(useSymmetricOptimizationCheckBox)
                .addGap(18, 18, 18)
                .addComponent(generateAllChangesCheckBox)
                .addGap(18, 18, 18)
                .addComponent(detectEntireEquivalenceClassesCheckBox)
                .addGap(18, 18, 18)
                .addComponent(randomErrorsCheckBox)
                .addGap(18, 18, 18)
                .addComponent(outlierErrorsCheckBox)
                .addContainerGap(11, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator2))
        );

        org.openide.awt.Mnemonics.setLocalizedText(printLogCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.printLogCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(debugCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.debugCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(useDeltaDBForChangesCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.useDeltaDBForChangesCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(recreateDBOnStartCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.recreateDBOnStartCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(checkCleanInstanceCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.checkCleanInstanceCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(checkChangesCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.checkChangesCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(excludeCrossProductsCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.excludeCrossProductsCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(avoidInteractionsCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.avoidInteractionsCheckBox.text")); // NOI18N

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(useDeltaDBForChangesCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(recreateDBOnStartCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkCleanInstanceCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkChangesCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(excludeCrossProductsCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(avoidInteractionsCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(debugCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(printLogCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(printLogCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(debugCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(useDeltaDBForChangesCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(recreateDBOnStartCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(checkCleanInstanceCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(checkChangesCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(excludeCrossProductsCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(avoidInteractionsCheckBox)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        org.openide.awt.Mnemonics.setLocalizedText(exportCellChangesCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.exportCellChangesCheckBox.text")); // NOI18N

        exportCellChangesPathTextField.setText(org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.exportCellChangesPathTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(exportDirtyDBCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.exportDirtyDBCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.jLabel2.text")); // NOI18N

        exportDirtyDbTypeTextField.setText(org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.exportDirtyDbTypeTextField.text")); // NOI18N

        exportDirtyDBPathTextField.setText(org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.exportDirtyDBPathTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cloneTargetSchemaCheckBox, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.cloneTargetSchemaCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.jLabel3.text")); // NOI18N

        cloneSuffixTextField.setText(org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.cloneSuffixTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.jLabel4.text")); // NOI18N

        sizeFactorReductionTextField.setText(org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.sizeFactorReductionTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(exportCellChangesButton, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.exportCellChangesButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(exportDirtyDbButton, org.openide.util.NbBundle.getMessage(ConfEGTaskPanel.class, "ConfEGTaskPanel.exportDirtyDbButton.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(exportCellChangesCheckBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(exportDirtyDBCheckBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(exportDirtyDbTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(exportCellChangesPathTextField)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cloneSuffixTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(sizeFactorReductionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(exportDirtyDBPathTextField))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(exportCellChangesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(exportDirtyDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator5))
                .addGap(15, 15, 15))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cloneTargetSchemaCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exportCellChangesCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exportCellChangesPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exportCellChangesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exportDirtyDBCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(exportDirtyDbTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exportDirtyDBPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exportDirtyDbButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cloneTargetSchemaCheckBox)
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cloneSuffixTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(sizeFactorReductionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox applyCellChangesCheckBox;
    private javax.swing.JCheckBox avoidInteractionsCheckBox;
    private javax.swing.JCheckBox checkChangesCheckBox;
    private javax.swing.JCheckBox checkCleanInstanceCheckBox;
    private javax.swing.JTextField cloneSuffixTextField;
    private javax.swing.JCheckBox cloneTargetSchemaCheckBox;
    private javax.swing.JCheckBox debugCheckBox;
    private javax.swing.JCheckBox detectEntireEquivalenceClassesCheckBox;
    private javax.swing.JCheckBox estimateRepairabilityCheckBox;
    private javax.swing.JCheckBox excludeCrossProductsCheckBox;
    private javax.swing.JButton exportCellChangesButton;
    private javax.swing.JCheckBox exportCellChangesCheckBox;
    private javax.swing.JTextField exportCellChangesPathTextField;
    private javax.swing.JCheckBox exportDirtyDBCheckBox;
    private javax.swing.JTextField exportDirtyDBPathTextField;
    private javax.swing.JButton exportDirtyDbButton;
    private javax.swing.JTextField exportDirtyDbTypeTextField;
    private javax.swing.JCheckBox generateAllChangesCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JCheckBox outlierErrorsCheckBox;
    private javax.swing.JCheckBox printLogCheckBox;
    private javax.swing.JTextField queryWxecutionTimeOutTextField;
    private javax.swing.JCheckBox randomErrorsCheckBox;
    private javax.swing.JCheckBox recreateDBOnStartCheckBox;
    private javax.swing.JTextField sizeFactorReductionTextField;
    private javax.swing.JCheckBox useDeltaDBForChangesCheckBox;
    private javax.swing.JCheckBox useSymmetricOptimizationCheckBox;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the applyCellChangesCheckBox
     */
    public javax.swing.JCheckBox getApplyCellChangesCheckBox() {
        return applyCellChangesCheckBox;
    }

    /**
     * @return the avoidInteractionsCheckBox
     */
    public javax.swing.JCheckBox getAvoidInteractionsCheckBox() {
        return avoidInteractionsCheckBox;
    }

    /**
     * @return the checkChangesCheckBox
     */
    public javax.swing.JCheckBox getCheckChangesCheckBox() {
        return checkChangesCheckBox;
    }

    /**
     * @return the checkCleanInstanceCheckBox
     */
    public javax.swing.JCheckBox getCheckCleanInstanceCheckBox() {
        return checkCleanInstanceCheckBox;
    }

    /**
     * @return the cloneTargetSchemaCheckBox
     */
    public javax.swing.JCheckBox getCloneTargetSchemaCheckBox() {
        return cloneTargetSchemaCheckBox;
    }

    /**
     * @return the debugCheckBox
     */
    public javax.swing.JCheckBox getDebugCheckBox() {
        return debugCheckBox;
    }

    /**
     * @return the detectEntireEquivalenceClassesCheckBox
     */
    public javax.swing.JCheckBox getDetectEntireEquivalenceClassesCheckBox() {
        return detectEntireEquivalenceClassesCheckBox;
    }

    /**
     * @return the estimateRepairabilityCheckBox
     */
    public javax.swing.JCheckBox getEstimateRepairabilityCheckBox() {
        return estimateRepairabilityCheckBox;
    }

    /**
     * @return the excludeCrossProductsCheckBox
     */
    public javax.swing.JCheckBox getExcludeCrossProductsCheckBox() {
        return excludeCrossProductsCheckBox;
    }

    /**
     * @return the exportCellChangesCheckBox
     */
    public javax.swing.JCheckBox getExportCellChangesCheckBox() {
        return exportCellChangesCheckBox;
    }

    /**
     * @return the exportCellChangesPathTextField
     */
    public javax.swing.JTextField getExportCellChangesPathTextField() {
        return exportCellChangesPathTextField;
    }

    /**
     * @return the exportDirtyDBCheckBox
     */
    public javax.swing.JCheckBox getExportDirtyDBCheckBox() {
        return exportDirtyDBCheckBox;
    }

    /**
     * @return the exportDirtyDBPathTextField
     */
    public javax.swing.JTextField getExportDirtyDBPathTextField() {
        return exportDirtyDBPathTextField;
    }

    /**
     * @return the generateAllChangesCheckBox
     */
    public javax.swing.JCheckBox getGenerateAllChangesCheckBox() {
        return generateAllChangesCheckBox;
    }

    /**
     * @return the cloneSuffixTextField
     */
    public javax.swing.JTextField getCloneSuffixTextField() {
        return cloneSuffixTextField;
    }

    /**
     * @return the exportCellChangesButton
     */
    public javax.swing.JButton getExportCellChangesButton() {
        return exportCellChangesButton;
    }

    /**
     * @return the exportDirtyDbButton
     */
    public javax.swing.JButton getExportDirtyDbButton() {
        return exportDirtyDbButton;
    }

    /**
     * @return the exportDirtyDbTypeTextField
     */
    public javax.swing.JTextField getExportDirtyDbTypeTextField() {
        return exportDirtyDbTypeTextField;
    }

    /**
     * @return the outlierErrorsCheckBox
     */
    public javax.swing.JCheckBox getOutlierErrorsCheckBox() {
        return outlierErrorsCheckBox;
    }

    /**
     * @param outlierErrorsCheckBox the outlierErrorsCheckBox to set
     */
    public void setOutlierErrorsCheckBox(javax.swing.JCheckBox outlierErrorsCheckBox) {
        this.outlierErrorsCheckBox = outlierErrorsCheckBox;
    }

    /**
     * @return the printLogCheckBox
     */
    public javax.swing.JCheckBox getPrintLogCheckBox() {
        return printLogCheckBox;
    }

    /**
     * @param printLogCheckBox the printLogCheckBox to set
     */
    public void setPrintLogCheckBox(javax.swing.JCheckBox printLogCheckBox) {
        this.printLogCheckBox = printLogCheckBox;
    }

    /**
     * @return the randomErrorsCheckBox
     */
    public javax.swing.JCheckBox getRandomErrorsCheckBox() {
        return randomErrorsCheckBox;
    }

    /**
     * @param randomErrorsCheckBox the randomErrorsCheckBox to set
     */
    public void setRandomErrorsCheckBox(javax.swing.JCheckBox randomErrorsCheckBox) {
        this.randomErrorsCheckBox = randomErrorsCheckBox;
    }

    /**
     * @return the recreateDBOnStartCheckBox
     */
    public javax.swing.JCheckBox getRecreateDBOnStartCheckBox() {
        return recreateDBOnStartCheckBox;
    }

    /**
     * @param recreateDBOnStartCheckBox the recreateDBOnStartCheckBox to set
     */
    public void setRecreateDBOnStartCheckBox(javax.swing.JCheckBox recreateDBOnStartCheckBox) {
        this.recreateDBOnStartCheckBox = recreateDBOnStartCheckBox;
    }

    /**
     * @return the sizeFactorReductionTextField
     */
    public javax.swing.JTextField getSizeFactorReductionTextField() {
        return sizeFactorReductionTextField;
    }

    /**
     * @param sizeFactorReductionTextField the sizeFactorReductionTextField to set
     */
    public void setSizeFactorReductionTextField(javax.swing.JTextField sizeFactorReductionTextField) {
        this.sizeFactorReductionTextField = sizeFactorReductionTextField;
    }

    /**
     * @return the useDeltaDBForChangesCheckBox
     */
    public javax.swing.JCheckBox getUseDeltaDBForChangesCheckBox() {
        return useDeltaDBForChangesCheckBox;
    }

    /**
     * @param useDeltaDBForChangesCheckBox the useDeltaDBForChangesCheckBox to set
     */
    public void setUseDeltaDBForChangesCheckBox(javax.swing.JCheckBox useDeltaDBForChangesCheckBox) {
        this.useDeltaDBForChangesCheckBox = useDeltaDBForChangesCheckBox;
    }

    /**
     * @return the useSymmetricOptimizationCheckBox
     */
    public javax.swing.JCheckBox getUseSymmetricOptimizationCheckBox() {
        return useSymmetricOptimizationCheckBox;
    }

    /**
     * @param useSymmetricOptimizationCheckBox the useSymmetricOptimizationCheckBox to set
     */
    public void setUseSymmetricOptimizationCheckBox(javax.swing.JCheckBox useSymmetricOptimizationCheckBox) {
        this.useSymmetricOptimizationCheckBox = useSymmetricOptimizationCheckBox;
    }

    /**
     * @return the queryWxecutionTimeOutTextField
     */
    public javax.swing.JTextField getQueryWxecutionTimeOutTextField() {
        return queryWxecutionTimeOutTextField;
    }
}
