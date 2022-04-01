import preprocessing.Preprocessor;
import vis.ArticleField;
import vis.ArticleFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class FilterCheckBoxes extends JFrame implements ActionListener {
    JLabel l;
    List<JCheckBox> cbList = new ArrayList<>();
    JCheckBox keepEmptyVals;
    JButton b;

    FilterCheckBoxes() {
        l = new JLabel("Filter Articles");
        l.setBounds(50, 50, 300, 20);
        Preprocessor preprocessor = new Preprocessor();
        String[] keywords = preprocessor.getKeywordsArr();
        int y = 100;
        keepEmptyVals = new JCheckBox("Keep empty values");
        keepEmptyVals.setBounds(100, y, 150, 20);
        add(keepEmptyVals);
        b = new JButton("Filter");
        b.addActionListener(this);
        b.setBounds(100, 50, 50, 30);
        JButton btn1 = new JButton("Check all");
        btn1.addActionListener(new CheckAllAction());
        btn1.setBounds(10, 50, 50, 30);
        add(btn1);
        JButton btn2 = new JButton("Uncheck all");
        btn2.addActionListener(new UnCheckAllAction());
        btn2.setBounds(50, 50, 50, 30);
        add(btn2);
        for (String keyword : keywords) {
            JCheckBox cb = new JCheckBox(keyword, true);
            cb.setBounds(100, y += 20, 150, 20);
            add(cb);
            cbList.add(cb);
        }
        add(l);
        add(b);
        setSize(800, 800);
        JScrollBar vbar=new JScrollBar(JScrollBar.VERTICAL);
        add(vbar, BorderLayout.EAST);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
        String msg = "";
        List<String> selectedKeywords = new ArrayList<>();
        List<ArticleFilter> articleFilters = new ArrayList<>();
        ArticleFilter keywordFilter = new ArticleFilter();
        for (JCheckBox checkBox : cbList) {
            if (checkBox.isSelected()) {
                selectedKeywords.add(checkBox.getText());
            }
        }
        keywordFilter.setField(ArticleField.KEYWORD);
        keywordFilter.setSelectedValues(selectedKeywords);
        keywordFilter.setKeepEmptyValue(keepEmptyVals.isSelected());
        msg += "-----------------\n";
        JOptionPane.showMessageDialog(this, msg + "Total: " + selectedKeywords);
    }

    public static void main(String[] args) {
        new FilterCheckBoxes();
    }

    private class CheckAllAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

            Component[] components = (Component[])
                    getContentPane().getComponents();

            for (Component comp : components) {

                if (comp instanceof JCheckBox) {
                    JCheckBox box = (JCheckBox) comp;
                    box.setSelected(true);
                }
            }
        }
    }

    private class UnCheckAllAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {

            Component[] components = (Component[])
                    getContentPane().getComponents();

            for (Component comp : components) {

                if (comp instanceof JCheckBox) {
                    JCheckBox box = (JCheckBox) comp;
                    box.setSelected(false);
                }
            }
        }
    }
}