package scatterplot;

import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ChartPanel scatterPlot = new ScatterPlotFactory().getEmailClusteringPlot(
                "/Users/Yvan/Git/VA_Project/src/main/data/gastech_data/data/tsne.csv"
        );
        scatterPlot.setSize(750, 750);


        JFrame frame = new JFrame("ScatterPlot Frame Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLayout(new GridLayout(1, 1));
        frame.add(scatterPlot);
    }
}