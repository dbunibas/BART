package bart.model.database.mainmemory.datasource.nodes;

import bart.model.database.mainmemory.datasource.operators.INodeVisitor;
import java.util.ArrayList;
import java.util.List;

public class TupleNode extends IntermediateNode {

    private List<String> provenance = new ArrayList<String>();

    public TupleNode(String label) {
        super(label);
    }

    public TupleNode(String label, Object value) {
        super(label, value);
    }

    public List<String> getProvenance() {
        return provenance;
    }

    public void addProvenance(String tgdId) {
        if (!provenance.contains(tgdId)) {
            provenance.add(tgdId);
        }
    }

    public void addProvenanceList(List<String> newProvenance) {
        for (String tgdId : newProvenance) {
            if (!provenance.contains(tgdId)) {
                provenance.add(tgdId);
            }
        }
    }

    public void accept(INodeVisitor visitor) {
        visitor.visitTupleNode(this);
    }
}
