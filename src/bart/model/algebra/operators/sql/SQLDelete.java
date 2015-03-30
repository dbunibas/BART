package bart.model.algebra.operators.sql;

import bart.utility.BartUtility;
import bart.model.algebra.IAlgebraOperator;
import bart.model.algebra.Scan;
import bart.model.algebra.Select;
import bart.model.algebra.operators.IDelete;
import bart.model.database.AttributeRef;
import bart.model.database.IDatabase;
import bart.model.database.TableAlias;
import bart.model.database.dbms.DBMSDB;
import bart.model.expressions.Expression;
import bart.utility.DBMSUtility;
import bart.persistence.relational.QueryManager;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLDelete implements IDelete {

    private static Logger logger = LoggerFactory.getLogger(SQLDelete.class);

    public boolean execute(String tableName, IAlgebraOperator operator, IDatabase source, IDatabase target) {
        StringBuilder deleteQuery = new StringBuilder();
        deleteQuery.append("DELETE FROM ");
        deleteQuery.append(getScanQuery(operator, source, target));
        deleteQuery.append(getSelectQuery(operator));
        deleteQuery.append(";");
        if (logger.isDebugEnabled()) logger.debug("Delete query:\n" + deleteQuery.toString());
        return QueryManager.executeInsertOrDelete(deleteQuery.toString(), ((DBMSDB) target).getAccessConfiguration());
    }

    private String getScanQuery(IAlgebraOperator operator, IDatabase source, IDatabase target) {
        if (operator instanceof Scan) {
            TableAlias tableAlias = ((Scan) operator).getTableAlias();
            return tableAliasToSQL(tableAlias, source, target);
        }
        for (IAlgebraOperator child : operator.getChildren()) {
            return getScanQuery(child, source, target);
        }
        throw new IllegalArgumentException("Unable to create delete query from " + operator);
    }

    private String getSelectQuery(IAlgebraOperator operator) {
        if (operator instanceof Select) {
            StringBuilder result = new StringBuilder();
            result.append(" WHERE ");
            for (Expression condition : ((Select) operator).getSelections()) {
                result.append(DBMSUtility.expressionToSQL(condition));
                result.append(" AND ");
            }
            BartUtility.removeChars(" AND ".length(), result);
            return result.toString();
        }
        for (IAlgebraOperator child : operator.getChildren()) {
            return getSelectQuery(child);
        }
        return "";
    }

    private String tableAliasToSQL(TableAlias tableAlias, IDatabase source, IDatabase target) {
        String sourceSchemaName = "source";
        if (source != null && source instanceof DBMSDB) {
            sourceSchemaName = ((DBMSDB) source).getAccessConfiguration().getSchemaName();
        }
        String targetSchemaName = "target";
        if (target != null && target instanceof DBMSDB) {
            targetSchemaName = ((DBMSDB) target).getAccessConfiguration().getSchemaName();
        }
        StringBuilder sb = new StringBuilder();
        if (tableAlias.isSource()) {
            sb.append(sourceSchemaName);
        } else {
            sb.append(targetSchemaName);
        }
        sb.append(".");
        sb.append(tableAlias.getTableName());
        if (tableAlias.isAliased()) {
            sb.append(" AS ").append(DBMSUtility.tableAliasToSQL(tableAlias));
        }
        return sb.toString();
    }
}
