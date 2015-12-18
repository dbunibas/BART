package it.unibas.bartgui.controlegt.actions.node.ConfEGTNode;

import bart.model.EGTask;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DependenciesNodeNotify;
import it.unibas.bartgui.view.panel.editor.ConfEGTask.ConfEGTaskEditPanel;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import org.netbeans.api.actions.Editable;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

@ActionID(
        category = "ConfRGTaskConfNode",
        id = "it.unibas.bartgui.controlegt.actions.node.ConfEGTNode.Edit"
)
@ActionRegistration(
        displayName = "#CTL_Edit"
)
@Messages({"CTL_Edit=Edit",
            "TITLE_Dialog=EGTask Configuration Settings"})
public final class Edit implements ActionListener {

    private final Editable context;
    private EGTask egt;
    private EGTaskDataObjectDataObject dto;
    private ConfEGTaskEditPanel panel;

    public Edit(Editable context) {
        this.context = context;
        egt = Utilities.actionsGlobalContext().lookup(EGTask.class);
        dto = Utilities.actionsGlobalContext().lookup(EGTaskDataObjectDataObject.class);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        panel = new ConfEGTaskEditPanel();
        initButton(panel.getButtons());
        initPanel();
        Dialog d = createDialog(panel, panel.getButtons());
        d.setTitle(Bundle.TITLE_Dialog());
        d.setVisible(true);
    }
    
    private Dialog createDialog(Object innerPane, Object[] options)   {
       DialogDescriptor dsc =  new DialogDescriptor(innerPane, 
                                null, 
                                true, 
                                options, 
                                null,
                                DialogDescriptor.DEFAULT_ALIGN, 
                                HelpCtx.DEFAULT_HELP, 
                                null);
        Dialog d = DialogDisplayer.getDefault().createDialog(dsc);
        return d;
    }
    
