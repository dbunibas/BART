package bart.comparison.generator;

import bart.comparison.TupleMapping;
import speedy.model.database.IDatabase;

public class InstancePair {

    private IDatabase rightDB;
    private IDatabase leftDB;
    private TupleMapping tupleMapping;

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

    public TupleMapping getTupleMapping() {
        return tupleMapping;
    }

    public void setTupleMapping(TupleMapping tupleMapping) {
        this.tupleMapping = tupleMapping;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------\n");
        sb.append("Left DB\n").append(leftDB.toString());
        sb.append("---------------------------\n");
        sb.append("Right DB\n").append(rightDB.toString());
        sb.append("---------------------------\n");
        sb.append("Tuple Mappings\n").append(tupleMapping.toString());
        sb.append("---------------------------\n");
        return sb.toString();
    }

}
