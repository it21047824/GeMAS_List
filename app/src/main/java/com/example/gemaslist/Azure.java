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
        USER_VALID,
        USERNAME_INVALID,
        PASSWORD_INVALID,
        QUERY_FAILED
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

    public static Validity validateUser(Connection conn, String username, String password){

        ResultSet res;
        Validity validity = Validity.PASSWORD_INVALID;

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_accounts WHERE user_name=?");
            stmt.setString(1, username);
            res = stmt.executeQuery();

            if(res.next()){
                String pass = res.getString(3);
                if(pass.equals(password)){
                    validity = Validity.USER_VALID;
                }
            } else {
                validity = Validity.USERNAME_INVALID;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            validity = Validity.QUERY_FAILED;
        }


        return validity;
    }

}
