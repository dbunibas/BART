package bart.persistence.parser;

import java.util.ArrayList;
import java.util.List;

public class ParserSchema {

    private static int counter = 0;

    private String id;
    private List<ParserTable> tables = new ArrayList<ParserTable>();

    public ParserSchema() {
        this.id = "instance" + counter++ + ".";
    }

    public String getId() {
        return id;
    }

    public List<ParserTable> getTables() {
        return tables;
    }

    public void addTable(ParserTable table) {
        this.tables.add(table);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("-------- Parser Instance -----------\n");
        for (ParserTable parserFact : tables) {
            result.append(parserFact).append("\n");
        }
        return result.toString();
    }

}
