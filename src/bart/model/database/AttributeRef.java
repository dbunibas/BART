package bart.model.database;

import java.io.Serializable;

public class AttributeRef implements Serializable, Cloneable {

    private TableAlias tableAlias;
    private String name;

    public AttributeRef(TableAlias tableAlias, String name) {
        this.tableAlias = tableAlias;
        this.name = name;
    }

    public AttributeRef(String tableName, String name) {
        this.tableAlias = new TableAlias(tableName, "");
        this.name = name;
    }

    public AttributeRef(AttributeRef originalRef, TableAlias newAlias) {
        this.tableAlias = newAlias;
        this.name = originalRef.name;
    }

    public TableAlias getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias(TableAlias tableAlias) {
        this.tableAlias = tableAlias;
    }

    public boolean isAliased() {
        return this.tableAlias.isAliased();
    }

    public boolean isSource() {
        return this.tableAlias.isSource();
    }

    public boolean isTarget() {
        return !isSource();
    }

    public boolean isAuthoritative() {
        return this.tableAlias.isAuthoritative();
    }

    public String getName() {
        return name;
    }

    public String getTableName() {
        return this.tableAlias.getTableName();
    }

    public boolean equalsModuloAlias(AttributeRef other) {
        return this.name.equals(other.name) && this.tableAlias.getTableName().equals(other.tableAlias.getTableName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final AttributeRef other = (AttributeRef) obj;
        if (this.tableAlias != other.tableAlias && (this.tableAlias == null || !this.tableAlias.equals(other.tableAlias))) return false;
        if ((this.name == null) ? (other.name != null) : !this.name.equalsIgnoreCase(other.name)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.tableAlias != null ? this.tableAlias.hashCode() : 0);
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public AttributeRef clone() {
        try {
            AttributeRef c = (AttributeRef) super.clone();
            c.tableAlias = this.tableAlias.clone();
            return c;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException(ex.getLocalizedMessage());
        }
    }

    @Override
    public String toString() {
        return (tableAlias.toStringWithoutSource().isEmpty() ? "" : tableAlias.toStringWithoutSource() + ".") + name;
    }

    public String toStringNoAlias() {
        return tableAlias.getTableName() + "." + name;
    }
}
