package vis.article;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import preprocessing.Article;
import preprocessing.Edge;
import preprocessing.Preprocessor;

import java.util.ArrayList;
import java.util.List;

public class MainVisualizer {
    protected static final Preprocessor PREPROCESSOR = new Preprocessor();
    private List<ArticleFilter> filters;

    public static Graph initGraph(String styleSheet) {
        Graph graph = new SingleGraph("single graph");
        graph.setAttribute("ui.stylesheet", styleSheet);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("org.graphstream.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        return graph;
    }

    public void makeGraph(boolean showEdges) {

    }

    public Graph prepareGraph(boolean showEdges, boolean doFilter) {
        return null;
    }


    protected void updateEdges(List<Article> currList, List<ArticleFilter> filters) {
        ArticleFilter keywordFilter = null;
        List<String> keywords = null;
        for (ArticleFilter filter : filters) {
            if (filter.getField().equals(ArticleField.KEYWORD)) {
                keywordFilter = filter;
                break;
            }
        }
        if (null != keywordFilter) {
            keywords = keywordFilter.getSelectedValues();
        }
        PREPROCESSOR.findEdges(currList, keywords);
    }

    protected void resetEdges(List<Article> currList) {
        for (Article article : currList) {
            article.setEdges(new ArrayList<Edge>());
        }
    }


    public void setFilters(List<ArticleFilter> filters) {
        this.filters = filters;
    }

    public List<ArticleFilter> getFilters() {
        filters = new ArrayList<>();
        ArticleFilter af = new ArticleFilter();
        af.setField(ArticleField.KEYWORD);
        af.getSelectedValues().add("investigation");
        af.getSelectedValues().add("gastech");
        //filters.add(af);
        af = new ArticleFilter();
        af.setField(ArticleField.PUBLICATION);
        List<String> selectedPublications = new ArrayList<>();
        selectedPublications.add("International News");
        af.setSelectedValues(selectedPublications);

        filters.add(af);
        return filters;
    }

    protected void sleep() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            System.out.println("ERROR: exception occured in method sleep()");
        }
    }

}
