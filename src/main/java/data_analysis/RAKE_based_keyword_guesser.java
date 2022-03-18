package data_analysis;


import data_analysis.rake.Rake;
import data_analysis.rake.RakeLanguages;

import java.io.*;
import java.util.*;

public class RAKE_based_keyword_guesser {
    public static void main(String[] args) {
        String filePath = "src/main/data/10 year historical document clean.txt";
        File file = new File(filePath);
        List<String> sentences = new ArrayList<String>();

        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null)
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        String text =  contentBuilder.toString();
        text = text.trim().replaceAll(Rake.getStopWordsPattern(), "");
        String languageCode = RakeLanguages.ENGLISH;
        Rake rake = new Rake(languageCode);
        LinkedHashMap<String, Double> results = rake.getKeywordsFromText(text);
        System.out.println(results.keySet());
    }
}
