package bart.model.dependency.operators;

import bart.model.EGTask;
import bart.model.VioGenQueryConfiguration;
import bart.model.dependency.ComparisonAtom;
import bart.model.dependency.Dependency;
import bart.model.dependency.DependencyStratification;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.IFormulaAtom;
import bart.model.errorgenerator.VioGenQuery;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.AttributeRef;
import speedy.utility.SpeedyUtility;

public class GenerateStratification {

    private final static Logger logger = LoggerFactory.getLogger(GenerateStratification.class);

    public DependencyStratification generate(List<Dependency> dependencies, EGTask task) {
        Map<Dependency, Set<AttributeRef>> affectedAttributes = findAllAffectedAttributes(dependencies, task);
        if (logger.isDebugEnabled()) logger.debug("Affected attributes: " + SpeedyUtility.printMap(affectedAttributes));
        Map<Dependency, Set<AttributeRef>> queriedAttributes = findAllQueriedAttributes(dependencies, task);
        if (logger.isDebugEnabled()) logger.debug("Queried attributes: " + SpeedyUtility.printMap(queriedAttributes));
        DirectedGraph<Dependency, DefaultEdge> dependencyGraph = initDependencyGraph(dependencies, affectedAttributes, queriedAttributes);
        if (logger.isDebugEnabled()) logger.debug("Dependency graph: \n" + dependencyGraph);
        StrongConnectivityInspector<Dependency, DefaultEdge> connectivityInstector = new StrongConnectivityInspector<Dependency, DefaultEdge>(dependencyGraph);
        List<Set<Dependency>> strata = connectivityInstector.stronglyConnectedSets();
        Collections.sort(strata, new StratumComparator(dependencyGraph));
        return new DependencyStratification(strata);
    }

    private DirectedGraph<Dependency, DefaultEdge> initDependencyGraph(List<Dependency> dependencies, Map<Dependency, Set<AttributeRef>> affectedAttributes, Map<Dependency, Set<AttributeRef>> queriedAttributes) {
        DirectedGraph<Dependency, DefaultEdge> dependencyGraph = new DefaultDirectedGraph<Dependency, DefaultEdge>(DefaultEdge.class);
        for (Dependency dependency : dependencies) {
            dependencyGraph.addVertex(dependency);
        }
        for (int i = 0; i < dependencies.size(); i++) {
            Dependency d1 = dependencies.get(i);
            for (int j = 0; j < dependencies.size(); j++) {
                if (i == j) {
                    continue;
                }
                Dependency d2 = dependencies.get(j);
                if (haveOverlap(d1, d2, affectedAttributes, queriedAttributes)) {
                    if (logger.isDebugEnabled()) logger.debug("Edge btw " + d1.getId() + " and " + d2.getId());
                    dependencyGraph.addEdge(d1, d2);
                }
            }
        }
        return dependencyGraph;
    }

    private boolean haveOverlap(Dependency d1, Dependency d2, Map<Dependency, Set<AttributeRef>> affectedAttributes, Map<Dependency, Set<AttributeRef>> queriedAttributes) {
        Set<AttributeRef> attributes1 = affectedAttributes.get(d1);
        Set<AttributeRef> attributes2 = queriedAttributes.get(d2);
        return !Collections.disjoint(attributes1, attributes2);
    }

    private Map<Dependency, Set<AttributeRef>> findAllAffectedAttributes(List<Dependency> dependencies, EGTask task) {
        Map<Dependency, Set<AttributeRef>> result = new HashMap<Dependency, Set<AttributeRef>>();
        for (Dependency dependency : dependencies) {
            result.put(dependency, findAffectedAttributes(dependency, task));
        }
        return result;
    }

    private Set<AttributeRef> findAffectedAttributes(Dependency dependency, EGTask task) {
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        for (VioGenQuery vioGenQuery : dependency.getVioGenQueries()) {
            VioGenQueryConfiguration configuration = vioGenQuery.getConfiguration();
            double percentage = configuration.getPercentage();
            if (logger.isDebugEnabled()) logger.debug("VioQuery " + vioGenQuery + " # Percentage: " + percentage);
            if (percentage == 0) {
                continue;
            }
            ComparisonAtom vioGenComparison = vioGenQuery.getVioGenComparison();
            List<FormulaVariable> variables = vioGenComparison.getVariables();
            for (FormulaVariable variable : variables) {
                for (AttributeRef attributeRef : variable.getAttributeRefs()) {
                    if (attributeRef.isSource()) {
                        continue;
                    }
                    result.add(SpeedyUtility.unAlias(attributeRef));
                }
            }
        }
        return result;
    }

    private Map<Dependency, Set<AttributeRef>> findAllQueriedAttributes(List<Dependency> dependencies, EGTask task) {
        Map<Dependency, Set<AttributeRef>> result = new HashMap<Dependency, Set<AttributeRef>>();
        for (Dependency dependency : dependencies) {
            result.put(dependency, findQueriedAttributes(dependency, task));
        }
        return result;
    }

    private Set<AttributeRef> findQueriedAttributes(Dependency dependency, EGTask task) {
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        for (int i = 0; i < dependency.getPremise().getAtoms().size(); i++) {
            IFormulaAtom atom = dependency.getPremise().getAtoms().get(i);
            if (!(atom instanceof ComparisonAtom)) {
                continue;
            }
            ComparisonAtom comparison = (ComparisonAtom) dependency.getPremise().getAtoms().get(i);
            List<FormulaVariable> variables = comparison.getVariables();
            for (FormulaVariable variable : variables) {
                for (AttributeRef attributeRef : variable.getAttributeRefs()) {
                    if (attributeRef.isSource()) {
                        continue;
                    }
                    result.add(SpeedyUtility.unAlias(attributeRef));
                }
            }
        }
        return result;
    }

    private static class StratumComparator implements Comparator<Set<Dependency>>{

        private DirectedGraph<Dependency, DefaultEdge> dependencyGraph;

        public StratumComparator(DirectedGraph<Dependency, DefaultEdge> dependencyGraph) {
            this.dependencyGraph = dependencyGraph;
        }

        public int compare(Set<Dependency> s1, Set<Dependency> s2) {
            if (existsPath(s1, s2)) {
                return -1;
            } else if (existsPath(s2, s1)) {
                return 1;
            }
            return 0;
        }

        private boolean existsPath(Set<Dependency> s1, Set<Dependency> s2) {
            for (Dependency dependency1 : s1) {
                for (Dependency dependency2 : s2) {
                    List<DefaultEdge> path = DijkstraShortestPath.findPathBetween(dependencyGraph, dependency1, dependency2);
                    if (path != null) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

}
