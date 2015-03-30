package bart.model.dependency.analysis;

import bart.model.dependency.ComparisonAtom;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.RelationalAtom;
import bart.utility.BartUtility;
import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.experimental.equivalence.EquivalenceComparator;
import org.jgrapht.graph.DefaultEdge;

public class VertexEquivalenceComparator implements EquivalenceComparator<FormulaGraphVertex, Graph<FormulaGraphVertex, DefaultEdge>> {

    private static final String V = "V";
    private List<VariablePair> variablePairs;
    private List<FormulaVariable> anonymousVariables;

    public VertexEquivalenceComparator(List<VariablePair> variablePairs, List<FormulaVariable> anonymousVariables) {
        this.variablePairs = variablePairs;
        this.anonymousVariables = anonymousVariables;
    }

    private String vertexToString(FormulaGraphVertex vertex) {
        if (vertex.isAtom() && vertex.getAtom().isRelational()) {
            return ((RelationalAtom) vertex.getAtom()).getTableName();
        }
        if (vertex.isAtom() && vertex.getAtom().isComparison()) {
            return ((ComparisonAtom) vertex.getAtom()).getOperator();
        }
        if (vertex.isConstant()) {
            return vertex.getConstant();
        }
        assert (vertex.isVariable()) : "Incorrect vertex in graph";
        FormulaVariable variable = vertex.getVariable();
        if (anonymousVariables.contains(variable)) {
            return V;
        }
        for (VariablePair variablePair : variablePairs) {
            if (variablePair.getLeftVariable().equals(variable)
                    || variablePair.getRightVariable().equals(variable)) {
                return variablePair.getLeftVariable().toString();
            }
        }
        throw new IllegalArgumentException("Unable to find vertex " + vertex + ".Variable pairs: \n" + BartUtility.printCollection(variablePairs));
    }

    public boolean equivalenceCompare(FormulaGraphVertex v1, FormulaGraphVertex v2, Graph<FormulaGraphVertex, DefaultEdge> g1, Graph<FormulaGraphVertex, DefaultEdge> g2) {
        String s1 = vertexToString(v1);
        String s2 = vertexToString(v2);
        return s1.equals(s2);
    }

    public int equivalenceHashcode(FormulaGraphVertex v, Graph<FormulaGraphVertex, DefaultEdge> g) {
        return vertexToString(v).hashCode();
    }

}
