package bart.comparison.generator;

import speedy.model.database.IDatabase;

public class InstancePair {

    private IDatabase rightDB;
    private IDatabase leftDB;

    public InstancePair(IDatabase rightDB, IDatabase leftDB) {
        this.rightDB = rightDB;
        this.leftDB = leftDB;
    }

    public IDatabase getRightDB() {
        return rightDB;
    }

    public IDatabase getLeftDB() {
        return leftDB;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------\n");
        sb.append("Left DB\n").append(leftDB.toString());
        sb.append("---------------------------\n");
        sb.append("Right DB\n").append(rightDB.toString());
        sb.append("---------------------------\n");
        return sb.toString();
    }

}
