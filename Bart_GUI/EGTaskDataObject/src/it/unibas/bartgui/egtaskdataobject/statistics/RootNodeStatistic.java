package it.unibas.bartgui.egtaskdataobject.statistics;

import it.unibas.bartgui.resources.R;
import it.unibas.centrallookup.CentralLookup;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
@NbBundle.Messages({
    "MSG_RootNodeStatistic_Name=Statistics of EGTask Execution",
})
public class RootNodeStatistic extends AbstractNode  {

    private static Logger log = Logger.getLogger(RootNodeStatistic.class.getName());
    
    public RootNodeStatistic(Children children) {
        super(children);
        setIconBaseWithExtension(R.IMAGE_STATISTIC);
        try{
            DataObject egtDO = CentralLookup.getDefLookup().lookup(DataObject.class);
            FileObject statFolder = FileUtil.getConfigFile("statistics");
            StringBuilder path = new StringBuilder("statistics/");
            path.append(egtDO.getPrimaryFile().getName());
            FileObject folderName = FileUtil.getConfigFile(path.toString());
            if(folderName == null)  {
              folderName = statFolder.createFolder(egtDO.getPrimaryFile().getName());;
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE,"Exception create folder statistic",ex);
        }
    }

    @Override
    public boolean canCut() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public boolean canRename() {
        return false;
    }

    @Override
    public String getHtmlDisplayName() {
        StringBuilder sb = new StringBuilder(R.HTML_R_Node);
        sb.append(Bundle.MSG_RootNodeStatistic_Name());
        sb.append(R.HTML_CL_R_Node);
        return sb.toString();
    }
    
    

}
