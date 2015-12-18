package bart.model.dependency.operators;

import bart.model.dependency.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignAliasesInFormulas {

    public void assignAliases(Dependency dependency) {
        AssignAliasesVisitor visitor = new AssignAliasesVisitor();
        dependency.accept(visitor);
    }
}

class AssignAliasesVisitor implements IFormulaVisitor {

    private int differenceId = -1;

    public void visitDependency(Dependency dependency) {
        dependency.getPremise().accept(this);
        this.differenceId = -1;
        dependency.getConclusion().accept(this);
    }

    public void visitPositiveFormula(PositiveFormula formula) {
        Map<String, List<RelationalAtom>> atomMap = new HashMap<String, List<RelationalAtom>>();
        for (IFormulaAtom atom : formula.getAtoms()) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                relationalAtom.addAlias((differenceId > 0 ? "D" + differenceId : ""));
                String tableName = relationalAtom.getTableName();
                List<RelationalAtom> atomsWithSameName = atomMap.get(tableName);
                if (atomsWithSameName == null) {
                    atomsWithSameName = new ArrayList<RelationalAtom>();
                    atomMap.put(tableName, atomsWithSameName);
                }
                atomsWithSameName.add((RelationalAtom) atom);
            }
        }
        for (List<RelationalAtom> atoms : atomMap.values()) {
            if (atoms.size() > 1) {
                int counter = 1;
                for (RelationalAtom relationalAtom : atoms) {
                    relationalAtom.addAlias("" + counter++);
                }
            }
        }
    }

    public void visitFormulaWithNegations(FormulaWithNegations formula) {
        this.differenceId++;
        formula.getPositiveFormula().accept(this);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            negatedFormula.accept(this);
        }
    }

    public Object getResult() {
        return null;
    }
}
