package vis;

import org.apache.commons.lang3.StringUtils;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import preprocessing.Article;
import preprocessing.Edge;
import preprocessing.Preprocessor;

import java.util.*;

public class ArticleVisualizer {
    private List<ArticleFilter> filters;
    protected static String styleSheet =
            "node {	fill-color: black; } node.marked { fill-color: red;}" +
                    "node.kronos { fill-color: blue;}" +
                    "node.tethys { fill-color: green;}";

    protected static Graph graph = initGraph(styleSheet);
    protected static List<Article> ARTICLE_LIST = getArticles();

    public static void main(String args[]) {
        ArticleVisualizer av = new ArticleVisualizer();
        List<ArticleFilter> filters = av.getFilters();
        makeGraph(filters, true);
    }

    public List<ArticleFilter> getFilters() {
        if (null == filters){
            filters = new ArrayList<>();
        }
        return filters;
    }

    public void setFilters(List<ArticleFilter> filters) {
        this.filters = filters;
    }

    public static void makeGraph(List<ArticleFilter> filters, boolean showEdges) {
        System.out.println("article list size is " + ARTICLE_LIST.size());
        for (Article article : ARTICLE_LIST) {
            if (isRemoveItem(filters, article)) {
                continue;
            }
            Node node = graph.addNode(article.getFileName());
            node.setAttribute("xy", article.getxCoordinate(), article.getyCoordinate() * 200);
            node.setAttribute("title", article.getTitle());
            node.setAttribute("place", article.getPlace());
            node.setAttribute("author", article.getAuthor());
            node.setAttribute("publication", article.getPublication());
            if (article.getPlace().toLowerCase(Locale.ROOT).contains("kronos")) {
                node.setAttribute("ui.class", "kronos");
            } else if (article.getPlace().toLowerCase(Locale.ROOT).contains("tethys")) {
                node.setAttribute("ui.class", "tethys");
            }
//            else {
//                System.out.println("Unknown place:" + article);
//            }
        }
        if (showEdges) {
            for (Article article : ARTICLE_LIST) {
                for (Edge edge : article.getEdges()) {
                    graph.addEdge(String.valueOf(edge.hashCode()), edge.getNode1(), edge.getNode2());
                }
            }
        }

        Viewer viewer = graph.display();
        viewer.disableAutoLayout();
        Node start = graph.getNode("100.txt");
        Iterator<? extends Node> k = start.getBreadthFirstIterator();

        while (k.hasNext()) {
            Node next = k.next();
            next.setAttribute("ui.class", "marked");
            sleep();
        }

    }

    public Graph prepareGraph(boolean showEdges) {
        Graph graph = initGraph(styleSheet);
        List<ArticleFilter> filters = getFilters();
        System.out.println("article list size is " + ARTICLE_LIST.size());
        for (Article article : ARTICLE_LIST) {
            if (isRemoveItem(filters, article)) {
                continue;
            }
            Node node = graph.addNode(article.getFileName());
            node.setAttribute("xy", article.getxCoordinate(), article.getyCoordinate() * 200);
            node.setAttribute("title", article.getTitle());
            node.setAttribute("place", article.getPlace());
            node.setAttribute("author", article.getAuthor());
            node.setAttribute("publication", article.getPublication());
            if (article.getPlace().toLowerCase(Locale.ROOT).contains("kronos")) {
                node.setAttribute("ui.class", "kronos");
            } else if (article.getPlace().toLowerCase(Locale.ROOT).contains("tethys")) {
                node.setAttribute("ui.class", "tethys");
            }
//            else {
//                System.out.println("Unknown place:" + article);
//            }
        }
        if (showEdges) {
            for (Article article : ARTICLE_LIST) {
                for (Edge edge : article.getEdges()) {
                    graph.addEdge(String.valueOf(edge.hashCode()), edge.getNode1(), edge.getNode2());
                }
            }
        }
        return graph;

    }


    private static boolean isRemoveItem(List<ArticleFilter> filters, Article article) {
        boolean removeItem = false;
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
                removeItem = isRemoveItemByFieldVal(filter, author);
            } else if (ArticleField.PUBLICATION.equals(filter.getField())) {
                String publication = article.getPublication();
                removeItem = isRemoveItemByFieldVal(filter, publication);
            } else if (ArticleField.PLACE.equals(filter.getField())) {
                String place = article.getPlace();
                removeItem = isRemoveItemByFieldVal(filter, place);
            } else if (ArticleField.KEYWORD.equals(filter.getField())) {
                String keywords = "";
                List<String> valList = Arrays.asList(keywords.split("#"));
                return isRemoveItem(filter, keywords, filter.getSelectedValues().contains(valList),
                        filter.getUnselectedValues().contains(valList));
            }
        }
        return removeItem;
    }

    private static boolean isRemoveItem(ArticleFilter filter, String keywords, boolean contains, boolean contains2) {
        if (StringUtils.isBlank(keywords)) {
            if (!filter.isKeepEmptyValue()) {
                return true;
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

    public static List<Article> getArticles() {
        Preprocessor preprocessor = new Preprocessor();
        List<Article> articleList = preprocessor.getArticleList();
        return articleList;
    }

    public static Graph initGraph(String styleSheet) {
        Graph graph = new SingleGraph("single graph");

        graph.setAttribute("ui.stylesheet", styleSheet);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        System.setProperty("org.graphstream.ui", "javafx");
        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        return graph;
    }

    protected static void sleep() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}