package it.unibas.bartgui.view.panel.chart;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.category.CategoryDataset;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class StatisticChart {

    private StatisticChart(){}
    
    public static JPanel createBarChart(String title,String category,String value,CategoryDataset dataset)   {
        JFreeChart barChart = ChartFactory
                .createBarChart3D(title, 
                        category, value, dataset, 
                        PlotOrientation.VERTICAL, true, true, false);
        ChartPanel panel = new ChartPanel(barChart);
        panel.setPreferredSize(new Dimension(700, 440));
        return panel;
    }
    
    public static JPanel createStatisticBarChart(String title,String categoryX,String valueY,CategoryDataset dataset)   {
        final CategoryAxis xAxis = new CategoryAxis(categoryX);
        xAxis.setLowerMargin(0.01d); // percentage of space before first bar
        xAxis.setUpperMargin(0.01d); // percentage of space after last bar
        xAxis.setCategoryMargin(0.05d); // percentage of space between categories
        final ValueAxis yAxis = new NumberAxis("Value");
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, new StatisticalBarRenderer());
        JFreeChart statChart =  new JFreeChart(title, new Font("Helvetica", Font.BOLD, 14), plot, true);
        ChartPanel panel = new ChartPanel(statChart);
        panel.setPreferredSize(new Dimension(700, 440));
        return panel;
    }
}
