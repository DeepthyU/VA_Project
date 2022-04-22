package main.article;

import org.jfree.chart.ChartPanel;
import preprocessing.Preprocessor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ArticleAutoVisMainLogic {

    private SentimentMainLogic sentimentVisualizer;
    private ArticleTSNEMainLogic articleTSNEMainLogic;
    private JTabbedPane mainAutoPanel;
    private ChartPanel articleTSNEChart;
private Preprocessor preprocessor;

    public ArticleAutoVisMainLogic() {
        preprocessor = new Preprocessor();
        mainAutoPanel = new JTabbedPane();

        sentimentVisualizer = new SentimentMainLogic(preprocessor);

        articleTSNEMainLogic = new ArticleTSNEMainLogic(preprocessor);
        articleTSNEChart = articleTSNEMainLogic.simulate_graph();
    }

    public JTabbedPane simulate_tab(int width, int height) {
        //tab1
        sentimentVisualizer.getPanel().setSize(width, height);
        sentimentVisualizer.getPanel().setVisible(true);
        // Add in frame
        mainAutoPanel.addTab("Sentiment Trend", sentimentVisualizer.getPanel());
        //tab2
        articleTSNEChart.setSize(width, height);
        // Add in frame
        mainAutoPanel.addTab("Clustering", articleTSNEChart);
        mainAutoPanel.setFont( new Font( "Tahoma", Font.BOLD, 10 ) );
        return mainAutoPanel;
    }

    public void applyFilters(List filters){
        if (null != filters) {
            sentimentVisualizer.applyFilters(filters);
            articleTSNEMainLogic.applyFilters(filters);
        }
    }
    public JTabbedPane getPane() {
        return mainAutoPanel;
    }
}