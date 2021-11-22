package bart;

import bart.comparison.ComparisonConfiguration;
import bart.comparison.ComparisonStats;
import bart.comparison.InstanceMatchTask;
import bart.comparison.operators.ComputeInstanceSimilarityHashing;
import bart.comparison.operators.ComputeInstanceSimilarityBruteForceCompatibility;
import bart.comparison.operators.IComputeInstanceSimilarity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.database.IDatabase;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.persistence.DAOMainMemoryDatabase;
import speedy.utility.PrintUtility;
import speedy.utility.SpeedyUtility;

public class CompareInstance {

    private final static Logger logger = LoggerFactory.getLogger(CompareInstance.class);
    private final static IComputeInstanceSimilarity similarityCheckerBruteforce = new ComputeInstanceSimilarityBruteForceCompatibility();
    private final static IComputeInstanceSimilarity similarityCheckerHashing = new ComputeInstanceSimilarityHashing();
    private final static DAOMainMemoryDatabase daoDatabase = new DAOMainMemoryDatabase();

    public static void main(String args[]) {
        try {
            if (args.length != 1) {
                printUsage();
                return;
            }
            String comparisonTaskPath = args[0];
            printSystemInformation();
            Properties properties = new Properties();
            properties.load(new FileInputStream(comparisonTaskPath));
            loadComparisonConfiguration(properties);
            PrintUtility.printMessage(ComparisonConfiguration.getInstance().toString());
            IDatabase leftDb = loadDatabase(properties.getProperty("leftDB"), comparisonTaskPath);
//            PrintUtility.printMessage("Left DB\n" + leftDb.printInstances(true));
            PrintUtility.printMessage("Left DB contains " + leftDb.getSize() + " tuples");
            IDatabase rightDb = loadDatabase(properties.getProperty("rightDB"), comparisonTaskPath);
//            PrintUtility.printMessage("Right DB\n" + rightDb.printInstances(true));
            PrintUtility.printMessage("Right DB contains " + rightDb.getSize() + " tuples");
            String strategy = properties.getProperty("strategy");
            PrintUtility.printMessage("Strategy: " + strategy);
            IComputeInstanceSimilarity similarityChecker = getSimilarityChecker(strategy);
            long start = System.currentTimeMillis();
            PrintUtility.printInformation("Starting execution: " + SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US).format(new Date()));
            InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
            if (logger.isTraceEnabled()) logger.trace(result.toString());
            long totalTime = System.currentTimeMillis() - start;
            PrintUtility.printInformation("Total Time: " + totalTime + " ms");
            PrintUtility.printInformation("Score: " + result.getTupleMapping().getScore());
            PrintUtility.printMessage("Non matching left tuples: " + result.getTupleMapping().getLeftNonMatchingTuples().size());
            if (!result.getTupleMapping().getLeftNonMatchingTuples().isEmpty() && result.getTupleMapping().getLeftNonMatchingTuples().size() < 5) {
                PrintUtility.printMessage(SpeedyUtility.printCollection(result.getTupleMapping().getLeftNonMatchingTuples(), "\t"));
            }
            PrintUtility.printMessage("Non matching right tuples: " + result.getTupleMapping().getRightNonMatchingTuples().size());
            if (!result.getTupleMapping().getRightNonMatchingTuples().isEmpty() && result.getTupleMapping().getRightNonMatchingTuples().size() < 5) {
                PrintUtility.printMessage(SpeedyUtility.printCollection(result.getTupleMapping().getRightNonMatchingTuples(), "\t"));
            }
            PrintUtility.printMessage(ComparisonStats.getInstance().toString());
            PrintUtility.printMessage("--------------------------------------------------");
        } catch (Exception ex) {
            PrintUtility.printMessage("Unable to load task. " + ex.getLocalizedMessage());
        }
    }

    private static void loadComparisonConfiguration(Properties properties) {
        try {
            ComparisonConfiguration.setScenario(properties.getProperty("scenario"));
            ComparisonConfiguration.setFunctional(Boolean.parseBoolean(properties.getProperty("functional")));
            ComparisonConfiguration.setInjective(Boolean.parseBoolean(properties.getProperty("injective")));
            ComparisonConfiguration.setK(Double.parseDouble(properties.getProperty("K")));
        } catch (Exception e) {
            PrintUtility.printError("Unable to load task. " + e.getLocalizedMessage());
            System.exit(0);
        }
    }

    private static IDatabase loadDatabase(String dbFolder, String comparisonTaskPath) {
        File file = new File(dbFolder);
        if (!file.isAbsolute()) {
            file = new File(new File(comparisonTaskPath).getParentFile().getAbsoluteFile() + File.separator + dbFolder);
        }
        String absolutePath = file.toString();
        PrintUtility.printMessage("Loading database from folder: " + absolutePath);
        long start = System.currentTimeMillis();
        boolean convertSkolemInHash = ComparisonConfiguration.isConvertSkolemInHash();
        MainMemoryDB database = daoDatabase.loadCSVDatabase(absolutePath, ',', null, convertSkolemInHash, true);
        ComparisonStats.getInstance().addStat(ComparisonStats.LOAD_INSTANCE_TIME, System.currentTimeMillis() - start);
        return database;
    }

    private static IComputeInstanceSimilarity getSimilarityChecker(String strategy) {
        if (strategy.equalsIgnoreCase("BRUTEFORCE")) {
            return similarityCheckerBruteforce;
        }
        if (strategy.equalsIgnoreCase("SIGNATURE")) {
            return similarityCheckerHashing;
        }
        throw new IllegalArgumentException("Unknown strategy " + strategy);
    }

    private static void printUsage() {
        PrintUtility.printMessage("****************    Usage   ****************\n");
        PrintUtility.printMessage("compare <propertiesFile>");
    }

    private static void printSystemInformation() {
        PrintUtility.printMessage("##################################### ");
        PrintUtility.printMessage(
                "#  ___   _   ___ _____ \n"
                + "# | _ ) /_\\ | _ \\_   _|\n"
                + "# | _ \\/ _ \\|   / | |  \n"
                + "# |___/_/ \\_\\_|_\\ |_|  \n"
                + "#                      ");
        PrintUtility.printMessage("##################################### ");
        int mb = 1024 * 1024;
        PrintUtility.printMessage("# OS            : " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
        PrintUtility.printMessage("# CPU           : " +  getCPUModel());
        PrintUtility.printMessage("# CPU Cores     : " + Runtime.getRuntime().availableProcessors());
        PrintUtility.printMessage("# Max Memory    : " + Runtime.getRuntime().maxMemory() / mb + " MB");
        PrintUtility.printMessage("# Free Memory   : " + Runtime.getRuntime().freeMemory() / mb + " MB");
        PrintUtility.printMessage("# Used Memory   : " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb + " MB");
        PrintUtility.printMessage("# Total Memory  : " + Runtime.getRuntime().totalMemory() / mb + " MB");
        PrintUtility.printMessage("##################################### ");

    }

    private static String getCPUModel() {
        String model = null;
        try {
            model = Files.lines(Paths.get("/proc/cpuinfo"))
                    .filter(line -> line.startsWith("model name"))
                    .map(line -> line.replaceAll(".*: ", ""))
                    .findFirst().orElse("");
        } catch (Exception e) {
        }
        if (model == null) {
            try {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec("sysctl -n machdep.cpu.brand_string");
                BufferedReader lineReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                model = lineReader.readLine();
            } catch (Exception e) {
                logger.error("Unable to retrieve CPU information", e);
            }
        }
        if (model == null) {
            model = "unknown";
        }
        return model;
    }

}
