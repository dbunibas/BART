package bart.model.errorgenerator.operator;

import bart.model.EGTask;
import bart.model.VioGenQueryConfiguration;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.TableAlias;
import bart.model.errorgenerator.SampleParameters;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableSizeSampleStrategy extends AbstractSampleStrategy {

    private static Logger logger = LoggerFactory.getLogger(TableSizeSampleStrategy.class);

    public SampleParameters computeParameters(IAlgebraOperator operator, Set<TableAlias> tableInFormula, String queryType, int sampleSize, VioGenQueryConfiguration queryConfiguration, EGTask task) {
        if (logger.isDebugEnabled()) logger.debug("Tables in formula: " + tableInFormula);
        long tableSize = getTableSize(tableInFormula, task);
        if (logger.isDebugEnabled()) logger.debug("Table size: " + tableSize);
        double sizeFactor = getSizeFactor(queryType, queryConfiguration);
        double windowSizeFactor = getWindowSizeFactor(queryType, queryConfiguration);
        int windowSize = getWindowSize(windowSizeFactor, tableSize, sampleSize, sizeFactor);
        Double probability = findProbability(sampleSize, windowSize);
        double probabilityFactor = getProbabilityFactor(queryType, queryConfiguration);
        if (probability != null) {
            probability *= probabilityFactor;
        }
        boolean useLimit = getUseLimit(queryType, queryConfiguration);
        Integer limit = null;
        if (useLimit) {
            limit = windowSize;
        }
        boolean useOffset = getUseOffset(queryType, queryConfiguration);
        Integer offset = null;
        if (useOffset) {
            double offsetFactor = getOffsetFactor(queryType, queryConfiguration);
            int maxOffset = (int) ((1 * sizeFactor) * offsetFactor * tableSize);
            if (maxOffset == 0) {
                offset = 0;
            } else {
                offset = new Random().nextInt(maxOffset);
            }
        }
        return new SampleParameters(probability, limit, offset);
    }

}
