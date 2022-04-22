package preprocessing;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import main.article.ArticleField;
import main.article.ArticleFilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
import java.util.Locale;
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
                        System.out.println("ERROR: parsing date failed with error " + parseEx.getCause());
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
            System.out.println("ERROR: File read failed for " + path + " with error " + e.getCause());
            return "";
        }
    }

    public static String readAndDeleteFile(String path, Charset encoding) {
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(path));

        } catch (IOException e) {
            System.out.println("ERROR: file read failed for " + path);
        }
        deleteFile(path);
        return new String(encoded, encoding);
    }

    public static void deleteFile(String path) {
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            System.out.println("ERROR: delete operation failed for " + path);
        }
    }


    public static BufferedImage readAndDeleteImageFile(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));

        } catch (IOException e) {
            System.out.println("ERROR: image read failed for " + path);
        }
        deleteFile(path);
        return image;
    }

    public static BufferedImage readImageFile(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));

        } catch (IOException e) {
            System.out.println("ERROR: image read failed for " + path);
        }
        return image;
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
            System.out.println("ERROR: An error occurred during file write to path " + path);
        }
    }

    public static int writeArticleListToFile(String fileName, List<Article> articleList) {
        try {
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(fileName));
            Gson gson = new Gson();
            Type type = new TypeToken<List<Article>>() {
            }.getType();
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
            Type type = new TypeToken<Article>() {
            }.getType();
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
            System.out.println("ERROR: File read failed. " + e.getCause());
        } catch (JsonSyntaxException e) {
            System.out.println("ERROR: Article deserialise read failed:" + e.getCause());
        }
        return articleList;
    }

    public static List<Article> readArticleList(String fileName) {
        List<Article> articleList = new ArrayList<>();
        String line = null;
        try {
            BufferedReader buffReader = new BufferedReader(new FileReader(fileName));
            Gson gson = new Gson();
            Type type = new TypeToken<List<Article>>() {
            }.getType();
            while ((line = buffReader.readLine()) != null) {
                articleList.addAll(gson.fromJson(line, type));
            }
            buffReader.close();
        } catch (IOException e) {
            System.out.println("ERROR: File read failed in readArticleList()");
        } catch (JsonSyntaxException e) {
            System.out.println("ERROR: Article deserialise read failed in readArticleList() for line :");
        }
        return articleList;
    }

    public static boolean isRemoveItem(List<ArticleFilter> filters, Article article) {
        boolean removeItem = false;
        if (filters == null) {
            return false;
        }
        for (ArticleFilter filter : filters) {
            if (ArticleField.DATE.equals(filter.getField())) {
                long start = filter.getStartDate();
                long end = filter.getEndDate();
                if (article.getDate().getTime() < start || article.getDate().getTime() > end) {
                    removeItem = true;
                    break;
                }
            } else if (ArticleField.AUTHOR.equals(filter.getField())) {
                String author = article.getAuthor();
                if (null != author) {
                    author = author.toLowerCase(Locale.ROOT);
                }
                removeItem |= isRemoveItemByFieldVal(filter, author);
            } else if (ArticleField.PUBLICATION.equals(filter.getField())) {
                String publication = article.getPublication();
                if (null != publication) {
                    publication = publication.toLowerCase(Locale.ROOT);
                }
                removeItem |= isRemoveItemByFieldVal(filter, publication);
            } else if (ArticleField.PLACE.equals(filter.getField())) {
                String place = article.getPlace().toLowerCase(Locale.ROOT);
                place = place.toLowerCase(Locale.ROOT);
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
            return !filter.isKeepEmptyValue();
        }
        if (!contains) {
            return true;
        }
        return contains2;
    }

    private static boolean isRemoveItemByFieldVal(ArticleFilter filter, String fieldValue) {
        return isRemoveItem(filter, fieldValue, filter.getSelectedValues().contains(fieldValue),
                filter.getUnselectedValues().contains(fieldValue));
    }


}
