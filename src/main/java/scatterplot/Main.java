package scatterplot;

import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        ChartPanel emailPlot = new ScatterPlotFactory().getEmailClusteringPlot(
                "./src/main/data/gastech_data/data/email_tsne/0_9.csv"
        );
        emailPlot.setSize(400, 400);

        ChartPanel articlePlot = new ScatterPlotFactory().getArticleClusteringPlot(
                "./src/main/data/gastech_data/data/article_tsne.csv"
        );
        articlePlot.setSize(750, 750);


        JFrame frame = new JFrame("ScatterPlot Frame Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLayout(new GridLayout(1, 2));
//        frame.add(emailPlot);
        frame.add(articlePlot);
    }
}