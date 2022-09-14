package com.RedRose.gemaslist;

public class UserAccount {
    protected int userID;
    protected String username;
    protected String email;
    protected Azure.Validity result;

    public UserAccount(int userID, String username, String email, Azure.Validity result) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.result = result;
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Azure.Validity getResult() {
        return result;
    }
}
