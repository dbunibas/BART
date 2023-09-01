package bart.comparison.generator;

import bart.comparison.ComparisonConfiguration;
import bart.comparison.InstanceMatchTask;
import bart.comparison.TupleMapping;
import bart.comparison.TupleMatch;
import bart.comparison.operators.CheckTupleMatch;
import bart.comparison.operators.ComputeInstanceSimilarityHashing;
import bart.comparison.operators.ComputeScore;
import bart.utility.BartUtility;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.OperatorFactory;
import speedy.SpeedyConstants;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.algebra.operators.IUpdateCell;
import speedy.model.algebra.operators.mainmemory.MainMemoryDelete;
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
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.model.database.operators.IDatabaseManager;
import speedy.model.database.operators.IOIDGenerator;
import speedy.persistence.Types;
import static speedy.persistence.Types.REAL;
import speedy.utility.SpeedyUtility;

public class ComparisonScenarioGeneratorWithMappingsBigInstances {

    private final static Logger logger = LoggerFactory.getLogger(ComparisonScenarioGeneratorWithMappingsBigInstances.class);
    private Random random;
    private IDatabaseManager dbManager;
    private IInsertTuple insertTupleOperator;
    private IUpdateCell updateCellOperator;
    private IOIDGenerator oidGenerator;
    private MainMemoryDelete deleteOperator;
    private long lastPlaceholderId = 1;
    private long timeGeneration = 0;
    private int versionFile = 0;

    private int newRedundantTuplesPerc = 10;
    private int newRandomTuplesPerc = 5;
    private int cellsToChangePerc = 20;
    private ComputeInstanceSimilarityHashing similarityChecker = new ComputeInstanceSimilarityHashing(true);

    public ComparisonScenarioGeneratorWithMappingsBigInstances(int newRedundantTuplesPerc, int newRandomTuplesPerc, int cellsToChangePerc, long seed) {
        this.newRedundantTuplesPerc = newRedundantTuplesPerc;
        this.newRandomTuplesPerc = newRandomTuplesPerc;
        this.cellsToChangePerc = cellsToChangePerc;
        this.random = new Random(seed);
        this.similarityChecker.setIsForGeneration(true);
    }

    public InstancePair generateWithMappings(String originalDBPath, boolean changeSource, boolean changeTarget) {
        IDatabase originalDB = BartUtility.loadMainMemoryDatabase(originalDBPath);
        long start = System.currentTimeMillis();
        initOperators(originalDB);
        oidGenerator.initializeOIDs(originalDB);
//        List<TupleWithTable> originalTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(originalDB);
        logger.info("Load left DB");
        Integer leftCounter = IntegerOIDGenerator.getCounter();
        IDatabase leftDB = dbManager.cloneTarget(BartUtility.loadMainMemoryDatabase(originalDBPath), "_left");
        logger.info("Loaded left DB");
        logger.info("Load right DB");
        Integer rightCounter = IntegerOIDGenerator.getCounter();
        List<TupleWithTable> leftTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(leftDB);
        IDatabase rightDB = dbManager.cloneTarget(BartUtility.loadMainMemoryDatabase(originalDBPath), "_right");
        logger.info("Loaded right DB");
        List<TupleWithTable> rightTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(rightDB);
        TupleMapping tupleMapping = initMappings(leftTuples, rightTuples);
        logger.info("Left tuples: {}, Right Tuples: {}", leftTuples.size(), rightTuples.size());
        // fist change the cells
        boolean changed = false;
        if (changeSource && changeTarget) {
            modifyCellsSourceAndTarget(leftTuples, rightTuples, leftDB, rightDB, tupleMapping);
            changed = true;
        } else {
            if (changeSource) {
                modifyCellsSource(leftTuples, rightTuples, leftDB, tupleMapping);
                changed = true;
            }
            if (changeTarget) {
                modifyCellsTarget(leftTuples, rightTuples, rightDB, tupleMapping);
                changed = true;
            }
        }
        IntegerOIDGenerator.setCounter(leftCounter);
        leftDB = updateDBFromCSV(leftTuples, originalDBPath);
        IntegerOIDGenerator.setCounter(rightCounter);
        rightDB = updateDBFromCSV(rightTuples, originalDBPath);
        tupleMapping.updateValueMappings();
        if (changed) {
            logger.info("Computing Greedy for cleaning");
            InstanceMatchTask greedyResult = similarityChecker.compare(leftDB, rightDB);
            checkAndCleanMatches(leftDB, rightDB, leftTuples, rightTuples, tupleMapping, greedyResult);
        }
        // add redundant or random tuples
        if (changeSource) {
            if (!ComparisonConfiguration.isFunctional()) {
                addRandomTuples(leftDB, rightDB, tupleMapping, true);
                addRedundantTuples(leftDB, rightDB, tupleMapping, true);
            }
        }
        if (changeTarget) {
            if (!ComparisonConfiguration.isInjective()) {
                addRandomTuples(rightDB, leftDB, tupleMapping, false);
                addRedundantTuples(rightDB, leftDB, tupleMapping, false);
            }
        }
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
        this.deleteOperator = new MainMemoryDelete();
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
        logger.debug("Init-Mappings TupleMapping {}", tupleMapping.getTupleMapping().size());
        logger.debug("Init-Mapping Reverse TupleMapping {}", tupleMapping.getReverseTupleMapping().size());
        return tupleMapping;
    }

