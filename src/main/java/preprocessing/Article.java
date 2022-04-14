package preprocessing;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Article {

    private String fileName;
    private String publication;
    private String title;
    private Timestamp date;
    private String place;
    private String content;
    private String[] stems;
    private List<String> keywordsList = new ArrayList<String>();
    private String author;
    private boolean hasDeletedImg;
    private String time;
    private int xCoordinate;
    private int yCoordinate;
    private List<Edge> edges = new ArrayList<>();

    public int getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(int sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    private int sentimentScore;

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public Article(String publication, String title, String author, Timestamp date, String place, String content, boolean hasDeletedImg) {
        this.publication = publication;
        this.title = title;
        this.author = author;
        this.date = date;
        this.place = place;
        this.content = content;
        this.hasDeletedImg = hasDeletedImg;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getStems() {
        return stems;
    }

    public void setStems(String[] stems) {
        this.stems = stems;
    }

    public String getAuthor() {
        return author;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isHasDeletedImg() {
        return hasDeletedImg;
    }

    public void setHasDeletedImg(boolean hasDeletedImg) {
        this.hasDeletedImg = hasDeletedImg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public int getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public List<String> getKeywordsList() {
        List<String> list = new ArrayList<>();
        for (String str : keywordsList){
            list.add(str.toLowerCase(Locale.ROOT));
        }
        keywordsList = list;
        return keywordsList;
    }

    public void setKeywordsList(List<String> keywordsList) {
        this.keywordsList = keywordsList;
    }

    @Override
    public String toString() {
        return "Article{" +
                "fileName='" + fileName + '\'' +
                ", publication='" + publication + '\'' +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", place='" + place + '\'' +
                ", content='" + content + '\'' +
                ", stems=" + Arrays.toString(stems) +
                ", keywordsList=" + keywordsList +
                ", author='" + author + '\'' +
                ", hasDeletedImg=" + hasDeletedImg +
                ", time='" + time + '\'' +
                ", xCoordinate=" + xCoordinate +
                ", yCoordinate=" + yCoordinate +
                '}';
    }
}