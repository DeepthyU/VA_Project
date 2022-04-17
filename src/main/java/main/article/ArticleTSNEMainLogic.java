package main.article;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import preprocessing.Article;
import preprocessing.Preprocessor;
import preprocessing.Utils;
import scatterplot.ArticleData;
import scatterplot.ScatterPlotFactory;
import style.GlasbeyColors;
import vis.article.ArticleFilter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleTSNEMainLogic {
    private ChartPanel articleChart;
    private XYSeriesCollection dataset;
    private Map<XYDataItem, ArticleData> parsedCsv;
    private static final String DATA_PATH = "./src/main/data/gastech_data/data/article_tsne.csv";
    private Map<String, Article> articleMap;
    public ArticleTSNEMainLogic(Preprocessor preprocessor) {
        makeArticleMap(preprocessor);
        ScatterPlotFactory scFactory = new ScatterPlotFactory();
        parsedCsv = scFactory.parseArticleCsv(DATA_PATH);

        dataset = scFactory.createArticleTsneDataset(parsedCsv);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "TSNE Clustering of News Articles after TF-IDF Vectorization",
                "", "", dataset,
                PlotOrientation.HORIZONTAL, true, true, true
        );
        chart.setAntiAlias(true);
        XYPlot plot = (XYPlot) chart.getPlot();
        // Set background to lighter color
        plot.setBackgroundPaint(new Color(229, 235, 247));
        // Hide x and y axis
        plot.getDomainAxis().setVisible(false);
        plot.getRangeAxis().setVisible(false);

        addToolTip(parsedCsv, plot);
        articleChart = new ChartPanel(chart);
        articleChart.setInitialDelay(0);

    }

    private void makeArticleMap(Preprocessor preprocessor) {
        articleMap = new HashMap<String, Article>();
        for (Article article: preprocessor.getArticleList())
        {
            articleMap.put(article.getFileName(), article);
        }
    }


    public ChartPanel simulate_graph() {
        return articleChart;
    }

    public void applyFilters(List filters) {
        updateDataset(filters);
        articleChart.getChart().fireChartChanged();
        articleChart.repaint();
    }

    /**
     * Update dataset, grouped by publication, for visualization
     */
    public void updateDataset(java.util.List<ArticleFilter> filters) {
        dataset.removeAllSeries();
        ArrayList<Integer> seenPubIds = new ArrayList<>();
        Map<Integer, XYSeries> xySeries = new HashMap<>();
        for (Map.Entry<XYDataItem, ArticleData> entry : parsedCsv.entrySet()) {
            Article article = articleMap.get(entry.getValue().getFilename());
            if (!Utils.isRemoveItem(filters, article)) {
                int pubId = entry.getValue().getPublicationId();
                String pub = entry.getValue().getPublication();
                if (!seenPubIds.contains(pubId)) {
                    seenPubIds.add(pubId);
                    xySeries.put(pubId, new XYSeries(pub));
                }
                xySeries.get(pubId).add(entry.getKey());
            }
        }
        for (XYSeries xy : xySeries.values()) {
            dataset.addSeries(xy);
        }
    }


    private void addToolTip(Map<XYDataItem, ArticleData> parsedCsv, XYPlot plot) {
        // The whole thing below this is to make a tooltip
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        XYToolTipGenerator xyToolTipGenerator = new XYToolTipGenerator() {
            @Override
            public String generateToolTip(XYDataset dataset, int series, int item) {
                XYDataItem dataItem = new XYDataItem(dataset.getX(series, item), dataset.getY(series, item));
                ArticleData articleData = parsedCsv.get(dataItem);
                String title = articleData.getTitle();
                String publication = articleData.getPublication();
                String date = articleData.getDate();
                String filename = articleData.getFilename();
                return String.format("<html><p><b>%s</b></p>", title) +
                        String.format("<p>%s</p>", publication) +
                        String.format("<p>%s</p>", date) +
                        String.format("<p>filename: %s</p>", filename) +
                        "</html>";
            }
        };
        renderer.setDefaultToolTipGenerator(xyToolTipGenerator);
        for (int i = 0; i < plot.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, GlasbeyColors.colors[i]);
        }
    }


}