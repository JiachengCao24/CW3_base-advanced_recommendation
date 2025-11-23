/**
 * 电影类，表示电影的基本信息
 */
public class Movie {
    private String id;
    private String title;
    private String genre;
    private int year;
    private double rating;

    public Movie(String id, String title, String genre, int year, double rating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return id + " - " + title + " (" + year + ") [" + genre + "] Rating: " + rating;
    }
}