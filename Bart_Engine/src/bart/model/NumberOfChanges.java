package bart.model;

public class NumberOfChanges {

    private int changes;

    public int getChanges() {
        return changes;
    }

    public void setChanges(int changes) {
        this.changes = changes;
    }

    public void addChange() {
        changes++;
    }

}
