package com.RedRose.gemaslist;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity {

    private CircularProgressIndicator progressIndicator;
    private MaterialButton loginButton;
    private Intent mainActivityIntent, signInIntent;
    private FirebaseAuth mAuth;
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            Login.this::onSignInResult
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        //handle sign in
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null){
                Toast.makeText(this,
                        getResources().getString(R.string.signed_in, user.getEmail()),
                        Toast.LENGTH_SHORT).show();

                startActivity(mainActivityIntent);
            }
        } else {
            loginButton.setVisibility(View.VISIBLE);
            if(response != null){
//                switch(response.getError().getErrorCode()){
//                    case ErrorCodes.NO_NETWORK:
//                        break;
//                    case Error
//                }
                Snackbar.make(loginButton, "Cancelled : code "+response.getError().getErrorCode()
                        , Toast.LENGTH_SHORT).show();
            }
        }
        progressIndicator.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
            finish();
        }
    }
}