    private void initButton(Object[] obj)   {
        for(Object o : obj)   {
            ((JButton)o).addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if(e.getActionCommand().equalsIgnoreCase("OK"))   {
                        egt.getConfiguration().setPrintLog(panel.getPanel().getPrintLogCheckBox().isSelected());
                        egt.getConfiguration().setDebug(panel.getPanel().getDebugCheckBox().isSelected());
                        egt.getConfiguration().setUseDeltaDBForChanges(panel.getPanel().getUseDeltaDBForChangesCheckBox().isSelected());
                        egt.getConfiguration().setRecreateDBOnStart(panel.getPanel().getRecreateDBOnStartCheckBox().isSelected());
                        egt.getConfiguration().setCheckCleanInstance(panel.getPanel().getCheckCleanInstanceCheckBox().isSelected());
                        egt.getConfiguration().setCheckChanges(panel.getPanel().getCheckChangesCheckBox().isSelected());
                        egt.getConfiguration().setExcludeCrossProducts(panel.getPanel().getExcludeCrossProductsCheckBox().isSelected());
                        egt.getConfiguration().setAvoidInteractions(panel.getPanel().getAvoidInteractionsCheckBox().isSelected());
                        
                        egt.getConfiguration().setApplyCellChanges(panel.getPanel().getApplyCellChangesCheckBox().isSelected());
                        egt.getConfiguration().setEstimateRepairability(panel.getPanel().getEstimateRepairabilityCheckBox().isSelected());
                        egt.getConfiguration().setUseSymmetricOptimization(panel.getPanel().getUseSymmetricOptimizationCheckBox().isSelected());
                        egt.getConfiguration().setGenerateAllChanges(panel.getPanel().getGenerateAllChangesCheckBox().isSelected());
                        egt.getConfiguration().setDetectEntireEquivalenceClasses(panel.getPanel().getDetectEntireEquivalenceClassesCheckBox().isSelected());
                        egt.getConfiguration().setRandomErrors(panel.getPanel().getRandomErrorsCheckBox().isSelected());
                        egt.getConfiguration().setOutlierErrors(panel.getPanel().getOutlierErrorsCheckBox().isSelected());
                        
                        egt.getConfiguration().setExportCellChanges(panel.getPanel().getExportCellChangesCheckBox().isSelected());
                        egt.getConfiguration().setExportCellChangesPath(panel.getPanel().getExportCellChangesPathTextField().getText());
                        egt.getConfiguration().setExportDirtyDB(panel.getPanel().getExportDirtyDBCheckBox().isSelected());
                        egt.getConfiguration().setExportDirtyDBType(panel.getPanel().getExportDirtyDbTypeTextField().getText());
                        egt.getConfiguration().setExportDirtyDBPath(panel.getPanel().getExportDirtyDBPathTextField().getText());
                        egt.getConfiguration().setCloneTargetSchema(panel.getPanel().getCloneTargetSchemaCheckBox().isSelected());
                        egt.getConfiguration().setCloneSuffix(panel.getPanel().getCloneSuffixTextField().getText());
                        
                        
                        try{
                            double d = Double.parseDouble(panel.getPanel().getSizeFactorReductionTextField().getText());
                            egt.getConfiguration().setSizeFactorReduction(d);
                            long l = Long.parseLong(panel.getPanel().getQueryWxecutionTimeOutTextField().getText());
                            egt.getConfiguration().setQueryExecutionTimeout(l);
                        }catch(Exception ex)   {
                            
                        }
                        dto.setEgtModified(true);
                        context.edit();
                        DependenciesNodeNotify.fire();
                    }
                }
            });
        }
    }
    
    private void initPanel()   {
        panel.getPanel().getPrintLogCheckBox().setSelected(egt.getConfiguration().isPrintLog());
        panel.getPanel().getDebugCheckBox().setSelected(egt.getConfiguration().isDebug());
        panel.getPanel().getUseDeltaDBForChangesCheckBox().setSelected(egt.getConfiguration().isUseDeltaDBForChanges());
        panel.getPanel().getRecreateDBOnStartCheckBox().setSelected(egt.getConfiguration().isRecreateDBOnStart());
        panel.getPanel().getCheckCleanInstanceCheckBox().setSelected(egt.getConfiguration().isCheckCleanInstance());
        panel.getPanel().getCheckChangesCheckBox().setSelected(egt.getConfiguration().isCheckChanges());
        panel.getPanel().getExcludeCrossProductsCheckBox().setSelected(egt.getConfiguration().isExcludeCrossProducts());
        panel.getPanel().getAvoidInteractionsCheckBox().setSelected(egt.getConfiguration().isAvoidInteractions());
        panel.getPanel().getApplyCellChangesCheckBox().setSelected(egt.getConfiguration().isApplyCellChanges());
        panel.getPanel().getEstimateRepairabilityCheckBox().setSelected(egt.getConfiguration().isEstimateRepairability());
        panel.getPanel().getUseSymmetricOptimizationCheckBox().setSelected(egt.getConfiguration().isUseSymmetricOptimization());
        panel.getPanel().getGenerateAllChangesCheckBox().setSelected(egt.getConfiguration().isGenerateAllChanges());
        panel.getPanel().getDetectEntireEquivalenceClassesCheckBox().setSelected(egt.getConfiguration().isDetectEntireEquivalenceClasses());
        panel.getPanel().getRandomErrorsCheckBox().setSelected(egt.getConfiguration().isRandomErrors());
        panel.getPanel().getOutlierErrorsCheckBox().setSelected(egt.getConfiguration().isOutlierErrors());
        
        if(egt.getConfiguration().isExportCellChanges())panel.getPanel().getExportCellChangesCheckBox().doClick();
        panel.getPanel().getExportCellChangesPathTextField().setText(egt.getConfiguration().getExportCellChangesPath());
        
        if(egt.getConfiguration().isExportDirtyDB())panel.getPanel().getExportDirtyDBCheckBox().doClick();
        panel.getPanel().getExportDirtyDBPathTextField().setText(egt.getConfiguration().getExportDirtyDBPath());           
        
        panel.getPanel().getExportDirtyDbTypeTextField().setText(egt.getConfiguration().getExportDirtyDBType());

        if(egt.getConfiguration().isCloneTargetSchema())panel.getPanel().getCloneTargetSchemaCheckBox().doClick();                      
        panel.getPanel().getCloneSuffixTextField().setText(egt.getConfiguration().getCloneSuffix());
        
        panel.getPanel().getSizeFactorReductionTextField().setText(egt.getConfiguration().getSizeFactorReduction()+"");
        panel.getPanel().getQueryWxecutionTimeOutTextField().setText(egt.getConfiguration().getQueryExecutionTimeout()+"");               
    }
    
    
}
