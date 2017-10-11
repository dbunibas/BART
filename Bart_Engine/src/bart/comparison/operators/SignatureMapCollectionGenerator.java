package bart.comparison.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bart.comparison.ComparisonStats;
import bart.comparison.ComparisonUtility;
import bart.comparison.SignatureAttributes;
import bart.comparison.SignatureMap;
import bart.comparison.SignatureMapCollection;
import bart.comparison.TupleSignature;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.Tuple;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;
import speedy.utility.comparator.StringComparator;

public class SignatureMapCollectionGenerator {

    private final static Logger logger = LoggerFactory.getLogger(SignatureMapCollectionGenerator.class);
    private final static String SIGNATURE_SEPARATOR = "|";

    public SignatureMapCollection generateIndexForTuples(List<TupleWithTable> tuples) {
        long start = System.currentTimeMillis();
        SignatureMapCollection signatureCollection = new SignatureMapCollection();
        for (TupleWithTable tupleWithTable : tuples) {
            Set<AttributeRef> groundAttributes = ComparisonUtility.findAttributesWithGroundValue(tupleWithTable.getTuple());
            if (groundAttributes.isEmpty()) {
                signatureCollection.addTupleWithoutGroundValues(tupleWithTable);
                continue;
            }
            TupleSignature maximalSignature = generateSignature(tupleWithTable, groundAttributes);
            if (logger.isDebugEnabled()) logger.debug("Signature: " + maximalSignature);
            SignatureMap signatureMap = signatureCollection.getOrCreateSignatureMap(maximalSignature.getSignatureAttribute());
            signatureMap.addSignature(maximalSignature);
        }
        long end = System.currentTimeMillis();
        if (logger.isInfoEnabled()) logger.info("Generating index time:" + (end - start) + " ms");
        ComparisonStats.getInstance().addStat(ComparisonStats.GENERATE_SIGNATURE_MAP_COLLECTION_TIME, end - start);
        return signatureCollection;
    }

    public TupleSignature generateSignature(TupleWithTable tupleWithTable, Collection<AttributeRef> attributes) {
        if (attributes.isEmpty()) {
            throw new IllegalArgumentException("Unable to generate signature for an empty set of attributes");
        }
        long start = System.currentTimeMillis();
        List<AttributeRef> sortedAttributes = new ArrayList<AttributeRef>(attributes);
        Collections.sort(sortedAttributes, new StringComparator());
        Tuple tuple = tupleWithTable.getTuple();
        String tableName = tupleWithTable.getTable();
        StringBuilder signature = new StringBuilder();
        for (AttributeRef attribute : sortedAttributes) {
            Cell cell = tuple.getCell(attribute);
            signature.append(cell.getValue()).append(SIGNATURE_SEPARATOR);
        }
        SpeedyUtility.removeChars(SIGNATURE_SEPARATOR.length(), signature);
        SignatureAttributes signatureAttribute = new SignatureAttributes(tableName, sortedAttributes);
        long end = System.currentTimeMillis();
        ComparisonStats.getInstance().addStat(ComparisonStats.GENERATE_TUPLE_SIGNATURE_TIME, end - start);
        return new TupleSignature(tuple, signatureAttribute, signature.toString());
    }
}
