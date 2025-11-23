import java.util.*;

/**
 * 推荐引擎类，根据用户偏好推荐电影，支持多种策略
 */
public class RecommendationEngine {
    private Map<String, Movie> movies;
    private String currentStrategy;

    public RecommendationEngine(Map<String, Movie> movies) {
        this.movies = movies;
        this.currentStrategy = "hybrid"; // 默认使用混合策略
    }

    /**
     * 获取推荐电影列表
     */
    public List<Movie> getRecommendations(User user, int topN) {
        switch (currentStrategy) {
            case "genre":
                return getGenreBasedRecommendations(user, topN);
            case "rating":
                return getRatingBasedRecommendations(user, topN);
            case "year":
                return getYearBasedRecommendations(user, topN);
            case "hybrid":
            default:
                return getHybridRecommendations(user, topN);
        }
    }

    /**
     * 设置当前推荐策略
     */
    public void setCurrentStrategy(String strategy) {
        if (strategy.equals("genre") || strategy.equals("rating") ||
                strategy.equals("year") || strategy.equals("hybrid")) {
            this.currentStrategy = strategy;
        }
    }

    /**
     * 获取当前策略名称
     */
    public String getCurrentStrategyName() {
        switch (currentStrategy) {
            case "genre": return "Genre-Based Recommendation";
            case "rating": return "Rating-Based Recommendation";
            case "year": return "Year-Based Recommendation";
            case "hybrid": return "Hybrid Recommendation";
            default: return "Unknown Strategy";
        }
    }

    /**
     * 获取策略描述
     */
    public String getStrategyDescription(String strategy) {
        switch (strategy) {
            case "genre": return "Recommends movies based on your favorite genres from watch history and watchlist";
            case "rating": return "Recommends highest rated movies you haven't watched yet";
            case "year": return "Recommends the most recent movies you haven't watched yet";
            case "hybrid": return "Recommends movies based on your favorite genres and highest ratings";
            default: return "Unknown strategy";
        }
    }

    /**
     * 获取所有可用的策略列表
     */
    public List<String> getAvailableStrategies() {
        List<String> strategies = new ArrayList<>();
        strategies.add("genre");
        strategies.add("rating");
        strategies.add("year");
        strategies.add("hybrid");
        return strategies;
    }

    /**
     * 获取策略显示名称
     */
    public String getStrategyDisplayName(String strategy) {
        switch (strategy) {
            case "genre": return "Genre-Based Recommendation";
            case "rating": return "Rating-Based Recommendation";
            case "year": return "Year-Based Recommendation";
            case "hybrid": return "Hybrid Recommendation";
            default: return "Unknown";
        }
    }

    /**
     * 基于类型的推荐策略
     */
    private List<Movie> getGenreBasedRecommendations(User user, int topN) {
        if (user.getHistory().isEmpty() && user.getWatchlist().isEmpty()) {
            return getTopRatedMovies(topN);
        }

        Map<String, Integer> genreCounts = getUserFavoriteGenres(user);

        if (genreCounts.isEmpty()) {
            return getTopRatedMovies(topN);
        }

        // 获取用户已经观看或计划观看的电影ID
        Set<String> userMovieIds = new HashSet<>();
        userMovieIds.addAll(user.getHistory().getMovieIds());
        userMovieIds.addAll(user.getWatchlist().getMovieIds());

        // 创建候选电影列表
        List<Movie> candidateMovies = new ArrayList<>();
        for (Movie movie : movies.values()) {
            if (!userMovieIds.contains(movie.getId())) {
                candidateMovies.add(movie);
            }
        }

        // 使用简单的冒泡排序根据用户偏好排序
        sortMoviesByPreference(candidateMovies, genreCounts);

        // 返回前topN个推荐
        List<Movie> recommendations = new ArrayList<>();
        int count = Math.min(topN, candidateMovies.size());
        for (int i = 0; i < count; i++) {
            recommendations.add(candidateMovies.get(i));
        }

        return recommendations;
    }

    /**
     * 基于评分的推荐策略
     */
    private List<Movie> getRatingBasedRecommendations(User user, int topN) {
        List<Movie> allMovies = new ArrayList<>(movies.values());

        // 排除用户已经观看或计划观看的电影
        Set<String> userMovieIds = new HashSet<>();
        userMovieIds.addAll(user.getHistory().getMovieIds());
        userMovieIds.addAll(user.getWatchlist().getMovieIds());

        List<Movie> candidateMovies = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (!userMovieIds.contains(movie.getId())) {
                candidateMovies.add(movie);
            }
        }

        // 使用冒泡排序按评分排序
        sortMoviesByRating(candidateMovies);

        // 返回前topN个推荐
        List<Movie> recommendations = new ArrayList<>();
        int count = Math.min(topN, candidateMovies.size());
        for (int i = 0; i < count; i++) {
            recommendations.add(candidateMovies.get(i));
        }

