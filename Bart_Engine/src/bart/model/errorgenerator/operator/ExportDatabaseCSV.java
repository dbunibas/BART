package bart.model.errorgenerator.operator;

import bart.BartConstants;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.IValue;
import speedy.model.database.Tuple;
import bart.model.errorgenerator.CellChanges;
import bart.utility.BartUtility;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DAOException;
import speedy.persistence.xml.operators.TransformFilePaths;

public class ExportDatabaseCSV implements IExportDatabase {

    private static Logger logger = LoggerFactory.getLogger(ExportDatabaseCSV.class);
    private TransformFilePaths filePathTransformator = new TransformFilePaths();
    private static String SEPARATOR = ",";
    private static String NEW_LINE = "\n";

    @Override
    public void export(IDatabase database, String prefix, String path, String taskPath) {
        export(database, prefix, new CellChanges(), path, taskPath);
    }

    @Override
    public void export(IDatabase database, String prefix, CellChanges cellChanges, String path, String taskPath) {
        path = expandPath(taskPath, path);
        if (logger.isDebugEnabled()) logger.debug("Exporting database to path " + path);
        for (String tableName : database.getTableNames()) {
            ITable table = database.getTable(tableName);
            exportTable(table, prefix, cellChanges, path);
        }
    }

    private void exportTable(ITable table, String prefix, CellChanges cellChanges, String path) {
        path += File.separator + prefix + "_" + table.getName() + ".csv";
        Writer out = null;
        try {
            File outFile = new File(path);
            outFile.getParentFile().mkdirs();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
            out.write(writeHeader(table));
            ITupleIterator it = table.getTupleIterator();
            while (it.hasNext()) {
                Tuple tuple = it.next();
                out.write(writeTuple(tuple, table, cellChanges));
            }
        } catch (Exception ex) {
            logger.error("Unable to export cell changes to path " + path + "\n\t" + ex.getLocalizedMessage());
            throw new DAOException(ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    private String writeHeader(ITable table) {
        StringBuilder sb = new StringBuilder();
        for (Attribute attribute : getAttributes(table)) {
            sb.append(attribute.getName());
            sb.append(SEPARATOR);
        }
        BartUtility.removeChars(SEPARATOR.length(), sb);
        sb.append(NEW_LINE);
        return sb.toString();
    }

    private String writeTuple(Tuple tuple, ITable table, CellChanges cellChanges) {
        StringBuilder sb = new StringBuilder();
        for (Attribute attribute : getAttributes(table)) {
            Cell cell = tuple.getCell(new AttributeRef(attribute.getTableName(), attribute.getName()));
            IValue value;
            if (cellChanges.cellHasBeenChanged(cell)) {
                value = cellChanges.getNewValue(cell);
            } else {
                value = cell.getValue();
            }
            sb.append(writeValue(value));
            sb.append(SEPARATOR);
        }
        BartUtility.removeChars(SEPARATOR.length(), sb);
        sb.append(NEW_LINE);
        return sb.toString();
    }

    private String writeValue(IValue value) {
        if (value == null) {
            return "";
        }
        String s = value.toString();
        if (s.contains(SEPARATOR)) {
            logger.warn("Removing csv separator value " + SEPARATOR + " from " + s);
            s = s.replaceAll(SEPARATOR, "");
        }
        return s;
    }

    private List<Attribute> getAttributes(ITable table) {
        List<Attribute> result = new ArrayList<Attribute>();
        for (Attribute attribute : table.getAttributes()) {
            if (attribute.getName().equalsIgnoreCase(BartConstants.OID)) {
                continue;
            }
            result.add(attribute);
        }
        return result;
    }

    private String expandPath(String taskPath, String path) {
        if (path.startsWith(File.separator)) {
            return path;
        }
        return filePathTransformator.expand(taskPath, path);
    }

}
