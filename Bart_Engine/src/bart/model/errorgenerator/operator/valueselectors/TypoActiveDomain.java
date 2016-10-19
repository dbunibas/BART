package bart.model.errorgenerator.operator.valueselectors;

import bart.OperatorFactory;
import bart.model.EGTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.Distinct;
import speedy.model.algebra.Limit;
import speedy.model.algebra.Project;
import speedy.model.algebra.Scan;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.AttributeRef;
import speedy.model.database.IValue;
import speedy.model.database.TableAlias;
import speedy.model.database.Tuple;
import speedy.model.database.operators.IRunQuery;
import speedy.utility.SpeedyUtility;

public class TypoActiveDomain implements IDirtyStrategy {

    private final static Logger logger = LoggerFactory.getLogger(TypoActiveDomain.class);
    private AttributeRef attributeRef;
    private List<IValue> activeDomains;

    public TypoActiveDomain(AttributeRef attributeRef) {
        this.attributeRef = attributeRef;
    }

    public IValue generateNewValue(IValue value, EGTask egTask) {
        if (activeDomains == null) {
            loadActiveDomainValues(egTask);
        }
        if (activeDomains.size() < 2) {
            throw new IllegalArgumentException("Unable to apply an active domain typo error. Too few different values.");
        }
        while (true) {
            int randomNumber = new Random().nextInt(activeDomains.size());
            IValue newValue = activeDomains.get(randomNumber);
            if (newValue.equals(value)) {
                continue;
            }
            return newValue;
        }
    }

    private void loadActiveDomainValues(EGTask egTask) {
        if (logger.isDebugEnabled()) logger.debug("Loading active domain values for attribute " + attributeRef);
        Set<IValue > values = new HashSet<IValue>();
        Limit limit = new Limit(1000);
        Distinct distinct = new Distinct();
        Project project = new Project(SpeedyUtility.createProjectionAttributes(Arrays.asList(new AttributeRef[]{attributeRef})));
        Scan scan = new Scan(new TableAlias(attributeRef.getTableName()));
        limit.addChild(distinct);
        distinct.addChild(project);
        project.addChild(scan);
        IRunQuery queryRunner = OperatorFactory.getInstance().getQueryRunner(egTask);
        ITupleIterator it = queryRunner.run(limit, egTask.getSource(), egTask.getTarget());
        while (it.hasNext()) {
            Tuple tuple = it.next();
            values.add(tuple.getCell(attributeRef).getValue());
        }
        it.close();
        activeDomains = new ArrayList<IValue>(values);
    }

}
