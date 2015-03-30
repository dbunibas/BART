package bart.model.database.dbms;

import java.util.ArrayList;
import java.util.List;

public class InitDBConfiguration {

    private String initDBScript;
    private List<String> xmlFilesToImport = new ArrayList<String>();
    private boolean createTablesFromXML = true;

    public String getInitDBScript() {
        return initDBScript;
    }

    public void setInitDBScript(String initDBScript) {
        this.initDBScript = initDBScript;
    }

    public List<String> getXmlFilesToImport() {
        return xmlFilesToImport;
    }

    public void setXmlFilesToImport(List<String> xmlFilesToImport) {
        this.xmlFilesToImport = xmlFilesToImport;
    }

    public void addXmlFileToImport(String xmlFileToImport) {
        this.xmlFilesToImport.add(xmlFileToImport);
    }

    public boolean isCreateTablesFromXML() {
        return createTablesFromXML;
    }

    public void setCreateTablesFromXML(boolean createTablesFromXML) {
        this.createTablesFromXML = createTablesFromXML;
    }

    public boolean isEmpty() {
        return initDBScript == null && xmlFilesToImport.isEmpty();
    }

    @Override
    public String toString() {
        return "InitDBConfiguration{" + "initDBScript=" + initDBScript + ", xmlFilesToImport=" + xmlFilesToImport + ", createTablesFromXML=" + createTablesFromXML + '}';
    }
    
    

}
