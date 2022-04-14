package preprocessing;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//readfile
//writefile
public class Utils {

    public static Timestamp format_date(String oldDateString) {
        Pattern pattern = Pattern.compile("((\\d{2}|[0-9])[\\s\\.\\-\\/]\\d{2}[\\s\\.\\-\\/]\\d{4})|(\\d{4}[\\s\\.\\-\\/]\\d{2}[\\s\\.\\-\\/]\\d{2})|((\\d{2}|[0-9])\\s*((J|j)(anuary|une|uly|an|un|ul|AN|UN|UL)|(F|f)(ebruary|eb|an|EB)|(M|m)(arch|ar|AR|ay|AY)|(A|a)(pril|ugust|pr|ug|PR|UG)|(S|s)(eptember|ept|EPT)|(O|o)(ctober|ct|CT)|(N|n)(ovember|ov|OV)|(D|d)(ecember|ec|EC))\\s*\\d{4})");
        Matcher matcher = pattern.matcher(oldDateString);
        boolean matchFound = matcher.find();
        if (matchFound) {
            String old_format = "dd MMMM yyyy";

            try {
                SimpleDateFormat sdf = new SimpleDateFormat(old_format);
                Date d = sdf.parse(oldDateString);
                Timestamp ts = new Timestamp(d.getTime());
                //sdf.applyPattern(new_format);
                return ts; //sdf.format(d);
            } catch (ParseException e) {
                old_format = "yyyy/MM/dd";
                SimpleDateFormat sdf = new SimpleDateFormat(old_format);
                try {
                    Date d = sdf.parse(oldDateString);
                    Timestamp ts = new Timestamp(d.getTime());
                    return ts;
                } catch (ParseException parseException) {
                    old_format = "ddMMMM yyyy";
                    sdf = new SimpleDateFormat(old_format);
                    try {
                        Date d = sdf.parse(oldDateString);
                        Timestamp ts = new Timestamp(d.getTime());
                        return ts;
                    } catch (ParseException parseEx) {
                        parseException.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public static String readFile(String path, Charset encoding) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        } catch (IOException e) {
            return "";
        }
    }

    public static String readAndDeleteFile(String path, Charset encoding) {
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(path));

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: file read failed for " + path);
        }
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            System.out.println("ERROR: delete operation failed for " + path);
            e.printStackTrace();
        }
        return new String(encoded, encoding);
    }

    public static String getText(String path) {
        File folder = new File(path);
        String text = "";
        if (folder.isDirectory()) {
            for (final File fileEntry : folder.listFiles()) {
                text.concat(readFile(fileEntry.getPath(), StandardCharsets.UTF_8));
            }
        } else {
            text.concat(readFile(folder.getPath(), StandardCharsets.UTF_8));
        }
        return text;

    }

    public static void writeFile(String path, String text) {
        try {
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(text);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Timestamp output = format_date("2007/03/22");
        System.out.println(output);
    }

    public static int writeArticleListToFile(String fileName, List<Article> articleList) {
        try {
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(fileName));
            Gson gson = new Gson();
            Type type = new TypeToken<List<Article>>() {}.getType();
            String jsonStr = gson.toJson(articleList, type);
            bWriter.append(jsonStr);
            bWriter.newLine();
            bWriter.close();
        } catch (IOException e) {
            return -1;
        }
        return 0;
    }

    public static int writeArticleToFile(String fileName, Article article) {
        try {
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(fileName, true));
            Gson gson = new Gson();
            Type type = new TypeToken<Article>() {}.getType();
            String jsonStr = gson.toJson(article, type);
            bWriter.append(jsonStr);
            bWriter.newLine();
            bWriter.close();
        } catch (IOException e) {
            return -1;
        }
        return 0;
    }

    public static List<Article> readArticles(String fileName) {
        List<Article> articleList = new ArrayList<>();
        String line = null;
        try {
            BufferedReader buffReader = new BufferedReader(new FileReader(fileName));
            Gson gson = new Gson();
            while ((line = buffReader.readLine()) != null) {
                Article article = gson.fromJson(line, Article.class);
                articleList.add(article);
            }
            buffReader.close();
        } catch (IOException e) {
            System.out.println("ERROR: File read failed in readArticleList()");
        } catch (JsonSyntaxException e){
            System.out.println("ERROR: Article deserialise read failed in readArticles() for line :");
        }
        return articleList;
    }

    public static List<Article> readArticleList(String fileName) {
        List<Article> articleList = new ArrayList<>();
        String line = null;
        try {
            BufferedReader buffReader = new BufferedReader(new FileReader(fileName));
            Gson gson = new Gson();
            Type type = new TypeToken<List<Article>>() {}.getType();
            while ((line = buffReader.readLine()) != null) {
                articleList.addAll(gson.fromJson(line, type));
            }
            buffReader.close();
        } catch (IOException e) {
            System.out.println("ERROR: File read failed in readArticleList()");
        } catch (JsonSyntaxException e){
            System.out.println("ERROR: Article deserialise read failed in readArticleList() for line :");
        }
        return articleList;
    }
}