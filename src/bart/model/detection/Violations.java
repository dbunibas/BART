package bart.model.detection;

import bart.model.dependency.Dependency;
import bart.model.errorgenerator.ViolationContext;
import bart.utility.BartUtility;
import bart.utility.comparator.DependencyComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Violations {

    private Map<Dependency, Set<ViolationContext>> violations = new HashMap<Dependency, Set<ViolationContext>>();

    public Map<Dependency, Set<ViolationContext>> getViolations() {
        return violations;
    }

    public void addViolation(Dependency dependency, ViolationContext violation) {
        Set<ViolationContext> violationsForDependency = violations.get(dependency);
        if (violationsForDependency == null) {
            violationsForDependency = new HashSet<ViolationContext>();
            violations.put(dependency, violationsForDependency);
        }
        violationsForDependency.add(violation);
    }
    
    public int getTotalViolations(){
        int sum = 0;
        for (Dependency keySet : violations.keySet()) {
            sum += violations.get(keySet).size();
        }
        return sum;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Violations for dependencies (number of violation contexts):\n");
        List<Dependency> sortedDependencies = new ArrayList<Dependency>(violations.keySet());
        Collections.sort(sortedDependencies, new DependencyComparator());
        for (Dependency dependency : sortedDependencies) {
            sb.append("\t").append(dependency.getId()).append(":\t").append(violations.get(dependency).size()).append("\n");
        }
        return sb.toString();
    }

    public String toLongString() {
        return "All Violations " + BartUtility.printMap(violations);
    }

}
