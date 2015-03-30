package bart.model.detection.operator;

import bart.model.algebra.operators.GenerateTupleFromTuplePair;
import bart.model.database.Cell;
import bart.model.database.CellRef;
import bart.model.database.Tuple;
import bart.model.errorgenerator.CellChange;
import bart.model.errorgenerator.EquivalenceClass;
import bart.model.errorgenerator.VioGenQuery;
import bart.model.errorgenerator.ViolationContext;
import bart.model.errorgenerator.operator.GenerateChangesAndContexts;
import bart.utility.BartUtility;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EstimateRepairabilityAPriori {

    private static Logger logger = LoggerFactory.getLogger(EstimateRepairabilityAPriori.class);

    private EstimateRepairabilityForCellChange changeRepairabilityEstimator = new EstimateRepairabilityForCellChange();
    private GenerateTupleFromTuplePair tupleMerger = new GenerateTupleFromTuplePair();
    private GenerateChangesAndContexts changesGenerator = new GenerateChangesAndContexts();

    public void estimateRepairabilityFromGeneratingContext(List<CellChange> changes, VioGenQuery vioGenQuery) {
        ViolationContext changesContexts = mergeGeneratingContexts(changes, vioGenQuery);
        if (logger.isDebugEnabled()) logger.debug("Original cells in generating contexts: " + changesContexts);
        applyChangesInContext(changes, changesContexts);
        if (logger.isDebugEnabled()) logger.debug("Generating context for changes " + changes + ":\n\t" + changesContexts);
        for (CellChange change : changes) {
            double repairability = changeRepairabilityEstimator.computeRepairability(change, changesContexts);
            change.setRepairabilityAPriori(repairability);
        }
    }

    private ViolationContext mergeGeneratingContexts(List<CellChange> changes, VioGenQuery vioGenQuery) {
        ViolationContext context = new ViolationContext();
        context.setDependencyId(vioGenQuery.getDependency().getId());
        for (CellChange change : changes) {
            ViolationContext contextForChange = change.getGeneratingContext();
            context.addAllCells(contextForChange.getCells());
        }
        return context;
    }

    public void estimateRepairabilityInEquivalenceClass(List<CellChange> changes, EquivalenceClass equivalenceClass, VioGenQuery vioGenQuery) {
        if (vioGenQuery.getVioGenComparison().isInequalityComparison()) {
            estimateRepairabilityFromGeneratingContext(changes, vioGenQuery);
            return;
        }
        if (logger.isDebugEnabled()) logger.debug("Estimating apriori repairability for changes " + changes + " in equivalence class\n\t" + equivalenceClass);
        ViolationContext equivalenceClassContext = generateEquivalenceClassContext(equivalenceClass, vioGenQuery);
        if (logger.isDebugEnabled()) logger.debug("Original cells in equivalence class: " + equivalenceClassContext);
        applyChangesInContext(changes, equivalenceClassContext);
        if (logger.isDebugEnabled()) logger.debug("Violation context for equivalence class: " + equivalenceClassContext);
        for (CellChange change : changes) {
            double repairability = changeRepairabilityEstimator.computeRepairability(change, equivalenceClassContext);
            change.setRepairabilityAPriori(repairability);
        }
    }

    private ViolationContext generateEquivalenceClassContext(EquivalenceClass equivalenceClass, VioGenQuery vioGenQuery) {
        ViolationContext context = new ViolationContext();
        context.setDependencyId(vioGenQuery.getDependency().getId());
        for (int i = 0; i < equivalenceClass.getTuples().size() - 1; i++) {
            Tuple firstTuple = equivalenceClass.getTuples().get(i);
            for (int j = i + 1; j < equivalenceClass.getTuples().size(); j++) {
                Tuple secondTuple = equivalenceClass.getTuples().get(j);
                Tuple mergedTuple = tupleMerger.generateTuple(firstTuple, secondTuple);
                ViolationContext contextForTuple = changesGenerator.buildVioContext(vioGenQuery.getFormula(), mergedTuple, vioGenQuery.getDependency().getId());
                context.addAllCells(contextForTuple.getCells());
            }
        }
        return context;
    }

    private void applyChangesInContext(List<CellChange> changes, ViolationContext changesContexts) {
        Set<Cell> updatedCells = generateUpdatedCells(changes);
        if (logger.isDebugEnabled()) logger.debug("Updated cells: " + updatedCells);
        Set<CellRef> updatedCellRefs = extractCellRefs(updatedCells);
        for (Iterator<Cell> iterator = changesContexts.getCells().iterator(); iterator.hasNext();) {
            Cell cell = iterator.next();
            CellRef cellRef = BartUtility.getCellRefNoAlias(cell);
            if (updatedCellRefs.contains(cellRef)) {
                iterator.remove();
            }
        }
        changesContexts.addAllCells(updatedCells);
    }

    private Set<Cell> generateUpdatedCells(List<CellChange> changes) {
        Set<Cell> result = new HashSet<Cell>();
        for (CellChange change : changes) {
            Cell changedCell = new Cell(new CellRef(change.getCell()), change.getNewValue());
            result.add(changedCell);
        }
        return result;
    }

    private Set<CellRef> extractCellRefs(Set<Cell> updatedCells) {
        Set<CellRef> result = new HashSet<CellRef>();
        for (Cell cell : updatedCells) {
            result.add(BartUtility.getCellRefNoAlias(cell));
        }
        return result;
    }

}
