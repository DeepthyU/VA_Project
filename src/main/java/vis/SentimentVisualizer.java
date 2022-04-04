package vis;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.Viewer;
import preprocessing.Article;
import preprocessing.Edge;
import vis.article.ArticleFilter;
import vis.article.MainVisualizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SentimentVisualizer extends MainVisualizer {

    protected static String styleSheet =
            "node {	size: 5px, 5px; fill-color: black; } " +
                    "node.marked { fill-color: red;}" +
                    "node.positive { fill-color: blue;}" +
                    "node.negative { fill-color: red;}" +
                    "edge {\n" +
                    "\tshape: line;\n" +
                    "\tarrow-size: 3px, 2px;\n" +
                    "}" +
                    "edge.marked {\n" +
                    "\tshape: line;\n" +
                    "\tfill-color: red;\n" +
                    "\tarrow-size: 3px, 2px;\n" +
                    "}";

    public void makeGraph(boolean showEdges, boolean doFilter) {
        Graph graph = prepareGraph(showEdges, doFilter);

        Viewer viewer = graph.display();
        viewer.disableAutoLayout();
        if (showEdges) {
            Node start = graph.getNode(1);
            Iterator<? extends Node> k = start.getBreadthFirstIterator();
            Node previous = null;
            while (k.hasNext()) {

                Node next = k.next();
//            if (null != previous) {
//                org.graphstream.graph.Edge edge = previous.getEdgeBetween(next.getId());
//                if (null == edge) {
//                    System.out.println("Edge missing for :" + previous.getId() + " , " + next.getId());
//                } else {
//                    edge.setAttribute("ui.class", "marked");
//                }
//            }
                next.setAttribute("ui.class", "marked");
                previous = next;
                sleep();
            }
        }
        System.out.println("DONE");
    }

    public Graph prepareGraph(boolean showEdges, boolean doFilter) {
        Graph graph = initGraph(styleSheet);
        List<ArticleFilter> filters = new ArrayList<>();
        if (doFilter) {
            filters = getFilters();
        }
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
            node.setAttribute("xy", article.getxCoordinate(), article.getSentimentScore() * 100);
            node.setAttribute("title", article.getTitle());
            node.setAttribute("sentiment", article.getSentimentScore());
            node.setAttribute("publication", article.getPublication());
            if (article.getSentimentScore() < 0) {
                node.setAttribute("ui.class", "negative");
            } else if (article.getSentimentScore() > 0) {
                node.setAttribute("ui.class", "positive");
            }
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
    public static void main(String args[]) {
        SentimentVisualizer sv = new SentimentVisualizer();
        sv.makeGraph(false, true);
    }

}
