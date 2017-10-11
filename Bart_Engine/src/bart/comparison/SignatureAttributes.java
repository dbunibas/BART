package bart.comparison;

import java.util.List;
import speedy.model.database.AttributeRef;

public class SignatureAttributes implements Comparable<SignatureAttributes> {

    private String tableName;
    private List<AttributeRef> attributes;

    public SignatureAttributes(String tableName, List<AttributeRef> groundAttributes) {
        this.tableName = tableName;
        this.attributes = groundAttributes;
    }

    public String getTableName() {
        return tableName;
    }

    public List<AttributeRef> getAttributes() {
        return attributes;
    }

    @Override
    public int hashCode() {
        return this.toHashString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final SignatureAttributes other = (SignatureAttributes) obj;
        return this.toHashString().equals(other.toHashString());
    }

    private String toHashString() {
        StringBuilder result = new StringBuilder();
        result.append(tableName).append(".");
        for (AttributeRef attribute : attributes) {
            result.append(attribute.getName()).append("|");
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return tableName + "." + attributes;
    }

    public int compareTo(SignatureAttributes o) {
        return o.getAttributes().size() - this.getAttributes().size();
    }

}
