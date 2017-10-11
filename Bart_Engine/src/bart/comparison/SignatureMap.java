package bart.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import speedy.model.database.Tuple;

public class SignatureMap {

    private SignatureAttributes signatureAttribute;
    private Map<String, List<Tuple>> index = new HashMap<String, List<Tuple>>();

    public SignatureMap(SignatureAttributes signatureAttribute) {
        this.signatureAttribute = signatureAttribute;
    }

    public SignatureAttributes getSignatureAttribute() {
        return signatureAttribute;
    }

    public Map<String, List<Tuple>> getIndex() {
        return index;
    }

    public List<Tuple> getTuplesForSignature(String signature) {
        return index.get(signature);
    }

    public void addSignature(TupleSignature signature) {
        Map<String, List<Tuple>> buckets = this.getIndex();
        List<Tuple> bucket = buckets.get(signature.getSignature());
        if (bucket == null) {
            bucket = new ArrayList<Tuple>();
            buckets.put(signature.getSignature(), bucket);
        }
        bucket.add(signature.getTuple());
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("\n------------------------------------").append("\n");
        result.append(" Table: ").append(signatureAttribute.getTableName());
        result.append(" Attributes: ").append(signatureAttribute.getAttributes().isEmpty() ? "EMPTY ATTRIBUTES" : signatureAttribute.getAttributes()).append("\n");
        result.append("------------------------------------").append("\n");
        for (String key : index.keySet()) {
            result.append(" ").append((key.isEmpty() ? "[empty]" : key)).append("\n");
            for (Tuple tupleNodeBenchmark : index.get(key)) {
                result.append("     ").append(tupleNodeBenchmark.toStringNoOID()).append("\n");
            }
        }
        result.append("------------------------------------").append("\n");
        return result.toString();
    }
}
