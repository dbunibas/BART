package bart.test.comparison;

import junit.framework.TestCase;
import bart.comparison.InstanceMatchTask;
import bart.comparison.operators.ComputeInstanceSimilarityHashing;
import speedy.model.database.IDatabase;
import speedy.model.database.dbms.DBMSDB;
import speedy.model.database.dbms.InitDBConfiguration;
import speedy.persistence.DAODBMSDatabase;
import speedy.persistence.file.CSVFile;
import speedy.utility.test.UtilityForTests;

public class TestCompareGroundInstances extends TestCase {

    private ComputeInstanceSimilarityHashing instanceComparator = new ComputeInstanceSimilarityHashing();
    private static String BASE_FOLDER = "/resources/comparison/";

    public void testInstance1() {
        IDatabase expected = loadDatabase("01", "expected");
        IDatabase generated = loadDatabase("01", "generated");
        InstanceMatchTask result = instanceComparator.compare(expected, generated);
//        assertEquals(1.0, result.getSimilarityForTable("r").getFMeasure());
    }

    private IDatabase loadDatabase(String expName, String schemaName) {
        DAODBMSDatabase daoDatabase = new DAODBMSDatabase();
        String driver = "org.postgresql.Driver";
        String uri = "jdbc:postgresql:speedy_comparison_" + expName;
        String login = "pguser";
        String password = "pguser";
        DBMSDB database = daoDatabase.loadDatabase(driver, uri, schemaName, login, password);
        InitDBConfiguration initDBConfiguration = database.getInitDBConfiguration();
        initDBConfiguration.setCreateTablesFromFiles(true);
        String baseAbsFolder = UtilityForTests.getAbsoluteFileName(BASE_FOLDER);
        initDBConfiguration.addFileToImportForTable("r", new CSVFile(baseAbsFolder + expName + "-" + schemaName + ".csv", ',', '"'));
        database.initDBMS();
        return database;
    }

}
