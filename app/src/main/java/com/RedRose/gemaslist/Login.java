package com.RedRose.gemaslist;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity {

    private SharedPreferences sp;
    private CircularProgressIndicator progressIndicator;
    private MaterialButton loginButton;
    private Intent mainActivityIntent, signInIntent;
    private FirebaseAuth mAuth;
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        //handle sign in
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Toast.makeText(this,
                    getResources().getString(R.string.signed_in),
                    Toast.LENGTH_SHORT).show();

            if(user != null){
                SharedPreferences.Editor spEditor = sp.edit();
                spEditor.putString(getString(R.string.user_id), user.getUid());
                spEditor.putString(getString(R.string.username), user.getDisplayName());
                spEditor.putString(getString(R.string.email), response.getEmail());
                spEditor.apply();

                startActivity(mainActivityIntent);
            }
        } else {
            progressIndicator.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp = getSharedPreferences(getString(R.string.login), MODE_PRIVATE);
        progressIndicator = findViewById(R.id.login_progress_indicator);
        loginButton = findViewById(R.id.login_button);
        mainActivityIntent = new Intent(Login.this, MainActivity.class);
        mAuth = FirebaseAuth.getInstance();

        //firebase login activity
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);

        loginButton.setOnClickListener(view -> {
            progressIndicator.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);

            signInLauncher.launch(signInIntent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //start main activity if already logged in
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivity(mainActivityIntent);
        }
    }




//    private void deprecatedLoginMethod() {
//private TextInputEditText emailInput;
//    private TextInputEditText password;
//
//    protected MaterialTextView error;
//    protected Login login;
//    protected LinearProgressIndicator progressIndicator;
//    emailInput.setText(sp.getString(getString(R.string.email), null));
//        password.setText(sp.getString(getString(R.string.password), null));


//        emailInput = findViewById(R.id.input_email);
//        password = findViewById(R.id.input_password);
//        error = findViewById(R.id.login_error);
//        progressIndicator = findViewById(R.id.login_loading_indicator);
//
//        login = Login.this;
//
//        //login
//        Button loginButton = findViewById(R.id.login_button);
//        loginButton.setOnClickListener((View view) -> {
//
//            progressIndicator.setVisibility(View.VISIBLE);
//            loginButton.setEnabled(false);
//
//            Thread loginThread = new Thread(() -> {
//
//                String email=null, pass=null;
//                if(emailInput.getText() != null){
//                    email = emailInput.getText().toString();
//                    Log.e("Login", email);
//                }
//                if(password.getText() != null){
//                    pass = password.getText().toString();
//                }
//
//                UserAccount result = Azure.validateUserCreds(email, pass);
//
//                runOnUiThread(() -> {
//                    switch (result.getResult()) {
//                        case PASSWORD_INVALID:
//                            login.error.setText(R.string.invalid_password);
//                            login.error.setVisibility(View.VISIBLE);
//                            break;
//                        case USERNAME_INVALID:
//                            login.error.setText(R.string.invalid_email);
//                            login.error.setVisibility(View.VISIBLE);
//                            break;
//                        case QUERY_SUCCESSFUL:
//                            login.error.setVisibility(View.INVISIBLE);
//                            SharedPreferences.Editor spEditor = sp.edit();
//                            spEditor.putInt(getString(R.string.user_id), result.getUserID());
//                            spEditor.putString(getString(R.string.username), result.getUsername());
//                            spEditor.putString(getString(R.string.email), result.getEmail());
//                            spEditor.remove(getString(R.string.password));
//                            spEditor.putBoolean(getString(R.string.login), true);
//                            spEditor.apply();
//
//                            Intent intent = new Intent(Login.this, MainActivity.class);
//                            startActivity(intent);
//                            break;
//                        case QUERY_FAILED:
//                            login.error.setText(R.string.network_error);
//                            login.error.setVisibility(View.VISIBLE);
//                            break;
//                        case NULL_ARGS:
//                            login.error.setText(R.string.error);
//                            login.error.setVisibility(View.VISIBLE);
//                            break;
//                    }
//                    login.progressIndicator.setVisibility(View.INVISIBLE);
//                    loginButton.setEnabled(true);
//                });
//            });
//            loginThread.start();
//
//        });
//
//        //signup
//        MaterialTextView signupText = findViewById(R.id.login_signup_link);
//        signupText.setOnClickListener(view -> startActivity(new Intent(Login.this, SignUp.class)));
//
//        //set custom back button navigation
//        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                finishAffinity();
//                System.exit(0);
//            }
//        };
//        this.getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
//
//        //initialize the database connection
//        Thread databaseConn = new Thread(Azure::getConnection);
//        databaseConn.start();
//    }
}