package sentimentAnalysis;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import preprocessing.Article;
import preprocessing.Utils;

import java.util.List;

public class SentimentAnalysis {


    private static final String ARTICLE_LIST_FILE_PATH = "article_list.json";

    public static void setSentiments(List<Article> articleList) {
        for (Article article:articleList) {
            StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
            String content = article.getTitle() + article.getContent();
            //remove lines which don't have keyword?
            CoreDocument coreDocument = new CoreDocument(content);
            stanfordCoreNLP.annotate(coreDocument);
            List<CoreSentence> sentences = coreDocument.sentences();
            int score = 0;
            for (CoreSentence sentence : sentences) {
                String sentiment = sentence.sentiment();
                if (sentiment.equals("Negative")) {
                    score -= 1;
                } else if (sentiment.equals("Positive")) {
                    score += 1;
                }
            }
            article.setSentimentScore(score);
            Utils.writeArticleToFile(ARTICLE_LIST_FILE_PATH, article);
            System.out.println("DEBUG: Sentiments calculated for article"+ article.getFileName());
        }
    }

}
