

//Migrated from Azure to Firebase
//This File is Deprecated

package com.RedRose.gemaslist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public static final int WATCHING = 0;
    public static final int PLANNING = 1;
    public static final int COMPLETED = 2;
    public static Connection conn;

    public enum Validity {
        QUERY_SUCCESSFUL,
        USERNAME_INVALID,
        PASSWORD_INVALID,
        QUERY_FAILED,
        ALREADY_IN_USE,
        NULL_ARGS
    }

    private Azure(){}

    public static Connection getConnection() {
        try {
            if(conn==null || conn.isClosed()) {
                synchronized (Azure.class){
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    conn = DriverManager.getConnection(URL);
                    Log.e("Azure", "new conn");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            Log.e("Azure", e.getMessage());
        }
        return conn;
    }

    public static void closeConnection() {
        try {
            conn.close();
            Log.e("Azure", "conn closed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static UserAccount validateUserCreds(String email, String password){

        //to prevent app from crashing
        if(getConnection() == null){
            return new UserAccount(
                    -1,
                    null,
                    null,
                    Validity.QUERY_FAILED
            );
        }

        ResultSet res;
        Validity validity = Validity.NULL_ARGS;
        String resUsername = null, resEmail = null;
        int resUID = 0;

        if(email != null){
            try {
                PreparedStatement stmt = getConnection()
                        .prepareStatement("SELECT * FROM user_accounts WHERE email=?");
                stmt.setString(1, email);
                res = stmt.executeQuery();

                if (res.next()) {
                    String pass = res.getString(3);
                    if (Password.validatePassword(password, pass)) {
                        validity = Validity.QUERY_SUCCESSFUL;
                        resUID = res.getInt(1);
                        resUsername = res.getString(2);
                        resEmail = res.getString(4);
                    } else {
                        validity = Validity.PASSWORD_INVALID;
                    }
                } else {
                    validity = Validity.USERNAME_INVALID;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                validity = Validity.QUERY_FAILED;
            }
        }

        return new UserAccount(
                resUID,
                resUsername,
                resEmail,
                validity
        );
    }

    public static Validity addUserAccount(String username, String email, String password) {
        //to prevent app from crashing
        if(getConnection() == null){
            return Validity.QUERY_FAILED;
        }

        //check if email already in use
        try {
            PreparedStatement checkEmail = getConnection()
                    .prepareStatement("SELECT email from user_accounts WHERE email=?");
            checkEmail.setString(1, "email");
            ResultSet resultEmail = checkEmail.executeQuery();

            if(resultEmail.next()){
                return Validity.ALREADY_IN_USE;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //if email available, continue
        int res;
        String hashedPassword = Password.hashPassword(password);

        try {
            PreparedStatement stmt = getConnection()
                    .prepareStatement("INSERT INTO user_accounts VALUES(?,?,?)");
            stmt.setString(1,username);
            stmt.setString(2,hashedPassword);
            stmt.setString(3,email);
            res = stmt.executeUpdate();

            if(res>0){
                //register user to save anime data
                PreparedStatement getUid = getConnection()
                        .prepareStatement("SELECT user_id FROM user_accounts " +
                        "WHERE email=?");
                getUid.setString(1, email);

                ResultSet uid = getUid.executeQuery();

                if(uid.next()) {
                    PreparedStatement stmt2 = getConnection()
                            .prepareStatement("INSERT INTO " +
                            "anime_user_data(user_id) VALUES(?)");
                    stmt2.setInt(1, uid.getInt(0));
                    int finalResult = stmt2.executeUpdate();

                    if(finalResult > 0){
                        return Validity.QUERY_SUCCESSFUL;
                    }
                }

                return Validity.QUERY_FAILED;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Validity.QUERY_FAILED;
    }

    public static Validity addNewAnimeTitle(Context context,
                                            String title,
                                            String description,
                                            Uri poster,
                                            String episodes,
                                            String romanji
    ) {
        //to prevent app from crashing
        if(getConnection() == null){
            return Validity.QUERY_FAILED;
        }

        //check if the title already exists
        try {
            PreparedStatement stmt = getConnection().prepareCall
                    ("SELECT title FROM anime_titles WHERE title=?");
            stmt.setString(1, title);

            ResultSet res = stmt.executeQuery();

            if(res.next()){
                return Validity.ALREADY_IN_USE;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //if not add the title
        int result;
        try {
            PreparedStatement stmt = getConnection()
                    .prepareStatement("INSERT INTO anime_titles VALUES(?,?,?,?,?)");
            stmt.setString(1, title);
            stmt.setString(2, description);

            byte[] image = uriToBytes(context, poster);
            stmt.setBytes(3, image);
            stmt.setInt(4, Integer.parseInt(episodes));
            stmt.setString(5, (romanji == null ? "" : romanji));

            result = stmt.executeUpdate();

            if(result>0){
                return Validity.QUERY_SUCCESSFUL;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Validity.QUERY_FAILED;
    }

    public static AnimeTitle getAnimeTitle(int titleID) {
        //to prevent app from crashing
        if(getConnection() == null){
            Log.e("Azure", "conn null");
            return null;
        }

        try {
            PreparedStatement stmt = getConnection()
                    .prepareStatement("SELECT * FROM anime_titles WHERE anime_id=?");
            stmt.setInt(1, titleID);

            ResultSet res = stmt.executeQuery();

            if (res.next()){
                return new AnimeTitle(res.getInt(1),
                        res.getString(2),
                        res.getString(3),
                        byteToBitmap(res.getBytes(4)),
                        res.getInt(5),
                        res.getString(6),
                        (float) 8.8);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("Azure", e.getMessage());
        }
        return null;
    }

    public static Validity getAnimeUserData(int accountID) {
        //to prevent app from crashing
        if(getConnection() == null){
            return Validity.QUERY_FAILED;
        }

        JSONObject retrievedData;
        JSONArray dataArray;

        try {
            PreparedStatement stmt = getConnection()
                    .prepareStatement("SELECT user_data FROM anime_user_data WHERE user_id=?");
            stmt.setInt(1, accountID);

            ResultSet res = stmt.executeQuery();

            AnimeUserData userData = AnimeUserData.getAnimeUserData();
            CustomLinkList watching = userData.getWatchingList();
            CustomLinkList planning = userData.getPlanningList();
            CustomLinkList completed = userData.getCompletedList();

            //process stored data
            if(res.next()){

                if(res.getString(1) != null){
                    retrievedData = new JSONObject(res.getString(1));
                    dataArray = retrievedData.getJSONArray("DATA");

                    for (int i=0; i<dataArray.length(); i++) {
                        JSONObject obj = dataArray.getJSONObject(i);
                        int title = obj.getInt("TITLE");
                        int status = obj.getInt("STATUS");
                        int rating = obj.getInt("RATING");
                        int progress = obj.getInt("PROGRESS");
                        boolean favourite = obj.getBoolean("FAVOURITE");

                        switch (status) {
                            case WATCHING:
                                watching.addItem(new AnimeDataEntry(title, status, progress, rating, favourite));
                                break;
                            case PLANNING:
                                planning.addItem(new AnimeDataEntry(title, status, progress, rating, favourite));
                                break;
                            case COMPLETED:
                                completed.addItem(new AnimeDataEntry(title, status, progress, rating, favourite));
                                break;
                        }
                    }
                }

                return Validity.QUERY_SUCCESSFUL;
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }

        return Validity.QUERY_FAILED;
    }

    public static Validity saveAnimeUserData(int accountID) {
        //to prevent app from crashing
        if(getConnection() == null){
            return Validity.QUERY_FAILED;
        }

        //create a JSON string to save
        AnimeUserData currentData = AnimeUserData.getAnimeUserData();
        JSONArray dataArray = new JSONArray();

        CustomLinkList watching = currentData.getWatchingList();
        CustomLinkList planning = currentData.getPlanningList();
        CustomLinkList completed = currentData.getCompletedList();

        //get all data from watching list

        for(int i=0; i<watching.size(); i++){
            JSONObject obj = new JSONObject();
            try {
                obj.put("TITLE", watching.getItem(i).title);
                obj.put("STATUS", watching.getItem(i).status);
                obj.put("RATING", watching.getItem(i).rating);
                obj.put("PROGRESS", watching.getItem(i).progress);
                obj.put("FAVOURITE", watching.getItem(i).favourite);
                dataArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //get all data from planning list

        for(int i=0; i<planning.size(); i++){
            JSONObject obj = new JSONObject();
            try {
                obj.put("TITLE", planning.getItem(i).title);
                obj.put("STATUS", planning.getItem(i).status);
                obj.put("RATING", planning.getItem(i).rating);
                obj.put("PROGRESS", planning.getItem(i).progress);
                obj.put("FAVOURITE", planning.getItem(i).favourite);
                dataArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Azure", "stuck");
            }
        }

        //get all data from completed list
        Log.e("Azure", "c-size "+completed.size());
        for(int i=0; i<completed.size(); i++){
            JSONObject obj = new JSONObject();
            try {
                obj.put("TITLE", completed.getItem(i).title);
                obj.put("STATUS", completed.getItem(i).status);
                obj.put("RATING", completed.getItem(i).rating);
                obj.put("PROGRESS", completed.getItem(i).progress);
                obj.put("FAVOURITE", completed.getItem(i).favourite);
                dataArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Azure", e.getMessage());
            }
        }

        JSONObject finalData = new JSONObject();
        String finalDataString = null;
        try {
            finalData.put("DATA", dataArray);
            finalDataString = finalData.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement saveData = getConnection()
                    .prepareStatement("UPDATE anime_user_data " +
                    "SET user_data=? WHERE user_id=?");
            saveData.setString(1, finalDataString);
            saveData.setInt(2, accountID);

            int res = saveData.executeUpdate();

            if(res>0) {
                return Validity.QUERY_SUCCESSFUL;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("Azure", e.getMessage());
        }

        return Validity.QUERY_FAILED;
    }






    //util methods
    //code snippet from
    //https://colinyeoh.wordpress.com/2012/05/18/android-convert-image-uri-to-byte-array/
    public static byte[] uriToBytes(Context context, Uri uri) {
        InputStream inputStream;
        byte[] data = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            data = outputStream.toByteArray();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    //end of code snippet

    public static Bitmap byteToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static float pxFromDp(Context context, float dip) {
        Resources resources = context.getResources();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                resources.getDisplayMetrics());
    }
}
