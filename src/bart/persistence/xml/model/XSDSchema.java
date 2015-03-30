package bart.persistence.xml.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XSDSchema {
    
    private static Logger logger = LoggerFactory.getLogger(XSDSchema.class);
    
    private IXSDNode root;
    // constraints' names must be different
    // key's name: PK
    // keyref's name: FK|PK
    private Map<String,List<String[]>> mapOfUniqueConstraints = new HashMap<String,List<String[]>>();
    private Map<String,List<String[]>> mapOfKeyConstraints = new HashMap<String,List<String[]>>();
    private Map<String,List<String[]>> mapOfForeignKeyConstraints = new HashMap<String,List<String[]>>();

    public IXSDNode getRoot() {
        return root;
    }

    public void setRoot(IXSDNode root) {
        this.root = root;
    }

    public Map<String, List<String[]>> getMapOfKeyConstraints() {
        return mapOfKeyConstraints;
    }
    
    public Map<String, List<String[]>> getMapOfUniqueConstraints() {
        return mapOfUniqueConstraints;
    }
    
    public Map<String, List<String[]>> getMapOfForeignKeyConstraints() {
        return mapOfForeignKeyConstraints;
    }
    
    public void addKeyXPathStr(String constraintName, String[] vectStrs){
        List<String[]> vectors = getMapOfKeyConstraints().get(constraintName);
        if(vectors == null){
            vectors = new ArrayList<String[]>();
            getMapOfKeyConstraints().put(constraintName, vectors);
        }
        vectors.add(vectStrs);
    }
    
    public void addUniqueXPathStr(String constraintName, String[] vectStrs){
        List<String[]> vectors = getMapOfUniqueConstraints().get(constraintName);
        if(vectors == null){
            vectors = new ArrayList<String[]>();
            getMapOfUniqueConstraints().put(constraintName, vectors);
        }
        vectors.add(vectStrs);
    }
    
    public List<String[]> getUniqueXPathStrs(String constraintName){
        return getMapOfUniqueConstraints().get(constraintName);
    }
    
    public List<String[]> getKeyXPathStrs(String constraintName){
        return getMapOfKeyConstraints().get(constraintName);
    }
    
    public void addForeignKeyXPathStr(String constraintName, String[] vectStrs){
        List<String[]> vectors = getMapOfForeignKeyConstraints().get(constraintName);
        if(vectors == null){
            vectors = new ArrayList<String[]>();
            getMapOfForeignKeyConstraints().put(constraintName, vectors);
        }
        vectors.add(vectStrs);
    }
    
    public List<String[]> getForeignKeyXPathStrs(String constraintName){
        return getMapOfForeignKeyConstraints().get(constraintName);
    }    
    
    public String toString() {
        String result = "********************** XSDSchema *****************************\n";
        result += root + "\n";
        result += printMaps() + "\n";
        return result;
    }
    
    public String printMaps(){
        String result = "-------------- CONSTRAINTS --------------\n";
        result += printForeignKeyConstraints() + "\n";
        result += printUniqueConstraints() + "\n";
        result += printKeyConstraints() + "\n";
        return result;
    }
    
    private String printForeignKeyConstraints(){
        String result = "[FOREIGN KEY CONSTRAINTS]";
        Set<String> keys = getMapOfForeignKeyConstraints().keySet();
        List<String[]> vectors;
        for(String key : keys){
            result += key;
            vectors = getMapOfForeignKeyConstraints().get(key);
            result += printVectors(vectors);
        }
        return result;
    }
    
    private String printUniqueConstraints(){
        String result = "[UNIQUE CONSTRAINTS]";
        Set<String> keys = getMapOfUniqueConstraints().keySet();
        List<String[]> vectors;
        for(String key : keys){
            result += key;
            vectors = getMapOfUniqueConstraints().get(key);
            result += printVectors(vectors);
        }
        return result;
    }
    
    private String printKeyConstraints(){
        String result = "[KEY CONSTRAINTS]";
        Set<String> keys = getMapOfKeyConstraints().keySet();
        List<String[]> vectors;
        for(String key : keys){
            result += key;
            vectors = getMapOfKeyConstraints().get(key);
            result += printVectors(vectors);
        }
        return result;
    }

    private String printVectors(List<String[]> vectors){
        String result = "";
        for(String[] singleVector : vectors){
            result += "\tELEMENT string[0] = " + singleVector[0] + "\n";
            result += "\tSELECTOR string[1] = " + singleVector[1] + "\n";
            result += "\tFIELD string[2] = " + singleVector[2] + "\n";
            result += "\t" + getXPathStringConstraintFiled(singleVector[0],singleVector[1],singleVector[2]) + "\n";
        }
        return result;
    }

    private String getXPathStringConstraintFiled(String elementStr, String selectorStr, String filedStr){
        String result = "/" + elementStr + selectorStr.substring(1) + filedStr.substring(1);
        return result;
    }

}
