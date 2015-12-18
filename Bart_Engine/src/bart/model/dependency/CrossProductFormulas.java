package bart.model.dependency;

import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.TableAlias;
import bart.utility.BartUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrossProductFormulas {

    private Map<TableAlias, IAlgebraOperator> crossProducts = new HashMap<TableAlias, IAlgebraOperator>();
    private List<ComparisonAtom> inequalityComparisons = new ArrayList<ComparisonAtom>();

    public List<TableAlias> getTableAliasInCrossProducts() {
        return new ArrayList<TableAlias>(crossProducts.keySet());
    }

    public IAlgebraOperator getCrossProductAlgebraOperator(TableAlias tableAlias) {
        return this.crossProducts.get(tableAlias);
    }

    public void addCrossProduct(TableAlias tableAlias, IAlgebraOperator crossProductAlgebraOperator) {
        this.crossProducts.put(tableAlias, crossProductAlgebraOperator);
    }

    public List<ComparisonAtom> getInequalityComparisons() {
        return inequalityComparisons;
    }

    public void addInequalityComparison(ComparisonAtom inequalityComparison) {
        this.inequalityComparisons.add(inequalityComparison);
    }

    @Override
    public String toString() {
        return "CrossProductFormulas:\n" + BartUtility.printMap(crossProducts) + "\ninequalityComparisons: " + inequalityComparisons;
    }

}
