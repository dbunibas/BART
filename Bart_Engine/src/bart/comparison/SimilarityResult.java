package bart.comparison;

import java.util.HashMap;
import java.util.Map;
import speedy.utility.SpeedyUtility;

public class SimilarityResult {

    private Map<String, TableSimilarity> tableSimilarities = new HashMap<String, TableSimilarity>();

    public double getSimilarity() {
        if (tableSimilarities.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (TableSimilarity tableSimilarity : tableSimilarities.values()) {
            sum += tableSimilarity.getSimilarity();
        }
        return sum / (double) tableSimilarities.size();
    }

    public TableSimilarity getSimilarityForTable(String tableName) {
        return tableSimilarities.get(tableName);
    }

    public void setTableSimilarity(String table, TableSimilarity similarity) {
        if (tableSimilarities.containsKey(table)) {
            throw new IllegalArgumentException("Similarity for table " + table + " has been already computed");
        }
        tableSimilarities.put(table, similarity);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Table Similarity\n").append(SpeedyUtility.printMap(tableSimilarities));
        return sb.toString();
    }

}
