package it.unibas.bartgui.controlegt.actions.node.ConfVioGenQ;

import bart.model.EGTask;
import bart.model.VioGenQueryConfiguration;
import it.unibas.bartgui.egtaskdataobject.EGTaskDataObjectDataObject;
import it.unibas.bartgui.egtaskdataobject.notifier.DependenciesNodeNotify;
import it.unibas.bartgui.view.panel.editor.ConfVioGenQ.ConfVioGenQEditPanel;
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
        category = "ConfVioGenQueryCNode",
        id = "it.unibas.bartgui.controlegt.actions.node.ConfVioGenQ.Edit"
)
@ActionRegistration(
        displayName = "#CTL_Edit"
)
@Messages({"CTL_Edit=Edit",
           "TITLE_Dialog=VioGenQuery Configuration Settings"})
public final class Edit implements ActionListener {

    private final Editable context;
    private EGTask task;
    private EGTaskDataObjectDataObject dto;
    private ConfVioGenQEditPanel panel;

    public Edit(Editable context) {
        this.context = context;
        task = Utilities.actionsGlobalContext().lookup(EGTask.class);
        dto = Utilities.actionsGlobalContext().lookup(EGTaskDataObjectDataObject.class);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        panel = new ConfVioGenQEditPanel();
        initButton(panel.getButtons());
        InitPanel();
        Dialog d = createDialog(panel, panel.getButtons());
        d.setTitle(Bundle.TITLE_Dialog());
        d.setVisible(true);
    }
    
