import java.util.*;

/**
 * 观看历史类，管理用户已观看的电影
 */
public class History {
    private List<String> movieIds;

    public History() {
        this.movieIds = new ArrayList<>();
    }

    public boolean addMovie(String movieId) {
        if (!movieIds.contains(movieId)) {
            movieIds.add(movieId);
            return true;
        }
        return false;
    }

    public List<String> getMovieIds() {
        return new ArrayList<>(movieIds);
    }

    public boolean isEmpty() {
        return movieIds.isEmpty();
    }
}