package keywordsearch;

import org.apache.commons.lang3.ArrayUtils;
import preprocessing.Article;
import preprocessing.Utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeywordFinder {

    public static String[] keywordsArr;
    public static final String KEYWORDS_FILE_PATH = "keywords_list.txt";

    public KeywordFinder(List<Article> articleList, String history) {
        getKeywordsFromFile();
        findAndWriteKeywordsToFile(articleList, history);
    }

    private void findAndWriteKeywordsToFile(List<Article> articleList, String history) {
        if (ArrayUtils.isEmpty(keywordsArr)) {
            keywordsArr = getKeywords(articleList, 0, Long.MAX_VALUE, history);
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(KEYWORDS_FILE_PATH));

                outputStream.writeObject(keywordsArr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getKeywordsFromFile() {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(KEYWORDS_FILE_PATH));
            keywordsArr = (String[]) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String[] getKeywordsArr() {
        return keywordsArr;
    }

    private String[] getKeywords(List<Article> articleList, String history) {
        return getKeywords(articleList, 0, Long.MAX_VALUE, history);
    }

    private String[] getKeywords(List<Article> articleList, long start, long end, String history) {
        List articleSubSet = new ArrayList();
        String searchText = getSearchText(articleList, articleSubSet, start, end);
        searchText.concat(history);
        //https://github.com/kennycason/kumo
        Utils.writeFile("search_text.txt", searchText);
        PythonExecuter executer = new PythonExecuter();
        executer.runWordCloudScript();
        String outFileName = Utils.readAndDeleteFile("pythonOut.txt", Charset.defaultCharset());
        String data = Utils.readAndDeleteFile(outFileName + ".txt", Charset.defaultCharset());
        String[] keywords = data.split("##");
        System.out.println("keywords: " + keywords.length + " :: " + Arrays.toString(keywords));
        return keywords;//Arrays.copyOfRange(keywords, 0, 10);
    }

    private String getSearchText(List<Article> articleList, List articleSubSet, long start, long end) {
        String searchText = "";
        for (Article article : articleList) {
            long date = article.getDate().getTime();
            if (date >= start || date <= end) {
                articleSubSet.add(article);
                if (null != article.getTitle()) {
                    searchText = searchText.concat(article.getTitle());
                }
                searchText = searchText.concat(article.getContent());
            }
        }
        //add historical documents

        return searchText.toLowerCase();
    }


}
