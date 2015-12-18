package it.unibas.bartgui.view.panel.editor.database.visual;

import it.unibas.bartgui.resources.R;
import java.awt.Color;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.graph.layout.GridGraphLayout;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;
import org.netbeans.api.visual.vmd.VMDGraphScene;
import org.netbeans.api.visual.vmd.VMDNodeWidget;
import org.netbeans.api.visual.vmd.VMDPinWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.openide.util.ImageUtilities;
import speedy.model.database.Attribute;
import speedy.model.database.AttributeRef;
import speedy.model.database.ForeignKey;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.Key;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DB_VMDGraph {

    private static Logger log = Logger.getLogger(DB_VMDGraph.class.getName());
    
    private VMDGraphScene scene;
    private GridGraphLayout<String, String> graphLayout;
    private SceneLayout sceneGraphLayout;
    private IDatabase db;
    private int EDGE_ID=0;
    private final String topCompName;
    private final Border highlighterErrorBorderAttr;
    private final Border emptyBorder;
    private List<String> pinsError = null;

    public DB_VMDGraph(IDatabase db,String topCompName) {
        scene = new VMDGraphScene();
        this.db=db;
        this.topCompName = topCompName;
        highlighterErrorBorderAttr = BorderFactory.createLineBorder(3, Color.MAGENTA);
        emptyBorder = BorderFactory.createEmptyBorder();
        graphLayout = new GridGraphLayout<String, String> ();
        sceneGraphLayout = LayoutFactory.createSceneGraphLayout(scene, graphLayout);
        createTables();
        createEdges();
    }
    
    public void sethighlighterError(List<String> pins)   {
        this.pinsError = pins;
    }
    
    public void highlighterError(List<String> pins)   {
        if(pins != null)   {
            for(String p : scene.getPins())   {
                if(pins.contains(p))   {
                    scene.findWidget(p).setBorder(highlighterErrorBorderAttr);
                }else{
                    scene.findWidget(p).setBorder(emptyBorder);
                }
            }
        }else{
            for(String p : scene.getPins())   {
                scene.findWidget(p).setBorder(emptyBorder);
            }           
        }
        scene.validate();       
    }
    
    private void createTables()   {
       for(String nameT : db.getTableNames())   {
            createTable(db.getTable(nameT));
        }
    }
    
    private void createTable(ITable table)   {
        VMDNodeWidget tableWidget = (VMDNodeWidget)scene.addNode(table.getName());
        tableWidget.setNodeProperties(ImageUtilities.loadImage(R.IMAGE_NODE_DBMS), 
                                      table.getName(), 
                                      "TABLE", null);
        tableWidget.setToolTipText("Double click for View Table data");
        tableWidget.getActions().addAction(ActionFactory.createEditAction(
                                        new TableEditProvider(topCompName, table)));
        for(Attribute attribute : table.getAttributes())   {
            if(attribute.getName().equalsIgnoreCase("oid"))continue;
            createAttribute(table.getName(),attribute);
        }
    }

    private void createAttribute(String table , Attribute attribute)   {
        VMDPinWidget attributeWidget = (VMDPinWidget)scene.addPin(table,table+attribute.getName());
        attributeWidget.setPinName(attribute.getName());
        attributeWidget.setToolTipText("Double click for View Table data");
        attributeWidget.setLayout(LayoutFactory.createHorizontalFlowLayout(
                                    LayoutFactory.SerialAlignment.JUSTIFY, 7));
        
        LabelWidget type = new LabelWidget(scene);
        type.setForeground(Color.GRAY);
        type.setLabel(attribute.getType());
        attributeWidget.addChild(type);
        
        for(Key k : db.getKeys(table))   {
            for(AttributeRef attr : k.getAttributes())   {
                if(attr.getName().equals(attribute.getName()) && k.isPrimaryKey())   {
                    LabelWidget pk = new LabelWidget(scene);
                    pk.setForeground(Color.BLUE);
                    pk.setLabel(" PK");
                    attributeWidget.addChild(pk);    
                }
            }
        }
    }
    
    private void createEdges()   {
       for(String nameT : db.getTableNames())   {
            for(Attribute att : db.getTable(nameT).getAttributes())   {
                if(att.getName().equalsIgnoreCase("oid"))continue;
                createEdge(nameT, att.getName());
            }
        } 
    }
    
    private void createEdge(String table,String attribute)   {
        try{
            for(ForeignKey fk : db.getForeignKeys(table))   {
                for(int i=0;i<fk.getRefAttributes().size();i++)   {
                    if(attribute.equals(fk.getRefAttributes().get(i).getName()))   {
                        String edgeID = table+(EDGE_ID++);
                        scene.addEdge(edgeID);
                        scene.setEdgeSource(edgeID, table+attribute);
                        String pinTarget = fk.getKeyAttributes().get(i).getTableName()+
                                           fk.getKeyAttributes().get(i).getName();
                        scene.setEdgeTarget(edgeID, pinTarget);
                    }
                }
            }
        }catch(Exception ex)   {
            
        }
    }
    
    
    /**
     * @return the scene
     */
    public VMDGraphScene getScene() {
        return scene;
    }
    
    public JComponent getView()   {
        return scene.createView();
    }
    
    public void invokeLayout()   {
        sceneGraphLayout.invokeLayout();
    }
    
}
