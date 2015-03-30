package bart.model.algebra.operators.sql;

public class SQLQuery {

    private StringBuilder sb = new StringBuilder();
    private boolean distinct;

    public SQLQuery() {
    }

    public SQLQuery(Object initialString) {
        sb.append(initialString);
    }

    public StringBuilder append(Object s) {
        return sb.append(s);
    }

    public StringBuilder getStringBuilder() {
        return sb;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
