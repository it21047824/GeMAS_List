package com.example.gemaslist;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {

    private TextInputEditText username;
    private TextInputEditText password;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
        sp = getSharedPreferences(getString(R.string.login), MODE_PRIVATE);

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

            String uname=null, pass=null;
            if(username.getText() != null){
                uname = username.getText().toString();
            }
            if(password.getText() != null){
                pass = password.getText().toString();
            }


            SharedPreferences.Editor spEditor = sp.edit();
            spEditor.putString(getString(R.string.username), uname);
            spEditor.putString(getString(R.string.password), pass);
            spEditor.putBoolean(getString(R.string.login), true);
            spEditor.apply();

            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);

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

    @Override
    protected void onResume() {
        super.onResume();

        //start main activity if already logged in
        if(sp.getBoolean(getString(R.string.login), false)){
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
    }
}