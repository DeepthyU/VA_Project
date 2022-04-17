package main.employee;

import hierarchicaledgebundling.HebFrame;
import preprocessing.Utils;

import javax.swing.*;
import java.nio.charset.Charset;

public class HebMainLogic {

    private static final String DATA_PATH= "./src/main/data/gastech_data/data/adjacency.json";
    private HebFrame panel;
    public HebMainLogic()
    {
        String data = Utils.readFile(DATA_PATH, Charset.defaultCharset());
        panel = new HebFrame(data);
        panel.setSize(750, 750);
    }


    public JPanel simulate_graph() {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVisible(true);
        scrollPane.repaint();
        return panel;
    }



}