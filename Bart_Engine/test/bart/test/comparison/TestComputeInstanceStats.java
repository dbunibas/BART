package bart.test.comparison;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class TestComputeInstanceStats extends TestCase {

    private final static Logger logger = LoggerFactory.getLogger(TestComputeInstanceStats.class);

    private static final String BASE_FOLDER = "/Users/Shared/Work/BartComparison/datasets/redundancy";
//    private static final String BASE_FOLDER = "/resources/redundancy/"; //Resource folder

    public void test() {
        IDatabase database = ComparisonUtilityTest.loadDatabase("doctors-1k/initial", BASE_FOLDER);
        List<TupleWithTable> tuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(database);
        logger.info("Database: {}", database.getFirstTable().getName());
        logger.info("Tuples: {}", database.getFirstTable().getSize());
        logger.info("Attributes: {}", database.getFirstTable().getAttributes().size());
        long cells = database.getFirstTable().getSize() * database.getFirstTable().getAttributes().size();
        logger.info("Cells: {}", cells);
        Map<IValue, Integer> valueHistogram = new HashMap<>();
        for (TupleWithTable tuple : tuples) {
            for (Cell cell : tuple.getTuple().getCells()) {
                if (cell.isOID()) continue;
                valueHistogram.put(cell.getValue(), valueHistogram.getOrDefault(cell.getValue(), 0) + 1);
            }
        }
        logger.trace("valueHistogram: {}", valueHistogram);
        SummaryStatistics constantStats = new SummaryStatistics();
        SummaryStatistics skolemStats = new SummaryStatistics();
        for (IValue value : valueHistogram.keySet()) {
            Integer occurrence = valueHistogram.get(value);
            if (value instanceof NullValue) {
                skolemStats.addValue(occurrence);
            } else if (value instanceof ConstantValue){
                constantStats.addValue(occurrence);
            } else {
                throw new IllegalArgumentException("Unknown value " + value);
            }
        }
        logger.info("Constant Stats: \n{}", constantStats.toString());
        logger.info("Skolem Stats: \n{}", skolemStats.toString());
        
        logger.info("% of constant values: {}%", constantStats.getSum() / cells);
        logger.info("% of skolem values: {}%", skolemStats.getSum() / cells);
    }

}
