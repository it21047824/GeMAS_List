package com.RedRose.gemaslist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    protected TextInputEditText usernameInput;
    protected TextInputEditText emailInput;
    protected TextInputEditText passwordInput;
    protected TextInputEditText confirmPassInput;
    protected MaterialTextView error;
    protected MaterialButton createAccountButton;
    protected LinearProgressIndicator progressIndicator;
    protected TextInputLayout passwordLayout;
    protected TextInputLayout passwordConfirm;
    protected TextInputLayout emailLayout;
    protected TextInputLayout usernameLayout;
    protected SignUp activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        activity = SignUp.this;
        usernameInput = findViewById(R.id.signup_input_username);
        emailInput = findViewById(R.id.signup_input_email);
        passwordInput = findViewById(R.id.signup_input_password);
        confirmPassInput = findViewById(R.id.signup_input_confirm_password);
        error = findViewById(R.id.signup_error);
        createAccountButton = findViewById(R.id.signup_create_acc_button);
        progressIndicator = findViewById(R.id.signup_progress);
        passwordLayout = findViewById(R.id.signup_password_layout);
        passwordConfirm = findViewById(R.id.signup_confirm_password_layout);
        emailLayout = findViewById(R.id.signup_email_layout);
        usernameLayout = findViewById(R.id.signup_username_layout);

        //validate password
        passwordInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validatePassword(charSequence);
                String pass = Objects.requireNonNull(confirmPassInput.getText()).toString();
                if(charSequence.toString().equals(pass)) {
                    passwordConfirm.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        //validate password confirmation
        confirmPassInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateConfirmPassword(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        //validate email
        emailInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateEmail(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        //validate username
        usernameInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateUsername(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        createAccountButton.setOnClickListener(view -> {
            progressIndicator.setVisibility(View.VISIBLE);
            createAccountButton.setEnabled(false);

            String username = Objects.requireNonNull(usernameInput.getText()).toString();
            String email = Objects.requireNonNull(emailInput.getText()).toString();
            String password = Objects.requireNonNull(passwordInput.getText()).toString();
            String confirm = Objects.requireNonNull(confirmPassInput.getText()).toString();

            progressIndicator.setVisibility(View.INVISIBLE);
            createAccountButton.setEnabled(true);
        });

    }

    private boolean validateUsername(CharSequence charSequence) {
        if(charSequence.length()<3){
            usernameLayout.setError("*username must be at least 3 characters");
        } else {
            if(charSequence.length()>20){
                usernameLayout.setError("*username must be no more than 20 characters");
            }
            usernameLayout.setError(null);
            return true;
        }
        return false;
    }

    private boolean validateConfirmPassword(CharSequence charSequence) {
        String pass = Objects.requireNonNull(passwordInput.getText()).toString();
        if(charSequence.toString().equals(pass)) {
            passwordConfirm.setError(null);
            return true;
        } else {
            passwordConfirm.setError("*passwords do not match");
        }
        return false;
    }

    public boolean validatePassword(CharSequence charSequence) {
        if(charSequence.length() < 8) {
            passwordLayout.setError("*must contain at least 8 characters");
        } else {
            if(!checkLowerCase(charSequence)) {
                passwordLayout.setError("*must contain a lowercase character");
            } else {
                if(!checkUpperCase(charSequence)) {
                    passwordLayout.setError("*must contain an uppercase character");
                } else {
                    if(!checkNumber(charSequence)) {
                        passwordLayout.setError("*must contain a number");
                    } else {
                        passwordLayout.setError(null);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean validateEmail(CharSequence charSequence) {
        if(charSequence.toString().contains("@") && charSequence.length()>6) {
            emailLayout.setError(null);
            return true;
        } else {
            emailLayout.setError("*invalid email address");
        }
        return false;
    }

    public boolean checkLowerCase(CharSequence input) {
        char ch;
        for (int i=0; i<input.length(); i++) {
            ch = input.charAt(i);
            if(Character.isLowerCase(ch)){
                return true;
            }
        }
        return false;
    }

    public boolean checkUpperCase(CharSequence input) {
        char ch;
        for (int i=0; i<input.length(); i++) {
            ch = input.charAt(i);
            if(Character.isUpperCase(ch)){
                return true;
            }
        }
        return false;
    }

    public boolean checkNumber(CharSequence input) {
        char ch;
        for (int i=0; i<input.length(); i++) {
            ch = input.charAt(i);
            if(Character.isDigit(ch)){
                return true;
            }
        }
        return false;
    }
}