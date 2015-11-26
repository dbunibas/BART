package bart.model.detection.operator;

import bart.model.EGTask;
import speedy.model.database.IDatabase;
import bart.model.dependency.Dependency;
import bart.model.detection.Violations;

public interface IDetectViolations {

    public void detect(Dependency dependency, Violations violations, IDatabase source, IDatabase target, EGTask task);

    public boolean check(Dependency dependency, IDatabase source, IDatabase target, EGTask task);

}
