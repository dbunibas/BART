package bart.model.algebra;

import bart.model.algebra.operators.IAlgebraTreeVisitor;
import bart.model.algebra.operators.ITupleIterator;
import bart.model.algebra.operators.ListTupleIterator;
import bart.model.database.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractRandomSample extends AbstractOperator {

    private static Logger logger = LoggerFactory.getLogger(ExtractRandomSample.class);
    private long sampleSize;
    private long floor;
    private long ceil;

    public ExtractRandomSample(long sampleSize, long floor, long ceil) {
        this.sampleSize = sampleSize;
        this.floor = floor;
        this.ceil = ceil;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitExtractRandomSample(this);
    }

    public String getName() {
        return "RANDOM SAMPLE [" + sampleSize + "] - Range [" + floor + "," + ceil + "]";
    }

    public long getSampleSize() {
        return sampleSize;
    }

    public long getCeil() {
        return ceil;
    }

    public long getFloor() {
        return floor;
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        if (logger.isDebugEnabled()) logger.debug("Extracting random sample from " + getChildren().get(0).toString("\t"));
        Map<Long, Tuple> childTuples = materializeChildTuples(source, target);
        if (logger.isDebugEnabled()) logger.debug("Original tuples: " + childTuples);
        Set<Long> randomSequence = generateRandomSequence();
        if (logger.isDebugEnabled()) logger.debug("Random sequence: " + randomSequence);
        List<Tuple> result = new ArrayList<Tuple>();
        for (Long randomIndex : randomSequence) {
            Tuple randomTuple = childTuples.get(randomIndex);
            if (randomTuple != null) {
                result.add(randomTuple);
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Result: " + result);
        return new ListTupleIterator(result);
    }

    private Map<Long, Tuple> materializeChildTuples(IDatabase source, IDatabase target) {
        Map<Long, Tuple> result = new HashMap<Long, Tuple>();
        ITupleIterator iterator = this.getChildren().get(0).execute(source, target);
        while (iterator.hasNext()) {
            Tuple tuple = iterator.next();
            long longOid = Long.parseLong(tuple.getOid().toString());
            result.put(longOid, tuple);
        }
        iterator.close();
        return result;
    }

    private Set<Long> generateRandomSequence() {
        Set<Long> result = new HashSet<Long>();
        while (result.size() != Math.min(sampleSize, ceil - floor + 1)) {
            result.add(getRandomLong(floor, ceil));
        }
        return result;
    }

    private long getRandomLong(long min, long max) {
        return min + ((long) (new Random().nextDouble() * (max - min + 1)));
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        return this.children.get(0).getAttributes(source, target);
    }

    @Override
    public IAlgebraOperator clone() {
        ExtractRandomSample clone = (ExtractRandomSample) super.clone();
        return clone;
    }

}
