package bart.comparison;

import speedy.model.database.IDatabase;

public class InstanceMatchTask {

    private final String strategy;
    private final IDatabase sourceDb;
    private final IDatabase targetDb;
    private TupleMapping tupleMapping = new TupleMapping();
    private Boolean isomorphism;

    public InstanceMatchTask(String strategy, IDatabase sourceDb, IDatabase targetDb) {
        this.strategy = strategy;
        this.sourceDb = sourceDb;
        this.targetDb = targetDb;
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

    @Override
    public String toString() {
        return strategy + " - " + (isomorphism != null && isomorphism ? "(isomorphism) \n" : "")
                + tupleMapping + "\n";
    }

}
