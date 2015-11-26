package bart.model.errorgenerator.operator.deltadb;

import speedy.model.database.IDatabase;
import bart.model.dependency.Dependency;

public interface IBuildDatabaseForChaseStep {

    IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB, Dependency dependency);
    IDatabase extractDatabase(String stepId, IDatabase deltaDB, IDatabase originalDB);
    IDatabase extractDatabaseWithDistinct(String stepId, IDatabase deltaDB, IDatabase originalDB);

}
