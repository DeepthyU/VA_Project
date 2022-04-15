package main;

import org.graphstream.ui.swingViewer.View;
import org.graphstream.ui.swingViewer.Viewer;
import org.graphstream.ui.swingViewer.ViewerListener;
import org.graphstream.ui.swingViewer.ViewerPipe;
import preprocessing.Preprocessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TabbedVisMainLogic extends JPanel implements ActionListener, MouseWheelListener, MouseMotionListener {

    private static final Preprocessor PREPROCESSOR = new Preprocessor();

    //article tab
    private SentimentMainLogic sentimentMainLogic;
    private FrequencyVisMainLogic frequencyVisMainLogic;
    private FilterMainLogic filterMainLogic;

    //employee tab
    private HebMainLogic hebMainLogic;

    private JTabbedPane mainVisPanel;
    private JPanel articlePanel;
    private JPanel employeePanel;

    private JButton filterButton;
    private JButton resetAllButton;

    private JPanel filterPanel;
    private JPanel filterButtonPanel;
    private JPanel sliderPanel;
    private JTabbedPane freqPanel;
    private View vw = null;
    private GraphUIProperty gUIProp;

    private int frm_width, frm_height;
    private int ctrl_width, ctrl_height;
    private int filter_button_height;
    
    public TabbedVisMainLogic() {
        mainVisPanel = new JTabbedPane();
        articlePanel = new JPanel();
        initGUI();
        sentimentMainLogic = new SentimentMainLogic();
        filterMainLogic = new FilterMainLogic();
        frequencyVisMainLogic = new FrequencyVisMainLogic();
        hebMainLogic = new HebMainLogic();
        makeArticleVis();
        makeEmployeeVis();
        mainVisPanel = new JTabbedPane();
        mainVisPanel.addTab("Article Tab", articlePanel);
        mainVisPanel.addTab("Employee Tab", employeePanel);
    }

    private void makeEmployeeVis() {
        employeePanel = new JPanel();
        employeePanel.setBounds(0, 0, frm_width, frm_height);
        employeePanel.setLayout(null);
        loadSliderPanel();
        loadHebPanel();
    }

    private void loadHebPanel() {
        JPanel hebPanel = hebMainLogic.simulate_graph();
        hebPanel.setSize(gUIProp.width, gUIProp.height);
        hebPanel.setLocation(gUIProp.posx, gUIProp.posy);
        employeePanel.add(hebPanel);
    }

    public JTabbedPane simulateTabView(){
        mainVisPanel.setSize(frm_width, frm_height);
        mainVisPanel.setLocation(gUIProp.posx, gUIProp.posy);
        mainVisPanel.setVisible(true);
        return mainVisPanel;
    }

    public JPanel getArticlePanel(){
        return articlePanel;
    }
    public JPanel getEmployeePanel(){
        return employeePanel;
    }

    private void initGUI() {
        // Init GUI Settings
        Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();
        frm_width = screen_dim.width;
        frm_height = screen_dim.height;

        ctrl_width = (int) (0.2 * (double) screen_dim.width);
        ctrl_height = (int) (0.2 * (double) screen_dim.height);

        // Initialize graph UI property object to pass to sim
        gUIProp = new GraphUIProperty();
        gUIProp.height = screen_dim.height - 100;
        gUIProp.width = screen_dim.width - ctrl_width;
        gUIProp.posx = 0;
        gUIProp.posy = 0;

    }


    private void makeArticleVis() {
        articlePanel = new JPanel();
        articlePanel.setBounds(0, 0, frm_width, frm_height);
        articlePanel.setLayout(null);


        loadFilterPanel();
        loadGraph();
        loadFreqTab();

        filterButtonPanel = new JPanel();
        filterButton = new JButton("FILTER");
        filterButton.addActionListener(this);
        resetAllButton = new JButton("REMOVE FILTERS");
        resetAllButton.addActionListener(this);
        filterButtonPanel.add(filterButton);
        filterButtonPanel.add(resetAllButton);
        filterButtonPanel.setBounds(frm_width - ctrl_width, frm_height - ctrl_height, ctrl_width, ctrl_height);
        articlePanel.add(filterButtonPanel);

        // Features to add
        // Block Label

        articlePanel.setVisible(true);
    }

    /**
     * Action listener for click of buttons
     * This
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == filterButton) {
            filterMainLogic.makeFilters();
            reloadGraph(filterMainLogic.getFilters());
            reloadFreqTab(filterMainLogic.getFilters());
        } else if (source == resetAllButton) {
            filterMainLogic.doResetButtonAction();
            filterMainLogic.makeFilters();
            reloadGraph(filterMainLogic.getFilters());
            reloadFreqTab(filterMainLogic.getFilters());
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
            articlePanel.remove(vw);
        }

        //This is a sort of wrapper class which calls all
        //the other methods in GraphSims and GraphSimsAlgorithm
        //the actually creates the graph and animates it


        Viewer vwr = sentimentMainLogic.simulate_graph();
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
        clisten = new NodeClickListener(fromViewer, vw, sentimentMainLogic.getGraph());
        fromViewer.addViewerListener((ViewerListener) clisten);
        vw.addMouseWheelListener(this);
        vw.addMouseMotionListener(this);

        // Add in frame
        articlePanel.add(vw, BorderLayout.LINE_START);

    }

    public void reloadGraph(java.util.List articleFilterList) {

        // close event listener for mouse first before removing view
        // in next step
        if (clisten != null) {
            clisten.viewClosed(null);
        }
        // Remove view if exists
        if (vw != null) {
            articlePanel.remove(vw);
        }

        //This is a sort of wrapper class which calls all
        //the other methods in GraphSims and GraphSimsAlgorithm
        //the actually creates the graph and animates it

        sentimentMainLogic.applyFilters(articleFilterList);
        Viewer vwr = sentimentMainLogic.simulate_graph();
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
        clisten = new NodeClickListener(fromViewer, vw, sentimentMainLogic.getGraph());
        fromViewer.addViewerListener((ViewerListener) clisten);
        vw.addMouseWheelListener(this);
        vw.addMouseMotionListener(this);


        // Add in frame
        articlePanel.add(vw, BorderLayout.LINE_START);

    }


    public void loadFilterPanel() {
        // Remove view if exists
        if (filterPanel != null) {
            articlePanel.remove(filterPanel);
        }

        //This is a sort of wrapper class which calls all
        //the other methods in GraphSims and GraphSimsAlgorithm
        //the actually creates the graph and animates it

        filterPanel = filterMainLogic.simulate_filter();

        filterPanel.setSize(ctrl_width, frm_height - filter_button_height);

        filterPanel.setLocation(frm_width - ctrl_width, gUIProp.posy);

        filterPanel.setVisible(true);
        // Add in frame
        articlePanel.add(filterPanel, BorderLayout.LINE_START);
    }


    public void loadSliderPanel() {
        // Remove view if exists
        if (sliderPanel != null) {
            employeePanel.remove(sliderPanel);
        }

        //This is a sort of wrapper class which calls all
        //the other methods in GraphSims and GraphSimsAlgorithm
        //the actually creates the graph and animates it

        sliderPanel = new JPanel();

        JSlider slider = new JSlider(JSlider.VERTICAL);
        sliderPanel.add(slider);

        sliderPanel.setSize(ctrl_width, frm_height - filter_button_height);

        sliderPanel.setLocation(frm_width - ctrl_width, ctrl_height);

        sliderPanel.setVisible(true);
        // Add in frame
        employeePanel.add(sliderPanel, BorderLayout.LINE_START);
    }


    /**
     * Function to load graph and create graph listener
     */
    public void loadFreqTab() {

        // Remove view if exists
        if (freqPanel != null) {
            articlePanel.remove(freqPanel);
        }

        //This is a sort of wrapper class which calls all
        //the other methods in GraphSims and GraphSimsAlgorithm
        //the actually creates the graph and animates it


        freqPanel = frequencyVisMainLogic.simulate_tab(gUIProp.posx, gUIProp.height / 2, gUIProp.width, gUIProp.height / 2);
        freqPanel.setSize(gUIProp.width, gUIProp.height / 2);
        freqPanel.setLocation(gUIProp.posx, gUIProp.height / 2);

        freqPanel.setVisible(true);
        // Add in frame
        articlePanel.add(freqPanel, BorderLayout.LINE_START);
    }

    public void reloadFreqTab(java.util.List articleFilter) {

        // Remove view if exists
        if (freqPanel != null) {
            articlePanel.remove(freqPanel);
        }
        frequencyVisMainLogic.applyFilters(articleFilter);
        freqPanel = frequencyVisMainLogic.simulate_tab(gUIProp.posx, gUIProp.height / 2, gUIProp.width, gUIProp.height / 2);
        freqPanel.setSize(gUIProp.width, gUIProp.height / 2);
        freqPanel.setLocation(gUIProp.posx, gUIProp.height / 2);

        freqPanel.repaint();
        // Add in frame
        articlePanel.add(freqPanel, BorderLayout.LINE_START);
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