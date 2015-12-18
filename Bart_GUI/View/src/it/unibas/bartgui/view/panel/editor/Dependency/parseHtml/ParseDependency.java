package it.unibas.bartgui.view.panel.editor.Dependency.parseHtml;

import bart.model.dependency.Dependency;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.VariableEquivalenceClass;
import bart.model.errorgenerator.VioGenQuery;
import bart.model.errorgenerator.operator.ExecuteVioGenQueryUtility;
import bart.utility.DependencyUtility;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class ParseDependency {

    private static final Random rand = new Random(1234);
    
    public static String EQUAL = "==";
    public static String HTML_EQUAL = "&#61;&#61;";
    
    public static String NOT_EQUAL = "!=";
    public static String HTML_NOT_EQUAL = "&#33;&#61;";
    
    public static String GREATER = ">";
    public static String HTML_GREATER = "&#62;";
    
    public static String LOWER = "<";
    public static String HTML_LOWER = "&#60;";
    
    public static String GREATER_EQ = ">=";
    public static String HTML_GREATER_EQ = "&#62;&#61;";
    
    public static String LOWER_EQ = "<=";
    public static String HTML_LOWER_EQ = "&#60;&#61;";
    
    private  StringBuilder dependencyHtml = new StringBuilder();
    private Map<Integer,Object> mapHtml = new HashMap<>();
    private List<Variable> variables = new ArrayList<>();
    private Map<String,String> variablesColorMap = new HashMap<>();

    private Dependency dependency;
    private String formula;
    private int contPosition=1;
    private int contColor = 0;

    public  ParseDependency(Dependency dependency) {
        this.dependency = dependency;
        this.formula = dependency.getPremise().toString();
        initVariableColorMap();
        findVariables();        
        String tables = extractTable();
        String[] lineTables = split(tables);  
        analizeLinesTable(lineTables);
        String lineVariables = extractVariables(tables);
        analizeLineVariables(lineVariables);
        createHtmlDependency();      
    }
 
    private void initVariableColorMap()   {
        for(FormulaVariable v : this.dependency.getPremise().getLocalVariables())   {
            if(!variablesColorMap.containsKey(v.getId()))   {
                variablesColorMap.put(v.getId(), pickColor(contColor++));
            }
            try{
                VariableEquivalenceClass vec = ExecuteVioGenQueryUtility.findVariableEquivalenceClass(v, this.dependency.getPremise());           
                for(FormulaVariable fv : vec.getVariables())   {          
                    if(!variablesColorMap.containsKey(fv.getId()))   {                    
                        variablesColorMap.put(fv.getId(), variablesColorMap.get(v.getId()));
                    }                      
                }
            }catch(Exception ex)   {
                
            }
        }
    }
    
    private void findVariables()   {
        for(FormulaVariable v : this.dependency.getPremise().getLocalVariables())   {
            if(variablesColorMap.containsKey(v.getId()))   {
                variables.add(new Variable(v.getId(), variablesColorMap.get(v.getId())));
            }else{
                variables.add(new Variable(v.getId(), pickColor(contColor++)));
            }
        }
    }
     
    private String extractTable()   {
        String table = this.formula;
        for(VioGenQuery v : this.dependency.getVioGenQueries())   {
            String toreplace = invertComparison(v);
            table = table.replace(toreplace+",", "");
            table = table.replace(toreplace, "");
        }
        return table.trim();
    }
    
    private String[] split(String t)   {
        StringTokenizer tok = new StringTokenizer(t,")");
        List<String> tmp = new ArrayList<>();
        while(tok.hasMoreTokens())   {
            String s = tok.nextToken();
            if(s.charAt(0) == ',')   {
                s = s.replaceFirst(",", "");
            }
            if(s.trim().length() > 0)   {
                tmp.add(s+"),");
            }          
        }
        return tmp.toArray(new String[tmp.size()]);
    }
    
        private void analizeLinesTable(String[] line)   {
        for(String s : line)   {
            String toToken = addSpace(s);
            StringTokenizer t = new StringTokenizer(toToken);
            while(t.hasMoreTokens())   {
                String tmp = t.nextToken();
                
                if(tmp.contains(":"))   {
                    Attribute a = new Attribute(tmp.trim());
                    a.setPosition(contPosition++);
                    getMapHtml().put(a.getPosition(), a);
                    continue;
                }
                
                if(tmp.contains(","))   {
                    Comma c = new Comma();
                    c.setPosition(contPosition++);
                    getMapHtml().put(c.getPosition(), c);
                    continue;
                }
                if(tmp.contains("("))   {
                    BracketLeft bf = new BracketLeft();
                    bf.setPosition(contPosition++);
                    getMapHtml().put(bf.getPosition(), bf);
                    continue;
                }
                if(tmp.contains(")"))   {
                    BracketRight br = new BracketRight();
                    br.setPosition(contPosition++);
                    getMapHtml().put(br.getPosition(), br);
                    continue;
                }
                if(checkVariable(tmp.trim()))   {
                    Variable var = findVariable(tmp.trim());
                    Variable v = new Variable(var.getValue(), var.getColor());
                    v.setPosition(contPosition++);
                    getMapHtml().put(v.getPosition(), v);
                    continue;
                }
                
                TableName tn = new TableName(tmp.trim());
                tn.setPosition(contPosition++);
                getMapHtml().put(tn.getPosition(), tn);
                
            }
            getMapHtml().put(contPosition++, "<br></br>");
        }
    }
        
    private String extractVariables(String tables)   {
        return this.formula.replace(tables.trim(), "").trim();
    }
    
    private void analizeLineVariables(String line)   {
        String toToken = addSpace(line);
        StringTokenizer tok = new StringTokenizer(toToken);
        while(tok.hasMoreTokens())   {
            String tmp = tok.nextToken();
             if(tmp.contains(","))   {
                Comma c = new Comma();
                c.setPosition(contPosition++);
                getMapHtml().put(c.getPosition(), c);
                continue;
            }
            if(tmp.contains("("))   {
                BracketLeft bf = new BracketLeft();
                bf.setPosition(contPosition++);
                getMapHtml().put(bf.getPosition(), bf);
                continue;
            }
            if(tmp.contains(")"))   {
                BracketRight br = new BracketRight();
                br.setPosition(contPosition++);
                getMapHtml().put(br.getPosition(), br);
                continue;
            }
            if(findOperator(tmp.trim(), contPosition++))   {
                continue;
            }
            if(tmp.contains("\""))   {
                Value v = new Value(tmp.trim());
                v.setPosition(contPosition++);
                getMapHtml().put(v.getPosition(), v);
                continue;
            }
            
            Variable var = findVariable(tmp.trim());
            
            if(var != null)   {
                var.setPosition(contPosition++);
                getMapHtml().put(var.getPosition(), var);
                continue;
            }

            
            try{
                NumberFormat.getInstance().parse(tmp.trim());
                MyNumber n = new MyNumber(tmp.trim());
                n.setPosition(contPosition++);
                getMapHtml().put(n.getPosition(), n);
            }catch(Exception ex)   {}
        }
    }
    
    private String invertComparison(VioGenQuery vio)   {
        String invertedOperator = DependencyUtility.invertOperator(vio.getVioGenComparison().getOperator());
        StringBuilder stringExpression = new StringBuilder();
        stringExpression.append("(");
        stringExpression.append(vio.getVioGenComparison().getLeftArgument());
        stringExpression.append(" ");
        stringExpression.append(invertedOperator);
        stringExpression.append(" ");
        stringExpression.append(vio.getVioGenComparison().getRightArgument());
        stringExpression.append(")");
        return stringExpression.toString();
    }
                     
    private String addSpace(String t)   {
        t = t.replace("(", " ( ");
        t = t.replace(")", " ) ");
        t = t.replace(",", " , ");
        return t;
    }
           
    
    private Variable findVariable(String s)   {
        for(Variable v : variables)   {
            if(v.getValue().equals(s))   {
                return v;
            }
        }
        return null;
    }
    
    private boolean checkVariable(String s)   {
        for(Variable tmp : variables)   {
            if(s.equals(tmp.getValue()))   {
                return true;
            }
        }
        return false;
    }
    
    private String pickColor(int i)   {       
        String[] color = {
                "#009933",
                "#B800B8",
                "#663300",
                "#ff5050",
                "#999966",
                "#333300",
                "#ff99ff",
                "#a6a6a6",
                "#d1c814",
                "#000000",
        };
        if(i > color.length-1)   {
            String red = Integer.toHexString(rand.nextInt(256));
            String green = Integer.toHexString(rand.nextInt(256));
            String blue = Integer.toHexString(rand.nextInt(256));
            StringBuilder sb = new StringBuilder("#");
            sb.append((red.length() == 2) ? red : 0+red);
            sb.append((green.length() == 2) ? green : 0+green);
            sb.append((blue.length() == 2) ? blue : 0+blue);
            return sb.toString();
       }
        return color[i];
    }
    
    private boolean findOperator(String operator,int cont) {
        if (operator.contains(EQUAL)) {
            Operator op = new Operator(HTML_EQUAL);
            op.setPosition(cont);
            getMapHtml().put(op.getPosition(), op);
            return true;
        }
        if (operator.contains(NOT_EQUAL)) {
            Operator op = new Operator(HTML_NOT_EQUAL);
            op.setPosition(cont);
            getMapHtml().put(op.getPosition(), op);
            return true;
        }
        if (operator.contains(GREATER)) {
            Operator op = new Operator(HTML_GREATER);
            op.setPosition(cont);
            getMapHtml().put(op.getPosition(), op);
            return true;
        }
        if (operator.contains(LOWER)) {
            Operator op = new Operator(HTML_LOWER);
            op.setPosition(cont);
            getMapHtml().put(op.getPosition(), op);
            return true;
        }
        if (operator.contains(GREATER_EQ)) {
            Operator op = new Operator(HTML_GREATER_EQ);
            op.setPosition(cont);
            getMapHtml().put(op.getPosition(), op);
            return true;
        }
        if (operator.contains(LOWER_EQ)) {
            Operator op = new Operator(HTML_LOWER_EQ);
            op.setPosition(cont);
            getMapHtml().put(op.getPosition(), op);
            return true;
        }
        return false;
    }
    
    private void createHtmlDependency()   {
        ID idTable = new ID(this.dependency.getId());
        idTable.setPosition(0);
        Fail fail = new Fail(this.dependency.getConclusion().toString());
        fail.setPosition(getMapHtml().keySet().size()+1000);
        getMapHtml().put(idTable.getPosition(),idTable);
        getMapHtml().put(fail.getPosition(),fail);
        Object[] o = getMapHtml().keySet().toArray();
        Arrays.sort(o);
        for(Object obj : o)   {
            dependencyHtml.append(getMapHtml().get((Integer)obj));
        }
    }

    /**
     * @return the dependencyHtml
     */
    public  String getDependencyHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(dependencyHtml.toString());
        sb.append("</html>");
        return sb.toString();
    }

    /**
     * @return the mapHtml
     */
    public Map<Integer,Object> getMapHtml() {
        return mapHtml;
    }
}
