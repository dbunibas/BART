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

public class DBMSVirtualTable implements ITable {

    private final String tableName;
    private String suffix;
    private AccessConfiguration accessConfiguration;
    private List<Attribute> attributes;
//    private final ITable originalTable;

    public DBMSVirtualTable(ITable originalTable, AccessConfiguration accessConfiguration, String suffix) {
        this.tableName = originalTable.getName();
        this.accessConfiguration = accessConfiguration;
        this.suffix = suffix;
//        this.originalTable = originalTable;
//        initConnection();
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

    public ITupleIterator getTupleIterator() {
        ResultSet resultSet = DBMSUtility.getTableResultSet(tableName + suffix, accessConfiguration);
        return new DBMSTupleIterator(resultSet, tableName);
    }

    public ITupleIterator getTupleIterator(int offset, int limit) {
        String query = getPaginationQuery(offset, limit);
        ResultSet resultSet = QueryManager.executeQuery(query, accessConfiguration);
        return new DBMSTupleIterator(resultSet, tableName);
    }

    public String getPaginationQuery(int offset, int limit) {
        return DBMSUtility.createTablePaginationQuery(tableName + suffix, accessConfiguration, offset, limit);
    }

    public String printSchema(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("VirtualTable: ").append(toShortString()).append("{\n");
        for (Attribute attribute : getAttributes()) {
            result.append(indent).append(BartConstants.INDENT);
            result.append(attribute.getName()).append(" ");
            result.append(attribute.getType()).append("\n");
        }
        result.append(indent).append("}\n");
        return result.toString();
    }

    public String toString() {
        return toString("");
    }

    public String toShortString() {
        return this.accessConfiguration.getSchemaName() + "." + this.tableName + suffix;
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("VirtualTable: ").append(toShortString()).append("{\n");
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
            resultSet = DBMSUtility.getTableResultSetForSchema(tableName + suffix, accessConfiguration);
            this.attributes = DBMSUtility.getTableAttributes(resultSet, tableName);
        } catch (SQLException ex) {
            throw new DBMSException("Unable to load table " + tableName + ".\n" + ex);
        } finally {
            QueryManager.closeResultSet(resultSet);
        }
    }

    public long getSize() {
        String query = "SELECT count(*) as count FROM " + accessConfiguration.getSchemaName() + "." + tableName + suffix;
        ResultSet resultSet = null;
        try {
            resultSet = QueryManager.executeQuery(query, accessConfiguration);
            resultSet.next();
            return resultSet.getLong("count");
        } catch (SQLException ex) {
            throw new DBMSException("Unable to execute query " + query + " on database \n" + accessConfiguration + "\n" + ex);
        } finally {
            QueryManager.closeResultSet(resultSet);
        }
    }
}
