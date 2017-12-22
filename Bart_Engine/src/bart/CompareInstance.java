package bart;

import bart.comparison.ComparisonConfiguration;
import bart.comparison.ComparisonStats;
import bart.comparison.InstanceMatchTask;
import bart.comparison.operators.ComputeInstanceSimilarityHashing;
import bart.comparison.operators.ComputeInstanceSimilarityBruteForceCompatibility;
import bart.comparison.operators.IComputeInstanceSimilarity;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import speedy.model.database.IDatabase;
import speedy.model.database.mainmemory.MainMemoryDB;
import speedy.persistence.DAOMainMemoryDatabase;
import speedy.utility.PrintUtility;

public class CompareInstance {

    private final static IComputeInstanceSimilarity similarityCheckerBruteforce = new ComputeInstanceSimilarityBruteForceCompatibility();
    private final static IComputeInstanceSimilarity similarityCheckerHashing = new ComputeInstanceSimilarityHashing();
    private final static DAOMainMemoryDatabase daoDatabase = new DAOMainMemoryDatabase();

    public static void main(String args[]) {
        try {
            if (args.length != 1) {
                printUsage();
                return;
            }
            Properties properties = new Properties();
            properties.load(new FileInputStream(args[0]));
            loadComparisonConfiguration(properties);
            PrintUtility.printMessage(ComparisonConfiguration.getInstance().toString());
            IDatabase leftDb = loadDatabase(properties.getProperty("leftDB"));
            PrintUtility.printMessage("Left DB\n" + leftDb.printInstances(true));
            IDatabase rightDb = loadDatabase(properties.getProperty("rightDB"));
            PrintUtility.printMessage("Right DB\n" + rightDb.printInstances(true));
            String strategy = properties.getProperty("strategy");
            PrintUtility.printMessage("Strategy: " + strategy );
            IComputeInstanceSimilarity similarityChecker = getSimilarityChecker(strategy);
            InstanceMatchTask result = similarityChecker.compare(leftDb, rightDb);
            PrintUtility.printMessage(result.toString());
        } catch (IOException ex) {
            PrintUtility.printMessage("Unable to load task. " + ex.getLocalizedMessage());
        }
    }

    private static void loadComparisonConfiguration(Properties properties) {
        try {
            ComparisonConfiguration.setFunctional(Boolean.parseBoolean(properties.getProperty("functional")));
            ComparisonConfiguration.setInjective(Boolean.parseBoolean(properties.getProperty("injective")));
            ComparisonConfiguration.setK(Double.parseDouble(properties.getProperty("K")));
        } catch (Exception e) {
            PrintUtility.printError("Unable to load task. " + e.getLocalizedMessage());
            System.exit(0);
        }
    }

    private static IDatabase loadDatabase(String absoluteFolder) {
        long start = System.currentTimeMillis();
        boolean convertSkolemInHash = ComparisonConfiguration.isConvertSkolemInHash();
        MainMemoryDB database = daoDatabase.loadCSVDatabase(absoluteFolder, ',', null, convertSkolemInHash, true);
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

}
