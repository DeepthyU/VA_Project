package preprocessing;

import keywordsearch.KeywordFinder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import sentimentAnalysis.SentimentAnalysis;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Preprocessor {
    public static final String ARTICLE_LIST_FILE_PATH = "article_list.json";
    private static final String ARTICLES_PATH = "./src/main/data/gastech_data/data/articles/";
    private static final String HISTORIC_DOCS_PATH = "./src/main/data/gastech_data/data/HistoricalDocuments/txt versions";
    private List<Article> articleList;
    private static final String HISTORY = Utils.getText(HISTORIC_DOCS_PATH);
    private String[] keywordsArr;
    private List<String> places;
    private List<String> authors;
    private List<String> publications;

    public Preprocessor() {
        // check file. if file exists, read from file
        articleList = Utils.readArticles(ARTICLE_LIST_FILE_PATH);
        KeywordFinder kf = new KeywordFinder(articleList, HISTORY);
        keywordsArr = kf.getKeywordsArr();
        places = articleList.stream().map(Article::getPlace).distinct().filter(Objects::nonNull)
                .filter(Predicate.not(String::isBlank)).collect(Collectors.toList());
        authors = articleList.stream().map(Article::getAuthor).distinct().filter(Objects::nonNull)
                .filter(Predicate.not(String::isBlank)).collect(Collectors.toList());
        publications = articleList.stream().map(Article::getPublication).distinct().filter(Objects::nonNull)
                .filter(Predicate.not(String::isBlank)).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(articleList)) {
            File file = new File(ARTICLES_PATH);
            List<Article> articleList = readFiles(file);
            SentimentAnalysis.setSentiments(articleList);
        }
        //articleList = fillArticleList();
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

    private List<Article> fillArticleList() {
        //get coordinates
        Utils.readAndDeleteFile(ARTICLE_LIST_FILE_PATH, Charset.defaultCharset());
        setXCoordinate(articleList);
        setYCoordinate(articleList);
        //findEdges(articleList);
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
        for (Article article : articleList) {
            String title = article.getTitle();
            String content = article.getContent();
            int count = 0;
            for (String keyword : keywordsArr) {
                if (StringUtils.containsIgnoreCase(title, keyword)
                        || StringUtils.containsIgnoreCase(content, keyword)) {
                    article.getKeywordsList().add(keyword);
                    count++;
                }
            }
            article.setyCoordinate(count);
            Utils.writeArticleToFile(ARTICLE_LIST_FILE_PATH, article);
        }
    }


    private static List<Article> readFiles(File folder) {
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
        System.out.println("No. of files: " + folder.listFiles().length);
        System.out.println("No. of article objs: " + articleList.size());
        return articleList;
    }


    private void setXCoordinate(List<Article> articleList) {
        articleList.sort(Comparator.comparing(Article::getDate));
        long startDate = articleList.get(0).getDate().getTime();
        long endDate = articleList.get(articleList.size() - 1).getDate().getTime();
        long totalDays = (endDate - startDate) / (1000 * 60 * 60 * 24);
        System.out.println("START DATE:" + articleList.get(0).getDate());
        System.out.println("END DATE:" + articleList.get(articleList.size() - 1).getDate());
        System.out.println(totalDays);
        for (Article article : articleList) {
            Timestamp date = article.getDate();
            int x = (int) ((date.getTime() - startDate) / (1000 * 60 * 60 * 24));
            article.setxCoordinate(x);
        }
    }

    public List<String> getPlaces() {
        return places;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<String> getPublications() {
        return publications;
    }
}