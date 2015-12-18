package bart.model.dependency;

import bart.model.dependency.operators.DependencyToString;
import bart.model.dependency.operators.IFormulaVisitor;
import bart.model.errorgenerator.VioGenQuery;
import java.util.ArrayList;
import java.util.List;

public class Dependency implements Cloneable {

    private String id;
    private IFormula premise;
    private IFormula conclusion;
    private String type;
    private List<VioGenQuery> vioGenQueries = new ArrayList<VioGenQuery>();

    public Dependency() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (this.id != null) {
            return;
        }
        this.id = id;
    }

    public void addSuffixId(String suffixId) {
        this.id += suffixId;
    }

    public IFormula getConclusion() {
        return conclusion;
    }

    public IFormula getPremise() {
        return premise;
    }

    public String getType() {
        return type;
    }

    public void setConclusion(IFormula conclusion) {
        this.conclusion = conclusion;
    }

    public void setPremise(IFormula premise) {
        this.premise = premise;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean hasNegations() {
        return !this.premise.getNegatedSubFormulas().isEmpty();
    }

    public List<VioGenQuery> getVioGenQueries() {
        return vioGenQueries;
    }

    public void setVioGenQueries(List<VioGenQuery> vioGenQueries) {
        this.vioGenQueries = vioGenQueries;
    }

    public boolean isSymmetric() {
        return this.premise.isSymmetric();
    }

    public void accept(IFormulaVisitor visitor) {
        visitor.visitDependency(this);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Dependency other = (Dependency) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) return false;
        return true;
    }

    @Override
    public Dependency clone() {
        Dependency clone = null;
        try {
            clone = (Dependency) super.clone();
            clone.premise = premise.clone();
            clone.conclusion = conclusion.clone();
        } catch (CloneNotSupportedException ex) {
        }
        return clone;
    }

    @Override
    public String toString() {
        return new DependencyToString().toLogicalString(this, "", false);
    }

    public String toLongString() {
        StringBuilder result = new StringBuilder();
        result.append(this.toString());
        result.append("  Type: ").append(type).append("\n");
        result.append("  VioGenQueries:\n");
        for (VioGenQuery vioGenQuery : vioGenQueries) {
            result.append("      ").append(vioGenQuery.toLongString());
        }
        return result.toString();
    }

}
