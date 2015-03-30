package bart.model.algebra.operators;

import bart.model.database.AttributeRef;
import bart.model.database.Cell;
import bart.model.database.TableAlias;
import bart.model.database.Tuple;
import bart.model.database.TupleOID;
import bart.model.database.mainmemory.datasource.IntegerOIDGenerator;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateTupleFromTuplePair {
    
    private static final Logger logger = LoggerFactory.getLogger(GenerateTupleFromTuplePair.class.getName());
    
    public Tuple generateTuple(Tuple firstTuple, Tuple secondTuple) {
        if (logger.isDebugEnabled()) logger.debug("Merging tuple\n\t" + firstTuple.toStringWithOIDAndAlias() + "\n\t" + secondTuple.toStringWithOIDAndAlias());
        TupleOID tupleOID = new TupleOID(IntegerOIDGenerator.getNextOID());
        Tuple result = new Tuple(tupleOID);
        Set<TableAlias> firstTupleAliases = new HashSet<TableAlias>();
        for (Cell cell : firstTuple.getCells()) {
            firstTupleAliases.add(cell.getAttributeRef().getTableAlias());
            result.addCell(new Cell(tupleOID, cell.getAttributeRef(), cell.getValue()));
        }
        if (logger.isInfoEnabled()) logger.info("FirstTuple Aliases: " + firstTupleAliases);
        for (Cell cell : secondTuple.getCells()) {
            TableAlias cellTableAlias = cell.getAttributeRef().getTableAlias();
            TableAlias newCellAlias = cellTableAlias;
            if (firstTupleAliases.contains(cellTableAlias) && cellTableAlias.getAlias().equals("1")) {
                newCellAlias = new TableAlias(cellTableAlias.getTableName(), "2");
            } else if (firstTupleAliases.contains(cellTableAlias) && cellTableAlias.getAlias().equals("2")) {
                newCellAlias = new TableAlias(cellTableAlias.getTableName(), "1");
            }
            result.addCell(new Cell(tupleOID, new AttributeRef(cell.getAttributeRef(), newCellAlias), cell.getValue()));
        }
        if (logger.isDebugEnabled()) logger.debug("Result tuple: \n" + result.toStringWithOIDAndAlias());
        return result;
    }
    
}
