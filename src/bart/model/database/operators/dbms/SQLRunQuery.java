package bart.model.database.operators.dbms;

import bart.exceptions.DBMSException;
import bart.model.database.operators.IRunQuery;
import bart.model.algebra.IAlgebraOperator;
import bart.model.algebra.operators.ITupleIterator;
import bart.model.algebra.operators.sql.AlgebraTreeToSQL;
import bart.model.database.AttributeRef;
import bart.model.database.IDatabase;
import bart.model.database.ResultInfo;
import bart.model.database.dbms.DBMSDB;
import bart.model.database.dbms.DBMSTupleIterator;
import bart.model.database.dbms.DBMSVirtualDB;
import bart.persistence.relational.AccessConfiguration;
import bart.persistence.relational.QueryManager;
import bart.utility.DBMSUtility;
import bart.utility.DependencyUtility;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLRunQuery implements IRunQuery {

    private static Logger logger = LoggerFactory.getLogger(SQLRunQuery.class);

    private AlgebraTreeToSQL translator = new AlgebraTreeToSQL();

    public ITupleIterator run(IAlgebraOperator operator, IDatabase source, IDatabase target) {
        AccessConfiguration accessConfiguration = getAccessConfiguration(target);
        if (logger.isDebugEnabled()) logger.debug("Executing query \n" + operator);
        String sqlCode = translator.treeToSQL(operator, source, target, "");
        if (logger.isDebugEnabled()) logger.debug("Executing sql \n" + sqlCode);
        ResultSet resultSet = QueryManager.executeQuery(sqlCode, accessConfiguration);
        return new DBMSTupleIterator(resultSet);
    }

    public ResultInfo getSize(IAlgebraOperator operator, IDatabase source, IDatabase target) {
        AccessConfiguration accessConfiguration = getAccessConfiguration(target);
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append("count(*) as count");
        AttributeRef oidAttribute = DependencyUtility.getFirstOIDAttribute(operator.getAttributes(source, target));
        if (oidAttribute != null) {
            String oidAttributeSQL = DBMSUtility.attributeRefToAliasSQL(oidAttribute);
            query.append(", min(").append(oidAttributeSQL).append(") as min_oid");
            query.append(", max(").append(oidAttributeSQL).append(") as max_oid");
        }
        query.append(" FROM (\n");
        query.append(translator.treeToSQL(operator, source, target, "\t"));
        query.append(") as v");
        if (logger.isDebugEnabled()) logger.debug("GetSize query\n\t" + query);
        ResultSet resultSet = null;
        try {
            resultSet = QueryManager.executeQuery(query.toString(), accessConfiguration);
            resultSet.next();
            long count = resultSet.getLong("count");
            ResultInfo result = new ResultInfo(count);
            if (oidAttribute != null) {
                result.setMinOid(resultSet.getLong("min_oid"));
                result.setMaxOid(resultSet.getLong("max_oid"));
            }
            return result;
        } catch (SQLException ex) {
            throw new DBMSException("Unable to execute query " + query + " on database \n" + accessConfiguration + "\n" + ex);
        } finally {
            QueryManager.closeResultSet(resultSet);
        }
    }

    private AccessConfiguration getAccessConfiguration(IDatabase target) throws IllegalArgumentException {
        AccessConfiguration accessConfiguration;
        if (target instanceof DBMSDB) {
            accessConfiguration = ((DBMSDB) target).getAccessConfiguration();
        } else if (target instanceof DBMSVirtualDB) {
            accessConfiguration = ((DBMSVirtualDB) target).getAccessConfiguration();
        } else {
            throw new IllegalArgumentException("Unable to execute SQL on main memory db. " + target);
        }
        return accessConfiguration;
    }
}
