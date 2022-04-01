package keywordsearch;

import preprocessing.Article;
import preprocessing.Utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class KeywordFinder {

    public static String[] keywordsArr;

    public KeywordFinder(List<Article> articleList, String history){
        keywordsArr = getKeywords(articleList, history);
    }

    public String[] getKeywordsArr()
    {
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
        System.out.println("keycount: "+keywords.length);
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
