package bart.comparison.generator;

import bart.comparison.ComparisonConfiguration;
import bart.comparison.TupleMapping;
import bart.comparison.TupleMatch;
import bart.comparison.ValueMapping;
import bart.comparison.operators.CheckTupleMatch;
import bart.utility.BartUtility;
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
import speedy.SpeedyConstants;
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
import speedy.model.database.TupleWithTable;
import speedy.model.database.operators.IDatabaseManager;
import speedy.model.database.operators.IOIDGenerator;
import speedy.persistence.Types;
import static speedy.persistence.Types.REAL;
import speedy.utility.SpeedyUtility;

public class ComparisonScenarioGeneratorWithMappings {

    private final static Logger logger = LoggerFactory.getLogger(ComparisonScenarioGeneratorWithMappings.class);
    private Random random;
    private IDatabaseManager dbManager;
    private IInsertTuple insertTupleOperator;
    private IUpdateCell updateCellOperator;
    private IOIDGenerator oidGenerator;
    private long lastPlaceholderId = 1;
    private long timeGeneration = 0;

    private int newRedundantTuplesPerc = 10;
    private int newRandomTuplesPerc = 5;
    private int cellsToChangePerc = 20;

    public ComparisonScenarioGeneratorWithMappings(int newRedundantTuplesPerc, int newRandomTuplesPerc, int cellsToChangePerc, long seed) {
        this.newRedundantTuplesPerc = newRedundantTuplesPerc;
        this.newRandomTuplesPerc = newRandomTuplesPerc;
        this.cellsToChangePerc = cellsToChangePerc;
        this.random = new Random(seed);
    }

    public InstancePair generateWithMappings(String originalDBPath, boolean changeSource, boolean changeTarget) {
        IDatabase originalDB = BartUtility.loadMainMemoryDatabase(originalDBPath);
        long start = System.currentTimeMillis();
        initOperators(originalDB);
        oidGenerator.initializeOIDs(originalDB);
        IDatabase leftDB = dbManager.cloneTarget(BartUtility.loadMainMemoryDatabase(originalDBPath), "_left");
        List<TupleWithTable> leftTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(leftDB);
        IDatabase rightDB = dbManager.cloneTarget(BartUtility.loadMainMemoryDatabase(originalDBPath), "_right");
        changeOid(rightDB); // TODO: fix not working
        List<TupleWithTable> rightTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(rightDB);
        TupleMapping tupleMapping = initMappings(leftTuples, rightTuples);
        if (changeSource) {
            logger.info("Change Source");
            modifyCells(leftTuples, rightTuples, tupleMapping, leftDB, true);
            if (!ComparisonConfiguration.isFunctional()) {
                addRandomTuples(leftDB, rightDB, tupleMapping, true);
                addRedundantTuples(leftDB, rightDB, tupleMapping, true);
            } else {
                // TODO: remove tuples
            }
        }
        leftTuples = SpeedyUtility.extractAllTuplesFromDatabase(leftDB);
        rightTuples = SpeedyUtility.extractAllTuplesFromDatabase(rightDB);
        if (changeTarget) {
            logger.info("Change Target");
            modifyCells(rightTuples, leftTuples, tupleMapping, rightDB, false);
            if (!ComparisonConfiguration.isInjective()) {
                addRandomTuples(rightDB, leftDB, tupleMapping, false);
                addRedundantTuples(rightDB, leftDB, tupleMapping, false);
            } else {
                //TODO: remove tuples
            }
        }
//        tupleMapping.updateValueMappings(); // clean and replace value mappings
        tupleMapping.updateValueMappings();
        // TODO: check non matching tuples but it should be not necessary
        InstancePair instancePair = new InstancePair(rightDB, leftDB);
        instancePair.setTupleMapping(tupleMapping);
        long end = System.currentTimeMillis();
        this.timeGeneration = (end - start);
        return instancePair;
    }

    public long getTimeGeneration() {
        return timeGeneration;
    }

