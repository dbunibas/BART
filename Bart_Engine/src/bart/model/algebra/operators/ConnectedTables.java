package bart.model.algebra.operators;

import java.util.List;
import java.util.Set;
import speedy.model.database.TableAlias;

class ConnectedTables {

    Set<TableAlias> tableAliases;
    List<EqualityGroup> equalityGroups;

    public ConnectedTables(Set<TableAlias> tableAliases) {
        this.tableAliases = tableAliases;
    }

    public Set<TableAlias> getTableAliases() {
        return tableAliases;
    }

    public List<EqualityGroup> getEqualityGroups() {
        return equalityGroups;
    }

    public void setEqualityGroups(List<EqualityGroup> equalityGroups) {
        this.equalityGroups = equalityGroups;
    }

    @Override
    public String toString() {
        return "ConnectedEqualityGroups:" + tableAliases;
    }

}