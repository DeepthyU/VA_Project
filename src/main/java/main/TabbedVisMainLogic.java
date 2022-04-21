package main;

import main.article.*;
import main.employee.EmployeeTSNEMainLogic;
import main.employee.HebMainLogic;
import org.jfree.chart.ChartPanel;
import vis.SentimentVisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class TabbedVisMainLogic extends JPanel implements ActionListener, MouseWheelListener, MouseMotionListener {

    //article tab
    private FrequencyVisMainLogic frequencyVisMainLogic;
    private FilterMainLogic filterMainLogic;
    private ArticleAutoVisMainLogic articleAutoVisMainLogic;

    //employee tab
    private HebMainLogic hebMainLogic;
    private EmployeeTSNEMainLogic empTSNEMainLogic;

    private JTabbedPane mainVisPanel;
    private JLayeredPane articlePanel;
    private JPanel employeePanel;

    private JButton filterButton;
    private JButton resetAllButton;

    private JPanel filterPanel;
    private JSplitPane articleVisPanel;
    private JPanel filterButtonPanel;
    private JPanel datePanel;
    private JComboBox<String> startDatePicker;
    private JComboBox<String> endDatePicker;
    private JTabbedPane freqPanel;
    private JTabbedPane autoPanel;

    private JPanel hebPanel;
    private ChartPanel empTSNEChart;
    private JSplitPane empVisPanel;
    private GraphUIProperty gUIProp;

    private int frm_width, frm_height;
    private int ctrl_width, ctrl_height;
    private int filter_button_height;

    public TabbedVisMainLogic() {
        mainVisPanel = new JTabbedPane();
        initGUI();
        filterMainLogic = new FilterMainLogic();
        frequencyVisMainLogic = new FrequencyVisMainLogic();
        articleAutoVisMainLogic = new ArticleAutoVisMainLogic();
        hebMainLogic = new HebMainLogic();
        empTSNEMainLogic = new EmployeeTSNEMainLogic();
        makeArticleVis();
        makeEmployeeVis();
        mainVisPanel = new JTabbedPane();
        mainVisPanel.addTab("Article Tab", articlePanel);
        mainVisPanel.addTab("Employee Tab", employeePanel);
        mainVisPanel.setFont( new Font( "Tahoma", Font.BOLD, 10 ) );

    }

    private void makeEmployeeVis() {
        employeePanel = new JPanel();
        empVisPanel = new JSplitPane();
        employeePanel.setBounds(0, 0, gUIProp.width, gUIProp.height);
        employeePanel.setLayout(null);
        empVisPanel.setOneTouchExpandable(true);
        empVisPanel.setDividerLocation(frm_width / 2 - 100);
        loadHebPanel();
        loadEmpTSNEPanel();
        empVisPanel.setLocation(gUIProp.posx, gUIProp.posy);
        empVisPanel.setSize(frm_width - 200, frm_height - 100);
        employeePanel.add(empVisPanel);
        loadDatePanel();
    }

    private void loadHebPanel() {
        // Remove view if exists
        if (hebPanel != null) {
            empVisPanel.remove(hebPanel);
        }

        hebPanel = hebMainLogic.simulate_graph();
        hebPanel.setSize(frm_width / 2, frm_height - 150);
        hebPanel.setLocation(gUIProp.posx, gUIProp.posy);
        empVisPanel.setLeftComponent(hebPanel);
    }

    private void loadEmpTSNEPanel() {
        // Remove view if exists
        if (empTSNEChart != null) {
            empVisPanel.remove(empTSNEChart);
        }

        empTSNEChart = empTSNEMainLogic.simulate_graph();
        empTSNEChart.setSize(frm_width / 2 - 100, frm_height - 150);
        empTSNEChart.setLocation(frm_width / 2 - 100, gUIProp.posy);
        empVisPanel.setRightComponent(empTSNEChart);
    }

    public JTabbedPane simulateTabView() {
        mainVisPanel.setSize(frm_width, frm_height);
        mainVisPanel.setLocation(gUIProp.posx, gUIProp.posy);
        mainVisPanel.setVisible(true);
        return mainVisPanel;
    }

    public JLayeredPane getArticlePanel() {
        return articlePanel;
    }

    public JPanel getEmployeePanel() {
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
        articlePanel = new JLayeredPane();
        articlePanel.setBounds(0, 0, frm_width, frm_height);
        articlePanel.setLayout(null);

        articleVisPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        articleVisPanel.setOneTouchExpandable(true);
        articleVisPanel.setDividerLocation(frm_height / 2);

        loadFilterPanel();

        loadGraph(null);
        loadFreqTab(null);
        articleVisPanel.setSize(gUIProp.width, gUIProp.height);
        articleVisPanel.setLocation(gUIProp.posx, gUIProp.posy);
        articlePanel.add(articleVisPanel);
        filterButtonPanel = new JPanel();
        filterButton = new JButton("Filter");
        filterButton.addActionListener(this);
        filterButton.setPreferredSize(new Dimension(ctrl_width - 30, 25));
        filterButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        resetAllButton = new JButton("Remove Filters");
        resetAllButton.addActionListener(this);
        resetAllButton.setPreferredSize(new Dimension(ctrl_width - 30, 25));
        resetAllButton.setFont(new Font("Tahoma", Font.PLAIN, 12));

        filterButtonPanel.add(filterButton);
        filterButtonPanel.add(resetAllButton);
        filterButtonPanel.setBounds(frm_width - ctrl_width, frm_height - ctrl_height - 50, ctrl_width, ctrl_height);
        articlePanel.add(filterButtonPanel, JLayeredPane.PALETTE_LAYER);

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
        if (source == filterButton ) {
            filterMainLogic.makeFilters();
            loadGraph(filterMainLogic.getFilters());
            loadFreqTab(filterMainLogic.getFilters());
            articleVisPanel.setOneTouchExpandable(true);
            articleVisPanel.setDividerLocation(frm_height / 2);

        } else if (source == resetAllButton) {
            filterMainLogic.doResetButtonAction();
            filterMainLogic.makeFilters();
            loadGraph(filterMainLogic.getFilters());
            loadFreqTab(filterMainLogic.getFilters());
            articleVisPanel.setOneTouchExpandable(true);
            articleVisPanel.setDividerLocation(frm_height / 2);
        } else if (source == startDatePicker || source == endDatePicker) {
            System.out.println("DatePicker fired an action");
            if (Objects.equals(e.getActionCommand(), "comboBoxChanged")) {
                // Prepare variables for easier access
                String startDate = (String) startDatePicker.getSelectedItem();
                int startIndex = startDatePicker.getSelectedIndex();
                String endDate = (String) endDatePicker.getSelectedItem();
                int endIndex = endDatePicker.getSelectedIndex();
                // Debug prints
                System.out.println("Chosen dates: " + startDate + " [" + startIndex + "] to " + endDate + " [" + endIndex + "]");
                // Set the HEB to use a filter
                hebMainLogic.setStartFilter(startIndex);
                hebMainLogic.setEndFilter(endIndex);
                empTSNEMainLogic.setDateRange(startIndex, endIndex);

            }
        }
    }

    /**
     * Function to load graph and create graph listener
     */
    NodeClickListener clisten = null;

    public void loadGraph(java.util.List articleFilterList) {

        // close event listener for mouse first before removing view
        // in next step
        if (clisten != null) {
            clisten.viewClosed(null);
        }
        // Remove view if exists
        if (autoPanel != null) {
            articleVisPanel.remove(autoPanel);
        }


        //This is a sort of wrapper class which calls all
        //the other methods in GraphSims and GraphSimsAlgorithm
        //the actually creates the graph and animates it

        articleAutoVisMainLogic.applyFilters(articleFilterList);
        autoPanel = articleAutoVisMainLogic.simulate_tab(gUIProp.width, gUIProp.height / 2);
        autoPanel.setSize(gUIProp.width, gUIProp.height / 2);
        autoPanel.setLocation(gUIProp.posx, gUIProp.height);

        autoPanel.setMinimumSize(new Dimension(0, 0));


        // Add in frame
        articleVisPanel.setTopComponent(autoPanel);

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


    public void loadDatePanel() {
        // Remove view if exists
        if (datePanel != null) {
            employeePanel.remove(datePanel);
        }

        //This is a sort of wrapper class which calls all
        //the other methods in GraphSims and GraphSimsAlgorithm
        //the actually creates the graph and animates it
        datePanel = new JPanel();
        String[] dateStrings = {
                "January 6, 2014", "January 7, 2014", "January 8, 2014", "January 9, 2014",
                "January 10, 2014", "January 13, 2014", "January 14, 2014", "January 15, 2015",
                "January 16, 2014", "January 17, 2014"
        };
        // Start Date Filter Picker
        JLabel startDateLabel = new JLabel("Filter Start Date");
        startDatePicker = new JComboBox<>(dateStrings);
        startDatePicker.addActionListener(this);

        JLabel endDateLabel = new JLabel("Filter End Date");
        endDatePicker = new JComboBox<>(dateStrings);
        endDatePicker.setSelectedIndex(dateStrings.length - 1);
        endDatePicker.addActionListener(this);

        datePanel.add(startDateLabel);
        datePanel.add(startDatePicker);
        datePanel.add(endDateLabel);
        datePanel.add(endDatePicker);

//        JSlider slider = new JSlider(JSlider.VERTICAL);
//        datePanel.add(slider);

        datePanel.setSize(200, frm_height - filter_button_height);

        datePanel.setLocation(frm_width - 200, ctrl_height);

        datePanel.setVisible(true);
        // Add in frame
        employeePanel.add(datePanel, BorderLayout.LINE_START);
    }


    public void loadFreqTab(java.util.List articleFilter) {

        // Remove view if exists
        if (freqPanel != null) {
            articleVisPanel.remove(freqPanel);
        }
        frequencyVisMainLogic.applyFilters(articleFilter);
        freqPanel = frequencyVisMainLogic.simulate_tab(gUIProp.width, gUIProp.height / 2);
        freqPanel.setSize(gUIProp.width, gUIProp.height / 2);
        freqPanel.setLocation(gUIProp.posx, gUIProp.height / 2);


        freqPanel.setMinimumSize(new Dimension(0, 0));
        freqPanel.repaint();

        // Add in frame
        articleVisPanel.setBottomComponent(freqPanel);
    }


    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }


    /**
     * Invoked when the mouse wheel is rotated.
     *
     * @param e the event to be processed
     * @see MouseWheelEvent
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
