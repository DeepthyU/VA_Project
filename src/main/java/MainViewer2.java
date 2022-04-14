//import org.graphstream.graph.Graph;
//import vis.SentimentVisualizer;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//
//public class MainViewer2 implements Runnable {
//
//    private boolean filterFlag;
//    private JButton button;
//    private JLabel label;
//
//    private DefaultView viewPanel2;
//
//    protected static String styleSheet =
//            "graph {\n" +
//                    " fill-color: red;" +
//                    " fill-mode: plain;"+
//                    "}"+
//                    "node {	size: 2px, 2px; fill-color: black; } " +
//                    "node.marked { fill-color: red;}" +
//                    "node.positive { fill-color: blue;}" +
//                    "node.negative { fill-color: red;}" +
//                    "edge {\n" +
//                    "\tshape: line;\n" +
//                    "\tarrow-size: 3px, 2px;\n" +
//                    "}" +
//                    "edge.marked {\n" +
//                    "\tshape: line;\n" +
//                    "\tfill-color: red;\n" +
//                    "\tarrow-size: 3px, 2px;\n" +
//                    "}";
//
//
//
//    public static void main(String args[]) {
//        SwingUtilities.invokeLater(new MainViewer2());
//    }
//
//
//    @Override
//    public void run() {
//        System.setProperty("org.graphstream.ui", "swing");
//        SentimentVisualizer sv = new SentimentVisualizer();
//
//        JFrame frame = new JFrame("Vis tool");
//
//        frame.add(createPanel1(sv), BorderLayout.CENTER);
//        frame.setLocationByPlatform(true);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//
//    }
//
//    private JPanel createPanel1(SentimentVisualizer mv) {
//        JPanel panel = new JPanel(new BorderLayout(5, 5));
//        panel.setBackground(Color.BLACK);
//        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//
//        Graph graph = mv.prepareGraph(filterFlag);
//        graph.setAttribute("ui.stylesheet", styleSheet);
//        SwingViewer viewer2 = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
//        //Viewer viewer2 = graph.display(false);
//
//        viewer2.disableAutoLayout();
//
//        //viewPanel2   = (DefaultView)viewer2.addView( "view1", new SwingGraphRenderer() );
//        //viewPanel2 = new DefaultView(viewer2, "panel2", new SwingGraphRenderer());
//        viewPanel2 = (DefaultView)viewer2.addDefaultView(false, new SwingGraphRenderer());
//        viewPanel2.getCamera().setAutoFitView(true);
//        //viewPanel2.setPreferredSize(new Dimension(500, 500));
//
//        button = new JButton("Filter");
//        button.setBounds(10, 10, 100, 20);
//        label = new JLabel("Filter : off");
//        label.setSize(100,20);
//        label.setBounds(30, 30, 100, 20);
//        button.addActionListener(new AbstractAction("add") {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                filterFlag = !filterFlag;
//                label.setText("Filter : " + filterFlag);
//
//                if (viewPanel2 != null) {
//                    panel.remove(viewPanel2);
//                }
//                Viewer viewer2 = new SwingViewer(mv.prepareGraph(filterFlag), Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
//                viewPanel2 = new DefaultView(viewer2, "panel2", new SwingGraphRenderer());
//                panel.add(viewPanel2);
//            }
//        });
//        panel.add(button);
//        panel.add(label);
//        panel.add(viewPanel2);
//        return panel;
//    }
//
//    private JPanel createPanel2(SentimentVisualizer mv) {
//        JPanel panel = new JPanel(new BorderLayout(5, 5));
//        Viewer viewer2 = new SwingViewer(mv.prepareGraph(filterFlag), Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
//
//        ViewPanel viewPanel2 = new DefaultView(viewer2, "panel2", new SwingGraphRenderer());
//
//        viewPanel2.setPreferredSize(new Dimension(500, 500));
//        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//        panel.add(viewPanel2, BorderLayout.CENTER);
//
////        JComboBox<Transaction> minoltaCommunitiesNames = new JComboBox<>();
////        panel.add(minoltaCommunitiesNames, BorderLayout.AFTER_LAST_LINE);
//
//        return panel;
//    }
//
//
//    public class Transaction {
//
//    }
//
//}