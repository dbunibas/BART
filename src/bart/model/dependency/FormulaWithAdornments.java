package bart.model.dependency;

import bart.BartConstants;
import bart.model.errorgenerator.EquivalenceClassQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormulaWithAdornments {

    private IFormula formula;
    private Map<FormulaVariable, String> adornments = new HashMap<FormulaVariable, String>();
    private List<EquivalenceClassQuery> equivalenceClassQueries;

    public FormulaWithAdornments(IFormula formula) {
        this.formula = formula;
    }

    public IFormula getFormula() {
        return formula;
    }

    public Map<FormulaVariable, String> getAdornments() {
        return adornments;
    }

    public void addAdornment(FormulaVariable variable, String adornment) {
        this.adornments.put(variable, adornment);
    }

    public List<EquivalenceClassQuery> getEquivalenceClassQueries() {
        return equivalenceClassQueries;
    }

    public void setEquivalenceClassQueries(List<EquivalenceClassQuery> equivalenceClassQueries) {
        this.equivalenceClassQueries = equivalenceClassQueries;
    }

    boolean hasEqualityAdornments() {
        for (String operator : adornments.values()) {
            if (operator.equals(BartConstants.EQUAL)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "FormulaWithAdornments: " + formula + ",\n\tadornments:" + adornments;
    }

}
