package it.unibas.bartgui.view.panel.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import speedy.model.database.Cell;
import speedy.model.database.IValue;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class CellChangesTableModel extends DefaultTableModel  {
    
    private Map<Cell,IValue> cellChanges;
    private List<Cell> cell;
    
    
    public void setCellChanges(Map<Cell,IValue> cellChanges)   {
        this.cellChanges = null;
        this.cell = null;
        this.cellChanges = cellChanges;
        if(this.cellChanges != null)   {
            this.cell = new ArrayList<Cell>(this.cellChanges.keySet());
        }else{
            this.cell = new ArrayList<Cell>();
        }     
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(getColumnName(columnIndex).equals("Tuple OID"))   {
            return Long.class;
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int column) {
        if(column == 2)return "Attribute";
        if(column == 3)return "Old Value";
        if(column == 4)return "New Value";
        if(column == 1)return "Table";
        if(column == 0)return "Tuple OID";
        return null;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(cellChanges != null && cell != null)   {
            if(column == 2)return cell.get(row).getAttribute();
            if(column == 3)return cell.get(row).getValue().getPrimitiveValue();
            if(column == 4)return cellChanges.get(cell.get(row));
            if(column == 1)return cell.get(row).getAttributeRef().getTableName();
            if(column == 0)return cell.get(row).getTupleOID().getNumericalValue();
        }
        return null;
    }

    @Override
    public int getRowCount() {
        if(cellChanges != null)return cellChanges.size();
        return 0;
    }
    
    

}
