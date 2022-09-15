package com.RedRose.gemaslist;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class Login extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText password;
    private SharedPreferences sp;
    protected MaterialTextView error;
    protected Login login;
    protected LinearProgressIndicator progressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        error = findViewById(R.id.login_error);
        progressIndicator = findViewById(R.id.login_loading_indicator);
        sp = getSharedPreferences(getString(R.string.login), MODE_PRIVATE);
        login = Login.this;

        //login
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener((View view) -> {

            progressIndicator.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);

            Thread loginThread = new Thread(() -> {

                String email=null, pass=null;
                if(emailInput.getText() != null){
                    email = emailInput.getText().toString();
                    Log.e("Login", email);
                }
                if(password.getText() != null){
                    pass = password.getText().toString();
                }

                UserAccount result = Azure.validateUser(email, pass);

                runOnUiThread(() -> {
                    switch (result.getResult()) {
                        case PASSWORD_INVALID:
                            login.error.setText(R.string.invalid_password);
                            login.error.setVisibility(View.VISIBLE);
                            break;
                        case USERNAME_INVALID:
                            login.error.setText(R.string.invalid_email);
                            login.error.setVisibility(View.VISIBLE);
                            break;
                        case QUERY_SUCCESSFUL:
                            login.error.setVisibility(View.INVISIBLE);
                            SharedPreferences.Editor spEditor = sp.edit();
                            spEditor.putInt(getString(R.string.user_id), result.getUserID());
                            spEditor.putString(getString(R.string.username), result.getUsername());
                            spEditor.putString(getString(R.string.email), result.getEmail());
                            spEditor.remove(getString(R.string.password));
                            spEditor.putBoolean(getString(R.string.login), true);
                            spEditor.apply();

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        case QUERY_FAILED:
                            login.error.setText(R.string.network_error);
                            login.error.setVisibility(View.VISIBLE);
                            break;
                        case NULL_ARGS:
                            login.error.setText(R.string.error);
                            login.error.setVisibility(View.VISIBLE);
                            break;
                    }
                    login.progressIndicator.setVisibility(View.INVISIBLE);
                    loginButton.setEnabled(true);
                });
            });
            loginThread.start();

        });

        //signup
        MaterialTextView signupText = findViewById(R.id.login_signup_link);
        signupText.setOnClickListener(view -> startActivity(new Intent(Login.this, SignUp.class)));

        //set custom back button navigation
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                    finishAffinity();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, backPressedCallback);

        //initialize the database connection
        Thread databaseConn = new Thread(Azure::getConnection);
        databaseConn.start();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //start main activity if already logged in
        if(sp.getBoolean(getString(R.string.login), false)){
            startActivity(new Intent(Login.this, MainActivity.class));
        }

        //set credentials from previous login
        emailInput.setText(sp.getString(getString(R.string.email), null));
        password.setText(sp.getString(getString(R.string.password), null));
    }
}