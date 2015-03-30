package bart.model.database.dbms;

import bart.model.algebra.operators.ITupleIterator;
import bart.model.database.Attribute;
import bart.model.database.ITable;
import bart.model.database.OidTupleComparator;
import bart.model.database.Tuple;
import bart.BartConstants;
import bart.exceptions.DBMSException;
import bart.persistence.relational.AccessConfiguration;
import bart.utility.DBMSUtility;
import bart.persistence.relational.QueryManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBMSTable implements ITable {

    private String tableName;
    private AccessConfiguration accessConfiguration;
    private List<Attribute> attributes;
    private Long size;

    public DBMSTable(String name, AccessConfiguration accessConfiguration) {
        this.tableName = name;
        this.accessConfiguration = accessConfiguration;
    }

    public String getName() {
        return this.tableName;
    }

    public List<Attribute> getAttributes() {
        if (attributes == null) {
            initConnection();
        }
        return attributes;
    }
    
    public Attribute getAttribute(String name){
        for (Attribute attribute : getAttributes()) {
            if(attribute.getName().equals(name)){
                return attribute;
            }
        }
        throw new IllegalArgumentException("Table " + tableName + " doesn't contain attribute " + name);
    }

    public ITupleIterator getTupleIterator(int offset, int limit) {
        String query = getPaginationQuery(offset, limit);
        ResultSet resultSet = QueryManager.executeQuery(query, accessConfiguration);
        return new DBMSTupleIterator(resultSet, tableName);
    }

    public String getPaginationQuery(int offset, int limit) {
        return DBMSUtility.createTablePaginationQuery(tableName, accessConfiguration, offset, limit);
    }

    public ITupleIterator getTupleIterator() {
        ResultSet resultSet = DBMSUtility.getTableResultSet(tableName, accessConfiguration);
        return new DBMSTupleIterator(resultSet, tableName);
    }

    public long getSize() {
        if (size == null) {
            String query = "SELECT count(*) as count FROM " +  DBMSUtility.getSchema(accessConfiguration) + tableName;
            ResultSet resultSet = null;
            try {
                resultSet = QueryManager.executeQuery(query, accessConfiguration);
                resultSet.next();
                size = resultSet.getLong("count");
            } catch (SQLException ex) {
                throw new DBMSException("Unable to execute query " + query + " on database \n" + accessConfiguration + "\n" + ex);
            } finally {
                QueryManager.closeResultSet(resultSet);
            }
        }
        return size;
    }

    public AccessConfiguration getAccessConfiguration() {
        return accessConfiguration;
    }

    public void setAccessConfiguration(AccessConfiguration accessConfiguration) {
        this.accessConfiguration = accessConfiguration;
    }

    public String printSchema(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("Table: ").append(getName()).append("{\n");
        for (Attribute attribute : getAttributes()) {
            result.append(indent).append(BartConstants.INDENT);
            result.append(attribute.getName()).append(" ");
            result.append(attribute.getType()).append("\n");
        }
        result.append(indent).append("}\n");
        return result.toString();
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toShortString() {
        return DBMSUtility.getSchema(accessConfiguration) + this.tableName;
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("Table: ").append(getName()).append("{\n");
        ITupleIterator iterator = getTupleIterator();
        while (iterator.hasNext()) {
            result.append(indent).append(BartConstants.INDENT).append(iterator.next()).append("\n");
        }
        iterator.close();
        result.append(indent).append("}\n");
        return result.toString();
    }

    public String toStringWithSort(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("Table: ").append(getName()).append(" {\n");
        ITupleIterator iterator = getTupleIterator();
        List<Tuple> tuples = new ArrayList<Tuple>();
        while (iterator.hasNext()) {
            tuples.add(iterator.next());
        }
        Collections.sort(tuples, new OidTupleComparator());
        for (Tuple tuple : tuples) {
            result.append(indent).append(BartConstants.INDENT).append(tuple.toString()).append("\n");
        }
        iterator.close();
        result.append(indent).append("}\n");
        return result.toString();
    }

    private void initConnection() {
        ResultSet resultSet = null;
        try {
            resultSet = DBMSUtility.getTableResultSetForSchema(tableName, accessConfiguration);
            this.attributes = DBMSUtility.getTableAttributes(resultSet, tableName);
        } catch (SQLException ex) {
            throw new DBMSException("Unable to load table " + tableName + ".\n" + ex);
        } finally {
            QueryManager.closeResultSet(resultSet);
        }
    }
}
