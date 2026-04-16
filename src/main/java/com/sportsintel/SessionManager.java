package com.sportsintel;

public class SessionManager {

    private static boolean loggedIn = false;
    private static String fullName = "";
    private static String username = "";
    private static SearchResult latestSearch;

    private SessionManager() {
    }

    public static void login(String fullNameValue, String usernameValue) {
        loggedIn = true;
        fullName = fullNameValue;
        username = usernameValue;
    }

    public static void logout() {
        loggedIn = false;
        fullName = "";
        username = "";
        latestSearch = null;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static String getFullName() {
        return fullName;
    }

    public static String getUsername() {
        return username;
    }

    public static void setLatestSearch(SearchResult searchResult) {
        latestSearch = searchResult;
    }

    public static SearchResult getLatestSearch() {
        return latestSearch;
    }

    public record SearchResult(
            String player,
            String opponent,
            String stat,
            String season,
            String seasonType,
            String location,
            String lastN,
            int gamesPlayed,
            double average,
            double high,
            double low
    ) {
    }
}
