package bart.model.dependency.analysis;

import bart.model.EGTask;
import bart.model.algebra.operators.BuildAlgebraTreeUtility;
import speedy.model.algebra.operators.AlgebraOperatorWithStats;
import speedy.model.database.TableAlias;
import bart.model.dependency.ComparisonAtom;
import bart.model.dependency.CrossProductFormulas;
import bart.model.dependency.IFormula;
import bart.model.dependency.IFormulaAtom;
import bart.model.dependency.PositiveFormula;
import bart.model.dependency.RelationalAtom;
import bart.utility.DependencyUtility;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindCrossProductFormulas {

    private static Logger logger = LoggerFactory.getLogger(FindCrossProductFormulas.class);

    public void findCrossProductFormulas(IFormula formula, EGTask task) {
        if (!DependencyUtility.hasOnlyVariableInequalities(formula)) {
            //TODO Check CP in case of equalties (ex. R(a, b) S(b, c) T(d, e) (d<a) )
            return;
        }
        if (formula.hasNegations()) {
            throw new UnsupportedOperationException("Unable to check inequalities for formula with negations");
        }
        if (DependencyUtility.hasBuiltIns(formula)) {
            throw new UnsupportedOperationException("Unable to check cross products with built-ins");
        }
        if (logger.isDebugEnabled()) logger.debug("Finding cross product formulas in formula \n\t" + formula);
        PositiveFormula positiveFormula = formula.getPositiveFormula();
        List<RelationalAtom> relationalAtoms = BuildAlgebraTreeUtility.extractRelationalAtoms(positiveFormula);
        List<IFormulaAtom> builtInAtoms = BuildAlgebraTreeUtility.extractBuiltInAtoms(positiveFormula);
        List<IFormulaAtom> comparisonAtoms = BuildAlgebraTreeUtility.extractComparisonAtoms(positiveFormula);
        if (logger.isDebugEnabled()) logger.debug("--Relational atoms: " + relationalAtoms);
        if (logger.isDebugEnabled()) logger.debug("--Builtin atoms: " + builtInAtoms);
        if (logger.isDebugEnabled()) logger.debug("--Comparisons: " + comparisonAtoms);
        Map<TableAlias, AlgebraOperatorWithStats> treeMap = BuildAlgebraTreeUtility.initializeMap(relationalAtoms);
        BuildAlgebraTreeUtility.addLocalSelectionsForBuiltinsAndComparisonsAndRemove(builtInAtoms, treeMap);
        BuildAlgebraTreeUtility.addLocalSelectionsForBuiltinsAndComparisonsAndRemove(comparisonAtoms, treeMap);
        CrossProductFormulas crossProductFormulas = new CrossProductFormulas();
        for (TableAlias tableAlias : treeMap.keySet()) {
            crossProductFormulas.addCrossProduct(tableAlias, treeMap.get(tableAlias).getOperator());
        }
        for (IFormulaAtom comparisonAtom : comparisonAtoms) {
            crossProductFormulas.addInequalityComparison((ComparisonAtom) comparisonAtom);
        }
        formula.setCrossProductFormulas(crossProductFormulas);
    }

}
