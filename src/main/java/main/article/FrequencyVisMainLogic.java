package main.article;

import org.apache.commons.collections4.CollectionUtils;
import org.jfree.chart.ChartPanel;
import preprocessing.Preprocessor;

import javax.swing.*;
import java.util.List;

public class FrequencyVisMainLogic {

    private LineGraphMainLogic lineGraphMainLogic;
    private WordCloudMainLogic wordCloudMainLogic;
    private JTabbedPane mainFrequencyPanel;
    private ChartPanel lineChartPanel;
    private JScrollPane wordCloudPanel;
    private static final Preprocessor PREPROCESSOR = new Preprocessor();

    public FrequencyVisMainLogic() {
        mainFrequencyPanel = new JTabbedPane();

        lineGraphMainLogic = new LineGraphMainLogic(PREPROCESSOR);
        lineChartPanel = lineGraphMainLogic.simulate_graph();

        wordCloudMainLogic = new WordCloudMainLogic(PREPROCESSOR);
        wordCloudPanel = wordCloudMainLogic.simulate_graph();
    }

    public JTabbedPane simulate_tab(int width, int height) {
        //tab1
        lineChartPanel.setSize(width, height);
        lineChartPanel.setVisible(true);
        // Add in frame
        mainFrequencyPanel.addTab("Ngram", lineChartPanel);
        //tab2
        wordCloudPanel.setSize(width, height);

        // Add in frame
        mainFrequencyPanel.addTab("WordCloud", wordCloudPanel);
        return mainFrequencyPanel;
    }

    public void applyFilters(List filters){
            lineGraphMainLogic.applyFilters(filters);
            wordCloudMainLogic.applyFilters(filters);
    }
    public JTabbedPane getPane() {
        return mainFrequencyPanel;
    }
}