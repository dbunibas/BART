package bart.model.dependency.analysis;

import bart.model.dependency.FormulaVariable;
import bart.model.dependency.IFormulaAtom;

public class FormulaGraphVertex {

    private IFormulaAtom atom;
    private FormulaVariable variable;
    private String constant;
    private boolean virtual;

    public FormulaGraphVertex(IFormulaAtom atom) {
        this.atom = atom;
    }

    public FormulaGraphVertex(FormulaVariable variable) {
        this.variable = variable;
    }

    public FormulaGraphVertex(String constant) {
        this.constant = constant;
    }

    public IFormulaAtom getAtom() {
        return atom;
    }

    public FormulaVariable getVariable() {
        return variable;
    }

    public String getConstant() {
        return constant;
    }

    public boolean isAtom() {
        return atom != null;
    }

    public boolean isVariable() {
        return variable != null;
    }

    public boolean isConstant() {
        return constant != null;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    @Override
    public String toString() {
        return (atom == null ? "" : atom.toString()) + (variable == null ? "" : variable.toString()) + (constant == null ? "" : constant.toString()) + (virtual == false ? "" : " virtual");
    }

}
