package main.article;

import main.DateLabelFormatter;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import org.apache.commons.lang3.StringUtils;
import preprocessing.Preprocessor;
import utils.vis.SearchableJComboBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FilterMainLogic {

    private static Preprocessor PREPROCESSOR = new Preprocessor();
    private JButton unselectAllKeywordCbs, unselectAllPlaceCbs, unselectAllAuthorCbs, unselectAllPublicationCbs;
    private JRadioButton includeEmptyAuthorFilter, includeEmptyPlaceFilter, includeEmptyPublicationFilter;

    private List<JCheckBox> keywordsCbList = new ArrayList<>();
    private List<JCheckBox> placeCbList = new ArrayList<>();
    private List<JCheckBox> publicationCbList = new ArrayList<>();
    private List<JCheckBox> authorCbList = new ArrayList<>();

    private JDatePickerImpl startDatePicker, endDatePicker;
    private AtomicBoolean includeEmptyAuthor, includeEmptyPublication, includeEmptyPlace;
    private List<ArticleFilter> filters = new ArrayList<>();
    private JPanel filterMainPanel = new JPanel();
    private JComboBox keywordsCombo, placeCombo, authorCombo, publicationCombo;
    private JButton addKeyword, addPlace, addAuthor, addPublication;
    private boolean movingComponents = false;
    private int visibleIndex = 4;
    private JPanel keywordPanel;

    public FilterMainLogic() {
        UtilDateModel startDateModel = new UtilDateModel();
        startDateModel.setDate(1982, 9, 2);
        startDateModel.setSelected(true);
        UtilDateModel endDateModel = new UtilDateModel();
        endDateModel.setDate(2014, 03, 26);
        endDateModel.setSelected(true);
        JDatePanelImpl startDatePanel = new JDatePanelImpl(startDateModel);
        JDatePanelImpl endDatePanel = new JDatePanelImpl(endDateModel);
        startDatePicker = new JDatePickerImpl(startDatePanel, new DateLabelFormatter());
        endDatePicker = new JDatePickerImpl(endDatePanel, new DateLabelFormatter());

        ///////////////////////////////////////////////
        unselectAllKeywordCbs = new JButton("Unselect All Keywords");
        keywordsCombo = new JComboBox(new Preprocessor().getKeywordsList().toArray());
        // has to be editable
        keywordsCombo.setEditable(true);
        // change the editor's document

        new SearchableJComboBox(keywordsCombo);
        addKeyword = new JButton("ADD");
        //////////////////////////////////////////////////////////////
        unselectAllPlaceCbs = new JButton("Unselect All Place");
        includeEmptyPlaceFilter = new JRadioButton("Include emtpy values");
        includeEmptyPlaceFilter.setSelected(true);
        placeCombo = new JComboBox(new Preprocessor().getPlaces().toArray());
        // has to be editable
        placeCombo.setEditable(true);
        // change the editor's document
        new SearchableJComboBox(placeCombo);
        addPlace = new JButton("ADD");
        ////////////////////////////////////////////////////////////////////////
        unselectAllAuthorCbs = new JButton("Unselect All Author");
        includeEmptyAuthorFilter = new JRadioButton("Include emtpy values");
        includeEmptyAuthorFilter.setSelected(true);
        authorCombo = new JComboBox(new Preprocessor().getAuthors().toArray());
        // has to be editable
        authorCombo.setEditable(true);
        // change the editor's document
        new SearchableJComboBox(authorCombo);
        addAuthor = new JButton("ADD");
        /////////////////////////////////////////////////////////////
        unselectAllPublicationCbs = new JButton("Unselect All Publication");
        includeEmptyPublicationFilter = new JRadioButton("Include emtpy values");
        includeEmptyPublicationFilter.setSelected(true);
        publicationCombo = new JComboBox(new Preprocessor().getPublications().toArray());
        // has to be editable
        publicationCombo.setEditable(true);
        // change the editor's document
        new SearchableJComboBox(publicationCombo);
        addPublication = new JButton("ADD");
        /////////////////////////////////
        includeEmptyPublication = new AtomicBoolean(true);
        includeEmptyPlace = new AtomicBoolean(true);
        includeEmptyAuthor = new AtomicBoolean(true);

        addActionListeners();

        filterMainPanel.setLayout(null);
        // Add children and compute prefSize.

        Dimension d = new Dimension();
        int h = 0;
        String[] child_names = {"Date", "Keyword", "Place", "Author", "Publication"};
        for (int j = 0; j < child_names.length; j++) {
            ChildPanel child = new ChildPanel(child_names[j], j + 1, ml);
            filterMainPanel.add(child);
            d = child.getPreferredSize();
            child.setBounds(0, h, d.width, d.height);
            h += ControlPanel.HEIGHT;
        }
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
            int index = ((ControlPanel) e.getSource()).id - 1;
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

    private void addOptions(List<String> options, JPanel panel, List<JCheckBox> checkBoxListList) {
        for (String option : options) {
            JCheckBox cb = new JCheckBox(option, true);
            cb.setPreferredSize(new Dimension(150, 20));
            checkBoxListList.add(cb);
            panel.add(cb);
        }
    }


    private void addActionListeners() {
        unselectAllKeywordCbs.addActionListener(new UnselectAllActionListener(keywordsCbList));
        unselectAllPublicationCbs.addActionListener(new UnselectAllActionListener(publicationCbList));
        unselectAllAuthorCbs.addActionListener(new UnselectAllActionListener(authorCbList));
        unselectAllPlaceCbs.addActionListener(new UnselectAllActionListener(placeCbList));
        includeEmptyPlaceFilter.addActionListener(new IncludeEmptyButtonActionListener(includeEmptyPlaceFilter, includeEmptyPlace));
        includeEmptyPublicationFilter.addActionListener(new IncludeEmptyButtonActionListener(includeEmptyPublicationFilter, includeEmptyPublication));
        includeEmptyAuthorFilter.addActionListener(new IncludeEmptyButtonActionListener(includeEmptyAuthorFilter, includeEmptyAuthor));
        addKeyword.addActionListener(new AddKeywordButtonActionListener());
        addAuthor.addActionListener(new AddButtonActionListener(authorCombo, authorCbList));
        addPlace.addActionListener(new AddButtonActionListener(placeCombo, placeCbList));
        addPublication.addActionListener(new AddButtonActionListener(publicationCombo, publicationCbList));
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
        startDatePicker.getModel().setDate(1982, 9, 2);
        endDatePicker.getModel().setDate(2014, 3, 26);
        includeEmptyAuthor.set(true);
        includeEmptyAuthorFilter.setSelected(true);
        includeEmptyPlace.set(true);
        includeEmptyPlaceFilter.setSelected(true);
        includeEmptyPublication.set(true);
        includeEmptyPublicationFilter.setSelected(true);
    }

    class AddKeywordButtonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            int keywordIdx = keywordsCombo.getSelectedIndex();
            if (keywordIdx > -1) {
                keywordsCbList.get(keywordIdx).setSelected(true);
            } else {
                Object newObj = keywordsCombo.getSelectedItem();
                if (null != newObj) {
                    String newKeyword = newObj.toString();
                    if (StringUtils.isNotBlank(newKeyword)) {
                        PREPROCESSOR.updateKeywordsList(newKeyword);
                        JCheckBox cb = new JCheckBox(newKeyword, true);
                        cb.setPreferredSize(new Dimension(150, 20));
                        keywordsCbList.add(cb);
                        keywordPanel.add(cb);
                        keywordPanel.repaint();
                    }
                }
            }
        }

    }


    class AddButtonActionListener implements ActionListener {
        private JComboBox comboBox;
        private List<JCheckBox> cbList;

        public AddButtonActionListener(JComboBox comboBox, List<JCheckBox> cbList) {
            this.comboBox = comboBox;
            this.cbList = cbList;
        }

        public void actionPerformed(ActionEvent e) {
            int keywordIdx = comboBox.getSelectedIndex();
            if (keywordIdx > -1) {
                cbList.get(keywordIdx).setSelected(true);
            }
        }

    }

    class IncludeEmptyButtonActionListener implements ActionListener {
        private JRadioButton radioButton;
        private AtomicBoolean include;

        public IncludeEmptyButtonActionListener(JRadioButton radioButton, AtomicBoolean include) {
            this.radioButton = radioButton;
            this.include = include;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            include.set(!include.get());
            this.radioButton.setSelected(include.get());

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
            JPanel child = getContent(name);
            JScrollPane scrollPane = new JScrollPane(child);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.setVisible(true);
            scrollPane.repaint();
            add(scrollPane);
            if ("Keyword".equals(name)) {
                keywordPanel = child;
            }
        }

        private JPanel getContent(String name) {
            JPanel panel = new JPanel(new GridLayout());
            switch (name) {
                case "Date":
                    panel.add(startDatePicker);
                    panel.add(endDatePicker);
                    panel.setLayout(new GridLayout(2, 2));
                    break;
                case "Keyword":
                    panel.add(keywordsCombo);
                    panel.add(addKeyword);
                    panel.add(unselectAllKeywordCbs);
                    panel.add(new JLabel());
                    addOptions(PREPROCESSOR.getKeywordsList(), panel, keywordsCbList);
                    panel.setLayout(new GridLayout(44, 2));
                    break;
                case "Author":
                    panel.add(authorCombo);
                    panel.add(addAuthor);
                    panel.add(unselectAllAuthorCbs);
                    panel.add(includeEmptyAuthorFilter);
                    addOptions(PREPROCESSOR.getAuthors(), panel, authorCbList);
                    panel.setLayout(new GridLayout(7, 2));
                    break;
                case "Place":
                    panel.add(placeCombo);
                    panel.add(addPlace);
                    panel.add(unselectAllPlaceCbs);
                    panel.add(includeEmptyPlaceFilter);
                    addOptions(PREPROCESSOR.getPlaces(), panel, placeCbList);
                    panel.setLayout(new GridLayout(7, 2));
                    break;
                case "Publication":
                    panel.add(publicationCombo);
                    panel.add(addPublication);
                    panel.add(unselectAllPublicationCbs);
                    panel.add(includeEmptyPublicationFilter);
                    addOptions(PREPROCESSOR.getPublications(), panel, publicationCbList);
                    panel.setLayout(new GridLayout(17, 2));
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
        Color c1 = new Color(187, 193, 198);
        Color fontFg = new Color(250, 249, 249);
        Color rolloverFg = new Color(0, 94, 184);
        public final static int HEIGHT = 30;

        public ControlPanel(String name, int id, MouseListener ml) {
            this.id = id;
            this.name = name;
            setLayout(new BorderLayout());
            titleLabel = new JLabel("  " + name + " Filter", JLabel.LEFT);
            add(titleLabel);
            titleLabel.setForeground(fontFg);
            titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
            Dimension d = getPreferredSize();
            d.height = HEIGHT;
            setPreferredSize(d);
            addMouseListener(ml);
            addMouseListener(listener);
        }

        protected void paintComponent(Graphics g) {
            int w = getWidth();
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(w / 2, 0, c1, w / 2, HEIGHT / 2, c1));
            g2.fillRect(0, 0, w, HEIGHT);
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