        return recommendations;
    }

    /**
     * 基于年份的推荐策略
     */
    private List<Movie> getYearBasedRecommendations(User user, int topN) {
        List<Movie> allMovies = new ArrayList<>(movies.values());

        // 排除用户已经观看或计划观看的电影
        Set<String> userMovieIds = new HashSet<>();
        userMovieIds.addAll(user.getHistory().getMovieIds());
        userMovieIds.addAll(user.getWatchlist().getMovieIds());

        List<Movie> candidateMovies = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (!userMovieIds.contains(movie.getId())) {
                candidateMovies.add(movie);
            }
        }

        // 使用冒泡排序按年份排序（最新的在前）
        sortMoviesByYear(candidateMovies);

        // 返回前topN个推荐
        List<Movie> recommendations = new ArrayList<>();
        int count = Math.min(topN, candidateMovies.size());
        for (int i = 0; i < count; i++) {
            recommendations.add(candidateMovies.get(i));
        }

        return recommendations;
    }

    /**
     * 混合推荐策略（结合类型和评分）
     */
    private List<Movie> getHybridRecommendations(User user, int topN) {
        if (user.getHistory().isEmpty() && user.getWatchlist().isEmpty()) {
            return getTopRatedMovies(topN);
        }

        Map<String, Integer> genreCounts = getUserFavoriteGenres(user);

        if (genreCounts.isEmpty()) {
            return getTopRatedMovies(topN);
        }

        // 获取用户已经观看或计划观看的电影ID
        Set<String> userMovieIds = new HashSet<>();
        userMovieIds.addAll(user.getHistory().getMovieIds());
        userMovieIds.addAll(user.getWatchlist().getMovieIds());

        // 创建候选电影列表
        List<Movie> candidateMovies = new ArrayList<>();
        for (Movie movie : movies.values()) {
            if (!userMovieIds.contains(movie.getId())) {
                candidateMovies.add(movie);
            }
        }

        // 混合排序：先按类型匹配度，再按评分
        sortMoviesHybrid(candidateMovies, genreCounts);

        // 返回前topN个推荐
        List<Movie> recommendations = new ArrayList<>();
        int count = Math.min(topN, candidateMovies.size());
        for (int i = 0; i < count; i++) {
            recommendations.add(candidateMovies.get(i));
        }

        return recommendations;
    }

    /**
     * 使用冒泡排序根据用户偏好排序电影
     */
    private void sortMoviesByPreference(List<Movie> movies, Map<String, Integer> genreCounts) {
        int n = movies.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                Movie m1 = movies.get(j);
                Movie m2 = movies.get(j + 1);

                int genreScore1 = genreCounts.containsKey(m1.getGenre()) ? genreCounts.get(m1.getGenre()) : 0;
                int genreScore2 = genreCounts.containsKey(m2.getGenre()) ? genreCounts.get(m2.getGenre()) : 0;

                // 先按类型匹配度排序
                if (genreScore1 < genreScore2) {
                    // 交换位置
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                }
                // 如果类型匹配度相同，按评分排序
                else if (genreScore1 == genreScore2 && m1.getRating() < m2.getRating()) {
                    // 交换位置
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * 混合排序：先按类型匹配度，再按评分
     */
    private void sortMoviesHybrid(List<Movie> movies, Map<String, Integer> genreCounts) {
        int n = movies.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                Movie m1 = movies.get(j);
                Movie m2 = movies.get(j + 1);

                int genreScore1 = genreCounts.containsKey(m1.getGenre()) ? genreCounts.get(m1.getGenre()) : 0;
                int genreScore2 = genreCounts.containsKey(m2.getGenre()) ? genreCounts.get(m2.getGenre()) : 0;

                // 主要按类型匹配度，次要按评分
                if (genreScore1 < genreScore2) {
                    // 交换位置
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                } else if (genreScore1 == genreScore2) {
                    // 类型匹配度相同时，按评分排序
                    if (m1.getRating() < m2.getRating()) {
                        // 交换位置
                        Movie temp = movies.get(j);
                        movies.set(j, movies.get(j + 1));
                        movies.set(j + 1, temp);
                    }
                }
            }
        }
    }

    /**
     * 使用冒泡排序按评分排序电影
     */
    private void sortMoviesByRating(List<Movie> movies) {
        int n = movies.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                Movie m1 = movies.get(j);
                Movie m2 = movies.get(j + 1);

                if (m1.getRating() < m2.getRating()) {
                    // 交换位置
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * 使用冒泡排序按年份排序电影
     */
    private void sortMoviesByYear(List<Movie> movies) {
        int n = movies.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                Movie m1 = movies.get(j);
                Movie m2 = movies.get(j + 1);

                if (m1.getYear() < m2.getYear()) {
                    // 交换位置
                    Movie temp = movies.get(j);
                    movies.set(j, movies.get(j + 1));
                    movies.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * 获取用户最喜欢的电影类型
     */
    private Map<String, Integer> getUserFavoriteGenres(User user) {
        Map<String, Integer> genreCounts = new HashMap<>();

        // 从观看历史中统计类型
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

        // 从观看列表中统计类型
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
     * 获取评分最高的电影
     */
    private List<Movie> getTopRatedMovies(int topN) {
        List<Movie> allMovies = new ArrayList<>(movies.values());

        // 使用冒泡排序按评分排序
        sortMoviesByRating(allMovies);

        // 返回前topN个电影
        List<Movie> topMovies = new ArrayList<>();
        int count = Math.min(topN, allMovies.size());
        for (int i = 0; i < count; i++) {
            topMovies.add(allMovies.get(i));
        }

        return topMovies;
    }
}