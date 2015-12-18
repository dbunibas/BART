/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.bartgui.controlegt.actions.node.Statistics;

import it.unibas.bartgui.egtaskdataobject.statistics.Statistic;
import it.unibas.bartgui.view.ViewResource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(
        category = "StatisticNode",
        id = "it.unibas.bartgui.controlegt.actions.node.Statistics.Open"
)
@ActionRegistration(
        displayName = "#CTL_Open"
)
@Messages("CTL_Open=Open")
public final class Open implements ActionListener {

    private final Statistic context;

    public Open(Statistic context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        TopComponent tc = WindowManager.getDefault().findTopComponent(ViewResource.TOP_ID_ChartTopComponent);
        if(tc != null)   {
            if(tc.isOpened())  {
                tc.requestActive();
                return;
            }else{
                tc.open();
                tc.requestActive();
            }
        }
    }
}
