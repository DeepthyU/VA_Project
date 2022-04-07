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
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.*;
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

    private static final long serialVersionUID = 1L;
    private JTextField t1 = new JTextField("Start Date");
    private JTextField t2 = new JTextField("End Date");
    private boolean filterFlag;
    private JButton button;
    private JButton hideOrShowDateFitler;
    private JButton hideOrShowAuthorFilter;
    private JButton hideOrShowPlaceFilter;
    private JButton hideOrShowPublicationFilter;
    private JButton hideOrShowKeywordFilter;
    private JButton unselectAllButton;
    private JButton unselectAllPlaceButton;
    private JButton unselectAllAuthorButton;
    private JButton unselectAllPublicationButton;
    private JButton resetAllButton;
    private List<JCheckBox> cbList = new ArrayList<>();
    private List<JCheckBox> placeCbList = new ArrayList<>();
    private List<JCheckBox> publicationCbList = new ArrayList<>();
    private List<JCheckBox> authorCbList = new ArrayList<>();
    private JFreeChart chart;
    private ChartPanel panel;
    private JPanel filterPanel;
    private JPanel authorfilterPanel;
    private JPanel keywordfilterPanel;
    private JPanel datefilterPanel;
    private JPanel publicationfilterPanel;
    private JPanel placefilterPanel;
    private static int[][] keywordCount;
    protected static final Preprocessor PREPROCESSOR = new Preprocessor();
    private JDatePickerImpl startDatePicker;
    private JDatePickerImpl endDatePicker;
    private XYSeriesCollection dataset;
    private boolean showDateFilter = true;
    private boolean showKeywordFilter = true;
    private boolean showAuthorFilter = true;
    private boolean showPublicationFilter = true;
    private boolean showPlaceFilter = true;

    public LineChart(String title, List<ArticleFilter> filters) {
        super(title);
        setLayout(new GridLayout(1, 2));
        // Create dataset
        dataset = createDataset(filters);
        // Create chart
        DateAxis dateAxis = new DateAxis("Date");
        dateAxis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));
        dateAxis.setVerticalTickLabels(true);
        dateAxis.setAutoRange(false);
        System.out.println(dateAxis.getTickUnit().getUnitType().toString());
        chart = ChartFactory.createXYLineChart("Keyword Frequency", "Date", "Frequency"
                , dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.getXYPlot().setDomainAxis(dateAxis);
        chart.getXYPlot().mapDatasetToRangeAxis(0, 0);
        panel = new ChartPanel(chart, 300, 300, 100, 100, 2000, 2000, true, false, true, true, true, true);
        panel.setPreferredSize(new Dimension(500, 300));
        add(panel, BorderLayout.EAST);
        filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout());
        button = new JButton("Filter");
        datefilterPanel = new JPanel();
        hideOrShowDateFitler = new JButton("date filter");
        hideOrShowDateFitler.setBackground(Color.RED);
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
        datefilterPanel.add(startDatePicker);
        datefilterPanel.add(endDatePicker);
        datefilterPanel.setLayout(new GridLayout(2, 2));

        ///////////////////////////////////////////////
        keywordfilterPanel = new JPanel();

        int y = 100;
        for (String keyword : PREPROCESSOR.getKeywordsArr()) {
            JCheckBox cb = new JCheckBox(keyword, true);
            cb.setBounds(100, y += 20, 150, 20);
            cbList.add(cb);
            keywordfilterPanel.add(cb);
        }
        unselectAllButton = new JButton("UnselectAll");
        keywordfilterPanel.add(unselectAllButton);
        hideOrShowKeywordFilter = new JButton("keyword filter");
        hideOrShowKeywordFilter.setBackground(Color.RED);
        keywordfilterPanel.setLayout(new GridLayout(20, 5));
        //////////////////////////////////////////////////////////////
        placefilterPanel = new JPanel();
        placefilterPanel.setLayout(new FlowLayout());
        y = 100;
        for (String place : PREPROCESSOR.getPlaces()) {
            JCheckBox cb = new JCheckBox(place, true);
            cb.setBounds(100, y += 20, 150, 20);
            placeCbList.add(cb);
            placefilterPanel.add(cb);
        }
        unselectAllPlaceButton = new JButton("Unselect All Place");
        placefilterPanel.add(unselectAllPlaceButton);
        placefilterPanel.setLayout(new GridLayout(5, 5));
        hideOrShowPlaceFilter = new JButton("place filter");
        hideOrShowPlaceFilter.setBackground(Color.RED);