    private void initOperators(IDatabase originalDB) {
        this.dbManager = OperatorFactory.getInstance().getDatabaseManager(originalDB);
        this.insertTupleOperator = OperatorFactory.getInstance().getInsertOperator(originalDB);
        this.updateCellOperator = OperatorFactory.getInstance().getCellUpdater(originalDB);
        this.oidGenerator = OperatorFactory.getInstance().getOIDGenerator(originalDB);
    }

    private TupleMapping initMappings(List<TupleWithTable> leftTuples, List<TupleWithTable> rightTuples) {
        TupleMapping tupleMapping = new TupleMapping();
        tupleMapping.setEnableReverse(true);
        for (int i = 0; i < leftTuples.size(); i++) {
            TupleWithTable leftTuple = leftTuples.get(i);
            TupleWithTable rightTuple = rightTuples.get(i);
            tupleMapping.putTupleMapping(leftTuple, rightTuple);
            tupleMapping.addValueMapping(leftTuple, leftTuple, true);
        }
        return tupleMapping;
    }

    private void modifyCells(List<TupleWithTable> sourceTuples, List<TupleWithTable> targetTuples, TupleMapping tupleMapping, IDatabase db, boolean isLeft) {
        if (this.cellsToChangePerc == 0) {
            return;
        }
        for (int pos = 0; pos < sourceTuples.size(); pos++) {
            Set<TupleWithTable> nonMatchingTuplesComparisons = tupleMapping.getRightNonMatchingTuples();
            Set<TupleWithTable> nonMatchingTuples = tupleMapping.getLeftNonMatchingTuples();
            if (!isLeft) {
                nonMatchingTuplesComparisons = tupleMapping.getLeftNonMatchingTuples();
                nonMatchingTuples = tupleMapping.getRightNonMatchingTuples();
            }
            TupleWithTable originalTuple = sourceTuples.get(pos);
            TupleWithTable targetTuple = targetTuples.get(pos);
            TupleWithTable modifiedTuple = modifyTuple(db, originalTuple);
            originalTuple.setIsForGeneration(true);
            targetTuple.setIsForGeneration(true);
            modifiedTuple.setIsForGeneration(true);
            if (modifiedTuple.equals(originalTuple)) {
                continue;
            }
            logger.debug("*** Modify cell");
            logger.debug("OriginalTuple tuple: {}", originalTuple);
            logger.debug("Modified tuple: {}", modifiedTuple);
            logger.debug("Target tuple: {}", targetTuple);
            logger.debug("Is left: {}", isLeft);
//            List<TupleMatch> matches = verifyMatches(modifiedTuple, targetTuples);
            List<TupleMatch> matches = verifyMatchesWithTimer(modifiedTuple, targetTuples, 2);
            if (matches.isEmpty()) {
                logger.debug("Is a non-match");
                // check if there were previous matches
//                List<TupleMatch> previousMatches = verifyMatches(originalTuple, targetTuples);
                List<TupleMatch> previousMatches = verifyMatchesParallel(originalTuple, targetTuples);
//                List<TupleMatch> previousMatches = getPreviousMatches(originalTuple, tupleMapping);
                logger.debug("Previous Matches: {}", previousMatches.size());
                if (!previousMatches.isEmpty()) {
                    for (TupleMatch match : previousMatches) {
                        TupleWithTable leftTuple = match.getLeftTuple();
                        TupleWithTable rightTuple = match.getRightTuple();
                        if (isLeft) {
                            tupleMapping.removeKeyTupleMapping(leftTuple);
                        } else {
                            leftTuple = match.getRightTuple();
                            rightTuple = match.getLeftTuple();
                            tupleMapping.removeFromTupleSetTupleMapping(leftTuple, rightTuple);
                        }
                        tupleMapping.removeValueMapping(leftTuple, rightTuple, isLeft);
                        nonMatchingTuples.remove(leftTuple);
                        if (!nonMatchingTuplesComparisons.contains(rightTuple)) {
                            nonMatchingTuplesComparisons.add(rightTuple);
                        }
                    }
                    nonMatchingTuples.add(modifiedTuple);
                } else {
                    nonMatchingTuples.remove(originalTuple);
                    nonMatchingTuples.add(modifiedTuple);
                }
                logger.debug("Update orig: {} with modif: {}", originalTuple, modifiedTuple);
                updateTuple(originalTuple, modifiedTuple, db);
            } else {
                if (matches.size() == 1) {
                    logger.debug("Is a match");
                    boolean canUpdate = false;
                    for (TupleMatch match : matches) {
                        TupleWithTable leftTuple = match.getLeftTuple(); //modified
                        TupleWithTable rightTuple = match.getRightTuple(); // other
                        if (isLeft) {
                            canUpdate = checkValueMapping(leftTuple, rightTuple, tupleMapping);
                            if (!canUpdate) {
                                continue;
                            }
                            logger.debug("Update mapping: {} to {}", leftTuple, rightTuple);
                            tupleMapping.updateKeyTupleMapping(originalTuple, leftTuple);
                            tupleMapping.putTupleMapping(leftTuple, rightTuple);
                        } else {
                            leftTuple = match.getRightTuple(); // modified
                            rightTuple = match.getLeftTuple(); // other
                            canUpdate = checkValueMapping(leftTuple, rightTuple, tupleMapping);
                            if (!canUpdate) {
                                continue;
                            }
                            logger.debug("Update TupleSet with key {}. Change {} to {}", leftTuple, originalTuple, rightTuple);
                            tupleMapping.updateTupleSetTupleMapping(leftTuple, originalTuple, rightTuple);
                        }
                        tupleMapping.removeMappingForKey(originalTuple, isLeft);
                        tupleMapping.removeValueMapping(originalTuple, targetTuple, isLeft);
                        tupleMapping.addValueMapping(leftTuple, rightTuple, isLeft);
                    }
                    if (canUpdate) {
                        updateTuple(originalTuple, modifiedTuple, db);
                        if (nonMatchingTuples.contains(originalTuple)) {
                            nonMatchingTuples.remove(originalTuple);
                        }
                        logger.debug("Remove old refence for {} in left {}", originalTuple, isLeft);
                        tupleMapping.removeMappingForKey(originalTuple, isLeft);
                    }
                } else {
                    logger.debug("Too many matches, ignore update");
                    // ignore update, non functional and non injective are managed with random and redundant tuples
                }
            }
            logger.debug("TupleMapping: \n{}", tupleMapping);
            logger.debug("Modified db: {}", db);
        }
    }

