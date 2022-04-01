import org.graphstream.graph.Graph;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import vis.ArticleVisualizer;

import javax.swing.*;
import java.awt.*;

public class MainViewer implements Runnable {


    public static void main(String args[]) {
        SwingUtilities.invokeLater(new MainViewer());
    }


    @Override
    public void run() {
        System.setProperty("org.graphstream.ui", "swing");
        ArticleVisualizer av = new ArticleVisualizer();
        Graph sourceGraph = av.prepareGraph(true);
        Viewer viewer1 = new SwingViewer(sourceGraph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer1.enableAutoLayout(new SpringBox());
        Viewer viewer2 = new SwingViewer(sourceGraph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer2.enableAutoLayout(new SpringBox());
        ViewPanel viewPanel1 = new DefaultView(viewer1,"panel1",new SwingGraphRenderer());
        viewPanel1.setPreferredSize(new Dimension(750,350));
        ViewPanel viewPanel2 = new DefaultView(viewer2,"panel2",new SwingGraphRenderer());
        viewPanel2.setPreferredSize(new Dimension(750,350));

        JFrame frame = new JFrame("Multiple Views");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(createPanel1(viewPanel1), BorderLayout.BEFORE_LINE_BEGINS);
        frame.add(createPanel2(viewPanel2), BorderLayout.AFTER_LINE_ENDS);

        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        //new FilterCheckBoxes();
    }

    private JPanel createPanel1(ViewPanel viewPanel) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(viewPanel, BorderLayout.WEST);

        JComboBox<Transaction> lCCommunitiesNames = new JComboBox<>();
        panel.add(lCCommunitiesNames, BorderLayout.AFTER_LAST_LINE);

        return panel;
    }

    private JPanel createPanel2(ViewPanel viewPanel) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(viewPanel, BorderLayout.CENTER);

        JComboBox<Transaction> minoltaCommunitiesNames = new JComboBox<>();
        panel.add(minoltaCommunitiesNames, BorderLayout.AFTER_LAST_LINE);

        return panel;
    }


    public class Transaction {

    }

}