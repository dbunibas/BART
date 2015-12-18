package bart.model.dependency;

import bart.model.dependency.operators.IFormulaVisitor;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class NullFormula extends PositiveFormula {

    private static NullFormula singleton = new NullFormula();

    public static NullFormula getInstance() {
        return singleton;
    }

    private NullFormula() {
    }

    @Override
    public void accept(IFormulaVisitor visitor) {
        super.accept(visitor);
    }

    @Override
    public void addAtom(IFormulaAtom a) {
    }

    @Override
    public boolean addLocalVariable(FormulaVariable e) {
        return false;
    }

    @Override
    public void addNegatedFormula(IFormula formula) {
    }

    @Override
    public List<FormulaVariable> getAllVariables() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<IFormulaAtom> getAtoms() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public IFormula getFather() {
        return null;
    }

    @Override
    public String getId() {
        return "fail";
    }

    @Override
    public List<FormulaVariable> getLocalVariables() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<IFormula> getNegatedSubFormulas() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public PositiveFormula getPositiveFormula() {
        return this;
    }

    @Override
    public void setFather(IFormula father) {
    }

    @Override
    public void setPositiveFormula(PositiveFormula formula) {
    }

    @Override
    public String toString() {
        return "fail";
    }

    @Override
    public NullFormula clone() {
        return getInstance();
    }
}
