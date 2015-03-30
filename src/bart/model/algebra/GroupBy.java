package bart.model.algebra;

import bart.utility.BartUtility;
import bart.model.algebra.operators.ListTupleIterator;
import bart.model.algebra.operators.IAlgebraTreeVisitor;
import bart.model.algebra.operators.ITupleIterator;
import bart.model.database.AttributeRef;
import bart.model.database.Cell;
import bart.model.database.IDatabase;
import bart.model.database.IValue;
import bart.model.database.Tuple;
import bart.model.database.TupleOID;
import bart.model.database.mainmemory.datasource.IntegerOIDGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupBy extends AbstractOperator {

    private static Logger logger = LoggerFactory.getLogger(GroupBy.class);

    private List<AttributeRef> groupingAttributes;
    private List<IAggregateFunction> aggregateFunctions;

    public GroupBy(List<AttributeRef> groupingAttributes, List<IAggregateFunction> aggregateFunctions) {
        this.groupingAttributes = groupingAttributes;
        this.aggregateFunctions = aggregateFunctions;
    }

    public String getName() {
        return "GROUP-BY-" + groupingAttributes + " - SELECT " + aggregateFunctions;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitGroupBy(this);
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        if (logger.isDebugEnabled()) logger.debug("Executing groupby: " + getName() + " on source\n" + (source == null ? "" : source.printInstances()) + "\nand target:\n" + target.printInstances());
        List<Tuple> result = new ArrayList<Tuple>();
        ITupleIterator originalTuples = children.get(0).execute(source, target);
        materializeResult(originalTuples, result);
        originalTuples.close();
        if (logger.isDebugEnabled()) logger.debug("Result:\n" + BartUtility.printCollection(result));
        return new ListTupleIterator(result);
    }

    private void materializeResult(ITupleIterator originalTuples, List<Tuple> result) {
        Map<String, List<Tuple>> groups = groupTuples(originalTuples);
        for (List<Tuple> group : groups.values()) {
            Tuple tuple = new Tuple(new TupleOID(IntegerOIDGenerator.getNextOID()));
            for (IAggregateFunction function : aggregateFunctions) {
                IValue aggregateValue = function.evaluate(group);
                Cell cell = new Cell(tuple.getOid(), function.getAttributeRef(), aggregateValue);
                tuple.addCell(cell);
            }
            result.add(tuple);
        }
    }

    public static List<Object> getTupleValues(Tuple tuple, List<AttributeRef> attributes) {
        List<Object> values = new ArrayList<Object>();
        for (AttributeRef attribute : attributes) {
            values.add(tuple.getCell(attribute).getValue());
        }
        return values;
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        return this.groupingAttributes;
    }

    private String generateKey(List<Object> tupleValues) {
        StringBuilder result = new StringBuilder("|");
        for (Object value : tupleValues) {
            result.append(value).append("|");
        }
        return result.toString();
    }

    private Map<String, List<Tuple>> groupTuples(ITupleIterator originalTuples) {
        Map<String, List<Tuple>> groups = new HashMap<String, List<Tuple>>();
        while (originalTuples.hasNext()) {
            Tuple originalTuple = originalTuples.next();
            List<Object> tupleValues = getTupleValues(originalTuple, groupingAttributes);
            String key = generateKey(tupleValues);
            List<Tuple> groupForKey = groups.get(key);
            if (groupForKey == null) {
                groupForKey = new ArrayList<Tuple>();
                groups.put(key, groupForKey);
            }
            groupForKey.add(originalTuple);
        }
        return groups;
    }

    public List<AttributeRef> getGroupingAttributes() {
        return groupingAttributes;
    }

    public List<IAggregateFunction> getAggregateFunctions() {
        return aggregateFunctions;
    }
}
