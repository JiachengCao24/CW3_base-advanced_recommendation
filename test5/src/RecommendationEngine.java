import java.util.*;

/**
 * Recommendation engine class, recommends movies based on user preferences with multiple strategies
 */
public class RecommendationEngine {
    private Map<String, Movie> movies;
    private String currentStrategy;

    public RecommendationEngine(Map<String, Movie> movies) {
        this.movies = movies;
        this.currentStrategy = "genre"; // Default to genre strategy
    }

    /**
     * Get recommended movie list
     */
    public List<Movie> getRecommendations(User user, int topN) {
        switch (currentStrategy) {
            case "genre":
                return getGenreBasedRecommendations(user, topN);
            case "rating":
                return getRatingBasedRecommendations(user, topN);
            case "year":
                return getYearBasedRecommendations(user, topN);
        }
        // This should never happen due to setCurrentStrategy validation
        return getGenreBasedRecommendations(user, topN);
    }

    /**
     * Set current recommendation strategy
     */
    public void setCurrentStrategy(String strategy) {
        if (strategy.equals("genre") || strategy.equals("rating") || strategy.equals("year")) {
            this.currentStrategy = strategy;
        }
        // Silently ignore invalid strategies
    }

    /**
     * Get current strategy name
     */
    public String getCurrentStrategyName() {
        switch (currentStrategy) {
            case "genre": return "Genre-Based Recommendation";
            case "rating": return "Rating-Based Recommendation";
            case "year": return "Year-Based Recommendation";
        }
        return "Genre-Based Recommendation"; // Fallback
    }

    /**
     * Get strategy description
     */
    public String getStrategyDescription(String strategy) {
        switch (strategy) {
            case "genre": return "Recommends movies based on your favorite genres from watch history and watchlist";
            case "rating": return "Recommends highest rated movies you haven't watched yet";
            case "year": return "Recommends the most recent movies you haven't watched yet";
        }
        return "Unknown strategy"; // Fallback
    }

    /**
     * Get all available strategy list
     */
    public List<String> getAvailableStrategies() {
        List<String> strategies = new ArrayList<>();
        strategies.add("genre");
        strategies.add("rating");
        strategies.add("year");
        return strategies;
    }

    /**
     * Get strategy display name
     */
    public String getStrategyDisplayName(String strategy) {
        switch (strategy) {
            case "genre": return "Genre-Based Recommendation";
            case "rating": return "Rating-Based Recommendation";
            case "year": return "Year-Based Recommendation";
        }
        return "Unknown"; // Fallback
    }

    /**
     * Genre-based recommendation strategy
     */
    private List<Movie> getGenreBasedRecommendations(User user, int topN) {
        if (user.getHistory().isEmpty() && user.getWatchlist().isEmpty()) {
            return getTopRatedMovies(topN);
        }

        Map<String, Integer> genreCounts = getUserFavoriteGenres(user);

        if (genreCounts.isEmpty()) {
            return getTopRatedMovies(topN);
        }

        // Get movie IDs that user has already watched or plans to watch
        Set<String> userMovieIds = new HashSet<>();
        userMovieIds.addAll(user.getHistory().getMovieIds());
        userMovieIds.addAll(user.getWatchlist().getMovieIds());

        // Create candidate movie list
        List<Movie> candidateMovies = new ArrayList<>();
        for (Movie movie : movies.values()) {
            if (!userMovieIds.contains(movie.getId())) {
                candidateMovies.add(movie);
            }
        }

        // Sort using bubble sort based on user preferences
        sortMoviesByPreference(candidateMovies, genreCounts);

        // Return topN recommendations
        List<Movie> recommendations = new ArrayList<>();
        int count = Math.min(topN, candidateMovies.size());
        for (int i = 0; i < count; i++) {
            recommendations.add(candidateMovies.get(i));
        }

        return recommendations;
    }

    /**
     * Rating-based recommendation strategy
     */
    private List<Movie> getRatingBasedRecommendations(User user, int topN) {
        List<Movie> allMovies = new ArrayList<>(movies.values());

        // Exclude movies user has already watched or plans to watch
        Set<String> userMovieIds = new HashSet<>();
        userMovieIds.addAll(user.getHistory().getMovieIds());
        userMovieIds.addAll(user.getWatchlist().getMovieIds());

        List<Movie> candidateMovies = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (!userMovieIds.contains(movie.getId())) {
                candidateMovies.add(movie);
            }
        }

