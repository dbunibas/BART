package bart.model.errorgenerator.operator;

import bart.model.EGTask;
import bart.model.EGTaskConfiguration;
import bart.model.VioGenQueryConfiguration;
import bart.model.dependency.ComparisonAtom;
import bart.model.dependency.Dependency;
import bart.model.dependency.IFormulaAtom;
import bart.model.dependency.analysis.FindCrossProductFormulas;
import bart.model.dependency.operators.FindVariableEquivalenceClasses;
import bart.model.dependency.analysis.FindFormulaWithAdornments;
import bart.model.errorgenerator.VioGenQuery;
import speedy.model.expressions.Expression;
import bart.utility.BartUtility;
import bart.utility.DependencyUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nfunk.jep.SymbolTable;
import org.nfunk.jep.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DAOException;

public class GenerateVioGenQueries {

    private static Logger logger = LoggerFactory.getLogger(GenerateVioGenQueries.class);
    private FindFormulaWithAdornments symmetryFinder = new FindFormulaWithAdornments();
    private FindCrossProductFormulas crossProductFinder = new FindCrossProductFormulas();
    private FindVariableEquivalenceClasses equivalenceClassFinder = new FindVariableEquivalenceClasses();

    public List<VioGenQuery> generateVioGenQueries(Dependency dependency, EGTask task) {
        if (logger.isDebugEnabled()) logger.debug("Generating VioGen Queries for dependency " + dependency);
        List<VioGenQuery> vioGenQueries = new ArrayList<VioGenQuery>();
        for (int i = 0; i < dependency.getPremise().getAtoms().size(); i++) {
            IFormulaAtom atom = dependency.getPremise().getAtoms().get(i);
            if (!(atom instanceof ComparisonAtom)) {
                continue;
            }
            ComparisonAtom originalComparison = (ComparisonAtom) dependency.getPremise().getAtoms().get(i);
            if (logger.isInfoEnabled()) logger.info("Original atom:" + originalComparison.toLongString());
            Dependency invertedDipendency = dependency.clone();
            ComparisonAtom invertedComparison = (ComparisonAtom) invertedDipendency.getPremise().getAtoms().get(i);
            if (logger.isInfoEnabled()) logger.info("Cloned atom:" + invertedComparison.toLongString());
            invertComparison(invertedComparison);
            VioGenQueryConfiguration defaultVioGenQueryConfiguration = task.getConfiguration().getDefaultVioGenQueryConfiguration().clone();
            if (!task.getConfiguration().isGenerateAllChanges()) {
                double percentage = findRandomPercentage(dependency, invertedComparison, task.getConfiguration());
                defaultVioGenQueryConfiguration.setPercentage(percentage);
            }
            String executor = findQueryExecutor(dependency, originalComparison, task.getConfiguration());
            if (executor != null) {
                defaultVioGenQueryConfiguration.setQueryExecutor(executor);
            }
            VioGenQuery vioGenQuery = new VioGenQuery(dependency, invertedDipendency.getPremise(), invertedComparison, defaultVioGenQueryConfiguration);
            equivalenceClassFinder.findVariableEquivalenceClasses(invertedDipendency.getPremise());
            symmetryFinder.findFormulaWithAdornments(vioGenQuery.getFormula().getPositiveFormula(), task);
            crossProductFinder.findCrossProductFormulas(vioGenQuery.getFormula().getPositiveFormula(), task);
            if (logger.isDebugEnabled()) logger.debug("VioGen query: " + vioGenQuery.toLongString());
            vioGenQueries.add(vioGenQuery);
        }
        return vioGenQueries;
    }

    public void setErrorPercentages(EGTask task) {
        List<VioGenQuery> vioGenQueries = new ArrayList<VioGenQuery>();
        for (Dependency dc : task.getDCs()) {
            vioGenQueries.addAll(dc.getVioGenQueries());
        }
        setErrorPercentageConfiguration(vioGenQueries, task.getConfiguration());
    }

    private void invertComparison(ComparisonAtom comparison) {
        if (logger.isInfoEnabled()) logger.info("Inverting comparison " + comparison.toString());
        String invertedOperator = DependencyUtility.invertOperator(comparison.getOperator());
        StringBuilder stringExpression = new StringBuilder();
        stringExpression.append(comparison.getLeftArgument());
        stringExpression.append(invertedOperator);
        stringExpression.append(comparison.getRightArgument());
        Expression newExpression = new Expression(stringExpression.toString());
        SymbolTable originalSymbolTable = comparison.getExpression().getJepExpression().getSymbolTable();
        for (Variable variable : originalSymbolTable.getVariables()) {
            newExpression.setVariableDescription(variable.getName(), variable.getDescription());
        }
        comparison.setOperator(invertedOperator);
        comparison.setExpression(newExpression);
        if (logger.isInfoEnabled()) logger.info("Inverted comparison " + comparison.toString());
    }

    private double findRandomPercentage(Dependency dependency, ComparisonAtom comparison, EGTaskConfiguration configuration) {
        String vioGenKey = BartUtility.getVioGenQueryKey(dependency.getId(), comparison.toString());
        double percentage = configuration.getDefaultVioGenQueryConfiguration().getPercentage();
        if (configuration.getVioGenQueryProbabilities().containsKey(vioGenKey)) {
            percentage = configuration.getVioGenQueryProbabilities().get(vioGenKey);
        }
        return percentage;
    }

    private String findQueryExecutor(Dependency dependency, ComparisonAtom comparison, EGTaskConfiguration configuration) {
        String vioGenKey = BartUtility.getVioGenQueryKey(dependency.getId(), comparison.toString());
        return configuration.getVioGenQueryStrategy().get(vioGenKey);
    }

    private void setErrorPercentageConfiguration(List<VioGenQuery> vioGenQueries, EGTaskConfiguration configuration) {
        Map<String, VioGenQuery> queriesMap = generateVioGenQueryMap(vioGenQueries);
        if (logger.isInfoEnabled()) logger.info("Setting error percentages: " + BartUtility.printMap(configuration.getVioGenQueryProbabilities()));
        for (String vioGenConfiguration : configuration.getVioGenQueryProbabilities().keySet()) {
            double percentage = configuration.getVioGenQueryProbabilities().get(vioGenConfiguration);
            VioGenQuery query = queriesMap.get(vioGenConfiguration);
            if (query == null) {
                throw new DAOException("Configuration error! An unknow vioGenQuery is defined in the errorPercentages section.\n"
                        + "Unknow vioGenQuery: " + vioGenConfiguration + "\n"
                        + "Valid vioGenQueries are \n" + BartUtility.printCollectionSorted(queriesMap.keySet(), "\t"));
            }
            query.getConfiguration().setPercentage(percentage);
        }
    }

    private Map<String, VioGenQuery> generateVioGenQueryMap(List<VioGenQuery> vioGenQueries) {
        Map<String, VioGenQuery> result = new HashMap<String, VioGenQuery>();
        for (VioGenQuery vioGenQuery : vioGenQueries) {
            String key = generateKey(vioGenQuery);
            result.put(key, vioGenQuery);
        }
        return result;
    }

    private String generateKey(VioGenQuery vioGenQuery) {
        ComparisonAtom invertedComparison = vioGenQuery.getVioGenComparison().clone();
        invertComparison(invertedComparison);
        return vioGenQuery.getDependency().getId() + " " + invertedComparison.toString();
    }

}
