package bart.model.dependency;

import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.TableAlias;

public class FormulaSampling {

    private PositiveFormula originalFormula;
    private PositiveFormula samplingFormula;
    private TableAlias tableAlias;
    private IAlgebraOperator samplingQuery;

    public FormulaSampling(PositiveFormula originalFormula, PositiveFormula samplingFormula, TableAlias tableAlias) {
        this.originalFormula = originalFormula;
        this.samplingFormula = samplingFormula;
        this.tableAlias = tableAlias;
    }

    public PositiveFormula getOriginalFormula() {
        return originalFormula;
    }

    public PositiveFormula getSamplingFormula() {
        return samplingFormula;
    }

    public TableAlias getTableAlias() {
        return tableAlias;
    }

    public IAlgebraOperator getSamplingQuery() {
        return samplingQuery;
    }

    public void setSamplingQuery(IAlgebraOperator samplingQuery) {
        this.samplingQuery = samplingQuery;
    }

    @Override
    public String toString() {
        return "FormulaSampling:\n" + "Original Formula:" + originalFormula + "\nSamplingFormula=" + samplingFormula + "\n\tTableAlias=" + tableAlias;
    }

}
