package preprocessing;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class ArticleParser {

    List<String> STD_FORMAT_PUBLISHERS = new ArrayList<>(
            Arrays.asList("The Orb", "The Light of Truth", "The Tulip", "Worldwise", "Central Bulletin", "Athena Speaks",
                    "The Guide", "News Desk", "The Truth", "The World", "The General Post", "Who What News", "Daily Pegasus",
                    "International News", "The Wrap", "All News Today", "The Explainer", "All News Today", "The World"));

    List<String> TYPE2_FORMAT_PUBLISHERS = new ArrayList<>(
            Arrays.asList("Everyday News", "World Journal", "The Continent", "International Times",
                    "Tethys News", "World Journal", "World Source", "Tethys News", "News Online Today"));
    List<String> NO_TITLE_FORMAT_PUBLISHERS = new ArrayList<>(
            Arrays.asList("Homeland Illumination", "Centrum Sentinel"));

    public Article parseArticle(String text) {
        List<String> list = new ArrayList<>(Arrays.asList(text.split("\n")));
        list.removeIf(String::isBlank);
        list.replaceAll(String::trim);
        String[] lines = list.toArray(new String[0]);
        String publication = lines[0];
        if (lines.length < 4) {
            return null;
        }
        if (STD_FORMAT_PUBLISHERS.contains(publication)) {
            return parseStdArticle(lines, publication);
        }
        if (TYPE2_FORMAT_PUBLISHERS.contains(publication)) {
            return parseType2Article(lines, publication);
        }
        if ("Modern Rubicon".equalsIgnoreCase(publication) || "The Abila Post".equalsIgnoreCase(publication)) {
            int dateIdx = 2;
            int contentIdx = 3;
            // date format 20 January 2014
            Timestamp date = Utils.format_date(lines[dateIdx]);
            String author = "";
            if (null == date) {
                author = lines[dateIdx];
                dateIdx += 1;
                contentIdx += 1;
                date = Utils.format_date(lines[dateIdx]);
            }
            if (contentIdx >= lines.length) {
                System.out.println("Error in length:" + Arrays.toString(lines));
                return null;
            }
            Pattern pattern = Pattern.compile("^[0-9]*[\s]*(MODERNIZATION)[\s]*[0-9]*[\s]*[-]", CASE_INSENSITIVE);
            if ("The Abila Post".equalsIgnoreCase(publication))
                pattern = Pattern.compile("^[0-9]*[\s]*(UPDATE)[\s]*[-]", CASE_INSENSITIVE);
            Matcher content_matcher = pattern.matcher(lines[contentIdx]);
            String content = remove_pattern(contentIdx, content_matcher, lines);
            String[] results = getTitlePlaceContent(lines, 2, contentIdx);
            String place = results[1];
            return new Article(publication, "", author, date, place, content);
        }
        if ("Kronos Star".equalsIgnoreCase(publication)) {
            String title = lines[1];
            if (title.startsWith("Breaking: ")) {
                return parseStdArticle(lines, publication);
            } else {
                //breaking news
                title = title.substring(10);
                Article article = parseType2Article(lines, publication);
                article.setTitle(title);
                Pattern pattern = Pattern.compile("^(Update, )[0-9]{1,2}(:)[0-9]{1,2}[\\s](PM|AM){1}[:]");
                Matcher content_matcher = pattern.matcher(lines[3]);
                String content = remove_pattern(3, content_matcher, lines);
                article.setContent(content);
                return article;
            }
        }
        if (NO_TITLE_FORMAT_PUBLISHERS.contains(publication)) {
            Article article = parseNoTitleArticle(lines, publication);
            Pattern pattern = Pattern.compile("^[0-9]+[\\-]");
            Matcher content_matcher = pattern.matcher(lines[3]);
            String content = remove_pattern(3, content_matcher, lines);
            article.setContent(content);
            return article;
        }

        return null;
    }

    Article parseStdArticle(String[] lines, String publication) {
        int dateIdx = 2;
        int contentIdx = 3;
        //date format yyyy / mm / dd
        Timestamp date = Utils.format_date(lines[dateIdx]);
        String author = "";
        if (null == date) {
            author = lines[dateIdx];
            dateIdx += 1;
            contentIdx += 1;
            date = Utils.format_date(lines[dateIdx]);
        }
        if (contentIdx >= lines.length) {
            System.out.println("Error in length::" + Arrays.toString(lines));
            return null;
        }
        //search place only in beginning of content
        String[] output = getTitlePlaceContent(lines, 1, contentIdx);
        String title = output[0];
        String place = output[1];
        String content = output[2];
        return new Article(publication, title, author, date, place, content);
    }


    String remove_pattern(int contentIdx, Matcher content_match, String[] lines) {
        String content;
        if (content_match.find()) {
            content = content_match.group(1);
            if (lines.length > (contentIdx + 1)) {
                content = StringUtils.join(Arrays.copyOfRange(lines, contentIdx + 1, lines.length), " ");
            }
        } else {
            content = StringUtils.join(Arrays.copyOfRange(lines, contentIdx, lines.length), " ");
        }
        return content;
    }


    Article parseNoTitleArticle(String[] lines, String publication) {
        int dateIdx = 2;
        int contentIdx = 3;
        //date format 20 January 2014
        Timestamp date = Utils.format_date(lines[dateIdx]);
        String author = "";
        if (null == date) {
            author = lines[dateIdx];
            dateIdx += 1;
            contentIdx += 1;
            date = Utils.format_date(lines[dateIdx]);
        }
        if (contentIdx >= lines.length) {
            System.out.println("Error in length::" + Arrays.toString(lines));
            return null;
        }
        String[] results = getTitlePlaceContent(lines, 2, contentIdx);
        String place = results[1];
        String content = results[2];
        return new Article(publication, "", author, date, place, content);
    }

    Article parseType2Article(String[] lines, String publication) {
        int titleIdx = 1;
        int dateIdx = 2;
        int contentIdx = 3;
        //date format yyyy / mm / dd
        Timestamp date = Utils.format_date(lines[dateIdx]);
        String author = "";
        if (null == date) {
            author = lines[dateIdx];
            dateIdx += 1;
            contentIdx += 1;
            date = Utils.format_date(lines[dateIdx]);
        }

        if (contentIdx >= lines.length) {
            System.out.println("Error in length::" + Arrays.toString(lines));
            return null;
        }
        //search place in entire article
        String[] results = getTitlePlaceContent(lines, 2, contentIdx);
        String title = results[0];
        String place = results[1];
        String content = results[2];
        if (null == date) {
            date = Utils.format_date(lines[titleIdx]);
            if (null != date) {
                title = null;
                author = null;
            } else {
                System.out.println("CANNOT FIND DATE::" + Arrays.toString(lines));
                return null;
            }
        }
        return new Article(publication, title, author, date, place, content);
    }

    String[] getTitlePlaceContent(String[] lines, int search_type, int contentIdx) {
        String title = lines[1];
        String place = "";
        String content;
        Pattern pattern = Pattern.compile("^[A-Za-z]+,(\\s)*(kronos|tethys)(\\s)*(-)?", CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(lines[contentIdx]);
        boolean matchFound = matcher.find();
        if (matchFound) {
            place = matcher.group(0);
            content = lines[contentIdx].substring(place.length());
            if (place.indexOf("-") > 0) {
                place = place.substring(0, place.indexOf("-"));
            }
            content = content.concat(StringUtils.join(Arrays.copyOfRange(lines, contentIdx + 1, lines.length), " "));
            if (search_type == 1) {
                return new String[]{title, place, content};
            }
        } else {
            content = StringUtils.join(Arrays.copyOfRange(lines, contentIdx, lines.length), " ");
            int idx = contentIdx;
            while (idx != lines.length && !matchFound) {
                matcher = pattern.matcher(lines[idx]);
                matchFound = matcher.find();
                idx++;
            }
            if (matchFound) {
                place = matcher.group(0);
                content = lines[idx - 1].substring(place.length());
                if (idx > lines.length)
                    content = content.concat(StringUtils.join(Arrays.copyOfRange(lines, idx + 1, lines.length), " "));
            }
        }
        if (StringUtils.isBlank(place)) {
            String search_str = lines[1].concat(StringUtils.join(Arrays.copyOfRange(lines, contentIdx, lines.length), " "));
            if (search_str.toLowerCase().contains("kronos")) {
                place = "kronos";
            } else if (search_str.toLowerCase().contains("tethys")) {
                place = "tethys";
            }
        }
        return new String[]{title, place, content};
    }


}
