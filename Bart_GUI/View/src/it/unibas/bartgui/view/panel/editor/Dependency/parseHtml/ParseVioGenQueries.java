package it.unibas.bartgui.view.panel.editor.Dependency.parseHtml;

import bart.model.EGTask;
import bart.model.dependency.Dependency;
import bart.model.errorgenerator.VioGenQuery;
import static it.unibas.bartgui.view.panel.editor.Dependency.parseHtml.ParseDependency.EQUAL;
import static it.unibas.bartgui.view.panel.editor.Dependency.parseHtml.ParseDependency.GREATER;
import static it.unibas.bartgui.view.panel.editor.Dependency.parseHtml.ParseDependency.GREATER_EQ;
import static it.unibas.bartgui.view.panel.editor.Dependency.parseHtml.ParseDependency.LOWER;
import static it.unibas.bartgui.view.panel.editor.Dependency.parseHtml.ParseDependency.LOWER_EQ;
import static it.unibas.bartgui.view.panel.editor.Dependency.parseHtml.ParseDependency.NOT_EQUAL;
import it.unibas.bartgui.view.panel.editor.Dependency.tableModel.VioGenQueriesData;
import it.unibas.bartgui.view.panel.editor.Dependency.tableModel.VioGenQueryData;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class ParseVioGenQueries {

    private Dependency dependency;
    private EGTask egt;
    private VioGenQueriesData vioGenQueriesData;
    private Map<Integer,Object> comparisonMap = new HashMap<Integer, Object>();
    private int cont = 0;

    public ParseVioGenQueries(Map<Integer,Object> mapHtml,Dependency dependency, EGTask egt) {
        this.dependency = dependency;
        this.egt = egt;
        vioGenQueriesData = new VioGenQueriesData();
        parse(mapHtml);       
    }   
    
    private void parse(Map<Integer,Object> mapHtml)   {
        for(VioGenQuery v : this.dependency.getVioGenQueries())   {            
            String comp = addSpace(v.getVioGenComparison().toString());
            StringTokenizer tok = new StringTokenizer(comp);
            while(tok.hasMoreTokens())   {
                String tmp = tok.nextToken();           
                if(tmp.contains("("))   {
                    BracketLeft bf = new BracketLeft();
                    bf.setPosition(cont++);
                    comparisonMap.put(bf.getPosition(), bf);
                    continue;
                }
                if(tmp.contains(")"))   {
                    BracketRight br = new BracketRight();
                    br.setPosition(cont++);
                    comparisonMap.put(br.getPosition(), br);
                    continue;
                }
                if(findOperator(tmp.trim(), cont++))   {
                    continue;
                }
                if(tmp.contains("\""))   {
                    Value val = new Value(tmp.trim());
                    val.setPosition(cont++);
                    comparisonMap.put(val.getPosition(), val);
                    continue;
                }
                
                Variable val = findVariable(tmp, mapHtml);
                
                if(val != null)   {
                    comparisonMap.put(val.getPosition(), val);
                    continue;
                }             
                
                try{
                    NumberFormat.getInstance().parse(tmp.trim());
                    MyNumber n = new MyNumber(tmp.trim());
                    n.setPosition(cont++);
                    comparisonMap.put(n.getPosition(), n);
                }catch(Exception ex)   {}
            }
            
            VioGenQueryData data = new VioGenQueryData();
            StringBuilder compHtml = new StringBuilder("<html>");
            data.setId(dependency.getId().trim());
            data.setPercentage(DependecyParseUtil.getPercentage(v, egt));
            data.setQueryExecutor(DependecyParseUtil.getStrategy(v, egt));
            data.setVioGenQuery(v);
            Object[] o = comparisonMap.keySet().toArray();
            Arrays.sort(o);
            for(Object obj : o)   {
                compHtml.append(comparisonMap.get((Integer)obj));    
            }
            compHtml.append("</html>");
            data.setComparison(compHtml.toString());
            vioGenQueriesData.addVio(data);
            comparisonMap.clear();
        }   
    }   
    
    private Variable findVariable(String tmp,Map<Integer,Object> mapHtml)   {
        for(Integer k : mapHtml.keySet())   {
            if(mapHtml.get(k) instanceof Variable)   {
                Variable var = (Variable)mapHtml.get(k);
                    if(tmp.trim().equals(var.getValue()))   {
                        Variable newVar = new Variable(var.getValue(), var.getColor());
                        newVar.setPosition(cont++);
                        return newVar;
                    }
                }
            }
        return null;
    }
    
    private String addSpace(String t)   {
        t = t.replace("(", " ( ");
        t = t.replace(")", " ) ");
        return t;
    }
    
    private boolean findOperator(String operator,int cont) {
        if (operator.contains(EQUAL)) {
            Operator op = new Operator(EQUAL);
            op.setPosition(cont);
            comparisonMap.put(op.getPosition(), op);
            return true;
        }
        if (operator.contains(NOT_EQUAL)) {
            Operator op = new Operator(NOT_EQUAL);
            op.setPosition(cont);
            comparisonMap.put(op.getPosition(), op);
            return true;
        }
        if (operator.contains(GREATER)) {
            Operator op = new Operator(GREATER);
            op.setPosition(cont);
            comparisonMap.put(op.getPosition(), op);
            return true;
        }
        if (operator.contains(LOWER)) {
            Operator op = new Operator(LOWER);
            op.setPosition(cont);
            comparisonMap.put(op.getPosition(), op);
            return true;
        }
        if (operator.contains(GREATER_EQ)) {
            Operator op = new Operator(GREATER_EQ);
            op.setPosition(cont);
            comparisonMap.put(op.getPosition(), op);
            return true;
        }
        if (operator.contains(LOWER_EQ)) {
            Operator op = new Operator(LOWER_EQ);
            op.setPosition(cont);
            comparisonMap.put(op.getPosition(), op);
            return true;
        }
        return false;
    }

    public VioGenQueriesData getVioGenQueriesData()   {
        return vioGenQueriesData;
    }
}
