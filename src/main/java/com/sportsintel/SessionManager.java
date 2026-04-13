package com.sportsintel;

public class SessionManager {

    private static boolean loggedIn = false;
    private static String fullName = "";
    private static String username = "";

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
}
