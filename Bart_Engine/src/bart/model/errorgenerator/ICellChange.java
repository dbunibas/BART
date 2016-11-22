package bart.model.errorgenerator;

import speedy.model.database.Cell;
import speedy.model.database.IValue;
import java.util.Set;

public interface ICellChange {
    
    Cell getCell();
    IValue getNewValue();
    Set<String> getViolatedDependencies();
    void addViolatedDependency(String dependencyId);
    String toLongString();
    String toShortString();
    String getType();
    void addBlackListValue(ValueConstraint value);
    void addWhiteListValue(ValueConstraint value);
    Set<ValueConstraint> getBlackList();
    Set<ValueConstraint> getWhiteList();
    ValueConstraint getWhiteListIntersection();
    void setBlackList(Set<ValueConstraint> blackList);
    void setNewValue(IValue newValue);
    void setWhiteListIntersection(ValueConstraint whiteListIntersection);

}
