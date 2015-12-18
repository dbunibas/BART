package it.unibas.bartgui.view.panel.editor.database.visual;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Attribute;
import speedy.model.database.Cell;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class TableDataModel extends DefaultTableModel   {
    
    
    
    private ITable table;
    private List<Attribute> attribute;
    private List<Tuple> tuples;
    private Map<Cell,IValue> cellChanges;

    public TableDataModel(ITable table) {
        this.table = table;
        attribute = table.getAttributes();
    }
    
    public void clear()   {
        if(tuples == null)return;
        tuples.clear();
        fireTableDataChanged();
    }

    public void setPage(ITupleIterator iterator)   {
        if(iterator == null)return;
        if(tuples == null)tuples = new ArrayList<Tuple>();
        tuples.clear();
        while(iterator.hasNext())   {
            tuples.add(iterator.next());
        }  
        iterator.close();
        fireTableDataChanged();
    }
        
    public void setCellChanges(Map<Cell,IValue> cell)   {
        cellChanges = null;
        cellChanges = cell;
        fireTableDataChanged();
    }
    
    @Override
    public int getColumnCount() {
        if(attribute == null) return 0;
        return attribute.size();
    }

    @Override
    public String getColumnName(int column) {
        if(table == null) return null;
        try{
            String name = attribute.get(column).getName();
            return name;
        }catch(IndexOutOfBoundsException ex)   {
            return null;
        }
    }

    @Override
    public int getRowCount() {
        if(tuples == null)return 0;
        return tuples.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        try{
            Tuple t = tuples.get(row);
            for(Cell c : t.getCells())   {
                if(c.getAttribute().equals(getColumnName(column)))   {
                    if(getColumnName(column).equalsIgnoreCase("oid"))   {
                        return c.getTupleOID().getNumericalValue();
                    }
                    if(cellChanges != null)   {
                        Iterator<Cell> it = cellChanges.keySet().iterator();
                        while(it.hasNext())   {
                            Cell change = it.next();
                            if(c.getTupleOID().equals(change.getTupleOID()) &&
                               c.getAttribute().equals(change.getAttribute()))   {
                                StringBuilder sb = new StringBuilder();
                                sb.append(c.getValue().getPrimitiveValue());
                                sb.append("  ( ");
                                sb.append(cellChanges.get(change).getPrimitiveValue());
                                sb.append(" )");
                                sb.append(" <!CellChanged!>");
                                return sb.toString();
                            }
                        }         
                    }
                    return c.getValue().getPrimitiveValue();
                }
            }
        }catch(Exception ex)   {
            return null;
        }
        return null;
    }    

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(getColumnName(columnIndex).equalsIgnoreCase("oid"))   {
            return Long.class;
        }
        return super.getColumnClass(columnIndex);
    }
    
    
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        super.addTableModelListener(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        super.removeTableModelListener(l);
    }
    
    


}
