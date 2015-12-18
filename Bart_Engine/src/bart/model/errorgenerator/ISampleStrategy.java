package bart.model.errorgenerator;

import bart.model.EGTask;
import bart.model.VioGenQueryConfiguration;
import speedy.model.algebra.IAlgebraOperator;
import speedy.model.database.TableAlias;
import java.util.Set;

public interface ISampleStrategy {

//    public SampleParameters computeParameters(IAlgebraOperator operator, IFormula formula, String queryType, int sampleSize, VioGenQueryConfiguration queryConfiguration, EGTask task);
    public SampleParameters computeParameters(IAlgebraOperator operator, Set<TableAlias> tableInFormula, String queryType, int sampleSize, VioGenQueryConfiguration queryConfiguration, EGTask task);
}
