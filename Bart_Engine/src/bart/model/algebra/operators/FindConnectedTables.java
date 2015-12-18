package bart.model.algebra.operators;

import bart.model.dependency.RelationalAtom;
import bart.model.dependency.LabeledEdge;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.TableAlias;

public class FindConnectedTables {

    private static final Logger logger = LoggerFactory.getLogger(FindConnectedTables.class.getName());

    List<ConnectedTables> findConnectedEqualityGroups(List<RelationalAtom> atoms, List<EqualityGroup> equalityGroups) {
        List<ConnectedTables> result = new ArrayList<ConnectedTables>();
        UndirectedGraph<TableAlias, LabeledEdge> joinGraph = initJoinGraph(atoms, equalityGroups);
        ConnectivityInspector<TableAlias, LabeledEdge> inspector = new ConnectivityInspector<TableAlias, LabeledEdge>(joinGraph);
        List<Set<TableAlias>> connectedVertices = inspector.connectedSets();
        for (Set<TableAlias> connectedComponent : connectedVertices) {
            ConnectedTables connectedTables = new ConnectedTables(connectedComponent);
            result.add(connectedTables);
        }
        return result;
    }

    private UndirectedGraph<TableAlias, LabeledEdge> initJoinGraph(List<RelationalAtom> atoms, List<EqualityGroup> equalityGroups) {
        UndirectedGraph<TableAlias, LabeledEdge> joinGraph = new SimpleGraph<TableAlias, LabeledEdge>(LabeledEdge.class);
        if (logger.isDebugEnabled()) logger.debug("Build join graph for equality groups " + equalityGroups);
        Set<TableAlias> tableAliases = extracTableAliases(atoms);
        for (TableAlias tableAlias : tableAliases) {
            joinGraph.addVertex(tableAlias);
        }
        for (EqualityGroup equalityGroup : equalityGroups) {
            TableAlias leftTable = equalityGroup.getLeftTable();
            TableAlias rightTable = equalityGroup.getRightTable();
            if(leftTable.equals(rightTable)){
                continue;
            }
            joinGraph.addEdge(leftTable, rightTable, new LabeledEdge(leftTable.toString(), rightTable.toString(), equalityGroup.toString()));
        }
        return joinGraph;
    }

    private Set<TableAlias> extracTableAliases(List<RelationalAtom> atoms) {
        Set<TableAlias> result = new HashSet<TableAlias>();
        for (RelationalAtom atom : atoms) {
            result.add(atom.getTableAlias());
        }
        return result;
    }

}
