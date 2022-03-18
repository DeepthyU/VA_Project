package preprocessing;

import java.sql.Timestamp;
import java.util.Arrays;

public class Article{

    private String fileName;
    private String publication;
    private String title;
    private Timestamp date;
    private String place;
    private String content;
    private String[] stems;
    private String[] keyword_count_arr;
    private String author;
    private boolean hasDeletedImg;
    private String time;

    private int yCoordinate;


    public Article(String publication, String title, String author, Timestamp date, String place, String content, boolean hasDeletedImg)
    {
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

    public String[] getKeyword_count_arr() {
        return keyword_count_arr;
    }

    public void setKeyword_count_arr(String[] keyword_count_arr) {
        this.keyword_count_arr = keyword_count_arr;
    }

    public String getAuthor() {
        return author;
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
                ", keyword_count_arr=" + Arrays.toString(keyword_count_arr) +
                ", author='" + author + '\'' +
                ", hasDeletedImg=" + hasDeletedImg +
                ", time='" + time + '\'' +
                ", yCoordinate=" + yCoordinate +
                '}';
    }

}