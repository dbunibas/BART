package bart.model.algebra.operators.mainmemory;

import bart.model.EGTask;
import bart.model.algebra.operators.IInsertTuple;
import bart.utility.BartUtility;
import bart.model.database.*;
import bart.model.database.mainmemory.MainMemoryTable;
import bart.model.database.mainmemory.datasource.DataSource;
import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.IntegerOIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertTuple implements IInsertTuple {

    private static Logger logger = LoggerFactory.getLogger(InsertTuple.class);

    @Override
    public void execute(ITable table, Tuple tuple, EGTask task) {
        if (logger.isDebugEnabled()) logger.debug("----Executing insert into table " + table.getName() + " tuple: " + tuple);
        DataSource dataSource = ((MainMemoryTable) table).getDataSource();
        String tupleLabel = dataSource.getSchema().getChild(0).getLabel();
        INode tupleNode = BartUtility.createNode("TupleNode", tupleLabel, tuple.getOid());
        dataSource.getInstances().get(0).addChild(tupleNode);
        for (Cell cell : tuple.getCells()) {
            INode attributeNode = BartUtility.createNode("AttributeNode", cell.getAttribute(), IntegerOIDGenerator.getNextOID());
            tupleNode.addChild(attributeNode);
            String leafLabel = dataSource.getSchema().getChild(0).getChild(cell.getAttribute()).getChild(0).getLabel();
            INode leafNode = BartUtility.createNode("LeafNode", leafLabel, cell.getValue());
            attributeNode.addChild(leafNode);
        }
    }
}
