package bart.model.errorgenerator;

import bart.BartConstants;
import bart.model.VioGenQueryConfiguration;
import speedy.model.algebra.IAlgebraOperator;
import bart.model.dependency.ComparisonAtom;
import bart.model.dependency.Dependency;
import bart.model.dependency.IFormula;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;

public class VioGenQuery {

    private Dependency dependency;
    private IFormula formula;
    private ComparisonAtom vioGenComparison;
    private IAlgebraOperator query;
    private VioGenQueryConfiguration configuration;

    public VioGenQuery(Dependency dependency, IFormula formula, ComparisonAtom vioGenComparison, VioGenQueryConfiguration configuration) {
        this.dependency = dependency;
        this.formula = formula;
        this.vioGenComparison = vioGenComparison;
        this.configuration = configuration;
    }

    public VioGenQueryConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(VioGenQueryConfiguration configuration) {
        this.configuration = configuration;
    }

    public IFormula getFormula() {
        return formula;
    }

    public ComparisonAtom getVioGenComparison() {
        return vioGenComparison;
    }

    public IAlgebraOperator getQuery() {
        return query;
    }

    public void setQuery(IAlgebraOperator query) {
        this.query = query;
    }

    public Dependency getDependency() {
        return dependency;
    }

    @Override
    public String toString() {
        return "VioGenQuery for dependency " + dependency.getId() + "\n"
                + formula.getPositiveFormula() + "\n  comparison=" + vioGenComparison;
    }

    public String getId() {
        StringBuilder sb = new StringBuilder();
        sb.append(dependency.getId());
        sb.append("[").append(vioGenComparison).append("]");
        return sb.toString();
    }

    public String toShortString() {
        StringBuilder sb = new StringBuilder();
        sb.append(dependency.getId());
        if (this.getFormula().getFormulaWithAdornments() != null) {
            sb.append("(");
            for (String comparison : this.getFormula().getFormulaWithAdornments().getAdornments().values()) {
                sb.append(comparison).append(",");
            }
            BartUtility.removeChars(",".length(), sb);
            sb.append(")");
        }
        if (DependencyUtility.hasOnlyVariableInequalities(this)) {
            sb.append("* ");
        }
        sb.append("[").append(vioGenComparison).append("]");
        return sb.toString();
    }

    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VioGenQuery for dependency ").append(dependency.getId()).append("\n");
        sb.append(formula.getPositiveFormula().toLongString()).append("\n");
        sb.append(BartConstants.INDENT).append("Comparison: ").append(vioGenComparison).append("\n");
        sb.append(BartConstants.INDENT).append("Query: ").append(query).append("\n");
        return sb.toString();
    }

}
