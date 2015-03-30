package bart.model.algebra.operators;

import bart.model.algebra.*;

public interface IAlgebraTreeVisitor {

    void visitScan(Scan operator);
    void visitSelect(Select operator);
    void visitDistinct(Distinct operator);
    void visitSelectIn(SelectIn operator);
    void visitJoin(Join operator);
    void visitCartesianProduct(CartesianProduct operator);
    void visitProject(Project operator);
    void visitDifference(Difference operator);
    void visitUnion(Union operator);
    void visitGroupBy(GroupBy operator);
    void visitPartition(Partition operator);
    void visitOrderBy(OrderBy operator);
    void visitLimit(Limit operator);
    void visitOffset(Offset operator);
    void visitRestoreOIDs(RestoreOIDs operator);
    void visitCreateTable(CreateTable operator);
    void visitExtractRandomSample(ExtractRandomSample operator);
    Object getResult();
}
