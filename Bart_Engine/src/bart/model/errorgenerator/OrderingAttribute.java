package bart.model.errorgenerator;

import speedy.model.database.AttributeRef;

public class OrderingAttribute {

    public static final String ASC = "ASC";
    public static final String DESC = "DESC";

    private String table;
    private String attribute;
    private String ordering;

    public OrderingAttribute(String attribute, String table) {
        this.attribute = attribute;
        this.ordering = ASC;
        this.table = table;
    }

    public OrderingAttribute(String attribute, String table, String ordering) {
        this.attribute = attribute;
        this.ordering = ordering;
        this.table = table;
        if (!ordering.equalsIgnoreCase(ASC) && !ordering.equalsIgnoreCase(DESC)) {
            throw new IllegalArgumentException("Ordering attributes can be only \"ASC\" or \"DESC\"");
        }
    }

    public String getAttribute() {
        return attribute;
    }

    public String getOrdering() {
        return ordering;
    }

    public String getTable() {
        return table;
    }
    
    public boolean isAsc() {
        return this.ordering.equalsIgnoreCase(ASC);
    }
    
    public boolean match(AttributeRef attributeRef) {
        return this.attribute.equals(attributeRef.getName()) && this.table.equals(attributeRef.getTableName());
    }
    
    public AttributeRef getAttributeRef() {
        return new AttributeRef(table, attribute);
    }

    @Override
    public String toString() {
        return "OrderingAttribute[" + "table=" + table + ", attribute=" + attribute + ", ordering=" + ordering + ']';
    }

}
