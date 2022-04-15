package main;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import preprocessing.Preprocessor;
import vis.article.ArticleField;
import vis.article.ArticleFilter;

import javax.swing.*;
import java.awt.*;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FilterMainLogic {

    private JButton unselectAllButton, unselectAllPlaceButton, unselectAllAuthorButton, unselectAllPublicationButton;
    private JToggleButton includeEmptyDateFilter, includeEmptyAuthorFilter, includeEmptyPlaceFilter, includeEmptyPublicationFilter, includeEmptyKeywordFilter;

    private List<JCheckBox> keywordsCbList = new ArrayList<>();
    private List<JCheckBox> placeCbList = new ArrayList<>();
    private List<JCheckBox> publicationCbList = new ArrayList<>();
    private List<JCheckBox> authorCbList = new ArrayList<>();
    protected static Preprocessor PREPROCESSOR = new Preprocessor();
    private JDatePickerImpl startDatePicker, endDatePicker;
    private AtomicBoolean showDateFilter, showKeywordFilter, showAuthorFilter, showPublicationFilter, showPlaceFilter;
    private AtomicBoolean includeEmptyDate, includeEmptyKeyword, includeEmptyAuthor, includeEmptyPublication, includeEmptyPlace;
    List<ArticleFilter> filters = new ArrayList<>();
    JPanel filterMainPanel = new JPanel();
    boolean movingComponents = false;
    int visibleIndex = 4;

    public  FilterMainLogic(){
        includeEmptyDateFilter = new JToggleButton("Include emtpy values");
        includeEmptyDateFilter.setSelected(true);
        UtilDateModel startDateModel = new UtilDateModel();
        startDateModel.setDate(1982, 10, 2);
        startDateModel.setSelected(true);
        UtilDateModel endDateModel = new UtilDateModel();
        endDateModel.setDate(2014, 03, 26);
        endDateModel.setSelected(true);
        JDatePanelImpl startDatePanel = new JDatePanelImpl(startDateModel);
        JDatePanelImpl endDatePanel = new JDatePanelImpl(endDateModel);
        startDatePicker = new JDatePickerImpl(startDatePanel, new DateLabelFormatter());
        endDatePicker = new JDatePickerImpl(endDatePanel, new DateLabelFormatter());

        ///////////////////////////////////////////////
        unselectAllButton = new JButton("Unselect All Keywords");
        includeEmptyKeywordFilter = new JToggleButton("Include emtpy values");
        includeEmptyKeywordFilter.setSelected(true);
        //////////////////////////////////////////////////////////////
        unselectAllPlaceButton = new JButton("Unselect All Place");
        includeEmptyPlaceFilter = new JToggleButton("Include emtpy values");
        includeEmptyPlaceFilter.setSelected(true);
        ////////////////////////////////////////////////////////////////////////
        unselectAllAuthorButton = new JButton("Unselect All Author");
        includeEmptyAuthorFilter = new JToggleButton("Include emtpy values");
        includeEmptyAuthorFilter.setSelected(true);
        /////////////////////////////////////////////////////////////
        unselectAllPublicationButton = new JButton("Unselect All Publication");
        includeEmptyPublicationFilter = new JToggleButton("Include emtpy values");
        includeEmptyPublicationFilter.setSelected(true);
        /////////////////////////////////

        showDateFilter = new AtomicBoolean(true);
        showKeywordFilter = new AtomicBoolean(true);
        showPublicationFilter = new AtomicBoolean(true);
        showPlaceFilter = new AtomicBoolean(true);
        showAuthorFilter = new AtomicBoolean(true);

        includeEmptyDate = new AtomicBoolean(true);
        includeEmptyKeyword = new AtomicBoolean(true);
        includeEmptyPublication = new AtomicBoolean(true);
        includeEmptyPlace = new AtomicBoolean(true);
        includeEmptyAuthor = new AtomicBoolean(true);

        addActionListeners();




        filterMainPanel.setLayout(null);
        // Add children and compute prefSize.

        Dimension d = new Dimension();
        int h = 0;
        String[] child_names = {"date", "keyword", "place", "publication", "author"};
        for (int j= 0; j < child_names.length; j++) {
            ChildPanel child = new ChildPanel(child_names[j], j + 1, ml);
            filterMainPanel.add(child);
            d = child.getPreferredSize();
            child.setBounds(0, h, d.width, d.height);
            if (j < child_names.length - 1)
                h += ControlPanel.HEIGHT;
        }
        h += d.height;
        filterMainPanel.setPreferredSize(new Dimension(d.width, h));
        // Set z-order for children.
        setZOrder();
    }

    private void setZOrder() {
        Component[] c = filterMainPanel.getComponents();
        for (int j = 0; j < c.length - 1; j++) {
            filterMainPanel.setComponentZOrder(c[j], c.length - 1 - j);
        }
    }

    private void setChildVisible(int indexToOpen) {
        // If visibleIndex < indexToOpen, components at
        // [visibleIndex+1 down to indexToOpen] move up.
        // If visibleIndex > indexToOpen, components at
        // [indexToOpen+1 up to visibleIndex] move down.
        // Collect indices of components that will move
        // and determine the distance/direction to move.
        int[] indices = new int[0];
        int travelLimit = 0;
        if (visibleIndex < indexToOpen) {
            travelLimit = ControlPanel.HEIGHT -
                    filterMainPanel.getComponent(visibleIndex).getHeight();
            int n = indexToOpen - visibleIndex;
            indices = new int[n];
            for (int j = visibleIndex, k = 0; j < indexToOpen; j++, k++)
                indices[k] = j + 1;
        } else if (visibleIndex > indexToOpen) {
            travelLimit = filterMainPanel.getComponent(visibleIndex).getHeight() -
                    ControlPanel.HEIGHT;
            int n = visibleIndex - indexToOpen;
            indices = new int[n];
            for (int j = indexToOpen, k = 0; j < visibleIndex; j++, k++)
                indices[k] = j + 1;
        }
        movePanels(indices, travelLimit);
        visibleIndex = indexToOpen;
    }

    private void movePanels(final int[] indices, final int travel) {
        movingComponents = true;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Component[] c = filterMainPanel.getComponents();
                int limit = travel > 0 ? travel : 0;
                int count = travel > 0 ? 0 : travel;
                //TODO : change dy using function
                int dy = travel > 0 ? 8 : -8;

                while (count < limit) {
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        System.out.println("interrupted");
                        break;
                    }
                    for (int j = 0; j < indices.length; j++) {

                        // The z-order reversed the order returned
                        // by getComponents. Adjust the indices to
                        // get the correct components to relocate.
                        int index = c.length - 1 - indices[j];
                        Point p = c[index].getLocation();
                        p.y += dy;
                        c[index].setLocation(p.x, p.y);
                    }
                    filterMainPanel.repaint();
                    count = count + 8;
                }
                movingComponents = false;
            }
        });
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }

    private MouseListener ml = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            int index = ((ControlPanel)e.getSource()).id-1;
            if (!movingComponents)
                setChildVisible(index);
        }
    };


    public JPanel simulate_filter() {
        return filterMainPanel;
    }

    public List<ArticleFilter> getFilters() {
        return filters;
    }

    public void makeFilters() {
        filters.clear();
        List<String> selectedKeywords = new ArrayList<>();
        //////////////////////////////////////////////
        ArticleFilter keywordFilter = new ArticleFilter();
        for (JCheckBox checkBox : keywordsCbList) {
            if (checkBox.isSelected()) {
                selectedKeywords.add(checkBox.getText());
            }
        }
        keywordFilter.setField(ArticleField.KEYWORD);
        keywordFilter.setSelectedValues(selectedKeywords);
        keywordFilter.setKeepEmptyValue(includeEmptyKeyword.get());
        filters.add(keywordFilter);
        //////////////////////////////
        ArticleFilter dateFilter = new ArticleFilter();
        dateFilter.setField(ArticleField.DATE);
        Date selectedStartDate = (Date) startDatePicker.getModel().getValue();
        Date selectedEndDate = (Date) endDatePicker.getModel().getValue();
        dateFilter.setStartDate(selectedStartDate.getTime());
        dateFilter.setEndDate(selectedEndDate.getTime());
        dateFilter.setKeepEmptyValue(includeEmptyDate.get());
        filters.add(dateFilter);
        ////////////////////////////////////
        selectedKeywords = new ArrayList<>();
        ArticleFilter authorFilter = new ArticleFilter();
        for (JCheckBox checkBox : authorCbList) {
            if (checkBox.isSelected()) {
                selectedKeywords.add(checkBox.getText());
            }
        }
        authorFilter.setField(ArticleField.AUTHOR);
        authorFilter.setSelectedValues(selectedKeywords);
        authorFilter.setKeepEmptyValue(includeEmptyAuthor.get());
        filters.add(authorFilter);
        ////////////////////////////////////////////
        selectedKeywords = new ArrayList<>();
        ArticleFilter placeFilter = new ArticleFilter();
        for (JCheckBox checkBox : placeCbList) {
            if (checkBox.isSelected()) {
                selectedKeywords.add(checkBox.getText());
            }
        }
        placeFilter.setField(ArticleField.PLACE);
        placeFilter.setSelectedValues(selectedKeywords);
        placeFilter.setKeepEmptyValue(includeEmptyPlace.get());
        filters.add(placeFilter);
        ////////////////////////////////////////////////
        selectedKeywords = new ArrayList<>();
        ArticleFilter publicationFilter = new ArticleFilter();
        for (JCheckBox checkBox : publicationCbList) {
            if (checkBox.isSelected()) {
                selectedKeywords.add(checkBox.getText());
            }
        }
        publicationFilter.setField(ArticleField.PUBLICATION);
        publicationFilter.setSelectedValues(selectedKeywords);
        publicationFilter.setKeepEmptyValue(includeEmptyPublication.get());
        filters.add(publicationFilter);
    }

    private JPanel addOptions(List<String> options, List<JCheckBox> checkBoxListList) {
        JPanel panel = new JPanel();
        for (String option : options) {
            JCheckBox cb = new JCheckBox(option, true);
            cb.setPreferredSize(new Dimension(150, 20));
            checkBoxListList.add(cb);
            panel.add(cb);
        }
        return panel;
    }

    private void addOptions(List<String> options, JPanel panel, List<JCheckBox> checkBoxListList) {
        for (String option : options) {
            JCheckBox cb = new JCheckBox(option, true);
            cb.setPreferredSize(new Dimension(150, 20));
            checkBoxListList.add(cb);
            panel.add(cb);
        }
    }


    private void addActionListeners() {
        unselectAllButton.addActionListener(new UnselectAllActionListener(keywordsCbList));
        unselectAllPublicationButton.addActionListener(new UnselectAllActionListener(publicationCbList));
        unselectAllAuthorButton.addActionListener(new UnselectAllActionListener(authorCbList));
        unselectAllPlaceButton.addActionListener(new UnselectAllActionListener(placeCbList));
        includeEmptyDateFilter.addActionListener(new IncludeEmptyButtonActionListener(includeEmptyDate));
        includeEmptyPlaceFilter.addActionListener(new IncludeEmptyButtonActionListener(includeEmptyPlace));
        includeEmptyPublicationFilter.addActionListener(new IncludeEmptyButtonActionListener(includeEmptyPublication));
        includeEmptyAuthorFilter.addActionListener(new IncludeEmptyButtonActionListener(includeEmptyAuthor));
        includeEmptyKeywordFilter.addActionListener(new IncludeEmptyButtonActionListener(includeEmptyKeyword));
    }

    public void doResetButtonAction() {
        for (JCheckBox checkBox : keywordsCbList) {
            checkBox.setSelected(true);
        }
        for (JCheckBox checkBox : authorCbList) {
            checkBox.setSelected(true);
        }
        for (JCheckBox checkBox : placeCbList) {
            checkBox.setSelected(true);
        }
        for (JCheckBox checkBox : publicationCbList) {
            checkBox.setSelected(true);
        }
        startDatePicker.getModel().setDate(1982, 10, 2);
        endDatePicker.getModel().setDate(2014, 3, 26);
        includeEmptyDate.set(true);
        includeEmptyDateFilter.setSelected(true);
        includeEmptyKeyword.set(true);
        includeEmptyKeywordFilter.setSelected(true);
        includeEmptyAuthor.set(true);
        includeEmptyAuthorFilter.setSelected(true);
        includeEmptyPlace.set(true);
        includeEmptyPlaceFilter.setSelected(true);
        includeEmptyPublication.set(true);
        includeEmptyPublicationFilter.setSelected(true);
    }

    class HideOrShowActionListener implements ActionListener {
        private AtomicBoolean showFilter;
        private JPanel panel;

        public HideOrShowActionListener(AtomicBoolean showFilter, JPanel panel) {
            this.showFilter = showFilter;
            this.panel = panel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showFilter.set(hideOrShow(showFilter.get(), panel));
        }

        private boolean hideOrShow(boolean show, JPanel panel) {
            show = !show;
            panel.setVisible(show);
            return show;
        }
    }

    class IncludeEmptyButtonActionListener implements ActionListener {
        private AtomicBoolean include;

        public IncludeEmptyButtonActionListener(AtomicBoolean include) {
            this.include = include;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            include.set(!include.get());
        }

    }

    class UnselectAllActionListener implements ActionListener {
        private List<JCheckBox> optionsList;

        public UnselectAllActionListener(List optionsList) {
            this.optionsList = optionsList;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for (JCheckBox checkBox : optionsList) {
                checkBox.setSelected(false);
            }
        }
    }

    class ChildPanel extends JPanel {
        public ChildPanel(String name, int id, MouseListener ml) {
            setLayout(new BorderLayout());
            add(new ControlPanel(name, id, ml), "First");
            JScrollPane scrollPane = new JScrollPane(getContent(name));
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.setVisible(true);
            scrollPane.repaint();
            add(scrollPane);
        }

        private JPanel getContent(String name) {
            JPanel panel = new JPanel(new GridLayout());
            panel.setLayout(new GridLayout(10, 1));
            switch (name) {
                case "date":
                    panel.add(startDatePicker);
                    panel.add(endDatePicker);
                    break;
                case "keyword":
                    addOptions(Arrays.asList(PREPROCESSOR.getKeywordsArr()), panel, keywordsCbList);
                    panel.add(includeEmptyKeywordFilter);
                    panel.add(unselectAllButton);
                    break;
                case "author":
                    addOptions(PREPROCESSOR.getAuthors(), panel, authorCbList);
                    panel.add(includeEmptyAuthorFilter);
                    panel.add(unselectAllAuthorButton);
                    break;
                case "place":
                    addOptions(PREPROCESSOR.getPlaces(), panel, placeCbList);
                    panel.add(includeEmptyPlaceFilter);
                    panel.add(unselectAllPlaceButton);
                    break;
                case "publication":
                    addOptions(PREPROCESSOR.getPublications(), panel, publicationCbList);
                    panel.add(includeEmptyPublicationFilter);
                    panel.add(unselectAllPublicationButton);
                    break;
            }
            return panel;
        }

        public Dimension getPreferredSize() {
            return new Dimension(256, 200);
        }
    }

    class ControlPanel extends JPanel {
        int id;
        String name;
        JLabel titleLabel;
        Color c1 = new Color(200,180,180);
        Color c2 = new Color(200,220,220);
        Color fontFg = Color.blue;
        Color rolloverFg = Color.red;
        public final static int HEIGHT = 30;

        public ControlPanel(String name, int id, MouseListener ml) {
            this.id = id;
            this.name = name;
            setLayout(new BorderLayout());
            add(titleLabel = new JLabel(name + " Filter", JLabel.CENTER));
            titleLabel.setForeground(fontFg);
            Dimension d = getPreferredSize();
            d.height = HEIGHT;
            setPreferredSize(d);
            addMouseListener(ml);
            addMouseListener(listener);
        }

        protected void paintComponent(Graphics g) {
            int w = getWidth();
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(w/2, 0, c1, w/2, HEIGHT/2, c2));
            g2.fillRect(0,0,w,HEIGHT);
        }

        private MouseListener listener = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                titleLabel.setForeground(rolloverFg);
            }

            public void mouseExited(MouseEvent e) {
                titleLabel.setForeground(fontFg);
            }
        };
    }
}
