package bart.model.algebra.operators;

import bart.utility.AlgebraUtility;
import bart.BartConstants;
import bart.IInitializableOperator;
import bart.OperatorFactory;
import bart.model.EGTask;
import bart.model.dependency.*;
import speedy.model.expressions.Expression;
import bart.utility.BartUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.CartesianProduct;
import speedy.model.algebra.ExtractRandomSample;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.algebra.Join;
import speedy.model.algebra.Select;
import speedy.model.algebra.operators.AlgebraOperatorWithStats;
import speedy.model.database.AttributeRef;
import speedy.model.database.ResultInfo;
import speedy.model.database.TableAlias;
import speedy.model.database.operators.IRunQuery;

public class BuildAlgebraTreeForPositiveFormula implements IInitializableOperator {

    private static Logger logger = LoggerFactory.getLogger(BuildAlgebraTreeForPositiveFormula.class);

    private FindConnectedTables connectedTablesFinder = new FindConnectedTables();
    private IRunQuery queryRunner;

    public IAlgebraOperator buildTreeForPositiveFormula(PositiveFormula positiveFormula, Integer sampleSize, EGTask task) {
        intitializeOperators(task);
        if (logger.isDebugEnabled()) logger.debug("Building tree for formula: " + positiveFormula);
        List<RelationalAtom> relationalAtoms = BuildAlgebraTreeUtility.extractRelationalAtoms(positiveFormula);
        List<IFormulaAtom> builtInAtoms = BuildAlgebraTreeUtility.extractBuiltInAtoms(positiveFormula);
        List<IFormulaAtom> comparisonAtoms = BuildAlgebraTreeUtility.extractComparisonAtoms(positiveFormula);
        if (logger.isDebugEnabled()) logger.debug("--Relational atoms: " + relationalAtoms);
        if (logger.isDebugEnabled()) logger.debug("--Builtin atoms: " + builtInAtoms);
        if (logger.isDebugEnabled()) logger.debug("--Comparisons: " + comparisonAtoms);
        Map<TableAlias, AlgebraOperatorWithStats> treeMap = BuildAlgebraTreeUtility.initializeMap(relationalAtoms);
        BuildAlgebraTreeUtility.addLocalSelectionsForBuiltinsAndComparisonsAndRemove(builtInAtoms, treeMap);
        BuildAlgebraTreeUtility.addLocalSelectionsForBuiltinsAndComparisonsAndRemove(comparisonAtoms, treeMap);
        if (sampleSize != null) {
            initTableSize(treeMap, task);
        }
        IAlgebraOperator root;
        if (relationalAtoms.size() == 1) {
            TableAlias tableAlias = relationalAtoms.get(0).getTableAlias();
            root = handleSampling(sampleSize, tableAlias, treeMap);
        } else {
            root = addJoinsAndCartesianProducts(positiveFormula, relationalAtoms, treeMap, sampleSize);
            root = addGlobalSelectionsForBuiltins(builtInAtoms, root);
            root = addGlobalSelectionsForComparisons(comparisonAtoms, root, positiveFormula);
        }
        if (logger.isDebugEnabled()) logger.debug("--Result: " + root);
        return root;
    }

    //////////////////////          RANDOM SAMPLE
    private void initTableSize(Map<TableAlias, AlgebraOperatorWithStats> treeMap, EGTask task) {
        for (TableAlias tableAlias : treeMap.keySet()) {
            AlgebraOperatorWithStats operatorWithStats = treeMap.get(tableAlias);
            IAlgebraOperator operator = operatorWithStats.getOperator();
            ResultInfo resultInfo = queryRunner.getSize(operator, task.getSource(), task.getTarget());
            operatorWithStats.setResultInfo(resultInfo);
            if (logger.isDebugEnabled()) logger.debug(operatorWithStats.toString());
        }
    }