    private void updateValueMappings(TupleWithTable modifiedTuple, TupleWithTable targetTuple, TupleMapping tupleMapping) {
        Map<IValue, IValue> valueMappingLR = new HashMap<>();
        Map<IValue, IValue> valueMappingRL = new HashMap<>();
        for (int i = 0; i < modifiedTuple.getTuple().getCells().size(); i++) {
            IValue valueInCloned = modifiedTuple.getTuple().getCells().get(i).getValue();
            IValue valueInTarget = targetTuple.getTuple().getCells().get(i).getValue();
            if (valueInCloned instanceof NullValue) {
                valueMappingLR.put(valueInCloned, valueInTarget);
            }
            if (valueInTarget instanceof NullValue) {
                valueMappingRL.put(valueInTarget, valueInCloned);
            }
        }
        updateValueMappings(tupleMapping.getValueMappings().getLeftToRightValueMapping(), valueMappingLR);
        updateValueMappings(tupleMapping.getValueMappings().getRightToLeftValueMapping(), valueMappingRL);
    }

    private TupleWithTable modifyTuple(IDatabase db, TupleWithTable originalTuple) {
        List<Cell> cellsToChangeInSource = new ArrayList<>();
        ITable table = db.getTable(originalTuple.getTable());
        for (Cell cell : originalTuple.getTuple().getCells()) {
            if (cell.isOID()) {
                continue;
            }
            if (random.nextInt(100) <= cellsToChangePerc) {
                cellsToChangeInSource.add(cell);
            }
        }
        Map<AttributeRef, IValue> newCellValues = new HashMap<>();
        for (Cell cell : cellsToChangeInSource) {
            Attribute attribute = table.getAttribute(cell.getAttribute());
            AttributeRef attributeRef = new AttributeRef(table.getName(), attribute.getName());
            IValue newValue = generateNewValueForModify(attribute.getType());
            newCellValues.put(attributeRef, newValue);
        }
        TupleWithTable clonedSource = originalTuple.clone();
        Tuple tuple = clonedSource.getTuple();
        for (AttributeRef attribute : newCellValues.keySet()) {
            Cell cell = tuple.getCell(attribute);
            cell.setValue(newCellValues.get(attribute));
        }
        clonedSource.setIsForGeneration(true);
        return clonedSource;
    }

