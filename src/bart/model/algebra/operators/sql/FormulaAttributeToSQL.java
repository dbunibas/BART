package bart.model.algebra.operators.sql;

import bart.model.dependency.*;
import bart.utility.DBMSUtility;
import bart.utility.DependencyUtility;

public class FormulaAttributeToSQL {

    public String generateSQL(FormulaAttribute attribute, Dependency dependency) {
        if (attribute.getValue() instanceof FormulaExpression) {
            throw new UnsupportedOperationException("Target expressions are not supported yet in SQL script");
        } else if (attribute.getValue() instanceof FormulaConstant) {
            FormulaConstant constant = (FormulaConstant) attribute.getValue();
            return "'" + constant.toString() + "'";
        } else if (attribute.getValue() instanceof FormulaVariableOccurrence) {
            return formulaVariableOccurrenceToSQL(attribute, dependency);
        }
        throw new IllegalArgumentException("Unknow type for attribute " + attribute);
    }

    private String formulaVariableOccurrenceToSQL(FormulaAttribute attribute, Dependency dependency) {
        FormulaVariableOccurrence occurrence = (FormulaVariableOccurrence) attribute.getValue();
        FormulaVariable existentialVariable = DependencyUtility.findVariableInList(occurrence, dependency.getConclusion().getLocalVariables());
        if (existentialVariable != null) {
            throw new IllegalArgumentException("Exisential variables are not supported");
        }
        FormulaVariable universalVariable = DependencyUtility.findVariableInList(occurrence, dependency.getPremise().getLocalVariables());
        FormulaVariableOccurrence sourceOccurrence = universalVariable.getRelationalOccurrences().get(0);
//        return dependency.getId() + "." + sourceOccurrence.getAttributeRef().toScriptString();
        return DBMSUtility.attributeRefToSQL(sourceOccurrence.getAttributeRef());
    }
}
