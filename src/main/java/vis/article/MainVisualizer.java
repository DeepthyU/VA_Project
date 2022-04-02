package vis.article;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import preprocessing.Article;
import preprocessing.Edge;
import preprocessing.Preprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainVisualizer {
    protected static final Preprocessor PREPROCESSOR = new Preprocessor();
    private List<ArticleFilter> filters;

    public static Graph initGraph(String styleSheet) {
        Graph graph = new SingleGraph("single graph");
        graph.setAttribute("ui.stylesheet", styleSheet);
        graph.setAutoCreate(true);
        graph.setStrict(false);
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        return graph;
    }

    public void makeGraph(List<ArticleFilter> filters, boolean showEdges) {

    }

    public Graph prepareGraph(boolean showEdges) {
        return null;
    }


    //TODO: zoom https://stackoverflow.com/questions/44675827/how-to-zoom-into-a-graphstream-view
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

    protected boolean isRemoveItem(List<ArticleFilter> filters, Article article) {
        boolean removeItem = false;
        for (ArticleFilter filter : filters) {
            if (ArticleField.DATE.equals(filter.getField())) {
                long start = filter.getStartDate();
                long end = filter.getEndDate();
                if (article.getDate().getTime() < start || article.getDate().getTime() > end) {
                    removeItem |= true;
                    break;
                }
            } else if (ArticleField.AUTHOR.equals(filter.getField())) {
                String author = article.getAuthor().toLowerCase(Locale.ROOT);
                removeItem |= isRemoveItemByFieldVal(filter, author);
            } else if (ArticleField.PUBLICATION.equals(filter.getField())) {
                String publication = article.getPublication().toLowerCase(Locale.ROOT);
                removeItem |= isRemoveItemByFieldVal(filter, publication);
            } else if (ArticleField.PLACE.equals(filter.getField())) {
                String place = article.getPlace().toLowerCase(Locale.ROOT);
                removeItem |= isRemoveItemByFieldVal(filter, place);
            } else if (ArticleField.KEYWORD.equals(filter.getField())) {
                String keywords = "key";
                List<String> valList = article.getKeywordsList();
                removeItem |= isRemoveItem(filter, keywords,
                        CollectionUtils.isNotEmpty(CollectionUtils.intersection(filter.getSelectedValues(), valList)),
                        CollectionUtils.isNotEmpty(CollectionUtils.intersection(filter.getUnselectedValues(), valList)));
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

    public void setFilters(List<ArticleFilter> filters) {
        this.filters = filters;
    }

    public List<ArticleFilter> getFilters() {
        filters = new ArrayList<>();
        ArticleFilter af = new ArticleFilter();
        af.setField(ArticleField.KEYWORD);
        af.getSelectedValues().add("government");
        filters.add(af);
        af = new ArticleFilter();
        af.setField(ArticleField.PUBLICATION);
        List<String> selectedPublications = new ArrayList<>();
        selectedPublications.add("International News");
        af.setSelectedValues(selectedPublications);

        //filters.add(af);
        return filters;
    }

    protected void sleep() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
