import main.DateLabelFormatter;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.helpers.DateTimeDateFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import preprocessing.Article;
import preprocessing.Preprocessor;
import vis.article.ArticleField;
import vis.article.ArticleFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class LineChart extends JFrame {

    private final JTextField t1 = new JTextField("Start Date");
    private final JTextField t2 = new JTextField("End Date");
    private boolean filterFlag;
    private JButton filterButton;
    private JButton hideOrShowDateFilter, hideOrShowAuthorFilter, hideOrShowPlaceFilter, hideOrShowPublicationFilter, hideOrShowKeywordFilter, unselectAllButton, unselectAllPlaceButton, unselectAllAuthorButton, unselectAllPublicationButton;
    private JButton resetAllButton;
    private List<JCheckBox> keywordsCbList = new ArrayList<>();
    private List<JCheckBox> placeCbList = new ArrayList<>();
    private List<JCheckBox> publicationCbList = new ArrayList<>();
    private List<JCheckBox> authorCbList = new ArrayList<>();
    private JFreeChart chart;
    private ChartPanel panel;
    private JPanel filterPanel, authorFilterPanel, keywordFilterPanel, dateFilterPanel, publicationFilterPanel, placeFilterPanel;
    private static int[][] keywordCount;
    protected static final Preprocessor PREPROCESSOR = new Preprocessor();
    private JDatePickerImpl startDatePicker, endDatePicker;
    private XYSeriesCollection dataset;
    private boolean showDateFilter = true, showKeywordFilter = true, showAuthorFilter = true, showPublicationFilter = true, showPlaceFilter = true;

    public LineChart(String title, List<ArticleFilter> filters) {
        super(title);
        setLayout(new GridLayout(1, 2));
        panel = makeLineChart(filters);
        add(panel, BorderLayout.EAST);
        filterPanel = makeFilterPanel();
        add(filterPanel, BorderLayout.WEST);
    }

    private JPanel makeFilterPanel() {
        JPanel filterPanel = new JPanel();
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
        dateFilterPanel.setLayout(new GridLayout(2, 2));

        ///////////////////////////////////////////////

        int y = 100;
        keywordFilterPanel = addOptions(y, Arrays.asList(PREPROCESSOR.getKeywordsArr()), keywordsCbList);
        unselectAllButton = new JButton("UnselectAll");
        keywordFilterPanel.add(unselectAllButton);
        hideOrShowKeywordFilter = new JButton("keyword filter");
        hideOrShowKeywordFilter.setBackground(Color.RED);
        panel.setLayout(new GridLayout(20, 5));
        //////////////////////////////////////////////////////////////
        placeFilterPanel = addOptions(y, PREPROCESSOR.getPlaces(), placeCbList);
        unselectAllPlaceButton = new JButton("Unselect All Place");
        placeFilterPanel.add(unselectAllPlaceButton);
        placeFilterPanel.setLayout(new GridLayout(5, 5));
        hideOrShowPlaceFilter = new JButton("place filter");
        hideOrShowPlaceFilter.setBackground(Color.RED);

////////////////////////////////////////////////////////////////////////
        authorFilterPanel = addOptions(100, PREPROCESSOR.getAuthors(), authorCbList);
        unselectAllAuthorButton = new JButton("Unselect All Author");
        authorFilterPanel.add(unselectAllAuthorButton);
        authorFilterPanel.setLayout(new GridLayout(5, 5));
        hideOrShowAuthorFilter = new JButton("author filter");
        hideOrShowAuthorFilter.setBackground(Color.RED);
        /////////////////////////////////////////////////////////////
        publicationFilterPanel = addOptions(100, PREPROCESSOR.getPublications(), publicationCbList);
        unselectAllPublicationButton = new JButton("Unselect All Publication");
        publicationFilterPanel.setLayout(new GridLayout(5, 5));
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
        return filterPanel;
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

    private ChartPanel makeLineChart(List<ArticleFilter> filters) {
        // Create dataset
        dataset = createDataset(filters);
        // Create chart
        DateAxis dateAxis = new DateAxis("Date");
        dateAxis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));
        dateAxis.setVerticalTickLabels(true);
        LogAxis logAxis = new LogAxis("Frequency");
        logAxis.setBase(2);
        logAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chart = ChartFactory.createXYLineChart("Keyword Frequency", "Date", "Frequency"
                , dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.getXYPlot().setDomainAxis(dateAxis);
        chart.getXYPlot().setRangeAxis(logAxis);
        ChartPanel panel = new ChartPanel(chart, 300, 300, 100, 100, 2000, 2000, true, false, true, true, true, true);
        panel.setPreferredSize(new Dimension(500, 300));
        return panel;
    }

    public XYSeriesCollection createDataset(List<ArticleFilter> filters) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        updateDataset(dataset, filters);
        return dataset;
    }

    public void updateDataset(XYSeriesCollection dataset, List<ArticleFilter> articleFilter) {
        long startDate = computeKeywordFrequency(articleFilter);
        dataset.removeAllSeries();
        String[] keywords = PREPROCESSOR.getKeywordsArr();
        for (int i = 0; i < keywordCount.length; i++) {
            XYSeries keywordSeries = new XYSeries(keywords[i]);
            for (int j = 0; j < keywordCount[i].length; j++) {
                if (keywordCount[i][j] >= 0) {
                    keywordSeries.add(((long) j * (1000 * 60 * 60 * 24) + startDate), keywordCount[i][j]);
                }
            }
            if (!keywordSeries.isEmpty()) {
                dataset.addSeries(keywordSeries);
            }
        }
    }

    private boolean hideOrShow(boolean show, JPanel panel) {
        show = !show;
        panel.setVisible(show);
        return show;
    }

    public static void main(String[] args) {
        List<ArticleFilter> filters = new ArrayList<>();
        SwingUtilities.invokeLater(() -> {
            LineChart example = new LineChart("Line Chart Example", filters);
            example.hideOrShowDateFilter.addActionListener(new AbstractAction("hideOrShow") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    example.showDateFilter = example.hideOrShow(example.showDateFilter, example.dateFilterPanel);
                }
            });
            example.hideOrShowKeywordFilter.addActionListener(new AbstractAction("hideOrShow") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    example.showKeywordFilter = example.hideOrShow(example.showKeywordFilter, example.keywordFilterPanel);
                }
            });
            example.hideOrShowAuthorFilter.addActionListener(new AbstractAction("hideOrShow") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    example.showAuthorFilter = example.hideOrShow(example.showAuthorFilter, example.authorFilterPanel);
                }
            });
            example.hideOrShowPublicationFilter.addActionListener(new AbstractAction("hideOrShow") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    example.showPublicationFilter = example.hideOrShow(example.showPublicationFilter, example.publicationFilterPanel);
                }
            });
            example.hideOrShowPlaceFilter.addActionListener(new AbstractAction("hideOrShow") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    example.showPlaceFilter = example.hideOrShow(example.showPlaceFilter, example.placeFilterPanel);
                }
            });
            example.hideOrShowDateFilter.addActionListener(new AbstractAction("hideOrShow") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    example.hideOrShow(example.showDateFilter, example.dateFilterPanel);
                }
            });

            example.unselectAllButton.addActionListener(new AbstractAction("unselect") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (JCheckBox checkBox : example.keywordsCbList) {
                        checkBox.setSelected(false);
                    }
                }
            });

            example.unselectAllPublicationButton.addActionListener(new AbstractAction("unselect") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (JCheckBox checkBox : example.publicationCbList) {
                        checkBox.setSelected(false);
                    }
                }
            });

            example.unselectAllAuthorButton.addActionListener(new AbstractAction("unselect") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (JCheckBox checkBox : example.authorCbList) {
                        checkBox.setSelected(false);
                    }
                }
            });

            example.unselectAllPlaceButton.addActionListener(new AbstractAction("unselect") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (JCheckBox checkBox : example.placeCbList) {
                        checkBox.setSelected(false);
                    }
                }
            });

            example.resetAllButton.addActionListener(new AbstractAction("reset") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (JCheckBox checkBox : example.keywordsCbList) {
                        checkBox.setSelected(true);
                    }
                    for (JCheckBox checkBox : example.authorCbList) {
                        checkBox.setSelected(true);
                    }
                    for (JCheckBox checkBox : example.placeCbList) {
                        checkBox.setSelected(true);
                    }
                    for (JCheckBox checkBox : example.publicationCbList) {
                        checkBox.setSelected(true);
                    }
                    example.startDatePicker.getModel().setDate(1982, 10, 2);
                    example.endDatePicker.getModel().setDate(2014, 3, 26);
                    example.showDateFilter = true;
                    example.dateFilterPanel.setVisible(true);
                    example.showAuthorFilter = true;
                    example.authorFilterPanel.setVisible(true);
                    example.showPlaceFilter = true;
                    example.placeFilterPanel.setVisible(true);
                    example.showPublicationFilter = true;
                    example.publicationFilterPanel.setVisible(true);
                    example.showKeywordFilter = true;
                    example.keywordFilterPanel.setVisible(true);
                }
            });
            example.filterButton.addActionListener(new AbstractAction("filter") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filters.clear();
                    example.filterFlag = !example.filterFlag;
                    List<String> selectedKeywords = new ArrayList<>();
                    //////////////////////////////////////////////
                    ArticleFilter keywordFilter = new ArticleFilter();
                    for (JCheckBox checkBox : example.keywordsCbList) {
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
                    Date selectedStartDate = (Date) example.startDatePicker.getModel().getValue();
                    Date selectedEndDate = (Date) example.endDatePicker.getModel().getValue();
                    dateFilter.setStartDate(selectedStartDate.getTime());
                    dateFilter.setEndDate(selectedEndDate.getTime());
                    filters.add(dateFilter);
                    ////////////////////////////////////
                    selectedKeywords = new ArrayList<>();
                    ArticleFilter authorFilter = new ArticleFilter();
                    for (JCheckBox checkBox : example.authorCbList) {
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
                    for (JCheckBox checkBox : example.placeCbList) {
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
                    for (JCheckBox checkBox : example.publicationCbList) {
                        if (checkBox.isSelected()) {
                            selectedKeywords.add(checkBox.getText());
                        }
                    }
                    publicationFilter.setField(ArticleField.PUBLICATION);
                    publicationFilter.setSelectedValues(selectedKeywords);
                    filters.add(publicationFilter);

                    example.updateDataset(example.dataset, filters);
                    example.panel.getChart().fireChartChanged();

                    example.panel.repaint();
                }
            });
            example.pack();
            example.setSize(700, 700);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }

    private static long getTimestamp(JTextField textField) {
        DateFormat df = new DateTimeDateFormat();
        Date date = null;
        try {
            date = df.parse(textField.getText());
        } catch (ParseException parseException) {
            System.out.println("ERROR: Error parsing date : " + parseException.getCause());
        }
        long timestamp = date.getTime();
        return timestamp;
    }


    private static void clearKeyCount() {
        for (int[] ints : keywordCount) {
            Arrays.fill(ints, -1);
        }
    }

    private static long computeKeywordFrequency(List<ArticleFilter> filters) {
        String[] keywords = PREPROCESSOR.getKeywordsArr();
        List<Article> articleList = PREPROCESSOR.getArticleList();
        articleList.sort(Comparator.comparing(Article::getDate));
        long startDate = articleList.get(0).getDate().getTime();
        long endDate = articleList.get(articleList.size() - 1).getDate().getTime();
        int totalDays = (int) ((endDate - startDate) / (1000 * 60 * 60 * 24));
        System.out.println("START DATE:" + articleList.get(0).getDate());
        System.out.println("END DATE:" + articleList.get(articleList.size() - 1).getDate());
        System.out.println("TOTAL DAYS:" + totalDays);
        System.out.println("number of keywords:" + keywords.length);
        int maxKeyCount = 0;
        String maxKeyword = "";
        int remainingArticlesCount = 0;
        List<String> currKeywords = null;
        if (filters != null) {
            for (ArticleFilter filter : filters) {
                if (filter.getField().equals(ArticleField.KEYWORD)) {
                    currKeywords = filter.getSelectedValues();
                }
            }
        }

        keywordCount = new int[keywords.length][totalDays + 1];
        clearKeyCount();
        for (int i = 0; i < keywords.length; i++) {
            for (Article article : articleList) {
                if (!isRemoveItem(filters, article)) {
                    remainingArticlesCount++;
                    if (currKeywords != null) {
                        if (!currKeywords.contains(keywords[i].toLowerCase(Locale.ROOT))) {
                            continue;
                        }
                    }
                    if (article.getKeywordsList().contains(keywords[i].toLowerCase(Locale.ROOT))) {
                        int dateIdx = (int) ((article.getDate().getTime() - startDate) / (1000 * 60 * 60 * 24));
                        if (keywordCount[i][dateIdx] == -1) {
                            keywordCount[i][dateIdx] = 0;
                        }
                        keywordCount[i][dateIdx]++;
                        if (maxKeyCount < keywordCount[i][dateIdx]) {
                            maxKeyCount = keywordCount[i][dateIdx];
                            maxKeyword = keywords[i] + " " + i;
                        }
                    }
                }
            }

        }
        System.out.println("Max key count = " + maxKeyCount);
        System.out.println("Max keyword = " + maxKeyword);
        System.out.println("Remaining articles = " + remainingArticlesCount);
        return startDate;
    }

    public static boolean isRemoveItem(List<ArticleFilter> filters, Article article) {
        boolean removeItem = false;
        if (filters == null) {
            return false;
        }
        for (ArticleFilter filter : filters) {
            if (ArticleField.DATE.equals(filter.getField())) {
                long start = filter.getStartDate();
                long end = filter.getEndDate();
                if (article.getDate().getTime() < start || article.getDate().getTime() > end) {
                    removeItem = true;
                    break;
                }
            } else if (ArticleField.AUTHOR.equals(filter.getField())) {
                String author = article.getAuthor();
                if (null != author) {
                    author = author.toLowerCase(Locale.ROOT);
                }
                removeItem |= isRemoveItemByFieldVal(filter, author);
            } else if (ArticleField.PUBLICATION.equals(filter.getField())) {
                String publication = article.getPublication();
                if (null != publication) {
                    publication = publication.toLowerCase(Locale.ROOT);
                }
                removeItem |= isRemoveItemByFieldVal(filter, publication);
            } else if (ArticleField.PLACE.equals(filter.getField())) {
                String place = article.getPlace().toLowerCase(Locale.ROOT);
                place = place.toLowerCase(Locale.ROOT);
                removeItem |= isRemoveItemByFieldVal(filter, place);
            } else if (ArticleField.KEYWORD.equals(filter.getField())) {
                String keywords = "key";
                List<String> valList = article.getKeywordsList();
                removeItem |= isRemoveItem(filter, keywords,
                        CollectionUtils.isNotEmpty(CollectionUtils.intersection(filter.getSelectedValues(), valList)),
                        CollectionUtils.isNotEmpty(CollectionUtils.intersection(filter.getUnselectedValues(), valList)));
            }
        }
        return removeItem;
    }

    private static boolean isRemoveItem(ArticleFilter filter, String keywords, boolean contains, boolean contains2) {
        if (StringUtils.isBlank(keywords)) {
            return !filter.isKeepEmptyValue();
        }
        if (!contains) {
            return true;
        }
        return contains2;
    }

    private static boolean isRemoveItemByFieldVal(ArticleFilter filter, String fieldValue) {
        return isRemoveItem(filter, fieldValue, filter.getSelectedValues().contains(fieldValue),
                filter.getUnselectedValues().contains(fieldValue));
    }

}