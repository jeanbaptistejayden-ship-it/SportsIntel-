package com.sportsintel;

import java.util.ArrayList;

public class User {
    private String username;
    private String fullName;
    private String password;
    private String email;
    private ArrayList<ArrayList<String>> userHistory;
    private int trackSearchCount = 0;
    //private ArrayList<ArrayList<String>> userSearchHistory;


    public User(String username, String fullName, String email, String password /*,ArrayList<ArrayList<String>> userSearchHistory*/) {
        this.username = username;
       this.fullName = fullName;
        this.email = email;
        this.password = password;
        //this.userSearchHistory = userSearchHistory;

    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setTrackSearchCount(int trackSearchCount) {
        this.trackSearchCount = trackSearchCount;
    }

    public int getTrackSearchCount() {
        return trackSearchCount;
    }

    /*public ArrayList<ArrayList<String>> getUserSearchHistory() {
        return userSearchHistory;
    }*/
}
