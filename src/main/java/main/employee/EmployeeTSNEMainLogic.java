package main.employee;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import scatterplot.DataValue;
import scatterplot.ScatterPlotFactory;
import style.EmailColors;
import utils.VisualizerPrefs;

import java.util.Map;

public class EmployeeTSNEMainLogic {
    ChartPanel emailPlot;
    ScatterPlotFactory scFactory = new ScatterPlotFactory();
    private static final String DATA_PATH = VisualizerPrefs.getInstance().getFullDataDirPath().resolve("email_tsne").toString();
    public EmployeeTSNEMainLogic()
    {
        emailPlot = scFactory.getEmailClusteringPlot(DATA_PATH);
    }

    public ChartPanel simulate_graph() {
        return emailPlot;
    }

    public void setDateRange(int startIdx, int endIdx) {
        Object[] datasets = scFactory.createEmailDatasetFromCsv(DATA_PATH, startIdx, endIdx);
        XYDataset dataset = (XYSeriesCollection) datasets[0];
        Map<XYDataItem, DataValue> coordinateNameMap = (Map<XYDataItem, DataValue>) datasets[1];

        XYPlot plot = (XYPlot) emailPlot.getChart().getPlot();
        plot.setDataset(dataset);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        XYToolTipGenerator xyToolTipGenerator = scFactory.generateEmailTooltip(coordinateNameMap);
        renderer.setDefaultToolTipGenerator(xyToolTipGenerator);
        for (int i = 0; i < plot.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, EmailColors.deptColors[i]);
        }
        emailPlot.updateUI();
    }

}