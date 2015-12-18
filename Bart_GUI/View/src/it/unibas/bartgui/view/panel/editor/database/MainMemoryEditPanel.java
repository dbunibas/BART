package it.unibas.bartgui.view.panel.editor.database;

import java.awt.BorderLayout;
import java.awt.Color;
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
@SuppressWarnings({"rawtypes","unchecked"})
public class MainMemoryEditPanel extends JPanel   {

    private JButton okButton;
    private JButton cancelButton;
    private JXTitledSeparator title;
    private ValidationPanel panelValAcc;
    private ValidationGroup vg;
    private MainMemoryPanel panelMainMem;
    
    public MainMemoryEditPanel() {
        setLayout(new BorderLayout());
        //InitTitle();
        InitButton();
        initValidator();
        //add(title,BorderLayout.NORTH);
        add(panelValAcc,BorderLayout.CENTER);
        
    }

    /*private void InitTitle()   {
        title = new JXTitledSeparator();
        title.setIcon(ImageUtilities.image2Icon(ImageUtilities.loadImage(R.IMAGE_SETTINGS_Blu)));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(Color.BLUE.darker());
        title.setFont(new Font("Times New Roman", Font.ITALIC, 16));
        title.setTitle("MainMemory Configuration Settings");       
    }*/
    
    private void initValidator()   {
        panelValAcc = new ValidationPanel();
        panelValAcc.setBorder(new TitledBorder(new LineBorder(Color.BLUE), "Main Memory Configuration", TitledBorder.CENTER,TitledBorder.TOP));
        panelMainMem = new MainMemoryPanel();
        panelValAcc.setInnerComponent(panelMainMem);
        vg = panelValAcc.getValidationGroup();
        vg.add(panelMainMem.getXmlSchemaTextField(), StringValidators.REQUIRE_NON_EMPTY_STRING,
                                                       StringValidators.NO_WHITESPACE
                                                       //StringValidators.FILE_MUST_EXIST
                                                               );
        vg.add(panelMainMem.getXmlInstanceTextField(), StringValidators.REQUIRE_NON_EMPTY_STRING,
                                                       StringValidators.NO_WHITESPACE
                                                       //StringValidators.FILE_MUST_EXIST 
                                                               );
        panelMainMem.getXmlInstanceTextField()
                .getDocument()
                .addDocumentListener(new ValidDocumentListener());
        panelMainMem.getXmlSchemaTextField()
                .getDocument()
                .addDocumentListener(new ValidDocumentListener());
    }
    
    private void InitButton()  {
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        okButton.setEnabled(false);
    }
    
    public Object[] getButtons()   {
        Object[] o = {getOkButton(),getCancelButton()};
        return o;
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
     * @return the panelMainMem
     */
    public MainMemoryPanel getPanelMainMem() {
        return panelMainMem;
    }
    
    private class ValidDocumentListener implements DocumentListener   {
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
