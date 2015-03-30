package bart.persistence.xml.operators;

import bart.model.database.mainmemory.datasource.*;
import bart.model.database.mainmemory.datasource.nodes.*;
import bart.model.database.mainmemory.datasource.operators.INodeVisitor;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FindNodeFromXPath {
    
    INode findNode(INode root, List<String> paths) {
        FindNodeFromXPathVisitor visitor = new FindNodeFromXPathVisitor(root, paths);
        root.accept(visitor);
        return visitor.getResult();
    }
    
}

class FindNodeFromXPathVisitor implements INodeVisitor {

    private static Logger logger = LoggerFactory.getLogger(FindNodeFromXPathVisitor.class);

    private INode root;
    private List<String> paths;
    private int currentPosition = 0;
    private INode targetNode;
    
    FindNodeFromXPathVisitor(INode root, List<String> paths){
        this.root = root;
        this.paths = paths;
    }
    
    private boolean check(INode node){
        if(node instanceof AttributeNode && !(currentPosition == paths.size() - 1)) {
            return false;
        }
        return true;
    }
    
    private void visitGenericNode(INode node) {
        if(paths.size() >= currentPosition && targetNode == null){
            if (logger.isDebugEnabled()) logger.debug(" --- paths.get(" + currentPosition + ") =" + paths.get(currentPosition));
            if (logger.isDebugEnabled()) logger.debug(" --- node label: " + node.getLabel());
            
            if(node.getLabel().equalsIgnoreCase(paths.get(currentPosition)) && !node.isVirtual() && check(node)){
                if (logger.isDebugEnabled()) logger.debug(" --- correspondence founded " + node.getLabel());
                currentPosition++;
            }

            if(paths.size() == currentPosition){
                if (logger.isDebugEnabled()) logger.debug(" --- node founded in: " + node.getFather().getLabel() + "." + node.getLabel());
                this.targetNode = node;
            } else {
                if (logger.isDebugEnabled()) logger.debug(" --- visiting children of " + node.getLabel());
                if (logger.isDebugEnabled()) logger.debug(" --- favorite child: " + paths.get(currentPosition));

                INode favoriteChild = node.getChildStartingWith(paths.get(currentPosition));
                if(favoriteChild == null){
                    for(INode child : node.getChildren()){
                        if (logger.isDebugEnabled()) logger.debug(" --- child " + child.getLabel());
                        child.accept(this);
                    }
                } else {
                    if (logger.isDebugEnabled()) logger.debug(" --- found favorite child: " + paths.get(currentPosition));
                    favoriteChild.accept(this);
                }
            }
        } 
    }

    public void visitSetNode(SetNode node) {
        visitGenericNode(node);
    }
    
    public void visitTupleNode(TupleNode node) {
        visitGenericNode(node);
    }
    
    public void visitSequenceNode(SequenceNode node) {
        visitGenericNode(node);
    }
    
    public void visitAttributeNode(AttributeNode node) {
        visitGenericNode(node);
    }
    
    public void visitMetadataNode(MetadataNode node) {
        visitGenericNode(node);
    }
    
    public void visitLeafNode(LeafNode node) {
        return;
    }
    
    public INode getResult(){
        if(targetNode == null){
            throw new NullPointerException("Error: unable to find target node: " + paths + " in " + root);
        }
        if(logger.isDebugEnabled()) logger.debug(" Node: " + paths + " founded with " + this.targetNode.getFather().getLabel());
        return this.targetNode;
    }
}
