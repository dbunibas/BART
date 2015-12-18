package bart.model.dependency.analysis;

import bart.model.EGTask;
import bart.utility.AlgebraUtility;
import bart.model.dependency.ComparisonAtom;
import bart.model.dependency.FormulaAttribute;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaVariableOccurrence;
import bart.model.dependency.FormulaWithAdornments;
import bart.model.dependency.IFormula;
import bart.model.dependency.IFormulaAtom;
import bart.model.dependency.RelationalAtom;
import bart.model.dependency.VariableEquivalenceClass;
import speedy.model.expressions.Expression;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.experimental.equivalence.UniformEquivalenceComparator;
import org.jgrapht.experimental.isomorphism.AdaptiveIsomorphismInspectorFactory;
import org.jgrapht.experimental.isomorphism.GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UndirectedSubgraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindFormulaWithAdornments {

    private static Logger logger = LoggerFactory.getLogger(FindFormulaWithAdornments.class);
    private static GenerateFormulaWithAdornments formulaWithAdornmentsGenerator = new GenerateFormulaWithAdornments();

    public void findFormulaWithAdornments(IFormula formula, EGTask task) {
        if (!task.getConfiguration().isUseSymmetricOptimization()) {
            return;
        }
        if (DependencyUtility.hasNumericComparison(formula) || formula.hasNegations()) {
            return;
        }
        if (DependencyUtility.hasBuiltIns(formula)) {
            throw new UnsupportedOperationException("Unable to check symmetry with built-ins");
        }
        if (logger.isDebugEnabled()) logger.debug("Finding symmetric atoms in formula \n\t" + formula);
        if (logger.isDebugEnabled()) logger.debug("EquivalenceClasses: \n\t" + formula.getLocalVariableEquivalenceClasses());
        UndirectedGraph<FormulaGraphVertex, DefaultEdge> formulaGraph = initJoinGraph(formula);
        if (logger.isDebugEnabled()) logger.debug("Formula graph: \n\t" + formulaGraph.toString());
        List<VariablePair> variablePairs = findVariablePairs(formulaGraph);
        if (logger.isDebugEnabled()) logger.debug("Variable pairs:\n" + BartUtility.printCollection(variablePairs, "\t"));
        if (variablePairs.isEmpty()) {
            return;
        }
        List<FormulaVariable> anonymousVariables = findAnonymousVariables(formula, variablePairs);
        FormulaWithAdornments formulaWithAdornments = findFormulaWithAdornments(formulaGraph, variablePairs, anonymousVariables, formula);
        if (logger.isDebugEnabled()) logger.debug("Formula with adornments: \n\t" + formulaWithAdornments);
        if (formulaWithAdornments == null) {
            return;
        }
        formula.setFormulaWithAdornments(formulaWithAdornments);
    }

    private UndirectedGraph<FormulaGraphVertex, DefaultEdge> initJoinGraph(IFormula formula) {
        Map<FormulaVariable, FormulaGraphVertex> vertexMap = new HashMap<FormulaVariable, FormulaGraphVertex>();
        UndirectedGraph<FormulaGraphVertex, DefaultEdge> graph = new SimpleGraph<FormulaGraphVertex, DefaultEdge>(DefaultEdge.class);
        for (FormulaVariable formulaVariable : formula.getLocalVariables()) {
            FormulaGraphVertex vertex = new FormulaGraphVertex(formulaVariable);
            graph.addVertex(vertex);
            vertexMap.put(formulaVariable, vertex);
        }
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (atom.isRelational()) {
                addVerticesForRelationalAtom(atom, graph, vertexMap);
            }
            if (atom.isComparison()) {
                addVerticesForComparisonAtom(atom, graph, vertexMap);
            }
        }
        return graph;
    }

    private void addVerticesForRelationalAtom(IFormulaAtom atom, UndirectedGraph<FormulaGraphVertex, DefaultEdge> graph, Map<FormulaVariable, FormulaGraphVertex> vertexMap) {
        RelationalAtom relationalAtom = (RelationalAtom) atom;
        FormulaGraphVertex atomVertex = new FormulaGraphVertex(relationalAtom);
        graph.addVertex(atomVertex);
        for (FormulaAttribute formulaAttribute : relationalAtom.getAttributes()) {
            if (formulaAttribute.getValue().isVariable()) {
                String variableId = ((FormulaVariableOccurrence) formulaAttribute.getValue()).getVariableId();
                FormulaVariable variable = AlgebraUtility.findVariable(variableId, new ArrayList<FormulaVariable>(vertexMap.keySet()));
                FormulaGraphVertex variableVertex = vertexMap.get(variable);
                assert (variableVertex != null) : "Unable to find vertex for variable " + variableId;
                graph.addEdge(atomVertex, variableVertex);
            } else if (formulaAttribute.getValue().isConstant() || formulaAttribute.getValue().isNull()) {
                createConstantVertex(formulaAttribute.getValue().toString(), atomVertex, graph);
            } else if (formulaAttribute.getValue().isExpression()) {
                throw new UnsupportedOperationException("Unable to check symmetry with expression");
            }
        }
    }

    private void addVerticesForComparisonAtom(IFormulaAtom atom, UndirectedGraph<FormulaGraphVertex, DefaultEdge> graph, Map<FormulaVariable, FormulaGraphVertex> vertexMap) {
        ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
        FormulaGraphVertex comparisonVertex = new FormulaGraphVertex(comparisonAtom);
        graph.addVertex(comparisonVertex);
        for (FormulaVariable variable : comparisonAtom.getVariables()) {
            FormulaGraphVertex variableVertex = vertexMap.get(variable);
            graph.addEdge(comparisonVertex, variableVertex);
        }
        if (comparisonAtom.getLeftConstant() != null) {
            createConstantVertex(comparisonAtom.getLeftConstant(), comparisonVertex, graph);
            addEdgesForEquivalenceClass(comparisonAtom.getLeftConstant(), comparisonAtom.getRightVariable(), comparisonAtom.getOperator(), atom.getFormula(), graph, vertexMap);
        }
        if (comparisonAtom.getRightConstant() != null) {
            createConstantVertex(comparisonAtom.getRightConstant(), comparisonVertex, graph);
            addEdgesForEquivalenceClass(comparisonAtom.getRightConstant(), comparisonAtom.getLeftVariable(), comparisonAtom.getOperator(), atom.getFormula(), graph, vertexMap);
        }
    }

    private void addEdgesForEquivalenceClass(String constant, FormulaVariable variable, String operator,
            IFormula formula, UndirectedGraph<FormulaGraphVertex, DefaultEdge> graph, Map<FormulaVariable, FormulaGraphVertex> vertexMap) {
        VariableEquivalenceClass equivalenceClass = DependencyUtility.findEquivalenceClassForVariable(variable, formula.getLocalVariableEquivalenceClasses());
        if (logger.isDebugEnabled()) logger.debug("Adding edges for equivalence class " + equivalenceClass);
        for (FormulaVariable otherVariable : equivalenceClass.getVariables()) {
            if (otherVariable.equals(variable)) {
                continue;
            }
            if (existComparison(otherVariable, constant, formula)) {
                continue;
            }
            Expression expression = new Expression(otherVariable.getId() + operator + constant);
            ComparisonAtom virtualComparisonAtom = new ComparisonAtom(formula, expression, otherVariable.getId(), null, null, constant, operator);
            virtualComparisonAtom.addVariable(otherVariable);
            FormulaGraphVertex virtualComparisonVertex = new FormulaGraphVertex(virtualComparisonAtom);
            virtualComparisonVertex.setVirtual(true);
            graph.addVertex(virtualComparisonVertex);
            FormulaGraphVertex variableVertex = vertexMap.get(otherVariable);
            graph.addEdge(virtualComparisonVertex, variableVertex);
            createConstantVertex(constant, virtualComparisonVertex, graph);
        }
    }

    private boolean existComparison(FormulaVariable variable, String constant, IFormula formula) {
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (!atom.isComparison()) {
                continue;
            }
            ComparisonAtom comparison = (ComparisonAtom) atom;
            if ((variable.equals(comparison.getLeftVariable()) && constant.equals(comparison.getRightConstant()))
                    || variable.equals(comparison.getRightVariable()) && constant.equals(comparison.getLeftConstant())) {
                return true;
            }
        }
        return false;
    }

    private void createConstantVertex(String value, FormulaGraphVertex vertexToConnect, UndirectedGraph<FormulaGraphVertex, DefaultEdge> graph) {
        FormulaGraphVertex constantVertex = new FormulaGraphVertex(value);
        graph.addVertex(constantVertex);
        graph.addEdge(vertexToConnect, constantVertex);
    }

    private List<VariablePair> findVariablePairs(UndirectedGraph<FormulaGraphVertex, DefaultEdge> formulaGraph) {
        List<VariablePair> result = new ArrayList<VariablePair>();
        for (FormulaGraphVertex vertex : formulaGraph.vertexSet()) {
            if (!vertex.isAtom() || (vertex.isAtom() && !(vertex.getAtom().isComparison()))) {
                continue;
            }
            ComparisonAtom comparisonAtom = (ComparisonAtom) vertex.getAtom();
            if (comparisonAtom.isNumericalComparison() || !comparisonAtom.isVariableComparison()) {
                continue;
            }
            FormulaVariable leftVariable = comparisonAtom.getLeftVariable();
            FormulaVariable rightVariable = comparisonAtom.getRightVariable();
            if (logger.isDebugEnabled()) logger.debug("Left variable: " + leftVariable);
            if (logger.isDebugEnabled()) logger.debug("Right variable: " + rightVariable);
            if (!haveOccurrencesInSameAttributes(leftVariable, rightVariable)) {
                continue;
            }
            VariablePair variablePair = new VariablePair(leftVariable, rightVariable, vertex);
            result.add(variablePair);
        }
        return result;
    }

    private boolean haveOccurrencesInSameAttributes(FormulaVariable leftVariable, FormulaVariable rightVariable) {
        List<FormulaVariableOccurrence> leftRelationalOccurrences = new ArrayList<FormulaVariableOccurrence>(leftVariable.getRelationalOccurrences());
        List<FormulaVariableOccurrence> rightRelationalOccurrences = new ArrayList<FormulaVariableOccurrence>(rightVariable.getRelationalOccurrences());
        if (logger.isDebugEnabled()) logger.trace("Left relational occurrences: " + leftRelationalOccurrences);
        if (logger.isDebugEnabled()) logger.trace("Right relational occurrences: " + rightRelationalOccurrences);
        for (FormulaVariableOccurrence leftOccurrence : leftRelationalOccurrences) {
            if (!searchAndRemoveSameAttribute(leftOccurrence, rightRelationalOccurrences)) {
                return false;
            }
        }
        if (rightRelationalOccurrences.isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean searchAndRemoveSameAttribute(FormulaVariableOccurrence occurrence, List<FormulaVariableOccurrence> relationalOccurrences) {
        for (Iterator<FormulaVariableOccurrence> it = relationalOccurrences.iterator(); it.hasNext();) {
            FormulaVariableOccurrence otherOccurrence = it.next();
            if (occurrence.getAttributeRef().toStringNoAlias().equals(otherOccurrence.getAttributeRef().toStringNoAlias())) {
                it.remove();
                return true;
            }
            if (logger.isDebugEnabled()) logger.trace("Left relational occurrence: " + occurrence.getAttributeRef().toStringNoAlias());
            if (logger.isDebugEnabled()) logger.trace("Right relational occurrence: " + otherOccurrence.getAttributeRef().toStringNoAlias());
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private FormulaWithAdornments findFormulaWithAdornments(UndirectedGraph<FormulaGraphVertex, DefaultEdge> formulaGraph,
            List<VariablePair> variablePairs, List<FormulaVariable> anonymousVariables, IFormula formula) {
        Set<FormulaGraphVertex> vertices = new HashSet<FormulaGraphVertex>(formulaGraph.vertexSet());
        for (VariablePair variablePair : variablePairs) {
            vertices.remove(variablePair.getVertex());
        }
        UndirectedSubgraph<FormulaGraphVertex, DefaultEdge> subgraph = new UndirectedSubgraph<FormulaGraphVertex, DefaultEdge>(formulaGraph, vertices, formulaGraph.edgeSet());
        ConnectivityInspector<FormulaGraphVertex, DefaultEdge> inspector = new ConnectivityInspector<FormulaGraphVertex, DefaultEdge>(subgraph);
        List<Set<FormulaGraphVertex>> connectedVertices = inspector.connectedSets();
        if (connectedVertices.size() != 2) {
            return null;
        }
        UndirectedSubgraph<FormulaGraphVertex, DefaultEdge> connectedSubgraphOne = new UndirectedSubgraph<FormulaGraphVertex, DefaultEdge>(formulaGraph, connectedVertices.get(0), formulaGraph.edgeSet());
        UndirectedSubgraph<FormulaGraphVertex, DefaultEdge> connectedSubgraphTwo = new UndirectedSubgraph<FormulaGraphVertex, DefaultEdge>(formulaGraph, connectedVertices.get(1), formulaGraph.edgeSet());
        VertexEquivalenceComparator vertexComparator = new VertexEquivalenceComparator(variablePairs, anonymousVariables);
        UniformEquivalenceComparator<DefaultEdge, Graph<FormulaGraphVertex, DefaultEdge>> edgeComparator = new UniformEquivalenceComparator<DefaultEdge, Graph<FormulaGraphVertex, DefaultEdge>>();
        GraphIsomorphismInspector<Graph<FormulaGraphVertex, DefaultEdge>> isomorphismInspector = AdaptiveIsomorphismInspectorFactory.createIsomorphismInspector(connectedSubgraphOne, connectedSubgraphTwo, vertexComparator, edgeComparator);
        boolean areIsomorphic = isomorphismInspector.isIsomorphic();
        if (logger.isDebugEnabled()) logger.debug("Graph One: \n" + connectedSubgraphOne + "\nGraph Two: \n" + connectedSubgraphTwo + "\nAre isomorphic: " + areIsomorphic);
        if (!areIsomorphic) {
            return null;
        }
        return formulaWithAdornmentsGenerator.generate(formula, connectedSubgraphOne, connectedSubgraphTwo, variablePairs);
    }

    private List<FormulaVariable> findAnonymousVariables(IFormula formula, List<VariablePair> variablePairs) {
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        for (FormulaVariable formulaVariable : formula.getLocalVariables()) {
            if (isInVariablePair(formulaVariable, variablePairs)) {
                continue;
            }
            result.add(formulaVariable);
        }
        return result;
    }

    private boolean isInVariablePair(FormulaVariable formulaVariable, List<VariablePair> variablePairs) {
        for (VariablePair variablePair : variablePairs) {
            if (variablePair.getLeftVariable().equals(formulaVariable)
                    || variablePair.getRightVariable().equals(formulaVariable)) {
                return true;
            }
        }
        return false;
    }

}

class VariablePair {

    private FormulaVariable leftVariable;
    private FormulaVariable rightVariable;
    private FormulaGraphVertex vertex;

    public VariablePair(FormulaVariable leftVariable, FormulaVariable rightVariable, FormulaGraphVertex vertex) {
        this.leftVariable = leftVariable;
        this.rightVariable = rightVariable;
        this.vertex = vertex;
    }

    public FormulaVariable getLeftVariable() {
        return leftVariable;
    }

    public FormulaVariable getRightVariable() {
        return rightVariable;
    }

    public FormulaGraphVertex getVertex() {
        return vertex;
    }

    public String getOperator() {
        ComparisonAtom atom = (ComparisonAtom) vertex.getAtom();
        return atom.getOperator();
    }

    @Override
    public String toString() {
        return "VariablePair: " + leftVariable + ", " + rightVariable + " in " + vertex;
    }

}
