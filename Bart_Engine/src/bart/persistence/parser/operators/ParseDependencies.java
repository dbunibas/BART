package bart.persistence.parser.operators;

import bart.model.EGTask;
import speedy.model.database.IDatabase;
import speedy.model.database.TableAlias;
import bart.model.dependency.*;
import bart.model.dependency.operators.AssignAliasesInFormulas;
import bart.model.dependency.operators.CheckVariablesInExpressions;
import bart.model.dependency.operators.FindFormulaVariables;
import bart.model.dependency.operators.FindVariableEquivalenceClasses;
import bart.persistence.parser.output.DependenciesLexer;
import bart.persistence.parser.output.DependenciesParser;
import java.util.ArrayList;
import java.util.List;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.ParserException;

@SuppressWarnings("unchecked")
public class ParseDependencies {

    public final static String NULL = "#NULL#";
    private static Logger logger = LoggerFactory.getLogger(ParseDependencies.class);

    private FindVariableEquivalenceClasses equivalenceClassFinder = new FindVariableEquivalenceClasses();
    private AssignAliasesInFormulas aliasAssigner = new AssignAliasesInFormulas();
    private CheckVariablesInExpressions checker = new CheckVariablesInExpressions();
    private FindFormulaVariables variableFinder = new FindFormulaVariables();

    private List<Dependency> dcs = new ArrayList<Dependency>();
    private EGTask task;

    public void generateDependencies(String text, EGTask task) throws Exception {
        try {
            this.task = task;
            DependenciesLexer lex = new DependenciesLexer(new ANTLRStringStream(text));
            CommonTokenStream tokens = new CommonTokenStream(lex);
            DependenciesParser g = new DependenciesParser(tokens);
            try {
                g.setGenerator(this);
                g.prog();
            } catch (RecognitionException ex) {
                logger.error("Unable to load mapping task: " + ex.getMessage());
                throw new ParserException(ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
            throw new ParserException(e);
        }
    }

    public void addDC(Dependency d) {
        if (!(d.getConclusion() instanceof NullFormula)) {
            throw new ParserException("DC must have no conclusion");
        }
        IDatabase source = task.getSource();
        List<String> sourceTables = source.getTableNames();
        IDatabase target = task.getTarget();
        List<String> targetTables = target.getTableNames();
        checkAtomsForDCs(sourceTables, targetTables, d.getPremise());
        this.dcs.add(d);
    }

    private void checkAtomsForDCs(List<String> sourceTables, List<String> targetTables, IFormula formula) {
        StringBuilder errors = new StringBuilder();
        for (IFormulaAtom atom : formula.getAtoms()) {
            if ((atom.isRelational())) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                if (sourceTables.contains(relationalAtom.getTableName())) {
                    relationalAtom.setSource(true);
                } else if (targetTables.contains(relationalAtom.getTableName())) {
                    relationalAtom.setSource(false);
                } else {
                    errors.append("Table not allowed in formula: ").append(relationalAtom.getTableName()).append(" - ").append(sourceTables).append(" - ").append(targetTables).append("\n");
                }
            }
            if(atom.isBuiltIn()){
                throw new IllegalArgumentException("BuiltIn comparison are not allowed");
            }
        }
        if (!errors.toString().isEmpty()) {
            throw new ParserException(errors.toString());
        }
    }


// final callback method for processing tgds
    public void processDependencies() {
        for (Dependency dc : dcs) {
            processDependency(dc);
        }
        task.setDCs(dcs);
    }

    public String clean(String expressionString) {
        String result = expressionString.trim();
        result = result.replaceAll("\\$", "");
        return result.substring(1, result.length() - 1);
    }

    private void processDependency(Dependency dependency) {
        assignAuthoritativeSources(dependency);
        aliasAssigner.assignAliases(dependency);
        variableFinder.findVariables(dependency, task.getSource().getTableNames(), task.getAuthoritativeSources());
        checker.checkVariables(dependency);
        equivalenceClassFinder.findVariableEquivalenceClasses(dependency);
    }

    private void assignAuthoritativeSources(Dependency dependency) {
        for (IFormulaAtom formulaAtom : dependency.getPremise().getAtoms()) {
            if (!(formulaAtom instanceof RelationalAtom)) {
                continue;
            }
            TableAlias tableAlias = ((RelationalAtom) formulaAtom).getTableAlias();
            if (task.getAuthoritativeSources().contains(tableAlias.getTableName())) {
                tableAlias.setAuthoritative(true);
            }
        }
    }
}
