package bart.comparison;

import speedy.model.database.IDatabase;
import speedy.model.database.operators.dbms.IValueEncoder;

public class InstanceMatchTask {

    private final String strategy;
    private final IDatabase sourceDb;
    private final IDatabase targetDb;
    private TupleMapping tupleMapping = new TupleMapping();
    private Boolean isomorphism;
    private IValueEncoder encoder;

    public InstanceMatchTask(String strategy, IDatabase sourceDb, IDatabase targetDb, IValueEncoder encoder) {
        this.strategy = strategy;
        this.sourceDb = sourceDb;
        this.targetDb = targetDb;
        this.encoder = encoder;
    }

    public String getStrategy() {
        return strategy;
    }

    public IDatabase getSourceDb() {
        return sourceDb;
    }

    public IDatabase getTargetDb() {
        return targetDb;
    }

    public boolean hasHomomorphism() {
        return !this.tupleMapping.isEmpty();
    }

    public TupleMapping getTupleMapping() {
        return tupleMapping;
    }

    public void setTupleMapping(TupleMapping tupleMapping) {
        this.tupleMapping = tupleMapping;
    }

    public Boolean isIsomorphism() {
        return isomorphism;
    }

    public void setIsomorphism(boolean isomorphism) {
        this.isomorphism = isomorphism;
    }

    public IValueEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(IValueEncoder encoder) {
        this.encoder = encoder;
    }
    
    @Override
    public String toString() {
        return strategy + " - " + (isomorphism != null && isomorphism ? "(isomorphism) \n" : "")
                + tupleMapping + "\n";
    }

}
