package it.unibas.bartgui.view.panel.editor.Dependency.tableModel;

import bart.model.EGTask;
import bart.utility.BartUtility;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.view.panel.editor.Dependency.parseHtml.ParseUtil;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class VioGenQueryTableModel extends DefaultTableModel  {

    private Vector<VioGenQueryData> vectorVio;
    private EGTask egt;
    private EGTaskDataObjectDataObject dto;
    
    public VioGenQueryTableModel(EGTaskDataObjectDataObject dto,Vector<VioGenQueryData> vectorVio) {
        this.vectorVio = vectorVio;
        this.dto = dto;
        this.egt = dto.getEgtask();
        setDataVector(this.vectorVio);
    }
        
    private void setDataVector(Vector dataVector) {
        this.dataVector = dataVector;
    }
        
        
    @Override
    public Object getValueAt(int row, int column) {
        if(column == 0)return vectorVio.get(row).getId();
        if(column == 1)return vectorVio.get(row).getComparison();
        if(column == 2)return vectorVio.get(row).getPercentage();
        //if(column == 3)return vectorVio.get(row).getQueryExecutor();
        return null;
    }
   
    @Override
    public int getRowCount() {
        if(vectorVio == null) return 0;
        return super.getRowCount();
    }
        
    @Override
    public int getColumnCount() {
        return 3;
    }
        
    @Override
    public String getColumnName(int column) {
        if(column == 0)return "ID";
        if(column == 1)return "Comparison";
        if(column == 2)return "Percentage";
        //if(column == 3)return "Strategy";
        return null;
    }  

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        if(column == 2 /*|| column == 3*/)   {
            VioGenQueryData data = vectorVio.get(row);
            StringBuilder invComp = new StringBuilder();
            invComp.append("(");
            invComp.append(ParseUtil.invertComparison(data.getVioGenQuery()));
            invComp.append(")");
            String vioGenKey = BartUtility.getVioGenQueryKey(data.getId(),invComp.toString().trim());
            if(column == 2)   {
                try{
                    double percentage = Double.parseDouble((String)aValue);
                    if((percentage<0) || (percentage>100))return;
                    data.setPercentage(percentage);
                    egt.getConfiguration().addVioGenQueryProbabilities(vioGenKey, percentage);
                    dto.setEgtModified(true);
                    fireTableCellUpdated(row, column);
                }catch(Exception ex)  {
                    return;
                }
            }/*
            if(column == 3)   {
                data.setQueryExecutor((String)aValue);
                egt.getConfiguration().addVioGenQueryStrategy(vioGenKey, (String)aValue);
                fireTableCellUpdated(row, column); 
            }      */  
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if(column == 2)   {
            return true;
        }
       /* if(column == 3)   {
            return true;
        }*/
        return false;
    }
    
    
}
