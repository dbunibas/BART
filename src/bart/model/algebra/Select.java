package bart.model.algebra;

import bart.BartConstants;
import bart.utility.BartUtility;
import bart.model.algebra.operators.EvaluateExpression;
import bart.model.algebra.operators.IAlgebraTreeVisitor;
import bart.model.algebra.operators.ITupleIterator;
import bart.model.database.*;
import bart.model.expressions.Expression;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Select extends AbstractOperator {

    private static Logger logger = LoggerFactory.getLogger(Select.class);

    private List<Expression> selections;
    private static EvaluateExpression expressionEvaluator = new EvaluateExpression();

    public Select(Expression expression) {
        this.selections = new ArrayList<Expression>();
        this.selections.add(expression);
    }

    public Select(List<Expression> selections) {
        this.selections = selections;
    }

    public List<Expression> getSelections() {
        return selections;
    }

    public void accept(IAlgebraTreeVisitor visitor) {
        visitor.visitSelect(this);
    }

    public String getName() {
//        return "SELECT";
        return "SELECT-" + selections;
    }

    public ITupleIterator execute(IDatabase source, IDatabase target) {
        SelectTupleIterator  tupleIterator = new SelectTupleIterator(children.get(0).execute(source, target));
//        if (logger.isDebugEnabled()) logger.debug("Executing select: " + getName() + " on source\n" + (source == null ? "" : source.printInstances()) + "\nand target:\n" + target.printInstances());
        if (logger.isDebugEnabled()) logger.debug("Executing select: " + getName() + " on tuples:\n" + BartUtility.printIterator(children.get(0).execute(source, target)));
        if (logger.isDebugEnabled()) logger.debug("Result:\n" + BartUtility.printIterator(tupleIterator));
        if (logger.isDebugEnabled()) tupleIterator.reset();
        return tupleIterator;
    }

    public List<AttributeRef> getAttributes(IDatabase source, IDatabase target) {
        return this.children.get(0).getAttributes(source, target);
    }

    @Override
    public IAlgebraOperator clone() {
        Select clone = (Select) super.clone();
        clone.selections = new ArrayList<Expression>();
        for (Expression selection : this.selections) {
            clone.selections.add(selection.clone());
        }
        return clone;
    }

    class SelectTupleIterator implements ITupleIterator {

        private ITupleIterator tableIterator;
        private Tuple nextTuple;

        public SelectTupleIterator(ITupleIterator tableIterator) {
            this.tableIterator = tableIterator;
        }

        public boolean hasNext() {
            if (nextTuple != null) {
                return true;
            } else {
                loadNextTuple();
                return nextTuple != null;
            }
        }

        private void loadNextTuple() {
            while (tableIterator.hasNext()) {
                Tuple tuple = tableIterator.next();
                if (conditionsAreTrue(tuple, selections)) {
                    nextTuple = tuple;
                    return;
                }
            }
            nextTuple = null;
        }

        private boolean conditionsAreTrue(Tuple tuple, List<Expression> selections) {
            if (logger.isDebugEnabled()) logger.debug("Evaluating conditions: " + selections + " on tuple\n" + tuple);
            for (Expression condition : selections) {
                if (expressionEvaluator.evaluateCondition(condition, tuple) != BartConstants.TRUE) {
                    if (logger.isDebugEnabled()) logger.debug("Condition is false");
                    return false;
                }
            }
            return true;
        }

        public Tuple next() {
            if (nextTuple != null) {
                Tuple result = nextTuple;
                nextTuple = null;
                return result;
            }
            return null;
        }

        public void reset() {
            this.tableIterator.reset();
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

        public void close() {
            tableIterator.close();
        }
    }
}
