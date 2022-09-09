package com.example.gemaslist;

import android.annotation.SuppressLint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Azure {
    @SuppressLint("AuthLeak")
    private static final String URL = "jdbc:jtds:sqlserver://gemas-list-server.database.windows.net:1433/" +
            "gemas-list-db;" +
            "user=appAdmin@gemas-list-server;" +
            "password=AdminPassword@1234;" +
            "encrypt=true;" +
            "trustServerCertificate=false;" +
            "hostNameInCertificate=*.database.windows.net;" +
            "loginTimeout=30;" +
            "ssl=require";

    public enum Validity {
        QUERY_SUCCESSFUL,
        USERNAME_INVALID,
        PASSWORD_INVALID,
        QUERY_FAILED,
        EMAIL_ALREADY_IN_USE
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static UserAccount validateUser(Connection conn, String email, String password){

        ResultSet res;
        Validity validity = Validity.PASSWORD_INVALID;
        String resUID = null, resUsername = null, resEmail = null;

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_accounts WHERE email=?");
            stmt.setString(1, email);
            res = stmt.executeQuery();

            if(res.next()){
                String pass = res.getString(3);
                if(Password.validatePassword(password, pass)){
                    validity = Validity.QUERY_SUCCESSFUL;
                    resUID = res.getString(1);
                    resUsername = res.getString(2);
                    resEmail = res.getString(4);
                }
            } else {
                validity = Validity.USERNAME_INVALID;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            validity = Validity.QUERY_FAILED;
        }

        return new UserAccount(
                resUID,
                resUsername,
                resEmail,
                validity
        );
    }

    public static Validity addUserAccount(Connection conn, String username, String email, String password) {


        //check if email already in use
        try {
            PreparedStatement checkEmail = conn.prepareStatement("SELECT email from user_accounts WHERE email=?");
            checkEmail.setString(1, "email");
            ResultSet resultEmail = checkEmail.executeQuery();

            if(resultEmail.next()){
                return Validity.EMAIL_ALREADY_IN_USE;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //if email available, continue
        int res;
        String hashedPassword = Password.hashPassword(password);

        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO user_accounts VALUES(?,?,?)");
            stmt.setString(1,username);
            stmt.setString(2,hashedPassword);
            stmt.setString(3,email);
            res = stmt.executeUpdate();

            if(res>0){
                return Validity.QUERY_SUCCESSFUL;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Validity.QUERY_FAILED;
    }

}
