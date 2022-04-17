package main.article;

import org.apache.commons.collections4.CollectionUtils;
import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.jfree.chart.ChartPanel;
import preprocessing.Preprocessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.util.List;

public class ArticleAutoVisMainLogic {

    private SentimentMainLogic sentimentMainLogic;
    private ArticleTSNEMainLogic articleTSNEMainLogic;
    private JTabbedPane mainAutoPanel;
    private View vw;
    private ChartPanel articleTSNEChart;
private Preprocessor preprocessor;

    public ArticleAutoVisMainLogic() {
        preprocessor = new Preprocessor();
        mainAutoPanel = new JTabbedPane();

        sentimentMainLogic = new SentimentMainLogic();
        Viewer vwr = sentimentMainLogic.simulate_graph();
        vwr.disableAutoLayout();
        vw = vwr.addDefaultView(false);

        JLabel title = new JLabel("Sentiment Trend");
        vw.add(title);

        articleTSNEMainLogic = new ArticleTSNEMainLogic(preprocessor);
        articleTSNEChart = articleTSNEMainLogic.simulate_graph();
    }

    public JTabbedPane simulate_tab(int width, int height) {
        //tab1
        vw.setSize(width, height);
        vw.setVisible(true);
        // Add in frame
        mainAutoPanel.addTab("Sentiment Trend", vw);
        //tab2
        articleTSNEChart.setSize(width, height);

        // Add in frame
        mainAutoPanel.addTab("Clustering", articleTSNEChart);
        return mainAutoPanel;
    }

    public void applyFilters(List filters){
            sentimentMainLogic.applyFilters(filters);
            articleTSNEMainLogic.applyFilters(filters);
    }
    public JTabbedPane getPane() {
        return mainAutoPanel;
    }


    /**
     * Action listener for when the mouse wheel is moved.
     * Depending on if the mouse wheel is moved up or down the
     * graph zooms in our out. Also, it zooms in on a section of the graph
     * where the mouse is. It does this by dividing the graph
     * into a three by three grid. I have it hard coded for values that I
     * believe are specific for my computer. One of the things I wanted to
     * change was get it so that the value are based of the fields that get
     * the screen size.
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (vw != null) {
            int notches = e.getWheelRotation();
            Point point = e.getPoint();
            double i = vw.getCamera().getViewPercent();
            if (i < 1) {
                if (point.getX() < 400) {
                    //400 is an example of a hardcode value to change
                    if (point.getY() < 300) {
                        vw.getCamera().getViewCenter().move(-1, 1);
                    } else if (point.getY() < 600) {
                        vw.getCamera().getViewCenter().move(-1, 0);
                    } else {
                        vw.getCamera().getViewCenter().move(-1, -1);
                    }
                } else if (point.getX() < 800) {
                    if (point.getY() < 300) {
                        vw.getCamera().getViewCenter().move(0, 1);
                    } else if (point.getY() < 600) {
                        vw.getCamera().getViewCenter().move(0, 0);
                    } else {
                        vw.getCamera().getViewCenter().move(0, -1);
                    }
                } else {
                    if (point.getY() < 300) {
                        vw.getCamera().getViewCenter().move(1, 1);
                    } else if (point.getY() < 600) {
                        vw.getCamera().getViewCenter().move(1, 0);
                    } else {
                        vw.getCamera().getViewCenter().move(1, -1);
                    }
                }
            } else {
                vw.getCamera().resetView();
            }


            if (notches > 0) {
                vw.getCamera().setViewPercent(i * 1.1);
            } else {

                vw.getCamera().setViewPercent(i * 0.9);
            }
        }
    }

}