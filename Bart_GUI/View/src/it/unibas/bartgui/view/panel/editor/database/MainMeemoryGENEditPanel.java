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
public class MainMeemoryGENEditPanel extends JPanel  {

    private JButton okButton;
    private JButton cancelButton;
    private JXTitledSeparator title;
    private ValidationPanel panelValAcc;
    private ValidationGroup vg;
    private MainMemoryGeneratePanel memoryGeneratePanel;
    
    
    
    
    public MainMeemoryGENEditPanel() {
        setLayout(new BorderLayout());
        //InitTitle();
        InitButton();
        initValidator();
        //add(title,BorderLayout.NORTH);
        add(panelValAcc,BorderLayout.CENTER);
    }
    
    private void initValidator()   {
        panelValAcc = new ValidationPanel();
        panelValAcc.setBorder(new TitledBorder(new LineBorder(Color.BLUE), "Plain Instance", TitledBorder.CENTER,TitledBorder.TOP));
        memoryGeneratePanel = new MainMemoryGeneratePanel();
        panelValAcc.setInnerComponent(getMemoryGeneratePanel());
        vg = panelValAcc.getValidationGroup();
        vg.add(getMemoryGeneratePanel().getPlainInstanceArea(), StringValidators.REQUIRE_NON_EMPTY_STRING);
        getMemoryGeneratePanel().getPlainInstanceArea()
                .getDocument()
                .addDocumentListener(new ValidatorDocList());
    }
    
    private void InitButton()  {
        okButton = new JButton("OK");
        getOkButton().setEnabled(false);
        cancelButton = new JButton("Cancel");
    }
    
    /*private void InitTitle()   {
        title = new JXTitledSeparator();
        title.setIcon(ImageUtilities.image2Icon(ImageUtilities.loadImage(R.IMAGE_SETTINGS_Blu)));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(Color.BLUE.darker());
        title.setFont(new Font("Times New Roman", Font.ITALIC, 16));
        title.setTitle("MainMemory Generate Plain instance");     
    }*/
    
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
     * @return the memoryGeneratePanel
     */
    public MainMemoryGeneratePanel getMemoryGeneratePanel() {
        return memoryGeneratePanel;
    }

    private class ValidatorDocList implements DocumentListener   {
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
