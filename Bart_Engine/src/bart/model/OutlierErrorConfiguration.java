package bart.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class OutlierErrorConfiguration {

    private Map<String, Set<AttributeToDirty>> mapAttributesForTables = new HashMap<String, Set<AttributeToDirty>>(); // key: tableName

    public void addAttributes(String tableName, String attributeName, double percentage, boolean detectable) {
        AttributeToDirty attribute = new AttributeToDirty(attributeName, percentage, detectable);
        Set<AttributeToDirty> attributes = mapAttributesForTables.get(tableName);
        if (attributes == null) attributes = new HashSet<AttributeToDirty>();
        attributes.add(attribute);
        mapAttributesForTables.put(tableName, attributes);
    }

    public Set<String> getAttributesToDirty(String tableName) {
        Set<AttributeToDirty> attributes = mapAttributesForTables.get(tableName);
        Set<String> attributesName = new HashSet<String>();
        if (attributes != null && !attributes.isEmpty()) {
            Iterator<AttributeToDirty> iterator = attributes.iterator();
            while (iterator.hasNext()) {
                AttributeToDirty attribute = iterator.next();
                attributesName.add(attribute.getName());
            }
        }
        return attributesName;
    }

    public double getPercentageToDirty(String tableName, String attributeName) {
        Set<AttributeToDirty> attributes = mapAttributesForTables.get(tableName);
        if (attributes != null && !attributes.isEmpty()) {
            Iterator<AttributeToDirty> iterator = attributes.iterator();
            while (iterator.hasNext()) {
                AttributeToDirty attribute = iterator.next();
                if (attribute.getName().equals(attributeName)) {
                    return attribute.getPercentage();
                }
            }
        }
        return 0.0;
    }

    public boolean isDetectable(String tableName, String attributeName) {
        Set<AttributeToDirty> attributes = mapAttributesForTables.get(tableName);
        if (attributes != null && !attributes.isEmpty()) {
            Iterator<AttributeToDirty> iterator = attributes.iterator();
            while (iterator.hasNext()) {
                AttributeToDirty attribute = iterator.next();
                if (attribute.getName().equals(attributeName)) {
                    return attribute.isDetectable();
                }
            }
        }
        return true;
    }

    public Set<String> getTablesToDirty() {
        return mapAttributesForTables.keySet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Set<String> tableNames = mapAttributesForTables.keySet();
        for (String tableName : tableNames) {
            sb.append("\tTable: ").append(tableName).append("\n");
            Set<AttributeToDirty> attributes = mapAttributesForTables.get(tableName);
            for (AttributeToDirty attribute : attributes) {
                sb.append("\t\t\t").append(attribute).append("\n");
            }
        }
        return sb.toString();
    }
}

class AttributeToDirty {

    private String name;
    private double percentage;
    private boolean detectable;

    public AttributeToDirty(String name, double percentage, boolean detectable) {
        this.name = name;
        this.percentage = percentage;
        this.detectable = detectable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public boolean isDetectable() {
        return detectable;
    }

    public void setDetectable(boolean detectable) {
        this.detectable = detectable;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.percentage) ^ (Double.doubleToLongBits(this.percentage) >>> 32));
        hash = 47 * hash + (this.detectable ? 1 : 0);
        return hash;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final AttributeToDirty other = (AttributeToDirty) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) return false;
        if (this.percentage != other.percentage) return false;
        if (this.detectable != other.detectable) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Name=" + name + " Detectable=" + detectable + " Percentage=" + percentage;
    }
}
