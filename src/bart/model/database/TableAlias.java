package bart.model.database;

import java.io.Serializable;

public class TableAlias implements Cloneable, Serializable {

    private String tableName;
    private boolean source;
    private boolean authoritative;
    private String alias = "";

    public TableAlias(String tableName) {
        this.tableName = tableName;
    }

    public TableAlias(String tableName, String alias) {
        this.tableName = tableName;
        this.alias = alias;
    }

    public TableAlias(String tableName, boolean source) {
        this.tableName = tableName;
        this.source = source;
    }

    public boolean isSource() {
        return source;
    }

    public void setSource(boolean source) {
        this.source = source;
    }

    public void addAlias(String alias) {
        this.alias = this.alias + alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public boolean isAliased() {
        return !alias.equals("");
    }

    public boolean isAuthoritative() {
        return authoritative;
    }

    public void setAuthoritative(boolean authoritative) {
        this.authoritative = authoritative;
    }

    public String getTableName() {
        return tableName;
    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) return false;
//        if (getClass() != obj.getClass()) return false;
//        final TableAlias other = (TableAlias) obj;
//        if ((this.tableName == null) ? (other.tableName != null) : !this.tableName.equals(other.tableName)) return false;
//        if (this.source != other.source) return false;
//        if ((this.alias == null) ? (other.alias != null) : !this.alias.equals(other.alias)) return false;
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 3;
//        hash = 41 * hash + (this.tableName != null ? this.tableName.hashCode() : 0);
//        hash = 41 * hash + (this.source ? 1 : 0);
//        hash = 41 * hash + (this.alias != null ? this.alias.hashCode() : 0);
//        return hash;
//    }

    @Override
    public boolean equals(Object obj) {
        return this.toStringWithoutSource().equalsIgnoreCase(((TableAlias) obj).toStringWithoutSource());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return (source ? "Source." : "") + tableName + (alias.equals("") ? "" : "_" + alias);
    }

    public String toStringWithoutSource() {
        return tableName + (alias.equals("") ? "" : "_" + alias);
    }

    @Override
    public TableAlias clone() {
        TableAlias clone = null;
        try {
            clone = (TableAlias) super.clone();
        } catch (CloneNotSupportedException ex) {
        }
        return clone;
    }
}
