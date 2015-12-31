package it.unibas.bartgui.view.panel.editor.Dependency.parseHtml;

import bart.BartConstants;
import bart.model.EGTask;
import bart.model.dependency.ComparisonAtom;
import bart.model.errorgenerator.VioGenQuery;
import bart.utility.DependencyUtility;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class ParseUtil {
    
    private static final Random rand = new Random(1234);  

    public  static final String HTML_EQUAL = "&#61;&#61;";
    public  static final String HTML_NOT_EQUAL = "&#33;&#61;";
    public  static final String HTML_GREATER = "&#62;";
    public  static final String HTML_LOWER = "&#60;";
    public  static final String HTML_GREATER_EQ = "&#62;&#61;";
    public  static final String HTML_LOWER_EQ = "&#60;&#61;";
    
    public  static final String ID_HTML_OpenTag = "<p><font size='5' color='#0077AA'><strong><em>";
    public  static final String ID_HTML_CloseTag = "</em></strong></font></p>";
    
    public  static final String TableName_HTML_OpenTag = "<font size='4' color='#000000'><strong> ";
    public  static final String TableName_HTML_CloseTag = " </strong></font>";
    
    public  static final String BrkltLeft_HTML = "<font size='4' color='#000000'> ( </font>";
    public  static final String BrkltRight_HTML = "<font size='4' color='#000000'> ) </font>";
    
    public  static final String Attribute_HTML_OpenTag = "<font size='4' color='#0000FF'> ";
    public  static final String Attribute_HTML_CloseTag = ": </font>";
    
    public  static final String Comma_HTML = "<font size='4' color='#000000'>, </font>";
    
    public  static final String Number_HTML_OpenTag = "<font size='4' color='#990000'> ";
    public  static final String Number_HTML_CloseTag = " </font>";
    
    public  static final String Value_HTML_OpenTag = "<font size='4' color='#CC9900'> ";
    public  static final String Value_HTML_CloseTag = "</font>";
    
    public  static final String Conclusion_HTML_OpenTag = "<font size='4' color='#000000'><strong>  &rarr;&#35;";
    public  static final String Conclusion_HTML_CloseTag = "</strong></font>";
    
    public static String getVariableHTML(Map<String,String>variablesColorMap, String id)   {
        StringBuilder sb = new StringBuilder("<font size='4' color='");
        sb.append(variablesColorMap.get(id));
        sb.append("'><em> ");
        sb.append(id);
        sb.append(" </em></font>");
        return sb.toString();
    }
    
    public static String getOperatorHTML(String operator)   {
        StringBuilder sb = new StringBuilder("<font size='4' color='#000000'> ");
        if(operator.equals(BartConstants.EQUAL))   {
            sb.append(ParseUtil.HTML_EQUAL);
            sb.append(" </font>");
            return sb.toString();
        }
        if(operator.equals(BartConstants.NOT_EQUAL))   {
            sb.append(ParseUtil.HTML_NOT_EQUAL);
            sb.append(" </font>");
            return sb.toString();
        }
        if(operator.equals(BartConstants.GREATER))   {
            sb.append(ParseUtil.HTML_GREATER);
            sb.append(" </font>");
            return sb.toString();
        }
        if(operator.equals(BartConstants.LOWER))   {
            sb.append(ParseUtil.HTML_LOWER);
            sb.append(" </font>");
            return sb.toString();
        }
        if(operator.equals(BartConstants.GREATER_EQ))   {
            sb.append(ParseUtil.HTML_GREATER_EQ);
            sb.append(" </font>");
            return sb.toString();
        }
        if(operator.equals(BartConstants.LOWER_EQ))   {
            sb.append(ParseUtil.HTML_LOWER_EQ);
            sb.append(" </font>");
            return sb.toString();
        }
        return "error";
    }
    
    public static boolean checkComparison(List<ComparisonAtom> comparison,ComparisonAtom comp)   {
        for(ComparisonAtom tmp : comparison)   {
            if((tmp.getLeftArgument().equals(comp.getLeftArgument()) 
                    && tmp.getRightArgument().equals(comp.getRightArgument()) 
                    && tmp.getOperator().equals(comp.getOperator()))
                    ||
               (tmp.getLeftArgument().equals(comp.getRightArgument())
                    && tmp.getRightArgument().equals(comp.getLeftArgument()) 
                    &&tmp.getOperator().equals(comp.getOperator())))   {
                return true;
            }
        }
        return false;
    }
    
    public static String pickColor(int i)   {       
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
     
    public static String invertComparison(VioGenQuery vio)   {
        String invertedOperator = DependencyUtility.invertOperator(vio.getVioGenComparison().getOperator());
        StringBuilder stringExpression = new StringBuilder();
        stringExpression.append(vio.getVioGenComparison().getLeftArgument());
        stringExpression.append(" ");
        stringExpression.append(invertedOperator);
        stringExpression.append(" ");
        stringExpression.append(vio.getVioGenComparison().getRightArgument());
        return stringExpression.toString();
    }
    
    
    public static Double getPercentage(VioGenQuery vio,EGTask task)   {
        String comp = invertComparison(vio);
        for(String k : task.getConfiguration().getVioGenQueryProbabilities().keySet())   {
            if(k.contains(comp) && k.contains(vio.getDependency().getId().trim()))   {
                return task.getConfiguration().getVioGenQueryProbabilities().get(k);
            }
        }
        return task.getConfiguration().getDefaultVioGenQueryConfiguration().getPercentage();
    }
    
    public static String getStrategy(VioGenQuery vio,EGTask task)   {
        String comp = invertComparison(vio);
        for(String k : task.getConfiguration().getVioGenQueryStrategy().keySet())   {
            if(k.contains(comp) && k.contains(vio.getDependency().getId().trim()))   {
                return task.getConfiguration().getVioGenQueryStrategy().get(k);
            }
        }
        return "";
    }
    
    
}
