package it.unibas.bartgui.view.panel.editor.database;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
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
public class DbmsEditPanel extends JPanel  {

    private JButton okButton;
    private JButton cancelButton;
    private JXTitledSeparator title;
    private AccesConfPanel panelAcc;
    private ImportPanel panelImport;
    private InitDBPanel panelInitDb;
    private ValidationPanel panelValAcc;
    private ValidationGroup vg;
    
    public DbmsEditPanel() {
        setLayout(new BorderLayout());
        //title = new JXTitledSeparator();
        //InitTitle();
        InitButton();
        initImportPanel();
        initPanelInitDB();
        initAccPanel();
        //add(title,BorderLayout.NORTH);
        add(initLayout(),BorderLayout.CENTER);
    }
    
    private JPanel initLayout()   {
        JPanel up = new JPanel(new GridLayout(1, 2));
            up.add(panelValAcc);
            up.add(getPanelImport());
        JPanel complete = new JPanel(new BorderLayout());
        complete.add(up,BorderLayout.NORTH);
        complete.add(panelInitDb, BorderLayout.CENTER);
        return complete;
    }
    
    private void initAccPanel()   {
        panelValAcc = new ValidationPanel();
        panelValAcc.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Access Configuration", TitledBorder.CENTER,TitledBorder.TOP));
        panelAcc = new AccesConfPanel();
        panelValAcc.setInnerComponent(getPanelAcc());
        vg = panelValAcc.getValidationGroup();
        vg.add(getPanelAcc().getDriverTextField(), StringValidators.REQUIRE_NON_EMPTY_STRING,
                                              StringValidators.NO_WHITESPACE);
        vg.add(getPanelAcc().getUriTextField(), StringValidators.REQUIRE_NON_EMPTY_STRING,
                                              StringValidators.NO_WHITESPACE);
        vg.add(getPanelAcc().getSchemaTextField(), StringValidators.REQUIRE_NON_EMPTY_STRING,
                                              StringValidators.NO_WHITESPACE);
        vg.add(getPanelAcc().getLoginTextField(), StringValidators.REQUIRE_NON_EMPTY_STRING);
        vg.add(getPanelAcc().getPasswordTextField(), StringValidators.REQUIRE_NON_EMPTY_STRING);
        vg.add(panelInitDb.getScriptArea(), StringValidators.REQUIRE_NON_EMPTY_STRING);
        getPanelAcc().getDriverTextField()
                .getDocument()
                .addDocumentListener(new ValidatorDocumentListener());
        getPanelAcc().getUriTextField()
                .getDocument()
                .addDocumentListener(new ValidatorDocumentListener());
        getPanelAcc().getSchemaTextField()
                .getDocument()
                .addDocumentListener(new ValidatorDocumentListener());
        getPanelAcc().getLoginTextField()
                .getDocument()
                .addDocumentListener(new ValidatorDocumentListener());
        getPanelAcc().getPasswordTextField()
                .getDocument()
                .addDocumentListener(new ValidatorDocumentListener());
        //panelInitDb.getScriptArea()
         //       .getDocument()
         //       .addDocumentListener(new ValidatorDocumentListener());
    }
    
    private void initImportPanel()   {
        panelImport = new ImportPanel();
    }
    
    private void initPanelInitDB()   {
        panelInitDb = new InitDBPanel();
    }
    
    private void InitButton()  {
        okButton = new JButton("OK");
        okButton.setEnabled(false);
        cancelButton = new JButton("Cancel");
    }
    
    /*private void InitTitle()   {
        title.setIcon(ImageUtilities.image2Icon(ImageUtilities.loadImage(R.IMAGE_SETTINGS_Blu)));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(Color.BLUE.darker());
        title.setFont(new Font("Times New Roman", Font.ITALIC, 16));
        title.setTitle("DBMS Configuration Settings");
        
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
     * @return the panelAcc
     */
    public AccesConfPanel getPanelAcc() {
        return panelAcc;
    }

    /**
     * @return the panelImport
     */
    public ImportPanel getPanelImport() {
        return panelImport;
    }

    /**
     * @return the panelInitDb
     */
    public InitDBPanel getPanelInitDb() {
        return panelInitDb;
    }
    
    private void forceValidation() {
            Problem validateAll = vg.performValidation();
                if (validateAll != null) {
                    okButton.setEnabled(false);
                } else {
                    okButton.setEnabled(true);
            }
        }   
    
    private class ValidatorDocumentListener implements DocumentListener   {
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
