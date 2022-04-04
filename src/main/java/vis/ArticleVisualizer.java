package vis;

import org.apache.commons.lang3.StringUtils;
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
            "node {	size: 5px, 5px; fill-color: black; }  node.marked { fill-color: red;}" +
                    "node.kronos { fill-color: blue;}" +
                    "node.tethys { fill-color: green;}";


    public static void main(String args[]) {
        ArticleVisualizer av = new ArticleVisualizer();
        //av.makeGraph(true, false);
        av.makeGraph(true, true);

    }

    public void makeGraph(boolean showEdges, boolean doFilter) {

        Graph graph = prepareGraph(showEdges, doFilter);

        Viewer viewer = graph.display();
        viewer.disableAutoLayout();
        //View view = viewer.getDefaultView();
        //view.setMouseManager(new MouseOverMouseManager());
        viewer.enableXYZfeedback(true);
        ViewerPipe fromView = viewer.newViewerPipe(); // An object allowing thread-safe communication with the viewer
        fromView.addAttributeSink(graph); // Listen at the changes in the graphic graph.
        //fromView.addViewerListener();
        if (showEdges) {
            Node start = graph.getNode(0);
            if (start != null) {
                Iterator<? extends Node> k = start.getBreadthFirstIterator();

                while (k.hasNext()) {
                    Node next = k.next();
                    next.setAttribute("ui.class", "marked");
                    sleep();
                }
            }
        }
        System.out.println("DONE");
    }

    public Graph prepareGraph(boolean showEdges, boolean doFilter) {
        Graph graph = initGraph(styleSheet);
        System.out.println("article list size is " + PREPROCESSOR.getArticleList().size());
        List<ArticleFilter> filters = new ArrayList<>();
        if (doFilter) {
            filters = getFilters();
        }
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
            if (StringUtils.containsIgnoreCase(article.getPlace(), "kronos")) {
                node.setAttribute("ui.class", "kronos");
            } else if (StringUtils.containsIgnoreCase(article.getPlace(), "tethys")) {
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
        return graph;
    }

    //TODO: zoom https://stackoverflow.com/questions/44675827/how-to-zoom-into-a-graphstream-view


}