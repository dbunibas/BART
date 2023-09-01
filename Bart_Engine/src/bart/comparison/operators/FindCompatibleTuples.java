package bart.comparison.operators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import bart.comparison.AttributeValueMap;
import bart.comparison.ComparisonStats;
import bart.comparison.CompatibilityCache;
import bart.comparison.CompatibilityMap;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.IValue;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;
import speedy.utility.collection.ComputeSetIntersection;

public class FindCompatibleTuples {

    private final static Logger logger = LoggerFactory.getLogger(FindCompatibleTuples.class);
    private final ComputeSetIntersection<TupleWithTable> intersector = new ComputeSetIntersection<TupleWithTable>();
    private final long maxSizeCompatibilityCache = 10000;

    //For each tuple in the second-db, we associate a set of compatible tuples from the first db
    public CompatibilityMap find(List<TupleWithTable> firstDB, List<TupleWithTable> secondDB) {
        long start = System.currentTimeMillis();
        Set<TupleWithTable> allFirstDBTuples = new HashSet<TupleWithTable>(firstDB);
        AttributeValueMap firstDBValueMap = buildAttributeValueMap(firstDB);
        CompatibilityMap compatibilityMap = new CompatibilityMap();
//        CompatibilityCache compatibilityCache = new CompatibilityCache();
        Cache<AttributeWithValue, Set<TupleWithTable>> compatibilityCache = Caffeine.newBuilder().maximumSize(maxSizeCompatibilityCache).build();
        //TODO: Thread
        for (TupleWithTable secondTuple : secondDB) {
            Set<TupleWithTable> compatibilesSourceTuples = findCompatibleTuples(secondTuple, firstDBValueMap, allFirstDBTuples, compatibilityCache);
            if (logger.isDebugEnabled()) logger.debug("Tuples compatible with " + secondTuple + ":\n" + SpeedyUtility.printCollection(compatibilesSourceTuples, "\t"));
            compatibilityMap.setCompatibilityForTuple(secondTuple, compatibilesSourceTuples);
        }
        ComparisonStats.getInstance().addStat(ComparisonStats.FIND_COMPATIBLE_TUPLES_TIMES, System.currentTimeMillis() - start);
        return compatibilityMap;
    }

    private AttributeValueMap buildAttributeValueMap(List<TupleWithTable> tuples) {
        AttributeValueMap attributeValueMap = new AttributeValueMap();
        for (TupleWithTable tuple : tuples) {
            for (Cell cell : tuple.getTuple().getCells()) {
                if (cell.isOID()) {
                    continue;
                }
                AttributeRef attribute = cell.getAttributeRef();
                IValue value = cell.getValue();
                if (SpeedyUtility.isPlaceholder(value)) {
                    value = SpeedyConstants.WILDCARD;
                }
                attributeValueMap.addValueMapForAttribute(attribute, value, tuple);
            }
        }
        return attributeValueMap;
    }

    @SuppressWarnings("unchecked")
//    private Set<TupleWithTable> findCompatibleTuples(TupleWithTable secondTuple, AttributeValueMap firstDBValueMap, Set<TupleWithTable> allFirstDBTuples, CompatibilityCache compatibilityCache) {
    private Set<TupleWithTable> findCompatibleTuples(TupleWithTable secondTuple, AttributeValueMap firstDBValueMap, Set<TupleWithTable> allFirstDBTuples, Cache<AttributeWithValue, Set<TupleWithTable>> compatibilityCache) {
        if (logger.isDebugEnabled()) logger.debug("Finding compatible tuples for " + secondTuple + "...");
        if (logger.isDebugEnabled()) logger.debug("DB Value Map: " + firstDBValueMap);
        List<Set<TupleWithTable>> tuplesToIntersect = new ArrayList<Set<TupleWithTable>>();
        for (Cell cell : secondTuple.getTuple().getCells()) {
            if (cell.isOID()) {
                continue;
            }
            AttributeRef attribute = cell.getAttributeRef();
            IValue value = cell.getValue();
//            Set<TupleWithTable> compatibleTuplesForValue = findCompatibleTuplesForAttributeValue(attribute, value, firstDBValueMap, compatibilityCache);
            Set<TupleWithTable> compatibleTuplesForValue = compatibilityCache.get(new AttributeWithValue(attribute, value), k -> computeCompatibility(attribute, value, firstDBValueMap));
            if (compatibleTuplesForValue == null) {
                continue;
            }
            tuplesToIntersect.add(compatibleTuplesForValue);
        }
        Set<TupleWithTable> result;
        if (tuplesToIntersect.isEmpty()) {
            //All values are placeholders
            result = allFirstDBTuples;
        } else {
            result = intersector.computeIntersection(tuplesToIntersect);
        }
        if (logger.isDebugEnabled()) logger.debug("Result: " + result);
        return result;
    }

    private Set<TupleWithTable> findCompatibleTuplesForAttributeValue(AttributeRef attribute, IValue value, AttributeValueMap firstDBValueMap, CompatibilityCache compatibilityCache) {
        Set<TupleWithTable> compatibileTuples = compatibilityCache.getCompatibilitiesForValue(attribute, value);
        if (compatibileTuples != null) { //Using cached value
            return compatibileTuples;
        }
        if (SpeedyUtility.isPlaceholder(value)) {
            return null; //All tuples are compatible
        }
        compatibileTuples = new HashSet<TupleWithTable>();
        compatibileTuples.addAll(firstDBValueMap.getTuplesWithValue(attribute, value));
        compatibileTuples.addAll(firstDBValueMap.getTuplesWithValue(attribute, SpeedyConstants.WILDCARD));
        compatibilityCache.addCompatibilitiesForValue(attribute, value, compatibileTuples);
        return compatibileTuples;
    }
    
    private Set<TupleWithTable> computeCompatibility(AttributeRef attribute, IValue value, AttributeValueMap firstDBValueMap) {
        if (SpeedyUtility.isPlaceholder(value)) {
            return null; //All tuples are compatible
        }
        Set<TupleWithTable> compatibileTuples = new HashSet<TupleWithTable>();
//        firstDBValueMap.getTuplesWithValue(attribute, value);
        compatibileTuples.addAll(firstDBValueMap.getTuplesWithValue(attribute, value));
        compatibileTuples.addAll(firstDBValueMap.getTuplesWithValue(attribute, SpeedyConstants.WILDCARD));
//        compatibilityCache.addCompatibilitiesForValue(attribute, value, compatibileTuples);
        return compatibileTuples;
    }
    
    class AttributeWithValue {

        public AttributeWithValue(AttributeRef attribute, IValue value) {
            this.attribute = attribute;
            this.value = value;
        }

        AttributeRef attribute;
        IValue value;

        @Override
        public String toString() {
            return attribute.toString() + "-" + value.toString();
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this.toString().equals(obj.toString());
        }
    }
}
