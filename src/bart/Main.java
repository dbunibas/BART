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
                System.out.println("Unable to load task. File " + relativePathTask + " not found");
                return;
            }
            String fileTask = taskFile.getAbsolutePath();
            EGTaskConfiguration conf = daoTaskConfiguration.loadConfiguration(fileTask);
            if (conf.isRecreateDBOnStart()) {
                removeExistingDB(fileTask);
            }
            EGTask task;
            try {
                System.out.println("*** Loading task " + fileTask + "... ");
                task = daoTask.loadTask(fileTask);
                System.out.println(" EGTask loaded!");
//                System.out.println(task);
            } catch (DAOException ex) {
                System.out.println("\nUnable to load task. \n" + ex.getLocalizedMessage());
                return;
            }
            if (options.contains("-checkCleanInstance")) {
                checkCleanInstance(task);
                return;
            }
            CellChanges changes = executeTask(task);
            System.out.println("Total changes: " + changes.getChanges().size());
            System.out.println(ErrorGeneratorStats.getInstance().toString());
        } catch (Exception e) {
            System.out.println("Unexpected exception! " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.print("****************    BART   ****************\n");
        System.out.print("Usage: java -jar bart.jar <path_task.xml> [OPTION]\n");
        System.out.print("  [Options]\n");
        System.out.print("   -checkCleanInstance: Import data and check constraints. No error will be generated\n");
        System.out.print("   -removeExistingDB: Drop existing DB\n");
    }

    private static CellChanges executeTask(EGTask task) throws ErrorGeneratorException {
        System.out.println("*** Messing up...");
        long start = new Date().getTime();
        CellChanges changes = generator.run(task);
        long end = new Date().getTime();
        double executionTime = (end - start) / 1000.0;
        System.out.println("*** Execution time: " + executionTime + " sec");
        System.out.println("*** Cell changes: " + changes.getChanges().size());
        if (changes.getChanges().size() < 50) {
            for (ICellChange change : changes.getChanges()) {
                System.out.println(change);
            }
        }
        return changes;
    }

    private static void checkCleanInstance(EGTask task) {
        System.out.println("*** Checking constraints...");
        long start = new Date().getTime();
        try {
            cleanInstanceChecker.check(task.getDCs(), task.getSource(), task.getTarget(), task);
            System.out.println("*** Database is clean!");
        } catch (ErrorGeneratorException ex) {
            System.out.println("*** " + ex.getLocalizedMessage());
        }
        long end = new Date().getTime();
        double executionTime = (end - start) / 1000.0;
        System.out.println("*** Execution time: " + executionTime + " sec");
    }

    private static void removeExistingDB(String fileTask) {
        AccessConfiguration accessConfiguration = loadTargetAccessConfiguration(fileTask);
        if (accessConfiguration == null) {
            return;
        }
        try {
            System.out.println("Removing db " + accessConfiguration.getDatabaseName() + ", if exist...");
            BartDBMSUtility.deleteDB(accessConfiguration);
            System.out.println("Database removed!");
        } catch (DBMSException ex) {
            String message = ex.getMessage();
            if (!message.contains("does not exist")) {
                logger.warn("Unable to drop database.\n" + ex.getLocalizedMessage());
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
