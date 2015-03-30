package bart.model.algebra.operators.mainmemory;

import bart.model.algebra.operators.IUpdateCell;
import bart.model.database.CellRef;
import bart.model.database.IDatabase;
import bart.model.database.IValue;
import bart.model.database.mainmemory.MainMemoryDB;
import bart.model.database.mainmemory.datasource.INode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateCell implements IUpdateCell {

    private static Logger logger = LoggerFactory.getLogger(UpdateCell.class);

    public void execute(CellRef cellRef, IValue value, IDatabase database) {
        if (logger.isDebugEnabled()) logger.debug("Changing cell " + cellRef + " with new value " + value);
        if (logger.isTraceEnabled()) logger.trace("In database " + database);
        INode instanceRoot = ((MainMemoryDB)database).getDataSource().getInstances().get(0);
        for (INode set : instanceRoot.getChildren()) {
            if (!set.getLabel().equals(cellRef.getAttributeRef().getTableName())) {
                continue;
            }
            for (INode tuple : set.getChildren()) {
                if (!tuple.getValue().toString().equals(cellRef.getTupleOID().getValue().toString())) {
                    continue;
                }
                for (INode attribute : tuple.getChildren()) {
                    if (!attribute.getLabel().equals(cellRef.getAttributeRef().getName())) {
                        continue;
                    }
                    attribute.getChild(0).setValue(value);
                }                
            }
        }

    }
}
