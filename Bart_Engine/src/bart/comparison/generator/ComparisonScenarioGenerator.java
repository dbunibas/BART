package bart.comparison.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.OperatorFactory;
import bart.comparison.ComparisonConfiguration;
import speedy.SpeedyConstants;
import speedy.model.database.TupleWithTable;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.algebra.operators.IUpdateCell;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.CellRef;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleOID;
import speedy.model.database.operators.IDatabaseManager;
import speedy.persistence.Types;
import static speedy.persistence.Types.REAL;
import speedy.utility.SpeedyUtility;
import speedy.model.database.operators.IOIDGenerator;

public class ComparisonScenarioGenerator {
    
    private final static Logger logger = LoggerFactory.getLogger(ComparisonScenarioGenerator.class);
    private final Random random = new Random();
    private IDatabaseManager dbManager;
    private IInsertTuple insertTupleOperator;
    private IUpdateCell updateCellOperator;
    private IOIDGenerator oidGenerator;
    private long lastPlaceholderId = 0;
    //
    private int newRedundantTuplesPerc = 10;
    private int newRandomTuplesPerc = 5;
    private int cellsToChangePerc = 20;
    
    public InstancePair generate(IDatabase originalDB) {
        initOperators(originalDB);
        oidGenerator.initializeOIDs(originalDB);
        IDatabase leftDB = dbManager.cloneTarget(originalDB, "_left");
        List<TupleWithTable> leftTuples = SpeedyUtility.extractAllTuplesFromDatabase(leftDB);
        IDatabase rightDB = dbManager.cloneTarget(originalDB, "_right");
        List<TupleWithTable> rightTuples = SpeedyUtility.extractAllTuplesFromDatabase(rightDB);
        if (!ComparisonConfiguration.isInjective()) {
            insertRedundantTuples(rightDB, rightTuples);
            insertRandomTuples(rightDB);
        }
        if (!ComparisonConfiguration.isFunctional()) {
            insertRedundantTuples(leftDB, leftTuples);
            insertRandomTuples(leftDB);
        }
        if (ComparisonConfiguration.isTwoWayValueMapping()) {
            renameAndUpdateValuesInOriginalTuples(rightTuples, rightDB);
        }
        renameAndUpdateValuesInOriginalTuples(leftTuples, leftDB);
        return new InstancePair(leftDB, rightDB);
    }
    
    private void insertRedundantTuples(IDatabase db, List<TupleWithTable> tuples) {
        if (newRedundantTuplesPerc < 0 || newRedundantTuplesPerc > 100) {
            throw new IllegalArgumentException("Redundancy percentage must be >= 0 and <= 100");
        }
        if (tuples.isEmpty()) {
            throw new IllegalArgumentException("Unable to add redundancy in an empty database...");
        }
        Collections.shuffle(tuples);
        double tuplesToAddD = tuples.size() * (newRedundantTuplesPerc / 100.0);
        int tuplesToAdd = (int) Math.ceil(tuplesToAddD);
        if (logger.isDebugEnabled()) logger.debug("Adding " + tuplesToAdd + " redundant tuples");
        List<TupleWithTable> tuplesToDuplicate = tuples.subList(0, tuplesToAdd);
        List<TupleWithTable> redundantTuples = cloneTuples(tuplesToDuplicate);
        //Fix tuple oids
        if (logger.isDebugEnabled()) logger.debug("Redundant tuples: " + redundantTuples);
        for (TupleWithTable redundantTuple : redundantTuples) {
            renameValuesInTuple(redundantTuple);
            if (logger.isDebugEnabled()) logger.debug("Adding tuple " + redundantTuple.getTuple());
            insertTupleOperator.execute(db.getTable(redundantTuple.getTable()), redundantTuple.getTuple(), null, db);
        }
    }
    
    private void renameValuesInTuple(TupleWithTable tupleWithTable) {
        Map<IValue, NullValue> renamedValues = new HashMap<IValue, NullValue>();
        Set<AttributeRef> attributesToChange = chooseAttributesToChange(tupleWithTable);
        Tuple tuple = tupleWithTable.getTuple();
        for (AttributeRef attributeRef : attributesToChange) {
            Cell cell = tuple.getCell(attributeRef);
            IValue oldValue = cell.getValue();
            NullValue newValue;
            if (renamedValues.containsKey(oldValue)) {
                newValue = renamedValues.get(oldValue);
            } else {
                newValue = getNextPlaceholder();
                renamedValues.put(oldValue, newValue);
            }
            Cell newCell = new Cell(cell, newValue);
            tuple.getCells().set(tuple.getCells().indexOf(cell), newCell);
        }
    }
    
