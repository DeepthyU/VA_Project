package main.employee;

import hierarchicaledgebundling.HebFrame;
import preprocessing.Utils;
import utils.VisualizerPrefs;

import javax.swing.*;
import java.nio.charset.Charset;

public class HebMainLogic {

    private static final String DATA_PATH= VisualizerPrefs.getInstance().getFullDataDirPath().resolve("/adjacency.json").toString();
    private HebFrame panel;
    public HebMainLogic()
    {
        String data = Utils.readFile(DATA_PATH, Charset.defaultCharset());
        panel = new HebFrame(data);
        panel.setSize(750, 750);
    }

    public JPanel simulate_graph() {
        return panel;
    }
    public void setStartFilter(int i) {
        panel.setStartFilter(i);
    }
    public void setEndFilter(int i) {
        panel.setEndFilter(i);
    }
}