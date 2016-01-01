/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.view.panel.editor.dirtyStrategies;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.swing.ValidationPanel;
import speedy.model.database.Attribute;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;

/**
 *
 * @author Musicrizz
 */
@SuppressWarnings("unchecked")
public class DirtyStrategyPanel extends javax.swing.JPanel {

    private JButton okButton;
    private JButton cancelButton;
    private ButtonGroup bg;
    private ValidationPanel panelValidation;
    private ValidationGroup vg;
    /**
     * Creates new form DirtyStrategyPanel
     */
    public DirtyStrategyPanel() {
        initComponents();
        init();
        initButtons();
        initValidation();
        typoAddStringRadio.doClick();
    }
    
    
    
    private void initButtons()   {
        okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("Cancel");
    }
    
    public Object[] getButtons()   {
        Object[] obj = {
            okButton,
            cancelButton,
        };
        return obj;
    }
    
    private void initValidation()   {
        panelValidation = new ValidationPanel();
        panelValidation.setInnerComponent(this);
        panelValidation.setBorder(new TitledBorder(new LineBorder(Color.BLUE), "Strategies", 
                                                        TitledBorder.CENTER,TitledBorder.TOP));
        
        vg = panelValidation.getValidationGroup();
        vg.add(charsTextField, StringValidators.REQUIRE_NON_EMPTY_STRING,
                               StringValidators.NO_WHITESPACE);
        vg.add(charTOAddTextField, StringValidators.REQUIRE_NON_EMPTY_STRING,
                                   StringValidators.NO_WHITESPACE,
                                   StringValidators.REQUIRE_VALID_INTEGER);
        vg.add(charsToRemoveTextField, StringValidators.REQUIRE_NON_EMPTY_STRING,
                                   StringValidators.NO_WHITESPACE,
                                   StringValidators.REQUIRE_VALID_INTEGER);
        vg.add(charToSwitchTextField, StringValidators.REQUIRE_NON_EMPTY_STRING,
                                   StringValidators.NO_WHITESPACE,
                                   StringValidators.REQUIRE_VALID_INTEGER);
        
        charsTextField.getDocument()
                .addDocumentListener(new ValidatorDocListener());
        charTOAddTextField.getDocument()
                .addDocumentListener(new ValidatorDocListener());
        charsToRemoveTextField.getDocument()
                .addDocumentListener(new ValidatorDocListener());
        charToSwitchTextField.getDocument()
                .addDocumentListener(new ValidatorDocListener());
    }
    
