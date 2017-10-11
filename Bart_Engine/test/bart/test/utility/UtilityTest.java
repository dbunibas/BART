package bart.test.utility;

import bart.model.EGTask;
import bart.persistence.DAOEGTask;
import speedy.model.database.ITable;
import bart.model.errorgenerator.VioGenQuery;
import speedy.persistence.relational.AccessConfiguration;
import bart.utility.BartDBMSUtility;
import speedy.persistence.xml.DAOXmlUtility;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.jdom.Document;
import org.jdom.Element;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DAOException;

public class UtilityTest {

    private static Logger logger = LoggerFactory.getLogger(UtilityTest.class);
    private static DAOXmlUtility daoUtility = new DAOXmlUtility();
    private static Runtime runtime = Runtime.getRuntime();
    public static final String RESOURCES_FOLDER = "/resources/";
    public static final String EXPERIMENTS_FOLDER = "/experiments/";

    public static String getResultDir() {
        return System.getProperty("user.home") + "/Dropbox/BartExp/";
    }

    public static EGTask loadEGTaskFromResources(String fileTask) {
        return loadEGTaskFromResources(fileTask, false);
    }

    public static EGTask loadEGTaskFromAbsolutePath(String fileTask) {
        if (logger.isDebugEnabled()) logger.debug("Loading task " + fileTask);
        return loadEGTaskFromAbsolutePath(fileTask, false);
    }

    public static EGTask loadEGTaskFromResources(String fileTask, boolean recreateDB) {
        try {
            fileTask = RESOURCES_FOLDER + fileTask;
            URL taskURL = UtilityTest.class.getResource(fileTask);
            Assert.assertNotNull("Load task " + fileTask, taskURL);
            fileTask = new File(taskURL.toURI()).getAbsolutePath();
            return loadEGTask(fileTask, recreateDB);
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            Assert.fail(ex.getLocalizedMessage());
            return null;
        }
    }

    public static String getExperimentsFolder(String fileTask) {
        String experimentsFolder = UtilityTest.getExternalFolder(EXPERIMENTS_FOLDER);
        return experimentsFolder + fileTask;
    }

    public static String getResourcesFolder(String fileTask) {
        String resourcesFolder = UtilityTest.getExternalFolder(RESOURCES_FOLDER      );
        return resourcesFolder + fileTask;
    }

    public static EGTask loadEGTaskFromAbsolutePath(String fileTask, boolean recreateDB) {
        return loadEGTask(fileTask, recreateDB);
    }

    private static EGTask loadEGTask(String fileTask, boolean recreateDB) {
        try {
            if (recreateDB) {
                BartDBMSUtility.deleteDB(loadTargetAccessConfiguration(fileTask));
            }
        } catch (Exception ex) {
            logger.warn("Unable to drop database.\n" + ex.getLocalizedMessage());
        }
        try {
            DAOEGTask daoTask = new DAOEGTask();
            EGTask task = daoTask.loadTask(fileTask);
            task.setAbsolutePath(fileTask);
            return task;
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail(ex.getLocalizedMessage());
            return null;
        }
    }

    public static String getAbsoluteFileName(String fileName) {
        return UtilityTest.class.getResource(fileName).getFile();
    }

    public static String getExternalFolder(String fileName) {
        File buildDir = new File(UtilityTest.class.getResource("/").getFile()).getParentFile();
        File rootDir = buildDir.getParentFile();
        String miscDir = rootDir.toString() + File.separator + "misc";
        return miscDir + fileName;
    }

    public static long getSize(ITable table) {
        return table.getSize();
    }

    private static AccessConfiguration loadTargetAccessConfiguration(String fileTask) {
        Document document = daoUtility.buildDOM(fileTask);
        Element rootElement = document.getRootElement();
        Element databaseElement = rootElement.getChild("target");
        Element dbmsElement = databaseElement.getChild("access-configuration");
        if (dbmsElement == null) {
            throw new DAOException("Unable to load scenario from file " + fileTask + ". Missing tag <access-configuration>");
        }
        AccessConfiguration accessConfiguration = new AccessConfiguration();
        accessConfiguration.setDriver(dbmsElement.getChildText("driver").trim());
        accessConfiguration.setUri(dbmsElement.getChildText("uri").trim());
        accessConfiguration.setSchemaName(dbmsElement.getChildText("schema").trim());
        accessConfiguration.setLogin(dbmsElement.getChildText("login").trim());
        accessConfiguration.setPassword(dbmsElement.getChildText("password").trim());
        return accessConfiguration;
    }

    public static String getMemInfo() {
        NumberFormat format = NumberFormat.getInstance(Locale.ITALIAN);
        StringBuilder sb = new StringBuilder();
        long allocatedMemory = runtime.totalMemory();
        sb.append(format.format(allocatedMemory / 1024 / 1024)).append(" MB");
        return sb.toString();

    }

    public static List<VioGenQuery> sortVioGenQueries(Collection<VioGenQuery> keySet) {
        List<VioGenQuery> sortedList = new ArrayList<VioGenQuery>(keySet);
        Collections.sort(sortedList, new VioGenQueryComparator());
        return sortedList;
    }

    private static class VioGenQueryComparator implements Comparator<VioGenQuery> {

        public int compare(VioGenQuery o1, VioGenQuery o2) {
            return o1.getDependency().getId().compareTo(o2.getDependency().getId());
        }
    }

}
