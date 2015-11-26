package bart.model.algebra.operators;

import bart.utility.AlgebraUtility;
import bart.model.EGTask;
import bart.model.dependency.ComparisonAtom;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaVariableOccurrence;
import bart.model.dependency.FormulaWithNegations;
import bart.model.dependency.IFormula;
import bart.model.dependency.IFormulaAtom;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.Difference;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Join;
import speedy.model.algebra.Project;
import speedy.model.database.AttributeRef;
import speedy.model.database.TableAlias;
import speedy.utility.SpeedyUtility;

public class BuildAlgebraTree {

    private static Logger logger = LoggerFactory.getLogger(BuildAlgebraTree.class);

    private BuildAlgebraTreeForPositiveFormula builderForPositiveFormula = new BuildAlgebraTreeForPositiveFormula();

    public IAlgebraOperator buildTreeForPremise(IFormula formula, EGTask task) {
        return buildStandardTreeForFormulaWithNegations(formula, null, task);
    }

    public IAlgebraOperator buildTreeForPremiseWithPreRandomSampling(IFormula formula, Integer sampleSize, EGTask task) {
        return buildStandardTreeForFormulaWithNegations(formula, sampleSize, task);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////
    ////////    STANDARD QUERY WITH JOINS
    ////////
    ////////////////////////////////////////////////////////////////////////////////////////
    private IAlgebraOperator buildStandardTreeForFormulaWithNegations(IFormula formula, Integer sampleSize, EGTask task) {
        IAlgebraOperator root = builderForPositiveFormula.buildTreeForPositiveFormula(formula.getPositiveFormula(), sampleSize, task);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            root = addStandardDifferences(root, (FormulaWithNegations) negatedFormula, sampleSize, task);
        }
        return root;
    }

    private IAlgebraOperator addStandardDifferences(IAlgebraOperator root, FormulaWithNegations negatedFormula, Integer sampleSize, EGTask task) {
        IAlgebraOperator negatedRoot = buildStandardTreeForFormulaWithNegations(negatedFormula, sampleSize, task);
        IAlgebraOperator joinRoot = addJoinForDifference(root, negatedRoot, negatedFormula);
        IAlgebraOperator project = new Project(SpeedyUtility.createProjectionAttributes(root.getAttributes(task.getSource(), task.getTarget())));
        project.addChild(joinRoot);
        IAlgebraOperator difference = new Difference();
        difference.addChild(root);
        difference.addChild(project);
        return difference;
    }

    private IAlgebraOperator addJoinForDifference(IAlgebraOperator root, IAlgebraOperator negatedRoot, FormulaWithNegations negatedFormula) {
        List<DifferenceEquality> differenceEqualities = findDifferenceEqualities(negatedFormula);
        List<AttributeRef> leftAttributes = new ArrayList<AttributeRef>();
        List<AttributeRef> rightAttributes = new ArrayList<AttributeRef>();
        for (DifferenceEquality equality : differenceEqualities) {
            leftAttributes.add(equality.leftAttribute);
            rightAttributes.add(equality.rightAttribute);
        }
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(root);
        join.addChild(negatedRoot);
        return join;
    }

    private List<DifferenceEquality> findDifferenceEqualities(FormulaWithNegations negatedFormula) {
        List<DifferenceEquality> result = new ArrayList<DifferenceEquality>();
        for (FormulaVariable formulaVariable : negatedFormula.getAllVariables()) {
            DifferenceEquality equality = isDifferenceVariable(formulaVariable, negatedFormula);
            if (equality != null) {
                result.add(equality);
            }
        }
        for (IFormulaAtom atom : negatedFormula.getAtoms()) {
            if (!(atom instanceof ComparisonAtom)) {
                continue;
            }
            ComparisonAtom comparison = (ComparisonAtom) atom;
            if (comparison.getVariables().size() < 2) {
                continue;
            }
            DifferenceEquality equality = isDifferenceComparison(comparison, negatedFormula);
            if (equality != null) {
                result.add(equality);
            }
        }
        return result;
    }

    private DifferenceEquality isDifferenceVariable(FormulaVariable formulaVariable, FormulaWithNegations negatedFormula) {
        IFormula father = negatedFormula.getFather();
        AttributeRef leftAttribute = findFirstOccurrenceInFormula(formulaVariable, father);
        AttributeRef rightAttribute = findFirstOccurrenceInFormula(formulaVariable, negatedFormula);
        if (leftAttribute != null && rightAttribute != null) {
            return new DifferenceEquality(leftAttribute, rightAttribute);
        }
        return null;
    }

    private AttributeRef findFirstOccurrenceInFormula(FormulaVariable formulaVariable, IFormula formula) {
        List<TableAlias> aliasesInFormula = AlgebraUtility.findAliasesForFormula(formula.getPositiveFormula());
        for (FormulaVariableOccurrence occurrence : formulaVariable.getRelationalOccurrences()) {
            AttributeRef attribute = occurrence.getAttributeRef();
            if (aliasesInFormula.contains(attribute.getTableAlias())) {
                return attribute;
            }
        }
        return null;
    }

    private DifferenceEquality isDifferenceComparison(ComparisonAtom comparison, FormulaWithNegations negatedFormula) {
        IFormula father = negatedFormula.getFather();
        FormulaVariable firstVariable = comparison.getVariables().get(0);
        FormulaVariable secondVariable = comparison.getVariables().get(1);
        AttributeRef leftAttribute = findFirstOccurrenceInFormula(firstVariable, father);
        AttributeRef rightAttribute = findFirstOccurrenceInFormula(secondVariable, negatedFormula);
        if (leftAttribute == null) {
            leftAttribute = findFirstOccurrenceInFormula(secondVariable, father);
            rightAttribute = findFirstOccurrenceInFormula(firstVariable, negatedFormula);
        }
        if (leftAttribute == null) {
            return null;
        }
        return new DifferenceEquality(leftAttribute, rightAttribute);

    }
}

class DifferenceEquality {

    DifferenceEquality(AttributeRef leftAttribute, AttributeRef rightAttribute) {
        this.leftAttribute = leftAttribute;
        this.rightAttribute = rightAttribute;
    }
    AttributeRef leftAttribute;
    AttributeRef rightAttribute;
}
