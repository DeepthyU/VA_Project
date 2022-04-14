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


    private boolean filterFlag;
    private JButton filterButton;
    private JButton hideOrShowDateFilter, hideOrShowAuthorFilter, hideOrShowPlaceFilter, hideOrShowPublicationFilter, hideOrShowKeywordFilter, unselectAllButton, unselectAllPlaceButton, unselectAllAuthorButton, unselectAllPublicationButton;
    private JToggleButton includeEmptyDateFilter, includeEmptyAuthorFilter, includeEmptyPlaceFilter, includeEmptyPublicationFilter, includeEmptyKeywordFilter;

    private JButton resetAllButton;
    private List<JCheckBox> keywordsCbList = new ArrayList<>();
    private List<JCheckBox> placeCbList = new ArrayList<>();
    private List<JCheckBox> publicationCbList = new ArrayList<>();
    private List<JCheckBox> authorCbList = new ArrayList<>();
    private JPanel filterPanel, authorFilterPanel, keywordFilterPanel, dateFilterPanel, publicationFilterPanel, placeFilterPanel;
    protected static final Preprocessor PREPROCESSOR = new Preprocessor();
    private JDatePickerImpl startDatePicker, endDatePicker;
    private AtomicBoolean showDateFilter, showKeywordFilter, showAuthorFilter, showPublicationFilter, showPlaceFilter;
    private AtomicBoolean includeEmptyDate, includeEmptyKeyword, includeEmptyAuthor, includeEmptyPublication, includeEmptyPlace;
    List<ArticleFilter> filters = new ArrayList<>();

    public FilterMainLogic() {
        filterPanel = new JPanel();

        filterPanel.setLayout(new FlowLayout());
        filterButton = new JButton("Filter");
        dateFilterPanel = new JPanel();
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
        hideOrShowDateFilter = new JButton("date filter");
        hideOrShowDateFilter.setBackground(Color.RED);
        dateFilterPanel.add(startDatePicker);
        dateFilterPanel.add(endDatePicker);
        dateFilterPanel.setLayout(new GridLayout(2, 1));

        ///////////////////////////////////////////////
        keywordFilterPanel = addOptions(Arrays.asList(PREPROCESSOR.getKeywordsArr()), keywordsCbList);
        unselectAllButton = new JButton("Unselect All Keywords");
        keywordFilterPanel.add(unselectAllButton);
        hideOrShowKeywordFilter = new JButton("keyword filter");
        hideOrShowKeywordFilter.setBackground(Color.RED);
        includeEmptyKeywordFilter = new JToggleButton("Include emtpy values");
        includeEmptyKeywordFilter.setSelected(true);
        keywordFilterPanel.setLayout(new GridLayout(20, 1));
        //////////////////////////////////////////////////////////////
        placeFilterPanel = addOptions(PREPROCESSOR.getPlaces(), placeCbList);
        unselectAllPlaceButton = new JButton("Unselect All Place");
        placeFilterPanel.add(unselectAllPlaceButton);
        placeFilterPanel.setLayout(new GridLayout(10, 1));
        hideOrShowPlaceFilter = new JButton("place filter");
        hideOrShowPlaceFilter.setBackground(Color.RED);
        includeEmptyPlaceFilter = new JToggleButton("Include emtpy values");
        includeEmptyPlaceFilter.setSelected(true);
        ////////////////////////////////////////////////////////////////////////
        authorFilterPanel = addOptions(PREPROCESSOR.getAuthors(), authorCbList);
        unselectAllAuthorButton = new JButton("Unselect All Author");
        authorFilterPanel.add(unselectAllAuthorButton);
        authorFilterPanel.setLayout(new GridLayout(10, 1));
        hideOrShowAuthorFilter = new JButton("author filter");
        hideOrShowAuthorFilter.setBackground(Color.RED);
        includeEmptyAuthorFilter = new JToggleButton("Include emtpy values");
        includeEmptyAuthorFilter.setSelected(true);
        /////////////////////////////////////////////////////////////
        publicationFilterPanel = addOptions(PREPROCESSOR.getPublications(), publicationCbList);
        unselectAllPublicationButton = new JButton("Unselect All Publication");
        publicationFilterPanel.setLayout(new GridLayout(10, 1));
        publicationFilterPanel.add(unselectAllPublicationButton);
        hideOrShowPublicationFilter = new JButton("publication filter");
        hideOrShowPublicationFilter.setBackground(Color.RED);
        includeEmptyPublicationFilter = new JToggleButton("Include emtpy values");
        includeEmptyPublicationFilter.setSelected(true);
        /////////////////////////////////

        filterPanel.add(hideOrShowDateFilter);
        filterPanel.add(includeEmptyDateFilter);
        filterPanel.add(dateFilterPanel);
        filterPanel.add(hideOrShowKeywordFilter);
        filterPanel.add(includeEmptyKeywordFilter);
        filterPanel.add(keywordFilterPanel);
        filterPanel.add(hideOrShowPlaceFilter);
        filterPanel.add(includeEmptyPlaceFilter);
        filterPanel.add(placeFilterPanel);
        filterPanel.add(hideOrShowAuthorFilter);
        filterPanel.add(includeEmptyAuthorFilter);
        filterPanel.add(authorFilterPanel);
        filterPanel.add(hideOrShowPublicationFilter);
        filterPanel.add(includeEmptyPublicationFilter);
        filterPanel.add(publicationFilterPanel);

        resetAllButton = new JButton("Reset All");

        filterPanel.add(resetAllButton);

        filterPanel.add(filterButton);

        filterPanel.setPreferredSize(new Dimension(400, 700));
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
    }


    public JPanel simulate_filter() {
        JScrollPane scrollPane = new JScrollPane(filterPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVisible(true);
        scrollPane.repaint();
        return new AccordianPanel();
    }

    public List<ArticleFilter> getFilters() {
        return filters;
    }

    public void makeFilters() {
        filters.clear();
        filterFlag = !filterFlag;
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
        hideOrShowDateFilter.addActionListener(new HideOrShowActionListener(showDateFilter, dateFilterPanel));
        hideOrShowKeywordFilter.addActionListener(new HideOrShowActionListener(showKeywordFilter, keywordFilterPanel));
        hideOrShowAuthorFilter.addActionListener(new HideOrShowActionListener(showAuthorFilter, authorFilterPanel));
        hideOrShowPublicationFilter.addActionListener(new HideOrShowActionListener(showPublicationFilter, publicationFilterPanel));
        hideOrShowPlaceFilter.addActionListener(new HideOrShowActionListener(showPlaceFilter, placeFilterPanel));
        unselectAllButton.addActionListener(new UnselectAllActionListener(keywordsCbList));
        unselectAllPublicationButton.addActionListener(new UnselectAllActionListener(publicationCbList));
        unselectAllAuthorButton.addActionListener(new UnselectAllActionListener(authorCbList));
        unselectAllPlaceButton.addActionListener(new UnselectAllActionListener(placeCbList));
        includeEmptyDateFilter.addActionListener(new IncludeEmptyButtonActionListener(includeEmptyDate));
        includeEmptyPlaceFilter.addActionListener(new IncludeEmptyButtonActionListener(includeEmptyPlace));
        includeEmptyPublicationFilter.addActionListener(new IncludeEmptyButtonActionListener(includeEmptyPublication));
        includeEmptyAuthorFilter.addActionListener(new IncludeEmptyButtonActionListener(includeEmptyAuthor));
        includeEmptyKeywordFilter.addActionListener(new IncludeEmptyButtonActionListener(includeEmptyKeyword));
        resetAllButton.addActionListener(new AbstractAction("reset") {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                showDateFilter.set(true);
                dateFilterPanel.setVisible(true);
                showAuthorFilter.set(true);
                authorFilterPanel.setVisible(true);
                showPlaceFilter.set(true);
                placeFilterPanel.setVisible(true);
                showPublicationFilter.set(true);
                publicationFilterPanel.setVisible(true);
                showKeywordFilter.set(true);
                keywordFilterPanel.setVisible(true);
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
        });
        filterButton.addActionListener(new AbstractAction("filter") {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeFilters();
                System.out.println("Filters count ::" + filters.size());
            }


        });
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


    class AccordianPanel extends JPanel {
        boolean movingComponents = false;
        int visibleIndex = 4;

        public AccordianPanel() {
            setLayout(null);
            // Add children and compute prefSize.

            Dimension d = new Dimension();
            int h = 0;
            String[] child_names = {"date", "keyword", "place", "publication", "author"};
            for (int j= 0; j < child_names.length; j++) {
                ChildPanel child = new ChildPanel(child_names[j], j + 1, ml);
                add(child);
                d = child.getPreferredSize();
                child.setBounds(0, h, d.width, d.height);
                if (j < child_names.length - 1)
                    h += ControlPanel.HEIGHT;
            }
            h += d.height;
            setPreferredSize(new Dimension(d.width, h));
            // Set z-order for children.
            setZOrder();
        }

        private void setZOrder() {
            Component[] c = getComponents();
            for (int j = 0; j < c.length - 1; j++) {
                setComponentZOrder(c[j], c.length - 1 - j);
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
                        getComponent(visibleIndex).getHeight();
                int n = indexToOpen - visibleIndex;
                indices = new int[n];
                for (int j = visibleIndex, k = 0; j < indexToOpen; j++, k++)
                    indices[k] = j + 1;
            } else if (visibleIndex > indexToOpen) {
                travelLimit = getComponent(visibleIndex).getHeight() -
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
                    Component[] c = getComponents();
                    int limit = travel > 0 ? travel : 0;
                    int count = travel > 0 ? 0 : travel;
                    int dy = travel > 0 ? 8 : -8;
                    System.out.println("-----travel=" + travel);
                    System.out.println("--count---=" + count);
                    System.out.println("-limit-" + limit);

                    while (count < limit) {
                        try {
                            Thread.sleep(25);
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
                            System.out.println("x=" + p.x + "y=" + p.y);
                        }
                        repaint();
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
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(2, 2, 2, 2);
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = gbc.NORTHWEST;
            switch (name) {
                case "date":
                    panel.add(startDatePicker);
                    panel.add(endDatePicker);
                    panel.setLayout(new GridLayout(10, 1));
                    break;
                case "keyword":
                    addOptions(Arrays.asList(PREPROCESSOR.getKeywordsArr()), panel, keywordsCbList);
                    panel.add(includeEmptyKeywordFilter);
                    panel.add(unselectAllButton);
                    panel.setLayout(new GridLayout(10, 1));
                    break;
                case "author":
                    addOptions(PREPROCESSOR.getAuthors(), panel, authorCbList);
                    panel.add(includeEmptyAuthorFilter);
                    panel.add(unselectAllAuthorButton);
                    panel.setLayout(new GridLayout(10, 1));
                    break;
                case "place":
                    addOptions(PREPROCESSOR.getPlaces(), panel, placeCbList);
                    panel.add(includeEmptyPlaceFilter);
                    panel.add(unselectAllPlaceButton);
                    panel.setLayout(new GridLayout(10, 1));
                    break;
                case "publication":
                    addOptions(PREPROCESSOR.getPublications(), panel, publicationCbList);
                    panel.add(includeEmptyPublicationFilter);
                    panel.add(unselectAllPublicationButton);
                    panel.setLayout(new GridLayout(10, 1));
                    break;

            }

            return panel;
        }

        public Dimension getPreferredSize() {
            return new Dimension(256, 200);
        }
    }

}
