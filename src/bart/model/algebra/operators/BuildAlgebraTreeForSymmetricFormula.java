package bart.model.algebra.operators;

import bart.BartConstants;
import bart.model.EGTask;
import bart.model.algebra.CountAggregateFunction;
import bart.model.algebra.GroupBy;
import bart.model.algebra.IAggregateFunction;
import bart.model.algebra.IAlgebraOperator;
import bart.model.algebra.OrderBy;
import bart.model.algebra.Project;
import bart.model.algebra.Select;
import bart.model.algebra.SelectIn;
import bart.model.algebra.ValueAggregateFunction;
import bart.model.database.AttributeRef;
import bart.model.database.TableAlias;
import bart.model.dependency.PositiveFormula;
import bart.model.dependency.RelationalAtom;
import bart.model.expressions.Expression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildAlgebraTreeForSymmetricFormula {

    private static Logger logger = LoggerFactory.getLogger(BuildAlgebraTreeForSymmetricFormula.class);

    private BuildAlgebraTreeForPositiveFormula builderForPositiveFormula = new BuildAlgebraTreeForPositiveFormula();

    @SuppressWarnings("unchecked")
    public IAlgebraOperator buildTree(PositiveFormula symmetricFormula, List<AttributeRef> equalityAttributes, List<List<AttributeRef>> inequalityAttributes, EGTask task) {
        if (equalityAttributes.isEmpty()) {
            throw new IllegalArgumentException("Unable to generate algebra tree without equality attributes " + symmetricFormula);
        }
        if (logger.isDebugEnabled()) logger.debug("Building tree for symmetric formula...");
        IAlgebraOperator root = builderForPositiveFormula.buildTreeForPositiveFormula(symmetricFormula, null, task);
        List<IAlgebraOperator> subQueries = new ArrayList<IAlgebraOperator>();
        if (inequalityAttributes.isEmpty()) {
                IAlgebraOperator violationValues = generateViolationValues(symmetricFormula, equalityAttributes, Collections.EMPTY_LIST, root);
                subQueries.add(violationValues);
        } else {
            for (List<AttributeRef> attributeSet : inequalityAttributes) {
                IAlgebraOperator violationValues = generateViolationValues(symmetricFormula, equalityAttributes, attributeSet, root);
                subQueries.add(violationValues);
            }
        }
        SelectIn selectIn = new SelectIn(equalityAttributes, subQueries);
        selectIn.addChild(root);
        List<AttributeRef> orderByAttributes = new ArrayList<AttributeRef>(equalityAttributes);
        orderByAttributes.addAll(getOIDAttributes(symmetricFormula));
        OrderBy orderBy = new OrderBy(orderByAttributes);
        orderBy.addChild(selectIn);
        return orderBy;
    }
//    public IAlgebraOperator buildTree(PositiveFormula symmetricFormula, List<AttributeRef> equalityAttributes, List<AttributeRef> inequalityAttributes, EGTask task) {
//        if (logger.isDebugEnabled()) logger.debug("Building tree for symmetric formula...");
//        IAlgebraOperator root = builderForPositiveFormula.buildTreeForPositiveFormula(symmetricFormula, null, task);
//        IAlgebraOperator violationValues = generateViolationValues(symmetricFormula, equalityAttributes, inequalityAttributes, root);
//        if (equalityAttributes.isEmpty()) {
//            return violationValues;
//        }
//        List<IAlgebraOperator> subQueries = new ArrayList<IAlgebraOperator>();
//        subQueries.add(violationValues);
//        SelectIn selectIn = new SelectIn(equalityAttributes, subQueries);
//        selectIn.addChild(root);
//        List<AttributeRef> orderByAttributes = new ArrayList<AttributeRef>(equalityAttributes);
//        orderByAttributes.addAll(getOIDAttributes(symmetricFormula));
//        OrderBy orderBy = new OrderBy(orderByAttributes);
//        orderBy.addChild(selectIn);
//        return orderBy;
//    }

    private IAlgebraOperator generateViolationValues(PositiveFormula symmetricFormula, List<AttributeRef> equalityAttributes,
            List<AttributeRef> inequalityAttributes, IAlgebraOperator root) {
        if (logger.isDebugEnabled()) logger.debug("Generating violation values for " + symmetricFormula + ", equality attributes " + equalityAttributes + ", inequality variables " + inequalityAttributes);
        IAlgebraOperator operatorForQueriedAttributes = buildOperatorForQueriedAttributes(equalityAttributes, inequalityAttributes, root);

        AttributeRef countAttribute = new AttributeRef(new TableAlias(BartConstants.AGGR), BartConstants.COUNT);
        List<IAggregateFunction> aggregatesForWitness = new ArrayList<IAggregateFunction>();
        for (AttributeRef witnessAttribute : equalityAttributes) {
            aggregatesForWitness.add(new ValueAggregateFunction(witnessAttribute));
        }
        aggregatesForWitness.add(new CountAggregateFunction(countAttribute));
        GroupBy secondGroupBy = new GroupBy(equalityAttributes, aggregatesForWitness);
        secondGroupBy.addChild(operatorForQueriedAttributes);

        Expression expression = new Expression("count > 1");
        expression.changeVariableDescription("count", countAttribute);
        Select select = new Select(expression);
        select.addChild(secondGroupBy);
        Project violationProject = new Project(equalityAttributes);
        violationProject.addChild(select);
        return violationProject;
    }

    private IAlgebraOperator buildOperatorForQueriedAttributes(List<AttributeRef> equalityAttributes, List<AttributeRef> inequalityAttributes, IAlgebraOperator root) {
        if (inequalityAttributes.isEmpty()) {
            return root;
        }
        List<IAggregateFunction> aggreatesForQueriedAttributes = new ArrayList<IAggregateFunction>();
        List<AttributeRef> symmetricQueriedAttributes = new ArrayList<AttributeRef>();
        symmetricQueriedAttributes.addAll(equalityAttributes);
        symmetricQueriedAttributes.addAll(inequalityAttributes);
        for (AttributeRef queriedAttribute : symmetricQueriedAttributes) {
            aggreatesForQueriedAttributes.add(new ValueAggregateFunction(queriedAttribute));
        }
        GroupBy groupByQueriedAttributes = new GroupBy(symmetricQueriedAttributes, aggreatesForQueriedAttributes);
        groupByQueriedAttributes.addChild(root);
        return groupByQueriedAttributes;
    }

    private Set<AttributeRef> getOIDAttributes(PositiveFormula symmetricFormula) {
        Set<AttributeRef> result = new HashSet<AttributeRef>();
        for (RelationalAtom atom : symmetricFormula.getRelationalAtoms()) {
            result.add(new AttributeRef(atom.getTableAlias(), BartConstants.OID));
        }
        return result;
    }

}