        // Sort by rating using bubble sort
        sortMoviesByRating(candidateMovies);

        // Return topN recommendations
        List<Movie> recommendations = new ArrayList<>();
        int count = Math.min(topN, candidateMovies.size());
        for (int i = 0; i < count; i++) {
            recommendations.add(candidateMovies.get(i));
        }

        return recommendations;
    }

    /**
     * Year-based recommendation strategy
     */
    private List<Movie> getYearBasedRecommendations(User user, int topN) {
        List<Movie> allMovies = new ArrayList<>(movies.values());

        // Exclude movies user has already watched or plans to watch
        Set<String> userMovieIds = new HashSet<>();
        userMovieIds.addAll(user.getHistory().getMovieIds());
        userMovieIds.addAll(user.getWatchlist().getMovieIds());

        List<Movie> candidateMovies = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (!userMovieIds.contains(movie.getId())) {
                candidateMovies.add(movie);
            }
        }

        // Sort by year using bubble sort (newest first)
        sortMoviesByYear(candidateMovies);

        // Return topN recommendations
        List<Movie> recommendations = new ArrayList<>();
        int count = Math.min(topN, candidateMovies.size());
        for (int i = 0; i < count; i++) {
            recommendations.add(candidateMovies.get(i));
        }

        return recommendations;
    }

    /**
     * Sort movies by user preference using bubble sort
     */
    private void sortMoviesByPreference(List<Movie> movies, Map<String, Integer> genreCounts) {
        int n = movies.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                Movie m1 = movies.get(j);
                Movie m2 = movies.get(j + 1);

                int genreScore1 = genreCounts.containsKey(m1.getGenre()) ? genreCounts.get(m1.getGenre()) : 0;
                int genreScore2 = genreCounts.containsKey(m2.getGenre()) ? genreCounts.get(m2.getGenre()) : 0;

                // Sort by genre match first
                if (genreScore1 < genreScore2) {
                    // Swap positions
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                }
                // If genre match is equal, sort by rating
                else if (genreScore1 == genreScore2 && m1.getRating() < m2.getRating()) {
                    // Swap positions
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * Sort movies by rating using bubble sort
     */
    private void sortMoviesByRating(List<Movie> movies) {
        int n = movies.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                Movie m1 = movies.get(j);
                Movie m2 = movies.get(j + 1);

                if (m1.getRating() < m2.getRating()) {
                    // Swap positions
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * Sort movies by year using bubble sort
     */
    private void sortMoviesByYear(List<Movie> movies) {
        int n = movies.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                Movie m1 = movies.get(j);
                Movie m2 = movies.get(j + 1);

                if (m1.getYear() < m2.getYear()) {
                    // Swap positions
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * Get user's favorite movie genres
     */
    private Map<String, Integer> getUserFavoriteGenres(User user) {
        Map<String, Integer> genreCounts = new HashMap<>();

        // Count genres from watch history
        for (String movieId : user.getHistory().getMovieIds()) {
            Movie movie = movies.get(movieId);
            if (movie != null) {
                String genre = movie.getGenre();
                if (genreCounts.containsKey(genre)) {
                    genreCounts.put(genre, genreCounts.get(genre) + 1);
                } else {
                    genreCounts.put(genre, 1);
                }
            }
        }

        // Count genres from watchlist
        for (String movieId : user.getWatchlist().getMovieIds()) {
            Movie movie = movies.get(movieId);
            if (movie != null) {
                String genre = movie.getGenre();
                if (genreCounts.containsKey(genre)) {
                    genreCounts.put(genre, genreCounts.get(genre) + 1);
                } else {
                    genreCounts.put(genre, 1);
                }
            }
        }

        return genreCounts;
    }

    /**
     * Get top rated movies
     */
    private List<Movie> getTopRatedMovies(int topN) {
        List<Movie> allMovies = new ArrayList<>(movies.values());

        // Sort by rating using bubble sort
        sortMoviesByRating(allMovies);

        // Return topN movies
        List<Movie> topMovies = new ArrayList<>();
        int count = Math.min(topN, allMovies.size());
        for (int i = 0; i < count; i++) {
            topMovies.add(allMovies.get(i));
        }

        return topMovies;
    }
}