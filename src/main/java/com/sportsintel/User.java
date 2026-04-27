package com.sportsintel;

public class User {
    private String username;
    private String fullName;
private String password;
    private String email;



    public User(String username, String fullName, String email, String password) {
        this.username = username;
       this.fullName = fullName;
        this.email = email;
        this.password = password;
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
}
