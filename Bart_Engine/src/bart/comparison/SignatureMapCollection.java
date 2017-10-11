package bart.comparison;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class SignatureMapCollection {

    private Map<SignatureAttributes, SignatureMap> signatures = new HashMap<SignatureAttributes, SignatureMap>();
    private Map<String, List<SignatureAttributes>> signatureAttributesForTable = new HashMap<String, List<SignatureAttributes>>();
    private List<TupleWithTable> tuplesWithoutGroundValues = new ArrayList<TupleWithTable>();

    public SignatureMap getSignatureForAttributes(SignatureAttributes signatureAttributes) {
        return signatures.get(signatureAttributes);
    }

    public SignatureMap getOrCreateSignatureMap(SignatureAttributes signatureAttribute) {
        SignatureMap result = signatures.get(signatureAttribute);
        if (result == null) {
            result = new SignatureMap(signatureAttribute);
            signatures.put(signatureAttribute, result);
            updateSignatureAttributesForTable(signatureAttribute);
        }
        return result;
    }

    private void updateSignatureAttributesForTable(SignatureAttributes signatureAttribute) {
        String tableName = signatureAttribute.getTableName();
        List<SignatureAttributes> attributes = signatureAttributesForTable.get(tableName);
        if (attributes == null) {
            attributes = new ArrayList<SignatureAttributes>();
            signatureAttributesForTable.put(tableName, attributes);
        }
        attributes.add(signatureAttribute);
        Collections.sort(attributes);
    }

    public Set<String> getTables() {
        return signatureAttributesForTable.keySet();
    }

    public Collection<SignatureMap> getSignatureMaps() {
        return signatures.values();
    }

    public Map<String, List<SignatureAttributes>> getSignatureAttributesForTable() {
        return signatureAttributesForTable;
    }

    public List<TupleWithTable> getTuplesWithoutGroundValues() {
        return tuplesWithoutGroundValues;
    }

    public void addTupleWithoutGroundValues(TupleWithTable tuple) {
        this.tuplesWithoutGroundValues.add(tuple);
    }

    @SuppressWarnings("unchecked")
    public List<SignatureAttributes> getRankedAttributesForTable(String table) {
        List<SignatureAttributes> result = signatureAttributesForTable.get(table);
        if (result == null) {
            return Collections.EMPTY_LIST;
        }
        return result;
    }

    @Override
    public String toString() {
        return SpeedyUtility.printMap(signatures);
    }

}