    private List<TupleWithTable> cloneTuples(List<TupleWithTable> tuplesToDuplicate) {
        List<TupleWithTable> result = new ArrayList<TupleWithTable>();
        for (TupleWithTable tupleWithTable : tuplesToDuplicate) {
            Tuple clonedTuple = tupleWithTable.getTuple().clone();
            TupleOID tupleOID = new TupleOID(oidGenerator.getNextOID(tupleWithTable.getTable()));
            clonedTuple.setOid(tupleOID);
            TupleWithTable cloneTupleWithTable = new TupleWithTable(tupleWithTable.getTable(), clonedTuple);
            result.add(cloneTupleWithTable);
        }
        return result;
    }
    
    private void renameAndUpdateValuesInOriginalTuples(List<TupleWithTable> tuples, IDatabase db) {
        Map<IValue, NullValue> renamedValues = new HashMap<IValue, NullValue>();
        for (TupleWithTable tupleWithTable : tuples) {
            Set<AttributeRef> attributesToChange = chooseAttributesToChange(tupleWithTable);
            Tuple tuple = tupleWithTable.getTuple();
            for (AttributeRef attributeRef : attributesToChange) {
                Cell cell = tuple.getCell(attributeRef);
                IValue oldValue = cell.getValue();
                NullValue newValue;
                if (renamedValues.containsKey(oldValue)) {
                    newValue = renamedValues.get(oldValue);
                } else {
                    newValue = getNextPlaceholder();
                    renamedValues.put(oldValue, newValue);
                }
                updateCellOperator.execute(new CellRef(cell), newValue, db);
            }
        }
    }
    
    private Set<AttributeRef> chooseAttributesToChange(TupleWithTable tuple) {
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        for (Cell cell : tuple.getTuple().getCells()) {
            if (cell.isOID()) {
                continue;
            }
            if (random.nextInt(100) <= cellsToChangePerc) {
                result.add(cell.getAttributeRef());
            }
        }
        return result;
    }
    
    private void insertRandomTuples(IDatabase db) {
        for (String tableName : db.getTableNames()) {
            ITable table = db.getTable(tableName);
            double tuplesToAddD = table.getSize() * (newRandomTuplesPerc / 100.0);
            int tuplesToAdd = (int) Math.ceil(tuplesToAddD);
            for (int i = 0; i < tuplesToAdd; i++) {
                Tuple newTuple = new Tuple(new TupleOID(oidGenerator.getNextOID(table.getName())));
                for (Attribute attribute : table.getAttributes()) {
                    AttributeRef attributeRef = new AttributeRef(tableName, attribute.getName());
                    CellRef cellRef = new CellRef(newTuple.getOid(), attributeRef);
                    IValue newValue = generateNewValue(attribute.getType());
                    Cell cell = new Cell(cellRef, newValue);
                    newTuple.addCell(cell);
                }
                insertTupleOperator.execute(table, newTuple, null, db);
            }
        }
    }
    
    private NullValue getNextPlaceholder() {
        lastPlaceholderId++;
        return new NullValue(SpeedyConstants.getStringSkolemPrefixes()[0] + lastPlaceholderId);
    }
    
    private IValue generateNewValue(String type) {
        if (random.nextBoolean()) { //New Constant
            if (type.equals(Types.INTEGER) || type.equals(REAL)) {
                return new ConstantValue(random.nextInt(99999));
            }
            return new ConstantValue(getRandomString());
        } else { //Skolem
            int randomPlaceholderId = random.nextInt((int) lastPlaceholderId * 2); //50% probability of picking an existing null
            return new NullValue(SpeedyConstants.getStringSkolemPrefixes()[0] + randomPlaceholderId);
        }
    }
    
    private String getRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (new Random().nextFloat() * (rightLimit - leftLimit));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
    
    private void initOperators(IDatabase originalDB) {
        this.dbManager = OperatorFactory.getInstance().getDatabaseManager(originalDB);
        this.insertTupleOperator = OperatorFactory.getInstance().getInsertOperator(originalDB);
        this.updateCellOperator = OperatorFactory.getInstance().getCellUpdater(originalDB);
        this.oidGenerator = OperatorFactory.getInstance().getOIDGenerator(originalDB);
    }
    
}
