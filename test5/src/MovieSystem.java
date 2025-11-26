import java.util.*;

/**
 * Movie system main class, manages the entire application
 */
public class MovieSystem {
    private Map<String, Movie> movies;
    private Map<String, User> users;
    private User currentUser;
    private RecommendationEngine recommendationEngine;
    private Scanner scanner;

    public MovieSystem() {
        this.movies = FileManager.loadMovies();
        this.users = FileManager.loadUsers();
        this.recommendationEngine = new RecommendationEngine(movies);
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
    }

    /**
     * Start the movie system
     */
    public void start() {
        System.out.println("=== Movie Recommendation & Tracker System ===");

        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                showUserMenu();
            }
        }
    }

    /**
     * Show main menu (not logged in)
     */
    private void showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Please choose an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice == 1) {
                login();
            } else if (choice == 2) {
                System.out.println("Thank you for using Movie Recommendation & Tracker System!");
                System.exit(0);
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    /**
     * Show user menu (logged in)
     */
    private void showUserMenu() {
        System.out.println("\n=== User Menu (Logged in as: " + currentUser.getUsername() + ") ===");
        System.out.println("1. Browse movies");
        System.out.println("2. Add movie to watchlist");
        System.out.println("3. Remove movie from watchlist");
        System.out.println("4. View watchlist");
        System.out.println("5. Mark movie as watched");
        System.out.println("6. View history");
        System.out.println("7. Get recommendations");
        System.out.println("8. View recommendation strategies");
        System.out.println("9. Logout");
        System.out.print("Please choose an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            if (choice == 1) {
                browseMovies();
            } else if (choice == 2) {
                addToWatchlist();
            } else if (choice == 3) {
                removeFromWatchlist();
            } else if (choice == 4) {
                viewWatchlist();
            } else if (choice == 5) {
                markAsWatched();
            } else if (choice == 6) {
                viewHistory();
            } else if (choice == 7) {
                getRecommendations();
            } else if (choice == 8) {
                viewRecommendationStrategies();
            } else if (choice == 9) {
                logout();
            } else {
                System.out.println("Invalid option. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    /**
     * User login
     */
    private void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            System.out.println("Login successful! Welcome, " + username + "!");
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    /**
     * User logout
     */
    private void logout() {
        // Save user data
        FileManager.saveUsers(users);
        System.out.println("Goodbye, " + currentUser.getUsername() + "!");
        currentUser = null;
    }

    /**
     * Browse all movies - output according to actual number of movies in CSV file
     */
    private void browseMovies() {
        System.out.println("\n=== All Movies ===");

        int movieCount = 0;
        for (int i = 1; i <= 100; i++) {
            String movieId = "M" + String.format("%03d", i);
            Movie movie = movies.get(movieId);
            if (movie != null) {
                System.out.println(movie);
                movieCount++;
            }
        }

        System.out.println("Total movies: " + movieCount);
    }

    /**
     * Add movie to watchlist
     */
    private void addToWatchlist() {
        System.out.print("Enter movie ID to add to watchlist: ");
        String movieId = scanner.nextLine().trim().toUpperCase();

        if (!movies.containsKey(movieId)) {
            System.out.println("Movie ID not found.");
            return;
        }

        if (currentUser.getWatchlist().addMovie(movieId)) {
            System.out.println("Movie added to watchlist successfully.");
            // Update user data
            users.put(currentUser.getUsername(), currentUser);
            // Save to file
            FileManager.saveUsers(users);
        } else {
            System.out.println("Movie is already in your watchlist.");
        }
    }

    /**
     * Remove movie from watchlist
     */
    private void removeFromWatchlist() {
        System.out.print("Enter movie ID to remove from watchlist: ");
        String movieId = scanner.nextLine().trim().toUpperCase();

        if (currentUser.getWatchlist().removeMovie(movieId)) {
            System.out.println("Movie removed from watchlist successfully.");
            // Update user data
            users.put(currentUser.getUsername(), currentUser);
            // Save to file
            FileManager.saveUsers(users);
        } else {
            System.out.println("Movie not found in your watchlist.");
        }
    }

    /**
     * View watchlist
     */
    private void viewWatchlist() {
        System.out.println("\n=== Your Watchlist ===");
        Watchlist watchlist = currentUser.getWatchlist();

        if (watchlist.isEmpty()) {
            System.out.println("Your watchlist is empty.");
            return;
        }

        for (String movieId : watchlist.getMovieIds()) {
            Movie movie = movies.get(movieId);
            if (movie != null) {
                System.out.println(movie);
            }
        }
    }

    /**
     * Mark movie as watched
     */
    private void markAsWatched() {
        System.out.print("Enter movie ID to mark as watched: ");
        String movieId = scanner.nextLine().trim().toUpperCase();

        if (!movies.containsKey(movieId)) {
            System.out.println("Movie ID not found.");
            return;
        }

        if (currentUser.getHistory().addMovie(movieId)) {
            // If movie is in watchlist, remove it
            currentUser.getWatchlist().removeMovie(movieId);
            System.out.println("Movie marked as watched successfully.");
            // Update user data
            users.put(currentUser.getUsername(), currentUser);
            // Save to file
            FileManager.saveUsers(users);
        } else {
            System.out.println("Movie is already in your history.");
        }
    }

    /**
     * View watch history
     */
    private void viewHistory() {
        System.out.println("\n=== Your Viewing History ===");
        History history = currentUser.getHistory();

        if (history.isEmpty()) {
            System.out.println("You haven't watched any movies yet.");
            return;
        }

        for (String movieId : history.getMovieIds()) {
            Movie movie = movies.get(movieId);
            if (movie != null) {
                System.out.println(movie);
            }
        }
    }

    /**
     * Get movie recommendations (supports multiple strategies)
     */
    private void getRecommendations() {
        // Let user choose recommendation strategy
        System.out.println("\n=== Choose Recommendation Strategy ===");
        System.out.println("1. Genre-based (Your favorite genres)");
        System.out.println("2. Rating-based (Highest rated movies)");
        System.out.println("3. Year-based (Most recent movies)");
        System.out.print("Please choose a strategy (1-3, default 1): ");

        String strategyChoice = scanner.nextLine().trim();
        String strategyKey = "genre"; // Default to genre strategy

        if (!strategyChoice.isEmpty()) {
            switch (strategyChoice) {
                case "1": strategyKey = "genre"; break;
                case "2": strategyKey = "rating"; break;
                case "3": strategyKey = "year"; break;
                default:
                    System.out.println("Invalid choice. Using genre-based strategy.");
                    strategyKey = "genre";
            }
        }

        // Set recommendation strategy
        recommendationEngine.setCurrentStrategy(strategyKey);

        System.out.print("Enter number of recommendations (default 5): ");
        String input = scanner.nextLine().trim();
        int topN = 5;

        try {
            if (!input.isEmpty()) {
                topN = Integer.parseInt(input);
                if (topN <= 0) topN = 5;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Using default value 5.");
        }

        List<Movie> recommendations = recommendationEngine.getRecommendations(currentUser, topN);

        System.out.println("\n=== " + recommendationEngine.getCurrentStrategyName() + " ===");
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations available.");
        } else {
            for (int i = 0; i < recommendations.size(); i++) {
                System.out.println((i + 1) + ". " + recommendations.get(i));
            }
        }
    }

    /**
     * View all available recommendation strategies
     */
    private void viewRecommendationStrategies() {
        System.out.println("\n=== Available Recommendation Strategies ===");
        List<String> strategies = recommendationEngine.getAvailableStrategies();

        for (int i = 0; i < strategies.size(); i++) {
            String strategyKey = strategies.get(i);
            String displayName = recommendationEngine.getStrategyDisplayName(strategyKey);
            String description = recommendationEngine.getStrategyDescription(strategyKey);

            System.out.println((i + 1) + ". " + displayName);
            System.out.println("   " + description);
            System.out.println();
        }

        System.out.println("Current strategy: " + recommendationEngine.getCurrentStrategyName());
    }
}