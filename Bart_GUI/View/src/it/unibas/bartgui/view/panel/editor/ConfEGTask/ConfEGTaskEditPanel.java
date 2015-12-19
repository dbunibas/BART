package it.unibas.bartgui.view.panel.editor.ConfEGTask;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.swing.ValidationPanel;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@SuppressWarnings("unchecked")
public class ConfEGTaskEditPanel extends JPanel   {
    
    private JButton okButton;
    private JButton cancelButton;
    private JXTitledSeparator title;
    private ValidationPanel panelValAcc;
    private ValidationGroup vg;
    private ConfEGTaskPanel panel;

    public ConfEGTaskEditPanel() {
        setLayout(new BorderLayout());
        //InitTitle();
        InitButton();
        initPanel();
        initListenerCheckbox();
        //add(title,BorderLayout.NORTH); 
        add(panelValAcc,BorderLayout.CENTER);
    }
    
    private void initPanel()   {
        panelValAcc = new ValidationPanel();
        getPanelValAcc().setBorder(new TitledBorder(new LineBorder(Color.BLACK), "EGTask Configuration", 
                                                        TitledBorder.CENTER,TitledBorder.TOP));
        panel = new ConfEGTaskPanel();
        getPanelValAcc().setInnerComponent(panel);
        vg = getPanelValAcc().getValidationGroup();
        vg.add(panel.getExportCellChangesPathTextField(), StringValidators.REQUIRE_NON_EMPTY_STRING);
        vg.add(panel.getExportDirtyDbTypeTextField(),StringValidators.REQUIRE_NON_EMPTY_STRING);
        
        vg.add(panel.getExportDirtyDBPathTextField(), StringValidators.REQUIRE_NON_EMPTY_STRING);
        
        vg.add(panel.getCloneSuffixTextField(), StringValidators.REQUIRE_NON_EMPTY_STRING);
        
        //vg.add(panel.getQueryWxecutionTimeOutTextField(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getSizeFactorReductionTextField(), StringValidators.REQUIRE_VALID_NUMBER);
        
        
        panel.getExportCellChangesPathTextField()
                .getDocument()
                .addDocumentListener(new ValidatorDocListener());
        panel.getExportDirtyDbTypeTextField()
                .getDocument()
                .addDocumentListener(new ValidatorDocListener());
        panel.getExportDirtyDBPathTextField()
                .getDocument()
                .addDocumentListener(new ValidatorDocListener());
        panel.getCloneSuffixTextField()
                .getDocument()
                .addDocumentListener(new ValidatorDocListener());
        panel.getSizeFactorReductionTextField()
                .getDocument()
                .addDocumentListener(new ValidatorDocListener());
    }
    
    private void initListenerCheckbox()   {
        panel.getExportCellChangesCheckBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(panel.getExportCellChangesCheckBox().isSelected())   {
                    panel.getExportCellChangesPathTextField().setEditable(true);
                    panel.getExportCellChangesPathTextField().setEnabled(true); 
                    panel.getExportCellChangesButton().setEnabled(true);
                    forceValidation();
                }else{
                    panel.getExportCellChangesPathTextField().setEnabled(false);
                    panel.getExportCellChangesButton().setEnabled(false);     
                    forceValidation();
                }
            }
        });
        panel.getExportDirtyDBCheckBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(panel.getExportDirtyDBCheckBox().isSelected())   {
                    panel.getExportDirtyDbTypeTextField().setEnabled(true);
                    panel.getExportDirtyDBPathTextField().setEnabled(true);
                    panel.getExportDirtyDbButton().setEnabled(true);
                    forceValidation();
                }else{
                    panel.getExportDirtyDbTypeTextField().setEnabled(false);
                    panel.getExportDirtyDBPathTextField().setEnabled(false);
                    panel.getExportDirtyDbButton().setEnabled(false);
                    forceValidation();
                }
            }
        });
        panel.getCloneTargetSchemaCheckBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(panel.getCloneTargetSchemaCheckBox().isSelected())   {
                    panel.getCloneSuffixTextField().setEnabled(true);
                    forceValidation();
                }else{
                    panel.getCloneSuffixTextField().setEnabled(false);
                    forceValidation();
                }
            }
        });
    }
    

    
    /*private void InitTitle()   {
        title = new JXTitledSeparator();
        getTitle().setIcon(ImageUtilities.image2Icon(ImageUtilities.loadImage(R.IMAGE_SETTINGS_Blu)));
        getTitle().setHorizontalAlignment(SwingConstants.CENTER);
        getTitle().setForeground(Color.BLUE.darker());
        getTitle().setFont(new Font("Times New Roman", Font.ITALIC, 16));
        getTitle().setTitle("EGTask Configuration");       
    }*/

    public Object[] getButtons()   {
        Object[] o = {getOkButton(),getCancelButton()};
        return o;
    }
    
    private void InitButton()  {
        okButton = new JButton("OK");
        getOkButton().setEnabled(false);
        cancelButton = new JButton("Cancel");
    }

    /**
     * @return the okButton
     */
    public JButton getOkButton() {
        return okButton;
    }

    /**
     * @return the cancelButton
     */
    public JButton getCancelButton() {
        return cancelButton;
    }

    /**
     * @return the title
     */
    public JXTitledSeparator getTitle() {
        return title;
    }

    /**
     * @return the panelValAcc
     */
    public ValidationPanel getPanelValAcc() {
        return panelValAcc;
    }

    /**
     * @return the vg
     */
    public ValidationGroup getVg() {
        return vg;
    }

    /**
     * @return the panel
     */
    public ConfEGTaskPanel getPanel() {
        return panel;
    }
    
    private void forceValidation() {
            Problem validateAll = vg.performValidation();
                if (validateAll != null) {
                    okButton.setEnabled(false);
                } else {
                    okButton.setEnabled(true);
            }
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
                    okButton.setEnabled(false);
                } else {
                    okButton.setEnabled(true);
            }
        }
    }
}

