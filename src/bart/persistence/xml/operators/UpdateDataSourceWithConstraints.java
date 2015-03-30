package bart.persistence.xml.operators;

import bart.model.database.mainmemory.datasource.*;
import bart.model.database.mainmemory.paths.*;
import bart.model.database.mainmemory.paths.operators.GeneratePathExpression;
import bart.persistence.xml.model.XSDSchema;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateDataSourceWithConstraints {

    private static Logger logger = LoggerFactory.getLogger(UpdateDataSourceWithConstraints.class);

    private GeneratePathExpression pathGenerator = new GeneratePathExpression();

    private Map<String, KeyConstraint> mapOfUnique = new HashMap<String, KeyConstraint>();
    private Map<String, KeyConstraint> mapOfKeys = new HashMap<String, KeyConstraint>();
    private Map<String, ForeignKeyConstraint> mapOfForeignKeys = new HashMap<String, ForeignKeyConstraint>();
    
    public void updateDataSource(DataSource dataSource, XSDSchema xsdSchema){
        // updateUniqueOrKeyConstraints must be invoked before updateWithForeignKeyConstraints
        this.processKeyConstraints(dataSource.getSchema(), xsdSchema.getMapOfKeyConstraints());
        this.processUniqueConstraints(dataSource.getSchema(), xsdSchema.getMapOfUniqueConstraints());
        this.processForeignKeyConstraints(dataSource.getSchema(), xsdSchema.getMapOfForeignKeyConstraints());
        for(KeyConstraint keyConstraint : mapOfUnique.values()){
            dataSource.addKeyConstraint(keyConstraint);
        }
        for(KeyConstraint keyConstraint : mapOfKeys.values()){
            dataSource.addKeyConstraint(keyConstraint);
        }
        for(ForeignKeyConstraint foreignKeyConstraint : mapOfForeignKeys.values()){
            dataSource.addForeignKeyConstraint(foreignKeyConstraint);
        }
    }
    
    private void processKeyConstraints(INode schema, Map<String,List<String[]>> mapOfKeyConstraints){
        Set<String> keys = mapOfKeyConstraints.keySet();
        INode keyNode;
        for(String key : keys){
            if (logger.isDebugEnabled()) logger.debug(" -- key name: " + key);
            List<String[]> vectors = mapOfKeyConstraints.get(key);
            List<PathExpression> keyPathExpressions = new ArrayList<PathExpression>();
            FindNodeFromXPath nodeFinder = new FindNodeFromXPath();
            for(String[] vectorPaths : vectors) {
                keyNode = nodeFinder.findNode(schema, normalizeToList(vectorPaths));
                if (logger.isDebugEnabled()) logger.debug(" --- uniqueOrKeyNode: " + keyNode.getLabel() + " father: " + keyNode.getFather());
                PathExpression keyPathExpression = generatePathExpression(keyNode);
                keyPathExpressions.add(keyPathExpression);
            }
            addKeyConstraint(key, keyPathExpressions);
        }
    }
    
    private void processUniqueConstraints(INode schema, Map<String,List<String[]>> mapOfUniqueConstraints){
        Set<String> keys = mapOfUniqueConstraints.keySet();
        INode uniqueNode;
        for(String key : keys){
            if (logger.isDebugEnabled()) logger.debug(" -- key name: " + key);
            List<String[]> vectors = mapOfUniqueConstraints.get(key);
            List<PathExpression> keyPathExpressions = new ArrayList<PathExpression>();
            FindNodeFromXPath nodeFinder = new FindNodeFromXPath();
            for(String[] vectorPaths : vectors) {
                uniqueNode = nodeFinder.findNode(schema, normalizeToList(vectorPaths));
                if (logger.isDebugEnabled()) logger.debug(" --- uniqueOrKeyNode: " + uniqueNode.getLabel() + " father: " + uniqueNode.getFather());
                PathExpression keyPathExpression = generatePathExpression(uniqueNode);
                keyPathExpressions.add(keyPathExpression);
            }
            addUniqueConstraint(key, keyPathExpressions);
        }
    }
    
    private void processForeignKeyConstraints(INode schema, Map<String,List<String[]>> mapOfForeignKeyConstraints){
        Set<String> keys = mapOfForeignKeyConstraints.keySet();
        INode foreignKeyNode;
        // constraint's keyName format: FK|PK
        for(String key : keys){
            if (logger.isDebugEnabled()) logger.debug(" -- key: " + key);
            if(key.indexOf("|") == -1) {
                throw new IllegalArgumentException(" foreign key name must be in format FK|PK, instead is " + key);
            }
            List<String[]> vectors = mapOfForeignKeyConstraints.get(key);
            String primaryKeyString = key.substring(key.indexOf("|") + 1);
            if (logger.isDebugEnabled()) logger.debug(" --- FOREIGN KEY NAME: " + key + " with PRIMARY KEY NAME: " + primaryKeyString);
            List<PathExpression> foreignKeyPathExpressions = new ArrayList<PathExpression>();
            FindNodeFromXPath nodeFinder = new FindNodeFromXPath();
            for(String[] vectorPaths : vectors) {
                foreignKeyNode = nodeFinder.findNode(schema, normalizeToList(vectorPaths));
                if (logger.isDebugEnabled()) logger.debug(" --- foreignkey: " + foreignKeyNode.getLabel() + " father: " + foreignKeyNode.getFather());
                PathExpression foreignKeyPathExpression = generatePathExpression(foreignKeyNode);
                foreignKeyPathExpressions.add(foreignKeyPathExpression);
            }
            addForeignKeyConstraint(key,foreignKeyPathExpressions,primaryKeyString);
        }
    }
    
    private PathExpression generatePathExpression(INode node) {
        return pathGenerator.generatePathFromRoot(node);
    }
    
    private List<String> normalizeToList(String[] vector){
        String currentPath;
        ArrayList<String> listPaths = new ArrayList<String>();
        for(int i = 0; i < vector.length; i++) {
            currentPath = vector[i];
            listPaths.addAll(normalizePath(currentPath));
        }
        return listPaths;
    }
    
    private List<String> normalizePath(String path){
        String currentPath = path;
        ArrayList<String> list = new ArrayList<String>();
        if(currentPath.startsWith(".")){
            currentPath =  path.substring(1);
        }
        currentPath = currentPath.replaceAll("\\@","");
        currentPath = currentPath.replaceAll("\\*","");
        
        StringTokenizer tokenizer = new StringTokenizer(currentPath, "/");
        if(tokenizer.countTokens() > 0){
            if (logger.isDebugEnabled()) logger.debug(" --- Tokenizer for " + path + " with " + tokenizer.countTokens() + " elements");
            while(tokenizer.hasMoreElements()){
                String token = tokenizer.nextToken();
                if(!(token.trim().equals(""))){
                    if (logger.isDebugEnabled()) logger.debug("\t +" + token + " added");
                    list.add(token);
                }
            }
        } else {
            if(!(currentPath.trim().equals(""))){
                if (logger.isDebugEnabled()) logger.debug("\t +" + currentPath + " added");
                list.add(currentPath);
            }
            
        }
        
        if (logger.isDebugEnabled()) logger.debug(" --- Size of list for " + path + " is = " + list.size());
        return list;
    }
    
    private void addUniqueConstraint(String key, List<PathExpression> pathExpressions) {
        KeyConstraint keyConstraint = new KeyConstraint(pathExpressions);
        this.mapOfUnique.put(key, keyConstraint);
    }
    
    private void addKeyConstraint(String key, List<PathExpression> pathExpressions) {
        KeyConstraint keyConstraint = new KeyConstraint(pathExpressions, true);
        this.mapOfKeys.put(key, keyConstraint);
    }
    
    private KeyConstraint getUniqueConstraint(String key){
        return this.mapOfUnique.get(key);
    }
    
    private KeyConstraint getKeyConstraint(String key){
        return this.mapOfKeys.get(key);
    }
    
    private void addForeignKeyConstraint(String foreignKeyName, List<PathExpression> pathExpressions, String primaryKeyName) {
        KeyConstraint keyConstraint = this.getKeyConstraint(primaryKeyName);
        ForeignKeyConstraint foreignKeyConstraint = new ForeignKeyConstraint(keyConstraint, pathExpressions);
        this.mapOfForeignKeys.put(foreignKeyName, foreignKeyConstraint);
    }
}
