package preprocessing;

import preprocessing.Article;
import preprocessing.ArticleParser;
import preprocessing.Utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class Preprocessor {

    private static String articles_path = "./src/main/data/gastech_data/data/articles/";
    private static String historic_docs_path = "./src/main/data/gastech_data/data/HistoricalDocuments/txt versions";

    public static List readFiles(File folder) {
        List<Article> articleList = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            String text = Utils.readFile(fileEntry.getPath(), StandardCharsets.UTF_8);
            ArticleParser parser = new ArticleParser();
            Article article = parser.parseArticle(text);
            if (null == article) {
                System.out.println(fileEntry.getAbsoluteFile());
            } else {
                article.setFileName(fileEntry.getName());
                articleList.add(article);
            }
        }
        return articleList;
    }

    public static void main(String[] args) {

        File file = new File(articles_path);
        List articleList = readFiles(file);
        //get coordinates
        System.out.println(articleList);
        getCoordinates(articleList);


    }

    private static void getCoordinates(List<Article> articleList) {
        List<Timestamp> dateList = new ArrayList<>();
        for (Article article : articleList) {
            dateList.add(article.getDate());
        }
        articleList.sort(Comparator.comparing(Article::getDate));
        Timestamp startDate = articleList.get(0).getDate();
        Timestamp endDate = articleList.get(articleList.size() - 1).getDate();
        System.out.println(startDate);
        System.out.println(endDate);
    }

}