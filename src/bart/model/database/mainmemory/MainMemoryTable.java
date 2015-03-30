package bart.model.database.mainmemory;

import bart.model.algebra.operators.ITupleIterator;
import bart.BartConstants;
import bart.utility.BartUtility;
import bart.model.database.Attribute;
import bart.model.database.AttributeRef;
import bart.model.database.ITable;
import bart.model.database.OidTupleComparator;
import bart.model.database.Tuple;
import bart.model.database.mainmemory.datasource.DataSource;
import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.operators.CalculateSize;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainMemoryTable implements ITable {

    private DataSource dataSource;
    private MainMemoryDB database;

    public MainMemoryTable(DataSource dataSource, MainMemoryDB database) {
        this.dataSource = dataSource;
        this.database = database;
    }

    public String getName() {
        INode schema = dataSource.getSchema();
        return schema.getLabel();
    }

    public List<Attribute> getAttributes() {
        List<Attribute> result = new ArrayList<Attribute>();
        INode tupleNode = dataSource.getSchema().getChild(0);
        for (INode attributeNode : tupleNode.getChildren()) {
            result.add(new Attribute(getName(), attributeNode.getLabel(), attributeNode.getChild(0).getLabel()));
        }
        return result;
    }
    
    public Attribute getAttribute(String name){
        for (Attribute attribute : getAttributes()) {
            if(attribute.getName().equals(name)){
                return attribute;
            }
        }
        throw new IllegalArgumentException("Table " + getName() + " doesn't contain attribute " + name);
    }

    public long getSize() {
        CalculateSize calculator = new CalculateSize();
        return calculator.getNumberOfTuples(this.dataSource.getInstances().get(0));
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public MainMemoryDB getDatabase() {
        return database;
    }

    public ITupleIterator getTupleIterator(int offset, int limit) {
        return getTupleIterator();
    }

    public String getPaginationQuery(int offset, int limit) {
        return "pagintion disabled";
    }

    public ITupleIterator getTupleIterator() {
        return new MainMemoryTupleIterator(this);
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

    public String toString() {
        return toString("");
    }

    public String toShortString() {
        return getName();
    }

    public String toString(String indent) {
        StringBuilder result = new StringBuilder();
        result.append(indent).append("Table: ").append(getName()).append(" {\n");
        ITupleIterator iterator = getTupleIterator();
        while (iterator.hasNext()) {
            result.append(indent).append(BartConstants.INDENT).append(iterator.next().toStringWithOID()).append("\n");
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
            result.append(indent).append(BartConstants.INDENT);
            for (Attribute attribute : getAttributes()) {
                result.append(tuple.getCell(new AttributeRef(attribute.getTableName(), attribute.getName()))).append(", ");
            }
            BartUtility.removeChars(", ".length(), result);
            result.append("\n");
        }
        iterator.close();
        result.append(indent).append("}\n");
        return result.toString();
    }

    public void closeConnection() {
    }

    class MainMemoryTupleIterator implements ITupleIterator {

        private MainMemoryTable table;
        private int pos = 0;
        private long size;

        public MainMemoryTupleIterator(MainMemoryTable table) {
            this.table = table;
            this.size = table.getSize();
        }

        public boolean hasNext() {
            return pos < size;
        }

        public Tuple next() {
            INode tupleNode = this.table.dataSource.getInstances().get(0).getChild(pos);
            Tuple tuple = BartUtility.createTuple(tupleNode, table.getName());
            pos++;
            return tuple;
        }

        public void reset() {
            this.pos = 0;
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void close() {
        }
    }
}
