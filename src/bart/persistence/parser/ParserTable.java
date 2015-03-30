package bart.persistence.parser;

import java.util.ArrayList;
import java.util.List;

public class ParserTable {

    private String set;
    private List<ParserAttribute> attributes = new ArrayList<ParserAttribute>();

    public ParserTable(String set) {
        this.set = set;
    }

    public void addAttribute(ParserAttribute attribute) {
        this.attributes.add(attribute);
    }

    public List<ParserAttribute> getAttributes() {
        return attributes;
    }

    public String getSet() {
        return set;
    }

    public String toString() {
        String result = set + "(";
        for (int i = 0; i < attributes.size(); i++) {
            result += attributes.get(i);
            if (i != attributes.size() - 1) {
                result += ", ";
            }
        }
        result += ")";
        return result;
    }
}
