package bart.persistence;

import bart.model.EGTaskConfiguration;
import bart.model.OutlierErrorConfiguration;
import bart.model.VioGenQueryConfiguration;
import bart.model.errorgenerator.OrderingAttribute;
import speedy.model.database.AttributeRef;
import bart.model.errorgenerator.operator.valueselectors.IDirtyStrategy;
import bart.model.errorgenerator.operator.valueselectors.TypoActiveDomain;
import bart.model.errorgenerator.operator.valueselectors.TypoAddString;
import bart.model.errorgenerator.operator.valueselectors.TypoAppendString;
import bart.model.errorgenerator.operator.valueselectors.TypoRandom;
import bart.model.errorgenerator.operator.valueselectors.TypoRemoveString;
import bart.model.errorgenerator.operator.valueselectors.TypoSwitchValue;
import speedy.persistence.xml.DAOXmlUtility;
import bart.utility.BartUtility;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DAOException;

public class DAOEGTaskConfiguration {

    private static Logger logger = LoggerFactory.getLogger(DAOEGTaskConfiguration.class);
    private DAOXmlUtility daoUtility = new DAOXmlUtility();

    public DAOEGTaskConfiguration() {
    }

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
        Element estimateRepairabilityElement = configurationElement.getChild("estimateRepairability");
        if (estimateRepairabilityElement != null) {
            conf.setEstimateRepairability(Boolean.parseBoolean(estimateRepairabilityElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* estimateRepairability " + conf.isEstimateRepairability());
        }
        Element exportCellChangesElement = configurationElement.getChild("exportCellChanges");
        if (exportCellChangesElement != null) {
            conf.setExportCellChanges(Boolean.parseBoolean(exportCellChangesElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* exportCellChanges " + conf.isExportCellChanges());
            if (conf.isExportCellChanges()) {
                String attributeValueFull = exportCellChangesElement.getAttributeValue("full");
                if (attributeValueFull != null) {
                    if (logger.isDebugEnabled()) logger.debug("ExportFullChanges: " + attributeValueFull.equalsIgnoreCase("true"));
                    conf.setExportCellChangesFull(attributeValueFull.equalsIgnoreCase("true"));
                }
                Element exportCellChangesPathElement = configurationElement.getChild("exportCellChangesPath");
                if (exportCellChangesPathElement == null) {
                    throw new DAOException("exportCellChanges requires the exportCellChangesPath option");
                }
                conf.setExportCellChangesPath(exportCellChangesPathElement.getText());
                if (logger.isDebugEnabled()) logger.debug("* exportCellChangesPath " + conf.getExportCellChangesPath());
            }
        }
        Element exportCleanDBElement = configurationElement.getChild("exportCleanDB");
        if (exportCleanDBElement != null) {
            conf.setExportCleanDB(Boolean.parseBoolean(exportCleanDBElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* exportCleanDB " + conf.isExportCleanDB());
            if (conf.isExportCleanDB()) {
                Element exportCleanDBPathElement = configurationElement.getChild("exportCleanDBPath");
                if (exportCleanDBPathElement == null) {
                    throw new DAOException("exportCleanDB requires the exportCleanDBPath option");
                }
                conf.setExportCleanDBPath(exportCleanDBPathElement.getText());
                if (logger.isDebugEnabled()) logger.debug("* exportCleanDBPath " + conf.getExportCleanDBPath());
                Element exportCleanDBTypeElement = configurationElement.getChild("exportCleanDBType");
                if (exportCleanDBTypeElement != null) {
                    conf.setExportCleanDBType(exportCleanDBTypeElement.getText());
                    if (logger.isDebugEnabled()) logger.debug("* exportCleanDBType " + conf.getExportCleanDBPath());
                }
            }
        }
        Element exportDirtyDBElement = configurationElement.getChild("exportDirtyDB");
        if (exportDirtyDBElement != null) {
            conf.setExportDirtyDB(Boolean.parseBoolean(exportDirtyDBElement.getText()));
            if (logger.isDebugEnabled()) logger.debug("* exportDirtyDB " + conf.isExportDirtyDB());
            if (conf.isExportDirtyDB()) {
                Element exportDirtyDBPathElement = configurationElement.getChild("exportDirtyDBPath");
                if (exportDirtyDBPathElement == null) {
                    throw new DAOException("exportDirtyDB requires the exportDirtyDBPath option");
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
            Element vioGenQueriesElement = randomConfigurationElement.getChild("vioGenQueries");
            if (vioGenQueriesElement != null) {
                for (Element vioGenQueryElement : (List<Element>) vioGenQueriesElement.getChildren("vioGenQuery")) {
                    Attribute idAttribute = getMandatoryAttribute(vioGenQueryElement, "id");
                    Element comparisonElement = getMandatoryElement(vioGenQueryElement, "comparison");
                    Element percentageElement = getMandatoryElement(vioGenQueryElement, "percentage");
                    String vioGenKey = BartUtility.getVioGenQueryKey(idAttribute.getValue(), comparisonElement.getText());
                    conf.addVioGenQueryProbabilities(vioGenKey, Double.parseDouble(percentageElement.getText()));
                }
            }
        }
        Element dirtyStrategiesElement = configurationElement.getChild("dirtyStrategies");
        if (dirtyStrategiesElement != null) {
            Element defaultStrategyElement = getMandatoryElement(dirtyStrategiesElement, "defaultStrategy");
            Element strategyElement = getMandatoryElement(defaultStrategyElement, "strategy");
            IDirtyStrategy defaultDirtyStrategy = getDirtyStrategy(strategyElement, null);
            conf.setDefaultDirtyStrategy(defaultDirtyStrategy);
            Element attributeStrategyElement = dirtyStrategiesElement.getChild("attributeStrategy");
            if (attributeStrategyElement != null) {
                for (Element attributeElement : (List<Element>) attributeStrategyElement.getChildren("attribute")) {
                    Attribute tableAttribute = getMandatoryAttribute(attributeElement, "table");
                    Attribute nameAttribute = getMandatoryAttribute(attributeElement, "name");
                    AttributeRef attributeRef = new AttributeRef(tableAttribute.getValue().trim(), nameAttribute.getValue().trim());
                    Element strategyElementAttribute = getMandatoryElement(attributeElement, "strategy");
                    IDirtyStrategy attributeDirtyStrategy = getDirtyStrategy(strategyElementAttribute, attributeRef);
                    conf.addDirtyStrategyForAttribute(attributeRef, attributeDirtyStrategy);
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
        Element randomErrorsConfiguration = configurationElement.getChild("randomErrors");
        if (randomErrorsConfiguration != null) {
            conf.setRandomErrors(true);
            if (logger.isDebugEnabled()) logger.debug("* Generating Random Errors " + conf.isRandomErrors());
            Element tablesElement = getMandatoryElement(randomErrorsConfiguration, "tables");
            List<Element> tables = getMandatoryElements(tablesElement, "table");
            for (Element table : tables) {
                Attribute tableName = getMandatoryAttribute(table, "name");
                Element percentageErrorElement = table.getChild("percentage");
                Element attributesElement = getMandatoryElement(table, "attributes");
                List<Element> attributeElements = getMandatoryElements(attributesElement, "attribute");
                Set<String> attributes = new HashSet<String>();
                for (Element attributeElem : attributeElements) {
                    attributes.add(attributeElem.getTextTrim());
                }
                if (logger.isDebugEnabled()) logger.debug("\t* Table: " + tableName.getValue() + " --- Percentage: " + percentageErrorElement.getTextTrim() + " --- Attributes to dirty: " + attributes);
                conf.addTableForRandomErrors(tableName.getValue(), attributes);
                conf.addPercentageForRandomErrors(tableName.getValue(), Double.parseDouble(percentageErrorElement.getTextTrim()));
            }
        }
        Element outlierErrorsConfiguration = configurationElement.getChild("outlierErrors");
        if (outlierErrorsConfiguration != null) {
            conf.setOutlierErrors(true);
            if (logger.isDebugEnabled()) logger.debug("* Generating Outlier Errors " + conf.isRandomErrors());
            OutlierErrorConfiguration outlierErrorConfiguration = extractOutlierErrorConfiguration(outlierErrorsConfiguration);
            conf.setOutlierErrorConfiguration(outlierErrorConfiguration);
        }
        Element partialOrderConfiguration = configurationElement.getChild("partialOrder");
        if (partialOrderConfiguration != null) {
            if (logger.isDebugEnabled()) logger.debug("* Partial order configuration");
            List<Element> dependencies = getMandatoryElements(partialOrderConfiguration, "dependency");
            for (Element dependency : dependencies) {
                Element nameElement = getMandatoryElement(dependency, "name");
                if (logger.isDebugEnabled()) logger.debug("Dependecy name: " + nameElement.getText().trim());
                Element attributeElement = getMandatoryElement(dependency, "attribute");
                Attribute attributeOrder = attributeElement.getAttribute("ordering");
                Attribute attributeTable = getMandatoryAttribute(attributeElement, "table");
                String attributeName = attributeElement.getText().trim();
                String ordering = attributeOrder != null ? attributeOrder.getValue().trim() : OrderingAttribute.ASC;
                OrderingAttribute orderingAttribute = new OrderingAttribute(attributeName, attributeTable.getValue().trim(), ordering);
                if (logger.isDebugEnabled()) logger.debug(orderingAttribute.toString());
                conf.getVioGenOrderingAttributes().put(nameElement.getText().trim(), orderingAttribute);
            }
        }
        Element vioGenQueriesConfiguration = configurationElement.getChild("vioGenQueriesConfiguration");
        if (vioGenQueriesConfiguration != null) {
            if (logger.isDebugEnabled()) logger.debug("* VioGenQueries configuration");
            VioGenQueryConfiguration defaultConfig = conf.getDefaultVioGenQueryConfiguration();
            Double probabilityFactorForSymmetricQueries = extractDouble(vioGenQueriesConfiguration, "probabilityFactorForSymmetricQueries");
            if (probabilityFactorForSymmetricQueries != null) {
                defaultConfig.setProbabilityFactorForSymmetricQueries(probabilityFactorForSymmetricQueries);
            }
            Double probabilityFactorForStandardQueries = extractDouble(vioGenQueriesConfiguration, "probabilityFactorForStandardQueries");
            if (probabilityFactorForStandardQueries != null) {
                defaultConfig.setProbabilityFactorForStandardQueries(probabilityFactorForStandardQueries);
            }
            Double offsetFactorForStandardQueries = extractDouble(vioGenQueriesConfiguration, "offsetFactorForStandardQueries");
            if (offsetFactorForStandardQueries != null) {
                defaultConfig.setOffsetFactorForStandardQueries(offsetFactorForStandardQueries);
                defaultConfig.setUseOffsetInStandardQueries(true);
            }
            Double offsetFactorForSymmetricQueries = extractDouble(vioGenQueriesConfiguration, "offsetFactorForSymmetricQueries");
            if (offsetFactorForSymmetricQueries != null) {
                defaultConfig.setOffsetFactorForSymmetricQueries(offsetFactorForSymmetricQueries);
                defaultConfig.setUseOffsetInSymmetricQueries(true);
            }
            Double probabilityFactorForInequalityQueries = extractDouble(vioGenQueriesConfiguration, "probabilityFactorForInequalityQueries");
            if (probabilityFactorForInequalityQueries != null) {
                defaultConfig.setProbabilityFactorForInequalityQueries(probabilityFactorForInequalityQueries);
            }
            Double offsetFactorForInequalityQueries = extractDouble(vioGenQueriesConfiguration, "offsetFactorForInequalityQueries");
            if (offsetFactorForInequalityQueries != null) {
                defaultConfig.setOffsetFactorForInequalityQueries(offsetFactorForInequalityQueries);
                defaultConfig.setUseOffsetInInequalityQueries(true);
            }
            Double windowSizeFactorForInequalityQueries = extractDouble(vioGenQueriesConfiguration, "windowSizeFactorForInequalityQueries");
            if (windowSizeFactorForInequalityQueries != null) {
                defaultConfig.setWindowSizeFactorForInequalityQueries(windowSizeFactorForInequalityQueries);
            }
        }
        Element autoSelectBestNumberOfThreadsElement = configurationElement.getChild("autoSelectBestNumberOfThreads");
        if (autoSelectBestNumberOfThreadsElement != null) {
            conf.setAutoSelectBestNumberOfThreads(Boolean.parseBoolean(autoSelectBestNumberOfThreadsElement.getValue()));
        }
        Element maxNumberOfThreadsElement = configurationElement.getChild("maxNumberOfThreads");
        if (maxNumberOfThreadsElement != null) {
            conf.setMaxNumberOfThreads(Integer.parseInt(maxNumberOfThreadsElement.getValue()));
        }
        if (conf.isAutoSelectBestNumberOfThreads()) {
            selectBestNumberOfThreads(conf);
        }
        if (conf.isPrintLog()) System.out.println("Using " + conf.getMaxNumberOfThreads() + " threads");
        return conf;
    }

    private OutlierErrorConfiguration extractOutlierErrorConfiguration(Element outlierErrorsTag) {
        OutlierErrorConfiguration outlierErrorConfiguration = new OutlierErrorConfiguration();
        Element tablesElement = getMandatoryElement(outlierErrorsTag, "tables");
        List<Element> tables = getMandatoryElements(tablesElement, "table");
        for (Element table : tables) {
            Attribute tableName = getMandatoryAttribute(table, "name");
            Element attributesElement = getMandatoryElement(table, "attributes");
            List<Element> attributeElements = getMandatoryElements(attributesElement, "attribute");
            for (Element attribute : attributeElements) {
                Attribute percentageAttribute = getMandatoryAttribute(attribute, "percentage");
                Attribute detectableAttribute = getMandatoryAttribute(attribute, "detectable");
                String attributeName = attribute.getTextTrim();
                double percentage = Double.parseDouble(percentageAttribute.getValue().trim());
                boolean detectable = Boolean.parseBoolean(detectableAttribute.getValue().trim());
                outlierErrorConfiguration.addAttributes(tableName.getValue().trim(), attributeName, percentage, detectable);
            }
        }
        return outlierErrorConfiguration;
    }

    private IDirtyStrategy getDirtyStrategy(Element strategyElement, AttributeRef attributeRef) {
        String strategyName = strategyElement.getText().trim();
        if (strategyName.equals(IDirtyStrategy.TYPO_ADD_STRING)) {
            Attribute charsAttribute = getMandatoryAttribute(strategyElement, "chars");
            Attribute charsToAddAttribute = getMandatoryAttribute(strategyElement, "charsToAdd");
            String charsString = charsAttribute.getValue().trim();
            int times = Integer.parseInt(charsToAddAttribute.getValue().trim());
            return new TypoAddString(charsString, times);
        }
        if (strategyName.equals(IDirtyStrategy.TYPO_APPEND_STRING)) {
            Attribute charsAttribute = getMandatoryAttribute(strategyElement, "chars");
            Attribute charsToAddAttribute = getMandatoryAttribute(strategyElement, "charsToAdd");
            String charsString = charsAttribute.getValue().trim();
            int times = Integer.parseInt(charsToAddAttribute.getValue().trim());
            return new TypoAppendString(charsString, times);
        }
        if (strategyName.equals(IDirtyStrategy.TYPO_RANDOM)) {
            return new TypoRandom();
        }
        if (strategyName.equals(IDirtyStrategy.TYPO_REMOVE_STRING)) {
            Attribute charsToAddAttribute = getMandatoryAttribute(strategyElement, "charsToRemove");
            int times = Integer.parseInt(charsToAddAttribute.getValue().trim());
            return new TypoRemoveString(times);
        }
        if (strategyName.equals(IDirtyStrategy.TYPO_SWITCH_VALUE)) {
            Attribute charsToAddAttribute = getMandatoryAttribute(strategyElement, "charsToSwitch");
            int times = Integer.parseInt(charsToAddAttribute.getValue().trim());
            return new TypoSwitchValue(times);
        }
        if (strategyName.equals(IDirtyStrategy.TYPO_ACTIVE_DOMAIN)) {
            if (attributeRef == null) {
                throw new IllegalArgumentException("Unable to set " + IDirtyStrategy.TYPO_ACTIVE_DOMAIN + " as default strategy");
            }
            return new TypoActiveDomain(attributeRef);
        }
        throw new DAOException("Unable to load dirty strategy for: " + strategyName);
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

    @SuppressWarnings("unchecked")
    private List<Element> getMandatoryElements(Element father, String elementName) {
        assert (father != null) : "Unable to get elements from null node";
        List<Element> childred = father.getChildren(elementName);
        if (childred == null) {
            throw new DAOException("Unable to load configuration. Missing required tag <" + elementName + ">");
        }
        return childred;
    }

    private Double extractDouble(Element father, String attributeName) {
        Element child = father.getChild(attributeName);
        if (child == null) {
            return null;
        }
        double value = 0;
        try {
            value = Double.parseDouble(child.getValue().trim());
        } catch (NumberFormatException numberFormatException) {
            throw new DAOException("Unable to load configuration." + attributeName + " value must be a double");
        }
        return value;
    }

    private void selectBestNumberOfThreads(EGTaskConfiguration configuration) {
        int cores = Runtime.getRuntime().availableProcessors();
//        int threads = (cores * 2) - 1;
        int threads = cores;
        configuration.setMaxNumberOfThreads(threads);
    }
}