    private IAlgebraOperator handleSampling(Integer sampleSize, TableAlias tableAlias, Map<TableAlias, AlgebraOperatorWithStats> treeMap) {
        AlgebraOperatorWithStats operatorWithStats = treeMap.get(tableAlias);
        if (sampleSize == null) {
            return operatorWithStats.getOperator();
        }
        for (AlgebraOperatorWithStats currentOperator : treeMap.values()) {
            if (currentOperator.isRandomized()) {
                return operatorWithStats.getOperator();
            }
        }
        ResultInfo resultInfo = operatorWithStats.getResultInfo();
        ExtractRandomSample sampler = new ExtractRandomSample(sampleSize, resultInfo.getMinOid(), resultInfo.getMaxOid());
        sampler.addChild(operatorWithStats.getOperator());
        operatorWithStats.setRandomized(true);
        return sampler;
    }

    //////////////////////          JOINS
    private IAlgebraOperator addJoinsAndCartesianProducts(PositiveFormula positiveFormula, List<RelationalAtom> atoms, Map<TableAlias, AlgebraOperatorWithStats> treeMap, Integer sampleSize) {
        List<FormulaVariable> equalityGeneratingVariables = findEqualityGeneratingVariables(positiveFormula);
        if (logger.isDebugEnabled()) logger.debug("Equality generating variables: " + equalityGeneratingVariables);
        List<Equality> equalities = extractEqualities(equalityGeneratingVariables, positiveFormula);
        if (logger.isDebugEnabled()) logger.debug("Join equalities: " + equalities);
        List<EqualityGroup> equalityGroups = groupEqualities(equalities);
        List<ConnectedTables> connectedTables = connectedTablesFinder.findConnectedEqualityGroups(atoms, equalityGroups);
        if (logger.isDebugEnabled()) logger.debug("Connected tables: " + connectedTables);
        assignEqualityGroupsToConnectedTables(connectedTables, equalityGroups);
        List<IAlgebraOperator> rootsForConnectedComponents = new ArrayList<IAlgebraOperator>();
        for (ConnectedTables connectedComponent : connectedTables) {
            rootsForConnectedComponents.add(generateRootForConnectedComponent(connectedComponent, treeMap, positiveFormula, sampleSize));
        }
        if (rootsForConnectedComponents.size() == 1) {
            return rootsForConnectedComponents.get(0);
        }
        IAlgebraOperator cartesianProduct = addCartesianProduct(rootsForConnectedComponents, connectedTables, positiveFormula);
        return cartesianProduct;
    }

    private IAlgebraOperator addCartesianProduct(List<IAlgebraOperator> rootsForConnectedComponents, List<ConnectedTables> connectedTables, IFormula formula) {
        CartesianProduct cartesianProduct = new CartesianProduct();
        for (IAlgebraOperator rootForConnectedComponent : rootsForConnectedComponents) {
            cartesianProduct.addChild(rootForConnectedComponent);
        }
        if (cartesianProduct.getChildren().size() != 2) {
            return cartesianProduct;
        }
        IAlgebraOperator root = cartesianProduct;
        ConnectedTables leftConnectedTables = connectedTables.get(0);
        ConnectedTables rightConnectedTables = connectedTables.get(1);
        if (leftConnectedTables.getTableAliases().size() > 1 || rightConnectedTables.getTableAliases().size() > 1) {
            return root;
        }
        TableAlias leftTableAlias = leftConnectedTables.getTableAliases().iterator().next();
        TableAlias rightTableAlias = rightConnectedTables.getTableAliases().iterator().next();
        if (leftTableAlias.getTableName().equals(rightTableAlias.getTableName())) {
            root = addOidInequality(leftTableAlias, rightTableAlias, cartesianProduct, formula);
        }
        return root;
    }

    private List<FormulaVariable> findEqualityGeneratingVariables(PositiveFormula positiveFormula) {
        // finds variables that have multiple occurrences in relationala atoms; comparisons are handled as selections
        List<FormulaVariable> result = new ArrayList<FormulaVariable>();
        for (FormulaVariable formulaVariable : positiveFormula.getAllVariables()) {
            List<AttributeRef> occurrencesInFormula = findOccurrencesInFormula(formulaVariable, positiveFormula);
            if (logger.isDebugEnabled()) logger.debug("Occurrences for variable " + formulaVariable + ": " + occurrencesInFormula);
            if (occurrencesInFormula.size() > 1) {
                result.add(formulaVariable);
            }
        }
        return result;
    }

