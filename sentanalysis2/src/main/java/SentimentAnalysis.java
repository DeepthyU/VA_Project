import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class SentimentAnalysis {

    public static String txtToString() throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader("0.txt"));
        String everything = null;
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                try {
                    line = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            everything = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return everything;
    }
    public static List<String> ReadFile() throws FileNotFoundException {

        //change the path to where you stored the article folder
        File folder = new File("D:\\University\\TUe\\DSAI\\J1\\J1 Q3\\2AMV10 - Visual Analytics\\Challenges" +
                "\\3. Disappearance at GAStech\\3. Disappearance at GAStech\\data\\data\\data\\articles");
        File[] listOfArticles = folder.listFiles();
        List<String> articles = new ArrayList<String>();

        for (int i = 0; i < listOfArticles.length; i++) {
            File article = listOfArticles[i];
            Scanner reader = new Scanner(article);
            String text = null;
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                text = text + line;
            }
            articles.add(text);
            reader.close();
        }
        return articles;
    }
    
    public static void main(String[] args) throws FileNotFoundException {

        //change the path to where you want to save it
        PrintStream output = new PrintStream("D:\\University\\TUe\\DSAI\\J1\\J1 Q3\\2AMV10 - Visual Analytics\\output\\output.txt");
        System.setOut(output);

        StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
        List<String> articles = ReadFile();
        String article;
        CoreDocument coreDocument;
        List<CoreSentence> sentences;
        String sentiment;
        String articleSentiment;

        // in bad i put all the articles that are seen as null
        int[] bad = new int[]{4, 11, 19, 52, 65, 70, 91, 115, 118, 119, 123, 125, 144, 146, 151, 160, 186, 195, 210,
                                217, 225, 226, 238, 290, 398, 608, 711, 779, 822};

        for (int i = 0; i < articles.size(); i++) {
            article = articles.get(i);
            if (article != null) {
                coreDocument = new CoreDocument(article);
                stanfordCoreNLP.annotate(coreDocument);
                sentences = coreDocument.sentences();
                int score = 0;
                for (CoreSentence sentence : sentences) {

                    sentiment = sentence.sentiment();
                    if (sentiment.equals("Negative")) {
                        score -= 1;
                    } else if (sentiment.equals("Positive")) {
                        score += 1;
                    }
                }
                if (score >= 1) {
                    articleSentiment = "Positive";
                } else if (score <= -1) {
                    articleSentiment = "Negative";
                } else {
                    articleSentiment = "Neutral";
                }
                // this works only with ordered articles by name. but when we order them by date i will change it so it
                //still outputs the correct article number
                System.out.println("Article" + i + "\t" + articleSentiment + "\t" + score);
            }
        }
    }
}
