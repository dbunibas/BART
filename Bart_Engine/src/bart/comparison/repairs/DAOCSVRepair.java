package bart.comparison.repairs;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DAOException;
import speedy.persistence.DAOUtility;

public class DAOCSVRepair {

    private static Logger logger = LoggerFactory.getLogger(DAOCSVRepair.class);

    private static final char CSV_CHAR_SEP = ',';
    private static final char CSV_CHAR_QUOTE_SEPARATOR = '"';
    private DAOUtility utility = new DAOUtility();

    public Collection<Repair> loadRepair(String fileName) throws DAOException {
        return loadRepairMap(fileName).values();
    }

    public Map<String, Repair> loadRepairMap(String fileName) throws DAOException {
        Map<String, Repair> result = new HashMap<String, Repair>();
        try {
            BufferedReader reader = utility.getBufferedReader(fileName);
            MappingIterator<String[]> it = getMappingIterator(reader);
            while (it.hasNext()) {
                String[] tokens = it.next();
                if (tokens.length == 0 || tokens[0].startsWith("+++++++++++++++")) {
                    continue;
                }
                String tid = tokens[0];
                String oldValue;
                String newValue;
                if (tokens.length == 2) {
                    oldValue = null;
                    newValue = tokens[1];
                } else if (tokens.length == 3) {
                    oldValue = tokens[1];
                    newValue = tokens[2];
                } else {
                    throw new DAOException("Malformed file " + fileName + ".\nCSV file must contains at least two column (cell, newValue)");
                }
                Repair repair = new Repair(tid, oldValue, newValue);
                result.put(repair.getCellId(), repair);
            }
        } catch (IOException exception) {
            throw new DAOException("Unable to load file: " + fileName + "\n" + exception);
        }
        return result;
    }

    public List<Map<String, Repair>> loadMultipleRepair(String fileName) throws DAOException {
        List<Map<String, Repair>> result = new ArrayList<Map<String, Repair>>();
        try {
            BufferedReader reader = utility.getBufferedReader(fileName);
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Map<String, Repair> repairs = new HashMap<String, Repair>();
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    String[] tokens = getMappingIterator(line).next();
//                    String[] tokens = line.split(SEPARATOR, -1);
                    Repair repair = new Repair(tokens[0], tokens[1], tokens[2]);
                    repairs.put(repair.getCellId(), repair);
                }
                if (!repairs.isEmpty()) {
                    result.add(repairs);
                }
            }

        } catch (IOException exception) {
            throw new DAOException("Unable to load file: " + fileName + "\n" + exception);
        }
        return result;
    }

    private MappingIterator<String[]> getMappingIterator(BufferedReader br) throws IOException {
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        CsvSchema schema = CsvSchema.emptySchema().withColumnSeparator(CSV_CHAR_SEP).withQuoteChar(CSV_CHAR_QUOTE_SEPARATOR);
        return mapper.readerFor(String[].class).with(schema).readValues(br);
    }

    private MappingIterator<String[]> getMappingIterator(String line) throws IOException {
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        CsvSchema schema = CsvSchema.emptySchema().withColumnSeparator(CSV_CHAR_SEP).withQuoteChar(CSV_CHAR_QUOTE_SEPARATOR);
        return mapper.readerFor(String[].class).with(schema).readValues(line);
    }
}
