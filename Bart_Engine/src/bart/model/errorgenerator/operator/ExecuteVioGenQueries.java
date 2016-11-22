package bart.model.errorgenerator.operator;

import bart.BartConstants;
import bart.model.EGTask;
import bart.model.dependency.Dependency;
import bart.model.dependency.DependencyStratification;
import bart.model.dependency.operators.GenerateStratification;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.OrderingAttribute;
import bart.model.errorgenerator.VioGenQuery;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import bart.utility.ErrorGeneratorStats;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.thread.IBackgroundThread;
import speedy.model.thread.ThreadManager;

public class ExecuteVioGenQueries {

    private final static Logger logger = LoggerFactory.getLogger(ExecuteVioGenQueries.class);
    private ExecutePartialOrderErrors partialOrderErrorExecutor = new ExecutePartialOrderErrors();
    private SelectQueryExecutor executorSelector = new SelectQueryExecutor();
    private GenerateStratification stratificationGenerator = new GenerateStratification();

    public CellChanges executeVioGenQueries(EGTask task) {
        if (task.getConfiguration().isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        if (task.getConfiguration().isPrintLog()) System.out.println("*** Step 2: Executing vioGen queries");
        if (task.getConfiguration().isPrintLog()) System.out.println(BartConstants.PRINT_SEPARATOR);
        DependencyStratification stratification = stratificationGenerator.generate(task.getDCs(), task);
        if (logger.isDebugEnabled()) logger.debug("Stratification " + stratification.toString());
        CellChanges allCellChanges = new CellChanges();
        ThreadManager threadManager = new ThreadManager(task.getConfiguration().getMaxNumberOfThreads());
        for (Set<Dependency> stratum : stratification.getStrata()) {
            ExecuteStratumThread thread = new ExecuteStratumThread(task, allCellChanges, stratum);
            threadManager.startThread(thread);
        }
        threadManager.waitForActiveThread();
        if (logger.isDebugEnabled()) logger.debug(allCellChanges.toString());
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.NUMBER_CHANGES, allCellChanges.getChanges().size());
        return allCellChanges;
    }

    class ExecuteStratumThread implements IBackgroundThread {

        private EGTask task;
        private CellChanges allCellChanges;
        private Set<Dependency> stratum;

        public ExecuteStratumThread(EGTask task, CellChanges allCellChanges, Set<Dependency> stratum) {
            this.task = task;
            this.allCellChanges = allCellChanges;
            this.stratum = stratum;
        }

        public void execute() {
            for (Dependency dc : stratum) {
                for (VioGenQuery vioGenQuery : dc.getVioGenQueries()) {
                    if (task.getConfiguration().isExcludeCrossProducts() && DependencyUtility.isCrossProduct(vioGenQuery.getFormula())) {
                        if (task.getConfiguration().isDebug()) System.out.println("Skipping cross product: " + vioGenQuery.toShortString());
                        if (logger.isDebugEnabled()) logger.debug("Skipping cross product: " + vioGenQuery.toShortString());
                        continue;
                    }
                    long start = new Date().getTime();
                    IVioGenQueryExecutor executor = executorSelector.getExecutorForVioGenQuery(vioGenQuery, task);
                    int changes = executor.execute(vioGenQuery, allCellChanges, task);
                    executePartialOrderChanges(task, allCellChanges, dc);
                    long end = new Date().getTime();
                    ErrorGeneratorStats.getInstance().addVioGenQueryTime(vioGenQuery, end - start);
                    ErrorGeneratorStats.getInstance().addVioGenQueryErrors(vioGenQuery, changes);
                }
            }
        }

    }

    private void executePartialOrderChanges(EGTask task, CellChanges cellChanges, Dependency dc) {
        if (!task.getConfiguration().containsOrderingAttributes()) return;
        Map<String, OrderingAttribute> vioGenOrderingAttributes = task.getConfiguration().getVioGenOrderingAttributes();
        OrderingAttribute orderingAttribute = vioGenOrderingAttributes.get(dc.getId());
        if (orderingAttribute == null) return;
        CellChanges partialOrderCellChanges = partialOrderErrorExecutor.execute(task, cellChanges, dc);
        BartUtility.mergeChanges(cellChanges, partialOrderCellChanges);
        ErrorGeneratorStats.getInstance().addStat(ErrorGeneratorStats.NUMBER_CHANGES, cellChanges.getChanges().size());
    }
}
