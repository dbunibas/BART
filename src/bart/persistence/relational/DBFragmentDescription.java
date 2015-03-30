package bart.persistence.relational;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DBFragmentDescription {
    
    private List<String> listOfInclusionPaths = new ArrayList<String>();
    private List<String> listOfExclusionPaths = new ArrayList<String>();
    
    public static final String ALL = "*.*";

    public static DBFragmentDescription getAllDBDescription() {
        DBFragmentDescription description = new DBFragmentDescription();
        description.addInclusionPath(ALL);
        return description;
    }

    public DBFragmentDescription() {}
    
    public void addInclusionPath(String path) {
        assert(isValidPattern(path)) : "Inclusion Path is not a valid pattern: " + path;
        assert(this.listOfInclusionPaths.isEmpty() || (!this.listOfInclusionPaths.isEmpty() && !isAllPattern(path))) : "*.* is not compatible with other patterns of inclusion";
        assert(!this.listOfInclusionPaths.contains(ALL)) : "*.* is NOT compatible with other patterns of inclusion";
        assert(!this.listOfExclusionPaths.contains(path)) : "Found the same entry in listOfExclusionPaths";
        this.listOfInclusionPaths.add(path);
    }
    
    public void addExclusionPath(String path) {
        assert(isValidPattern(path) && !isAllPattern(path)) : "Cannot use *.* as an exclusion path: ";
        assert(!this.listOfInclusionPaths.contains(path)) : "Found the same entry in listOfInclusionPaths";
        this.listOfExclusionPaths.add(path);
    }
    
    private boolean isValidPattern(String pattern) {
        return (isAllPattern(pattern) || isTablePattern(pattern) || isAttributePattern(pattern));
    }
    
    private boolean isAllPattern(String pattern) {
        return Pattern.matches("[*][.][*]", pattern);
    }
    
    private boolean isTablePattern(String pattern) {
        return Pattern.matches("\\w+[.][*]", pattern);
    }
    
    private boolean isAttributePattern(String pattern) {
        return Pattern.matches("\\w+[.]\\w+", pattern);
    }
    
    private boolean containsAttributePattern(String tablePattern) {
        assert(isTablePattern(tablePattern)) : "containsAttributePattern(): variable tablePattern is not a table pattern: " + tablePattern;
        String rootTablePattern = tablePattern.substring(0, tablePattern.indexOf(".") - 1);
        for (String inclusionPath : this.listOfInclusionPaths) {
            if (inclusionPath.contains(rootTablePattern)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean checkLoadTable(String tableName) {
        String tablePattern = tableName + ".*";
        if (this.listOfExclusionPaths.contains(tablePattern)) {
            return false;
        }
        if (this.listOfInclusionPaths.isEmpty() || this.listOfInclusionPaths.contains(this.ALL)) {
            return true;
        }
        if (this.listOfInclusionPaths.contains(tablePattern) || this.containsAttributePattern(tablePattern)) {
            return true;
        }
        
        return false;
    }
 
    public boolean checkLoadAttribute(String tableName, String attributeName) {
        String pattern = tableName + "." + attributeName;
        assert(isAttributePattern(pattern)) : "it's not an attribute pattern: " + pattern;
        String patternTable = tableName + ".*";
        assert(isTablePattern(patternTable)) : "it's not a table pattern: " + patternTable;
        if (this.listOfExclusionPaths.contains(pattern)) {
            return false;
        }
        if (this.listOfInclusionPaths.contains(pattern)) {
            return true;
        }
        if (this.listOfInclusionPaths.isEmpty() || this.listOfInclusionPaths.contains(this.ALL)) {
            return true;
        }
        if (this.listOfInclusionPaths.contains(patternTable)) {
            return true;
        }
        return false;
    }
    
    public String toString() {
        String result = "\n------------DATA DESCRIPTION---------------\n";
        result += "-----Inclusion List\n";
        for (String path : listOfInclusionPaths) {
            result +="\tPath = "+ path +"\n";
        }
        result += "-----Exclusion List\n";
        for (String path : listOfExclusionPaths){
            result +="\tPath = "+ path +"\n";
        }
        result += "----------------------------------------\n";
        return result;
    }
    
    
    
}
