package it.unibas.bartgui.view.panel.editor.Dependency.parseHtml;

import bart.model.EGTask;
import bart.model.dependency.ComparisonAtom;
import bart.model.dependency.Dependency;
import bart.model.dependency.FormulaVariable;
import bart.model.errorgenerator.VioGenQuery;
import it.unibas.bartgui.view.panel.editor.Dependency.tableModel.VioGenQueriesData;
import it.unibas.bartgui.view.panel.editor.Dependency.tableModel.VioGenQueryData;
import java.text.NumberFormat;
import java.util.Map;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class ParseVioGenQueries {

    private Dependency dependency;
    private EGTask egt;
    private VioGenQueriesData vioGenQueriesData;
    private Map<String,String> variablesColorMap;

    public ParseVioGenQueries(Map<String,String> variablesColorMap,Dependency dependency, EGTask egt) {
        this.dependency = dependency;
        this.egt = egt;
        this.variablesColorMap = variablesColorMap;
        vioGenQueriesData = new VioGenQueriesData();
        parse();       
    }   
    
    private void parse()   {     
        for(VioGenQuery v : this.dependency.getVioGenQueries())   {  
            StringBuilder html = new StringBuilder("<html>");
            html.append(ParseUtil.BrkltLeft_HTML);
            ComparisonAtom comp = v.getVioGenComparison();
            if(comp.isVariableComparison())   {
                html.append(ParseUtil.getVariableHTML(variablesColorMap,comp.getLeftArgument().trim()));
                html.append(ParseUtil.getOperatorHTML(comp.getOperator().trim()));
                html.append(ParseUtil.getVariableHTML(variablesColorMap,comp.getRightArgument().trim()));
            }else{
                FormulaVariable tmp = null;
                if(comp.getRightVariable() != null)   {
                    tmp = comp.getRightVariable();
                }else{ 
                    tmp = comp.getLeftVariable();
                }
                if(tmp == null)html.append("error in comparison");
                try{
                    html.append(ParseUtil.getVariableHTML(variablesColorMap,tmp.getId().trim()));
                    html.append(ParseUtil.getOperatorHTML(comp.getOperator().trim()));
                    Number num = NumberFormat.getInstance().parse(comp.getConstant().trim());
                    html.append(ParseUtil.Number_HTML_OpenTag);
                    html.append(num);
                    html.append(ParseUtil.Number_HTML_CloseTag);
                }catch(Exception ex)  {
                    html.append(ParseUtil.Value_HTML_OpenTag);
                    html.append(comp.getConstant().trim());
                    html.append(ParseUtil.Value_HTML_CloseTag);
                }     
            }         
            html.append(ParseUtil.BrkltRight_HTML);
            html.append("</html>");
            
            VioGenQueryData data = new VioGenQueryData();
            data.setId(dependency.getId().trim());
            data.setPercentage(ParseUtil.getPercentage(v, egt));
            data.setQueryExecutor(ParseUtil.getStrategy(v, egt));
            data.setVioGenQuery(v);
            data.setComparison(html.toString());
            vioGenQueriesData.addVio(data);
        }   
    }   

    public VioGenQueriesData getVioGenQueriesData()   {
        return vioGenQueriesData;
    }
}
