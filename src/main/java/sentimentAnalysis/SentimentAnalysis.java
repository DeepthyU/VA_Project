package sentimentAnalysis;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.commons.collections4.CollectionUtils;
import preprocessing.Article;
import preprocessing.Preprocessor;

import java.io.FileNotFoundException;
import java.util.List;

public class SentimentAnalysis {


    //TODO: write article output list to json file
    //TODO: update to take articleList as arguement
    public static void setSentiments(Article article) {
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
    }

}
