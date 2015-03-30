package bart.model.algebra.operators;

import bart.model.algebra.*;
import java.util.List;

public class AlgebraTreeToString {

    public String treeToString(IAlgebraOperator root, String indent) {
        AlgebraTreeToStringVisitor visitor = new AlgebraTreeToStringVisitor(indent);
        root.accept(visitor);
        return visitor.getResult();
    }
}

class AlgebraTreeToStringVisitor implements IAlgebraTreeVisitor {

    private String initialIndent;
    private int indentLevel = 0;
    private StringBuilder result = new StringBuilder();

    public AlgebraTreeToStringVisitor(String indent) {
        this.initialIndent = indent;
    }

    private void visitChildren(IAlgebraOperator operator) {
        List<IAlgebraOperator> listOfChildren = operator.getChildren();
        if (listOfChildren != null) {
            this.indentLevel++;
            for (IAlgebraOperator child : listOfChildren) {
                child.accept(this);
            }
            this.indentLevel--;
        }
    }

    private StringBuilder indentString() {
        StringBuilder indent = new StringBuilder(initialIndent);
        for (int i = 0; i < this.indentLevel; i++) {
            indent.append("    ");
        }
        return indent;
    }

    public String getResult() {
        return result.toString();
    }

    public void visitScan(Scan operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
    }

    public void visitSelect(Select operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
//        result.append("WHERE ").append("\n");
//        for (Expression condition : operator.getSelections()) {
//            result.append(this.indentString()).append("|-").append(condition).append("\n");
//        }
    }

    public void visitJoin(Join operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitProject(Project operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitDifference(Difference operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitUnion(Union operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitGroupBy(GroupBy operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitLimit(Limit operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitRestoreOIDs(RestoreOIDs operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitOrderBy(OrderBy operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitSelectIn(SelectIn operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitCreateTable(CreateTable operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitDistinct(Distinct operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitCartesianProduct(CartesianProduct operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitPartition(Partition operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitExtractRandomSample(ExtractRandomSample operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

    public void visitOffset(Offset operator) {
        result.append(this.indentString()).append(operator.getName()).append("\n");
        visitChildren(operator);
    }

}