    private List<AttributeRef> findOccurrencesInFormula(FormulaVariable formulaVariable, PositiveFormula positiveFormula) {
        List<TableAlias> aliasesInFormula = AlgebraUtility.findAliasesForFormula(positiveFormula);
        if (logger.isDebugEnabled()) logger.debug("Finding occurrences for variable: " + formulaVariable + " in aliases " + aliasesInFormula);
        List<AttributeRef> variableAliasesInFormula = new ArrayList<AttributeRef>();
        for (FormulaVariableOccurrence occurrence : formulaVariable.getRelationalOccurrences()) {
            if (logger.isDebugEnabled()) logger.debug("\tOccurrence: " + occurrence.toLongString());
            if (aliasesInFormula.contains(occurrence.getAttributeRef().getTableAlias())) {
                variableAliasesInFormula.add(occurrence.getAttributeRef());
            }
        }
        if (logger.isDebugEnabled()) logger.debug("Filtering result occurrences for variable: " + variableAliasesInFormula);
        return variableAliasesInFormula;
    }

    private List<Equality> extractEqualities(List<FormulaVariable> joinVariables, PositiveFormula positiveFormula) {
        List<Equality> result = new ArrayList<Equality>();
        for (FormulaVariable joinVariable : joinVariables) {
            List<AttributeRef> occurrencesInFormula = findOccurrencesInFormula(joinVariable, positiveFormula);
            for (int i = 0; i < occurrencesInFormula.size() - 1; i++) {
                Equality equality = new Equality(occurrencesInFormula.get(i), occurrencesInFormula.get(i + 1));
                if (!equality.isTrivial()) {
                    result.add(equality);
                }
            }
        }
        return result;
    }

    private List<EqualityGroup> groupEqualities(List<Equality> equalities) {
        Map<String, EqualityGroup> groups = new HashMap<String, EqualityGroup>();
        for (Equality equality : equalities) {
            EqualityGroup group = groups.get(getHashString(equality.getLeftAttribute().getTableAlias(), equality.getRightAttribute().getTableAlias()));
            if (group == null) {
                group = new EqualityGroup(equality);
                groups.put(getHashString(equality.getLeftAttribute().getTableAlias(), equality.getRightAttribute().getTableAlias()), group);
            }
            group.getEqualities().add(equality);
        }
        return new ArrayList<EqualityGroup>(groups.values());
    }

    private String getHashString(TableAlias alias1, TableAlias alias2) {
        List<String> aliases = new ArrayList<String>();
        aliases.add(alias1.toString());
        aliases.add(alias2.toString());
        Collections.sort(aliases);
        return aliases.toString();
    }

    private void assignEqualityGroupsToConnectedTables(List<ConnectedTables> connectedTables, List<EqualityGroup> equalityGroups) {
        for (ConnectedTables connectedComponent : connectedTables) {
            List<EqualityGroup> equalityGroupsForConnectedComponent = new ArrayList<EqualityGroup>();
            for (EqualityGroup equalityGroup : equalityGroups) {
                if (connectedComponent.getTableAliases().contains(equalityGroup.getLeftTable()) && connectedComponent.getTableAliases().contains(equalityGroup.getRightTable())) {
                    equalityGroupsForConnectedComponent.add(equalityGroup);
                }
            }
            connectedComponent.setEqualityGroups(equalityGroupsForConnectedComponent);
        }
    }

