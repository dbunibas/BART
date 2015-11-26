package bart.model.dependency.analysis;

import speedy.model.database.AttributeRef;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaVariableOccurrence;
import bart.model.dependency.FormulaWithAdornments;
import bart.model.dependency.IFormula;
import bart.model.dependency.IFormulaAtom;
import bart.model.dependency.RelationalAtom;
import bart.model.dependency.operators.FindVariableEquivalenceClasses;
import bart.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.UndirectedSubgraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateFormulaWithAdornments {

    private static Logger logger = LoggerFactory.getLogger(GenerateFormulaWithAdornments.class);

    private FindVariableEquivalenceClasses equivalenceClassFinder = new FindVariableEquivalenceClasses();

    public FormulaWithAdornments generate(IFormula formula, UndirectedSubgraph<FormulaGraphVertex, DefaultEdge> connectedSubgraphOne, 
            UndirectedSubgraph<FormulaGraphVertex, DefaultEdge> connectedSubgraphTwo, List<VariablePair> variablePairs) {
        UndirectedSubgraph<FormulaGraphVertex, DefaultEdge> connectedSubgraph = chooseSubgraphWithNoVirtualVertices(connectedSubgraphOne, connectedSubgraphTwo);
        if (connectedSubgraph == null) {
            throw new IllegalArgumentException("Please add explicit selection for variables to make the formula symmetric...");
        }

        FormulaWithAdornments formulaWithAdornment = new FormulaWithAdornments(formula.clone());
        List<IFormulaAtom> symmetricAtoms = extractFormulaAtoms(connectedSubgraph);
        if (logger.isDebugEnabled()) logger.debug("Symmetric atoms: " + symmetricAtoms);
        removeAtoms(formulaWithAdornment, symmetricAtoms);
        Set<FormulaVariable> variablesToKeep = findVariablesToKeep(formulaWithAdornment);
        if (logger.isDebugEnabled()) logger.debug("Variables to keep: " + variablesToKeep);
        removeUnneededVariables(formulaWithAdornment, variablesToKeep);
        removeUnneededVariableOccurrences(formulaWithAdornment, symmetricAtoms);
        equivalenceClassFinder.findVariableEquivalenceClasses(formulaWithAdornment.getFormula());
        addAdornments(formulaWithAdornment, variablePairs);
        if (logger.isDebugEnabled()) logger.debug("Result: " + formulaWithAdornment);
        return formulaWithAdornment;
    }

    private UndirectedSubgraph<FormulaGraphVertex, DefaultEdge> chooseSubgraphWithNoVirtualVertices(UndirectedSubgraph<FormulaGraphVertex, DefaultEdge> connectedSubgraphOne, UndirectedSubgraph<FormulaGraphVertex, DefaultEdge> connectedSubgraphTwo) {
        if (hasNoVirtual(connectedSubgraphOne)) {
            return connectedSubgraphOne;
        } else if (hasNoVirtual(connectedSubgraphTwo)) {
            return connectedSubgraphTwo;
        }
        return null;
    }

    private boolean hasNoVirtual(UndirectedSubgraph<FormulaGraphVertex, DefaultEdge> connectedSubgraphOne) {
        for (FormulaGraphVertex formulaGraphVertex : connectedSubgraphOne.vertexSet()) {
            if (formulaGraphVertex.isVirtual()) {
                return false;
            }
        }
        return true;
    }

    private List<IFormulaAtom> extractFormulaAtoms(UndirectedSubgraph<FormulaGraphVertex, DefaultEdge> connectedSubgraph) {
        List<IFormulaAtom> result = new ArrayList<IFormulaAtom>();
        for (FormulaGraphVertex formulaGraphVertex : connectedSubgraph.vertexSet()) {
            if (formulaGraphVertex.isAtom()) {
                result.add(formulaGraphVertex.getAtom());
            }
        }
        return result;
    }

    private void removeAtoms(FormulaWithAdornments formulaWithAdornment, List<IFormulaAtom> symmetricAtoms) {
        for (Iterator<IFormulaAtom> it = formulaWithAdornment.getFormula().getAtoms().iterator(); it.hasNext();) {
            IFormulaAtom atom = it.next();
            if (!contained(atom, symmetricAtoms)) {
                it.remove();
            }
        }
    }

    private boolean contained(IFormulaAtom atom, List<IFormulaAtom> symmetricAtoms) {
        for (IFormulaAtom currentAtom : symmetricAtoms) {
            if (currentAtom.toString().equals(atom.toString())) {
                return true;
            }
        }
        return false;
    }

    private Set<FormulaVariable> findVariablesToKeep(FormulaWithAdornments formulaWithAdornment) {
        Set<FormulaVariable> result = new HashSet<FormulaVariable>();
        for (IFormulaAtom atom : formulaWithAdornment.getFormula().getAtoms()) {
            result.addAll(DependencyUtility.findVariablesInAtom(atom));
        }
        return result;
    }

    private void removeUnneededVariables(FormulaWithAdornments formulaWithAdornment, Set<FormulaVariable> variablesToKeep) {
        for (Iterator<FormulaVariable> it = formulaWithAdornment.getFormula().getLocalVariables().iterator(); it.hasNext();) {
            FormulaVariable formulaVariable = it.next();
            if (variablesToKeep.contains(formulaVariable)) {
                continue;
            }
            it.remove();
        }
    }

    private void removeUnneededVariableOccurrences(FormulaWithAdornments formulaWithAdornment, List<IFormulaAtom> symmetricAtoms) {
        for (FormulaVariable formulaVariable : formulaWithAdornment.getFormula().getLocalVariables()) {
            for (Iterator<FormulaVariableOccurrence> it = formulaVariable.getRelationalOccurrences().iterator(); it.hasNext();) {
                FormulaVariableOccurrence formulaVariableOccurrence = it.next();
                if (!contains(formulaVariableOccurrence.getAttributeRef(), symmetricAtoms)) {
                    it.remove();
                }
            }
            for (Iterator<IFormulaAtom> it = formulaVariable.getNonRelationalOccurrences().iterator(); it.hasNext();) {
                IFormulaAtom formulaAtom = it.next();
                if (!contained(formulaAtom, symmetricAtoms)) {
                    it.remove();
                }
            }
        }
    }

    private boolean contains(AttributeRef attributeRef, List<IFormulaAtom> symmetricAtoms) {
        for (IFormulaAtom atom : symmetricAtoms) {
            if (!atom.isRelational()) {
                continue;
            }
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            if (relationalAtom.getTableAlias().equals(attributeRef.getTableAlias())) {
                return true;
            }
        }
        return false;
    }

    private void addAdornments(FormulaWithAdornments formulaWithAdornment, List<VariablePair> variablePairs) {
        for (VariablePair variablePair : variablePairs) {
            if(formulaWithAdornment.getFormula().getLocalVariables().contains(variablePair.getLeftVariable())){
                formulaWithAdornment.addAdornment(variablePair.getLeftVariable(), variablePair.getOperator());
            }
            if(formulaWithAdornment.getFormula().getLocalVariables().contains(variablePair.getRightVariable())){
                formulaWithAdornment.addAdornment(variablePair.getRightVariable(), variablePair.getOperator());
            }
        }
    }

}
