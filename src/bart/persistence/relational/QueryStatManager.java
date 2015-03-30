package bart.persistence.relational;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryStatManager {

    private static Logger logger = LoggerFactory.getLogger(QueryStatManager.class);

    private static QueryStatManager singleton = new QueryStatManager();
    private List<QueryStat> statistics = new ArrayList<QueryStat>();
    private long readTuples = 0;
    private int TOP_K_QUERIES = 5; //0 Disabled
    private int QUERY_PREVIEW_LENGHT = -1; //-1 to print the whole query
    private Date lastPrint;
    private int SEC = -1; //-1 disabled

    private QueryStatManager() {
    }

    public static QueryStatManager getInstance() {
        return singleton;
    }

    public void addQuery(String query, long executionTime) {
        if (!logger.isDebugEnabled()) return;
        statistics.add(new QueryStat(query, executionTime));
        printStatisticsAfterSeconds();
    }

    private void printStatisticsAfterSeconds() {
        if (!logger.isDebugEnabled()) return;
        if(SEC < 0) return;
        Date nowDate = new Date();
        if (lastPrint == null || (nowDate.getTime() - lastPrint.getTime() > SEC * 1000)) {
            lastPrint = nowDate;
            printStatistics("* RUNNING *\n");
        }
    }

    public void printStatistics() {
        printStatistics("");
    }

    public void addReadTuple() {
        if (!logger.isDebugEnabled()) return;
        readTuples++;
    }

    public void printStatistics(String prefix) {
        if (!logger.isDebugEnabled()) return;
        Collections.sort(statistics);
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append("--------------------------").append("\n");
        sb.append("Number of queries:\t").append(statistics.size()).append("\n");
        sb.append("Total execution time:\t").append(getTotalExecutionTime()).append("\n");
        sb.append("Type of queries:").append("\n");
        int select = 0;
        int insert = 0;
        int update = 0;
        int delete = 0;
        int create = 0;
        for (QueryStat queryStat : statistics) {
            String query = queryStat.getQuery();
            if (query.startsWith("SELECT")) select++;
            if (query.startsWith("INSERT")) insert++;
            if (query.startsWith("UPDATE")) update++;
            if (query.startsWith("DELETE")) delete++;
            if (query.startsWith("CREATE")) create++;
        }
        sb.append("\t").append("Create").append("\t").append(create).append("\n");
        sb.append("\t").append("Select").append("\t").append(select).append("\n");
        sb.append("\t").append("Insert").append("\t").append(insert).append("\n");
        sb.append("\t").append("Update").append("\t").append(update).append("\n");
        sb.append("\t").append("Delete").append("\t").append(delete).append("\n");
        sb.append("Read tuples:\t").append(readTuples).append("\n");
        if (TOP_K_QUERIES > 0) sb.append("Most expensive queries:").append("\n");
        for (int i = 0; i < Math.min(TOP_K_QUERIES, statistics.size()); i++) {
            QueryStat query = statistics.get(i);
            sb.append("\t").append(query.getExecutionTime()).append("\t").append(printQuery(query.getQuery())).append("\n");
        }
        sb.append("--------------------------").append("\n");
        logger.debug(sb.toString());
    }

    private long getTotalExecutionTime() {
        long total = 0;
        for (QueryStat queryStat : statistics) {
            total += queryStat.getExecutionTime();
        }
        return total;
    }

    private String printQuery(String query) {
        String singleLine = query.replaceAll("\n", " ");
        if (QUERY_PREVIEW_LENGHT != -1) {
            singleLine = singleLine.substring(0, Math.min(QUERY_PREVIEW_LENGHT, singleLine.length()));
        }
        return singleLine;
    }

    public void resetStatistics() {
        statistics.clear();
    }
}

class QueryStat implements Comparable<QueryStat> {

    private String query;
    private long executionTime;

    public QueryStat(String query, long executionTime) {
        this.query = query;
        this.executionTime = executionTime;
    }

    public String getQuery() {
        return query;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public int compareTo(QueryStat o) {
        return (int) (o.executionTime - executionTime);
    }
}
