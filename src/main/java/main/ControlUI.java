package main;

import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.ViewerListener;
import org.graphstream.ui.swingViewer.ViewerPipe;
import org.jfree.chart.ChartPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;


/**
 * UI Class that contains all the UI functions
 *
 * @author brandon
 */
public class ControlUI extends JFrame implements ActionListener, MouseWheelListener, MouseMotionListener {


    // Logic Variables
    private SentimentMainLogic sentimentMLogic;
    private LineGraphMainLogic ngramMLogic;
    private FilterMainLogic filterMLogic;
    private GraphUIProperty gUIProp;

    // UI Components

    private int frm_width, frm_height;
    private int ctrl_width, ctrl_height;
    private int filter_button_width, filter_button_height;

    private JFrame jfrm;

    private JLabel lbl_name;
    private JButton btn_loadNgram;
    private JButton filterButton;

    private JButton btn_loadGraph;

    private JPanel ctrl_panel;

    private JPanel filterPanel;

    private JPanel filterButtonPanel;

    private ChartPanel chartPanel;

    private View vw = null;

    // Set % of span
    private double btn_loadNgram_h = .1;


    /**
     * Constructor, inits and starts UI
     */
    public ControlUI() {
        //sets the values in the fields dealing with the screen
        init();
        //Creates the GUI
        startUI();
        System.setProperty("org.graphstream.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

    }

    /**
     * initializes height, width variables, etc.
     * and creates main logic object for use by UI
     */
    private void init() {

        // Init GUI Settings
        Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();
        frm_width = screen_dim.width;
        frm_height = screen_dim.height;

        ctrl_width = (int) (0.2 * (double) screen_dim.width);
        //ctrl_height = screen_dim.height;
        ctrl_height = (int) (0.2 * (double) screen_dim.height);
        //System.out.println(screen_dim.height);
        filter_button_height = ctrl_height;


        // Initialize graph UI property object to pass to sim
        gUIProp = new GraphUIProperty();
        gUIProp.height = screen_dim.height - 100;
        gUIProp.width = screen_dim.width - ctrl_width;
        gUIProp.posx = 0;
        gUIProp.posy = 0;

        sentimentMLogic = new SentimentMainLogic();
        ngramMLogic = new LineGraphMainLogic();
        filterMLogic = new FilterMainLogic();
    }

    /**
     * Adds objects to UI, prepares UI window
     */
    private void startUI() {

        jfrm = new JFrame();
        jfrm.setBounds(0, 0, frm_width, frm_height);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.getContentPane().setLayout(null);

        //This is the code that places the the new pannel
        //This may be where I need to shift everything to make it straight
        ctrl_panel = new JPanel();
        ctrl_panel.setBounds(frm_width - ctrl_width, 0, ctrl_width, ctrl_height);
        //ctrl_panel.setBounds(0, 0, ctrl_width, frm_height);
        jfrm.getContentPane().add(ctrl_panel);
        ctrl_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        try {
            ControlUI.class.getResourceAsStream("./src/main/java/main/images/graph.png");
            BufferedImage myPicture = ImageIO.read(new File("./src/main/java/main/images/graph.png"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            picLabel.setPreferredSize(new Dimension(100, 100));
            ctrl_panel.add(picLabel);
            System.out.println("Picture dimension = " + picLabel.getSize().getHeight() + picLabel.getSize().getWidth());

        } catch (Exception e) {
            e.printStackTrace();
        }

        lbl_name = new JLabel("Kronos Visualisation Tool");
        lbl_name.setFont(new Font("Century", Font.BOLD, 16));
        ctrl_panel.add(lbl_name);

        btn_loadNgram = new JButton("Show keyword ngram");
        btn_loadNgram.setSize(100, 100);
        //ctrl_panel.add(btn_loadNgram);
        btn_loadNgram.addActionListener(this);

        btn_loadGraph = new JButton("Show Sentiment Graph");
        //ctrl_panel.add(btn_loadGraph);
        btn_loadGraph.addActionListener(this);
        loadFilterPanel();
        loadGraph();
        loadNgram();
        btn_loadNgram = new JButton("Show keyword ngram");
        btn_loadNgram.setSize(100, 100);
        //ctrl_panel.add(btn_loadNgram);
        btn_loadNgram.addActionListener(this);

        filterButtonPanel = new JPanel();
        filterButton = new JButton("FILTER");
        filterButton.addActionListener(this);
        filterButtonPanel.add(filterButton);
        filterButtonPanel.setBounds(frm_width - ctrl_width, frm_height - ctrl_height, ctrl_width, ctrl_height);
        //ctrl_panel.setBounds(0, 0, ctrl_width, frm_height);
        jfrm.getContentPane().add(filterButtonPanel);
        ctrl_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        // Features to add
        // Block Label

        jfrm.setVisible(true);


        // We connect back the viewer to the graph,
        // the graph becomes a sink for the viewer.
        // We also install us as a viewer listener to
        // intercept the graphic events.
        //ViewerPipe fromViewer = vwr.newViewerPipe();
        //fromViewer.addViewerListener((ViewerListener) new NodeClickListener());
        //fromViewer.addSink(graph);


    }


    /**
     * Action listener for click of buttons
     * This
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();


        //If the load graph button is pressed
        if (source == btn_loadGraph) {
            loadGraph();
        }
        if (source == btn_loadNgram) {
            loadNgram();
        }
        if (source == filterButton) {
            filterMLogic.makeFilters();
            reloadGraph(filterMLogic.getFilters());
            reloadNgram(filterMLogic.getFilters());
        }

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

    /**
     * Function to load graph and create graph listener
     */
    NodeClickListener clisten = null;

    public void loadGraph() {

        // close event listener for mouse first before removing view
        // in next step
        if (clisten != null) {
            clisten.viewClosed(null);
        }
        // Remove view if exists
        if (vw != null) {
            jfrm.remove(vw);
        }

        //This is a sort of wrapper class which calls all
        //the other methods in GraphSims and GraphSimsAlgorithm
        //the actually creates the graph and animates it


        Viewer vwr = sentimentMLogic.simulate_graph();
        vwr.disableAutoLayout();
        vw = vwr.addDefaultView(false);

        JLabel title = new JLabel("Sentiment Trend");
        vw.setSize(gUIProp.width, gUIProp.height / 2);
        vw.setLocation(gUIProp.posx, gUIProp.posy);
        vw.add(title);


        // We connect back the viewer to the graph,
        // the graph becomes a sink for the viewer.
        // We also install us as a viewer listener to
        // intercept the graphic events.
        ViewerPipe fromViewer = vwr.newViewerPipe();
        clisten = new NodeClickListener(fromViewer, vw, sentimentMLogic.getGraph());
        fromViewer.addViewerListener((ViewerListener) clisten);
        vw.addMouseWheelListener(this);
        vw.addMouseMotionListener(this);

        // Add in frame
        jfrm.add(vw, BorderLayout.LINE_START);

    }

    public void reloadGraph(java.util.List articleFilterList) {

        // close event listener for mouse first before removing view
        // in next step
        if (clisten != null) {
            clisten.viewClosed(null);
        }
        // Remove view if exists
        if (vw != null) {
            jfrm.remove(vw);
        }

        //This is a sort of wrapper class which calls all
        //the other methods in GraphSims and GraphSimsAlgorithm
        //the actually creates the graph and animates it

        sentimentMLogic.applyFilters(articleFilterList);
        Viewer vwr = sentimentMLogic.simulate_graph();
        vwr.disableAutoLayout();
        vw = vwr.addDefaultView(false);

        JLabel xtitle = new JLabel("Sentiment Trend");
        vw.setSize(gUIProp.width, gUIProp.height / 2);
        vw.setLocation(gUIProp.posx, gUIProp.posy);
        vw.add(xtitle);


        // We connect back the viewer to the graph,
        // the graph becomes a sink for the viewer.
        // We also install us as a viewer listener to
        // intercept the graphic events.
        ViewerPipe fromViewer = vwr.newViewerPipe();
        clisten = new NodeClickListener(fromViewer, vw, sentimentMLogic.getGraph());
        fromViewer.addViewerListener((ViewerListener) clisten);
        vw.addMouseWheelListener(this);
        vw.addMouseMotionListener(this);


        // Add in frame
        jfrm.add(vw, BorderLayout.LINE_START);

    }


    public void loadFilterPanel() {
        // Remove view if exists
        if (filterPanel != null) {
            jfrm.remove(filterPanel);
        }

        //This is a sort of wrapper class which calls all
        //the other methods in GraphSims and GraphSimsAlgorithm
        //the actually creates the graph and animates it

        filterPanel = filterMLogic.simulate_filter();

        filterPanel.setSize(ctrl_width, frm_height - ctrl_height - filter_button_height);
        System.out.println("ctrl_width"+ctrl_width);
        System.out.println("ctrl_height"+(frm_height - ctrl_height - filter_button_height));

        filterPanel.setLocation(frm_width - ctrl_width, ctrl_height);

        filterPanel.setVisible(true);
        // Add in frame
        jfrm.add(filterPanel, BorderLayout.LINE_START);
    }


    /**
     * Function to load graph and create graph listener
     */
    public void loadNgram() {

        // Remove view if exists
        if (chartPanel != null) {
            jfrm.remove(chartPanel);
        }

        //This is a sort of wrapper class which calls all
        //the other methods in GraphSims and GraphSimsAlgorithm
        //the actually creates the graph and animates it


        chartPanel = ngramMLogic.simulate_graph();

        chartPanel.setSize(gUIProp.width, gUIProp.height / 2);
        chartPanel.setLocation(gUIProp.posx, gUIProp.height / 2);

        chartPanel.setVisible(true);
        // Add in frame
        jfrm.add(chartPanel, BorderLayout.LINE_START);
    }

    public void reloadNgram(java.util.List articleFilter) {

        // Remove view if exists
        if (chartPanel != null) {
            jfrm.remove(chartPanel);
        }
        ngramMLogic.applyFilters(articleFilter);
        chartPanel = ngramMLogic.simulate_graph();

        chartPanel.setSize(gUIProp.width, gUIProp.height / 2);
        chartPanel.setLocation(gUIProp.posx, gUIProp.height / 2);

        chartPanel.setVisible(true);
        // Add in frame
        jfrm.add(chartPanel, BorderLayout.LINE_START);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (vw != null) {

        }
    }

}
