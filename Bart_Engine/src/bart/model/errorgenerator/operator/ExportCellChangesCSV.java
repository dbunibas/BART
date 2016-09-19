package bart.model.errorgenerator.operator;

import speedy.model.database.Cell;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ICellChange;
import bart.model.errorgenerator.RandomCellChange;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DAOException;
import speedy.persistence.xml.operators.TransformFilePaths;

public class ExportCellChangesCSV {

    private static Logger logger = LoggerFactory.getLogger(ExportCellChangesCSV.class);
    private TransformFilePaths filePathTransformator = new TransformFilePaths();
    private static String SEPARATOR = ",";

    public void export(CellChanges cellChanges, String path, String taskPath, boolean full) {
        path = expandPath(taskPath, path);
        Writer out = null;
        try {
            if (logger.isDebugEnabled()) logger.debug("Exporting cell changes in " + path);
            File outFile = new File(path);
            outFile.getParentFile().mkdirs();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
            for (ICellChange change : cellChanges.getChanges()) {
                if (change instanceof RandomCellChange) {
                    RandomCellChange randomCellChange = (RandomCellChange) change;
                    if (!randomCellChange.isExport()) continue;
                }
                out.write(changeToCSV(change, full));
                out.write("\n");
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

    private String changeToCSV(ICellChange change, boolean full) {
        StringBuilder sb = new StringBuilder();
        Cell originalCell = change.getCell();
        sb.append(originalCell.getTupleOID()).append(".").append(originalCell.getAttribute());
        sb.append(SEPARATOR);
        sb.append(change.getNewValue());
        sb.append(SEPARATOR);
        sb.append(originalCell.getValue());
        if (full) {
            sb.append(SEPARATOR);
            sb.append(change.getViolatedDependencies());
        }
        return sb.toString();
    }

    private String expandPath(String taskPath, String path) {
        if (path.startsWith(File.separator)) {
            return path;
        }
        return filePathTransformator.expand(taskPath, path);
    }

}