    private IAlgebraOperator generateRootForConnectedComponent(ConnectedTables connectedTables, Map<TableAlias, AlgebraOperatorWithStats> treeMap, IFormula formula, Integer sampleSize) {
        if (connectedTables.getTableAliases().size() == 1) {
            TableAlias singletonTable = connectedTables.getTableAliases().iterator().next();
            return handleSampling(sampleSize, singletonTable, treeMap);
        }
        IAlgebraOperator root = null;
        List<TableAlias> addedTables = new ArrayList<TableAlias>();
        sortEqualityGroups(connectedTables.getEqualityGroups());
        List<EqualityGroup> equalityGroupClone = new ArrayList<EqualityGroup>(connectedTables.getEqualityGroups());
        for (Iterator<EqualityGroup> it = equalityGroupClone.iterator(); it.hasNext();) {
            EqualityGroup equalityGroup = it.next();
            if (isSelection(equalityGroup, addedTables)) {
                continue;
            }
            root = addJoin(equalityGroup, addedTables, root, treeMap, formula, sampleSize);
            if (logger.isDebugEnabled()) logger.debug("Adding join for equality group:\n" + equalityGroup + "\nResult:\n" + root);
            it.remove();
        }
        if (!equalityGroupClone.isEmpty()) {
            List<Expression> selections = new ArrayList<Expression>();
            for (EqualityGroup equalityGroup : equalityGroupClone) {
                List<Expression> selectionsForEquality = equalityGroup.getEqualityExpressions();
                selections.addAll(selectionsForEquality);
            }
            Select select = new Select(selections);
            select.addChild(root);
            root = select;
        }
        return root;
    }

