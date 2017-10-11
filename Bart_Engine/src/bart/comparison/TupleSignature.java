package bart.comparison;

import speedy.model.database.Tuple;

public class TupleSignature {

    private Tuple tuple;
    private SignatureAttributes signatureAttribute;
    private String signature;

    public TupleSignature(Tuple tuple, SignatureAttributes signatureAttribute, String signature) {
        this.tuple = tuple;
        this.signatureAttribute = signatureAttribute;
        this.signature = signature;
    }

    public Tuple getTuple() {
        return tuple;
    }

    public SignatureAttributes getSignatureAttribute() {
        return signatureAttribute;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "Tuple " + tuple + " " + signatureAttribute + ":" + signature;
    }

}