    private void addRedundantTuples(IDatabase thisDB, IDatabase otherDB, TupleMapping tupleMapping, boolean isLeft) {
        if (newRedundantTuplesPerc < 0 || newRedundantTuplesPerc > 100) {
            throw new IllegalArgumentException("Redundancy percentage must be >= 0 and <= 100");
        }
        List<TupleWithTable> tuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(thisDB);
        if (tuples.isEmpty()) {
            throw new IllegalArgumentException("Unable to add redundancy in an empty database...");
        }
        if (newRedundantTuplesPerc == 0) {
            return;
        }
        Collections.shuffle(tuples, this.random);
        double tuplesToAddD = tuples.size() * (newRedundantTuplesPerc / 100.0);
        int tuplesToAdd = (int) Math.ceil(tuplesToAddD);
        List<TupleWithTable> tuplesToDuplicate = tuples.subList(0, tuplesToAdd);
        List<TupleWithTable> redundantTuples = cloneTuples(tuplesToDuplicate);
        List<TupleWithTable> otherTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(otherDB);
        for (int i = 0; i < tuplesToDuplicate.size(); i++) {
            Set<TupleWithTable> nonMatchingTuples = tupleMapping.getLeftNonMatchingTuples();
            if (!isLeft) {
                nonMatchingTuples = tupleMapping.getRightNonMatchingTuples();
            }
            logger.debug("*** Add Redundant Tuple. Is Left: {}", isLeft);
            TupleWithTable originalTuple = tuplesToDuplicate.get(i);
            TupleWithTable redundantTuple = redundantTuples.get(i);
            logger.debug("Original Tuple: {}", originalTuple);
            logger.debug("Reduntant Tuple: {}", redundantTuple);
//            List<TupleMatch> verifyMatches = verifyMatches(redundantTuple, otherTuples);
            List<TupleMatch> verifyMatches = verifyMatchesRedundant(redundantTuple, originalTuple, tupleMapping, isLeft);
            if (!verifyMatches.isEmpty()) {
                logger.debug("There are matches");
                for (TupleMatch match : verifyMatches) {
                    if (isLeft) {
                        TupleWithTable leftTuple = match.getLeftTuple();
                        TupleWithTable rightTuple = match.getRightTuple();
                        logger.debug("Put mapping Left To Right: {} to {}", leftTuple, rightTuple);
                        tupleMapping.putTupleMapping(leftTuple, rightTuple);
                        tupleMapping.addValueMapping(leftTuple, rightTuple, isLeft);
                    } else {
                        TupleWithTable leftTuple = match.getLeftTuple();
                        TupleWithTable rightTuple = match.getRightTuple();
                        logger.debug("Put mapping Right to Left: {} to {}", rightTuple, leftTuple);
                        tupleMapping.putTupleMapping(rightTuple, leftTuple);
                        tupleMapping.addValueMapping(rightTuple, leftTuple, isLeft);
                    }
                }
            } else {
                logger.debug("There are no matches, add to non matching tuples");
                nonMatchingTuples.add(redundantTuple);
            }
            insertTupleOperator.execute(thisDB.getTable(redundantTuple.getTable()), redundantTuple.getTuple(), null, thisDB);
            logger.debug("Tuple Mappings: \n{}", tupleMapping);
            logger.debug("Database: {}", thisDB);
        }
    }

