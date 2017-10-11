package bart.comparison.operators;

import bart.comparison.InstanceMatchTask;
import speedy.model.database.IDatabase;

public interface IComputeInstanceSimilarity {

    public InstanceMatchTask compare(IDatabase leftInstance, IDatabase rightInstance);
}
