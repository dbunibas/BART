package it.unibas.bartgui.view.panel.editor.Dependency.parseHtml;

import bart.model.dependency.ComparisonAtom;
import bart.model.dependency.Dependency;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaVariableOccurrence;
import bart.model.dependency.IFormulaAtom;
import bart.model.dependency.VariableEquivalenceClass;
import bart.model.errorgenerator.operator.ExecuteVioGenQueryUtility;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import speedy.model.database.TableAlias;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class ParseDependency {

    private Map<String,String> variablesColorMap = new HashMap<>();
    private Map<TableAlias,List<FormulaVariableOccurrence>> tablesMap = new HashMap<>();
    private List<ComparisonAtom> comparison = new ArrayList<>();
    
    private Dependency dependency;

    public ParseDependency(Dependency dependency) {
        this.dependency = dependency;
        initMap();
    }
    
    private void initMap()   {
        int contColor=0;
        for(FormulaVariable v : dependency.getPremise().getLocalVariables())   {
            if(!variablesColorMap.containsKey(v.getId()))   {
                getVariablesColorMap().put(v.getId(), ParseUtil.pickColor(contColor++));
            }
            try{
                VariableEquivalenceClass vec = ExecuteVioGenQueryUtility.findVariableEquivalenceClass(v, dependency.getPremise());           
                for(FormulaVariable fv : vec.getVariables())   {          
                    if(!variablesColorMap.containsKey(fv.getId()))   {                    
                        getVariablesColorMap().put(fv.getId(), getVariablesColorMap().get(v.getId()));
                    }                      
                }
            }catch(Exception ex)   {
                
            }
            for(FormulaVariableOccurrence fvo : v.getRelationalOccurrences())   {
                if(tablesMap.containsKey(fvo.getTableAlias()))   {
                    tablesMap.get(fvo.getTableAlias()).add(fvo);
                }else{
                    List<FormulaVariableOccurrence> list = new ArrayList<>();
                    list.add(fvo);
                    tablesMap.put(fvo.getTableAlias(),list);
                }
            }
            for(IFormulaAtom fa : v.getNonRelationalOccurrences())   {
                if(fa instanceof ComparisonAtom)   {
                    ComparisonAtom comp = (ComparisonAtom)fa;
                    if(!ParseUtil.checkComparison(comparison,comp))   {
                        comparison.add(comp);
                    }                  
                }
            }
        }
    }    
      
    public  String parse()   {
        StringBuilder html = new StringBuilder("<html>");
        html.append(ParseUtil.ID_HTML_OpenTag);
        html.append(dependency.getId());
        html.append(ParseUtil.ID_HTML_CloseTag);
        
        Iterator<TableAlias> it = tablesMap.keySet().iterator();
        while(it.hasNext())   {
            TableAlias table = it.next();
            html.append("<p>");
            html.append(ParseUtil.TableName_HTML_OpenTag);
            html.append(table.getTableName());
            html.append(ParseUtil.TableName_HTML_CloseTag);
            html.append(ParseUtil.BrkltLeft_HTML);
            
            List<FormulaVariableOccurrence> listOcc = tablesMap.get(table);
            for(int i=0;i<listOcc.size();i++)   {
                FormulaVariableOccurrence fvo = listOcc.get(i);
                html.append(ParseUtil.Attribute_HTML_OpenTag);
                html.append(fvo.getAttributeRef().getName());
                html.append(ParseUtil.Attribute_HTML_CloseTag);               
                html.append(ParseUtil.getVariableHTML(getVariablesColorMap(),fvo.getVariableId().trim()));
                if(!(i == (listOcc.size()-1)))html.append(ParseUtil.Comma_HTML);
            }
            html.append(ParseUtil.BrkltRight_HTML);
            html.append(ParseUtil.Comma_HTML);
            html.append("</p>");
        }
        
        html.append("<p>   ");
        for(int i=0;i<comparison.size();i++)   {
            html.append(ParseUtil.BrkltLeft_HTML);
            ComparisonAtom c = comparison.get(i);
            if(c.isVariableComparison())   {
                html.append(ParseUtil.getVariableHTML(getVariablesColorMap(),c.getLeftArgument().trim()));
                html.append(ParseUtil.getOperatorHTML(c.getOperator().trim()));
                html.append(ParseUtil.getVariableHTML(getVariablesColorMap(),c.getRightArgument().trim()));
            }else{
                FormulaVariable tmp = null;
                if(c.getRightVariable() != null)   {
                    tmp = c.getRightVariable();
                }else{ 
                    tmp = c.getLeftVariable();
                }
                if(tmp == null)html.append("error in comparison");
                try{
                    html.append(ParseUtil.getVariableHTML(getVariablesColorMap(),tmp.getId().trim()));
                    html.append(ParseUtil.getOperatorHTML(c.getOperator().trim()));
                    Number num = NumberFormat.getInstance().parse(c.getConstant().trim());
                    html.append(ParseUtil.Number_HTML_OpenTag);
                    html.append(num);
                    html.append(ParseUtil.Number_HTML_CloseTag);
                }catch(Exception ex)  {
                    html.append(ParseUtil.Value_HTML_OpenTag);
                    html.append(c.getConstant().trim());
                    html.append(ParseUtil.Value_HTML_CloseTag);
                }     
            }         
            html.append(ParseUtil.BrkltRight_HTML);
            if(!(i == (comparison.size()-1)))html.append(ParseUtil.Comma_HTML);          
        }
        html.append(ParseUtil.Conclusion_HTML_OpenTag);
        html.append(dependency.getConclusion().toString());
        html.append(ParseUtil.Conclusion_HTML_CloseTag);
        html.append("</p>");
        html.append("</html>");
        return html.toString();
    }

    /**
     * @return the variablesColorMap
     */
    public Map<String,String> getVariablesColorMap() {
        return variablesColorMap;
    }
}
