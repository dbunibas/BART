package bart;

import bart.exceptions.ErrorGeneratorException;
import bart.model.EGTask;
import bart.model.EGTaskConfiguration;
import bart.model.detection.operator.DetectViolations;
import bart.model.errorgenerator.CellChanges;
import bart.model.errorgenerator.ICellChange;
import bart.model.errorgenerator.operator.APrioriGenerator;
import bart.utility.ErrorGeneratorStats;
import bart.persistence.DAOEGTask;
import bart.persistence.DAOEGTaskConfiguration;
import speedy.persistence.relational.AccessConfiguration;
import speedy.persistence.xml.DAOXmlUtility;
import bart.utility.BartDBMSUtility;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DAOException;
import speedy.exceptions.DBMSException;
import speedy.utility.PrintUtility;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    private static DAOEGTask daoTask = new DAOEGTask();
    private static DAOEGTaskConfiguration daoTaskConfiguration = new DAOEGTaskConfiguration();
    private static APrioriGenerator generator = new APrioriGenerator();
    private static DetectViolations cleanInstanceChecker = new DetectViolations();

    public static void main(String[] args) {
        if (args.length < 1) {
            printUsage();
            return;
        }
        try {
            List<String> options = new ArrayList<String>(Arrays.asList(args));
            options.remove(0);
            String relativePathTask = args[0];
            if (relativePathTask.startsWith("-")) {
                printUsage();
                return;
            }
            File taskFile = new File(relativePathTask).getAbsoluteFile();
            if (!taskFile.exists()) {
                PrintUtility.printError("Unable to load task. File " + relativePathTask + " not found");
                return;
            }
            String fileTask = taskFile.getAbsolutePath();
            EGTaskConfiguration conf = daoTaskConfiguration.loadConfiguration(fileTask);
            if (conf.isDebug()) PrintUtility.printMessage(conf.toString());
            if (conf.isRecreateDBOnStart()) {
                removeExistingDB(fileTask);
            }
            EGTask task;
            try {
                PrintUtility.printMessage("*** Loading task " + fileTask + "... ");
                task = daoTask.loadTask(fileTask);
                PrintUtility.printSuccess(" EGTask loaded!");
//                System.out.println(task);
            } catch (DAOException ex) {
                PrintUtility.printError("\nUnable to load task. \n" + ex.getLocalizedMessage());
                return;
            }
            if (options.contains("-checkCleanInstance")) {
                checkCleanInstance(task);
                return;
            }
            CellChanges changes = executeTask(task);
            PrintUtility.printInformation("Total changes: " + changes.getChanges().size());
            PrintUtility.printMessage(ErrorGeneratorStats.getInstance().toString());
        } catch (Exception e) {
            PrintUtility.printError("Unexpected exception! " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        PrintUtility.printInformation("****************    BART   ****************\n");
        PrintUtility.printInformation("Usage: java -jar bart.jar <path_task.xml> [OPTION]\n");
        PrintUtility.printInformation("  [Options]\n");
        PrintUtility.printInformation("   -checkCleanInstance: Import data and check constraints. No error will be generated\n");
        PrintUtility.printInformation("   -removeExistingDB: Drop existing DB\n");
    }

    private static CellChanges executeTask(EGTask task) throws ErrorGeneratorException {
        PrintUtility.printMessage("*** Messing up...");
        long start = new Date().getTime();
        CellChanges changes = generator.run(task);
        long end = new Date().getTime();
        double executionTime = (end - start) / 1000.0;
        PrintUtility.printInformation("*** Execution time: " + executionTime + " sec");
        PrintUtility.printInformation("*** Cell changes: " + changes.getChanges().size());
        if (changes.getChanges().size() < 50) {
            for (ICellChange change : changes.getChanges()) {
                PrintUtility.printMessage(change.toString());
            }
        }
        return changes;
    }

    private static void checkCleanInstance(EGTask task) {
        PrintUtility.printMessage("*** Checking constraints...");
        long start = new Date().getTime();
        try {
            cleanInstanceChecker.check(task.getDCs(), task.getSource(), task.getTarget(), task);
            PrintUtility.printSuccess("*** Database is clean!");
        } catch (ErrorGeneratorException ex) {
            PrintUtility.printError("*** " + ex.getLocalizedMessage());
        }
        long end = new Date().getTime();
        double executionTime = (end - start) / 1000.0;
        PrintUtility.printInformation("*** Execution time: " + executionTime + " sec");
    }

    private static void removeExistingDB(String fileTask) {
        AccessConfiguration accessConfiguration = loadTargetAccessConfiguration(fileTask);
        if (accessConfiguration == null) {
            return;
        }
        try {
            PrintUtility.printInformation("Removing db " + accessConfiguration.getDatabaseName() + ", if exist...");
            BartDBMSUtility.deleteDB(accessConfiguration);
            PrintUtility.printSuccess("Database removed!");
        } catch (DBMSException ex) {
            String message = ex.getMessage();
            if (!message.contains("does not exist")) {
                PrintUtility.printError("Unable to drop database.\n" + ex.getLocalizedMessage());
            }
        }
    }

    private static AccessConfiguration loadTargetAccessConfiguration(String fileTask) {
        Document document = new DAOXmlUtility().buildDOM(fileTask);
        Element rootElement = document.getRootElement();
        Element databaseElement = rootElement.getChild("target");
        Element dbmsElement = databaseElement.getChild("access-configuration");
        if (dbmsElement == null) {
            return null;
        }
        AccessConfiguration accessConfiguration = new AccessConfiguration();
        accessConfiguration.setDriver(dbmsElement.getChildText("driver").trim());
        accessConfiguration.setUri(dbmsElement.getChildText("uri").trim());
        accessConfiguration.setSchemaName(dbmsElement.getChildText("schema").trim());
        accessConfiguration.setLogin(dbmsElement.getChildText("login").trim());
        accessConfiguration.setPassword(dbmsElement.getChildText("password").trim());
        return accessConfiguration;
    }

}
