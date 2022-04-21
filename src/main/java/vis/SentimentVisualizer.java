package vis;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import preprocessing.Article;
import preprocessing.Preprocessor;
import vis.article.ArticleFilter;

import java.awt.*;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SentimentVisualizer {

    private XYSeriesCollection dataset;
    private Map<XYDataItem, Article> tooltipLookup = new HashMap<>();
    protected Preprocessor preprocessor;
    private JFreeChart chart;
    private ChartPanel panel;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy");

    public SentimentVisualizer(Preprocessor preprocessor) {
        this.preprocessor = preprocessor;
        dataset = new XYSeriesCollection();

        for (XYSeries series : createDataSeries(null)) {
            dataset.addSeries(series);
        }

        chart = ChartFactory.createScatterPlot(
                "Article Sentiment",
                "Articles",
                "Sentiment",
                dataset
        );
        setToolTip();
        setSeriesPaint();

        panel = new ChartPanel(chart);
        panel.setInitialDelay(0);
    }

    /**
     * Creates initial "full" dataset without any filtering. Also, I don't really like methods with side effects
     * so yeah I wrote it with a return.
     * @return The negative, neutral, and positive XYSeries in that order
     */
    private List<XYSeries> createDataSeries(List<ArticleFilter> filters) {
        XYSeries seriesNeutral = new XYSeries("neutral");
        XYSeries seriesNegative = new XYSeries("negative");
        XYSeries seriesPositive = new XYSeries("positive");
        tooltipLookup = new HashMap<>();

        for (Article article : preprocessor.getArticleList()) {
            if (!decideIfArticleShouldBeAdded(article, filters)) continue;
            double x = article.getxCoordinate();
            int y = article.getSentimentScore();
            // Add article to the appropriate series
            if (y == 0){
                seriesNeutral.add(x, y);
            } else if (y < 0) {
                seriesNegative.add(x, y);
            } else {
                seriesPositive.add(x, y);
            }
            // Now make the lookup
            Map<String, Object> info = Map.of(
                    "fileName", article
            );
            tooltipLookup.put(new XYDataItem(x, y), article);
        }
        return List.of(seriesNegative, seriesNeutral, seriesPositive);
    }

    /**
     * Decides if an article should be added to the dataset based on the given filters
     * @param article The article that is the object of the judgement
     * @param filters The filters to be applied
     * @return
     */
    private boolean decideIfArticleShouldBeAdded(Article article, List<ArticleFilter> filters) {
        if (filters == null) return true;

        for (ArticleFilter filter : filters) {
            // Go through every filter and cheeck if the article fulfills the filter constraints
            switch (filter.getField()) {
                case DATE:
                    Timestamp startDate = new Timestamp(filter.getStartDate());
                    Timestamp endDate = new Timestamp(filter.getEndDate());
                    Timestamp d = article.getDate();
                    if (!(!d.before(startDate) && !d.after(endDate))) return false;
                    break;
                case PLACE:
                    if (!filter.getSelectedValues().contains(article.getPlace())) return false;
                    break;
                case AUTHOR:
                    if (!filter.getSelectedValues().contains(article.getAuthor())) return false;
                    break;
                case KEYWORD:
                    Set<String> result = article.getKeywordsList().stream()
                            .distinct()
                            .filter(filter.getSelectedValues()::contains)
                            .collect(Collectors.toSet());
                    if (result.isEmpty()) return false;
                    break;
                case PUBLICATION:
                    if (!filter.getSelectedValues().contains(article.getPublication())) return false;
                    break;
            }
        }
        return true;
    }

    /**
     * Method to apply filters to the currently loaded dataset.
     * @param filters New set of filters to filter the full dataset on
     */
    public void applyFilters(List<ArticleFilter> filters) {
        System.out.println("DEBUG: Dataset update called");
        dataset.removeAllSeries();
        for (XYSeries series : createDataSeries(filters)) {
            dataset.addSeries(series);
        }
        setToolTip();
        setSeriesPaint();
    }
    private XYToolTipGenerator createToolTipGenerator() {
        return new XYToolTipGenerator() {
            @Override
            public String generateToolTip(XYDataset dataset, int series, int item) {
                XYDataItem dataItem = new XYDataItem(dataset.getX(series, item), dataset.getY(series, item));
                Article article = tooltipLookup.get(dataItem);
                String title = article.getTitle();
                String publication = article.getPublication();
                String date = article.getDate().toLocalDateTime().format(FORMATTER);
                String filename = article.getFileName();
                return String.format("<html><p><b>%s</b></p>", title) +
                        String.format("<p>%s</p>", publication) +
                        String.format("<p>%s</p>", date) +
                        String.format("<p>filename: %s</p>", filename) +
                        "</html>";
            }
        };
    }
    private void setToolTip() {
        XYPlot plot = (XYPlot) chart.getPlot();
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setDefaultToolTipGenerator(createToolTipGenerator());
    }
    private void setSeriesPaint() {
        XYPlot plot = (XYPlot) chart.getPlot();
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(255, 193, 7));
        renderer.setSeriesPaint(1, new Color(50, 50, 50));
        renderer.setSeriesPaint(2, new Color(30, 136, 229));
    }

    public ChartPanel getPanel() {
        return panel;
    }
}
