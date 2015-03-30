package bart.persistence.parser;

import java.util.ArrayList;
import java.util.List;

public class ParserInstance {

    private static int counter = 0;

    private String id;
    private List<ParserFact> facts = new ArrayList<ParserFact>();

    public ParserInstance() {
        this.id = "instance" + counter++ + ".";
    }

    public String getId() {
        return id;
    }

    public List<ParserFact> getFacts() {
        return facts;
    }

    public void addFact(ParserFact fact) {
        this.facts.add(fact);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("-------- Parser Instance -----------\n");
        for (ParserFact parserFact : facts) {
            result.append(parserFact).append("\n");
        }
        return result.toString();
    }

}
