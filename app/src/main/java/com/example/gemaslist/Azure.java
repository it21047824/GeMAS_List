package com.example.gemaslist;

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

    public enum Validity {
        QUERY_SUCCESSFUL,
        USERNAME_INVALID,
        PASSWORD_INVALID,
        QUERY_FAILED,
        ALREADY_IN_USE
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

    public static void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                return Validity.ALREADY_IN_USE;
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
                //register user to save anime data
                PreparedStatement getUid = conn.prepareStatement("SELECT user_id FROM user_accounts " +
                        "WHERE email=?");
                getUid.setString(1, email);

                ResultSet uid = getUid.executeQuery();

                if(uid.next()) {
                    PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO " +
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

    public static Validity addNewAnimeTitle(Connection conn,
                                            Context context,
                                            String title,
                                            String description,
                                            Uri poster,
                                            String episodes,
                                            String romanji
    ) {

        //check if the title already exists
        try {
            PreparedStatement stmt = conn.prepareCall
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
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO anime_titles VALUES(?,?,?,?,?)");
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
            Log.e("Azure", e.getMessage());
        }
        return Validity.QUERY_FAILED;
    }

    public static AnimeTitle getAnimeTitle(Connection conn, int titleID) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT FROM anime_titles WHERE anime_id=?");
            stmt.setInt(1, titleID);

            ResultSet res = stmt.executeQuery();

            if (res.next()){
                return new AnimeTitle(res.getInt(0),
                        res.getString(1),
                        res.getString(2),
                        byteToBitmap(res.getBytes(3)),
                        res.getInt(4),
                        res.getString(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAnimeUserData(Connection conn, int accountID) {
        JSONObject retrievedData;
        JSONArray dataArray;

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT data FROM anime_user_data WHERE user_id=?");
            stmt.setInt(1, accountID);

            ResultSet res = stmt.executeQuery();
            //process stored data
            if(res.next()){
                retrievedData = new JSONObject(res.getString(0));
                dataArray = retrievedData.getJSONArray("DATA");

                AnimeUserData userData = AnimeUserData.getAnimeUserData();
                CustomLinkList watching = userData.getWatchingList();
                CustomLinkList planning = userData.getPlanningList();
                CustomLinkList completed = userData.getCompletedList();

                for (int i=0; i<dataArray.length(); i++) {
                    JSONObject obj = dataArray.getJSONObject(i);
                    int title = obj.getInt("TITLE");
                    int status = obj.getInt("STATUS");
                    int rating = obj.getInt("RATING");
                    int progress = obj.getInt("PROGRESS");

                    switch (status) {
                        case WATCHING:
                            watching.addItem(new AnimeDataEntry(title, status, progress, rating));
                            break;
                        case PLANNING:
                            planning.addItem(new AnimeDataEntry(title, status, progress, rating));
                            break;
                        case COMPLETED:
                            completed.addItem(new AnimeDataEntry(title, status, progress, rating));
                            break;
                    }
                }

            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Validity saveAnimeUserData(Connection conn, int accountID) {
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
                dataArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //get all data from completed list
        for(int i=0; i<completed.size(); i++){
            JSONObject obj = new JSONObject();
            try {
                obj.put("TITLE", completed.getItem(i).title);
                obj.put("STATUS", completed.getItem(i).status);
                obj.put("RATING", completed.getItem(i).rating);
                obj.put("PROGRESS", completed.getItem(i).progress);
                dataArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject finalData = new JSONObject();
        String finalDataString = null;
        try {
            finalData.put("DATA", dataArray);
            finalDataString = finalData.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            PreparedStatement saveData = conn.prepareStatement("UPDATE anime_user_data" +
                    "SET data=? WHERE user_id=?");
            saveData.setString(1, finalDataString);
            saveData.setInt(2, accountID);

            int res = saveData.executeUpdate();

            if(res>0) {
                return Validity.QUERY_SUCCESSFUL;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