    private TupleWithTable modifyTuple(IDatabase db, TupleWithTable originalTuple) {
        ITable table = db.getTable(originalTuple.getTable());
        TupleWithTable clonedSource = originalTuple.clone();
        for (Cell cell : clonedSource.getTuple().getCells()) {
            if (cell.isOID()) {
                continue;
            }
            if (random.nextInt(100) <= cellsToChangePerc) {
                Attribute attribute = table.getAttribute(cell.getAttribute());
                IValue newValue = generateNewValueForModify(attribute.getType());
                cell.setValue(newValue);
            }
        }
        clonedSource.setIsForGeneration(true);
        return clonedSource;
    }

    private void addRedundantTuples(IDatabase thisDB, IDatabase otherDB, TupleMapping tupleMapping, boolean isLeft) {
        if (newRedundantTuplesPerc < 0 || newRedundantTuplesPerc > 100) {
            throw new IllegalArgumentException("Redundancy percentage must be >= 0 and <= 100");
        }
        logger.info("Add Redundant Tuples");
        long start = System.currentTimeMillis();
        List<TupleWithTable> tuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(thisDB);
        long end = System.currentTimeMillis();
        logger.debug("Time extractAllTuples (ms): {}", (end - start));
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
        logger.info("Tuples to duplicate: {}", tuplesToDuplicate.size());
        List<TupleWithTable> redundantTuples = cloneTuples(tuplesToDuplicate);
//        List<TupleWithTable> otherTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(otherDB);
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
//            List<TupleMatch> verifyMatches = verifyMatches(redundantTuple, otherTuples); // exhaustive search
            List<TupleMatch> verifyMatches = verifyMatchesRedundant(redundantTuple, originalTuple, tupleMapping, isLeft);
//            start = System.currentTimeMillis();
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
//            logger.info("Creating tupleMatches (ms) : {}", (System.currentTimeMillis() - start));
//            start = System.currentTimeMillis();
            insertTupleOperator.execute(thisDB.getTable(redundantTuple.getTable()), redundantTuple.getTuple(), null, thisDB);
//            end = System.currentTimeMillis();
//            logger.info("Insert Tuple operator (ms): {}", (end - start));
//            logger.debug("Tuple Mappings: \n{}", tupleMapping);
//            logger.debug("Database: {}", thisDB);
        }
    }

    private void addRandomTuples(IDatabase thisDB, IDatabase otherDB, TupleMapping tupleMapping, boolean isLeft) {
        if (newRandomTuplesPerc == 0) {
            return;
        }
        List<TupleWithTable> tuplesInOtherDB = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(otherDB);
        logger.info("*** Add Random Tuple");
        for (String tableName : thisDB.getTableNames()) {
//            List<TupleWithTable> tuplesInDB = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(thisDB);
            ITable table = thisDB.getTable(tableName);
            double tuplesToAddD = table.getSize() * (newRandomTuplesPerc / 100.0);
            int tuplesToAdd = (int) Math.ceil(tuplesToAddD);
            logger.info("*** Random tuples to add: {}", tuplesToAdd);
            for (int i = 0; i < tuplesToAdd; i++) {
                long tStart = System.currentTimeMillis();
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
                logger.debug("Generated Random Tuple: {}", newTupleWithTable);
                if (verifyMatchesWithTimer(newTupleWithTable, tuplesInOtherDB, 1).isEmpty()) {
//                if (verifyMatchesWithLimit(newTupleWithTable, tuplesInOtherDB, 1).isEmpty()) {
//                if (verifyMatchesWithLimit(newTupleWithTable, tuplesInOtherDB, 1).isEmpty()
//                        && verifyMatchesWithLimit(newTupleWithTable, tuplesInDB, 1).isEmpty()) {
                    logger.debug("Add Random Tuple to DB: {}", newTuple);
                    nonMatchingTuples.add(newTupleWithTable);
                    insertTupleOperator.execute(table, newTuple, null, thisDB);
                    logger.debug("Tuple Mappings: \n{}", tupleMapping);
                    logger.debug("Database: {}", thisDB);
//                    tuplesInDB = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(thisDB);
                }
//                if (verifyMatches(newTupleWithTable, tuplesInOtherDB).isEmpty()) {
//                    if (verifyMatches(newTupleWithTable, tuplesInDB).isEmpty()) {
//                        logger.debug("Add Random Tuple to DB: {}", newTuple);
//                        nonMatchingTuples.add(newTupleWithTable);
//                        insertTupleOperator.execute(table, newTuple, null, thisDB);
//                        logger.debug("Tuple Mappings: \n{}", tupleMapping);
//                        logger.debug("Database: {}", thisDB);
//                        tuplesInDB = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(thisDB);
//                    }
//                }
//               logger.info("addRandomTuples - Tuple time {} (ms)", System.currentTimeMillis() - tStart);
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

    private TupleMatch verifyMatch(TupleWithTable source, TupleWithTable target) {
        CheckTupleMatch tupleMatch = new CheckTupleMatch();
        long start = System.currentTimeMillis();
        TupleMatch checkMatch = tupleMatch.checkMatch(source, target);
        long end = System.currentTimeMillis();
        logger.debug("Time verifyMatch (ms) {}", (end - start));
        return checkMatch;
    }

    private List<TupleWithTable> cloneTuples(List<TupleWithTable> tuplesToDuplicate) {
        long start = System.currentTimeMillis();
        List<TupleWithTable> result = new ArrayList<TupleWithTable>();
        for (TupleWithTable tupleWithTable : tuplesToDuplicate) {
            Tuple clonedTuple = tupleWithTable.getTuple().clone();
            TupleOID tupleOID = new TupleOID(oidGenerator.getNextOID(tupleWithTable.getTable()));
            clonedTuple.setOidNested(tupleOID);
            TupleWithTable cloneTupleWithTable = new TupleWithTable(tupleWithTable.getTable(), clonedTuple);
            cloneTupleWithTable.setIsForGeneration(true);
            result.add(cloneTupleWithTable);
        }
        long end = System.currentTimeMillis();
        logger.debug("Time cloneTuples (ms): {}", (end - start));
        return result;
    }

    private List<TupleMatch> verifyMatchesRedundant(TupleWithTable redundantTuple, TupleWithTable originalTuple, TupleMapping tupleMapping, boolean isLeft) {
//        long start = System.currentTimeMillis();
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
            // TODO: this can be slow with big instances
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
//        logger.info("Time verifyMatchesRedundant (ms): {}", (System.currentTimeMillis() - start));
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
        logger.debug("Time verifyMatchesWithTimert: {}", (end - start));
        return matches;
    }

    private void checkAndCleanMatches(IDatabase leftDB, IDatabase rightDB, List<TupleWithTable> leftTuples, List<TupleWithTable> rightTuples, TupleMapping tupleMappingGenerated, InstanceMatchTask result) {
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(leftTuples, rightTuples, tupleMappingGenerated);
        Double scoreGreedy = result.getTupleMapping().getScore();
        if (scoreGreedy == null) {
            scoreGreedy = 0.0;
        }
        logger.info("Check and Clean Matches: Score Greedy: {} - Score Generated: {}", scoreGreedy, score);
        if (scoreGreedy > score) {
            Map<TupleWithTable, Set<TupleWithTable>> tupleMappingGreedy = result.getTupleMapping().getTupleMapping();
            Map<TupleWithTable, Set<TupleWithTable>> tupleMappingGen = tupleMappingGenerated.getTupleMapping();
            logger.debug("Tuple Mapping Greedy: {}", BartUtility.printMap(tupleMappingGreedy));
            logger.debug("Tuple Mapping Gen: {}", BartUtility.printMap(tupleMappingGreedy));
            Set<TupleWithTable> keysToRemoveFromMappings = new HashSet<>();
            Set<TupleOID> oidsLeft = new HashSet<>();
            Set<TupleOID> oidsRight = new HashSet<>();
            for (TupleWithTable tupleWithTable : tupleMappingGreedy.keySet()) {
                Set<TupleWithTable> tupleSetRightRemove = tupleMappingGreedy.get(tupleWithTable);
                tupleWithTable.setIsForGeneration(true);
                Set<TupleWithTable> tupleSet = tupleMappingGen.get(tupleWithTable);
                if (tupleSet == null) {
                    keysToRemoveFromMappings.add(tupleWithTable);
                    oidsLeft.add(tupleWithTable.getTuple().getOid());
                    for (TupleWithTable tupleRemoveRight : tupleSetRightRemove) {
                        oidsRight.add(tupleRemoveRight.getTuple().getOid());
                    }
                }
            }
            MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingGreedy, tupleMappingGen);
            Set<TupleWithTable> tupleRemoveAlgorithm = difference.entriesOnlyOnRight().keySet();
            for (TupleWithTable tupleWithTable : tupleRemoveAlgorithm) {
                Set<TupleWithTable> tupleSetRightRemove = tupleMappingGen.get(tupleWithTable);
                Set<TupleWithTable> tupleSet = tupleMappingGreedy.get(tupleWithTable);
                if (tupleSet == null) {
                    keysToRemoveFromMappings.add(tupleWithTable);
                    oidsLeft.add(tupleWithTable.getTuple().getOid());
                    for (TupleWithTable tupleRemoveRight : tupleSetRightRemove) {
                        oidsRight.add(tupleRemoveRight.getTuple().getOid());
                    }
                }
            }
            logger.debug("Keys to remove from mapping: {}", keysToRemoveFromMappings.size());
            logger.debug("Tuples to delete left: {}", oidsLeft.size());
            logger.debug("Tuples to delete right: {}", oidsRight.size());
//            logger.info("Differences: \n");
//            print(difference);
//            logger.info("Tuples Remove Left {}", tupleRemoveLeft.size());
//            logger.info("Tuples Remove Right {}", tupleRemoveRight.size());
            for (TupleWithTable tupleWithTable : keysToRemoveFromMappings) {
                tupleWithTable.setIsForGeneration(true);
                tupleMappingGenerated.removeKeyTupleMapping(tupleWithTable);
            }
            this.deleteOperator.deleteTuples(leftTuples.get(0).getTable(), new ArrayList<>(oidsLeft), leftDB);
            this.deleteOperator.deleteTuples(rightTuples.get(0).getTable(), new ArrayList<>(oidsRight), rightDB);
            leftTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(leftDB);
            rightTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(rightDB);
            tupleMappingGenerated.updateValueMappings();

            computeScore = new ComputeScore();
            score = computeScore.computeScore(leftTuples, rightTuples, tupleMappingGenerated);
            result = similarityChecker.compare(leftDB, rightDB);
            scoreGreedy = result.getTupleMapping().getScore();
            if (scoreGreedy == null) {
                scoreGreedy = 0.0;
            }
            logger.info("New Score Greedy: {} - New Score Generated: {}", scoreGreedy, score);
        }
    }

    private void modifyCellsSource(List<TupleWithTable> leftTuples, List<TupleWithTable> rightTuples, IDatabase leftDB, TupleMapping tupleMapping) {
        if (this.cellsToChangePerc == 0) {
            return;
        }
        logger.info("*** Change Cells in Source");
        for (int row = 0; row < leftTuples.size(); row++) {
            long start = System.currentTimeMillis();
            TupleWithTable leftTuple = leftTuples.get(row);
            TupleWithTable rightTuple = rightTuples.get(row);
            TupleWithTable modifiedLeftTuple = modifyTuple(leftDB, leftTuple);
            leftTuples.remove(row);
            leftTuples.add(row, modifiedLeftTuple);
//            if (!leftTuple.equals(modifiedLeftTuple)) {
//                this.updateTuple(leftTuple, modifiedLeftTuple, leftDB);
//            }
            long ss = System.currentTimeMillis();
            tupleMapping.removeKeyTupleMapping(leftTuple);
            long se = System.currentTimeMillis();
            logger.debug("removeKeyTupleMapping ms {}", (se - ss));
            TupleMatch vm = this.verifyMatch(modifiedLeftTuple, rightTuple);
            if (vm != null) {
                tupleMapping.putTupleMapping(modifiedLeftTuple, rightTuple);
            }
            long end = System.currentTimeMillis();
            logger.debug("Changed in ms: {}", (end - start));
        }
    }

    private void modifyCellsTarget(List<TupleWithTable> leftTuples, List<TupleWithTable> rightTuples, IDatabase rightDB, TupleMapping tupleMapping) {
        if (this.cellsToChangePerc == 0) {
            return;
        }
        for (int row = 0; row < leftTuples.size(); row++) {
            TupleWithTable leftTuple = leftTuples.get(row);
            TupleWithTable rightTuple = rightTuples.get(row);
            TupleWithTable modifiedRight = modifyTuple(rightDB, rightTuple);
            rightTuples.remove(row);
            rightTuples.add(row, modifiedRight);
//            if (!rightTuple.equals(modifiedRight)) {
//                this.updateTuple(rightTuple, modifiedRight, rightDB);
//            }
            tupleMapping.removeKeyTupleMapping(leftTuple);
            TupleMatch vm = this.verifyMatch(leftTuple, modifiedRight);
            if (vm != null) {
                tupleMapping.putTupleMapping(leftTuple, modifiedRight);
            }
        }
    }

    private void modifyCellsSourceAndTarget(List<TupleWithTable> leftTuples, List<TupleWithTable> rightTuples, IDatabase leftDB, IDatabase rightDB, TupleMapping tupleMapping) {
        if (this.cellsToChangePerc == 0) {
            return;
        }
        for (int row = 0; row < leftTuples.size(); row++) {
            TupleWithTable leftTuple = leftTuples.get(row);
            TupleWithTable rightTuple = rightTuples.get(row);
            TupleWithTable modifiedLeftTuple = null;
            TupleWithTable modifiedRightTuple = null;
            int updateChoiche = random.nextInt(3);
            if (updateChoiche == 0) {
                // only left
                modifiedLeftTuple = modifyTuple(leftDB, leftTuple);
                leftTuples.remove(row);
                leftTuples.add(row, modifiedLeftTuple);
                modifiedRightTuple = rightTuple;
            }
            if (updateChoiche == 1) {
                // only right
                modifiedLeftTuple = leftTuple;
                modifiedRightTuple = modifyTuple(rightDB, rightTuple);
                rightTuples.remove(row);
                rightTuples.add(row, modifiedRightTuple);
            }
            if (updateChoiche == 2) {
                // both
                modifiedLeftTuple = modifyTuple(leftDB, leftTuple);
                leftTuples.remove(row);
                leftTuples.add(row, modifiedLeftTuple);
                modifiedRightTuple = modifyTuple(rightDB, rightTuple);
                rightTuples.remove(row);
                rightTuples.add(row, modifiedRightTuple);
            }
            // update on DB takes too much time, work only on main memory
//            if (!leftTuple.equals(modifiedLeftTuple)) {
//                this.updateTuple(leftTuple, modifiedLeftTuple, leftDB);
//            }
//            if (!rightTuple.equals(modifiedRightTuple)) {
//                this.updateTuple(rightTuple, modifiedRightTuple, rightDB);
//            }
            tupleMapping.removeKeyTupleMapping(leftTuple);
            TupleMatch vm = this.verifyMatch(modifiedLeftTuple, modifiedRightTuple);
            if (vm != null) {
                tupleMapping.putTupleMapping(modifiedLeftTuple, modifiedRightTuple);
            }

        }
    }

    private void print(MapDifference<TupleWithTable, Set<TupleWithTable>> difference) {
        Map<TupleWithTable, Set<TupleWithTable>> onlyOnGenerated = difference.entriesOnlyOnLeft();
        String onlyOnGeneratedString = "Only on Generated:\n" + SpeedyUtility.printMapCompact(onlyOnGenerated);
        Map<TupleWithTable, Set<TupleWithTable>> onlyOnAlgorithm = difference.entriesOnlyOnRight();
        String onlyOnAlgorithmString = "Only on Algorithm:\n" + SpeedyUtility.printMapCompact(onlyOnAlgorithm);
        Map<TupleWithTable, MapDifference.ValueDifference<Set<TupleWithTable>>> entriesDiffering = difference.entriesDiffering();
        String differencesInMapString = "Differences in common:\n" + SpeedyUtility.printMapCompact(entriesDiffering);
        String toPrint = "-----------------------------\n"
                + onlyOnGeneratedString
                + "-----------------------------\n"
                + onlyOnAlgorithmString
                + "-----------------------------\n"
                + differencesInMapString;
        System.out.println(toPrint);
    }

    private IDatabase updateDBFromCSV(List<TupleWithTable> tuples, String originalPath) {
        File folder = new File(originalPath);
        String[] tables = folder.list((File dir, String name) -> name.toLowerCase().endsWith(".csv"));
        // we have only one table per exp
        String table = tables[0];
        File parentFolder = folder.getParentFile();
        String newFolder = "modified_ver_" + this.versionFile;
        String newFolderPath = parentFolder.getAbsolutePath() + File.separator + newFolder;
        try {
            Files.createDirectories(Paths.get(newFolderPath));
            String csvFileToCreate = newFolderPath + File.separator + table;
            List<String> headers = getHeaders(tuples);
            List<List<String>> rows = getRows(tuples);
            CSVFormat csvFormat = CSVFormat.DEFAULT.withQuote('"').withDelimiter(',');
            CSVPrinter printer = new CSVPrinter(new FileWriter(csvFileToCreate), csvFormat);
            printer.printRecord(headers);
            printer.printRecords(rows);
            printer.close();
        } catch (IOException ex) {
            logger.error("Unable to create folder: {}", newFolderPath);
        }
        IDatabase originalDB = BartUtility.loadMainMemoryDatabase(newFolderPath);
        this.versionFile++;
        return originalDB;
    }

    private List<String> getHeaders(List<TupleWithTable> tuples) {
        TupleWithTable tuple = tuples.get(0);
        List<String> headers = new ArrayList<>();
        for (Cell cell : tuple.getTuple().getCells()) {
            if (cell.isOID()) {
                continue;
            }
            headers.add(cell.getAttribute());
        }
        return headers;
    }

    private List<List<String>> getRows(List<TupleWithTable> tuples) {
        List<List<String>> rows = new ArrayList<>();
        for (TupleWithTable tuple : tuples) {
            List<String> tupleString = new ArrayList<>();
            for (Cell cell : tuple.getTuple().getCells()) {
                if (cell.isOID()) {
                    continue;
                }
                tupleString.add(cell.getValue().toString());
            }
            rows.add(tupleString);
        }
        return rows;
    }

}
