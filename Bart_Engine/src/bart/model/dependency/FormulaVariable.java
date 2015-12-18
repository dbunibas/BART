package bart.model.dependency;

import java.util.ArrayList;
import java.util.List;
import speedy.model.database.AttributeRef;
import speedy.model.database.IVariableDescription;

public class FormulaVariable implements Cloneable, IVariableDescription {

    private String id;
    private List<FormulaVariableOccurrence> relationalOccurrences = new ArrayList<FormulaVariableOccurrence>();
    private List<IFormulaAtom> nonRelationalOccurrences = new ArrayList<IFormulaAtom>();

    public FormulaVariable(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<FormulaVariableOccurrence> getRelationalOccurrences() {
        return relationalOccurrences;
    }

    public void addRelationalOccurrence(FormulaVariableOccurrence occurrence) {
        this.relationalOccurrences.add(occurrence);
    }

    public List<IFormulaAtom> getNonRelationalOccurrences() {
        return nonRelationalOccurrences;
    }

    public void addNonRelationalOccurrence(IFormulaAtom atom) {
        if (atom instanceof RelationalAtom) {
            throw new IllegalArgumentException("This is a relational atom occurrence: " + atom);
        }
        if (this.nonRelationalOccurrences.contains(atom)) {
            return;
        }
        this.nonRelationalOccurrences.add(atom);
    }

    public void setNonRelationalOccurrences(List<IFormulaAtom> nonRelationalOccurrences) {
        this.nonRelationalOccurrences = nonRelationalOccurrences;
    }

    public boolean isUniversal() {
        return this.relationalOccurrences.size() > 0;
    }

    public List<AttributeRef> getAttributeRefs() {
        List<AttributeRef> result = new ArrayList<AttributeRef>();
        for (FormulaVariableOccurrence variableOccurrence : this.getRelationalOccurrences()) {
            result.add(variableOccurrence.getAttributeRef());
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final FormulaVariable other = (FormulaVariable) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) return false;
        return true;
    }

    @Override
    public FormulaVariable clone() {
        FormulaVariable clone = null;
        try {
            clone = (FormulaVariable) super.clone();
            clone.relationalOccurrences = new ArrayList<FormulaVariableOccurrence>();
            for (FormulaVariableOccurrence occurrence : relationalOccurrences) {
                clone.relationalOccurrences.add((FormulaVariableOccurrence) occurrence.clone());
            }
            clone.nonRelationalOccurrences = new ArrayList<IFormulaAtom>(this.nonRelationalOccurrences);
        } catch (CloneNotSupportedException ex) {
        }
        return clone;
    }

    @Override
    public String toString() {
        return id;
    }

    public String toLongString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.toString());
        sb.append("\n Occurrences: ");
        for (FormulaVariableOccurrence occurrence : relationalOccurrences) {
            sb.append(occurrence.toLongString()).append(" ");
        }
        sb.append("\n\t NonRelational: ").append(nonRelationalOccurrences);
        return sb.toString();
    }

}
