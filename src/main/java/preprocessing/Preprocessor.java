package preprocessing;

import keywordsearch.KeywordFinder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import sentimentAnalysis.SentimentAnalysis;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Preprocessor {
    private static boolean doInit = true;
    private static final String ARTICLES_PATH = "./src/main/data/gastech_data/data/articles/";
    private static final String HISTORIC_DOCS_PATH = "./src/main/data/gastech_data/data/HistoricalDocuments/txt versions";
    private List<Article> articleList;
    private static final String HISTORY = Utils.getText(HISTORIC_DOCS_PATH);
    private String[] keywordsArr;

    public Preprocessor() {
        // check file. if file exists, read from file
        articleList = makeArticleList(1000);
    }

    public Preprocessor(int count) {
        // check file. if file exists, read from file
        articleList = makeArticleList(count);
    }

    public List<Article> getArticleList() {
        return articleList;
    }
    public void setKeywordsArr(String[] keywordsArr) {
        this.keywordsArr = keywordsArr;
    }

    public String[] getKeywordsArr() {
        return keywordsArr;
    }

    private List<Article> makeArticleList(int count) {
        File file = new File(ARTICLES_PATH);
        List<Article> articleList = readFiles(file, count);
        //get coordinates
        setXCoordinate(articleList);
        setYCoordinate(articleList);
        findEdges(articleList);
        SentimentAnalysis.setSentiments(articleList, null);
        return articleList;
    }
    public void findEdges(List<Article> articleList) {
        findEdges(articleList, null);
    }
    public void findEdges(List<Article> articleList, List<String> keywordList) {
        for (Article article : articleList) {
            for (Article article1 : articleList) {
                if (article.getFileName().compareTo(article1.getFileName()) >= 0) {
                    break;
                }
                List<String> common = (List<String>) CollectionUtils.intersection(article.getKeywordsList(), article1.getKeywordsList());
                if (null != common) {
                    if (null != keywordList) {
                        common = (List<String>) CollectionUtils.intersection(common, keywordList);
                    }
                    if (null != common) {
                        Edge edge = new Edge();
                        edge.setNode1(article.getFileName());
                        edge.setNode2(article1.getFileName());
                        edge.setWeight(common.size());
                        article.getEdges().add(edge);
                    }
                }
            }
        }
    }

    private void setYCoordinate(List<Article> articleList) {
        KeywordFinder kf = new KeywordFinder(articleList, HISTORY);
        String[] keywords = kf.getKeywordsArr();
        setKeywordsArr(keywords);
        for (Article article : articleList) {
            String title = article.getTitle();
            String content = article.getContent();
            int count = 0;
            for (String keyword : keywords) {
                if ((StringUtils.isNotBlank(title) && title.contains(keyword)) ||
                        (StringUtils.isNotBlank(content) && content.contains(keyword))) {
                    article.getKeywordsList().add(keyword);
                    count++;
                }
            }
            article.setyCoordinate(count);
        }
    }

    private List<Article> readFiles(File folder, int count) {
        List<Article> articleList = new ArrayList<>();
        int counter = 0;
        for (final File fileEntry : folder.listFiles()) {
            if (count== counter){
                break;
            }
            counter++;
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
        System.out.println("No. of files: " + folder.listFiles().length);
        System.out.println("No. of article objs: " + articleList.size());
        return articleList;
    }


    private void setXCoordinate(List<Article> articleList) {
        articleList.sort(Comparator.comparing(Article::getDate));
        long startDate = articleList.get(0).getDate().getTime();
        long endDate = articleList.get(articleList.size() - 1).getDate().getTime();
        long totalDays = (endDate - startDate) / (1000 * 60 * 60 * 24);
        System.out.println(startDate);
        System.out.println(endDate);
        System.out.println(totalDays);
        for (Article article : articleList) {
            Timestamp date = article.getDate();
            int x = (int) ((date.getTime() - startDate) / (1000 * 60 * 60 * 24));
            article.setxCoordinate(x);
        }
    }

    public static void main(String[] args) {
        Preprocessor preprocessor = new Preprocessor(1000);
        Utils.writeArticleListToFile("article_list.json", preprocessor.getArticleList());
        List<Article> readArticles = Utils.readArticleList("article_list.json");
        System.out.println(readArticles.get(0).getTitle());
    }
}