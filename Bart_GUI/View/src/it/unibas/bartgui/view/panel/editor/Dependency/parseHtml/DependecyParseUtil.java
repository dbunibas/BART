package it.unibas.bartgui.view.panel.editor.Dependency.parseHtml;

import bart.model.EGTask;
import bart.model.errorgenerator.VioGenQuery;
import bart.utility.DependencyUtility;
import it.unibas.bartgui.resources.R;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DependecyParseUtil {

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
