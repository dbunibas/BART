package bart.persistence;

import bart.exceptions.DAOException;
import bart.model.EGTaskConfiguration;
import bart.model.RepairabilityRange;
import bart.persistence.xml.DAOXmlUtility;
import bart.utility.BartUtility;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DAOEGTaskConfiguration {

    private static Logger logger = LoggerFactory.getLogger(DAOEGTaskConfiguration.class);
    private DAOXmlUtility daoUtility = new DAOXmlUtility();

    public EGTaskConfiguration loadConfiguration(String fileTask) {
        try {
            Document document = daoUtility.buildDOM(fileTask);
            Element rootElement = document.getRootElement();
            Element configurationElement = rootElement.getChild("configuration");
            return loadConfiguration(configurationElement, fileTask);
        } catch (Throwable ex) {
            logger.error(ex.getLocalizedMessage());
            ex.printStackTrace();
            String message = "Unable to load egtask from file " + fileTask;
            if (ex.getMessage() != null && !ex.getMessage().equals("NULL")) {
                message += "\n" + ex.getMessage();
            }
            throw new DAOException(message);
        }
    }

    @SuppressWarnings("unchecked")
    public EGTaskConfiguration loadConfiguration(Element configurationElement, String fileTask) {
        EGTaskConfiguration conf = new EGTaskConfiguration();
        if (configurationElement == null) {
            return conf;
        }
        Element printElement = configurationElement.getChild("printLog");
        if (printElement != null) {
            conf.setPrintLog(Boolean.parseBoolean(printElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* printLog " + conf.isPrintLog());
        }
        Element checkChangesElement = configurationElement.getChild("checkChanges");
        if (checkChangesElement != null) {
            conf.setCheckChanges(Boolean.parseBoolean(checkChangesElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* checkChanges " + conf.isCheckChanges());
        }
        Element recreateDBElement = configurationElement.getChild("recreateDBOnStart");
        if (recreateDBElement != null) {
            conf.setRecreateDBOnStart(Boolean.parseBoolean(recreateDBElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* recreateDBOnStart " + conf.isRecreateDBOnStart());
        }
        Element applyChangesElement = configurationElement.getChild("applyCellChanges");
        if (applyChangesElement != null) {
            conf.setApplyCellChanges(Boolean.parseBoolean(applyChangesElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* applyCellChanges " + conf.isApplyCellChanges());
        }
        Element exportCellChangesElement = configurationElement.getChild("exportCellChanges");
        if (exportCellChangesElement != null) {
            conf.setExportCellChanges(Boolean.parseBoolean(exportCellChangesElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* exportCellChanges " + conf.isExportCellChanges());
            if (conf.isExportCellChanges()) {
                Element exportCellChangesPathElement = configurationElement.getChild("exportCellChangesPath");
                if (exportCellChangesPathElement == null) {
                    throw new DAOException("exportCellChanges requires the exportCellChangesPath option");
                }
                conf.setExportCellChangesPath(exportCellChangesPathElement.getText());
                if (logger.isDebugEnabled()) logger.debug("* exportCellChangesPath " + conf.getExportCellChangesPath());
            }
        }
        Element exportDirtyDBElement = configurationElement.getChild("exportDirtyDB");
        if (exportDirtyDBElement != null) {
            conf.setExportDirtyDB(Boolean.parseBoolean(exportDirtyDBElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* exportDiryDB " + conf.isExportDirtyDB());
            if (conf.isExportDirtyDB()) {
                Element exportDirtyDBPathElement = configurationElement.getChild("exportDirtyDBPath");
                if (exportDirtyDBPathElement == null) {
                    throw new DAOException("exportDiryDB requires the exportDirtyDBPath option");
                }
                conf.setExportDirtyDBPath(exportDirtyDBPathElement.getText());
                if (logger.isDebugEnabled()) logger.debug("* exportDirtyDBPath " + conf.getExportDirtyDBPath());
                Element exportDirtyDBTypeElement = configurationElement.getChild("exportDirtyDBType");
                if (exportDirtyDBTypeElement != null) {
                    conf.setExportDirtyDBType(exportDirtyDBTypeElement.getText());
                    if (logger.isDebugEnabled()) logger.debug("* exportDirtyDBType " + conf.getExportDirtyDBPath());
                }
            }
        }
        Element useDeltaDBForChangesElement = configurationElement.getChild("useDeltaDBForChanges");
        if (useDeltaDBForChangesElement != null) {
            conf.setUseDeltaDBForChanges(Boolean.parseBoolean(useDeltaDBForChangesElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* useDeltaDBForChanges " + conf.isUseDeltaDBForChanges());
        }
        Element applyChangesOnClonedDBElement = configurationElement.getChild("cloneTargetSchema");
        if (applyChangesOnClonedDBElement != null) {
            conf.setCloneTargetSchema(Boolean.parseBoolean(applyChangesOnClonedDBElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* cloneTargetSchema " + conf.isCloneTargetSchema());
        }
        Element cloneSuffixElement = configurationElement.getChild("cloneSuffix");
        if (cloneSuffixElement != null) {
            conf.setCloneSuffix(cloneSuffixElement.getText());
            if (logger.isDebugEnabled()) logger.debug("* cloneSuffix " + conf.getCloneSuffix());
        }
        Element generateAllChangesElement = configurationElement.getChild("generateAllChanges");
        if (generateAllChangesElement != null) {
            conf.setGenerateAllChanges(Boolean.parseBoolean(generateAllChangesElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* generateAllChanges " + conf.isGenerateAllChanges());
        }
        Element avoidInteractionsElement = configurationElement.getChild("avoidInteractions");
        if (avoidInteractionsElement != null) {
            conf.setAvoidInteractions(Boolean.parseBoolean(avoidInteractionsElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* avoidInteractions " + conf.isAvoidInteractions());
        }
        Element randomConfigurationElement = configurationElement.getChild("errorPercentages");
        if (randomConfigurationElement != null) {
            if (conf.isGenerateAllChanges()) {
                throw new DAOException("The errorPercentages configuration is not compatibile with generateAllChanges");
            }
            Element defaultPercentageElement = getMandatoryElement(randomConfigurationElement, "defaultPercentage");
            double defaultPercentage = Double.parseDouble(defaultPercentageElement.getText());
            conf.getDefaultVioGenQueryConfiguration().setPercentage(defaultPercentage);
            RepairabilityRange defaultRepairabilityRange = readRepairabilityRange(randomConfigurationElement.getChild("defaultRepairabilityRange"));
            if (defaultRepairabilityRange != null) {
                conf.getDefaultVioGenQueryConfiguration().setRepairabilityRange(defaultRepairabilityRange);
            }
            Element vioGenQueriesElement = randomConfigurationElement.getChild("vioGenQueries");
            if (vioGenQueriesElement != null) {
                for (Element vioGenQueryElement : (List<Element>) vioGenQueriesElement.getChildren("vioGenQuery")) {
                    Attribute idAttribute = getMandatoryAttribute(vioGenQueryElement, "id");
                    Element comparisonElement = getMandatoryElement(vioGenQueryElement, "comparison");
                    Element percentageElement = getMandatoryElement(vioGenQueryElement, "percentage");
                    String vioGenKey = BartUtility.getVioGenQueryKey(idAttribute.getValue(), comparisonElement.getText());
                    conf.addVioGenQueryProbability(vioGenKey, Double.parseDouble(percentageElement.getText()));
                    RepairabilityRange vioGenQueryRepairabilityRange = readRepairabilityRange(vioGenQueryElement.getChild("repairabilityRange"));
                    if (vioGenQueryRepairabilityRange != null) {
                        conf.addVioGenQueryRepairabilityRange(vioGenKey, vioGenQueryRepairabilityRange);
                    }
                }
            }
        }
        Element executorConfigurationElement = configurationElement.getChild("queryExecutors");
        if (executorConfigurationElement != null) {
            Element vioGenQueriesElement = executorConfigurationElement.getChild("vioGenQueries");
            if (vioGenQueriesElement != null) {
                for (Element vioGenQueryElement : (List<Element>) vioGenQueriesElement.getChildren("vioGenQuery")) {
                    Attribute idAttribute = getMandatoryAttribute(vioGenQueryElement, "id");
                    Element comparisonElement = getMandatoryElement(vioGenQueryElement, "comparison");
                    Element queryExecutorElement = getMandatoryElement(vioGenQueryElement, "queryExecutor");
                    String vioGenKey = BartUtility.getVioGenQueryKey(idAttribute.getValue(), comparisonElement.getText());
                    conf.addVioGenQueryStrategy(vioGenKey, queryExecutorElement.getText());
                }
            }
        }
        return conf;
    }

    private RepairabilityRange readRepairabilityRange(Element repairabilityRangeElement) {
        if (repairabilityRangeElement == null) {
            return null;
        }
        RepairabilityRange range = new RepairabilityRange();
        String minValue = repairabilityRangeElement.getChildText("minValue");
        if (minValue != null) {
            double value = Double.parseDouble(minValue);
            range.setMinValue(value);
        }
        String maxValue = repairabilityRangeElement.getChildText("maxValue");
        if (maxValue != null) {
            double value = Double.parseDouble(maxValue);
            range.setMaxValue(value);
        }
        return range;
    }

    private Element getMandatoryElement(Element father, String elementName) {
        assert (father != null) : "Unable to get elements from null node";
        Element child = father.getChild(elementName);
        if (child == null) {
            throw new DAOException("Unable to load configuration. Missing required tag <" + elementName + ">");
        }
        return child;
    }

    private Attribute getMandatoryAttribute(Element father, String attributeName) {
        assert (father != null) : "Unable to get attributes from null node";
        Attribute attribute = father.getAttribute(attributeName);
        if (attribute == null) {
            throw new DAOException("Unable to load configuration. Missing attribute " + attributeName);
        }
        return attribute;
    }
}
