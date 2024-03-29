package main.article;

import utils.PythonExecuter;
import preprocessing.Article;
import preprocessing.Preprocessor;
import preprocessing.Utils;
import utils.VisualizerPrefs;
import utils.vis.ZoomablePicturePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.Charset;
import java.util.List;

public class WordCloudMainLogic {

    public static final String DEFAULT_ARTICLE_WORD_CLOUD = VisualizerPrefs.getInstance().getDataDirPath().resolve("articleWordCloud.png").toString();
    private ZoomablePicturePanel panel;
    private static Preprocessor PREPROCESSOR;

    public WordCloudMainLogic(Preprocessor preprocessor) {
        PREPROCESSOR = preprocessor;
        panel = new ZoomablePicturePanel(makeWordCloud(null));
        panel.setBackground(new Color(229, 235, 247));
        panel.setPreferredSize(new Dimension(1000, 600));
    }


    public JScrollPane simulate_graph() {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVisible(true);
        scrollPane.repaint();
        return scrollPane;
    }


    public void applyFilters(List filters) {
        panel.updateImage(makeWordCloud(filters));
        panel.repaint();
    }


    private BufferedImage makeWordCloud(List<ArticleFilter> filters) {
        String searchText = getSearchText(filters);
        Utils.writeFile("search_text_for_wc.txt", searchText);
        PythonExecuter executer = new PythonExecuter();
        executer.runWordCloudScript("./src/main/python/word_cloud.py", new String[]{"search_text_for_wc.txt", "0.05"}, "wc");
        String outFileName = Utils.readAndDeleteFile("wcpythonOut.txt", Charset.defaultCharset());
        BufferedImage data = Utils.readAndDeleteImageFile(outFileName + ".png");
        if (null == data){
            data = Utils.readImageFile(DEFAULT_ARTICLE_WORD_CLOUD);
        }
        Utils.deleteFile("search_text_for_wc.txt");
        return data;
    }


    private String getSearchText(List<ArticleFilter> filters) {
        String searchText = "";
        for (Article article : PREPROCESSOR.getArticleList()) {
            if (!Utils.isRemoveItem(filters, article)) {
                if (null != article.getTitle()) {
                    searchText = searchText.concat(article.getTitle());
                }
                searchText = searchText.concat(article.getContent());
            }
        }
        return searchText.toLowerCase();
    }


}
