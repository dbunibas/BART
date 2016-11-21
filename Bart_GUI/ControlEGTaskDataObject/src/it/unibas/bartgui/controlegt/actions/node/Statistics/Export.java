/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions.node.Statistics;

import it.unibas.bartgui.controlegt.OutputWindow;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.statistics.Statistic;
import it.unibas.bartgui.view.panel.run.BusyDialog;
import it.unibas.centrallookup.CentralLookup;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.WindowManager;
import speedy.model.database.Cell;
import speedy.model.database.IValue;

@ActionID(
        category = "StatisticNode",
        id = "it.unibas.bartgui.controlegt.actions.node.Statistics.Export"
)
@ActionRegistration(
        displayName = "#CTL_Export"
)
@Messages({
    "CTL_Export=Export CSV",
    "# {0} - file name",
    "MSG_SAVE_OK=File {0} saved",
    "# {0} - file name",
    "MSG_NOT_SAVE=File {0} NOT saved",
    "MSG_NO_CHANGES=No Cell Changes"
})
public final class Export implements ActionListener {

    private static String COMMA = ",";
    private static String DOT = ".";
    private final Statistic context;
    private Map<Cell, IValue> map;
    private boolean result = false;
    private String dtoFileName;

    public Export(Statistic context) {
        this.context = context;
        EGTaskDataObjectDataObject dto = CentralLookup.getDefLookup().lookup(EGTaskDataObjectDataObject.class);
        dtoFileName = dto.getPrimaryFile().getName();
        map = this.context.getCellChanges(dtoFileName);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if ((map == null) || (map.isEmpty())) {
            DialogDisplayer.getDefault()
                    .notify(new NotifyDescriptor.Message(Bundle.MSG_NO_CHANGES(), NotifyDescriptor.INFORMATION_MESSAGE));
            return;
        }
        File toSave = chooseFile();
        if (toSave == null) return;
        if (!(toSave.getName().contains(".csv") || toSave.getName().contains(".CSV"))) {
            StringBuilder sb = new StringBuilder(toSave.getAbsolutePath());
            sb.append(".csv");
            toSave = new File(sb.toString());
        }
        final InputOutput io = IOProvider.getDefault().getIO(dtoFileName, false);
        io.select();
        OutputWindow.openOutputWindowStream(io.getOut(), io.getErr());
        final Dialog d = BusyDialog.getBusyDialog();
        final String fileName = toSave.getName();
        RequestProcessor.Task T = RequestProcessor.getDefault().post(new ExportRunnable(toSave));
        T.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(Task task) {
//                d.setVisible(false);
                if (result) {
                    System.out.println(Bundle.MSG_SAVE_OK(fileName));
                } else {
                    System.err.println(Bundle.MSG_NOT_SAVE(fileName));
                }
                OutputWindow.closeOutputWindowStream(io.getOut(), io.getErr());
            }
        });
//        d.setVisible(true);
    }

    private File chooseFile() {
        JFrame mainFrame = (JFrame) WindowManager.getDefault().getMainWindow();
        FileDialog fileDialog = new FileDialog(mainFrame, new File(System.getProperty("user.home")).toString());
        fileDialog.setTitle("Export changes in cvs file");
        fileDialog.setFile("expected.csv");
        fileDialog.setMode(FileDialog.SAVE);
        fileDialog.setFilenameFilter(new ExtFileFilter("csv"));
        fileDialog.setVisible(true);
        String filename = fileDialog.getFile();
        if (filename == null){
            return null;
        }
        String dir = fileDialog.getDirectory();
        return new File(dir + File.separator + filename);
    }

    private File chooseFileNB() {
        File toSave = new FileChooserBuilder("Create_CVS_FILE")
                .setTitle("Export changes in cvs file")
                .setDefaultWorkingDirectory(new File(System.getProperty("user.home")))
                .setApproveText("ok")
                .setDirectoriesOnly(false)
                .setFilesOnly(true)
                .setAcceptAllFileFilterUsed(false)
                .addFileFilter(new FileNameExtensionFilter("CVS File", "csv", "CSV"))
                .showSaveDialog();
        return toSave;
    }

    private class ExportRunnable implements Runnable {

        private File file;

        public ExportRunnable(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            Writer out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
                Iterator<Cell> it = map.keySet().iterator();
                while (it.hasNext()) {
                    Cell cell = it.next();
                    StringBuilder sb = new StringBuilder();
                    sb.append(cell.getTupleOID());
                    sb.append(DOT);
                    sb.append(cell.getAttribute());
                    sb.append(COMMA);
                    sb.append(map.get(cell));
                    sb.append(COMMA);
                    sb.append(cell.getValue());
                    out.write(sb.toString());
                    out.write("\n");
                }
                result = true;
            } catch (Exception ex) {
                result = false;
                ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
            } finally {
                try {
                    if (out != null) out.close();
                } catch (Exception ex) {
                }
            }
        }

    }

    public class ExtFileFilter implements java.io.FilenameFilter {

        String description = "";
        String fileExt = "";

        public ExtFileFilter(String extension) {
            fileExt = extension;
        }

        public ExtFileFilter(String extension, String typeDescription) {
            fileExt = extension;
            this.description = typeDescription;
        }

        @Override
        public boolean accept(File dir, String name) {
            return (name.toLowerCase().endsWith(fileExt));
        }
    }

}
