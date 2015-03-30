package bart.model.algebra.operators.sql;

import bart.BartConstants;
import bart.model.algebra.operators.IUpdateCell;
import bart.model.database.Attribute;
import bart.model.database.AttributeRef;
import bart.model.database.CellRef;
import bart.model.database.IDatabase;
import bart.model.database.IValue;
import bart.model.database.dbms.DBMSDB;
import bart.persistence.Types;
import bart.persistence.relational.QueryManager;
import bart.utility.BartUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLUpdateCell implements IUpdateCell {
    
    private static Logger logger = LoggerFactory.getLogger(SQLUpdateCell.class);
    
    @Override
    public void execute(CellRef cellRef, IValue value, IDatabase database) {
        if (logger.isDebugEnabled()) logger.debug("Changing cell " + cellRef + " with new value " + value + " in database " + database);
        StringBuilder query = new StringBuilder();
        query.append("UPDATE ");
        AttributeRef attributeRef = cellRef.getAttributeRef();
        query.append(((DBMSDB) database).getAccessConfiguration().getSchemaName()).append(".");
        query.append(cellRef.getAttributeRef().getTableName());
        query.append(" SET ").append(attributeRef.getName()).append("=");
        Attribute attribute = BartUtility.getAttribute(attributeRef, database);
        if(attribute.getType().equals(Types.STRING)){
            query.append("'");
        }
        query.append(cleanValue(value.toString()));
        if(attribute.getType().equals(Types.STRING)){
            query.append("'");
        }
        query.append(" WHERE ").append(BartConstants.OID).append("=");
        query.append(cellRef.getTupleOID());
        query.append(";");
        if (logger.isDebugEnabled()) logger.debug("Update script: \n" + query.toString());
        QueryManager.executeScript(query.toString(), ((DBMSDB) database).getAccessConfiguration(), true, true, false, false);
    }
    
    private String cleanValue(String string) {
        String sqlValue = string;
        sqlValue = sqlValue.replaceAll("'", "''");
        sqlValue = BartUtility.cleanConstantValue(sqlValue);
        return sqlValue;
    }
}
