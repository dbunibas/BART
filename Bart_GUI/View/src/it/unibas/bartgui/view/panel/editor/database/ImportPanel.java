/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.view.panel.editor.database;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.swingx.JXTable;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import speedy.model.database.dbms.InitDBConfiguration;
import speedy.persistence.file.CSVFile;
import speedy.persistence.file.IImportFile;
import speedy.persistence.file.XMLFile;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class ImportPanel extends javax.swing.JPanel {

    private JList<String> jListTables; 
    private JXTable jTableFiles;
    private DBMSImportTableModel jTableFilesTableModel;
    private DefaultListModel<String> jListTablesListModel;
    private Map<String,List<IImportFile>> map = new HashMap<>();
    
    
    public ImportPanel() {
        initComponents();
        init();
        initJListTables();
        initJTableFiles();
        initListenerButton();
    }
       
    public void setDataListModel(InitDBConfiguration conf)   {
        if(conf == null) return;
        Iterator<String> it = conf.getTablesToImport().iterator();
        while(it.hasNext())   {
            String tab = it.next();
            map.put(tab, new ArrayList<>(conf.getFilesToImport(tab)));
            jListTablesListModel.addElement(tab);
        }
    }
    
    private void initJTableFiles()   {
        jTableFiles = new JXTable();
        jTableFilesTableModel = new DBMSImportTableModel(jTableFiles);
        jTableFiles.setColumnControlVisible(true);
        jTableFiles.setEditable(false);
        jTableFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableFiles.setModel(jTableFilesTableModel);
        jTableFiles.setShowGrid(true);
        jTableFiles.setDragEnabled(false); 
        jTableFiles.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(jTableFiles.getSelectedRow() < 0)   {
                    delFileButton.setEnabled(false);
                }else{
                    delFileButton.setEnabled(true);
                }
            }
        });
        scrollPaneFiles.setViewportView(jTableFiles);   
    }
    
    private void initJListTables()   {
        jListTablesListModel = new DefaultListModel<>();
        jListTables = new JList<>(jListTablesListModel);
        jListTables.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jListTables.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(jListTables.getSelectedIndex() < 0)   {
                    delTableButton.setEnabled(false);
                    addFileButton.setEnabled(false);
                    delFileButton.setEnabled(false);
                    jTableFilesTableModel.initModel("",new HashMap<String, List<IImportFile>>());
                }else{
                    addFileButton.setEnabled(true);
                    delTableButton.setEnabled(true);
                    jTableFilesTableModel.initModel(jListTables.getSelectedValue(), map);
                }
            }
        });
        scrollPaneTables.setViewportView(jListTables);
    }
    
    private void initButtonImportFile(final ImportFilePanel panel)   {
        for(Object o : panel.getButtons())   {
            ((JButton)o).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(e.getActionCommand().equalsIgnoreCase("OK"))   {;                   
                        if(panel.isXML())   {
                            if(!panel.getPath().isEmpty())jTableFilesTableModel.addFile(new XMLFile(panel.getPath()));
                        }
                        if(panel.isCSV())   {
                            CSVFile csvFile = new CSVFile(panel.getPath());
                            csvFile.setQuoteCharacter(panel.getQuote().charAt(0));
                            csvFile.setSeparator(panel.getQuote().charAt(0));
                            jTableFilesTableModel.addFile(csvFile);
                        }
                    }
                }
            });
        }
    }
    
    private void initListenerButton()   {
        addFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImportFilePanel panel = new ImportFilePanel();
                initButtonImportFile(panel);
                Dialog d = createDialog(panel, panel.getButtons());
                d.setTitle("Import File");
                d.setVisible(true);
            }
        });
        
        addTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NotifyDescriptor.InputLine input = new NotifyDescriptor
                        .InputLine("Table Name", 
                                   "Insert Table Name",
                                   NotifyDescriptor.OK_CANCEL_OPTION,
                                   NotifyDescriptor.PLAIN_MESSAGE);
                DialogDisplayer.getDefault().notify(input);
                if((input.getInputText() != null) && (!input.getInputText().isEmpty()))   {
                    map.put(input.getInputText().trim(), new ArrayList<IImportFile>());
                    jListTablesListModel.addElement(input.getInputText().trim());
                }
            }
        });
        
        delFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = jTableFilesTableModel.getValueAt(jTableFiles.getSelectedRow(), 1);
                IImportFile fileToRemove = (obj != null) ? ((IImportFile)obj) : null;
                if(fileToRemove != null)   {
                    jTableFilesTableModel.removeFile(fileToRemove);
                }                       
            }
        });
        
        delTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableToDel = jListTables.getSelectedValue();
                if(tableToDel != null)     {
                    jListTablesListModel.removeElement(tableToDel);
                    jTableFilesTableModel.initModel("",new HashMap<String, List<IImportFile>>());
                    map.remove(tableToDel);
                }
            }
        });
    }
    
    private Dialog createDialog(Object innerPane, Object[] options)   {
         DialogDescriptor dsc = new DialogDescriptor(innerPane, 
                                null, 
                                true, 
                                options, 
                                null,
                                DialogDescriptor.DEFAULT_ALIGN, 
                                HelpCtx.DEFAULT_HELP, 
                                null);
        return DialogDisplayer.getDefault().createDialog(dsc);
    }
    
    private void init()   {
        createtableCheckBox.setSelected(false);
        delTableButton.setEnabled(false);
        delFileButton.setEnabled(false);
        addFileButton.setEnabled(false);
    }

    /**
     * @return the map
     */
    public Map<String,List<IImportFile>> getMapFileImport() {
        return map;
    }
    
    private class AddFileListener implements ActionListener   {

        @Override
        public void actionPerformed(ActionEvent e) {
            
        }
        
    }

    public boolean isCreatetable() {
        return createtableCheckBox.isSelected();
    }

    public void setCreatetable(boolean create) {
        createtableCheckBox.setSelected(create);
    }
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        createtableCheckBox = new javax.swing.JCheckBox();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        scrollPaneTables = new javax.swing.JScrollPane();
        addTableButton = new javax.swing.JButton();
        delTableButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        scrollPaneFiles = new javax.swing.JScrollPane();
        addFileButton = new javax.swing.JButton();
        delFileButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 255))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(createtableCheckBox, org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.createtableCheckBox.text")); // NOI18N
        createtableCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        jSplitPane1.setDividerLocation(150);

        scrollPaneTables.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.scrollPaneTables.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 204))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addTableButton, org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.addTableButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(delTableButton, org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.delTableButton.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(addTableButton)
                .addGap(18, 18, 18)
                .addComponent(delTableButton)
                .addGap(0, 29, Short.MAX_VALUE))
            .addComponent(scrollPaneTables)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(scrollPaneTables, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addTableButton)
                    .addComponent(delTableButton)))
        );

        jSplitPane1.setLeftComponent(jPanel1);

        scrollPaneFiles.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.scrollPaneFiles.border.title"), javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 204))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(addFileButton, org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.addFileButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(delFileButton, org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.delFileButton.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 71, Short.MAX_VALUE)
                .addComponent(addFileButton)
                .addGap(18, 18, 18)
                .addComponent(delFileButton)
                .addContainerGap())
            .addComponent(scrollPaneFiles)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(scrollPaneFiles, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addFileButton)
                    .addComponent(delFileButton)))
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(createtableCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(createtableCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSplitPane1)
                .addGap(3, 3, 3))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFileButton;
    private javax.swing.JButton addTableButton;
    private javax.swing.JCheckBox createtableCheckBox;
    private javax.swing.JButton delFileButton;
    private javax.swing.JButton delTableButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JScrollPane scrollPaneFiles;
    private javax.swing.JScrollPane scrollPaneTables;
    // End of variables declaration//GEN-END:variables

}
