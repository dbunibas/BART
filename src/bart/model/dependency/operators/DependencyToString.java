package bart.model.dependency.operators;

import bart.BartConstants;
import bart.model.dependency.*;
import java.util.List;
import java.util.Stack;

public class DependencyToString {
    
    public String toLogicalString(Dependency dependency, String indent, boolean compactFormat) {
        DependencyToStringVisitor visitor = new DependencyToStringVisitor(indent, compactFormat);
        dependency.accept(visitor);
        return visitor.getResult().toString();
    }
}

class DependencyToStringVisitor implements IFormulaVisitor {

    private Stack<String> indentStack = new Stack<String>();
    private boolean premise = true;
    private boolean compactFormat;
    private StringBuilder result = new StringBuilder();

    public DependencyToStringVisitor(String indent, boolean compactFormat) {
        this.indentStack.push(indent);
        this.compactFormat = compactFormat;
    }

    public void visitDependency(Dependency dependency) {
        if (!compactFormat) {
            result.append(getIndent()).append(dependency.getId()).append(":");
        }
        dependency.getPremise().accept(this);
        result.append(" -> ");
//        this.indentStack.push(BartConstants.INDENT);
        this.premise = false;
        dependency.getConclusion().accept(this);
        result.append(".\n");
        if (dependency.isSymmetric()) {
            result.append("(symmetric)\n");
        }
//        if (!useSaveFormat && printSkolems) {
//            result.append("\n");
//            result.append(printSkolems(dependency, scenario));
//            result.append("-----------------------------------\n");
//        }
    }

    public void visitPositiveFormula(PositiveFormula formula) {
        if (premise) {
            result.append("\n").append(getIndent());
        }
        if (formula instanceof NullFormula) {
            result.append("fail");
            return;
        }
        if (!compactFormat) {
            if (isRootFormula(formula)) {
                if (premise) {
                    result.append(getIndent()).append("for each ");
                } else {
                    this.indentStack.push(BartConstants.INDENT);
                    result.append("\n").append(getIndent());
                    if (!formula.getLocalVariables().isEmpty()) {
                        result.append("exist ");
                    }
                }
            } else {
                result.append(getIndent()).append("and not exist (");
            }
            result.append(printVariables(formula.getLocalVariables()));
        } else {
            if (!isRootFormula(formula)) {
                result.append(getIndent()).append(" !(");
            }
        }
        result.append(formula.toString());
//      result.append("\n");
    }

    public void visitFormulaWithNegations(FormulaWithNegations formula) {
//        this.indentStack.push(BartConstants.INDENT);
        formula.getPositiveFormula().accept(this);
        for (IFormula negatedFormula : formula.getNegatedSubFormulas()) {
            this.indentStack.push(BartConstants.SECONDARY_INDENT);
            negatedFormula.accept(this);
            this.indentStack.pop();
        }
//        this.indentStack.pop();
        if (formula.getFather() != null) {
            result.append(")");
        }
//        result.append("\n");
    }

    private String printVariables(List<FormulaVariable> variables) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < variables.size(); i++) {
            FormulaVariable formulaVariable = variables.get(i);
            buffer.append(formulaVariable);
            if (i != variables.size() - 1) {
                buffer.append(", ");
            }
        }
        if (!variables.isEmpty()) {
            buffer.append(":\n");
        }
        return buffer.toString();
    }

    private String getIndent() {
        StringBuilder indentString = new StringBuilder();
        for (String string : indentStack) {
            indentString.append(string);
        }
        return indentString.toString();
    }

    private boolean isRootFormula(IFormula formula) {
//        return formula.getFather() != null && formula.getFather().getFather() == null;
        return formula.getFather() == null || (formula.getFather() != null && formula.getFather().getFather() == null);
    }

    public Object getResult() {
        return result.toString();
    }
}
