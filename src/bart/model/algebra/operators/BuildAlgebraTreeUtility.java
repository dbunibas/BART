package bart.model.algebra.operators;

import bart.utility.AlgebraUtility;
import bart.model.dependency.BuiltInAtom;
import bart.model.dependency.ComparisonAtom;
import bart.model.dependency.FormulaAttribute;
import bart.model.dependency.FormulaVariable;
import bart.model.dependency.FormulaVariableOccurrence;
import bart.model.dependency.IFormulaAtom;
import bart.model.dependency.PositiveFormula;
import bart.model.dependency.RelationalAtom;
import speedy.model.expressions.Expression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Scan;
import speedy.model.algebra.Select;
import speedy.model.algebra.operators.AlgebraOperatorWithStats;
import speedy.model.database.AttributeRef;
import speedy.model.database.TableAlias;

public class BuildAlgebraTreeUtility {

    private static Logger logger = LoggerFactory.getLogger(BuildAlgebraTreeUtility.class);

    //////////////////////          INIT DATA STRUCTURES
    public static List<RelationalAtom> extractRelationalAtoms(PositiveFormula positiveFormula) {
        List<RelationalAtom> result = new ArrayList<RelationalAtom>();
        for (IFormulaAtom atom : positiveFormula.getAtoms()) {
            if (atom instanceof RelationalAtom) {
                result.add((RelationalAtom) atom);
            }
        }
        return result;
    }

    public static List<IFormulaAtom> extractBuiltInAtoms(PositiveFormula positiveFormula) {
        List<IFormulaAtom> result = new ArrayList<IFormulaAtom>();
        for (IFormulaAtom atom : positiveFormula.getAtoms()) {
            if (atom instanceof BuiltInAtom) {
                result.add((BuiltInAtom) atom);
            }
        }
        return result;
    }

    public static List<IFormulaAtom> extractComparisonAtoms(PositiveFormula positiveFormula) {
        List<IFormulaAtom> result = new ArrayList<IFormulaAtom>();
        for (IFormulaAtom atom : positiveFormula.getAtoms()) {
            if (atom instanceof ComparisonAtom) {
                result.add((ComparisonAtom) atom);
            }
        }
        return result;
    }

    public static Map<TableAlias, AlgebraOperatorWithStats> initializeMap(List<RelationalAtom> atoms) {
        Map<TableAlias, AlgebraOperatorWithStats> treeMap = new HashMap<TableAlias, AlgebraOperatorWithStats>();
        for (RelationalAtom atom : atoms) {
            if (logger.isDebugEnabled()) logger.debug("Initialize operator for table alias in atom " + atom);
            RelationalAtom relationalAtom = (RelationalAtom) atom;
            TableAlias tableAlias = relationalAtom.getTableAlias();
            IAlgebraOperator tableRoot = new Scan(tableAlias);
            tableRoot = addLocalSelections(tableRoot, relationalAtom);
            treeMap.put(tableAlias, new AlgebraOperatorWithStats(tableRoot));
        }
        return treeMap;
    }

    //////////////////////          LOCAL SELECTIONS
    private static IAlgebraOperator addLocalSelections(IAlgebraOperator scan, RelationalAtom relationalAtom) {
        IAlgebraOperator root = scan;
        List<Expression> selections = new ArrayList<Expression>();
        for (FormulaAttribute attribute : relationalAtom.getAttributes()) {
            if (attribute.getValue().isVariable()) {
                continue;
            }
            AttributeRef attributeRef = new AttributeRef(relationalAtom.getTableAlias(), attribute.getAttributeName());
            Expression selection;
            if (attribute.getValue().isNull()) {
                selection = new Expression("isNull(" + attribute.getAttributeName() + ")");
            } else {
                selection = new Expression(attribute.getAttributeName() + "==" + attribute.getValue());
            }
            selection.getJepExpression().getVar(attribute.getAttributeName()).setDescription(attributeRef);
//            selection.setVariableDescription(attribute.getAttributeName(), attributeRef);
            selections.add(selection);
        }
        if (!selections.isEmpty()) {
            Select select = new Select(selections);
            select.addChild(scan);
            root = select;
        }
        return root;
    }

    public static void addLocalSelectionsForBuiltinsAndComparisonsAndRemove(List<IFormulaAtom> atoms, Map<TableAlias, AlgebraOperatorWithStats> treeMap) {
        if (logger.isDebugEnabled()) logger.debug("Adding selections for atoms: " + atoms);
        for (Iterator<IFormulaAtom> it = atoms.iterator(); it.hasNext();) {
            IFormulaAtom atom = it.next();
            List<TableAlias> aliasesForAtom = AlgebraUtility.findAliasesForAtom(atom);
            boolean atomToRemove = false;
            for (TableAlias tableAlias : aliasesForAtom) {
                if (hasLocalOccurrences(tableAlias, atom)) {
                    atomToRemove = true;
                    IAlgebraOperator rootForAlias = treeMap.get(tableAlias).getOperator();
                    if (rootForAlias == null) {
                        throw new IllegalArgumentException("Unable to find operator for table alias " + tableAlias);
                    }
                    if (rootForAlias instanceof Select) {
                        Select select = (Select) rootForAlias;
                        select.getSelections().add(atom.getExpression());
                    } else {
                        Select select = new Select(atom.getExpression());
                        select.addChild(rootForAlias);
                        treeMap.put(tableAlias, new AlgebraOperatorWithStats(select));
                    }
                }
            }
            if (atomToRemove) {
                it.remove();
            }
        }
    }

    private static boolean hasLocalOccurrences(TableAlias tableAlias, IFormulaAtom atom) {
        for (FormulaVariable variable : atom.getVariables()) {
            if (!hasOccurenceInTable(variable, tableAlias)) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasOccurenceInTable(FormulaVariable variable, TableAlias tableAlias) {
        for (FormulaVariableOccurrence occurrence : variable.getRelationalOccurrences()) {
            if (occurrence.getAttributeRef().getTableAlias().equals(tableAlias)) {
                return true;
            }
        }
        return false;
    }
}
