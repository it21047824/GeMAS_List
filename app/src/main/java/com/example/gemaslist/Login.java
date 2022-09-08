package com.example.gemaslist;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.sql.Connection;

public class Login extends AppCompatActivity {

    private TextInputEditText username;
    private TextInputEditText password;
    private SharedPreferences sp;
    protected MaterialTextView error;
    protected Login login;
    protected LinearProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
        error = findViewById(R.id.login_error);
        progressIndicator = findViewById(R.id.login_loading_indicator);
        sp = getSharedPreferences(getString(R.string.login), MODE_PRIVATE);
        login = Login.this;

        //start main activity if already logged in
        if(sp.getBoolean(getString(R.string.login), false)){
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }

        //set credentials from previous login
        username.setText(sp.getString(getString(R.string.username), null));
        password.setText(sp.getString(getString(R.string.password), null));

        //login
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener((View view) -> {

            progressIndicator.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);

            Thread loginAction = new Thread(() -> {
                String uname=null, pass=null;
                if(username.getText() != null){
                    uname = username.getText().toString();
                }
                if(password.getText() != null){
                    pass = password.getText().toString();
                }

                Connection conn = Azure.getConnection();
                Azure.Validity validity = Azure.validateUser(conn, uname, pass);

                String finalPass = pass;
                String finalUname = uname;
                runOnUiThread(() -> {
                    switch (validity) {
                        case PASSWORD_INVALID:
                            login.error.setText(R.string.invalid_password);
                            login.error.setVisibility(View.VISIBLE);
                            break;
                        case USERNAME_INVALID:
                            login.error.setText(R.string.invalid_username);
                            login.error.setVisibility(View.VISIBLE);
                            break;
                        case USER_VALID:
                            login.error.setVisibility(View.INVISIBLE);
                            SharedPreferences.Editor spEditor = sp.edit();
                            spEditor.putString(getString(R.string.username), finalUname);
                            spEditor.putString(getString(R.string.password), finalPass);
                            spEditor.putBoolean(getString(R.string.login), true);
                            spEditor.apply();

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        case QUERY_FAILED:
                            login.error.setText(R.string.database_error);
                            login.error.setVisibility(View.VISIBLE);
                            break;
                    }
                    login.progressIndicator.setVisibility(View.INVISIBLE);
                    loginButton.setEnabled(true);
                });

            });
            loginAction.start();

        });

        //set custom back button navigation
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                    finishAffinity();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //start main activity if already logged in
        if(sp.getBoolean(getString(R.string.login), false)){
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
    }
}