    private void addRandomTuples(IDatabase thisDB, IDatabase otherDB, TupleMapping tupleMapping, boolean isLeft) {
        if (newRandomTuplesPerc == 0) {
            return;
        }
        logger.debug("*** Add Random Tuple");
        for (String tableName : thisDB.getTableNames()) {
            ITable table = thisDB.getTable(tableName);
            double tuplesToAddD = table.getSize() * (newRandomTuplesPerc / 100.0);
            int tuplesToAdd = (int) Math.ceil(tuplesToAddD);
            for (int i = 0; i < tuplesToAdd; i++) {
                Set<TupleWithTable> nonMatchingTuples = tupleMapping.getRightNonMatchingTuples();
                if (isLeft) {
                    nonMatchingTuples = tupleMapping.getLeftNonMatchingTuples();
                }
                TupleOID newOID = new TupleOID(oidGenerator.getNextOID(table.getName()));
                Tuple newTuple = new Tuple(newOID);
                CellRef cellRefOID = new CellRef(newOID, new AttributeRef(tableName, SpeedyConstants.OID));
                IValue oidValue = new ConstantValue(newOID.getValue());
                Cell cellOid = new Cell(cellRefOID, oidValue);
                newTuple.addCell(cellOid);
                for (Attribute attribute : table.getAttributes()) {
                    AttributeRef attributeRef = new AttributeRef(tableName, attribute.getName());
                    CellRef cellRef = new CellRef(newTuple.getOid(), attributeRef);
                    IValue newValue = generateNewValue(attribute.getType());
                    Cell cell = new Cell(cellRef, newValue);
                    newTuple.addCell(cell);
                }
                newTuple.setOidNested(newOID);
                TupleWithTable newTupleWithTable = new TupleWithTable(tableName, newTuple);
                logger.debug("Candidate Generated Random Tuple: {}", newTupleWithTable);
                List<TupleWithTable> tuplesInOtherDB = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(otherDB);
                List<TupleWithTable> tuplesInDB = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(thisDB);
                if (verifyMatchesWithTimer(newTupleWithTable, tuplesInOtherDB, 1).isEmpty()
                        && verifyMatchesWithTimer(newTupleWithTable, tuplesInDB, 1).isEmpty()) {
                    logger.debug("Add Random Tuple to DB: {}", newTuple);
                    nonMatchingTuples.add(newTupleWithTable);
                    insertTupleOperator.execute(table, newTuple, null, thisDB);
                    logger.debug("Tuple Mappings: \n{}", tupleMapping);
                    logger.debug("Database: {}", thisDB);
                }
//                if (verifyMatches(newTupleWithTable, tuplesInOtherDB).isEmpty()) {
//                    if (verifyMatches(newTupleWithTable, tuplesInDB).isEmpty()) {
//                        logger.debug("Add Random Tuple to DB: {}", newTuple);
//                        nonMatchingTuples.add(newTupleWithTable);
//                        insertTupleOperator.execute(table, newTuple, null, thisDB);
//                        logger.debug("Tuple Mappings: \n{}", tupleMapping);
//                        logger.debug("Database: {}", thisDB);
//                    }
//                }
            }
        }
    }

