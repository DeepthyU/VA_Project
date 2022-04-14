package main;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import preprocessing.Article;
import preprocessing.Preprocessor;
import preprocessing.Utils;
import vis.article.ArticleField;
import vis.article.ArticleFilter;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class LineGraphMainLogic {

    private ChartPanel panel;
    private static Preprocessor PREPROCESSOR;
    private XYSeriesCollection dataset;
    private JFreeChart chart;
    private static int[][] keywordCount;


    public LineGraphMainLogic(Preprocessor preprocessor) {
        PREPROCESSOR = preprocessor;
        // Create dataset
        dataset = createDataset(null);
        // Create chart
        DateAxis dateAxis = new DateAxis("Date");
        dateAxis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));
        dateAxis.setVerticalTickLabels(true);
        chart = ChartFactory.createXYLineChart("Keyword Frequency", "Date", "Frequency"
                , dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.getTitle().setFont(new Font("Tahoma", Font.PLAIN, 12));
        chart.getXYPlot().setDomainAxis(dateAxis);
        //chart.getXYPlot().mapDatasetToRangeAxis(0, 0);
        panel = new ChartPanel(chart, 300, 300, 100, 100, 2000, 2000, true, false, true, true, true, true);
        panel.setPreferredSize(new Dimension(500, 300));

    }


    public ChartPanel simulate_graph() {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setVisible(true);
        return panel;
    }

    public void applyFilters(List filters){
        updateDataset(dataset, filters);
        panel.getChart().fireChartChanged();
        panel.repaint();
    }


    public XYPlot getGraph() {
        return chart.getXYPlot();
    }

    private XYSeriesCollection createDataset(java.util.List<ArticleFilter> filters) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        updateDataset(dataset, filters);
        return dataset;
    }

    public void updateDataset(XYSeriesCollection dataset, List<ArticleFilter> articleFilter) {
        System.out.println("dataset update called");
        long startDate = computeKeywordFrequency(articleFilter);
        dataset.removeAllSeries();
        String[] keywords = PREPROCESSOR.getKeywordsArr();
        for (int i = 0; i < keywordCount.length; i++) {
            XYSeries keywordSeries = new XYSeries(keywords[i]);
            for (int j = 0; j < keywordCount[i].length; j++) {
                if (keywordCount[i][j] >= 0) {
                    keywordSeries.add(((long) j * (1000 * 60 * 60 * 24) + startDate), keywordCount[i][j]);
                }
            }
            if (!keywordSeries.isEmpty()) {
                dataset.addSeries(keywordSeries);
            }
        }
    }

    private static long computeKeywordFrequency(List<ArticleFilter> filters) {
        String[] keywords = PREPROCESSOR.getKeywordsArr();
        List<Article> articleList = PREPROCESSOR.getArticleList();
        articleList.sort(Comparator.comparing(Article::getDate));
        long startDate = articleList.get(0).getDate().getTime();
        long endDate = articleList.get(articleList.size() - 1).getDate().getTime();
        int totalDays = (int) ((endDate - startDate) / (1000 * 60 * 60 * 24));
        int maxKeyCount = 0;
        String maxKeyword = "";
        int remainingArticlesCount = 0;
        List<String> currKeywords = null;
        if (filters != null) {
            for (ArticleFilter filter : filters) {
                if (filter.getField().equals(ArticleField.KEYWORD)) {
                    currKeywords = filter.getSelectedValues();
                }
            }
        }

        keywordCount = new int[keywords.length][totalDays + 1];
        clearKeyCount();
        for (int i = 0; i < keywords.length; i++) {
            for (Article article : articleList) {
                if (!Utils.isRemoveItem(filters, article)) {
                    remainingArticlesCount++;
                    if (currKeywords != null) {
                        if (!currKeywords.contains(keywords[i].toLowerCase(Locale.ROOT))) {
                            continue;
                        }
                    }
                    if (article.getKeywordsList().contains(keywords[i].toLowerCase(Locale.ROOT))) {
                        int dateIdx = (int) ((article.getDate().getTime() - startDate) / (1000 * 60 * 60 * 24));
                        if (keywordCount[i][dateIdx] == -1) {
                            keywordCount[i][dateIdx] = 0;
                        }
                        keywordCount[i][dateIdx]++;
                        if (maxKeyCount < keywordCount[i][dateIdx]) {
                            maxKeyCount = keywordCount[i][dateIdx];
                            maxKeyword = keywords[i] + " " + i;
                        }
                    }
                }
            }
        }
        System.out.println("Max key count = " + maxKeyCount);
        System.out.println("Max keyword = " + maxKeyword);
        System.out.println("Remaining articles = " + remainingArticlesCount);
        return startDate;
    }


    private static void clearKeyCount() {
        for (int[] ints : keywordCount) {
            Arrays.fill(ints, -1);
        }
    }
}
