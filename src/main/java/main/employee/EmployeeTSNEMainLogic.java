package main.employee;

import org.jfree.chart.ChartPanel;
import scatterplot.ScatterPlotFactory;

public class EmployeeTSNEMainLogic {
    ChartPanel emailPlot;
    private static final String DATA_PATH= "./src/main/data/gastech_data/data/email_tsne.csv";
    public EmployeeTSNEMainLogic()
    {
        emailPlot = new ScatterPlotFactory().getEmailClusteringPlot(DATA_PATH);
    }

    public ChartPanel simulate_graph() {
        return emailPlot;
    }

}