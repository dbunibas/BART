package bart.model.dependency;

import org.jgrapht.graph.DefaultEdge;

public class LabeledEdge extends DefaultEdge {

    private String v1;
    private String v2;
    private String label;

    public LabeledEdge(String v1, String v2, String label) {
        this.v1 = v1;
        this.v2 = v2;
        this.label = label;
    }

    public String getV1() {
        return v1;
    }

    public String getV2() {
        return v2;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.v1 != null ? this.v1.hashCode() : 0);
        hash = 79 * hash + (this.v2 != null ? this.v2.hashCode() : 0);
        hash = 79 * hash + (this.label != null ? this.label.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final LabeledEdge other = (LabeledEdge) obj;
        if ((this.v1 == null) ? (other.v1 != null) : !this.v1.equals(other.v1)) return false;
        if ((this.v2 == null) ? (other.v2 != null) : !this.v2.equals(other.v2)) return false;
        if ((this.label == null) ? (other.label != null) : !this.label.equals(other.label)) return false;
        return true;
    }

    @Override
    public String toString() {
        return label;
    }
}
