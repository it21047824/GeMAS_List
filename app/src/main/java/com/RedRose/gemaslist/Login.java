package com.RedRose.gemaslist;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

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
    private SignInClient oneTapClient;
    private BeginSignInRequest googleSignInRequest;
    private static final int REQ_ONE_TAP = 100;

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
                //noinspection ConstantConditions
                Snackbar.make(loginButton, "Cancelled : code "+response.getError().getErrorCode()
                        , Toast.LENGTH_SHORT).show();
            }
        }
        progressIndicator.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    AuthCredential firebaseCredential = GoogleAuthProvider
                            .getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this, task -> {
                                if(task.isSuccessful()){
                                    checkUserSignIn();
                                } else {
                                    Snackbar.make(loginButton, "Error"
                                            , Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            } catch (ApiException e) {
                Log.e("Login86", e.getMessage());
                loginButton.setVisibility(View.VISIBLE);
                progressIndicator.setVisibility(View.GONE);
                signInLauncher.launch(signInIntent);
            }
        }
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

        oneTapClient = Identity.getSignInClient(this);
        googleSignInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions
                        .builder()
                        .setSupported(true)
                        .setFilterByAuthorizedAccounts(true)
                        .setServerClientId(getString(R.string.web_client_id))
                        .build())
                .setAutoSelectEnabled(true)
                .build();



        loginButton.setOnClickListener(view -> {
            progressIndicator.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);

            oneTapClient.beginSignIn(googleSignInRequest)
                    .addOnSuccessListener(beginSignInResult -> {
                        try{
                            //noinspection deprecation
                            startIntentSenderForResult(
                                    beginSignInResult.getPendingIntent().getIntentSender(),
                                    REQ_ONE_TAP, null, 0,0,0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("Login156", e.getMessage());
                        }
                    }).addOnFailureListener(this, e -> {
                        Log.e("Login165", e.getMessage());
                        progressIndicator.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                        signInLauncher.launch(signInIntent);
                    });
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        //start main activity if already logged in
        checkUserSignIn();
    }

    private void checkUserSignIn() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivity(mainActivityIntent);
            finish();
        }
    }
}