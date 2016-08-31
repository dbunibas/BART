package bart.test.operator.generator;

import bart.test.utility.CellChangeComparator;
import bart.model.EGTask;
import bart.model.dependency.Dependency;
import bart.model.errorgenerator.VioGenQueryCellChange;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ICellChange;
import bart.model.errorgenerator.VioGenQuery;
import bart.model.errorgenerator.operator.*;
import bart.model.errorgenerator.operator.IVioGenQueryExecutor;
import bart.model.errorgenerator.operator.valueselectors.TypoAppendString;
import speedy.persistence.relational.QueryStatManager;
import bart.test.utility.UtilityTest;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCheckSymmetricVioGenQuery extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TestCheckSymmetricVioGenQuery.class);
    private GenerateVioGenQueries vioGenQueriesGenerator = new GenerateVioGenQueries();
    private IVioGenQueryExecutor vioGenQueryExecutor = new ExecuteVioGenQueryStandard();
    private IVioGenQueryExecutor vioGenQueryExecutorForSymmetricFormula = new ExecuteVioGenQuerySymmetric();
    private EGTask task;

    @Override
    public void setUp() {
//        task = UtilityTest.loadEGTaskFromResources("employees/employees-symmetrictest-egtask.xml");
        task = UtilityTest.loadEGTaskFromResources("employees/employees-symmetrictest-dbms-egtask.xml");
        task.getConfiguration().setAvoidInteractions(false);
        task.getConfiguration().setGenerateAllChanges(true);
        task.getConfiguration().setDefaultDirtyStrategy(new TypoAppendString("*", 3));
//        task.getConfiguration().setMaxNumberOfInequalitiesInSymmetricQueries(1);
//        if (logger.isDebugEnabled()) logger.debug(task.toString());
    }

    public void testExecuteAllVioGenQueries() {
        if (logger.isTraceEnabled()) logger.trace(task.getTarget().printInstances());
        for (Dependency dc : task.getDCs()) {
//            if (!dc.getId().equals("e9")) {
//                continue;
//            }
            dc.setVioGenQueries(vioGenQueriesGenerator.generateVioGenQueries(dc, task));
            for (VioGenQuery vioQuery : dc.getVioGenQueries()) {
                if (!vioQuery.getFormula().isSymmetric() || DependencyUtility.hasOnlyVariableInequalities(vioQuery)) {
                    continue;
                }
                if (logger.isDebugEnabled()) logger.debug("\nTesting " + vioQuery);
                List<VioGenQueryCellChange> symmetricChanges = executeSymmetricOptimization(vioQuery);
                List<VioGenQueryCellChange> standardChanges = executeNoSymmetricOptimization(vioQuery);
                if (!areEquals(standardChanges, symmetricChanges)) {
                    logger.error(" -- NonSymmetric Changes --\n" + standardChanges.size());
                    logger.error(" -- Symmetric Changes --\n" + symmetricChanges.size());
                    logger.error(" -- NonSymmetric Changes --\n" + BartUtility.printCellChangesToLongString(standardChanges));
                    logger.error(" -- Symmetric Changes --\n" + BartUtility.printCellChangesToLongString(symmetricChanges));
                    Assert.fail();
                }
            }
        }
    }

    public List<VioGenQueryCellChange> executeSymmetricOptimization(VioGenQuery vioGenQuery) {
        long start = new Date().getTime();
        CellChanges symmetricCellChanges = new CellChanges();
        vioGenQueryExecutorForSymmetricFormula.execute(vioGenQuery, symmetricCellChanges, task);
        long end = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Symmetric execution time " + (end - start) + " ms");
        if (logger.isDebugEnabled()) logger.debug("Symmetric changes: " + symmetricCellChanges.getChanges().size());
//        if (logger.isDebugEnabled()) logger.debug("Symmetric RAM: " + UtilityTest.getMemInfo());
        QueryStatManager.getInstance().printStatistics("Symmetric\n");
        List<VioGenQueryCellChange> changes = createList(symmetricCellChanges.getChanges());
        Collections.sort(changes, new CellChangeComparator());
        return changes;
    }

    public List<VioGenQueryCellChange> executeNoSymmetricOptimization(VioGenQuery vioGenQuery) {
//        vioGenQueryExecutor.initializeQueryForSymmetricFormula(vioGenQuery, task);
        long start = new Date().getTime();
        CellChanges nonSymmetricChanges = new CellChanges();
        vioGenQueryExecutor.execute(vioGenQuery, nonSymmetricChanges, task);
        long end = new Date().getTime();
        if (logger.isDebugEnabled()) logger.debug("Non Symmetric execution time " + (end - start) + " ms");
        if (logger.isDebugEnabled()) logger.debug("Non Symmetric changes: " + nonSymmetricChanges.getChanges().size());
//        if (logger.isDebugEnabled()) logger.debug("Non Symmetric RAM: " + UtilityTest.getMemInfo());
        QueryStatManager.getInstance().printStatistics("Non Symmetric\n");
        List<VioGenQueryCellChange> changes = createList(nonSymmetricChanges.getChanges());
        Collections.sort(changes, new CellChangeComparator());
        return changes;
    }

    private boolean areEquals(List<VioGenQueryCellChange> changes, List<VioGenQueryCellChange> symmetricChanges) {
        if (changes.size() != symmetricChanges.size()) {
            return false;
        }
        List<VioGenQueryCellChange> copy = new ArrayList<VioGenQueryCellChange>(changes);
        copy.removeAll(symmetricChanges);
        return copy.isEmpty();
    }

    private List<VioGenQueryCellChange> createList(Set<ICellChange> changes) {
        List<VioGenQueryCellChange> list = new ArrayList<VioGenQueryCellChange>();
        Iterator<ICellChange> iterator = changes.iterator();
        while (iterator.hasNext()) {
            list.add((VioGenQueryCellChange) iterator.next());
        }
        return list;

    }

}