    private void sortEqualityGroups(List<EqualityGroup> equalityGroups) {
        if (equalityGroups.isEmpty()) {
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Sorting equality groups\n" + BartUtility.printCollection(equalityGroups));
        List<EqualityGroup> addedGroup = new ArrayList<EqualityGroup>();
        addedGroup.add(equalityGroups.remove(0));
        while (!equalityGroups.isEmpty()) {
            EqualityGroup nextEqualityGroup = findNextGroupInJoin(equalityGroups, addedGroup);
            addedGroup.add(nextEqualityGroup);
            equalityGroups.remove(nextEqualityGroup);
        }
        equalityGroups.addAll(addedGroup);
        if (logger.isDebugEnabled()) logger.debug("Result\n" + BartUtility.printCollection(equalityGroups));
    }

    private EqualityGroup findNextGroupInJoin(List<EqualityGroup> equalityGroups, List<EqualityGroup> sortedList) {
        for (EqualityGroup equalityGroup : equalityGroups) {
            if (containsTableAlias(equalityGroup.getLeftTable(), sortedList)
                    || containsTableAlias(equalityGroup.getRightTable(), sortedList)) {
                return equalityGroup;
            }
        }
        throw new IllegalArgumentException("Unable to find a path between equality groups\n" + BartUtility.printCollection(equalityGroups) + "\n" + BartUtility.printCollection(sortedList));
    }

    private boolean containsTableAlias(TableAlias table, List<EqualityGroup> sortedList) {
        for (EqualityGroup equalityGroup : sortedList) {
            if (equalityGroup.getLeftTable().equals(table)
                    || equalityGroup.getRightTable().equals(table)) {
                return true;
            }
        }
        return false;
    }

    private boolean singleTable(EqualityGroup equalityGroup) {
        return equalityGroup.getLeftTable().equals(equalityGroup.getRightTable());
    }

    private boolean isSelection(EqualityGroup equalityGroup, List<TableAlias> addedTables) {
        return singleTable(equalityGroup)
                || (addedTables.contains(equalityGroup.getLeftTable())
                && addedTables.contains(equalityGroup.getRightTable()));
    }

    private IAlgebraOperator addJoin(EqualityGroup equalityGroup, List<TableAlias> addedTables, IAlgebraOperator joinRoot, Map<TableAlias, AlgebraOperatorWithStats> treeMap, IFormula formula, Integer sampleSize) {
        if (logger.isDebugEnabled()) logger.debug("-------Adding join for equality: " + equalityGroup);
        // standard case: add table for right attribute
        IAlgebraOperator leftChild = joinRoot;
        if (addedTables.isEmpty()) {
            // initial joins: joinRoot == null
            leftChild = handleSampling(sampleSize, equalityGroup.getLeftTable(), treeMap);
            AlgebraUtility.addIfNotContained(addedTables, equalityGroup.getLeftTable());
        }
        IAlgebraOperator rightChild = handleSampling(sampleSize, equalityGroup.getRightTable(), treeMap);
        List<AttributeRef> leftAttributes = equalityGroup.getAttributeRefsForTableAlias(equalityGroup.getLeftTable());
        List<AttributeRef> rightAttributes = equalityGroup.getAttributeRefsForTableAlias(equalityGroup.getRightTable());
        if (addedTables.contains(equalityGroup.getRightTable())) {
            // alternative case: add table for right attribute    
            rightChild = handleSampling(sampleSize, equalityGroup.getLeftTable(), treeMap);
            leftAttributes = equalityGroup.getAttributeRefsForTableAlias(equalityGroup.getRightTable());
            rightAttributes = equalityGroup.getAttributeRefsForTableAlias(equalityGroup.getLeftTable());
            AlgebraUtility.addIfNotContained(addedTables, equalityGroup.getLeftTable());
        } else {
            AlgebraUtility.addIfNotContained(addedTables, equalityGroup.getRightTable());
        }
        Join join = new Join(leftAttributes, rightAttributes);
        join.addChild(leftChild);
        join.addChild(rightChild);
//        AlgebraUtility.addIfNotContained(addedTables, equalityGroup.leftTable);
//        AlgebraUtility.addIfNotContained(addedTables, equalityGroup.rightTable);
        IAlgebraOperator root = join;
        if (equalityGroup.getLeftTable().getTableName().equals(equalityGroup.getRightTable().getTableName())) {
            root = addOidInequality(equalityGroup.getLeftTable(), equalityGroup.getRightTable(), root, formula);
        }
        return root;
    }

    private Select addOidInequality(TableAlias leftTable, TableAlias rightTable, IAlgebraOperator root, IFormula formula) {
        String inequalityOperator = "!=";
//        if (formula.isSymmetric()) {
//            inequalityOperator = "<";
//        }
        Expression oidInequality = new Expression(leftTable.toString() + "." + BartConstants.OID + inequalityOperator + rightTable.toString() + "." + BartConstants.OID);
        oidInequality.changeVariableDescription(leftTable.toString() + "." + BartConstants.OID, new AttributeRef(leftTable, BartConstants.OID));
        oidInequality.changeVariableDescription(rightTable.toString() + "." + BartConstants.OID, new AttributeRef(rightTable, BartConstants.OID));
        Select select = new Select(oidInequality);
        select.addChild(root);
        return select;
    }

    //////////////////////          GLOBAL SELECTIONS
    private IAlgebraOperator addGlobalSelectionsForBuiltins(List<IFormulaAtom> atoms, IAlgebraOperator root) {
        for (IFormulaAtom atom : atoms) {
            BuiltInAtom builtInAtom = (BuiltInAtom) atom;
            Select select = new Select(builtInAtom.getExpression());
            select.addChild(root);
            root = select;
        }
        return root;
    }

    private IAlgebraOperator addGlobalSelectionsForComparisons(List<IFormulaAtom> atoms, IAlgebraOperator root, PositiveFormula positiveFormula) {
        if (logger.isDebugEnabled()) logger.debug("Adding global selections for comparisons " + atoms);
        for (IFormulaAtom atom : atoms) {
            ComparisonAtom comparisonAtom = (ComparisonAtom) atom;
            if (isDifference(comparisonAtom, positiveFormula)) {
                continue;
            }
            Select select = new Select(comparisonAtom.getExpression());
            select.addChild(root);
            root = select;
        }
        return root;
    }

    private boolean isDifference(ComparisonAtom comparisonAtom, PositiveFormula positiveFormula) {
        for (FormulaVariable variable : comparisonAtom.getVariables()) {
            if (findOccurrencesInFormula(variable, positiveFormula).size()
                    != variable.getRelationalOccurrences().size()) {
                return true;
            }
        }
        return false;
    }

    public void intitializeOperators(EGTask task) {
        queryRunner = OperatorFactory.getInstance().getQueryRunner(task);
    }

}
