package bart.model.errorgenerator.operator;

import bart.BartConstants;
import bart.model.EGTask;
import bart.model.VioGenQueryConfiguration;
import speedy.model.database.ITable;
import speedy.model.database.TableAlias;
import bart.model.errorgenerator.ISampleStrategy;
import java.util.Set;

public abstract class AbstractSampleStrategy implements ISampleStrategy {

    protected int getWindowSize(double windowSizeFactor, long tableSize, int sampleSize, double sizeFactor) {
        int windowSize = (int) (sampleSize * windowSizeFactor);
        return windowSize;
//        int expectedQuerySize = (int) (tableSize * sizeFactor);
//        return Math.min(windowSize, expectedQuerySize);
    }

    protected Double findProbability(Integer sampleSize, long windowSize) {
        if (sampleSize == 1) {
            return null;
        }
        return (double) sampleSize / windowSize;
    }

    protected long getTableSize(Set<TableAlias> tableInFormula, EGTask task) {
        if (tableInFormula.isEmpty()) {
            return 0;
        }
        long max = 0;
        for (TableAlias tableAlias : tableInFormula) {
            ITable table;
            if (tableAlias.isSource()) {
                table = task.getSource().getTable(tableAlias.getTableName());
            } else {
                table = task.getTarget().getTable(tableAlias.getTableName());
            }
            if (table.getSize() > max) {
                max = table.getSize();
            }
        }
//        for (TableAlias tableAlias : tableInFormula) {
//            ITable table;
//            if (tableAlias.isSource()) {
//                table = task.getSource().getTable(tableAlias.getTableName());
//            } else {
//                table = task.getTarget().getTable(tableAlias.getTableName());
//            }
//            total *= table.getSize();
//        }
        return max;
    }

    protected double getSizeFactor(String queryType, VioGenQueryConfiguration configuration) {
        if (queryType.equals(BartConstants.STANDARD_QUERY_TYPE)) {
            return configuration.getSizeFactorForStandardQueries();
        }
        if (queryType.equals(BartConstants.SYMMETRIC_QUERY_TYPE)) {
            return configuration.getSizeFactorForSymmetricQueries();
        }
        if (queryType.equals(BartConstants.INEQUALITY_QUERY_TYPE)) {
            return configuration.getSizeFactorForInequalityQueries();
        }
        if (queryType.equals(BartConstants.SINGLE_TUPLE_QUERY_TYPE)) {
            return configuration.getSizeFactorForSingleTupleQueries();
        }
        throw new IllegalArgumentException("Unknow query type " + queryType);
    }

    protected double getProbabilityFactor(String queryType, VioGenQueryConfiguration configuration) {
        if (queryType.equals(BartConstants.STANDARD_QUERY_TYPE)) {
            return configuration.getProbabilityFactorForStandardQueries();
        }
        if (queryType.equals(BartConstants.SYMMETRIC_QUERY_TYPE)) {
            return configuration.getProbabilityFactorForSymmetricQueries();
        }
        if (queryType.equals(BartConstants.INEQUALITY_QUERY_TYPE)) {
            return configuration.getProbabilityFactorForInequalityQueries();
        }
        if (queryType.equals(BartConstants.SINGLE_TUPLE_QUERY_TYPE)) {
            return configuration.getProbabilityFactorForSingleTupleQueries();
        }
        throw new IllegalArgumentException("Unknow query type " + queryType);
    }

    protected double getOffsetFactor(String queryType, VioGenQueryConfiguration configuration) {
        if (queryType.equals(BartConstants.STANDARD_QUERY_TYPE)) {
            return configuration.getOffsetFactorForStandardQueries();
        }
        if (queryType.equals(BartConstants.SYMMETRIC_QUERY_TYPE)) {
            return configuration.getOffsetFactorForSymmetricQueries();
        }
        if (queryType.equals(BartConstants.INEQUALITY_QUERY_TYPE)) {
            return configuration.getOffsetFactorForInequalityQueries();
        }
        if (queryType.equals(BartConstants.SINGLE_TUPLE_QUERY_TYPE)) {
            return configuration.getOffsetFactorForSingleTupleQueries();
        }
        throw new IllegalArgumentException("Unknow query type " + queryType);
    }

    protected boolean getUseLimit(String queryType, VioGenQueryConfiguration configuration) {
        if (queryType.equals(BartConstants.STANDARD_QUERY_TYPE)) {
            return configuration.isUseLimitInStandardQueries();
        }
        if (queryType.equals(BartConstants.SYMMETRIC_QUERY_TYPE)) {
            return configuration.isUseLimitInSymmetricQueries();
        }
        if (queryType.equals(BartConstants.INEQUALITY_QUERY_TYPE)) {
            return configuration.isUseLimitInInequalityQueries();
        }
        if (queryType.equals(BartConstants.SINGLE_TUPLE_QUERY_TYPE)) {
            return configuration.isUseLimitInSingleTupleQueries();
        }
        throw new IllegalArgumentException("Unknow query type " + queryType);
    }

    protected boolean getUseOffset(String queryType, VioGenQueryConfiguration configuration) {
        if (queryType.equals(BartConstants.STANDARD_QUERY_TYPE)) {
            return configuration.isUseOffsetInStandardQueries();
        }
        if (queryType.equals(BartConstants.SYMMETRIC_QUERY_TYPE)) {
            return configuration.isUseOffsetInSymmetricQueries();
        }
        if (queryType.equals(BartConstants.INEQUALITY_QUERY_TYPE)) {
            return configuration.isUseOffsetInInequalityQueries();
        }
        if (queryType.equals(BartConstants.SINGLE_TUPLE_QUERY_TYPE)) {
            return configuration.isUseOffsetInSingleTupleQueries();
        }
        throw new IllegalArgumentException("Unknow query type " + queryType);
    }

    protected double getWindowSizeFactor(String queryType, VioGenQueryConfiguration configuration) {
        if (queryType.equals(BartConstants.STANDARD_QUERY_TYPE)) {
            return configuration.getWindowSizeFactorForStandardQueries();
        }
        if (queryType.equals(BartConstants.SYMMETRIC_QUERY_TYPE)) {
            return configuration.getWindowSizeFactorForSymmetricQueries();
        }
        if (queryType.equals(BartConstants.INEQUALITY_QUERY_TYPE)) {
            return configuration.getWindowSizeFactorForInequalityQueries();
        }
        if (queryType.equals(BartConstants.SINGLE_TUPLE_QUERY_TYPE)) {
            return configuration.getWindowSizeFactorForSingleTupleQueries();
        }
        throw new IllegalArgumentException("Unknow query type " + queryType);
    }

}
