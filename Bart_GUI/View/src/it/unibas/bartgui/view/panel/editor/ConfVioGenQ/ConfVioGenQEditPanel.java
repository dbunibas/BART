package it.unibas.bartgui.view.panel.editor.ConfVioGenQ;

import it.unibas.bartgui.resources.R;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.swing.ValidationPanel;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class ConfVioGenQEditPanel extends JPanel{

    private JButton okButton;
    private JButton cancelButton;
    private JXTitledSeparator title;
    private ValidationPanel panelValAcc;
    private ValidationGroup vg;
    private ConfVioGenQPanel panel;
    
    public ConfVioGenQEditPanel() {
        setLayout(new BorderLayout());
        //InitTitle();
        InitButton();
        initPanel();
        //add(title,BorderLayout.NORTH); 
        add(panelValAcc,BorderLayout.CENTER);        
    }
    
    private void initPanel()   {
        panelValAcc = new ValidationPanel();
        getPanelValAcc().setBorder(new TitledBorder(new LineBorder(Color.BLACK), "VioGenQuery Configuration", 
                                                        TitledBorder.CENTER,TitledBorder.TOP));
        panel = new ConfVioGenQPanel();
        getPanelValAcc().setInnerComponent(panel);
        vg = getPanelValAcc().getValidationGroup();
        vg.add(panel.getPercentagjTextField1(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getQueryExecutorjTextField1(), StringValidators.NO_WHITESPACE);
        vg.add(panel.getMaxNumberOfRowsForSingleTupleQueriesjTextField1(),StringValidators.REQUIRE_VALID_INTEGER);
        
        vg.add(panel.getSizeFactorForStandardQueriesTextField(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getSizeFactorForSymmetricQueriesTextField(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getSizeFactorForInequalityQueriesTextField(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getSizeFactorForSingleTupleQueriesTextField(), StringValidators.REQUIRE_VALID_NUMBER);
        
        vg.add(panel.getProbabilityFactorForStandardQueriesTextField(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getProbabilityFactorForSymmetricQueriesTextField(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getProbabilityFactorForInequalityQueriesTextField(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getProbabilityFactorForSingleTupleQueriesTextField(), StringValidators.REQUIRE_VALID_NUMBER);
        
        vg.add(panel.getWindowSizeFactorForStandardQueriesTextField(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getWindowSizeFactorForSymmetricQueriesjTextField1(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getWindowSizeFactorForInequalityQueriesjTextField1(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getWindowSizeFactorForSingleTupleQueriesjTextField1(), StringValidators.REQUIRE_VALID_NUMBER);
        
        vg.add(panel.getOffsetFactorForStandardQueriesjTextField1(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getOffsetFactorForSymmetricQueriesjTextField1(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getOffsetFactorForInequalityQueriesjTextField1(), StringValidators.REQUIRE_VALID_NUMBER);
        vg.add(panel.getOffsetFactorForSingleTupleQueriesjTextField1(), StringValidators.REQUIRE_VALID_NUMBER);
        
        for(Document d : panel.getAllDocument())   {
            d.addDocumentListener(WeakListeners.create(DocumentListener.class, new ValidDocListener(), panel));
        }
    }

    /*private void InitTitle()   {
        title = new JXTitledSeparator();
        getTitle().setIcon(ImageUtilities.image2Icon(ImageUtilities.loadImage(R.IMAGE_SETTINGS_Blu)));
        getTitle().setHorizontalAlignment(SwingConstants.CENTER);
        getTitle().setForeground(Color.BLUE.darker());
        getTitle().setFont(new Font("Times New Roman", Font.ITALIC, 16));
        getTitle().setTitle("VioGenQuery Configuration");       
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
    public ConfVioGenQPanel getPanel() {
        return panel;
    }
    
    private class ValidDocListener implements DocumentListener   {
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
