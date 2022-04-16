package scatterplot;

public class ArticleData {
    private final String title;
    private final String publication;
    private final int publicationId;
    private final String date;
    private final String filename;

    public ArticleData(String title, String publication, int publicationId, String date, String filename) {
        this.title = title;
        this.publication = publication;
        this.publicationId = publicationId;
        this.date = date;
        this.filename = filename;
    }

    public String getTitle() {
        return title;
    }

    public String getPublication() {
        return publication;
    }

    public int getPublicationId() {
        return publicationId;
    }

    public String getDate() {
        return date;
    }

    public String getFilename() {
        return filename;
    }
}
