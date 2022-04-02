package vis;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;
import preprocessing.Article;
import preprocessing.Edge;
import vis.article.ArticleFilter;
import vis.article.MainVisualizer;

import java.util.*;

public class ArticleVisualizer extends MainVisualizer {

    protected static String styleSheet =
            "node {	fill-color: black; } node.marked { fill-color: red;}" +
                    "node.kronos { fill-color: blue;}" +
                    "node.tethys { fill-color: green;}";

    protected static Graph graph = initGraph(styleSheet);

    public static void main(String args[]) {
        ArticleVisualizer av = new ArticleVisualizer();
        List<ArticleFilter> filters = av.getFilters();
        av.makeGraph(filters, true);
    }

    public void makeGraph(List<ArticleFilter> filters, boolean showEdges) {
        System.out.println("article list size is " + PREPROCESSOR.getArticleList().size());
        List<Article> currList = new ArrayList<>();
        boolean needEdgeUpdate = false;
        for (Article article : PREPROCESSOR.getArticleList()) {
            if (isRemoveItem(filters, article)) {
                needEdgeUpdate = true;
                continue;
            }
            currList.add(article);
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
        System.out.println("Remaining articles after filtering:" + currList.size());
        if (showEdges) {
            if (needEdgeUpdate) {
                resetEdges(currList);
                updateEdges(currList, filters);
            }
            for (Article article : currList) {
                for (Edge edge : article.getEdges()) {
                    graph.addEdge(String.valueOf(edge.hashCode()), edge.getNode1(), edge.getNode2());
                }
            }
        }


        Viewer viewer = graph.display();
        viewer.disableAutoLayout();
        //View view = viewer.getDefaultView();
        //view.setMouseManager(new MouseOverMouseManager());
        viewer.enableXYZfeedback(true);
        ViewerPipe fromView = viewer.newViewerPipe(); // An object allowing thread-safe communication with the viewer
        fromView.addAttributeSink(graph); // Listen at the changes in the graphic graph.
        //fromView.addViewerListener();
        Node start = graph.getNode(currList.get(0).getFileName());
        Iterator<? extends Node> k = start.getBreadthFirstIterator();

        while (k.hasNext()) {
            Node next = k.next();
            next.setAttribute("ui.class", "marked");
            sleep();
        }
        System.out.println("DONE");
    }

    public Graph prepareGraph(boolean showEdges) {
        Graph graph = initGraph(styleSheet);
        List<ArticleFilter> filters = getFilters();
        boolean needEdgeUpdate = false;
        System.out.println("article list size is " + PREPROCESSOR.getArticleList().size());
        List<Article> currList = new ArrayList<>();
        for (Article article : PREPROCESSOR.getArticleList()) {
            if (isRemoveItem(filters, article)) {
                needEdgeUpdate = true;
                continue;
            }
            currList.add(article);
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
            if (needEdgeUpdate) {
                resetEdges(currList);
                updateEdges(currList, filters);
            }
            for (Article article : currList) {
                for (Edge edge : article.getEdges()) {
                    graph.addEdge(String.valueOf(edge.hashCode()), edge.getNode1(), edge.getNode2());
                }
            }
        }
        return graph;

    }

    //TODO: zoom https://stackoverflow.com/questions/44675827/how-to-zoom-into-a-graphstream-view


}