package bart.model.dependency;

import java.util.List;
import java.util.Set;

public class DependencyStratification {

    private List<Set<Dependency>> strata;

    public DependencyStratification(List<Set<Dependency>> strata) {
        this.strata = strata;
    }

    public List<Set<Dependency>> getStrata() {
        return strata;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Number of stratum: ").append(strata.size()).append("\n");
        int count = 1;
        for (Set<Dependency> stratum : strata) {
            sb.append("Stratum ").append(count++).append(":\n");
            for (Dependency dependency : stratum) {
                sb.append("\t").append(dependency.toString()).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
