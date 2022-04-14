package main;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import preprocessing.Preprocessor;
import vis.article.ArticleField;
import vis.article.ArticleFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FilterMainLogic {


    private boolean filterFlag;
    private JButton filterButton;
    private JButton hideOrShowDateFilter, hideOrShowAuthorFilter, hideOrShowPlaceFilter, hideOrShowPublicationFilter, hideOrShowKeywordFilter, unselectAllButton, unselectAllPlaceButton, unselectAllAuthorButton, unselectAllPublicationButton;
    private JButton resetAllButton;
    private java.util.List<JCheckBox> keywordsCbList = new ArrayList<>();
    private java.util.List<JCheckBox> placeCbList = new ArrayList<>();
    private java.util.List<JCheckBox> publicationCbList = new ArrayList<>();
    private List<JCheckBox> authorCbList = new ArrayList<>();
    private JPanel filterPanel, authorFilterPanel, keywordFilterPanel, dateFilterPanel, publicationFilterPanel, placeFilterPanel;
    protected static final Preprocessor PREPROCESSOR = new Preprocessor();
    private JDatePickerImpl startDatePicker, endDatePicker;
    private AtomicBoolean showDateFilter, showKeywordFilter, showAuthorFilter, showPublicationFilter, showPlaceFilter;
    List<ArticleFilter> filters = new ArrayList<>();

    public FilterMainLogic() {
        filterPanel = new JPanel();

        filterPanel.setLayout(new FlowLayout());
        filterButton = new JButton("Filter");
        dateFilterPanel = new JPanel();
        hideOrShowDateFilter = new JButton("date filter");
        hideOrShowDateFilter.setBackground(Color.RED);
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
        dateFilterPanel.add(startDatePicker);
        dateFilterPanel.add(endDatePicker);
        dateFilterPanel.setLayout(new GridLayout(2, 1));

        ///////////////////////////////////////////////
        int y = 100;
        keywordFilterPanel = addOptions(y, Arrays.asList(PREPROCESSOR.getKeywordsArr()), keywordsCbList);
        unselectAllButton = new JButton("UnselectAll");
        keywordFilterPanel.add(unselectAllButton);
        hideOrShowKeywordFilter = new JButton("keyword filter");
        hideOrShowKeywordFilter.setBackground(Color.RED);
        //////////////////////////////////////////////////////////////
        keywordFilterPanel.setLayout(new GridLayout(20, 1));
        placeFilterPanel = addOptions(y, PREPROCESSOR.getPlaces(), placeCbList);
        unselectAllPlaceButton = new JButton("Unselect All Place");
        placeFilterPanel.add(unselectAllPlaceButton);
        placeFilterPanel.setLayout(new GridLayout(10, 1));
        hideOrShowPlaceFilter = new JButton("place filter");
        hideOrShowPlaceFilter.setBackground(Color.RED);

        ////////////////////////////////////////////////////////////////////////
        authorFilterPanel = addOptions(100, PREPROCESSOR.getAuthors(), authorCbList);
        unselectAllAuthorButton = new JButton("Unselect All Author");
        authorFilterPanel.add(unselectAllAuthorButton);
        authorFilterPanel.setLayout(new GridLayout(10, 1));
        hideOrShowAuthorFilter = new JButton("author filter");
        hideOrShowAuthorFilter.setBackground(Color.RED);
        /////////////////////////////////////////////////////////////
        publicationFilterPanel = addOptions(100, PREPROCESSOR.getPublications(), publicationCbList);
        unselectAllPublicationButton = new JButton("Unselect All Publication");
        publicationFilterPanel.setLayout(new GridLayout(10, 1));
        publicationFilterPanel.add(unselectAllPublicationButton);
        hideOrShowPublicationFilter = new JButton("publication filter");
        hideOrShowPublicationFilter.setBackground(Color.RED);
        /////////////////////////////////

        filterPanel.add(hideOrShowDateFilter);
        filterPanel.add(dateFilterPanel);
        filterPanel.add(hideOrShowKeywordFilter);
        filterPanel.add(keywordFilterPanel);
        filterPanel.add(hideOrShowPlaceFilter);
        filterPanel.add(placeFilterPanel);
        filterPanel.add(hideOrShowAuthorFilter);
        filterPanel.add(authorFilterPanel);
        filterPanel.add(hideOrShowPublicationFilter);
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
        addActionListeners();
    }


    public JScrollPane simulate_filter() {
        JScrollPane scrollPane = new JScrollPane(filterPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setVisible(true);
        return scrollPane;
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
        filters.add(keywordFilter);
        //////////////////////////////
        ArticleFilter dateFilter = new ArticleFilter();
        dateFilter.setField(ArticleField.DATE);
        Date selectedStartDate = (Date) startDatePicker.getModel().getValue();
        Date selectedEndDate = (Date) endDatePicker.getModel().getValue();
        dateFilter.setStartDate(selectedStartDate.getTime());
        dateFilter.setEndDate(selectedEndDate.getTime());
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
        filters.add(publicationFilter);
    }

    private JPanel addOptions(int y, List<String> options, List<JCheckBox> checkBoxListList) {
        JPanel panel = new JPanel();
        for (String option : options) {
            JCheckBox cb = new JCheckBox(option, true);
            cb.setBounds(100, y += 20, 150, 20);
            checkBoxListList.add(cb);
            panel.add(cb);
        }
        return panel;
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
}