////////////////////////////////////////////////////////////////////////
        authorfilterPanel = new JPanel();
        authorfilterPanel.setLayout(new FlowLayout());
        y = 100;
        for (String author : PREPROCESSOR.getAuthors()) {
            JCheckBox cb = new JCheckBox(author, true);
            cb.setBounds(100, y += 20, 150, 20);
            authorCbList.add(cb);
            authorfilterPanel.add(cb);
        }
        unselectAllAuthorButton = new JButton("Unselect All Author");
        authorfilterPanel.add(unselectAllAuthorButton);
        authorfilterPanel.setLayout(new GridLayout(5, 5));
        hideOrShowAuthorFilter = new JButton("author filter");
        hideOrShowAuthorFilter.setBackground(Color.RED);
        /////////////////////////////////////////////////////////////
        publicationfilterPanel = new JPanel();
        publicationfilterPanel.setLayout(new FlowLayout());
        y = 100;
        unselectAllPublicationButton = new JButton("Unselect All Publication");

        publicationfilterPanel.setLayout(new GridLayout(5, 5));
        for (String pub : PREPROCESSOR.getPublications()) {
            JCheckBox cb = new JCheckBox(pub, true);
            //cb.setBounds(100, y += 20, 150, 20);
            publicationCbList.add(cb);
            publicationfilterPanel.add(cb);
        }
        publicationfilterPanel.add(unselectAllPublicationButton);
        publicationfilterPanel.revalidate();
        publicationfilterPanel.repaint();
        hideOrShowPublicationFilter = new JButton("publication filter");
        hideOrShowPublicationFilter.setBackground(Color.RED);
        /////////////////////////////////
        resetAllButton = new JButton("Reset All");

        filterPanel.add(hideOrShowDateFitler);
        filterPanel.add(datefilterPanel);
        filterPanel.add(hideOrShowKeywordFilter);
        filterPanel.add(keywordfilterPanel);
        filterPanel.add(hideOrShowPlaceFilter);
        filterPanel.add(placefilterPanel);
        filterPanel.add(hideOrShowAuthorFilter);
        filterPanel.add(authorfilterPanel);
        filterPanel.add(hideOrShowPublicationFilter);
        filterPanel.add(publicationfilterPanel);

        filterPanel.add(resetAllButton);
        filterPanel.add(button);
        filterPanel.setPreferredSize(new Dimension(400, 700));
        add(filterPanel, BorderLayout.WEST);
    }

    public XYSeriesCollection createDataset(List<ArticleFilter> filters) {
        long startDate = computeKeywordFrequency(filters);
        XYSeriesCollection dataset = new XYSeriesCollection();
        String[] keywords = PREPROCESSOR.getKeywordsArr();
        for (int i = 0; i < keywordCount.length; i++) {
            XYSeries keywordSeries = new XYSeries(keywords[i]);
            for (int j = 0; j < keywordCount[i].length; j++) {
                if (keywordCount[i][j] >= 0) {
                    keywordSeries.add(((long)j * (1000 * 60 * 60 * 24) + startDate), keywordCount[i][j]);
                }
            }
            dataset.addSeries(keywordSeries);

//            for (int j = 0; j < keywordCount[i].length; j++) {
//                if (keywordCount[i][j] >= 0) {
//                    dataset.addValue(keywordCount[i][j],  keywords[i], String.valueOf(j));
//                }
//            }
        }
        return dataset;
    }

    public XYDataset updateDataset(XYSeriesCollection dataset, List<ArticleFilter> articleFilter) {
        System.out.println("dataset update called");
        long startDate = computeKeywordFrequency(articleFilter);
        dataset.removeAllSeries();
        String[] keywords = PREPROCESSOR.getKeywordsArr();
        for (int i = 0; i < keywordCount.length; i++) {
            XYSeries keywordSeries = new XYSeries(keywords[i]);
            for (int j = 0; j < keywordCount[i].length; j++) {
                if (keywordCount[i][j] >= 0) {
                    keywordSeries.add(((long)j * (1000 * 60 * 60 * 24) + startDate), keywordCount[i][j]);
                }
            }
            if (!keywordSeries.isEmpty()) {
                dataset.addSeries(keywordSeries);
            }
        }
//        String[] keywords = PREPROCESSOR.getKeywordsArr();
//        for (int i = 0; i < keywordCount.length; i++) {
//            for (int j = 0; j < keywordCount[i].length; j++) {
//                if (keywordCount[i][j] >= 0) {
//                    dataset.addValue(keywordCount[i][j], keywords[i], String.valueOf(j));
//                }
//            }
//        }
        return dataset;
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
            example.hideOrShowDateFitler.addActionListener(new AbstractAction("hideOrShow") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    example.showDateFilter = example.hideOrShow(example.showDateFilter, example.datefilterPanel);
                }
            });
            example.hideOrShowKeywordFilter.addActionListener(new AbstractAction("hideOrShow") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    example.showKeywordFilter = example.hideOrShow(example.showKeywordFilter, example.keywordfilterPanel);
                }
            });
            example.hideOrShowAuthorFilter.addActionListener(new AbstractAction("hideOrShow") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    example.showAuthorFilter = example.hideOrShow(example.showAuthorFilter, example.authorfilterPanel);
                }
            });
            example.hideOrShowPublicationFilter.addActionListener(new AbstractAction("hideOrShow") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    example.showPublicationFilter = example.hideOrShow(example.showPublicationFilter, example.publicationfilterPanel);
                }
            });
            example.hideOrShowPlaceFilter.addActionListener(new AbstractAction("hideOrShow") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    example.showPlaceFilter = example.hideOrShow(example.showPlaceFilter, example.placefilterPanel);
                }
            });
            example.hideOrShowDateFitler.addActionListener(new AbstractAction("hideOrShow") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    example.hideOrShow(example.showDateFilter, example.datefilterPanel);
                }
            });

            example.unselectAllButton.addActionListener(new AbstractAction("unselect") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (JCheckBox checkBox : example.cbList) {
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
                    for (JCheckBox checkBox : example.cbList) {
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
                    example.datefilterPanel.setVisible(true);
                    example.showAuthorFilter = true;
                    example.authorfilterPanel.setVisible(true);
                    example.showPlaceFilter = true;
                    example.placefilterPanel.setVisible(true);
                    example.showPublicationFilter = true;
                    example.publicationfilterPanel.setVisible(true);
                    example.showKeywordFilter = true;
                    example.keywordfilterPanel.setVisible(true);
                }
            });
            example.button.addActionListener(new AbstractAction("filter") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    filters.clear();
                    example.filterFlag = !example.filterFlag;
                    List<String> selectedKeywords = new ArrayList<>();
                    //////////////////////////////////////////////
                    ArticleFilter keywordFilter = new ArticleFilter();
                    for (JCheckBox checkBox : example.cbList) {
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
                    //example.panel.getChart().removeLegend();
                    example.panel.getChart().fireChartChanged();
//                    LegendTitle legend = new LegendTitle(example.panel.getChart().getXYPlot());
//                    legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
//                    legend.setBackgroundPaint(Color.WHITE);
//                    legend.setPosition(RectangleEdge.BOTTOM);
//                    example.panel.getChart().addSubtitle(legend);
//

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
            parseException.printStackTrace();
        }
        long timestamp = date.getTime();
        return timestamp;
    }


    private static void clearKeycount() {
        for (int i = 0; i < keywordCount.length; i++) {
            for (int j = 0; j < keywordCount[i].length; j++) {
                keywordCount[i][j] = -1;
            }
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
        clearKeycount();
        int lastdateIdx = 0;
        Article lastArticle = null;
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
                        lastdateIdx = dateIdx;
                        lastArticle = article;
                    }
                }
            }

        }
        System.out.println("Max key count = " + maxKeyCount);
        System.out.println("Max keyword = " + maxKeyword);
        System.out.println("Remaining articles = " + remainingArticlesCount);
        System.out.println("lastdate" + lastdateIdx);
        long num = (long)lastdateIdx * (1000 * 60 * 60 * 24);
        System.out.println("num" + num);
        num = num + startDate;
        System.out.println("num+start" + num);
        System.out.println("last date " + new Date(num) + ":::" + lastdateIdx + ":::" + startDate + "::::" + num);
        System.out.println("last date " + new Date(lastArticle.getDate().getTime()) + ":::" + lastArticle.getDate().getTime());
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
                    removeItem |= true;
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
                if (null != place) {
                    place = place.toLowerCase(Locale.ROOT);
                }
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
            if (!filter.isKeepEmptyValue()) {
                return true;
            } else {
                return false;
            }
        }
        if (!contains) {
            return true;
        }
        if (contains2) {
            return true;
        }
        return false;
    }

    private static boolean isRemoveItemByFieldVal(ArticleFilter filter, String fieldValue) {
        return isRemoveItem(filter, fieldValue, filter.getSelectedValues().contains(fieldValue),
                filter.getUnselectedValues().contains(fieldValue));
    }

}