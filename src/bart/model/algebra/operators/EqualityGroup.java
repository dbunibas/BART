package bart.model.algebra.operators;

import bart.BartConstants;
import speedy.model.expressions.Expression;
import java.util.ArrayList;
import java.util.List;
import speedy.model.database.AttributeRef;
import speedy.model.database.TableAlias;

class EqualityGroup {

    private TableAlias leftTable;
    private TableAlias rightTable;
    private List<Equality> equalities = new ArrayList<Equality>();

    EqualityGroup(Equality equality) {
        this.leftTable = equality.getLeftAttribute().getTableAlias();
        this.rightTable = equality.getRightAttribute().getTableAlias();
    }

    TableAlias getLeftTable() {
        return leftTable;
    }

    TableAlias getRightTable() {
        return rightTable;
    }

    List<Equality> getEqualities() {
        return equalities;
    }

    List<AttributeRef> getAttributeRefsForTableAlias(TableAlias tableAlias) {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (Equality equality : equalities) {
            if (equality.getLeftAttribute().getTableAlias().equals(tableAlias)) {
                result.add(equality.getLeftAttribute());
            } else if (equality.getRightAttribute().getTableAlias().equals(tableAlias)) {
                result.add(equality.getRightAttribute());
            } else {
                throw new IllegalArgumentException("Unable to find attribute ref for table " + tableAlias + " in equality " + equality);
            }
        }
        return result;
    }

    List<Expression> getEqualityExpressions() {
        List<Expression> result = new ArrayList<Expression>();
        for (Equality equality : equalities) {
            Expression equalityExpression = new Expression(equality.getLeftAttribute() + " == " + equality.getRightAttribute());
            equalityExpression.changeVariableDescription(equality.getLeftAttribute().toString(), equality.getLeftAttribute());
            equalityExpression.changeVariableDescription(equality.getRightAttribute().toString(), equality.getRightAttribute());
            result.add(equalityExpression);
        }
        if (leftTable.getTableName().equals(rightTable.getTableName())) {
            String inequalityOperator = "!=";
            Expression oidInequality = new Expression(leftTable.toString() + "." + BartConstants.OID + inequalityOperator + rightTable.toString() + "." + BartConstants.OID);
            oidInequality.changeVariableDescription(leftTable.toString() + "." + BartConstants.OID, new AttributeRef(leftTable, BartConstants.OID));
            oidInequality.changeVariableDescription(rightTable.toString() + "." + BartConstants.OID, new AttributeRef(rightTable, BartConstants.OID));
            result.add(oidInequality);
        }
        return result;
    }

    @Override
    public String toString() {
        return "EqualityGroup{" + "leftTable=" + leftTable + ", rightTable=" + rightTable + ", equalities=" + equalities + '}';
    }
}