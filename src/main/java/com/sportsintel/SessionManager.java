package com.sportsintel;

public class SessionManager {

    private static boolean loggedIn = false;
    private static String fullName = "";
    private static String username = "";
    private static SearchResult latestSearch;
    private static CompareResult latestCompare;

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
        latestCompare = null;
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

    public static void setLatestCompare(CompareResult compareResult) {
        latestCompare = compareResult;
    }

    public static CompareResult getLatestCompare() {
        return latestCompare;
    }

    public record SearchResult(
            String player,
            String playerImageUrl,
            String opponent,
            String stat,
            String season,
            String seasonType,
            String location,
            String lastN,
            int gamesPlayed,
            double average,
            double high,
            double low,
            double seasonBaseline,
            double lastFiveAverage,
            double lastTenAverage,
            double fieldGoalPct,
            double minutes,
            double assists,
            double rebounds,
            double turnovers,
            double homeAverage,
            double awayAverage,
            String bestGameOpponent,
            double bestGameValue,
            String toughestGameOpponent,
            double toughestGameValue
    ) {
    }

    public record CompareResult(
            String playerOne,
            String playerTwo,
            String opponent,
            double playerOnePoints,
            double playerTwoPoints,
            double playerOneAssists,
            double playerTwoAssists,
            double playerOneRebounds,
            double playerTwoRebounds,
            double playerOneFgPct,
            double playerTwoFgPct,
            double playerOneMinutes,
            double playerTwoMinutes,
            int playerOneGames,
            int playerTwoGames
    ) {
    }
}
