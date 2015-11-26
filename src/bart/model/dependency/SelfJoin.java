package bart.model.dependency;

import speedy.model.database.TableAlias;
import java.util.List;

public class SelfJoin {

    private List<TableAlias> atoms;

    public SelfJoin(List<TableAlias> atoms) {
        this.atoms = atoms;
    }

    public List<TableAlias> getAtoms() {
        return atoms;
    }

    @Override
    public String toString() {
        return "SelfJoin{" + "atoms=" + atoms + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.atoms != null ? this.atoms.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final SelfJoin other = (SelfJoin) obj;
        if (this.atoms != other.atoms && (this.atoms == null || !this.atoms.equals(other.atoms))) return false;
        return true;
    }
}