    private void init()   {
        bg = new ButtonGroup();
        bg.add(typoAddStringRadio);
        bg.add(typoAppendStringRadio);
        bg.add(typoRandomRadio);
        bg.add(typoRemoveStringRadio);
        bg.add(typoSwitchValueRadio);
        
        charsTextField.setName("Chars");
        charTOAddTextField.setName("Num Chars To Add");
        charsToRemoveTextField.setName("Num Chars To Remove");
        charToSwitchTextField.setName("Num Chars To Switch");        
        
        typoAddStringRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                charsTextField.setEnabled(true);
                charTOAddTextField.setEnabled(true);
                charsToRemoveTextField.setEnabled(false);
                charToSwitchTextField.setEnabled(false);
                forceValidation();
            }
        });
        
        typoAppendStringRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                charsTextField.setEnabled(true);
                charTOAddTextField.setEnabled(true);
                charsToRemoveTextField.setEnabled(false);
                charToSwitchTextField.setEnabled(false);
                forceValidation();
            }
        });
        
        typoRandomRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                charsTextField.setEnabled(false);
                charTOAddTextField.setEnabled(false);
                charsToRemoveTextField.setEnabled(false);
                charToSwitchTextField.setEnabled(false);
                forceValidation();
            }
        });
        
        typoRemoveStringRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                charsTextField.setEnabled(false);
                charTOAddTextField.setEnabled(false);
                charsToRemoveTextField.setEnabled(true);
                charToSwitchTextField.setEnabled(false);
                forceValidation();
            }
        });
        
        typoSwitchValueRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                charsTextField.setEnabled(false);
                charTOAddTextField.setEnabled(false);
                charsToRemoveTextField.setEnabled(false);
                charToSwitchTextField.setEnabled(true);
                forceValidation();
            }
        });
        
    }

    public String getCharTOAdd() {
        return charTOAddTextField.getText();
    }

    public String getCharToSwitch() {
        return charToSwitchTextField.getText();
    }

    public String getChars() {
        return charsTextField.getText();
    }

    public String getCharsToRemove() {
        return charsToRemoveTextField.getText();
    }
    
    public void setCharTOAdd(int i) {
        charTOAddTextField.setText(i+"");
    }

    public void setCharToSwitch(int i) {
         charToSwitchTextField.setText(i+"");
    }

    public void setChars(String c) {
         charsTextField.setText(c);
    }

    public void setCharsToRemove(int i) {
         charsToRemoveTextField.setText(i+"");
    }

    public boolean isTypoAddString() {
        return typoAddStringRadio.isSelected();
    }

    public boolean isTypoAppendString() {
        return typoAppendStringRadio.isSelected();
    }

    public boolean isTypoRandom() {
        return typoRandomRadio.isSelected();
    }

    public boolean isTypoRemoveString() {
        return typoRemoveStringRadio.isSelected();
    }

    public boolean isTypoSwitchValue() {
        return typoSwitchValueRadio.isSelected();
    }
    
    public void setTypoAddString() {
         typoAddStringRadio.doClick();
    }

    public void setTypoAppendString() {
         typoAppendStringRadio.doClick();
    }

    public void setTypoRandom() {
         typoRandomRadio.doClick();
    }

    public void setTypoRemoveString() {
         typoRemoveStringRadio.doClick();
    }

    public void setTypoSwitchValue() {
         typoSwitchValueRadio.doClick();
    }
    
    private void forceValidation() {
        Problem validateAll = vg.performValidation();
        if (validateAll != null) {
            errorLabel.setText(validateAll.getMessage());
            okButton.setEnabled(false);
        } else {
            errorLabel.setText("");
            okButton.setEnabled(true);
        }
    }
    
    private void initListnerCombo(final IDatabase db)   {
        tableComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ITable table = db.getTable(getTable());
                initAttributeCombo(table);
            }
        });
    }
    
    public void initTableCombo(IDatabase db)   {
        tableComboBox.removeAllItems();
        for(String s : db.getTableNames())   {
            tableComboBox.addItem(s);
        }
        tableComboBox.setSelectedIndex(0);
        ITable table = db.getTable(tableComboBox.getItemAt(0));
        initAttributeCombo(table);
        initListnerCombo(db);
    }
    
    public void initTableCombo(String tb,IDatabase db)   {
        tableComboBox.removeAllItems();
        tableComboBox.addItem(tb);
        tableComboBox.setSelectedIndex(0);
        ITable table = db.getTable(tb);
        initAttributeCombo(table);
        initListnerCombo(db);
    }
    
    private void initAttributeCombo(ITable table)   {
        attributeComboBox.removeAllItems();
        if(!table.getAttributes().isEmpty())   {
            for(Attribute a : table.getAttributes())   {
                if(a.getName().equals("oid"))continue;
                attributeComboBox.addItem(a.getName());
            }
            attributeComboBox.setSelectedIndex(0);
        }
    }
    
    public String getTable()   {
        return(String)tableComboBox.getSelectedItem();
    }
    
    public String getAttibute()   {
        return(String)attributeComboBox.getSelectedItem();
    }
    
    private class ValidatorDocListener implements DocumentListener   {
        @Override
        public void insertUpdate(DocumentEvent e) {
            checkValidation();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkValidation();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            checkValidation();
        }
        
        private void checkValidation() {
            Problem validateAll = vg.performValidation();
                if (validateAll != null) {
                    errorLabel.setText(validateAll.getMessage());
                    okButton.setEnabled(false);
                } else {
                    errorLabel.setText("");
                    okButton.setEnabled(true);
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

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        typoAddStringRadio = new javax.swing.JRadioButton();
        typoAppendStringRadio = new javax.swing.JRadioButton();
        typoRandomRadio = new javax.swing.JRadioButton();
        typoRemoveStringRadio = new javax.swing.JRadioButton();
        typoSwitchValueRadio = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        charsTextField = new javax.swing.JTextField();
        charTOAddTextField = new javax.swing.JTextField();
        charsToRemoveTextField = new javax.swing.JTextField();
        charToSwitchTextField = new javax.swing.JTextField();
        errorLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        tableComboBox = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        attributeComboBox = new javax.swing.JComboBox<>();

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.jPanel3.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(typoAddStringRadio, org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.typoAddStringRadio.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(typoAppendStringRadio, org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.typoAppendStringRadio.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(typoRandomRadio, org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.typoRandomRadio.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(typoRemoveStringRadio, org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.typoRemoveStringRadio.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(typoSwitchValueRadio, org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.typoSwitchValueRadio.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(typoAddStringRadio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(typoAppendStringRadio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(typoRandomRadio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(typoRemoveStringRadio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(typoSwitchValueRadio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(typoAddStringRadio)
                .addGap(18, 18, 18)
                .addComponent(typoAppendStringRadio)
                .addGap(18, 18, 18)
                .addComponent(typoRandomRadio)
                .addGap(18, 18, 18)
                .addComponent(typoRemoveStringRadio)
                .addGap(18, 18, 18)
                .addComponent(typoSwitchValueRadio)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.jLabel4.text")); // NOI18N

        charsTextField.setText(org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.charsTextField.text")); // NOI18N

        charTOAddTextField.setText(org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.charTOAddTextField.text")); // NOI18N

        charsToRemoveTextField.setText(org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.charsToRemoveTextField.text")); // NOI18N

        charToSwitchTextField.setText(org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.charToSwitchTextField.text")); // NOI18N

        errorLabel.setForeground(java.awt.Color.red);
        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.errorLabel.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(charsToRemoveTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(charTOAddTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(charsTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(charToSwitchTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(charsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(charTOAddTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(charsToRemoveTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(charToSwitchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.jPanel4.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.jLabel5.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(DirtyStrategyPanel.class, "DirtyStrategyPanel.jLabel6.text")); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(attributeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tableComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(attributeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> attributeComboBox;
    private javax.swing.JTextField charTOAddTextField;
    private javax.swing.JTextField charToSwitchTextField;
    private javax.swing.JTextField charsTextField;
    private javax.swing.JTextField charsToRemoveTextField;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JComboBox<String> tableComboBox;
    private javax.swing.JRadioButton typoAddStringRadio;
    private javax.swing.JRadioButton typoAppendStringRadio;
    private javax.swing.JRadioButton typoRandomRadio;
    private javax.swing.JRadioButton typoRemoveStringRadio;
    private javax.swing.JRadioButton typoSwitchValueRadio;
    // End of variables declaration//GEN-END:variables
}
