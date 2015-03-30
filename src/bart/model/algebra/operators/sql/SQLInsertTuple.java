package bart.model.algebra.operators.sql;

import bart.model.EGTask;
import bart.utility.BartUtility;
import bart.model.algebra.operators.IInsertTuple;
import bart.model.database.*;
import bart.model.database.dbms.DBMSTable;
import bart.persistence.Types;
import bart.persistence.relational.AccessConfiguration;
import bart.persistence.relational.QueryManager;
import bart.utility.DBMSUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLInsertTuple implements IInsertTuple {

    private static Logger logger = LoggerFactory.getLogger(SQLInsertTuple.class);

    public void execute(ITable table, Tuple tuple, EGTask task) {
        DBMSTable dbmsTable = (DBMSTable) table;
        AccessConfiguration accessConfiguration = dbmsTable.getAccessConfiguration();
        StringBuilder insertQuery = new StringBuilder();
        insertQuery.append("INSERT INTO ");
        insertQuery.append(accessConfiguration.getSchemaName()).append(".").append(dbmsTable.getName());
        insertQuery.append(" (");
        for (Cell cell : tuple.getCells()) {
            insertQuery.append(cell.getAttribute()).append(", ");
        }
        BartUtility.removeChars(", ".length(), insertQuery);
        insertQuery.append(")");
        insertQuery.append(" VALUES (");
        for (Cell cell : tuple.getCells()) {
            String cellValue = cell.getValue().toString();
            cellValue = cellValue.replaceAll("'", "''");
            String attributeType = getAttributeType(cell.getAttributeRef(), task);
            if (attributeType.equals(Types.STRING)) {
                insertQuery.append("'");
            }
            insertQuery.append(cellValue);
            if (attributeType.equals(Types.STRING)) {
                insertQuery.append("'");
            }
            insertQuery.append(", ");
        }
        BartUtility.removeChars(", ".length(), insertQuery);
        insertQuery.append(");");
        if (logger.isDebugEnabled()) logger.debug("Insert query:\n" + insertQuery.toString());
        QueryManager.executeInsertOrDelete(insertQuery.toString(), ((DBMSTable) table).getAccessConfiguration());
    }

    private String getAttributeType(AttributeRef attributeRef, EGTask task) {
        ITable table = BartUtility.getTable(attributeRef, task);
        for (Attribute attribute : table.getAttributes()) {
            if (attribute.getName().equals(attributeRef.getName())) {
                return attribute.getType();
            }
        }
        //Original table doesn't contain the attribute (delta db attribute)
        return Types.STRING;
    }
}
