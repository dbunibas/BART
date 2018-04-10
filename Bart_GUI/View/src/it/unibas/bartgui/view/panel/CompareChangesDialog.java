package it.unibas.bartgui.view.panel;

import bart.comparison.repairs.ComputeQualityOfRepairs;
import bart.comparison.repairs.PrecisionAndRecall;
import java.awt.FileDialog;
import java.awt.Font;
import java.io.File;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.windows.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompareChangesDialog extends javax.swing.JDialog {

    private final static Logger logger = LoggerFactory.getLogger(CompareChangesDialog.class);
    private ComputeQualityOfRepairs comparator = new ComputeQualityOfRepairs();

    public CompareChangesDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        txtExpected.getDocument().addDocumentListener(new TextFieldListener());
        txtGenerated.getDocument().addDocumentListener(new TextFieldListener());
    }

    public void showDialog() {
        this.pack();
        setLocationRelativeTo(this.getParent());
        this.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        txtExpected = new javax.swing.JTextField();
        btnChooseExpected = new javax.swing.JButton();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        txtGenerated = new javax.swing.JTextField();
        btnChooseGenerated = new javax.swing.JButton();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        comboScore = new javax.swing.JComboBox<>();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        txtVariablePrefixes = new javax.swing.JTextField();
        btnCompare = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(CompareChangesDialog.class, "CompareChangesDialog.title")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CompareChangesDialog.class, "CompareChangesDialog.jLabel1.text")); // NOI18N

        txtExpected.setText(org.openide.util.NbBundle.getMessage(CompareChangesDialog.class, "CompareChangesDialog.txtExpected.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnChooseExpected, org.openide.util.NbBundle.getMessage(CompareChangesDialog.class, "CompareChangesDialog.btnChooseExpected.text")); // NOI18N
        btnChooseExpected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseExpectedActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CompareChangesDialog.class, "CompareChangesDialog.jLabel2.text")); // NOI18N

        txtGenerated.setText(org.openide.util.NbBundle.getMessage(CompareChangesDialog.class, "CompareChangesDialog.txtGenerated.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnChooseGenerated, org.openide.util.NbBundle.getMessage(CompareChangesDialog.class, "CompareChangesDialog.btnChooseGenerated.text")); // NOI18N
        btnChooseGenerated.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseGeneratedActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(CompareChangesDialog.class, "CompareChangesDialog.jLabel3.text")); // NOI18N

        comboScore.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0.0", "0.5", "1.0" }));
        comboScore.setSelectedIndex(1);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(CompareChangesDialog.class, "CompareChangesDialog.jLabel4.text")); // NOI18N

        txtVariablePrefixes.setText(org.openide.util.NbBundle.getMessage(CompareChangesDialog.class, "CompareChangesDialog.txtVariablePrefixes.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtExpected)
                        .addGap(0, 0, 0)
                        .addComponent(btnChooseExpected, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtGenerated)
                        .addGap(0, 0, 0)
                        .addComponent(btnChooseGenerated, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(comboScore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtVariablePrefixes, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtExpected, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnChooseExpected))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtGenerated, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnChooseGenerated))
                .addGap(3, 3, 3)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(comboScore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtVariablePrefixes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnCompare.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnCompare, org.openide.util.NbBundle.getMessage(CompareChangesDialog.class, "CompareChangesDialog.btnCompare.text")); // NOI18N
        btnCompare.setEnabled(false);
        btnCompare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompareActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnCancel, org.openide.util.NbBundle.getMessage(CompareChangesDialog.class, "CompareChangesDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCompare, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCompare)
                    .addComponent(btnCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnChooseExpectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseExpectedActionPerformed
        String fileName = getFileName();
        if (fileName != null) {
            txtExpected.setText(fileName);
        }
        enableActions();
    }//GEN-LAST:event_btnChooseExpectedActionPerformed

    private void btnChooseGeneratedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseGeneratedActionPerformed
        String fileName = getFileName();
        if (fileName != null) {
            txtGenerated.setText(fileName);
        }
        enableActions();
    }//GEN-LAST:event_btnChooseGeneratedActionPerformed

    private void btnCompareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompareActionPerformed
        String fileGenerated = txtGenerated.getText();
        String fileExpected = txtExpected.getText();
        String error = validate(fileGenerated, fileExpected);
        if (!error.isEmpty()) {
            showErrorMessage(error);
            return;
        }
        String[] variablePrefixes = extractVariablePrefixes();
        long start = new Date().getTime();
        double scoreForVariable = Double.parseDouble(comboScore.getSelectedItem().toString());
        List<PrecisionAndRecall> listPrecisionAndRecall = comparator.calculatePrecisionAndRecallValue(fileGenerated, fileExpected, scoreForVariable, variablePrefixes);
        Collections.sort(listPrecisionAndRecall);
        PrecisionAndRecall max = listPrecisionAndRecall.get(0);
        long end = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug(max.toString());
        StringBuilder result = new StringBuilder();
        result.append(" Expected:  ").append(fileExpected).append("\n");
        result.append(" Generated: ").append(fileGenerated).append("\n");
        result.append("--------------------\n");
        NumberFormat nf = new DecimalFormat("0.####");
        result.append(" # Precision: ").append(nf.format(max.getPrecision())).append("\n");
        result.append(" # Recall:    ").append(nf.format(max.getRecall())).append("\n");
        result.append(" # F-Measure: ").append(nf.format(max.getfMeasure())).append("\n");
        result.append("--------------------\n");
        result.append(" Comparison time: ").append(end - start).append("ms\n");
        result.append("--------------------\n");
        showResults(result.toString());
    }//GEN-LAST:event_btnCompareActionPerformed

    private String validate(String fileGenerated, String fileExpected) {
        StringBuilder sb = new StringBuilder();
        if (fileGenerated.trim().isEmpty()) {
            sb.append("Generated changes file is required.\n");
        } else {
            File file = new File(fileGenerated);
            if (!file.exists()) {
                sb.append("File ").append(file).append(" doesn't exist.\n");
            }
        }
        if (fileExpected.trim().isEmpty()) {
            sb.append("Expected changes file is required.\n");
        } else {
            File file = new File(fileExpected);
            if (!file.exists()) {
                sb.append("File ").append(file).append(" doesn't exist.\n");
            }
        }
        return sb.toString();
    }

    private String[] extractVariablePrefixes() {
        String[] result = txtVariablePrefixes.getText().split(",");
        for (int i = 0; i < result.length; i++) {
            result[i] = result[i].trim();
        }
        return result;
    }

    private void enableActions() {
        this.btnCompare.setEnabled(!txtExpected.getText().isEmpty() && !txtGenerated.getText().isEmpty());
    }

    private String getFileName() {
        JFrame mainFrame = (JFrame) WindowManager.getDefault().getMainWindow();
        FileDialog fileDialog = new FileDialog(mainFrame, new File(System.getProperty("user.home")).toString());
        fileDialog.setTitle("Export changes in cvs file");
        fileDialog.setFile("expected.csv");
        fileDialog.setMode(FileDialog.LOAD);
        fileDialog.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".csv"));
            }

        });
        fileDialog.setVisible(true);
        String filename = fileDialog.getFile();
        if (filename == null) {
            return null;
        }
        String dir = fileDialog.getDirectory();
        return dir + filename;
    }

    private void showErrorMessage(String error) {
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showResults(String results) {
        JDialog dialog = new JDialog(this, true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        JTextArea textArea = new JTextArea(results);
        textArea.setEditable(false);
        textArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        dialog.setContentPane(new JScrollPane(textArea));
        dialog.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnChooseExpected;
    private javax.swing.JButton btnChooseGenerated;
    private javax.swing.JButton btnCompare;
    private javax.swing.JComboBox<String> comboScore;
    private javax.swing.JTextField txtExpected;
    private javax.swing.JTextField txtGenerated;
    private javax.swing.JTextField txtVariablePrefixes;
    // End of variables declaration//GEN-END:variables

    class TextFieldListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            enableActions();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            enableActions();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            enableActions();
        }

    }

}