    private void initButton(Object[] obj)   {
        for(Object o : obj)   {
            ((JButton)o).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(e.getActionCommand().equalsIgnoreCase("OK"))   {
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setPercentage(Double.parseDouble(panel.getPanel()
                                                        .getPercentagjTextField1().getText()));
                        
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setSizeFactorForStandardQueries(Double.parseDouble(panel.getPanel()
                                                        .getSizeFactorForStandardQueriesTextField().getText()));
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setSizeFactorForSymmetricQueries(Double.parseDouble(panel.getPanel()
                                                        .getSizeFactorForSymmetricQueriesTextField().getText()));
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setSizeFactorForInequalityQueries(Double.parseDouble(panel.getPanel()
                                                        .getSizeFactorForInequalityQueriesTextField().getText()));
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setSizeFactorForSingleTupleQueries(Double.parseDouble(panel.getPanel()
                                                        .getSizeFactorForSingleTupleQueriesTextField().getText()));
                        
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setProbabilityFactorForStandardQueries(Double.parseDouble(panel.getPanel()
                                                        .getProbabilityFactorForStandardQueriesTextField().getText()));
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setProbabilityFactorForSymmetricQueries(Double.parseDouble(panel.getPanel()
                                                        .getProbabilityFactorForSymmetricQueriesTextField().getText()));
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setProbabilityFactorForInequalityQueries(Double.parseDouble(panel.getPanel()
                                                        .getProbabilityFactorForInequalityQueriesTextField().getText()));
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setProbabilityFactorForSingleTupleQueries(Double.parseDouble(panel.getPanel()
                                                        .getProbabilityFactorForSingleTupleQueriesTextField().getText()));
                        
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setWindowSizeFactorForStandardQueries(Double.parseDouble(panel.getPanel()
                                                        .getWindowSizeFactorForStandardQueriesTextField().getText()));
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setWindowSizeFactorForSymmetricQueries(Double.parseDouble(panel.getPanel()
                                                        .getWindowSizeFactorForSymmetricQueriesjTextField1().getText()));
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setWindowSizeFactorForInequalityQueries(Double.parseDouble(panel.getPanel()
                                                        .getWindowSizeFactorForInequalityQueriesjTextField1().getText()));
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setWindowSizeFactorForSingleTupleQueries(Double.parseDouble(panel.getPanel()
                                                        .getWindowSizeFactorForSingleTupleQueriesjTextField1().getText()));
                        
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setOffsetFactorForStandardQueries(Double.parseDouble(panel.getPanel()
                                                        .getOffsetFactorForStandardQueriesjTextField1().getText()));
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setOffsetFactorForSymmetricQueries(Double.parseDouble(panel.getPanel()
                                                        .getOffsetFactorForSymmetricQueriesjTextField1().getText()));
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setOffsetFactorForInequalityQueries(Double.parseDouble(panel.getPanel()
                                                        .getOffsetFactorForInequalityQueriesjTextField1().getText()));
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setOffsetFactorForSingleTupleQueries(Double.parseDouble(panel.getPanel()
                                                        .getOffsetFactorForSingleTupleQueriesjTextField1().getText()));
                        
                        
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setUseLimitInStandardQueries(panel.getPanel()
                                                        .getUseLimitInStandardQueriesjCheckBox1().isSelected());
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setUseLimitInSymmetricQueries(panel.getPanel()
                                                        .getUseLimitInSymmetricQueriesjCheckBox1().isSelected());
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setUseLimitInInequalityQueries(panel.getPanel()
                                                        .getUseLimitInInequalityQueriesjCheckBox1().isSelected());
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setUseLimitInSingleTupleQueries(panel.getPanel()
                                                        .getUseLimitInSingleTupleQueriesjCheckBox1().isSelected());
                        
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setUseOffsetInStandardQueries(panel.getPanel()
                                                        .getUseOffsetInStandardQueriesjCheckBox1().isSelected());
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setUseOffsetInSymmetricQueries(panel.getPanel()
                                                        .getUseOffsetInSymmetricQueriesjCheckBox1().isSelected());
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setUseOffsetInInequalityQueries(panel.getPanel()
                                                        .getUseOffsetInInequalityQueriesjCheckBox1().isSelected());
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setUseOffsetInSingleTupleQueries(panel.getPanel()
                                                        .getUseOffsetInSingleTupleQueriesjCheckBox1().isSelected());
                        if(panel.getPanel().getQueryExecutorjTextField1().getText().isEmpty())   {
                            task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setQueryExecutor(null);
                        }else{
                        task.getConfiguration()
                                .getDefaultVioGenQueryConfiguration()
                                .setQueryExecutor(panel.getPanel()
                                                        .getQueryExecutorjTextField1().getText());                        
                        }
                        dto.setEgtModified(true);
                        context.edit();
                        DependenciesNodeNotify.fire();
                    }
                } 
            });
        }
    }
          
    private void InitPanel()   {
        VioGenQueryConfiguration c = task.getConfiguration().getDefaultVioGenQueryConfiguration();
        
        panel.getPanel().getPercentagjTextField1().setText(c.getPercentage()+"");
        
        panel.getPanel().getSizeFactorForStandardQueriesTextField().setText(c.getSizeFactorForStandardQueries()+"");
        panel.getPanel().getSizeFactorForSymmetricQueriesTextField().setText(c.getSizeFactorForSymmetricQueries()+"");
        panel.getPanel().getSizeFactorForInequalityQueriesTextField().setText(c.getSizeFactorForInequalityQueries()+"");
        panel.getPanel().getSizeFactorForSingleTupleQueriesTextField().setText(c.getSizeFactorForSingleTupleQueries()+"");
        
        panel.getPanel().getProbabilityFactorForStandardQueriesTextField().setText(c.getProbabilityFactorForStandardQueries()+"");
        panel.getPanel().getProbabilityFactorForSymmetricQueriesTextField().setText(c.getProbabilityFactorForSymmetricQueries()+"");
        panel.getPanel().getProbabilityFactorForInequalityQueriesTextField().setText(c.getProbabilityFactorForInequalityQueries()+"");
        panel.getPanel().getProbabilityFactorForSingleTupleQueriesTextField().setText(c.getProbabilityFactorForSingleTupleQueries()+"");
        
        panel.getPanel().getWindowSizeFactorForStandardQueriesTextField().setText(c.getWindowSizeFactorForStandardQueries()+"");
        panel.getPanel().getWindowSizeFactorForSymmetricQueriesjTextField1().setText(c.getWindowSizeFactorForSymmetricQueries()+"");
        panel.getPanel().getWindowSizeFactorForInequalityQueriesjTextField1().setText(c.getWindowSizeFactorForInequalityQueries()+"");
        panel.getPanel().getWindowSizeFactorForSingleTupleQueriesjTextField1().setText(c.getWindowSizeFactorForSingleTupleQueries()+"");
        
        panel.getPanel().getOffsetFactorForStandardQueriesjTextField1().setText(c.getOffsetFactorForStandardQueries()+"");
        panel.getPanel().getOffsetFactorForSymmetricQueriesjTextField1().setText(c.getOffsetFactorForSymmetricQueries()+"");
        panel.getPanel().getOffsetFactorForInequalityQueriesjTextField1().setText(c.getOffsetFactorForInequalityQueries()+"");
        panel.getPanel().getOffsetFactorForSingleTupleQueriesjTextField1().setText(c.getOffsetFactorForSingleTupleQueries()+"");
        
        panel.getPanel().getUseLimitInStandardQueriesjCheckBox1().setSelected(c.isUseLimitInStandardQueries());
        panel.getPanel().getUseLimitInSymmetricQueriesjCheckBox1().setSelected(c.isUseLimitInSymmetricQueries());
        panel.getPanel().getUseLimitInInequalityQueriesjCheckBox1().setSelected(c.isUseLimitInInequalityQueries());
        panel.getPanel().getUseLimitInSingleTupleQueriesjCheckBox1().setSelected(c.isUseLimitInSingleTupleQueries());
        
        panel.getPanel().getUseOffsetInStandardQueriesjCheckBox1().setSelected(c.isUseOffsetInStandardQueries());
        panel.getPanel().getUseOffsetInSymmetricQueriesjCheckBox1().setSelected(c.isUseOffsetInSymmetricQueries());
        panel.getPanel().getUseOffsetInInequalityQueriesjCheckBox1().setSelected(c.isUseOffsetInInequalityQueries());
        panel.getPanel().getUseOffsetInSingleTupleQueriesjCheckBox1().setSelected(c.isUseOffsetInSingleTupleQueries());
        
        panel.getPanel().getQueryExecutorjTextField1().setText(c.getQueryExecutor());      
        
    }
    
    private Dialog createDialog(Object innerPane, Object[] options)   {
         DialogDescriptor dsc = new DialogDescriptor(innerPane, 
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
}