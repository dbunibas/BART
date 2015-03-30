package bart.model.database;

import bart.model.algebra.operators.ITupleIterator;
import java.util.List;

public interface ITable {

    public String getName();

    public List<Attribute> getAttributes();

    public ITupleIterator getTupleIterator();

    public String printSchema(String indent);

    public String toString(String indent);

    public String toStringWithSort(String indent);

    public String toShortString();

    public long getSize();

    public ITupleIterator getTupleIterator(int offset, int limit);

    public String getPaginationQuery(int offset, int limit);

    public Attribute getAttribute(String name);
}
