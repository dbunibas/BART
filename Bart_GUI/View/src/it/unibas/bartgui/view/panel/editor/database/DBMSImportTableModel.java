/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.view.panel.editor.database;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import speedy.persistence.file.IImportFile;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DBMSImportTableModel extends DefaultTableModel   {

    private Map<String,List<IImportFile>> map = null;
    private String table;
    
    private JTable jTable;

    public DBMSImportTableModel(JTable jtable) {
        this.jTable = jtable;
    }
      

    public void initModel(String table, Map<String,List<IImportFile>> map) {
        this.table = table;
        this.map = map;
        fireTableDataChanged();
    }
     
    @Override
    public Object getValueAt(int row, int column) {
        if((map == null)||(map.isEmpty())) return null;
        if(column == 0) return map.get(table).get(row).getType();
        if(column == 1) {
            IImportFile file = map.get(table).get(row);
            TableCellRender r = new TableCellRender();
            //r.setToolTipText(file.getFileName());
            jTable.getColumn(getColumnName(column)).setCellRenderer(r);
            return file;
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        if(column == 0) return "Type";
        if(column == 1) return "File";
        return null;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public int getRowCount() {
        if(map == null)return 0;
        if(map.get(table) == null)return 0;
        return map.get(table).size();
    }
    
    public void addFile(IImportFile file)   {
        map.get(table).add(file);
        fireTableDataChanged();
    }
    
    public void removeFile(IImportFile file)   {
        map.get(table).remove(file);
        fireTableDataChanged();
    }
    
    public class TableCellRender extends DefaultTableCellRenderer   {

        private IImportFile file;
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            file = (IImportFile)value;
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setBackground(isSelected ? Color.BLUE : Color.WHITE);
            label.setForeground(isSelected ? Color.WHITE : Color.BLACK);
            label.setText(file.getFileName());
            setToolTipText(file.getFileName());
            return label;
        }    

        
    }
}