    private IValue generateNewValueForModify(String type) {
        if (random.nextBoolean()) { //New Constant
            if (type.equals(Types.INTEGER) || type.equals(REAL)) {
                return new ConstantValue(random.nextInt(99999));
            }
            return new ConstantValue(getRandomString());
        } else { //Skolem
            return getNextPlaceholder();
        }
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
//            int randomLimitedInt = leftLimit + (int) (new Random().nextFloat() * (rightLimit - leftLimit));
            int randomLimitedInt = leftLimit + (int) (this.random.nextFloat() * (rightLimit - leftLimit));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    private NullValue getNextPlaceholder() {
        lastPlaceholderId++;
        return new NullValue(SpeedyConstants.getStringSkolemPrefixes()[0] + lastPlaceholderId);
    }

    private List<TupleMatch> verifyMatches(TupleWithTable tuple, List<TupleWithTable> otherTuples) {
        CheckTupleMatch tupleMatch = new CheckTupleMatch();
        List<TupleMatch> matches = new ArrayList<>();
        for (TupleWithTable nonMatchingTuple : otherTuples) {
            TupleMatch checkMatch = tupleMatch.checkMatch(tuple, nonMatchingTuple);
            if (checkMatch != null) {
                checkMatch.getLeftTuple().setIsForGeneration(true);
                checkMatch.getRightTuple().setIsForGeneration(true);
                matches.add(checkMatch);
            }
        }
        return matches;
    }

    private boolean verifyMatch(TupleWithTable source, TupleWithTable target) {
        CheckTupleMatch tupleMatch = new CheckTupleMatch();
        TupleMatch checkMatch = tupleMatch.checkMatch(source, target);
        return checkMatch != null;
    }

    private void updateTuple(TupleWithTable oldTuple, TupleWithTable newTuple, IDatabase database) {
        for (int i = 0; i < oldTuple.getTuple().getCells().size(); i++) {
            Cell oldCell = oldTuple.getTuple().getCells().get(i);
            Cell newCell = newTuple.getTuple().getCells().get(i);
            updateCellOperator.execute(new CellRef(oldCell), newCell.getValue(), database);
        }
    }

    private void updateValueMappings(ValueMapping valueMappings, Map<IValue, IValue> valueMapping) {
        for (IValue from : valueMapping.keySet()) {
            IValue to = valueMapping.get(from);
            valueMappings.putValueMapping(from, to);
        }
    }

    private List<TupleWithTable> cloneTuples(List<TupleWithTable> tuplesToDuplicate) {
        List<TupleWithTable> result = new ArrayList<TupleWithTable>();
        for (TupleWithTable tupleWithTable : tuplesToDuplicate) {
            Tuple clonedTuple = tupleWithTable.getTuple().clone();
            TupleOID tupleOID = new TupleOID(oidGenerator.getNextOID(tupleWithTable.getTable()));
            clonedTuple.setOidNested(tupleOID);
            TupleWithTable cloneTupleWithTable = new TupleWithTable(tupleWithTable.getTable(), clonedTuple);
            cloneTupleWithTable.setIsForGeneration(true);
            result.add(cloneTupleWithTable);
        }
        return result;
    }

    private void changeOid(IDatabase db) {
        List<TupleWithTable> tuples = SpeedyUtility.extractAllTuplesFromDatabase(db);
        for (TupleWithTable tuple : tuples) {
            TupleOID oldTupleOId = tuple.getTuple().getOid();
            TupleOID tupleOID = new TupleOID(oidGenerator.getNextOID(tuple.getTable()));
            tuple.getTuple().setOid(tupleOID);
            updateCellOperator.execute(new CellRef(oldTupleOId, new AttributeRef(tuple.getTable(), SpeedyConstants.OID)), new ConstantValue(tupleOID.getNumericalValue()), db);
        }
    }

    private boolean checkValueMapping(TupleWithTable leftTuple, TupleWithTable rightTuple, TupleMapping tupleMapping) {
        for (int i = 0; i < leftTuple.getTuple().getCells().size(); i++) {
            IValue leftValue = leftTuple.getTuple().getCells().get(i).getValue();
            IValue rightValue = leftTuple.getTuple().getCells().get(i).getValue();
            if (leftValue instanceof NullValue) {
                IValue allowedValue = tupleMapping.getValueMappings().getLeftToRightMappingForValue(leftValue);
                if (allowedValue != null && !allowedValue.equals(rightValue)) {
                    return false;
                }
            }
            if (rightValue instanceof NullValue) {
                IValue allowedValue = tupleMapping.getValueMappings().getRightToLeftMappingForValue(rightValue);
                if (allowedValue != null && !allowedValue.equals(leftValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<TupleMatch> getPreviousMatches(TupleWithTable originalTuple, TupleMapping tupleMapping) {
        long start = System.currentTimeMillis();
        List<TupleMatch> tupleMatches = new ArrayList<>();
        Set<TupleWithTable> tupleSet = tupleMapping.getTupleMapping().get(originalTuple);
        if (tupleSet != null && !tupleSet.isEmpty()) {
            for (TupleWithTable otherTuple : tupleSet) {
                TupleMatch match = new TupleMatch(originalTuple, otherTuple, null); // we don't need valueMapping
                tupleMatches.add(match);
            }
        }
        long end = System.currentTimeMillis();
        logger.debug("getPreviousMatches: {}", (end - start));
        return tupleMatches;
    }

    private List<TupleMatch> verifyMatchesParallel(TupleWithTable tuple, List<TupleWithTable> otherTuples) {
        List<TupleMatch> matches = Collections.synchronizedList(new ArrayList<TupleMatch>());
        long start = System.currentTimeMillis();
        CheckTupleMatch tupleMatch = new CheckTupleMatch();
        otherTuples.stream().parallel().forEach(otherTuple -> {
            TupleMatch checkMatch = tupleMatch.checkMatch(tuple, otherTuple);
            if (checkMatch != null) {
                checkMatch.getLeftTuple().setIsForGeneration(true);
                checkMatch.getRightTuple().setIsForGeneration(true);
                matches.add(checkMatch);
            }
        });
        long end = System.currentTimeMillis();
//        logger.info("Time Matches in parallel: {}", (end - start));
        return matches;
    }

    private List<TupleMatch> verifyMatchesWithTimer(TupleWithTable tuple, List<TupleWithTable> otherTuples, Integer limit) {
        long start = System.currentTimeMillis();
        long desiredEnd = start + 1000;
        CheckTupleMatch tupleMatch = new CheckTupleMatch();
        List<TupleMatch> matches = new ArrayList<>();
        for (TupleWithTable nonMatchingTuple : otherTuples) {
            if (System.currentTimeMillis() >= desiredEnd) {
                matches = null;
                break;
            }
            TupleMatch checkMatch = tupleMatch.checkMatch(tuple, nonMatchingTuple);
            if (checkMatch != null) {
                checkMatch.getLeftTuple().setIsForGeneration(true);
                checkMatch.getRightTuple().setIsForGeneration(true);
                matches.add(checkMatch);
                if (limit != null && matches.size() >= limit) {
                    long end = System.currentTimeMillis();
//                    logger.debug("Time verifyMatchesWithTimer (return): {}", (end - start));
                    return matches; // we don't need all the matches, but we need only to know if there are more than one
                }
            }
        }
        if (matches == null) {
            return verifyMatchesParallel(tuple, otherTuples);
        }
        long end = System.currentTimeMillis();
//        logger.debug("Time verifyMatchesWithTimert: {}", (end - start));
        return matches;
    }

    private List<TupleMatch> verifyMatchesRedundant(TupleWithTable redundantTuple, TupleWithTable originalTuple, TupleMapping tupleMapping, boolean isLeft) {
        long start = System.currentTimeMillis();
        List<TupleMatch> tupleMatches = new ArrayList<>();
        if (isLeft) {
            Set<TupleWithTable> tupleSet = tupleMapping.getTupleMapping().get(originalTuple);
            if (tupleSet != null && !tupleSet.isEmpty()) {
                for (TupleWithTable otherTuple : tupleSet) {
                    TupleMatch match = new TupleMatch(redundantTuple, otherTuple, null); // we don't need valueMapping
                    tupleMatches.add(match);
                }
            }
        } else {
            Set<TupleWithTable> leftTuplesInMatch = tupleMapping.getTupleMapping().keySet();
            Set<TupleWithTable> newKeys = new HashSet<>();
            for (TupleWithTable key : leftTuplesInMatch) {
                Set<TupleWithTable> tupleSet = tupleMapping.getTupleMapping().get(key);
                if (tupleSet.contains(originalTuple)) {
                    newKeys.add(key);
                }
            }
            for (TupleWithTable newKey : newKeys) {
                TupleMatch match = new TupleMatch(redundantTuple, newKey, null); // we don't need valueMapping
                tupleMatches.add(match);
            }
        }
        long end = System.currentTimeMillis();
        logger.debug("Time verifyMatchesRedundant (ms): {}", (end - start));
        return tupleMatches;
    }

}
