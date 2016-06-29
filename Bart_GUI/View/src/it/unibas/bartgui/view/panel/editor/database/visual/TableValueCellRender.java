package it.unibas.bartgui.view.panel.editor.database.visual;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class TableValueCellRender extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String cell = (value == null ? "" : value.toString());
        if (cell.contains("<!CellChanged!>")) {
            String cellValue = cell.replace("<!CellChanged!>", "");
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setBackground(new Color(255, 92, 51));
            label.setForeground(Color.BLACK);
            label.setText(cellValue);
            return label;
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

}
