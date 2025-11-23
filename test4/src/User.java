/**
 * 用户类，管理用户信息和关联的Watchlist、History对象
 */
public class User {
    private String username;
    private String password;
    private Watchlist watchlist;
    private History history;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.watchlist = new Watchlist();
        this.history = new History();
    }

    public User(String username, String password, Watchlist watchlist, History history) {
        this.username = username;
        this.password = password;
        this.watchlist = watchlist != null ? watchlist : new Watchlist();
        this.history = history != null ? history : new History();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public History getHistory() {
        return history;
    }
}